package com.blind.dating.domain.user.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserUpdateRequestDto {
        private String region;
        private String mbti;
        private List<String> interests;
        private String selfIntroduction;
}
