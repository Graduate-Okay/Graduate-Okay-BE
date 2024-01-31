package GraduateOk.graduateokv2.controller;

import GraduateOk.graduateokv2.dto.common.BaseResponse;
import GraduateOk.graduateokv2.dto.review.ReviewRequest;
import GraduateOk.graduateokv2.dto.review.ReviewResponse;
import GraduateOk.graduateokv2.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/review")
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * NAME : 리뷰 작성
     * DATE : 2023-10-23
     */
    @PostMapping("")
    @PreAuthorize("hasRole('USER')")
    public BaseResponse<ReviewResponse.Register> registerReview(@Valid @RequestBody ReviewRequest.Register request) {
        return BaseResponse.ok(HttpStatus.OK, reviewService.registerReview(request));
    }

    /**
     * NAME : 리뷰 상세 조회
     * DATE : 2023-10-23
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public BaseResponse<ReviewResponse.Detail> getReviewDetail(@PathVariable("id") Long id) {
        return BaseResponse.ok(HttpStatus.OK, reviewService.getReviewDetail(id));
    }

    /**
     * NAME : 리뷰 삭제 (사용자)
     * DATE : 2023-10-23
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public BaseResponse<?> deleteReview(@PathVariable("id") Long id) {
        reviewService.deleteReview(id);
        return BaseResponse.ok(HttpStatus.OK);
    }

    /**
     * NAME : 리뷰 다건 삭제 (관리자)
     * DATE : 2024-01-31
     */
    @DeleteMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> deleteReviews(@Valid @RequestBody ReviewRequest.Delete request) {
        reviewService.deleteReviews(request);
        return BaseResponse.ok(HttpStatus.OK);
    }
}
