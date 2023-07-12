package com.hangout.hangout.domain.user.dto;

import com.hangout.hangout.domain.user.entity.Gender;
import com.hangout.hangout.domain.user.entity.Role;
import com.hangout.hangout.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class UserResponse {

    private Long id;
    private String email;
    private String nickname;
    private String description;
    private List<String> imageUrls;
    private Gender gender;
    private int age;
    private Role role;

    public static UserResponse of(User user, List<String> imageUrls) {
        return new UserResponse(
            user.getId(),
            user.getEmail(),
            user.getNickname(),
            user.getDescription(),
            imageUrls,
            user.getGender(),
            user.getAge(),
            user.getRole()
        );
    }

}
