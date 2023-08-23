package com.blind.dating.dto.question;

import com.blind.dating.domain.Question;
import lombok.Data;

@Data
public class QuestionDto {
    private Long id;
    private Boolean status;

    public QuestionDto(Long id, Boolean status) {
        this.id = id;
        this.status = status;
    }

    public static QuestionDto from(Question question) {
        return new QuestionDto(question.getId(), question.getStatus());
    }
}
