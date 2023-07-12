package com.hangout.hangout.domain.report.controller;

import static com.hangout.hangout.global.common.domain.entity.Constants.API_PREFIX;

import com.hangout.hangout.domain.report.dto.CommentReportRequest;
import com.hangout.hangout.domain.report.dto.CommentReportResponse;
import com.hangout.hangout.domain.report.dto.PostReportRequest;
import com.hangout.hangout.domain.report.dto.PostReportResponse;
import com.hangout.hangout.domain.report.entity.CommentReport;
import com.hangout.hangout.domain.report.entity.PostReport;
import com.hangout.hangout.domain.report.service.ReportService;
import com.hangout.hangout.domain.user.entity.User;
import com.hangout.hangout.global.error.ResponseEntity;
import com.hangout.hangout.global.security.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(API_PREFIX + "/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "댓글 신고 생성")
    @PostMapping("/comment")
    public ResponseEntity<String> createCommentReport(@CurrentUser User user,
        @RequestBody CommentReportRequest commentReportRequest) {
        Long reportId = reportService.createCommentReport(user, commentReportRequest);
        return ResponseEntity.successResponse("댓글 신고가 접수되었습니다. Report ID: " + reportId);
    }

    @Operation(summary = "게시글 신고 생성")
    @PostMapping("/post")
    public ResponseEntity<String> createPostReport(@CurrentUser User user,
        @RequestBody PostReportRequest postReportRequest) {
        Long reportId = reportService.createPostReport(postReportRequest, user);
        return ResponseEntity.successResponse("게시글 신고가 접수되었습니다. Report ID: " + reportId);
    }

    @Operation(summary = "댓글 신고 단건 조회")
    @ApiResponse(responseCode = "200", description = "Found the comment report", content = {
        @Content(mediaType = "application/json", schema = @Schema(implementation = CommentReport.class))
    })
    @GetMapping("/comment/{id}")
    public ResponseEntity<CommentReportResponse> getCommentReport(@PathVariable("id") Long id) {
        CommentReportResponse commentReport = reportService.getCommentReport(id);
        return ResponseEntity.successResponse("댓글 신고 조회에 성공하셨습니다. Report ID: " + id, commentReport);
    }

    @Operation(summary = "게시글 신고 단건 조회")
    @ApiResponse(responseCode = "200", description = "Found the post report", content = {
        @Content(mediaType = "application/json", schema = @Schema(implementation = PostReport.class))
    })
    @GetMapping("/post/{id}")
    public ResponseEntity<PostReportResponse> getPostReport(@PathVariable("id") Long id) {
        PostReportResponse postReport = reportService.getPostReport(id);
        return ResponseEntity.successResponse("게시글 신고 조회에 성공하셨습니다. Report ID: " + id, postReport);
    }

    @Operation(summary = "댓글 신고 리스트 조회")
    @ApiResponse(responseCode = "200", description = "댓글 신고 전체 조회", content = {
        @Content(mediaType = "application/json", schema = @Schema(implementation = CommentReport.class))
    })
    @GetMapping("/comments")
    public ResponseEntity<List<CommentReportResponse>> getCommentReportList() {
        return ResponseEntity.successResponse(reportService.getCommentReportList());
    }

    @Operation(summary = "게시글 신고 리스트 조회")
    @ApiResponse(responseCode = "200", description = "게시글 신고 전체 조회", content = {
        @Content(mediaType = "application/json", schema = @Schema(implementation = PostReport.class))
    })
    @GetMapping("/posts")
    public ResponseEntity<List<PostReportResponse>> getPostReportList() {
        return ResponseEntity.successResponse(reportService.getPostReportList());

    }
}
