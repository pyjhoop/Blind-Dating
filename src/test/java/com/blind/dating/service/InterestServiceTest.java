package com.blind.dating.service;

import com.blind.dating.domain.interest.Interest;
import com.blind.dating.domain.interest.InterestService;
import com.blind.dating.domain.user.UserAccount;
import com.blind.dating.domain.interest.InterestRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("InterestService -테스트")
@ExtendWith(MockitoExtension.class)
class InterestServiceTest {

    @Mock private InterestRepository interestRepository;
    @InjectMocks private InterestService interestService;

    @DisplayName("관심사 저장 - 테스트")
    @Test
    void givenUserAndInterests_whenSaveInterests_thenReturnInterestsList(){
        //Given
        UserAccount user = UserAccount.of("qweeqw","asdfdf", "nickname","asdf","asdf","M","하이요");
        List<Interest> list = new ArrayList<>();
        list.add(Interest.of(user,"놀기"));
        list.add(Interest.of(user,"잠자기"));
        List<String> list2 = List.of("놀기","잠자기");

        //When
        List<Interest> result = interestService.saveInterest(user,list2);

        //Then
        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(list);

    }

}