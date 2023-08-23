package com.blind.dating.repository;

import com.blind.dating.config.JpaConfig;
import com.blind.dating.domain.Interest;
import com.blind.dating.domain.UserAccount;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Disabled
@DisplayName("JPA Repository 테스트")
@Import(JpaConfig.class)
@DataJpaTest
class JpaRepositoryTest {

    private final UserAccountRepository userAccountRepository;
    private final MessageRepository messageRepository;
    private final InterestRepository interestRepository;

    public JpaRepositoryTest(@Autowired UserAccountRepository userAccountRepository,@Autowired MessageRepository messageRepository,@Autowired InterestRepository interestRepository) {
        this.userAccountRepository = userAccountRepository;
        this.messageRepository = messageRepository;
        this.interestRepository = interestRepository;
    }

    @DisplayName("Insert 테스트")
    @Test
    void givenData_whenInsertData_returnEntity(){
        //Given
        UserAccount user1 = UserAccount.of("user01","pass01","user1","서울",12,"INFP","M","하이요");
        UserAccount user2 = UserAccount.of("user02","pass02","user2","서울",12,"INFP","M","하이");

        user1.setRefreshToken("token");
        user2.setRefreshToken("token");
        UserAccount savedUser1 = userAccountRepository.save(user1);
        UserAccount savedUser2 = userAccountRepository.save(user2);

        Message message = Message.of(savedUser1,Long.valueOf(2),"안녕하세요","read");
        Interest interest = Interest.of(savedUser1,"코딩");

        //When
        Message savedMessage = messageRepository.save(message);
        Interest savedInterest = interestRepository.save(interest);

        //Then
        assertThat(savedUser1).isNotNull().hasFieldOrPropertyWithValue("userId","user01");
        assertThat(savedMessage).isNotNull().hasFieldOrPropertyWithValue("messageContent","안녕하세요");
        assertThat(savedInterest).isNotNull().hasFieldOrPropertyWithValue("interestName","코딩");
    }

    @DisplayName("Select 테스트")
    @Test
    void givenData_whenSelectData_returnEntity(){
        //Given
        UserAccount user1 = UserAccount.of("user01","pass01","user1","서울",12,"INFP","M","하이요");
        UserAccount user2 = UserAccount.of("user02","pass02","user2","서울",12,"INFP","M","하이");

        user1.setRefreshToken("token");
        user2.setRefreshToken("token");
        UserAccount savedUser1 = userAccountRepository.save(user1);
        UserAccount savedUser2 = userAccountRepository.save(user2);

        Message message = Message.of(savedUser1,Long.valueOf(2),"안녕하세요", "read");
        Interest interest = Interest.of(savedUser1,"코딩");

        Message savedMessage = messageRepository.save(message);
        Interest savedInterest = interestRepository.save(interest);

        //When
        List<UserAccount> users = userAccountRepository.findAll();
        Message selectedMessage = messageRepository.findById(1L).orElseThrow();
        Interest selectedInterest = interestRepository.findById(1L).get();


        //Then
        assertThat(users).isNotNull().hasSize(2);
        assertThat(selectedMessage).isNotNull().hasFieldOrPropertyWithValue("messageContent","안녕하세요");
        assertThat(selectedInterest).isNotNull().hasFieldOrPropertyWithValue("interestName","코딩");
    }

    @DisplayName("Update 테스트")
    @Test
    void givenData_whenUpdateData_returnEntity(){
        //Given
        UserAccount user1 = UserAccount.of("user01","pass01","user1","서울",12,"INFP","M","하이요");
        UserAccount user2 = UserAccount.of("user02","pass02","user2","서울",12,"INFP","M","하이");

        user1.setRefreshToken("token");
        user2.setRefreshToken("token");

        UserAccount savedUser1 = userAccountRepository.save(user1);
        UserAccount savedUser2 = userAccountRepository.save(user2);

        Message message = Message.of(savedUser1,Long.valueOf(2),"안녕하세요", "read");
        Interest interest = Interest.of(savedUser1,"코딩");

        Message savedMessage = messageRepository.save(message);
        Interest savedInterest = interestRepository.save(interest);

        //When
        savedUser1.setUserId("user03");
        savedMessage.setMessageContent("가라");
        savedInterest.setInterestName("자전거");

        UserAccount updateUser = userAccountRepository.saveAndFlush(savedUser1);
        Message updateMessage = messageRepository.saveAndFlush(savedMessage);
        Interest updateInterest = interestRepository.saveAndFlush(savedInterest);


        //Then
        assertThat(savedUser1).hasFieldOrPropertyWithValue("userId","user03");
        assertThat(savedMessage).hasFieldOrPropertyWithValue("messageContent","가라");
        assertThat(savedInterest).isNotNull().hasFieldOrPropertyWithValue("interestName","자전거");
    }

    @DisplayName("Delete 테스트")
    @Test
    void givenData_whenDeleteData_returnEntity(){
        //Given
        UserAccount user1 = UserAccount.of("user01","pass01","user1","서울",12,"INFP","M","하이요");
        UserAccount user2 = UserAccount.of("user02","pass02","user2","서울",12,"INFP","M","하이");

        user1.setRefreshToken("token");
        user2.setRefreshToken("token");

        UserAccount savedUser1 = userAccountRepository.save(user1);
        UserAccount savedUser2 = userAccountRepository.save(user2);

        Message message = Message.of(savedUser1,Long.valueOf(2),"안녕하세요", "read");
        Interest interest = Interest.of(savedUser1,"코딩");

        Message savedMessage = messageRepository.save(message);
        Interest savedInterest = interestRepository.save(interest);

        //When

        userAccountRepository.delete(savedUser1);
        messageRepository.delete(savedMessage);
        interestRepository.delete(savedInterest);



        //Then
        assertThat(messageRepository.findAll()).hasSize(0);
        assertThat(userAccountRepository.findAll()).hasSize(1);
        assertThat(interestRepository.findAll()).hasSize(0);
    }


}