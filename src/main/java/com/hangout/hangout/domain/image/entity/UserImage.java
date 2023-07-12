package com.hangout.hangout.domain.image.entity;

import com.hangout.hangout.domain.user.entity.User;
import com.hangout.hangout.global.common.domain.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserImage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_IMAGE_ID")
    private Long id;
    @Column(name = "USER_IMAGE_NAME")
    private String name;
    @Column(name = "USER_IMAGE_URL")
    private String url;
    @Column(name = "IS_REMOVED")
    private boolean removed;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @Builder
    public UserImage(String name, String url, User user) {
        this.name = name;
        this.url = url;
        this.user = user;
    }
}
