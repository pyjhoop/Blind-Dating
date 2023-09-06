package com.blind.dating.controller;

import com.blind.dating.domain.ChatRoom;
import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.chat.ChatDto;
import com.blind.dating.dto.chat.ChatListWithOtherUserInfo;
import com.blind.dating.dto.chat.ChatRoomDto;
import com.blind.dating.dto.response.ResponseDto;
import com.blind.dating.service.ChatService;
import com.blind.dating.service.ChattingRoomService;
import com.blind.dating.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Tag(name = "Chatting Room Info", description = "채팅방 관련 서비스")
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ChattingRoomController {

    private final ChattingRoomService chattingRoomService;
    private final ChatService chatService;
    private final UserService userService;

    @GetMapping("/chatroom")
    @Operation(summary = "채팅방 전체 조회", description = "내가 들어간 채팅방을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    public ResponseEntity<ResponseDto<List<ChatRoomDto>>> getMyMessageList(
            Authentication authentication
    ){
        String userId = (String) authentication.getPrincipal();

        //나에게 생성된 채팅룸 조회
        List<ChatRoomDto> rooms = chattingRoomService.getRooms(Long.valueOf(userId));

        return ResponseEntity.<ResponseDto<List<ChatRoomDto>>>ok()
                .body(ResponseDto.<List<ChatRoomDto>>builder()
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
            @RequestParam String chatId,
            @PageableDefault(size = 30, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            Authentication authentication
    ){
        Long userId = Long.valueOf((String)authentication.getPrincipal());

        // 해당 방이 있는지 조회해서 없으면 예외 처리 해주기
        ChatRoom chatRoom = chattingRoomService.getRoom(roomId)
                .orElseThrow(() -> new RuntimeException("채팅목록 조회시 예외가 발생했습니다."));

        //다른 유저 정보 조회하기.
        Long otherId = 0L;
        for(UserAccount userAccount: chatRoom.getUsers()){
            if(userAccount.getId() != userId){
                otherId = userAccount.getId();
                break;
            }
        }

        UserAccount userInfo = userService.getUser(otherId)
                .orElseThrow(()-> new RuntimeException("채팅목록 조회시 예외가 발생했습니다."));
        //존재할경우 채팅 메세지 30개를 id 오름차순으로 가져오기
        List<ChatDto> chatList = chatService.selectChatList(chatRoom, chatId).stream().map(ChatDto::from).limit(30).collect(Collectors.toList());
        ChatListWithOtherUserInfo chatListWithOtherUserInfo = ChatListWithOtherUserInfo.of(userInfo.getId(), userInfo.getNickname(),chatRoom.getStatus(), chatList);


        return ResponseEntity.ok().body(ResponseDto.<ChatListWithOtherUserInfo>builder()
                .status("OK")
                .message("채팅 메세지가 성공적으로 조회되었습니다.")
                .data(chatListWithOtherUserInfo).build());
    }
}
