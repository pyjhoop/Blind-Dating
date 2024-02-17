package com.blind.dating.dto.interest;

import com.blind.dating.domain.interest.Interest;
import lombok.*;

@Getter
@Setter
@ToString
public class InterestDto {
    private Long id;
    private String interestName;

    protected InterestDto(){}

    private InterestDto(Long id, String interestName){
        this.id = id;
        this.interestName = interestName;
    }

    public static InterestDto of(Long id, String interestName){
        return new InterestDto(id, interestName);
    }

    public static InterestDto from(Interest interest){
        return new InterestDto(interest.getId() ,interest.getInterestName());
    }


}
