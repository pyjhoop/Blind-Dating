package com.blind.dating.domain.chatRoom;

import com.blind.dating.common.code.ChattingRoomResponseCode;
import com.blind.dating.domain.user.UserAccount;
import com.blind.dating.dto.chat.ChatDto;
import com.blind.dating.dto.chat.ChatListWithOtherUserInfo;
import com.blind.dating.dto.chat.ChatRoomDto;
import com.blind.dating.common.Api;
import com.blind.dating.domain.chat.ChatService;
import com.blind.dating.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ChattingRoomController {

    private final ChattingRoomService chattingRoomService;
    private final ChatService chatService;
    private final UserService userService;

    @GetMapping("/chatroom")
    public ResponseEntity<Api<List<ChatRoomDto>>> getMyMessageList(
            Authentication authentication
    ){
        String userId = (String) authentication.getPrincipal();
        //나에게 생성된 채팅룸 조회
        List<ChatRoomDto> rooms = chattingRoomService.getRooms(Long.valueOf(userId));

        return ResponseEntity.ok()
                .body(Api.OK(ChattingRoomResponseCode.GET_ROOMS_SUCCESS, rooms));
    }

    @GetMapping("/chatroom/{roomId}")
    public ResponseEntity<Api<ChatListWithOtherUserInfo>> enterRoom(
            @PathVariable Long roomId,
            @PageableDefault(size = 30, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable,
            Authentication authentication
    ){
        Long userId = Long.valueOf((String)authentication.getPrincipal());

        ChatRoomDto chatRoomDto = chattingRoomService.getRoom(userId, roomId);

        //존재할경우 채팅 메세지 30개를 id 오름차순으로 가져오기
        List<ChatDto> chatList = chatService.selectChatList(chatRoomDto, pageable)
                .getContent().stream().map(ChatDto::from).toList();

        ChatListWithOtherUserInfo chatListWithOtherUserInfo = ChatListWithOtherUserInfo.of(chatRoomDto.getOtherUserId(), chatRoomDto.getOtherUserNickname(), chatList);

        return ResponseEntity.ok()
                .body(Api.OK(ChattingRoomResponseCode.GET_CHATS_SUCCESS, chatListWithOtherUserInfo));
    }
}
