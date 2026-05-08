package com.colacode.practice.domain.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.colacode.common.Result;
import com.colacode.practice.application.feign.AiFeignClient;
import com.colacode.practice.application.feign.CachedSubjectFeignClient;
import com.colacode.practice.application.feign.dto.AiJudgeAnalysisReqDTO;
import com.colacode.practice.application.feign.dto.AiJudgeAnalysisRespDTO;
import com.colacode.practice.application.feign.dto.SubjectCodeJudgeDetailDTO;
import com.colacode.practice.config.JudgeProperties;
import com.colacode.practice.infra.entity.PracticeSubmission;
import com.colacode.practice.infra.entity.PracticeSubmissionCase;
import com.colacode.practice.infra.mapper.PracticeSubmissionCaseMapper;
import com.colacode.practice.infra.mapper.PracticeSubmissionMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Service
public class JudgeAiAnalysisService {

    public static final String AI_STATUS_PENDING = "PENDING";
    public static final String AI_STATUS_RUNNING = "RUNNING";
    public static final String AI_STATUS_SUCCESS = "SUCCESS";
    public static final String AI_STATUS_FAILED = "FAILED";
    public static final String AI_STATUS_SKIPPED = "SKIPPED";

    private final PracticeSubmissionMapper practiceSubmissionMapper;
    private final PracticeSubmissionCaseMapper practiceSubmissionCaseMapper;
    private final CachedSubjectFeignClient cachedSubjectFeignClient;
    private final AiFeignClient aiFeignClient;
    private final JudgeProperties judgeProperties;

    public JudgeAiAnalysisService(PracticeSubmissionMapper practiceSubmissionMapper,
                                  PracticeSubmissionCaseMapper practiceSubmissionCaseMapper,
                                  CachedSubjectFeignClient cachedSubjectFeignClient,
                                  AiFeignClient aiFeignClient,
                                  JudgeProperties judgeProperties) {
        this.practiceSubmissionMapper = practiceSubmissionMapper;
        this.practiceSubmissionCaseMapper = practiceSubmissionCaseMapper;
        this.cachedSubjectFeignClient = cachedSubjectFeignClient;
        this.aiFeignClient = aiFeignClient;
        this.judgeProperties = judgeProperties;
    }

    public boolean isEnabled() {
        return judgeProperties.getAi().isEnabled();
    }

    @Async("judgeAiTaskExecutor")
    public void analyzeSubmissionAsync(Long submissionId) {
        PracticeSubmission submission = practiceSubmissionMapper.selectById(submissionId);
        if (submission == null || !isEnabled()) {
            return;
        }
        if (!shouldAnalyze(submission.getStatus())) {
            updateAiResult(submissionId, AI_STATUS_SKIPPED, null);
            return;
        }

        updateAiResult(submissionId, AI_STATUS_RUNNING, null);
        try {
            AiJudgeAnalysisReqDTO reqDTO = buildRequest(submission);
            Result<AiJudgeAnalysisRespDTO> response = callAiWithRetry(reqDTO, submissionId);
            String feedback = response.getData().getFeedback();
            updateAiResult(submissionId, AI_STATUS_SUCCESS, limitFeedback(feedback));
            log.info("AI judge analysis completed, submissionId={}", submissionId);
        } catch (Exception e) {
            log.warn("AI judge analysis failed, submissionId={}", submissionId, e);
            updateAiResult(submissionId, AI_STATUS_FAILED, limitFeedback("AI 分析未完成: " + rootMessage(e)));
        }
    }

    private Result<AiJudgeAnalysisRespDTO> callAiWithRetry(AiJudgeAnalysisReqDTO reqDTO, Long submissionId) {
        int maxAttempts = Math.max(1, judgeProperties.getAi().getMaxAttempts());
        Exception lastException = null;
        Result<AiJudgeAnalysisRespDTO> lastResponse = null;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                Result<AiJudgeAnalysisRespDTO> response = aiFeignClient.analyse(reqDTO);
                if (isUsableResponse(response)) {
                    if (attempt > 1) {
                        log.info("AI judge analysis retry succeeded, submissionId={}, attempt={}", submissionId, attempt);
                    }
                    return response;
                }
                lastResponse = response;
                log.warn("AI judge analysis returned unusable response, submissionId={}, attempt={}, message={}",
                        submissionId, attempt, response == null ? "null" : response.getMessage());
            } catch (Exception e) {
                lastException = e;
                log.warn("AI judge analysis call failed, submissionId={}, attempt={}", submissionId, attempt, e);
            }
            sleepBeforeRetry(attempt, maxAttempts);
        }
        if (lastException != null) {
            throw new IllegalStateException("AI service call failed after " + maxAttempts + " attempts", lastException);
        }
        throw new IllegalStateException(lastResponse == null ? "AI service returned null response" : lastResponse.getMessage());
    }

    private boolean isUsableResponse(Result<AiJudgeAnalysisRespDTO> response) {
        return response != null
                && response.isSuccess()
                && response.getData() != null
                && StringUtils.hasText(response.getData().getFeedback());
    }

    private void sleepBeforeRetry(int attempt, int maxAttempts) {
        if (attempt >= maxAttempts) {
            return;
        }
        Long retryDelayMs = judgeProperties.getAi().getRetryDelayMs();
        if (retryDelayMs == null || retryDelayMs <= 0) {
            return;
        }
        try {
            Thread.sleep(retryDelayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("AI retry interrupted", e);
        }
    }

    private AiJudgeAnalysisReqDTO buildRequest(PracticeSubmission submission) {
        AiJudgeAnalysisReqDTO reqDTO = new AiJudgeAnalysisReqDTO();
        reqDTO.setLanguage(submission.getLanguage());
        reqDTO.setCode(limitCode(new String(submission.getCode() == null ? new byte[0] : submission.getCode(),
                StandardCharsets.UTF_8)));
        reqDTO.setStatus(submission.getStatus());
        reqDTO.setJudgeMessage(submission.getJudgeMessage());
        reqDTO.setStdoutPreview(limitText(submission.getStdoutPreview(), 1200));
        reqDTO.setStderrPreview(limitText(submission.getStderrPreview(), 1200));
        reqDTO.setFailedCaseSummary(buildFailedCaseSummary(submission.getId()));

        Result<SubjectCodeJudgeDetailDTO> subjectResult = cachedSubjectFeignClient.queryJudgeDetail(submission.getSubjectId());
        if (subjectResult != null && subjectResult.isSuccess() && subjectResult.getData() != null) {
            SubjectCodeJudgeDetailDTO detail = subjectResult.getData();
            reqDTO.setSubjectName(detail.getSubjectName());
            if (detail.getCodeConfig() != null) {
                reqDTO.setInputExample(limitText(detail.getCodeConfig().getInputExample(), 500));
                reqDTO.setOutputExample(limitText(detail.getCodeConfig().getOutputExample(), 500));
            }
        }
        if (!StringUtils.hasText(reqDTO.getSubjectName())) {
            reqDTO.setSubjectName("题目 " + submission.getSubjectId());
        }
        return reqDTO;
    }

    private String buildFailedCaseSummary(Long submissionId) {
        LambdaQueryWrapper<PracticeSubmissionCase> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PracticeSubmissionCase::getSubmissionId, submissionId);
        wrapper.orderByAsc(PracticeSubmissionCase::getCaseNo);
        List<PracticeSubmissionCase> cases = practiceSubmissionCaseMapper.selectList(wrapper);
        for (PracticeSubmissionCase item : cases) {
            if (!JudgeSubmissionExecutionService.STATUS_ACCEPTED.equals(item.getStatus())) {
                StringBuilder builder = new StringBuilder();
                builder.append("case=").append(item.getCaseNo());
                if (Integer.valueOf(1).equals(item.getSampleCase())) {
                    builder.append(" (sample)");
                }
                builder.append(", status=").append(item.getStatus());
                if (StringUtils.hasText(item.getJudgeMessage())) {
                    builder.append(", message=").append(limitText(item.getJudgeMessage(), 300));
                }
                if (StringUtils.hasText(item.getStdinText())) {
                    builder.append(", stdin=").append(limitText(item.getStdinText(), 300));
                }
                if (StringUtils.hasText(item.getExpectedStdout())) {
                    builder.append(", expected=").append(limitText(item.getExpectedStdout(), 300));
                }
                if (StringUtils.hasText(item.getActualStdout())) {
                    builder.append(", actual=").append(limitText(item.getActualStdout(), 300));
                }
                if (StringUtils.hasText(item.getStderrText())) {
                    builder.append(", stderr=").append(limitText(item.getStderrText(), 300));
                }
                return builder.toString();
            }
        }
        return null;
    }

    private boolean shouldAnalyze(String judgeStatus) {
        if (!StringUtils.hasText(judgeStatus)) {
            return false;
        }
        if (JudgeSubmissionExecutionService.STATUS_PENDING.equals(judgeStatus)
                || JudgeSubmissionExecutionService.STATUS_RUNNING.equals(judgeStatus)) {
            return false;
        }
        if (JudgeSubmissionExecutionService.STATUS_ACCEPTED.equals(judgeStatus)) {
            return judgeProperties.getAi().isIncludeAccepted();
        }
        return true;
    }

    private void updateAiResult(Long submissionId, String aiStatus, String aiFeedback) {
        PracticeSubmission update = new PracticeSubmission();
        update.setId(submissionId);
        update.setAiStatus(aiStatus);
        if (aiFeedback != null) {
            update.setAiFeedback(aiFeedback);
        }
        practiceSubmissionMapper.updateById(update);
    }

    private String limitCode(String code) {
        return limitText(code, judgeProperties.getAi().getMaxCodeContextLength());
    }

    private String limitFeedback(String feedback) {
        return limitText(feedback, judgeProperties.getAi().getMaxFeedbackLength());
    }

    private String limitText(String value, int maxLength) {
        if (!StringUtils.hasText(value)) {
            return value;
        }
        String trimmed = value.trim();
        return trimmed.length() > maxLength ? trimmed.substring(0, maxLength) + "..." : trimmed;
    }

    private String rootMessage(Exception e) {
        Throwable current = e;
        while (current.getCause() != null) {
            current = current.getCause();
        }
        return current.getMessage() == null ? current.getClass().getSimpleName() : current.getMessage();
    }
}
