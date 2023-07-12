package com.hangout.hangout.domain.user.entity;

import com.hangout.hangout.domain.auth.entity.oauth2.OAuth2UserInfo;
import com.hangout.hangout.global.common.domain.entity.BaseEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Table(name = "USER")
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long id;

    @Column(nullable = false, unique = true)
    private String email; // 이메일(ID)

    @Column(nullable = false)
    private String password; // 비밀번호

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false, unique = true)
    private String nickname;


    private String description;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private int age;

    @Column(unique = true)
    private String oAuth2Id;    // Resource Server에서 넘겨주는 소셜 로그인 플랫폼 식별

    public User update(OAuth2UserInfo oAuth2UserInfo) {
        this.oAuth2Id = oAuth2UserInfo.getOAuth2Id();
        return this;
    }

}
