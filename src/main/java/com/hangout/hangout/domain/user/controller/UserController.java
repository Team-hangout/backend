package com.hangout.hangout.domain.user.controller;

import static com.hangout.hangout.global.common.domain.entity.Constants.API_PREFIX;

import com.hangout.hangout.domain.image.service.UserImageFileUploadService;
import com.hangout.hangout.domain.user.dto.UserProfileUpdateRequest;
import com.hangout.hangout.domain.user.dto.UserResponse;
import com.hangout.hangout.domain.user.entity.User;
import com.hangout.hangout.domain.user.service.UserService;
import com.hangout.hangout.global.error.ResponseEntity;
import com.hangout.hangout.global.security.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(API_PREFIX + "/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserImageFileUploadService userImageFileUploadService;

    @GetMapping("/me")
    @ApiResponse(responseCode = "200", description = "OK", content = {
        @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))})
    @Operation(summary = "현재 유저의 정보 조회", tags = {"User Controller"})
    public ResponseEntity<UserResponse> getCurrentUser(@CurrentUser User user) {
        List<String> imagesByUser = userImageFileUploadService.getImagesByUser(user);
        return ResponseEntity.successResponse(UserResponse.of(user, imagesByUser));
    }

    /*
    todo - 유저의 히스토리 조회
     */

    @PutMapping("/me")
    @Operation(summary = "프로필 수정", tags = {"User Controller"})
    public ResponseEntity<UserResponse> updateProfile(@CurrentUser User user,
        @RequestBody @Valid UserProfileUpdateRequest request) {
        userService.updateProfile(user, request);
        return ResponseEntity.successResponse();
    }

    @GetMapping("/{id}")
    @Operation(summary = "유저 정보 조회", description = "유저 id에 따른 유저 정보 조회(상대방 프로필 조회 시)", tags = {
        "User Controller"})
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        User userById = userService.getUserById(id);
        List<String> imagesByUser = userImageFileUploadService.getImagesByUser(userById);
        return ResponseEntity.successResponse(UserResponse.of(userById, imagesByUser));
    }

}
