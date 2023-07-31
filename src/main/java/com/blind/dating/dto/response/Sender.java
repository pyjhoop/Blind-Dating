package com.blind.dating.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Sender {
    private Long senderId;
    private String userId;
    private String nickname;
}
