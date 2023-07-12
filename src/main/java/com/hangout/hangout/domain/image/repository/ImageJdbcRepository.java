package com.hangout.hangout.domain.image.repository;

import com.hangout.hangout.domain.image.entity.PostImage;
import com.hangout.hangout.domain.image.entity.UserImage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ImageJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ImageJdbcRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    private static final String POST_BULK_INSERT_SQL = "INSERT INTO " +
            "`postimage`(`post_image_name` , `post_image_url`, `created_At`, `updated_At`, `post_id`, `is_removed`) " +
            "VALUES(?, ?, ?, ?, ?, ?)";

    private static final String USER_BULK_INSERT_SQL = "INSERT INTO " +
            "`userimage`(`user_image_name` , `user_image_url`, `created_At`, `updated_At`, `user_id`, `is_removed`) " +
            "VALUES(?, ?, ?, ?, ?, ?)";

    public void saveAllPostImage(List<PostImage> postImages) {
        jdbcTemplate.batchUpdate(POST_BULK_INSERT_SQL,
                postImages,
                postImages.size(),
                (ps, postImage) -> {
                    LocalDateTime now = LocalDateTime.now();
                    ps.setString(1, postImage.getName());
                    ps.setString(2, postImage.getUrl());
                    ps.setTimestamp(3, Timestamp.valueOf(now));
                    ps.setTimestamp(4, Timestamp.valueOf(now));
                    ps.setLong(5, postImage.getPost().getId());
                    ps.setBoolean(6, postImage.isRemoved());
                });
    }

    public void saveAllUserImage(List<UserImage> userImages) {
        jdbcTemplate.batchUpdate(USER_BULK_INSERT_SQL,
                userImages,
                userImages.size(),
                (us, userImage) -> {
                    LocalDateTime now = LocalDateTime.now();
                    us.setString(1, userImage.getName());
                    us.setString(2, userImage.getUrl());
                    us.setTimestamp(3, Timestamp.valueOf(now));
                    us.setTimestamp(4, Timestamp.valueOf(now));
                    us.setLong(5, userImage.getUser().getId());
                    us.setBoolean(6, userImage.isRemoved());
                });
    }
}

