package com.colacode.practice.domain.service;

import com.colacode.common.enums.ResultCodeEnum;
import com.colacode.common.exception.BusinessException;
import com.colacode.practice.config.JudgeProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class JudgeSecurityService {

    private final StringRedisTemplate stringRedisTemplate;
    private final JudgeProperties judgeProperties;

    public JudgeSecurityService(StringRedisTemplate stringRedisTemplate, JudgeProperties judgeProperties) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.judgeProperties = judgeProperties;
    }

    public void assertSubmissionAllowed(Long userId, Long subjectId, String code) {
        assertCodeAllowed(code);
        assertMinuteLimit(
                "judge:submit:minute:" + userId,
                judgeProperties.getMaxSubmitPerMinute(),
                "提交过于频繁，请稍后再试");
        assertCooldown(
                "judge:submit:cooldown:" + userId + ":" + subjectId,
                judgeProperties.getSubmitCooldownSeconds(),
                "同一题目提交过于频繁，请稍后再试");
    }

    public void assertSampleRunAllowed(Long userId, Long subjectId, String code) {
        assertCodeAllowed(code);
        assertMinuteLimit(
                "judge:run:minute:" + userId,
                judgeProperties.getRun().getMaxRunPerMinute(),
                "运行过于频繁，请稍后再试");
        assertCooldown(
                "judge:run:cooldown:" + userId + ":" + subjectId,
                judgeProperties.getRun().getCooldownSeconds(),
                "同一题目运行过于频繁，请稍后再试");
    }

    private void assertCodeAllowed(String code) {
        if (code == null || code.isBlank()) {
            throw new BusinessException(ResultCodeEnum.BAD_REQUEST, "代码不能为空");
        }
        if (code.length() > judgeProperties.getMaxCodeLength()) {
            throw new BusinessException(ResultCodeEnum.BAD_REQUEST, "代码长度超出限制");
        }
    }

    private void assertMinuteLimit(String key, Integer limit, String errorMessage) {
        Long count = stringRedisTemplate.opsForValue().increment(key);
        if (count != null && count == 1L) {
            stringRedisTemplate.expire(key, 60, TimeUnit.SECONDS);
        }
        if (count != null && limit != null && count > limit) {
            throw new BusinessException(ResultCodeEnum.TOO_MANY_REQUESTS, errorMessage);
        }
    }

    private void assertCooldown(String key, Integer cooldownSeconds, String errorMessage) {
        Boolean existed = stringRedisTemplate.hasKey(key);
        if (Boolean.TRUE.equals(existed)) {
            throw new BusinessException(ResultCodeEnum.TOO_MANY_REQUESTS, errorMessage);
        }
        stringRedisTemplate.opsForValue().set(
                key,
                "1",
                cooldownSeconds,
                TimeUnit.SECONDS);
    }
}
