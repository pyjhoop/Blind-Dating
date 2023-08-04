package com.blind.dating.controller;

import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.answer.AnswerDto;
import com.blind.dating.dto.response.ResponseDto;
import com.blind.dating.service.AnswerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Answer Info", description = "답변 관련 서비스")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;


    @PostMapping("/answer")
    @Operation(summary = "내 답변 저장", description = "질문에 대한 답변을 저장합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    @Parameter(name = "List<AnswerDto>", description = "답변 리스트")
    public ResponseDto<List<AnswerDto>> saveAnswers(
            @RequestBody List<AnswerDto> answers,
            Authentication authentication
            ){

        UserAccount user = (UserAccount) authentication.getPrincipal();

        List<AnswerDto> list = answerService.saveAnswer(answers, user)
                .stream().map(AnswerDto::from).collect(Collectors.toList());

        return ResponseDto.<List<AnswerDto>>builder()
                .status("OK")
                .message("응답이 성공적으로 저장되었습니다.")
                .data(list).build();
    }

    @GetMapping("/answers")
    @Operation(summary = "답변 조회", description = "질문에 대한 답변을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    public ResponseDto<List<AnswerDto>> getAnswers(
            Authentication authentication
    ){
        UserAccount user = (UserAccount) authentication.getPrincipal();

        List<AnswerDto> list = answerService.getAnswers(user)
                .stream().map(AnswerDto::from).collect(Collectors.toList());

        return ResponseDto.<List<AnswerDto>>builder()
                .status("OK")
                .message("응답이 성공적으로 조회되었습니다.")
                .data(list).build();

    }


}
