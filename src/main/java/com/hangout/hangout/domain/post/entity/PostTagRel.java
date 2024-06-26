package com.hangout.hangout.domain.post.entity;

import com.hangout.hangout.global.common.domain.entity.BaseEntity;
import com.hangout.hangout.global.common.domain.entity.Tag;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Table(name = "POST_TAG_REL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostTagRel extends BaseEntity { // 게시물과 태그의 many to many로 인한 테이블
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POST_TAG_REL_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "POST_ID")
    @Setter
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "TAG_ID")
    @Setter
    private Tag tag;

    @Builder
    public PostTagRel(Long id, Post post, Tag tag) {
        this.id= id;
        this.post = post;
        this.tag = tag;
    }
}
