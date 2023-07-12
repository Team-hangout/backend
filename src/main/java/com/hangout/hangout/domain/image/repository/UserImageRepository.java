package com.hangout.hangout.domain.image.repository;

import com.hangout.hangout.domain.image.entity.UserImage;
import com.hangout.hangout.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserImageRepository extends JpaRepository<UserImage, Long> {
    @Query("SELECT ui FROM UserImage ui WHERE ui.user = :user")
    List<UserImage> findAllByUser(User user);
}
