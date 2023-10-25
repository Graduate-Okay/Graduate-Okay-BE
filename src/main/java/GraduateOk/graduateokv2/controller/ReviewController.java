package GraduateOk.graduateokv2.controller;

import GraduateOk.graduateokv2.dto.review.ReviewRequest;
import GraduateOk.graduateokv2.dto.review.ReviewResponse;
import GraduateOk.graduateokv2.service.ReviewService;
import lombok.RequiredArgsConstructor;
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
     * todo: 권한 추가
     */
    @PostMapping("")
    public ReviewResponse.Register registerReview(@Valid @RequestBody ReviewRequest.Register request) {
        return reviewService.registerReview(request);
    }

    /**
     * NAME : 리뷰 상세 조회
     * DATE : 2023-10-23
     * todo: 권한 추가
     */
    @GetMapping("/{id}")
    public ReviewResponse.Detail getReviewDetail(@PathVariable("id") Long id) {
        return reviewService.getReviewDetail(id);
    }

    /**
     * NAME : 리뷰 삭제
     * DATE : 2023-10-23
     * todo: 권한 추가
     */
    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable("id") Long id) {
        reviewService.deleteReview(id);
    }
}
