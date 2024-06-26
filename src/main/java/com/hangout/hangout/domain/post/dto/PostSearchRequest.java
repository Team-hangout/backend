package com.hangout.hangout.domain.post.dto;

import com.hangout.hangout.global.common.domain.entity.SearchType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@AllArgsConstructor
@ToString
public class PostSearchRequest {

    private SearchType searchType;
    private String searchKeyword1;
    private String searchKeyword2; // stateAndCity 타입일 경우만 사용
}
