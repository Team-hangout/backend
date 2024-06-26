package com.hangout.hangout.domain.post.repository;

import com.hangout.hangout.domain.post.dto.PostSearchRequest;
import com.hangout.hangout.domain.post.entity.Post;
import java.util.Optional;

import com.hangout.hangout.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryQuerydsl {

    Optional<Post> findPostById(Long postId);

    Page<Post> findAllPostByUser(Pageable pageable, User user);
    Page<Post> findAllPostByUserLike(Pageable pageable, User user);

    Page<Post> findAllPostByCreatedAtDesc(Pageable pageable);

    // 검색 조건 있는 모든 게시글 조회
    Page<Post> SearchAllPostByCreatedAtDesc(Pageable pageable, PostSearchRequest postSearchRequest);

    void addLikeCount(Post selectpost);

    void subLikeCount(Post selectpost);

    Page<Post> findAllByOrderByPostHits(Pageable page, boolean isDescending);

    Page<Post> findAllByOrderByPostLikes(Pageable page, boolean isDescending);

}
