package com.colacode.interview.domain.manager;

import com.colacode.interview.application.dto.session.InterviewSessionKeywordDTO;
import com.colacode.interview.application.dto.session.StartInterviewSessionReqDTO;
import com.colacode.interview.application.dto.session.StartInterviewSessionRespDTO;
import com.colacode.interview.application.dto.session.SubmitAnswerReqDTO;
import com.colacode.interview.application.dto.session.SubmitAnswerRespDTO;
import com.colacode.interview.domain.bo.EvaluateResultBO;
import com.colacode.interview.domain.bo.InterviewQuestionBO;
import com.colacode.interview.domain.enums.InterviewQuestionRecordStatusEnum;
import com.colacode.interview.domain.enums.InterviewSessionStatusEnum;
import com.colacode.interview.domain.service.InterviewDomainService;
import com.colacode.interview.domain.service.InterviewEvaluationDomainService;
import com.colacode.interview.domain.service.InterviewReportDomainService;
import com.colacode.interview.domain.service.InterviewSessionDomainService;
import com.colacode.interview.infra.entity.InterviewQuestionRecord;
import com.colacode.interview.infra.entity.InterviewSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InterviewFlowManagerTest {

    @Mock
    private InterviewDomainService interviewDomainService;
    @Mock
    private InterviewSessionDomainService interviewSessionDomainService;
    @Mock
    private InterviewEvaluationDomainService interviewEvaluationDomainService;
    @Mock
    private InterviewReportDomainService interviewReportDomainService;

    @InjectMocks
    private InterviewFlowManager interviewFlowManager;

    @Test
    void startSessionShouldNormalizeAiEngineAndPersistGeneratedQuestion() {
        StartInterviewSessionReqDTO reqDTO = new StartInterviewSessionReqDTO();
        reqDTO.setEngineType(" ai ");
        reqDTO.setInterviewType("MOCK");
        reqDTO.setPostType("BACKEND");
        reqDTO.setDifficultyLevel(2);
        reqDTO.setQuestionCount(1);
        reqDTO.setSourceMode("HYBRID");
        reqDTO.setKeywords(List.of(buildKeyword("JVM")));

        InterviewQuestionBO generatedQuestion = new InterviewQuestionBO();
        generatedQuestion.setKeyWord("JVM");
        generatedQuestion.setLabelName("Java基础");
        generatedQuestion.setSubjectName("请解释 JVM 的内存模型");
        generatedQuestion.setSubjectAnswer("堆、栈、方法区、程序计数器、本地方法栈");
        when(interviewDomainService.startInterview(eq("AI"), anyList())).thenReturn(List.of(generatedQuestion));

        AtomicReference<InterviewSession> createdSessionRef = new AtomicReference<>();
        when(interviewSessionDomainService.createSession(any())).thenAnswer(invocation -> {
            InterviewSession session = invocation.getArgument(0);
            session.setId(100L);
            session.setStatus(InterviewSessionStatusEnum.IN_PROGRESS.name());
            createdSessionRef.set(session);
            return session;
        });
        doAnswer(invocation -> {
            InterviewSession session = invocation.getArgument(0);
            session.setCurrentQuestionNo(1);
            session.setStatus(InterviewSessionStatusEnum.WAITING_ANSWER.name());
            return null;
        }).when(interviewSessionDomainService).markWaitingForFirstQuestion(any());

        AtomicReference<List<InterviewQuestionRecord>> savedRecordsRef = new AtomicReference<>();
        doAnswer(invocation -> {
            List<InterviewQuestionRecord> records = new ArrayList<>(invocation.getArgument(1));
            InterviewQuestionRecord first = records.get(0);
            first.setId(200L);
            first.setStatus(InterviewQuestionRecordStatusEnum.WAITING_ANSWER.name());
            savedRecordsRef.set(records);
            return null;
        }).when(interviewSessionDomainService).saveQuestionRecords(eq(100L), anyList());
        when(interviewSessionDomainService.listQuestionRecords(100L)).thenAnswer(invocation -> savedRecordsRef.get());

        StartInterviewSessionRespDTO respDTO = interviewFlowManager.startSession(10L, reqDTO);

        assertEquals("AI", createdSessionRef.get().getEngineType());
        assertEquals(100L, respDTO.getSessionId());
        assertEquals(InterviewSessionStatusEnum.WAITING_ANSWER.name(), respDTO.getStatus());
        assertEquals(1, respDTO.getTotalQuestionCount());
        assertNotNull(respDTO.getFirstQuestion());
        assertEquals(200L, respDTO.getFirstQuestion().getRecordId());
        assertEquals(1, respDTO.getFirstQuestion().getQuestionNo());
        assertEquals("AI", respDTO.getFirstQuestion().getQuestionSource());
        assertEquals("JVM", respDTO.getFirstQuestion().getKeyWord());
        assertEquals("请解释 JVM 的内存模型", respDTO.getFirstQuestion().getStem());
        assertEquals("堆、栈、方法区、程序计数器、本地方法栈", respDTO.getFirstQuestion().getStandardAnswer());

        List<InterviewQuestionRecord> savedRecords = savedRecordsRef.get();
        assertNotNull(savedRecords);
        assertEquals(1, savedRecords.size());
        assertEquals("AI", savedRecords.get(0).getQuestionSource());
        assertEquals("Java基础", savedRecords.get(0).getCategory());
        assertEquals("JVM", savedRecords.get(0).getKeyWords());
        assertEquals("请解释 JVM 的内存模型", savedRecords.get(0).getStem());
        verify(interviewDomainService).startInterview(eq("AI"), anyList());
    }

    @Test
    void submitAnswerShouldPersistMergedScoresAndReturnQuestionResult() {
        SubmitAnswerReqDTO reqDTO = new SubmitAnswerReqDTO();
        reqDTO.setSessionId(100L);
        reqDTO.setRecordId(200L);
        reqDTO.setAnswer("  JVM 通过堆、栈和方法区管理运行时数据，GC 负责回收堆内存。  ");

        InterviewSession session = new InterviewSession();
        session.setId(100L);
        session.setStatus(InterviewSessionStatusEnum.WAITING_ANSWER.name());
        session.setCurrentQuestionNo(1);

        InterviewQuestionRecord record = new InterviewQuestionRecord();
        record.setId(200L);
        record.setSessionId(100L);
        record.setStatus(InterviewQuestionRecordStatusEnum.WAITING_ANSWER.name());
        record.setKeyWords("JVM");
        record.setStem("请解释 JVM 的内存模型");
        record.setStandardAnswer("堆、栈、方法区、程序计数器、本地方法栈");
        record.setAskTime(new Date(System.currentTimeMillis() - 3000));

        when(interviewSessionDomainService.getSession(100L)).thenReturn(session);
        when(interviewSessionDomainService.getQuestionRecord(200L)).thenReturn(record);

        EvaluateResultBO evaluateResult = new EvaluateResultBO();
        evaluateResult.setRuleScore(new BigDecimal("2.00"));
        evaluateResult.setAiScore(new BigDecimal("4.50"));
        evaluateResult.setFinalScore(new BigDecimal("3.50"));
        evaluateResult.setHitPoints(List.of("JVM", "GC"));
        evaluateResult.setMissPoints(List.of("方法区"));
        evaluateResult.setWrongPoints(List.of("缺少线程隔离说明"));
        evaluateResult.setComment("整体表现良好，部分知识需要巩固。");
        when(interviewEvaluationDomainService.evaluate(session, record)).thenReturn(evaluateResult);

        doAnswer(invocation -> {
            InterviewQuestionRecord questionRecord = invocation.getArgument(0);
            questionRecord.setStatus(InterviewQuestionRecordStatusEnum.EVALUATED.name());
            return null;
        }).when(interviewSessionDomainService).saveEvaluatedQuestion(any());
        when(interviewSessionDomainService.listQuestionRecords(100L)).thenReturn(List.of(record));

        SubmitAnswerRespDTO respDTO = interviewFlowManager.submitAnswer(reqDTO);

        assertEquals("JVM 通过堆、栈和方法区管理运行时数据，GC 负责回收堆内存。", record.getUserAnswer());
        assertNotNull(record.getAnswerTime());
        assertNotNull(record.getCostSeconds());
        assertTrue(record.getCostSeconds() >= 0);
        assertEquals(new BigDecimal("2.00"), record.getRuleScore());
        assertEquals(new BigDecimal("4.50"), record.getAiScore());
        assertEquals(new BigDecimal("3.50"), record.getFinalScore());
        assertEquals("JVM、GC", record.getHitKeywords());
        assertEquals("方法区", record.getMissKeywords());
        assertEquals("缺少线程隔离说明", record.getWrongPoints());
        assertEquals("整体表现良好，部分知识需要巩固。", record.getEvaluationComment());
        assertEquals(InterviewQuestionRecordStatusEnum.EVALUATED.name(), record.getStatus());

        assertEquals(100L, respDTO.getSessionId());
        assertEquals(200L, respDTO.getRecordId());
        assertEquals(InterviewQuestionRecordStatusEnum.EVALUATED.name(), respDTO.getStatus());
        assertNotNull(respDTO.getQuestionResult());
        assertEquals(1, respDTO.getQuestionResult().getQuestionNo());
        assertEquals(new BigDecimal("2.00"), respDTO.getQuestionResult().getRuleScore());
        assertEquals(new BigDecimal("4.50"), respDTO.getQuestionResult().getAiScore());
        assertEquals(new BigDecimal("3.50"), respDTO.getQuestionResult().getFinalScore());
        assertEquals(List.of("JVM", "GC"), respDTO.getQuestionResult().getHitPoints());
        assertEquals(List.of("方法区"), respDTO.getQuestionResult().getMissPoints());
        assertEquals(List.of("缺少线程隔离说明"), respDTO.getQuestionResult().getWrongPoints());
        assertEquals("整体表现良好，部分知识需要巩固。", respDTO.getQuestionResult().getComment());

        verify(interviewSessionDomainService).markEvaluating(session);
        verify(interviewSessionDomainService).saveEvaluatedQuestion(record);
        verify(interviewSessionDomainService).refreshSessionScore(session, List.of(record));
    }

    private InterviewSessionKeywordDTO buildKeyword(String keyWord) {
        InterviewSessionKeywordDTO keywordDTO = new InterviewSessionKeywordDTO();
        keywordDTO.setKeyWord(keyWord);
        return keywordDTO;
    }
}
