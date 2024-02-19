package com.blind.dating.domain.interest;

import com.blind.dating.domain.user.UserAccount;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table
@Entity
public class Interest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interestId")
    private Long id;


    @Setter
    @Column(nullable = false, length = 50)
    private String interestName;

    private Interest (Long id) {
        this.id = id;
    }
    public static Interest of(Long id) {
        return new Interest(id);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Interest)) return false;
        Interest interest = (Interest) o;
        return Objects.equals(id, interest.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
