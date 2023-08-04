package com.blind.dating.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.builder.ToStringExclude;

import javax.persistence.*;
import java.util.Objects;

@Getter
@ToString
@Table
@Entity
@NoArgsConstructor
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    private Long id;

    @ManyToOne
    private UserAccount userAccount;

    @Setter
    private Long questionId;

    @Column(length = 1000)
    @Setter
    private String content;

    private Answer(Long questionId, UserAccount userAccount, String content) {
        this.questionId = questionId;
        this.userAccount = userAccount;
        this.content = content;
    }

    public static Answer of(Long questionId, UserAccount userAccount, String content) {
        return new Answer(questionId, userAccount, content);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Answer)) return false;
        Answer answer = (Answer) o;
        return Objects.equals(id, answer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
