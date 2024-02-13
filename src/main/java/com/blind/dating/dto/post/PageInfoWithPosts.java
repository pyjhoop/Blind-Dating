package com.blind.dating.dto.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PageInfoWithPosts {

    private int pageNumber;
    private int totalPages;

    List<PostResponseDto> posts;
}
