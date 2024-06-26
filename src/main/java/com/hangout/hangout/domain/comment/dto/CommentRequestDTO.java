package com.hangout.hangout.domain.comment.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hangout.hangout.domain.comment.entity.Comment;
import com.hangout.hangout.domain.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class CommentRequestDTO {
    private Long Id;
    private Long postId;
    private String postTitle;
    private String nickname;
    private String content;
    private Integer likeCount;
    private LocalDateTime createdAt;
    private List<CommentRequestDTO> children;

    public CommentRequestDTO(Long Id,Long postId,String postTitle,String nickname,String content,Integer likeCount, LocalDateTime createdAt) {
        this.Id = Id;
        this.postId = postId;
        this.postTitle = postTitle;
        this.nickname = nickname;
        this.content = content;
        this.likeCount = likeCount;
        this.createdAt = createdAt;
    }
    public List<CommentRequestDTO> getChildren() {
        if (children == null) {
            children = new ArrayList<>();
        }
        return children;
    }

    public CommentRequestDTO convertCommentTODto(Comment comment){
        return new CommentRequestDTO(comment.getId(),comment.getPost().getId(), comment.getPost().getTitle()
                ,comment.getUser().getNickname(),comment.getContent(),comment.getLikeCount(), comment.getCreatedAt());
    }
}