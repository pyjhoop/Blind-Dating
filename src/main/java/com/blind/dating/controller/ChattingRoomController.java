package com.blind.dating.controller;

import com.blind.dating.domain.Chat;
import com.blind.dating.domain.ChatRoom;
import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.chat.ChatDto;
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
import java.util.stream.Collectors;

@Tag(name = "Chatting Room Info", description = "채팅방 관련 서비스")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ChattingRoomController {

    private final ChattingRoomService chattingRoomService;
    private final ChatService chatService;

    @GetMapping("/rooms/{userId}")
    @Operation(summary = "채팅방 전체 조회", description = "내가 들어간 채팅방을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    @Parameter(name = "userId", description = "회원의 현재 번호")
    public ResponseDto<Page<ChatRoomDto>> getMyMessageList(
            @PathVariable String userId,
            @PageableDefault(size = 10, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable,
            Authentication authentication
    ){
        UserAccount userAccount = (UserAccount) authentication.getPrincipal();
        Page<ChatRoomDto> rooms = chattingRoomService.getRooms(userId, pageable).map(chatRoom -> {
            ChatRoomDto dto = new ChatRoomDto();
            if(chatRoom.getUser1().getId() != userAccount.getId()){
                chatRoom.setUser2(chatRoom.getUser1());
            }
            Long unReadCount = chatService.unreadChat(userAccount.getId(), chatRoom.getId());

            dto.setRoomId(chatRoom.getId());
            dto.setUpdatedAt(chatRoom.getUpdatedAt());
            dto.setOtherUserid(chatRoom.getUser2().getId());
            dto.setOtherUserNickname(chatRoom.getUser2().getNickname());
            dto.setRecentMessage(chatRoom.getRecentMessage());
            dto.setUnReadCount(unReadCount);

            // 읽지 않은 메세지 개수도 조회 해야함.

            return dto;
        });

        return ResponseDto.<Page<ChatRoomDto>>builder()
                .status("OK")
                .message("설공적으로 조회되었습니다.")
                .data(rooms)
                .build();
    }

    @GetMapping("/chatroom/{roomId}")
    @Operation(summary = "채팅방 입장", description = "채팅방 입장시 채팅 내역 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    @Parameter(name = "userId", description = "회원의 현재 번호")
    public ResponseEntity<ResponseDto<List<ChatDto>>> enterRoom(
            @PathVariable String roomId
    ){
        // 해당 방이 있는지 조회해서 없으면 예외 처리 해주기
        boolean check = chattingRoomService.checkRoomNumber(roomId);

        if(!check){
            throw new RuntimeException(roomId+"에 해당하는 채팅방이 존재하지 않습니다.");
        }

        //존재할경우 채팅 메세지 50개를 id 오름차순으로 가져오기
        List<ChatDto> chatList = chatService.selectChatList(roomId)
                .stream().map(ChatDto::from).collect(Collectors.toList());

        return ResponseEntity.ok().body(ResponseDto.<List<ChatDto>>builder()
                .status("OK")
                .message("채팅 메세지가 성공적으로 조회되었습니다.")
                .data(chatList).build());
    }
}
