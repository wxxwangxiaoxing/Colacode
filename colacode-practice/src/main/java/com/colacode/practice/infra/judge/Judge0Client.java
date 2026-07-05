package com.colacode.practice.infra.judge;

import com.colacode.common.enums.ResultCodeEnum;
import com.colacode.common.exception.BusinessException;
import com.colacode.practice.config.JudgeProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Slf4j
@Component
public class Judge0Client {

    private final RestTemplate judgeRestTemplate;
    private final JudgeProperties judgeProperties;
    private final ObjectMapper objectMapper;

    public Judge0Client(RestTemplate judgeRestTemplate, JudgeProperties judgeProperties, ObjectMapper objectMapper) {
        this.judgeRestTemplate = judgeRestTemplate;
        this.judgeProperties = judgeProperties;
        this.objectMapper = objectMapper;
    }

    public Judge0ExecutionResult execute(String sourceCode,
                                         Integer languageId,
                                         String stdin,
                                         Integer timeLimitMs,
                                         Integer memoryLimitKb) {
        return createSubmission(sourceCode, languageId, stdin, timeLimitMs, memoryLimitKb);
    }

    private Judge0ExecutionResult createSubmission(String sourceCode,
                                                  Integer languageId,
                                                  String stdin,
                                                  Integer timeLimitMs,
                                                  Integer memoryLimitKb) {
        try {
            URI uri = URI.create(judgeProperties.getBaseUrl() + "/submissions?base64_encoded=false&wait=true");

            CreateSubmissionRequest reqBody = new CreateSubmissionRequest();
            reqBody.sourceCode = sourceCode;
            reqBody.languageId = languageId;
            reqBody.stdin = stdin == null ? "" : stdin;
            if (timeLimitMs != null && timeLimitMs > 0) {
                reqBody.cpuTimeLimit = timeLimitMs / 1000.0d;
            }
            if (memoryLimitKb != null && memoryLimitKb > 0) {
                reqBody.memoryLimit = memoryLimitKb;
            }

            String jsonPayload = objectMapper.writeValueAsString(reqBody);
            log.info("Judge0 提交请求: {}", jsonPayload);

            RequestEntity<String> request = RequestEntity
                    .post(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(jsonPayload);
            ResponseEntity<QuerySubmissionResponse> response = judgeRestTemplate.exchange(
                    request,
                    QuerySubmissionResponse.class);
            QuerySubmissionResponse body = response.getBody();
            if (body == null) {
                throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR, "Judge0 未返回有效结果");
            }

            Judge0ExecutionResult result = new Judge0ExecutionResult();
            result.setToken(body.token);
            result.setStatusId(body.status == null ? null : body.status.id);
            result.setStatusDescription(body.status == null ? null : body.status.description);
            result.setStdout(body.stdout);
            result.setStderr(StringUtils.hasText(body.stderr) ? body.stderr : body.compileOutput);
            result.setExecuteTimeMs(parseMillis(body.time));
            result.setMemoryUsedKb(body.memory);
            return result;
        } catch (JsonProcessingException e) {
            log.error("Judge0 请求序列化失败", e);
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR, "Judge0 请求序列化失败");
        } catch (RestClientException e) {
            log.error("Judge0 提交失败", e);
            throw new BusinessException(ResultCodeEnum.SYSTEM_ERROR, "Judge0 提交失败");
        }
    }

    private Integer parseMillis(String seconds) {
        if (!StringUtils.hasText(seconds)) {
            return null;
        }
        try {
            double value = Double.parseDouble(seconds);
            return (int) Math.round(value * 1000);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private static class CreateSubmissionRequest {
        @JsonProperty("source_code")
        public String sourceCode;
        @JsonProperty("language_id")
        public Integer languageId;
        public String stdin;
        @JsonProperty("cpu_time_limit")
        public Double cpuTimeLimit;
        @JsonProperty("memory_limit")
        public Integer memoryLimit;
    }

    private static class CreateSubmissionResponse {
        public String token;
    }

    private static class QuerySubmissionResponse {
        public String stdout;
        public String stderr;
        @JsonProperty("compile_output")
        public String compileOutput;
        public String time;
        public Integer memory;
        public Status status;
        public String token;
    }

    private static class Status {
        public Integer id;
        public String description;
    }
}
