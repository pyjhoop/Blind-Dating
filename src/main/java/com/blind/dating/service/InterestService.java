package com.blind.dating.service;

import com.blind.dating.domain.Interest;
import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.interest.InterestDto;
import com.blind.dating.repository.InterestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InterestService {

    private final InterestRepository interestRepository;

    public List<Interest> getInterests(UserAccount user){
        return interestRepository.findAllByUserAccount(user);
    }

    @Transactional
    public List<Interest> saveInterest(Authentication authentication, List<String> interestName){

        UserAccount user =(UserAccount)authentication.getPrincipal();
        List<Interest> list = new ArrayList<>();

        for(String s: interestName){
            list.add(Interest.of(user,s));
        }

        return interestRepository.saveAll(list);
    }

    @Transactional
    public List<Interest> updateInterest(Authentication authentication, List<String> interestName){
        UserAccount user =(UserAccount)authentication.getPrincipal();
        List<Interest> list = new ArrayList<>();

        interestRepository.deleteAllByUserAccount(user);

        for(String s: interestName){
            list.add(Interest.of(user,s));
        }

        return interestRepository.saveAll(list);
    }


    @Transactional
    public void deleteInterest(UserAccount user){
        interestRepository.deleteAllByUserAccount(user);


    }



}
