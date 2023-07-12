package com.hangout.hangout.domain.like.service;

import com.hangout.hangout.domain.like.dto.LikeRequest;
import com.hangout.hangout.domain.like.entity.PostLike;
import com.hangout.hangout.domain.like.repository.LikeRepository;
import com.hangout.hangout.domain.post.entity.Post;
import com.hangout.hangout.domain.post.repository.PostRepository;
import com.hangout.hangout.domain.user.entity.User;
import com.hangout.hangout.domain.user.repository.UserRepository;
import com.hangout.hangout.global.error.ResponseType;
import com.hangout.hangout.global.exception.NotFoundException;
import com.hangout.hangout.global.exception.PostNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public void insert(LikeRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException(ResponseType.USER_NOT_EXIST_ID));
        Post post = postRepository.findPostById(request.getPostId())
                .orElseThrow(() -> new PostNotFoundException(ResponseType.POST_NOT_FOUND));
        Optional<PostLike> postLike = likeRepository.findByUserAndPost(user, post);

        if (postLike.isEmpty()){
            PostLike like = PostLike.builder()
                    .post(post)
                    .user(user)
                    .build();
            likeRepository.save(like);
            postRepository.addLikeCount(post);
        } else {
            likeRepository.deleteByUserAndPost(user, post);
            postRepository.subLikeCount(post);
        }
    }
}
