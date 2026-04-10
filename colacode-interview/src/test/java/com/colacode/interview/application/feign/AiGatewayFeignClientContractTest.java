package com.colacode.interview.application.feign;

import com.colacode.common.Result;
import com.colacode.interview.application.feign.dto.AiGenerateQuestionReqDTO;
import com.colacode.interview.application.feign.dto.AiGenerateQuestionRespDTO;
import com.colacode.interview.application.feign.dto.AiScoreAnswerReqDTO;
import com.colacode.interview.application.feign.dto.AiScoreAnswerRespDTO;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AiGatewayFeignClientContractTest {

    @Test
    void shouldKeepInterviewAiFeignContractAligned() throws NoSuchMethodException {
        FeignClient feignClient = AiGatewayFeignClient.class.getAnnotation(FeignClient.class);
        assertNotNull(feignClient);
        assertEquals("${interview.ai.service-name:colacode-ai}", feignClient.name());
        assertEquals("${interview.ai.base-url:}", feignClient.url());
        assertEquals("/ai/interview", feignClient.path());

        Method generateQuestion = AiGatewayFeignClient.class.getMethod("generateQuestion", AiGenerateQuestionReqDTO.class);
        PostMapping generateQuestionMapping = generateQuestion.getAnnotation(PostMapping.class);
        assertNotNull(generateQuestionMapping);
        assertArrayEquals(new String[]{"/question"}, generateQuestionMapping.value());
        assertResultPayload(generateQuestion, AiGenerateQuestionRespDTO.class);

        Method scoreAnswer = AiGatewayFeignClient.class.getMethod("scoreAnswer", AiScoreAnswerReqDTO.class);
        PostMapping scoreAnswerMapping = scoreAnswer.getAnnotation(PostMapping.class);
        assertNotNull(scoreAnswerMapping);
        assertArrayEquals(new String[]{"/score"}, scoreAnswerMapping.value());
        assertResultPayload(scoreAnswer, AiScoreAnswerRespDTO.class);
    }

    private void assertResultPayload(Method method, Class<?> payloadType) {
        Type genericReturnType = method.getGenericReturnType();
        ParameterizedType parameterizedType = assertInstanceOf(ParameterizedType.class, genericReturnType);
        assertEquals(Result.class, parameterizedType.getRawType());
        assertEquals(payloadType, parameterizedType.getActualTypeArguments()[0]);
    }
}
