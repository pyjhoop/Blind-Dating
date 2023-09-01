package com.blind.dating.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequestDto {
    private String region;
    private String mbti;
    private List<String> interests;
    private String selfIntroduction;
}
