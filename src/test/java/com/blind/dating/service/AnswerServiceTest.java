package com.blind.dating.service;

import com.blind.dating.domain.Answer;
import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.answer.AnswerDto;
import com.blind.dating.repository.AnswerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("응답 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class AnswerServiceTest {

    @Mock
    private AnswerRepository answerRepository;

    @InjectMocks
    private AnswerService answerService;

    @DisplayName("답변 저장 테스트")
    @Test
    void givenAnswerData_whenSaveAnswer_thenReturnAnswerList(){
        //Given
        UserAccount user = UserAccount.of("userId","pwd","nick","서울",12,"INFP","M");
        user.setDeleted(false);
        Answer answer = Answer.of(1L,user,"안녕");
        List<Answer> list = List.of(answer);
        List<AnswerDto> dtos = List.of();
        given(answerRepository.saveAll(anyList())).willReturn(list);

        //When
        List<Answer> result = answerService.saveAnswer(dtos, user);

        //Then
        assertThat(result).isNotEmpty();
        then(answerRepository).should().saveAll(anyList());

    }

    @DisplayName("답변 가져오기 테스트")
    @Test
    void givenUserAccount_whenSelectAnswers_thenReturnAnswerList(){
        //Given
        UserAccount user = UserAccount.of("userId","pwd","nick","서울",12,"INFP","M");
        user.setDeleted(false);
        Answer answer = Answer.of(1L,user,"안녕");
        List<Answer> list = List.of(answer);
        given(answerRepository.findAllByUserAccountId(1L)).willReturn(list);

        //When
        List<Answer> result = answerService.getAnswers(1L);

        //Then
        assertThat(result).isNotEmpty();
        then(answerRepository).should().findAllByUserAccountId(1L);
    }




}