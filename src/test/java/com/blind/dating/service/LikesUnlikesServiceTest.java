package com.blind.dating.service;

import com.blind.dating.domain.ChatRoom;
import com.blind.dating.domain.LikesUnlikes;
import com.blind.dating.domain.ReadChat;
import com.blind.dating.domain.UserAccount;
import com.blind.dating.repository.LikesUnlikesRepository;
import com.blind.dating.repository.ReadChatRepository;
import com.blind.dating.repository.UserAccountRepository;
import com.blind.dating.repository.querydsl.LikesUnlikesRepositoryImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@DisplayName("LikesUnlikesService - 테스트")
@ExtendWith(MockitoExtension.class)
class LikesUnlikesServiceTest {

    @Mock private LikesUnlikesRepository likesUnlikesRepository;
    @Mock private UserAccountRepository userAccountRepository;
    @Mock private LikesUnlikesRepositoryImpl likesUnlikesRepositoryImpl;
    @Mock private ChattingRoomService chatRoomService;
    @Mock private ReadChatRepository readChatRepository;
    @InjectMocks private LikesUnlikesService likesUnlikesService;

    @DisplayName("좋아요 - 테스트")
    @Test
    void givenUserIdAndReceiverId_whenLikeUser_thenSaveLikes(){
        //Given
        Authentication authentication = new UsernamePasswordAuthenticationToken("1",null,null);
        String userId = "1";
        String receiverId = "2";
        UserAccount senderAccount = UserAccount.of("qweeqw","asdfdf", "nickname","asdf","asdf","M","하이요");
        UserAccount receiverAccount = UserAccount.of("qweeqw1","asdfdf1", "nickname1","asdf1","asdf","M","하이요");

        given(userAccountRepository.findById(Long.parseLong(receiverId))).willReturn(Optional.of(receiverAccount));
        given(likesUnlikesRepository.save(any(LikesUnlikes.class))).willReturn(new LikesUnlikes());
        given(userAccountRepository.findById(Long.parseLong(userId))).willReturn(Optional.of(senderAccount));

        given(likesUnlikesRepositoryImpl.findLikes(receiverAccount.getId(), senderAccount)).willReturn(new ArrayList<>());

        // When
        Boolean result = likesUnlikesService.likeUser(authentication, receiverId);

        // Then
        assertThat(result).isFalse(); // 좋아요 눌렀으므로 true 여부 확인


    }

}