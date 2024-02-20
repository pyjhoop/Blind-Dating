package com.blind.dating.domain.UserInterest;

import com.blind.dating.domain.interest.Interest;
import com.blind.dating.domain.user.UserAccount;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@IdClass(UserInterestId.class)
public class UserInterest {

    @Id
    @ManyToOne
    @JoinColumn(name = "userAccountId")
    private UserAccount userAccount;

    @Id
    @ManyToOne
    @JoinColumn(name = "interestId")
    private Interest interest;
}
