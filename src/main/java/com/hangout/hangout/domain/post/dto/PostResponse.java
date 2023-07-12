package com.hangout.hangout.domain.post.dto;

import com.hangout.hangout.domain.user.entity.Gender;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class PostResponse {

    // 게시글 특정 게시물 조회 시 반환할 목록 클래스
    // 추후 기능 만들고 추가해야 할 것 : Map, 좋아요, 신고, 조회수, 이미지
    private final Long id;
    private final String title;
    private final String nickname;
    private final String context;
    private final List<String> tags;
    private final List<String> imageUrls;
    private final int likeStatus; // 좋아요가 되어있는 지 확인 // 좋아요 = 1 , 아님 = 0
    private final String statusType;
    private final Gender travelGender;
    private final String travelAge;
    private final String travelAt;
    private final int travelMember;
    private final Date travelDateStart;
    private final Date travelDateEnd;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final int likeCount;

}
