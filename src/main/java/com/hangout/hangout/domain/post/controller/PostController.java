package com.hangout.hangout.domain.post.controller;

import static com.hangout.hangout.global.common.domain.entity.Constants.API_PREFIX;
import static com.hangout.hangout.global.error.ResponseEntity.successResponse;

import com.hangout.hangout.domain.image.service.ImageFileUploadService;
import com.hangout.hangout.domain.like.dto.LikeRequest;
import com.hangout.hangout.domain.like.service.LikeService;
import com.hangout.hangout.domain.post.PostMapper;
import com.hangout.hangout.domain.post.dto.PostListResponse;
import com.hangout.hangout.domain.post.dto.PostRequest;
import com.hangout.hangout.domain.post.dto.PostResponse;
import com.hangout.hangout.domain.post.dto.PostSearchRequest;
import com.hangout.hangout.domain.post.entity.Post;
import com.hangout.hangout.domain.post.service.PostService;
import com.hangout.hangout.domain.post.service.PostTagService;
import com.hangout.hangout.domain.user.entity.User;
import com.hangout.hangout.global.error.ResponseEntity;
import com.hangout.hangout.global.security.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(API_PREFIX + "/post")
public class PostController {

    private final PostService postService;
    private final PostTagService postTagService;
    private final ImageFileUploadService imageFileUploadService;
    private final LikeService likeService;
    private final PostMapper mapper;

    @PostMapping
    public ResponseEntity<HttpStatus> createNewPost(@RequestBody @Valid PostRequest
        postRequest, @CurrentUser User user) {
        postService.createNewPost(postRequest, user);

        return successResponse();
    }

    @PostMapping("/like")
    public ResponseEntity<HttpStatus> postLike(@RequestBody LikeRequest request) {
        likeService.insert(request);
        return successResponse();
    }

    @GetMapping("/{postId}")
    @Operation(summary = "유저의 게시물 조회", tags = {"post Controller"}, description = "Redis를 사용하여 게시물 조회 수 없데이트")
    @ApiResponse(responseCode = "201", description = "OK")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long postId, @CurrentUser User user) {
        List<String> tagsByPost = postTagService.getTagsByPost(postService.findPostById(postId));
        List<String> imagesByPost = imageFileUploadService.getImagesByPost(postService.findPostById(postId));

        postService.updatePostHits(postId, user);
        int likeStatus = postService.findLike(user, postId);

        return successResponse(mapper.of(postService.findPostById(postId),tagsByPost,imagesByPost,likeStatus));


    }

    @GetMapping("/all/{page}")
    public ResponseEntity<List<PostListResponse>> getPosts(@PathVariable Integer page
        , @RequestParam(defaultValue = "8") Integer size,
        @ModelAttribute PostSearchRequest postSearchRequest) {
        List<PostListResponse> posts = postService.getPosts(page, size, postSearchRequest);

        return successResponse("게시물 조회에 성공하셨습니다!", posts);
    }


    @PutMapping("/{postId}")
    public ResponseEntity<HttpStatus> updatePost(@PathVariable Long postId
        , @RequestBody @Valid PostRequest postRequest, @CurrentUser User user) {
        Post post = postService.findPostById(postId);

        postService.updatePost(post, post.getPostInfo(), postRequest, user);

        return successResponse();
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<HttpStatus> deletePost(@PathVariable Long postId,
        @CurrentUser User user) {
        Post post = postService.findPostById(postId);

        postService.deletePost(post, user);

        return successResponse();
    }

    @GetMapping("/hits/{postId}")
    @Operation(summary = "게시물 조회 수 조회", tags = {"Post Controller"})
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<Long> getPostHits(@PathVariable Long postId) {
        return successResponse(postService.getPostHits(postId));
    }

}
