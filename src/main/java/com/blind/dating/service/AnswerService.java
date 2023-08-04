package com.blind.dating.service;

import com.blind.dating.domain.Answer;
import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.answer.AnswerDto;
import com.blind.dating.repository.AnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;

    @Transactional
    public List<Answer> saveAnswer(List<AnswerDto> answers, UserAccount user){

        List<Answer> list = new ArrayList<>();

        for(AnswerDto dto: answers){
            list.add(Answer.of(dto.getQuestionId(),user, dto.getContent()));
        }

        return answerRepository.saveAll(list);
    }

    public List<Answer> getAnswers(UserAccount user){

        return answerRepository.findAllByUserAccountId(user.getId());
    }


}
