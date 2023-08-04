package com.blind.dating.dto.answer;

import com.blind.dating.domain.Answer;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AnswerDto {

    private Long questionId;

    private String content;


    private AnswerDto(Long questionId, String content) {
        this.questionId = questionId;
        this.content = content;
    }

    public static AnswerDto of(Long questionId, String content) {
        return new AnswerDto(questionId,content);
    }

    public static AnswerDto from(Answer answer) {
        return new AnswerDto(answer.getQuestionId(), answer.getContent());
    }

}
