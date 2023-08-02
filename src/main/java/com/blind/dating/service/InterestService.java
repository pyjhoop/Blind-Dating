package com.blind.dating.service;

import com.blind.dating.domain.Interest;
import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.InterestDto;
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

    private final EntityManager entityManager;

    private final InterestRepository interestRepository;

    public List<Interest> getInterests(UserAccount user){
        return interestRepository.findAllByUserAccount(user);
    }

    public List<Interest> saveInterest(Authentication authentication, List<InterestDto> dtos){

        UserAccount user =(UserAccount)authentication.getPrincipal();
        List<Interest> list = new ArrayList<>();

        for(InterestDto dto: dtos){
            dto.setUserAccount(user);
            list.add(dto.toEntity());
        }

        return interestRepository.saveAll(list);

    }

    @Transactional
    public List<Interest> updateInterest(Authentication authentication, List<InterestDto> dtos){
        UserAccount user =(UserAccount)authentication.getPrincipal();
        List<Interest> list = new ArrayList<>();

        interestRepository.deleteAllByUserAccount(user);

        for(InterestDto dto: dtos){
            dto.setUserAccount(user);
            list.add(dto.toEntity());
        }

        return interestRepository.saveAll(list);
    }


    @Transactional
    public void deleteInterest(UserAccount user){
        interestRepository.deleteAllByUserAccount(user);


    }



}
