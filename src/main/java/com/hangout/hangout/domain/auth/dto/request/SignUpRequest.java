package com.hangout.hangout.domain.auth.dto.request;

import com.hangout.hangout.domain.user.entity.Gender;
import com.hangout.hangout.domain.user.entity.Role;
import com.hangout.hangout.domain.user.entity.User;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.crypto.password.PasswordEncoder;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SignUpRequest {

    @Email(message = "이메일 형식이 아닙니다.")
    @NotBlank(message = "이메일은 필수 항목입니다.")
    private String email;

    @NotBlank(message = "패스워드는 필수 항목입니다.")
    private String password;

    @NotBlank(message = "닉네임은 필수 항목입니다.")
    @Length(max = 10, message = "닉네임은 최대 10자를 넘을 수 없습니다.")
    private String nickname;

    @NotNull(message = "성별은 필수 항목입니다.")
    private Gender gender;

    @NotNull(message = "나이는 필수 항목입니다.")
    private int age;


    @Length(max = 100, message = "소개글은 최대 10자를 넘을 수 없습니다.")
    private String description;
    private Role role = Role.USER;

    public User toEntity(PasswordEncoder passwordEncoder) {
        return User.builder()
            .email(email)
            .nickname(nickname)
            .password(passwordEncoder.encode(password))
            .gender(gender)
            .age(age)
            .role(Role.USER)
            .description(description)
            .build();
    }
}
