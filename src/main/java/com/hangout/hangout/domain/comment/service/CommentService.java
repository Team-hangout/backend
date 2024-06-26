package com.hangout.hangout.domain.comment.service;

import com.hangout.hangout.domain.comment.domain.repository.CommentRepository;
import com.hangout.hangout.domain.comment.dto.CommentCreateDto;
import com.hangout.hangout.domain.comment.dto.CommentReadDto;
import com.hangout.hangout.domain.comment.dto.CommentRequestDTO;
import com.hangout.hangout.domain.comment.dto.CommentUpdateDto;
import com.hangout.hangout.domain.comment.entity.Comment;
import com.hangout.hangout.domain.like.dto.LikeCommentRequest;
import com.hangout.hangout.domain.like.entity.CommentLike;
import com.hangout.hangout.domain.like.repository.LikeCommentRepository;
import com.hangout.hangout.domain.post.entity.Post;
import com.hangout.hangout.domain.post.service.PostService;
import com.hangout.hangout.domain.user.entity.User;
import com.hangout.hangout.global.common.domain.entity.Status;
import com.hangout.hangout.global.common.domain.repository.StatusRepository;
import com.hangout.hangout.global.error.ResponseType;
import com.hangout.hangout.global.exception.NotFoundException;
import com.hangout.hangout.global.exception.StatusNotFoundException;
import com.hangout.hangout.global.exception.UnAuthorizedAccessException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostService postService;
    private final StatusRepository statusRepository;
    private final LikeCommentRepository likeCommentRepository;

    @Transactional
    public void saveComment(CommentCreateDto commentCreateDto, User user) {
        Post post = postService.findPostById(commentCreateDto.getPostId());
        Comment comment = commentCreateDto.toEntity(commentCreateDto, user, post);

        Long newStatus = 1L;
        Status status = statusRepository.findStatusById(newStatus).orElseThrow(
            () -> new StatusNotFoundException(ResponseType.STATUS_NOT_FOUND));

        comment.setStatus(status);

        commentRepository.save(comment);
    }

    @Transactional
    public void updateComment(Long id, CommentUpdateDto commentUpdateDto, User user) {
        Comment comment = commentRepository.findCommentById(id).orElseThrow(() ->
            new NotFoundException(ResponseType.COMMENT_NOT_FOUND));

        if (isMatchedNickname(comment, user)) {
            comment.update(commentUpdateDto.getContent());
        }
    }

    @Transactional
    public void deleteComment(Long id, User user) {
        Comment comment = commentRepository.findCommentById(id).orElseThrow(() ->
            new NotFoundException(ResponseType.COMMENT_NOT_FOUND));

        if (isMatchedNickname(comment, user)) {
            Long deleteStatus = 2L;
            Status status = statusRepository.findStatusById(deleteStatus).orElseThrow(
                () -> new StatusNotFoundException(ResponseType.STATUS_NOT_FOUND));

            comment.setStatus(status);
            commentRepository.save(comment);
        }
    }

    public boolean isMatchedNickname(Comment comment, User user) {
        String userNickname = user.getNickname();

        if (!comment.getUser().getNickname().equals(userNickname)) {
            throw new UnAuthorizedAccessException(ResponseType.UNMATCHED_COMMENT_AND_USER);
        }
        return true;
    }

    public int findLike(User user, LikeCommentRequest request) {
        Long commentId = request.getCommentId();

        Comment comment2 = commentRepository.findCommentById(commentId).orElseThrow(() ->
            new NotFoundException(ResponseType.COMMENT_NOT_FOUND));
        // 좋아요 상태가 아니면 0, 맞다면 1
        Optional<CommentLike> findLike = likeCommentRepository.findByUserAndComment(user, comment2);

        if (findLike.isEmpty()) {
            return 0;
        } else {
            return 1;
        }
    }

    @Transactional
    public List<CommentReadDto> findCommentByPostId(Long postid) {
        List<Comment> comments = commentRepository.findByPostId(postid);
        return comments.stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }


    private CommentReadDto mapToDTO(Comment comment) {
        return CommentReadDto.builder()
            .Id(comment.getId())
            .parentId(comment.getParentId())
            //.post(comment.getPost().getId())
            .content(comment.getContent())
            .build();
    }

    public Comment findCommentById(Long id) {
        return commentRepository.findById(id).orElseThrow(() ->
            new NotFoundException(ResponseType.COMMENT_NOT_FOUND));
    }

    public List<CommentRequestDTO> getCommentsByUser(User user) {
        List<Comment> comments = commentRepository.findCommentByUser(user);
        List<CommentRequestDTO> commentRequestDTOList = new ArrayList<>();
        Map<Long, CommentRequestDTO> commentDTOHashMap = new HashMap<>();

        for (Comment comment : comments) {
            CommentRequestDTO commentRequestDTO = convertCommentTODto(comment);
            commentDTOHashMap.put(commentRequestDTO.getId(), commentRequestDTO);
            commentRequestDTOList.add(commentRequestDTO);
        }
        return commentRequestDTOList;
    }

    @Transactional
    public List<CommentRequestDTO> getAllCommentsByPost(Long postId) {
        Post post = postService.findPostById(postId);
        List<Comment> comments = commentRepository.findByPostId(post.getId());
        List<CommentRequestDTO> commentRequestDTOList = new ArrayList<>();
        Map<Long, CommentRequestDTO> commentDTOHashMap = new HashMap<>();

        for (Comment comment : comments) {
            CommentRequestDTO commentRequestDTO = convertCommentTODto(comment);
            commentDTOHashMap.put(commentRequestDTO.getId(), commentRequestDTO);
            if (comment.getParent() != null) {
                commentDTOHashMap.get(comment.getParent().getId()).getChildren()
                    .add(commentRequestDTO);
            } else {
                commentRequestDTOList.add(commentRequestDTO);
            }
        }
        return commentRequestDTOList;
    }

    private CommentRequestDTO convertCommentTODto(Comment comment) {
        return new CommentRequestDTO(comment.getId(), comment.getPost().getId(),
            comment.getPost().getTitle()
            , comment.getUser().getNickname(), comment.getContent(), comment.getLikeCount(),
            comment.getCreatedAt());
    }

}