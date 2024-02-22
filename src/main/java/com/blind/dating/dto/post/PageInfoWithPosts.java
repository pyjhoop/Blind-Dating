package com.blind.dating.dto.post;

import com.blind.dating.domain.post.PostResponseDto;
import com.blind.dating.domain.post.PostResponseWithCommentDto;
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
