package com.blind.dating.controller;

import com.blind.dating.common.Api;
import com.blind.dating.common.code.MessageResponseCode;
import com.blind.dating.dto.message.MessageRequestDto;
import com.blind.dating.dto.message.MessageResponseDto;
import com.blind.dating.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class MessageController {
    private final MessageService messageService;

    //TODO 메시지 보내기
    @PostMapping("/messages")
    public ResponseEntity<?> postMessage(
            Authentication authentication,
            @RequestBody MessageRequestDto dto
    )
    {
        Long userId = Long.valueOf((String) authentication.getPrincipal());
        messageService.postMessage(userId, dto);

        return ResponseEntity.ok()
                .body(Api.OK(MessageResponseCode.POST_MESSAGE_SUCCESS));
    }

    //TODO 메시지 수락하기
    @PatchMapping("/messages/{messageId}/accept")
    public ResponseEntity<?> acceptMessage(
            @PathVariable Long messageId,
            Authentication authentication
    ) {
        Long userId = Long.valueOf((String) authentication.getPrincipal());
        messageService.acceptMessage(userId, messageId);

        return ResponseEntity.ok()
                .body(Api.OK(MessageResponseCode.ACCEPT_MESSAGE_SUCCESS));
    }

    //TODO 메시지 거절하기
    @PatchMapping("/messages/{messageId}/reject")
    public ResponseEntity<?> rejectMessage(
            @PathVariable Long messageId,
            Authentication authentication
    ) {
        Long userId = Long.valueOf((String) authentication.getPrincipal());
        messageService.rejectMessage(userId, messageId);
        return ResponseEntity.ok()
                .body(Api.OK(MessageResponseCode.REJECT_MESSAGE_SUCCESS));
    }

    //TODO 내게온 메시지 조회하기
    @GetMapping("/messages/me")
    public ResponseEntity<?> getMessageToMe(
            Authentication authentication
    ) {
        Long userId = Long.valueOf((String) authentication.getPrincipal());
        List<MessageResponseDto> list = messageService.getMessageToMe(userId);

        return ResponseEntity.ok()
                .body(Api.OK(MessageResponseCode.GET_MESSAGE_TOME_SUCCESS, list));
    }

    // TODO 내가 보낸 메시지 조회하기
    @GetMapping("/messages")
    public ResponseEntity<?> getMessageFromMe(
            Authentication authentication
    ) {
        Long userId = Long.valueOf((String) authentication.getPrincipal());
        List<MessageResponseDto> list = messageService.getMessageFromMe(userId);

        return ResponseEntity.ok()
                .body(Api.OK(MessageResponseCode.GET_MESSAGE_FROMME_SUCCESS, list));
    }

}
