package com.blind.dating.domain.comment;

import com.blind.dating.common.code.CommentResponseCode;
import com.blind.dating.common.code.PostResponseCode;
import com.blind.dating.common.code.UserResponseCode;
import com.blind.dating.domain.post.Post;
import com.blind.dating.domain.post.PostRepository;
import com.blind.dating.domain.user.UserAccount;
import com.blind.dating.domain.user.UserAccountRepository;
import com.blind.dating.dto.user.UserResponse;
import com.blind.dating.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;


@RequiredArgsConstructor
@Service
public class CommentService {
    private final PostRepository postRepository;
    private final UserAccountRepository userAccountRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public void createComment(Authentication authentication, CommentRequestDto commentRequestDto) {
        Long userId = Long.valueOf((String) authentication.getPrincipal());
        UserAccount userAccount = userAccountRepository.findById(userId)
                .orElseThrow(()-> new ApiException(UserResponseCode.USER_NOT_FOUND));

        Post post = postRepository.findById(commentRequestDto.postId())
                .orElseThrow(()-> new ApiException(PostResponseCode.POST_NOT_FOUNT));

        Comment comment = Comment.of(commentRequestDto.content(), true, userAccount, post);

        post.getComments().add(comment);
    }

    @Transactional
    public void updateComment(Authentication authentication, Long commentId, String content) {
        Long userId = Long.valueOf((String) authentication.getPrincipal());

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()-> new ApiException(CommentResponseCode.COMMENT_GET_FAIL));

        if(!Objects.equals(comment.getUserAccount().getId(), userId)) {
            throw  new ApiException(UserResponseCode.AUTHORIZE_FAIL);
        } else {
            comment.setContent(content);
        }

    }

    @Transactional
    public void deleteComment(Authentication authentication, Long commentId) {
        Long userId = Long.valueOf((String) authentication.getPrincipal());


        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()-> new ApiException(CommentResponseCode.COMMENT_GET_FAIL));

        if(!Objects.equals(comment.getUserAccount().getId(), userId)) {
            throw new ApiException(UserResponseCode.AUTHORIZE_FAIL);
        }
        comment.setStatus(false);
    }
}
