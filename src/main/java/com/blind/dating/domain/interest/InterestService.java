package com.blind.dating.domain.interest;

import com.blind.dating.domain.interest.Interest;
import com.blind.dating.domain.user.UserAccount;
import com.blind.dating.domain.interest.InterestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InterestService {

    private final InterestRepository interestRepository;

    /**
     * 내 관심사 저장하기.
     * @param userAccount
     * @param interestName
     * @return List<Interest>
     */
    @Transactional
    public List<Interest> saveInterest(UserAccount userAccount, List<String> interestName){

        List<Interest> list = new ArrayList<>();

        // TOdo
        for(String s: interestName){
//            list.add(Interest.of(userAccount,s));
        }

        return list;
    }
}
