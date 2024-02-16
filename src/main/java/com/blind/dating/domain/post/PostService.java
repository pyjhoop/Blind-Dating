package com.blind.dating.domain.post;

import com.blind.dating.common.code.PostResponseCode;
import com.blind.dating.domain.user.UserAccount;
import com.blind.dating.dto.post.PageInfoWithPosts;
import com.blind.dating.dto.post.PostRequestDto;
import com.blind.dating.dto.post.PostResponseDto;
import com.blind.dating.exception.ApiException;
import com.blind.dating.domain.user.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserAccountRepository userAccountRepository;

    @Transactional
    public PostResponseDto createPost(Authentication authentication, PostRequestDto request) {
        Long userId = Long.valueOf((String) authentication.getPrincipal());
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(()-> new ApiException(PostResponseCode.CREATE_POST_FAil));

        return PostResponseDto.From(postRepository.save(request.toEntity(user)));
    }

    @Transactional
    public PostResponseDto updatePost(Long postId, PostRequestDto request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ApiException(PostResponseCode.POST_NOT_FOUNT));
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());

        return PostResponseDto.From(post);
    }

    @Transactional
    public PostResponseDto getPost(Long postId) {
        Post post = postRepository.findPost(postId)
                .orElseThrow(()-> new ApiException(PostResponseCode.POST_NOT_FOUNT));
        // view 증가
        post.increaseView();
        return PostResponseDto.From(post);
    }

    @Transactional(readOnly = true)
    public PageInfoWithPosts getPosts(Pageable pageable) {
        Page<Post> list = postRepository.findAll(pageable);

        List<PostResponseDto> dtoList = list.getContent()
                .stream().map(PostResponseDto::From).toList();

        return new PageInfoWithPosts(list.getNumber(), list.getTotalPages(), dtoList);
    }
}
