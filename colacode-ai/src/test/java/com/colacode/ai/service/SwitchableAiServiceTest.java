package com.colacode.ai.service;

import com.colacode.ai.config.AiProperties;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class SwitchableAiServiceTest {

    @Test
    void shouldUseMockServiceByDefault() {
        AiProperties properties = new AiProperties();
        properties.setDefaultModel("mock");
        MockAiService mockAiService = new MockAiService();
        RealAiService openAiService = Mockito.mock(RealAiService.class);

        SwitchableAiService service = new SwitchableAiService(properties, mockAiService, openAiService);

        assertEquals("MOCK", service.getModelName());
    }

    @Test
    void shouldUseOpenAiWhenConfiguredAndAvailable() {
        AiProperties properties = new AiProperties();
        properties.setDefaultModel("openai");
        MockAiService mockAiService = new MockAiService();
        RealAiService openAiService = Mockito.mock(RealAiService.class);
        when(openAiService.isAvailable()).thenReturn(true);
        when(openAiService.getModelName()).thenReturn("gpt-test");

        SwitchableAiService service = new SwitchableAiService(properties, mockAiService, openAiService);

        assertEquals("gpt-test", service.getModelName());
    }

    @Test
    void shouldFallbackToMockWhenOpenAiIsUnavailable() {
        AiProperties properties = new AiProperties();
        properties.setDefaultModel("openai");
        MockAiService mockAiService = new MockAiService();
        RealAiService openAiService = Mockito.mock(RealAiService.class);
        when(openAiService.isAvailable()).thenReturn(false);

        SwitchableAiService service = new SwitchableAiService(properties, mockAiService, openAiService);

        assertEquals("MOCK", service.getModelName());
    }
}
