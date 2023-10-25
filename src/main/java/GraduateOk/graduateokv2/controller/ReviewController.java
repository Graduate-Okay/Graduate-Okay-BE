package GraduateOk.graduateokv2.controller;

import GraduateOk.graduateokv2.dto.review.ReviewRequest;
import GraduateOk.graduateokv2.dto.review.ReviewResponse;
import GraduateOk.graduateokv2.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ReviewResponse.Register> registerReview(@Valid @RequestBody ReviewRequest.Register request) {
        return ResponseEntity.ok(reviewService.registerReview(request));
    }

    /**
     * NAME : 리뷰 상세 조회
     * DATE : 2023-10-23
     * todo: 권한 추가
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponse.Detail> getReviewDetail(@PathVariable("id") Long id) {
        return ResponseEntity.ok(reviewService.getReviewDetail(id));
    }

    /**
     * NAME : 리뷰 삭제
     * DATE : 2023-10-23
     * todo: 권한 추가
     * todo: response 수정
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReview(@PathVariable("id") Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.ok(id + "번 리뷰 삭제 완료");
    }
}
