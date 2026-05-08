package com.colacode.practice.domain.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JudgeOutputComparatorTest {

    private final JudgeOutputComparator comparator = new JudgeOutputComparator();

    @Test
    void shouldIgnoreLineEndingAndTrailingWhitespaceDifferences() {
        assertTrue(comparator.matches("42\r\n", "42\n"));
        assertTrue(comparator.matches("42   ", "42"));
    }

    @Test
    void shouldDetectDifferentOutput() {
        assertFalse(comparator.matches("41", "42"));
    }
}
