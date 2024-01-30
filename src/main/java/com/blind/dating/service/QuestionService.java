package com.blind.dating.service;

import com.blind.dating.domain.Question;
import com.blind.dating.domain.UserAccount;
import com.blind.dating.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;

    @Transactional
    public List<Question> saveQuestions(UserAccount userAccount, List<Boolean> booleans){

        List<Question> list = new ArrayList<>();

        for(Boolean b: booleans){
            list.add(Question.of(userAccount, b));
        }

        return list;

    }
}
