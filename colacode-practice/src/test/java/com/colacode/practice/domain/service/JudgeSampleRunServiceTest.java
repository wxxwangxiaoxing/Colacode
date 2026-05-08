package com.colacode.practice.domain.service;

import com.colacode.common.Result;
import com.colacode.practice.application.dto.judge.JudgeRunSampleResultDTO;
import com.colacode.practice.application.feign.SubjectFeignClient;
import com.colacode.practice.application.feign.dto.SubjectCodeCaseDTO;
import com.colacode.practice.application.feign.dto.SubjectCodeConfigDTO;
import com.colacode.practice.application.feign.dto.SubjectCodeJudgeDetailDTO;
import com.colacode.practice.config.JudgeProperties;
import com.colacode.practice.infra.judge.Judge0Client;
import com.colacode.practice.infra.judge.Judge0ExecutionResult;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JudgeSampleRunServiceTest {

    @Test
    void shouldRunOnlySampleCases() {
        SubjectFeignClient subjectFeignClient = mock(SubjectFeignClient.class);
        Judge0Client judge0Client = mock(Judge0Client.class);
        JudgeSecurityService judgeSecurityService = mock(JudgeSecurityService.class);
        JudgeProperties judgeProperties = createJudgeProperties();
        JudgeSampleRunService service = new JudgeSampleRunService(
                subjectFeignClient,
                judge0Client,
                new JudgeOutputComparator(),
                judgeProperties,
                judgeSecurityService);

        when(subjectFeignClient.queryJudgeDetail(1001L)).thenReturn(Result.success(buildJudgeDetail()));
        when(judge0Client.execute(ArgumentMatchers.anyString(), ArgumentMatchers.eq(71), ArgumentMatchers.anyString(),
                ArgumentMatchers.eq(1000), ArgumentMatchers.eq(131072)))
                .thenReturn(buildJudgeResult("3", "9"))
                .thenReturn(buildJudgeResult("3", "2"));

        JudgeRunSampleResultDTO result = service.runSample(1L, 1001L, "python", "print(1)");

        assertEquals("AC", result.getOverallStatus());
        assertEquals(2, result.getResults().size());
        assertTrue(result.getMessage().contains("全部样例通过"));
        verify(judgeSecurityService, times(1)).assertSampleRunAllowed(1L, 1001L, "print(1)");
        verify(judge0Client, times(2)).execute(ArgumentMatchers.anyString(), ArgumentMatchers.eq(71),
                ArgumentMatchers.anyString(), ArgumentMatchers.eq(1000), ArgumentMatchers.eq(131072));
    }

    @Test
    void shouldReturnWrongAnswerWhenSampleFails() {
        SubjectFeignClient subjectFeignClient = mock(SubjectFeignClient.class);
        Judge0Client judge0Client = mock(Judge0Client.class);
        JudgeSecurityService judgeSecurityService = mock(JudgeSecurityService.class);
        JudgeProperties judgeProperties = createJudgeProperties();
        JudgeSampleRunService service = new JudgeSampleRunService(
                subjectFeignClient,
                judge0Client,
                new JudgeOutputComparator(),
                judgeProperties,
                judgeSecurityService);

        when(subjectFeignClient.queryJudgeDetail(1001L)).thenReturn(Result.success(buildJudgeDetail()));
        when(judge0Client.execute(ArgumentMatchers.anyString(), ArgumentMatchers.eq(71), ArgumentMatchers.anyString(),
                ArgumentMatchers.eq(1000), ArgumentMatchers.eq(131072)))
                .thenReturn(buildJudgeResult("3", "10"))
                .thenReturn(buildJudgeResult("3", "2"));

        JudgeRunSampleResultDTO result = service.runSample(1L, 1001L, "python", "print(1)");

        assertEquals("WA", result.getOverallStatus());
        assertTrue(result.getMessage().contains("样例 1 未通过"));
        assertEquals("WA", result.getResults().get(0).getStatus());
    }

    private JudgeProperties createJudgeProperties() {
        JudgeProperties judgeProperties = new JudgeProperties();
        judgeProperties.setLanguages(Map.of("python", 71));
        return judgeProperties;
    }

    private SubjectCodeJudgeDetailDTO buildJudgeDetail() {
        SubjectCodeConfigDTO configDTO = new SubjectCodeConfigDTO();
        configDTO.setTimeLimitMs(1000);
        configDTO.setMemoryLimitKb(131072);

        SubjectCodeJudgeDetailDTO detailDTO = new SubjectCodeJudgeDetailDTO();
        detailDTO.setCodeConfig(configDTO);
        detailDTO.setTestCases(List.of(
                buildCase(1, "2 7", "9", 1),
                buildCase(2, "-3 5", "2", 1),
                buildCase(3, "0 0", "0", 0)
        ));
        return detailDTO;
    }

    private SubjectCodeCaseDTO buildCase(int caseNo, String stdin, String expected, int sampleCase) {
        SubjectCodeCaseDTO caseDTO = new SubjectCodeCaseDTO();
        caseDTO.setCaseNo(caseNo);
        caseDTO.setStdinText(stdin);
        caseDTO.setExpectedStdout(expected);
        caseDTO.setSampleCase(sampleCase);
        return caseDTO;
    }

    private Judge0ExecutionResult buildJudgeResult(String statusDescription, String stdout) {
        Judge0ExecutionResult result = new Judge0ExecutionResult();
        result.setStatusId(3);
        result.setStatusDescription(statusDescription);
        result.setStdout(stdout);
        result.setExecuteTimeMs(1);
        result.setMemoryUsedKb(1024);
        return result;
    }
}
