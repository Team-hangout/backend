package com.hangout.hangout.domain.comment.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hangout.hangout.domain.comment.entity.Comment;
import com.hangout.hangout.domain.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class CommentRequestDTO extends Comment {
    private Long Id;
    @JsonIgnore
    private User user;
    private String content;
    private List<Comment> children;
    private List<CommentRequestDTO> children2;

    public CommentRequestDTO(Long Id,User user,String content) {
        this.Id = Id;
        this.user = user;
        this.content = content;
    }
    public List<CommentRequestDTO> getChildren2() {
        if (children2 == null) {
            children2 = new ArrayList<>();
        }
        return children2;
    }



    public CommentRequestDTO convertCommentTODto(Comment comment){
        return
                new CommentRequestDTO(comment.getId(),comment.getUser(), comment.getContent());
    }
}