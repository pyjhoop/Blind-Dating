package com.blind.dating.dto;

import com.blind.dating.domain.Interest;
import com.blind.dating.domain.UserAccount;
import lombok.*;

@Getter
@Setter
@ToString
public class InterestDto {
    private Long id;
    private UserAccount userAccount;
    private String interestName;

    protected InterestDto(){}

    private InterestDto(Long id,UserAccount userAccount, String interestName){
        this.id = id;
        this.userAccount = userAccount;
        this.interestName = interestName;
    }

    public static InterestDto of(Long id,UserAccount userAccount, String interestName){
        return new InterestDto(id,userAccount, interestName);
    }

    public static InterestDto from(Interest interest){
        return new InterestDto(interest.getId(),interest.getUserAccount() ,interest.getInterestName());
    }

    public Interest toEntity(){
        return Interest.of(userAccount,interestName);
    }

}
