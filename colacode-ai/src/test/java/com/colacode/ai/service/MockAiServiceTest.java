package com.colacode.ai.service;

import com.colacode.ai.service.dto.JudgeAnalysisContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MockAiServiceTest {

    private final MockAiService service = new MockAiService();

    @Test
    void shouldDescribeAcceptedSubmission() {
        JudgeAnalysisContext context = new JudgeAnalysisContext();
        context.setStatus("AC");

        String feedback = service.analyzeJudgeSubmission(context);

        assertTrue(feedback.contains("通过全部测试"));
    }

    @Test
    void shouldDescribePendingSubmission() {
        JudgeAnalysisContext context = new JudgeAnalysisContext();
        context.setStatus("pending");

        String feedback = service.analyzeJudgeSubmission(context);

        assertTrue(feedback.contains("稍后刷新结果"));
    }
}
