package com.blind.dating.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserInfoWithPageInfo {
    private int pageNumber;
    private int totalPages;
    private List<UserInfoDto> content;
}
