package com.hangout.hangout.domain.image.service;

import com.hangout.hangout.domain.image.entity.UserImage;
import com.hangout.hangout.domain.image.repository.ImageJdbcRepository;
import com.hangout.hangout.domain.image.repository.UserImageRepository;
import com.hangout.hangout.domain.image.util.FileUtils;

import com.hangout.hangout.domain.user.entity.User;
import com.hangout.hangout.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserImageFileUploadService {
    private final AwsS3Service awsS3Service;
    private final UserService userService;
    private final ImageJdbcRepository imageJdbcRepository;
    private final UserImageRepository userImageRepository;

    @Transactional
    public void upload(Long userId, List<MultipartFile> files) throws IOException {
        User user = userService.getUserById(userId);
        upload(files, user);
    }

    private void upload(List<MultipartFile> files, User user) throws IOException {
        List<UserImage> userImages = uploadImageToStorageServer(files, user);
        imageJdbcRepository.saveAllUserImage(userImages);
    }

    private List<UserImage> uploadImageToStorageServer(List<MultipartFile> files, User user) throws IOException {
        List<UserImage> userImages = new ArrayList<>();

        for(MultipartFile file : files) {
            String filename = FileUtils.getRandomFilename();
            String filepath = awsS3Service.upload(file, filename);
            userImages.add(UserImage.builder()
                    .name(filename)
                    .url(filepath)
                    .user(user)
                    .build());
        }

        return userImages;
    }

    public List<UserImage> findImageListByUser(User user) {
        return userImageRepository.findAllByUser(user);
    }

    public List<String> getImagesByUser(User user) {
        List<UserImage> userImages = findImageListByUser(user);
        List<String> imagesUrls = new ArrayList<>();
        for (UserImage userImage : userImages) {
            imagesUrls.add(userImage.getUrl());
        }
        return imagesUrls;
    }

}
