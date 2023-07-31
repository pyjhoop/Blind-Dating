package com.blind.dating.dto;

import com.blind.dating.domain.UserAccount;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Builder
public class UserWithMessageDto {

    private TokenDto token;
    private Long id;
    private String nickname;
    private String mbti;
    private String gender;
    private Set<MessageDto> messages;

    protected UserWithMessageDto(TokenDto token, Long id, String nickname, String mbti, String gender, Set<MessageDto> messages) {
        this.token = token;
        this.id = id;
        this.nickname = nickname;
        this.mbti = mbti;
        this.gender = gender;
        this.messages = messages;
    }

    public static UserWithMessageDto of(TokenDto token, Long id, String nickname, String mbti, String gender, Set<MessageDto> messages) {
        return new UserWithMessageDto(token, id, nickname, mbti, gender, messages);
    }

    public static UserWithMessageDto from(UserAccount user, String token){
        return new UserWithMessageDto(
                new TokenDto(token),
                user.getId(),
                user.getNickname(),
                user.getMbti(),
                user.getGender(),
                user.getMessages().stream().map(MessageDto::from)
                        .collect(Collectors.toCollection(LinkedHashSet::new))
        );
    }
}
