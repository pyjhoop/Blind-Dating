package com.blind.dating.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Interest Entity - 테스트")
class InterestTest {

    private Interest interest;

    @BeforeEach
    void setting() {
        UserAccount user = UserAccount.of("user01","pass01", "nickname1","서울","intp","M","하이요");
        interest = new Interest(1L, user,"name");
    }

    @Test
    void getInterestName() {
        String interestName = interest.getInterestName();

        assertEquals(interestName, "name");
    }

    @Test
    void setInterestName() {
        interest.setInterestName("newName");
        String interestName = interest.getInterestName();

        assertEquals(interestName, "newName");
    }

    @Test
    void getId() {
        Long id = interest.getId();

        assertEquals(id, 1L);
    }

}