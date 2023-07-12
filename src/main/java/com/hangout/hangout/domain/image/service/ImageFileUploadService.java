package com.hangout.hangout.domain.image.service;

import com.hangout.hangout.domain.image.entity.PostImage;
import com.hangout.hangout.domain.image.repository.ImageJdbcRepository;
import com.hangout.hangout.domain.image.repository.PostImageRepository;
import com.hangout.hangout.domain.image.util.FileUtils;
import com.hangout.hangout.domain.post.entity.Post;
import com.hangout.hangout.domain.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageFileUploadService {
    private final AwsS3Service awsS3Service;
    private final PostService postService;
    private final ImageJdbcRepository imageJdbcRepository;
    private final PostImageRepository postImageRepository;

    @Transactional
    public void upload(Long postId, List<MultipartFile> files) throws IOException {
        Post post = postService.findPostById(postId);
        upload(files, post);
    }

    private void upload(List<MultipartFile> files, Post post) throws IOException {
        List<PostImage> postImages = uploadImageToStorageServer(files, post);
        imageJdbcRepository.saveAllPostImage(postImages);
    }

    private List<PostImage> uploadImageToStorageServer(List<MultipartFile> files, Post post) throws IOException {
        List<PostImage> postImages = new ArrayList<>();

        for(MultipartFile file : files) {
            String filename = FileUtils.getRandomFilename();
            String filepath = awsS3Service.upload(file, filename);
            postImages.add(PostImage.builder()
                    .name(filename)
                    .url(filepath)
                    .post(post)
                    .build());
        }

        return postImages;
    }

    public List<PostImage> findImageListByPost(Post post) {
        return postImageRepository.findAllByPost(post);
    }

    public List<String> getImagesByPost(Post post) {
        List<PostImage> postImages = findImageListByPost(post);
        List<String> imagesUrls = new ArrayList<>();
        for (PostImage postImage : postImages) {
            imagesUrls.add(postImage.getUrl());
        }
        return imagesUrls;
    }

}
