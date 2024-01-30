package com.blind.dating.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("BaseEntity - 테스트")
public class BaseEntityTest {

    private static class TestEntity extends BaseEntity {}
    private TestEntity testEntity;

    @BeforeEach
    void setting() {
        testEntity = new TestEntity();
    }

    @Test
    void getCreatedAt() {
        LocalDateTime now = LocalDateTime.now();
        testEntity.setCreatedAt(now);
        LocalDateTime result = testEntity.getCreatedAt();

        assertThat(result).isEqualTo(now);
    }

    @Test
    void setUpdatedAt() {
        LocalDateTime now = LocalDateTime.now();
        testEntity.setUpdatedAt(now);
        LocalDateTime result = testEntity.getUpdatedAt();

        assertThat(result).isEqualTo(now);
    }


}
