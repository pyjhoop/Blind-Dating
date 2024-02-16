package com.blind.dating.domain.post;

import com.blind.dating.domain.post.Post;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("select p from Post p where p.id = :postId")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Post> findPost(Long postId);


}
