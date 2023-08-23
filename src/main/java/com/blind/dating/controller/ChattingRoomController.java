package com.blind.dating.controller;

import com.blind.dating.domain.Chat;
import com.blind.dating.domain.ChatRoom;
import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.chat.ChatDto;
import com.blind.dating.dto.chat.ChatListWithOtherUserInfo;
import com.blind.dating.dto.chat.ChatRoomDto;
import com.blind.dating.dto.response.ResponseDto;
import com.blind.dating.dto.user.UserWithInterestAndAnswerDto;
import com.blind.dating.service.ChatService;
import com.blind.dating.service.ChattingRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Tag(name = "Chatting Room Info", description = "채팅방 관련 서비스")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ChattingRoomController {

    private final ChattingRoomService chattingRoomService;
    private final ChatService chatService;

    @GetMapping("/chatroom")
    @Operation(summary = "채팅방 전체 조회", description = "내가 들어간 채팅방을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    public ResponseEntity<ResponseDto<Page<ChatRoomDto>>> getMyMessageList(
            @PageableDefault(size = 10, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable,
            Authentication authentication
    ){
        UserAccount userAccount = (UserAccount) authentication.getPrincipal();

        //나에게 생성된 채팅룸 조회
        Page<ChatRoomDto> rooms = chattingRoomService.getRooms(userAccount.getId(), pageable).map(chatRoom -> {
            ChatRoomDto dto = new ChatRoomDto();

            // 조회를 위해 상대방 유저 정보 찾은 후 user2에 저장하기
            if(chatRoom.getUser1().getId() != userAccount.getId()){
                chatRoom.setUser2(chatRoom.getUser1());
            }
            // 채팅방에서 읽지 않은 채팅 개수 조회
            Long unReadCount = chatService.unreadChat(userAccount.getId(), chatRoom.getId());

            // 응답 데이터를 보여주기 위해 ChatRoom -> ChatRoomDto 로 변환
            dto.setRoomId(chatRoom.getId());
            dto.setUpdatedAt(chatRoom.getUpdatedAt());
            dto.setOtherUserid(chatRoom.getUser2().getId());
            dto.setOtherUserNickname(chatRoom.getUser2().getNickname());
            dto.setRecentMessage(chatRoom.getRecentMessage());
            dto.setUnReadCount(unReadCount);

            return dto;
        });
        return ResponseEntity.<ResponseDto<Page<ChatRoomDto>>>ok()
                .body(ResponseDto.<Page<ChatRoomDto>>builder()
                        .status("OK")
                        .message("채팅방 리스트가 성공적으로 조회되었습니다.")
                        .data(rooms)
                        .build());
    }

    @GetMapping("/chatroom/{roomId}")
    @Operation(summary = "채팅내용 조회", description = "채팅방 입장시 채팅 내역 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    @Parameter(name = "roomId", description = "채팅방 번호")
    public ResponseEntity<ResponseDto<ChatListWithOtherUserInfo>> enterRoom(
            @PathVariable String roomId,
            @PageableDefault(size = 30, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            Authentication authentication
    ){
        UserAccount user = (UserAccount)authentication.getPrincipal();

        // 해당 방이 있는지 조회해서 없으면 예외 처리 해주기
        Optional<ChatRoom> chatRoom = chattingRoomService.getRoom(roomId);

        if(chatRoom.isEmpty()){
            throw new RuntimeException(roomId+"에 해당하는 채팅방이 존재하지 않습니다.");
        }
        //
        UserAccount userInfo = chatRoom.get().getUser1();

        if(user == chatRoom.get().getUser1()){
            userInfo = chatRoom.get().getUser2();
        }



        //존재할경우 채팅 메세지 50개를 id 오름차순으로 가져오기
        Page<ChatDto> chatList = chatService.selectChatList(roomId, pageable).map(ChatDto::from);

        return ResponseEntity.ok().body(ResponseDto.<ChatListWithOtherUserInfo>builder()
                .status("OK")
                .message("채팅 메세지가 성공적으로 조회되었습니다.")
                .data(ChatListWithOtherUserInfo.of(userInfo.getId(), userInfo.getNickname(), chatList))
                .build());
    }
}
