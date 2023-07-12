package com.hangout.hangout.domain.user.service;

import com.hangout.hangout.domain.user.dto.UserProfileUpdateRequest;
import com.hangout.hangout.domain.user.entity.User;
import com.hangout.hangout.domain.user.repository.UserRepository;
import com.hangout.hangout.global.error.ResponseType;
import com.hangout.hangout.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public User getUserByEmail(String userEmail) {
        return userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new NotFoundException(ResponseType.USER_NOT_EXIST_EMAIL));
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException(ResponseType.USER_NOT_EXIST_ID));
    }

    @Transactional
    public void updateProfile(User user, UserProfileUpdateRequest request) {
        User updatedUser = User.builder()
            .id(user.getId())
            .email(user.getEmail())
            .password(user.getPassword())
            .nickname(request.getNickname() != null ? request.getNickname() : user.getNickname())
            //.image(request.getImage() != null ? request.getImage() : user.getImage())
            .description(
                request.getDescription() != null ? request.getDescription() : user.getDescription())
            .gender(request.getGender() != null ? request.getGender() : user.getGender())
            .age(request.getAge() != null ? request.getAge() : user.getAge()).build();
        userRepository.save(updatedUser);
    }
}
