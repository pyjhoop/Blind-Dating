package com.blind.dating.controller;

import com.blind.dating.domain.Message;
import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.MessageDto;
import com.blind.dating.dto.MessageInput;
import com.blind.dating.dto.response.MessageResponse;
import com.blind.dating.dto.response.ResponseDto;
import com.blind.dating.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "message API", description = "메세지 관련 API")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class MessageController {

    private final MessageService messageService;

    //나에게 할당된 메세지들 가져오기
    @Operation(summary = "messages GET", description = "내가 보내진 메세지 가져오기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    @GetMapping("/messages/me")
    public ResponseDto<List<MessageResponse>> getMessages(
            Authentication authentication
    ){
        UserAccount user = (UserAccount) authentication.getPrincipal();
        Long receiverId = user.getId();
        List<MessageResponse> messageList = messageService.getMessages(receiverId)
                .stream().map(MessageDto::from).map(MessageResponse::from).collect(Collectors.toList());

        return ResponseDto.<List<MessageResponse>>builder()
                .status("OK")
                .message("내게온 메세지가 성공적으로 조회되었습니다.")
                .data(messageList).build();
    }

    // 내가 보낸 메세지 조회하기.
    @Operation(summary = "messages GET", description = "내가 보낸 메세지 가져오기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    @GetMapping("/messages/sent")
    public ResponseDto<List<MessageResponse>> getSendMessages(
            Authentication authentication
    ){
        UserAccount sender = (UserAccount) authentication.getPrincipal();

        List<MessageResponse> messageList = messageService.getSentMessages(sender)
                .stream().map(MessageDto::from).map(MessageResponse::from).collect(Collectors.toList());

        return ResponseDto.<List<MessageResponse>>builder()
                .status("OK")
                .message("내가 보낸 메세지가 성공적으로 조회되었습니다.")
                .data(messageList).build();
    }


    @Operation(summary = "message GET", description = "메세지 단건 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    @GetMapping("/message/{id}")
    public ResponseDto<MessageResponse> getMessage(
            @PathVariable Long id
    ){
        Message message = messageService.getMessage(id).orElseThrow(RuntimeException::new);

        return ResponseDto.<MessageResponse>builder()
                .status("OK")
                .message("메세지가 성공적으로 조회되었습니다.")
                .data(MessageResponse.from(MessageDto.from(message))).build();
    }

    // 메세지 작성하기
    @Operation(summary = "message POST", description = "메세지 작성하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    @Parameters(value = {
            @Parameter(name = "receiverId", description = "받는 유저 ID"),
            @Parameter(name = "messageContent", description = "메세지 내용")
    })
    @PostMapping("/message")
    public ResponseDto<MessageResponse> createMessage(
           @RequestBody MessageInput messageInput,
            Authentication authentication
    ){
        UserAccount userAccount = (UserAccount)authentication.getPrincipal();
        MessageDto dto = MessageDto.of(0L,userAccount, messageInput.getReceiver(), messageInput.getMessageContent(),"UNREAD");

        MessageDto message = MessageDto.from(messageService.createMessage(dto));

        return ResponseDto.<MessageResponse>builder()
                .status("OK")
                .message("성공적으로 메세지를 보냈습니다.")
                .data(MessageResponse.from(message)).build();

    }



}
