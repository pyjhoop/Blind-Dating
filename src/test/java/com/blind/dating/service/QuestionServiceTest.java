package com.blind.dating.service;

import com.blind.dating.domain.Question;
import com.blind.dating.domain.UserAccount;
import com.blind.dating.repository.QuestionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@DisplayName("QuestionService - 테스트")
@ExtendWith(MockitoExtension.class)
class QuestionServiceTest {

    @Mock private QuestionRepository questionRepository;
    @InjectMocks private QuestionService questionService;

    @DisplayName("답변 저장하기")
    @Test
    void givenUserAccountAndAnswers_whenSaveAnswers_thenReturnList(){
        //Given
        UserAccount user = UserAccount.of("qweeqw","asdfdf", "nickname","asdf","asdf","M","하이요");
        List<Boolean> bList = List.of(true, false);

        List<Question> list = new ArrayList<>();
        Question q1 = Question.of(user,true);
        Question q2 = Question.of(user,false);
        list.add(q1);
        list.add(q2);


        //When
        List<Question> result = questionService.saveQuestions(user, bList);

        //Then
        assertThat(result).hasSize(2);


    }

}