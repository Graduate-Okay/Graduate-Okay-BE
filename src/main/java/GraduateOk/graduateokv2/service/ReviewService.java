package GraduateOk.graduateokv2.service;

import GraduateOk.graduateokv2.domain.Review;
import GraduateOk.graduateokv2.domain.Subject;
import GraduateOk.graduateokv2.domain.User;
import GraduateOk.graduateokv2.dto.review.ReviewRequest;
import GraduateOk.graduateokv2.dto.review.ReviewResponse;
import GraduateOk.graduateokv2.exception.CustomException;
import GraduateOk.graduateokv2.exception.Error;
import GraduateOk.graduateokv2.repository.ReviewRepository;
import GraduateOk.graduateokv2.repository.SubjectRepository;
import GraduateOk.graduateokv2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final LoginService loginService;

    @Transactional
    public ReviewResponse.Register registerReview(ReviewRequest.Register request) {
        Long userId = loginService.getLoginUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(Error.NOT_FOUND_USER));

        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new CustomException(Error.NOT_FOUND_SUBJECT));

        Review review = Review.builder()
                .user(user)
                .subject(subject)
                .title(request.getTitle())
                .content(request.getContent())
                .starScore(request.getStarScore())
                .isDeleted(false)
                .build();
        reviewRepository.save(review);

        return ReviewResponse.Register.builder().id(review.getId()).build();
    }

    @Transactional(readOnly = true)
    public ReviewResponse.Detail getReviewDetail(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new CustomException(Error.NOT_FOUND_REVIEW));

        return ReviewResponse.Detail.of(review);
    }

    @Transactional
    public void deleteReview(Long id) {
        Long userId = loginService.getLoginUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(Error.NOT_FOUND_USER));

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new CustomException(Error.NOT_FOUND_REVIEW));

        // 본인 작성 글 확인
        if (review.getUser().equals(user)) {
            review.deleteReview();
        } else {
            throw new CustomException(Error.FORBIDDEN);
        }
    }
}
