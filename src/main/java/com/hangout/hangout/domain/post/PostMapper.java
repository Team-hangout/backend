package com.hangout.hangout.domain.post;

import com.hangout.hangout.domain.image.service.ImageFileUploadService;
import com.hangout.hangout.domain.post.dto.PostListResponse;
import com.hangout.hangout.domain.post.dto.PostRequest;
import com.hangout.hangout.domain.post.dto.PostResponse;
import com.hangout.hangout.domain.post.entity.Post;
import com.hangout.hangout.domain.post.entity.PostInfo;
import com.hangout.hangout.domain.post.service.PostTagService;
import com.hangout.hangout.domain.user.entity.User;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostMapper {

    private final PostTagService postTagService;
    private final ImageFileUploadService imageFileUploadService;

    public Post toEntity(PostRequest postRequest, User user) {
        return Post.builder()
            .title(postRequest.getTitle())
            .context(postRequest.getContext())
            .user(user)
            .postInfo(PostInfo.builder()
                .travelAt(postRequest.getTravelAt())
                .travelAge(postRequest.getTravelAge())
                .travelGender(postRequest.trueGender(postRequest.getTravelGender()))
                .travelDateStart(postRequest.getTravelDateStart())
                .travelDateEnd(postRequest.getTravelDateEnd())
                .travelMember(postRequest.getTravelMember())
                .build())
            .build();
    }


    public static PostResponse of(Post post, List<String> tags, List<String> imageUrls, int likeStatus) { // 유저 정보 추가 예정 // 목록 상세 조회 시 사용
        return PostResponse.builder()

                .id(post.getId())
                .title(post.getTitle())
                .context(post.getContext())
                .nickname(post.getUser().getNickname())
                .likeStatus(likeStatus)
                .tags(tags)
                .imageUrls(imageUrls)
                .statusType(post.getPostInfo().getStatus().getType())
                .travelGender(post.getPostInfo().getTravelGender())
                .travelAge(post.getPostInfo().getTravelAge())
                .travelAt(post.getPostInfo().getTravelAt())
                .travelMember(post.getPostInfo().getTravelMember())
                .travelDateStart(post.getPostInfo().getTravelDateStart())
                .travelDateEnd(post.getPostInfo().getTravelDateEnd())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .likeCount(post.getLikeCount())
                .build();
    }

    public PostListResponse toDto(Post post) { // 목록 전체 조회 시 사용하는 DTO

        List<String> tags = postTagService.getTagsByPost(post);
        List<String> imageUrls = imageFileUploadService.getImagesByPost(post);

        return PostListResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .nickname(post.getUser().getNickname())
                .tags(tags)
                .imageUrls(imageUrls)
                .statusType(post.getPostInfo().getStatus().getType())
                .travelGender(post.getPostInfo().getTravelGender())
                .travelAge(post.getPostInfo().getTravelAge())
                .travelAt(post.getPostInfo().getTravelAt())
                .travelMember(post.getPostInfo().getTravelMember())
                .travelDateStart(post.getPostInfo().getTravelDateStart())
                .travelDateEnd(post.getPostInfo().getTravelDateEnd())
                .build();
    }

    public List<PostListResponse> toDtoList(List<Post> list) {
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }
}
