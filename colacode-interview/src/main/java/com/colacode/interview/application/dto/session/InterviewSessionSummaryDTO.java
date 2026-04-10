package com.colacode.interview.application.dto.session;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class InterviewSessionSummaryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long sessionId;
    private String interviewType;
    private String postType;
    private String engineType;
    private String status;
    private Integer currentQuestionNo;
    private Integer totalQuestionCount;
    private BigDecimal totalScore;
    private Long reportId;
    private Integer durationSeconds;
    private Date startTime;
    private Date endTime;
}
