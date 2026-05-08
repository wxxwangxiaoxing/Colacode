package com.colacode.practice.domain.service;

import com.colacode.common.Result;
import com.colacode.practice.application.feign.AiFeignClient;
import com.colacode.practice.application.feign.CachedSubjectFeignClient;
import com.colacode.practice.application.feign.dto.AiJudgeAnalysisRespDTO;
import com.colacode.practice.application.feign.dto.SubjectCodeConfigDTO;
import com.colacode.practice.application.feign.dto.SubjectCodeJudgeDetailDTO;
import com.colacode.practice.config.JudgeProperties;
import com.colacode.practice.infra.entity.PracticeSubmission;
import com.colacode.practice.infra.mapper.PracticeSubmissionCaseMapper;
import com.colacode.practice.infra.mapper.PracticeSubmissionMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JudgeAiAnalysisServiceTest {

    @Test
    void shouldRetryAndEventuallySaveSuccess() {
        PracticeSubmissionMapper submissionMapper = mock(PracticeSubmissionMapper.class);
        PracticeSubmissionCaseMapper caseMapper = mock(PracticeSubmissionCaseMapper.class);
        CachedSubjectFeignClient subjectFeignClient = mock(CachedSubjectFeignClient.class);
        AiFeignClient aiFeignClient = mock(AiFeignClient.class);
        JudgeProperties judgeProperties = createJudgeProperties();
        JudgeAiAnalysisService service = new JudgeAiAnalysisService(
                submissionMapper, caseMapper, subjectFeignClient, aiFeignClient, judgeProperties);

        PracticeSubmission submission = new PracticeSubmission();
        submission.setId(1L);
        submission.setSubjectId(1001L);
        submission.setLanguage("python");
        submission.setStatus("WA");
        submission.setCode("print(1)".getBytes(StandardCharsets.UTF_8));
        when(submissionMapper.selectById(1L)).thenReturn(submission);
        when(caseMapper.selectList(any())).thenReturn(Collections.emptyList());
        when(subjectFeignClient.queryJudgeDetail(1001L)).thenReturn(Result.success(buildJudgeDetail()));
        when(aiFeignClient.analyse(any()))
                .thenReturn(Result.fail("temporary failure"))
                .thenReturn(Result.success(buildAiResponse("修复建议")));

        service.analyzeSubmissionAsync(1L);

        verify(aiFeignClient, times(2)).analyse(any());
        ArgumentCaptor<PracticeSubmission> captor = ArgumentCaptor.forClass(PracticeSubmission.class);
        verify(submissionMapper, times(2)).updateById(captor.capture());
        assertEquals(JudgeAiAnalysisService.AI_STATUS_RUNNING, captor.getAllValues().get(0).getAiStatus());
        assertEquals(JudgeAiAnalysisService.AI_STATUS_SUCCESS, captor.getAllValues().get(1).getAiStatus());
        assertEquals("修复建议", captor.getAllValues().get(1).getAiFeedback());
    }

    private JudgeProperties createJudgeProperties() {
        JudgeProperties properties = new JudgeProperties();
        JudgeProperties.AiProperties aiProperties = properties.getAi();
        aiProperties.setEnabled(true);
        aiProperties.setMaxAttempts(2);
        aiProperties.setRetryDelayMs(0L);
        return properties;
    }

    private SubjectCodeJudgeDetailDTO buildJudgeDetail() {
        SubjectCodeConfigDTO configDTO = new SubjectCodeConfigDTO();
        configDTO.setInputExample("2 7");
        configDTO.setOutputExample("9");

        SubjectCodeJudgeDetailDTO detailDTO = new SubjectCodeJudgeDetailDTO();
        detailDTO.setSubjectName("两数之和");
        detailDTO.setCodeConfig(configDTO);
        return detailDTO;
    }

    private AiJudgeAnalysisRespDTO buildAiResponse(String feedback) {
        AiJudgeAnalysisRespDTO response = new AiJudgeAnalysisRespDTO();
        response.setFeedback(feedback);
        return response;
    }
}
