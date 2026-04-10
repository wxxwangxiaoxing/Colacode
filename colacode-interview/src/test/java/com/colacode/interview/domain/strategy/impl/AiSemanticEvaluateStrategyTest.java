package com.colacode.interview.domain.strategy.impl;

import com.colacode.interview.domain.bo.InterviewQuestionBO;
import com.colacode.interview.domain.bo.InterviewResultBO;
import com.colacode.interview.domain.service.InterviewDomainService;
import com.colacode.interview.infra.entity.InterviewQuestionRecord;
import com.colacode.interview.infra.entity.InterviewSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiSemanticEvaluateStrategyTest {

    @Mock
    private InterviewDomainService interviewDomainService;

    @InjectMocks
    private AiSemanticEvaluateStrategy aiSemanticEvaluateStrategy;

    @Test
    void evaluateShouldMapAiScoreAndCommentFromInterviewEngine() {
        InterviewSession session = new InterviewSession();
        session.setEngineType("AI");

        InterviewQuestionRecord record = new InterviewQuestionRecord();
        record.setKeyWords("JVM");
        record.setCategory("Java基础");
        record.setStem("请解释 JVM 的内存模型");
        record.setStandardAnswer("堆、栈、方法区、程序计数器、本地方法栈");
        record.setUserAnswer("JVM 通过堆、栈和方法区管理运行时数据。");

        AtomicReference<InterviewQuestionBO> forwardedQuestionRef = new AtomicReference<>();
        when(interviewDomainService.submitAnswers(eq("AI"), anyList())).thenAnswer(invocation -> {
            List<InterviewQuestionBO> questions = invocation.getArgument(1);
            InterviewQuestionBO questionBO = questions.get(0);
            forwardedQuestionRef.set(questionBO);
            questionBO.setUserScore(4.60D);
            InterviewResultBO interviewResultBO = new InterviewResultBO();
            interviewResultBO.setAvgScore(4.10D);
            interviewResultBO.setAvgTips("整体表现优秀，基础扎实！");
            return interviewResultBO;
        });

        var result = aiSemanticEvaluateStrategy.evaluate(session, record);

        assertEquals(0, new BigDecimal("4.60").compareTo(result.getAiScore()));
        assertEquals("整体表现优秀，基础扎实！", result.getComment());
        assertEquals("JVM", forwardedQuestionRef.get().getKeyWord());
        assertEquals("Java基础", forwardedQuestionRef.get().getLabelName());
        assertEquals("请解释 JVM 的内存模型", forwardedQuestionRef.get().getSubjectName());
        assertEquals("堆、栈、方法区、程序计数器、本地方法栈", forwardedQuestionRef.get().getSubjectAnswer());
        assertEquals("JVM 通过堆、栈和方法区管理运行时数据。", forwardedQuestionRef.get().getUserAnswer());
        verify(interviewDomainService).submitAnswers(eq("AI"), anyList());
    }

    @Test
    void evaluateShouldSkipAiScoringWhenSessionIsNotAi() {
        InterviewSession session = new InterviewSession();
        session.setEngineType("DATABASE");

        InterviewQuestionRecord record = new InterviewQuestionRecord();
        record.setStem("请解释 JVM 的内存模型");

        var result = aiSemanticEvaluateStrategy.evaluate(session, record);

        assertNull(result.getAiScore());
        assertNull(result.getComment());
    }
}
