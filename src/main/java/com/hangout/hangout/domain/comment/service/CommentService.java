package com.hangout.hangout.domain.comment.service;

import com.hangout.hangout.domain.comment.domain.repository.CommentRepository;
import com.hangout.hangout.domain.comment.dto.*;
import com.hangout.hangout.domain.comment.entity.Comment;
import com.hangout.hangout.domain.post.entity.Post;
import com.hangout.hangout.domain.post.repository.PostRepository;
import com.hangout.hangout.domain.user.entity.User;
import com.hangout.hangout.global.common.domain.entity.Status;
import com.hangout.hangout.global.common.domain.repository.StatusRepository;
import com.hangout.hangout.global.error.ResponseType;
import com.hangout.hangout.global.exception.StatusNotFoundException;
import com.hangout.hangout.global.exception.UnAuthorizedAccessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//import static com.hangout.hangout.domain.comment.entity.Comment.comment;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final StatusRepository statusRepository;

    @Transactional
    public void saveComment(CommentCreateDto commentDto,User user){
        Post post = postRepository.findPostById(commentDto.getPostId()).get();
        Comment comment = commentDto.toEntity(commentDto,user,post);

        Long newStatus = 1L;
        Status status = statusRepository.findStatusById(newStatus).orElseThrow(
                () -> new StatusNotFoundException(ResponseType.STATUS_NOT_FOUND));

        comment.setStatus(status);

        commentRepository.save(comment);
    }

    @Transactional
    public void updateComment(Long id, CommentUpdateDto comment, User user){
        Comment comment2 = commentRepository.findById(id).orElseThrow(()->
                new IllegalArgumentException("해당댓글이 존재하지 않습니다."+id ));
        if(isMatchedNickname(comment2, user)) {
            comment2.update(comment.getContent());
        }
    }

    @Transactional
    public void deleteComment(Long id, User user){
        Comment comment2 = commentRepository.findById(id).orElseThrow(()->
                new IllegalArgumentException("해당댓글이 존재하지 않습니다."+id ));

        if(isMatchedNickname(comment2, user)) {
            Long deleteStatus = 2L;
            Status status = statusRepository.findStatusById(deleteStatus).orElseThrow(
                    () -> new StatusNotFoundException(ResponseType.STATUS_NOT_FOUND));

            comment2.setStatus(status);
            commentRepository.save(comment2);
        }
    }

    public boolean isMatchedNickname(Comment comment, User user) {
        String userNickname = user.getNickname();

        if (!comment.getUser().getNickname().equals(userNickname)) {
            throw new UnAuthorizedAccessException(ResponseType.REQUEST_NOT_VALID);
        }
        return true;
    }

    @Transactional
    public List<CommentReadDto> findCommentByPostId(Long postid){
        List<Comment> comments = commentRepository.findByPostId(postid);
        return comments.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }



    private CommentReadDto mapToDTO(Comment comment){
        return CommentReadDto.builder()
                .Id(comment.getId())
                .parentId(comment.getParentId())
                //.post(comment.getPost().getId())
                .content(comment.getContent())
                .build();
    }

    public Comment findCommentById(Long id) {
        return commentRepository.findById(id).orElseThrow(()->
                new IllegalArgumentException("해당댓글이 존재하지 않습니다."+id));
    }

    @Transactional
    public List<CommentRequestDTO> getAllCommentsByPost(Long postId) {

        List<Comment> comments = commentRepository.findByPostId(postId);
        List<CommentRequestDTO> commentRequestDTOList = new ArrayList<>();
        Map<Long, CommentRequestDTO> commentDTOHashMap = new HashMap<>();

        for (Comment comment : comments) {
            CommentRequestDTO commentRequestDTO = convertCommentTODto(comment);
            commentDTOHashMap.put(commentRequestDTO.getId(),commentRequestDTO);
            if(comment.getParent() !=null){
                commentDTOHashMap.get(comment.getParent().getId()).getChildren().add(commentRequestDTO);
            }else{
                commentRequestDTOList.add(commentRequestDTO);
            }
        }
        return commentRequestDTOList;
    }

    private CommentRequestDTO convertCommentTODto(Comment comment){
        return
                new CommentRequestDTO(comment.getId(),comment.getUser(), comment.getContent());
    }



}