package com.hangout.hangout.domain.post.entity;

import com.hangout.hangout.domain.post.dto.PostRequest;
import com.hangout.hangout.global.common.domain.entity.BaseEntity;
import com.hangout.hangout.global.common.domain.entity.Map;
import com.hangout.hangout.global.common.domain.entity.Status;
import com.hangout.hangout.domain.user.entity.Gender;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Table(name = "POST_INFO")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostInfo extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POST_INFO_ID")
    private Long id;

    @OneToOne(mappedBy = "postInfo")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MAP_ID")
    private Map map;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STATUS_ID")
    private Status status;

    @Enumerated(EnumType.STRING)
    @Column(name = "TRAVEL_GENDER", nullable = false)
    private Gender travelGender; // 성별

    @Column(name = "TRAVEL_AGE", nullable = false)
    private String travelAge; // 연령대

    @Column(name = "TRAVEL_AT", nullable = false)
    private String travelAt; // 여행지

    @Column(name = "TRAVEL_MEMBER", nullable = false)
    private int travelMember; // 여행 모집 인원

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date travelDateStart; // 여행 시작 날짜

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date travelDateEnd; // 여행 종료 날짜

    @Builder
    public PostInfo(Post post, Map map, Status status,
                    Gender travelGender, String travelAge, String travelAt, int travelMember,
                    Date travelDateStart, Date travelDateEnd){
        this.post = post;
        this.map = map;
        this.status = status;
        this.travelGender = travelGender;
        this.travelAge = travelAge;
        this.travelAt = travelAt;
        this.travelMember = travelMember;
        this.travelDateStart = travelDateStart;
        this.travelDateEnd = travelDateEnd;
    }

    public void updatePostInfo(PostRequest postRequest) {
        this.travelGender = postRequest.trueGender(postRequest.getTravelGender());
        this.travelAt = postRequest.getTravelAt();
        this.travelAge = postRequest.getTravelAge();
        this.travelMember = postRequest.getTravelMember();
        this.travelDateStart = postRequest.getTravelDateStart();
        this.travelDateEnd = postRequest.getTravelDateEnd();
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
