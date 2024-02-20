package com.blind.dating.domain.UserInterest;

import java.io.Serializable;
import java.util.Objects;

public class UserInterestId implements Serializable {
    private Long userAccount;
    private Long interest;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserInterestId that = (UserInterestId) o;
        return Objects.equals(userAccount, that.userAccount) && Objects.equals(interest, that.interest);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userAccount, interest);
    }
}
