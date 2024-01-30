package com.blind.dating.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.Objects;

@Getter
@ToString
@Entity
@NoArgsConstructor
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne()
    private UserAccount userAccount;

    @Setter
    private Boolean status;

    private Question(UserAccount userAccount, Boolean status) {
        this.userAccount = userAccount;
        this.status = status;
    }

    public static Question of(UserAccount userAccount, Boolean status) {
        return new Question(userAccount, status);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Question)) return false;
        Question question = (Question) o;
        return Objects.equals(id, question.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
