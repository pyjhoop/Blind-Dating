package com.blind.dating.dto.user;

import lombok.Data;

import java.util.List;

@Data
public class UserInfoWithPageInfo {
    private int pageNumber;
    private int totalPages;
    private List<UserWithInterestAndQuestionDto> content;
}
