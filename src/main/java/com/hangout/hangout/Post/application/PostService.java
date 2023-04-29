package com.hangout.hangout.Post.application;

import com.hangout.hangout.Post.domain.repository.PostRepository;
import com.hangout.hangout.Post.dto.PostCreateRequest;
import com.hangout.hangout.domain.post.entity.Post;
import com.hangout.hangout.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    @Transactional
    public void createNewPost(PostCreateRequest postCreateRequest, User user) {
        Post post = postCreateRequest.toEntity(user);
        postRepository.save(post);
    }
}
