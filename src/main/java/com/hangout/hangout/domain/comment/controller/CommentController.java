package com.hangout.hangout.domain.comment.controller;

import com.hangout.hangout.domain.comment.dto.*;
import com.hangout.hangout.domain.comment.service.CommentService;
import com.hangout.hangout.domain.like.dto.LikeCommentRequest;
import com.hangout.hangout.domain.like.service.LikeService;
import com.hangout.hangout.domain.user.entity.User;
import com.hangout.hangout.global.error.ResponseEntity;
import com.hangout.hangout.global.security.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.hangout.hangout.global.common.domain.entity.Constants.API_PREFIX;
import static com.hangout.hangout.global.error.ResponseEntity.successResponse;


@RequiredArgsConstructor
@RestController
@RequestMapping(API_PREFIX + "/comment")
public class CommentController {
    private final CommentService commentService;
    private final LikeService likeService;

    @PostMapping
    public ResponseEntity<HttpStatus> Create(@RequestBody CommentCreateDto
                                                            commentCreateDto, @CurrentUser User user){
        commentService.saveComment(commentCreateDto,user);
        return successResponse();
    }

    @PostMapping("/like")
    public ResponseEntity<HttpStatus> CommentLike(@RequestBody LikeCommentRequest request,
                                                  @CurrentUser User user){
        likeService.insert(user, request);
        return successResponse();
    }

    @GetMapping("/like")
    public ResponseEntity<Integer> FindLike(@RequestBody LikeCommentRequest request,
                                            @CurrentUser User user){
        int like = commentService.findLike(user, request);
        return successResponse("작성자가 좋아요를 누른 상태 여부 조사 결과 ( 좋아요 상태가 아니면 0, 맞다면 1 )", like);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HttpStatus> update(@PathVariable Long id, @CurrentUser User user,
                                             @RequestBody CommentUpdateDto commentUpdateRequestDto){
        commentService.updateComment(id,commentUpdateRequestDto, user);

        return successResponse();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteComment(@PathVariable Long id,
                                                    @CurrentUser User user){
        commentService.deleteComment(id, user);
        return successResponse();

    }
    @GetMapping("/{postId}")
    public ResponseEntity<List<CommentRequestDTO>> readComment(@PathVariable Long postId){
        List<CommentRequestDTO> comments= commentService.getAllCommentsByPost(postId);
        return successResponse("답글 조회에 성공하셨습니다!", comments);
    }

    @GetMapping("/me")
    @Operation(summary = "작성한 댓글 조회")
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<List<CommentRequestDTO>> getMyComments(@CurrentUser User user) {
        List<CommentRequestDTO> comments= commentService.getCommentsByUser(user);
        return successResponse("작성한 댓글 조회에 성공하셨습니다!", comments);
    }
}