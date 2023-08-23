package com.blind.dating.controller;

import com.blind.dating.domain.ChatRoom;
import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.chat.ChatDto;
import com.blind.dating.dto.chat.ChatListWithOtherUserInfo;
import com.blind.dating.dto.chat.ChatRoomDto;
import com.blind.dating.dto.response.ResponseDto;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
    public ResponseEntity<ResponseDto<List<ChatRoomDto>>> getMyMessageList(
            Authentication authentication
    ){
        UserAccount userAccount = (UserAccount) authentication.getPrincipal();

        //나에게 생성된 채팅룸 조회
        List<ChatRoomDto> rooms = chattingRoomService.getRooms(userAccount);

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

        //존재할경우 채팅 메세지 30개를 id 오름차순으로 가져오기
        Page<ChatDto> chatList = chatService.selectChatList(roomId, pageable).map(ChatDto::from);

        return ResponseEntity.ok().body(ResponseDto.<ChatListWithOtherUserInfo>builder()
                .status("OK")
                .message("채팅 메세지가 성공적으로 조회되었습니다.")
                .data(ChatListWithOtherUserInfo.of(userInfo.getId(), userInfo.getNickname(), chatList))
                .build());
    }

//    @PutMapping("/chatroom/{roomId}")
//    public ResponseEntity<ResponseDto> leaveChatRoom(@PathVariable String roomId, Authentication authentication){
//        //룸 조회후 내 정보를 null로 수정
//        UserAccount user = (UserAccount) authentication.getPrincipal();
//        //채팅방 떠나는 기능
//        ChatRoom room = chattingRoomService.leaveChatRoom(roomId, user);
//        //채팅방에 아무도 없을 때 채팅방 삭제
//        boolean check = chattingRoomService.removeRoom(room);
//        String message = "";
//
//        if(check){
//            message = "채팅방이 삭제되었습니다.";
//        }else{
//            message = "채팅방을 성공적으로 나갔습니다.";
//        }
//
//        return ResponseEntity.<ResponseDto>ok()
//                .body(ResponseDto.<String>builder()
//                        .status("OK")
//                        .message(message)
//                        .build());
//    }


}
