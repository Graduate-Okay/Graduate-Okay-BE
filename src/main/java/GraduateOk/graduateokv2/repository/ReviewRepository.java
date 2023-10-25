package GraduateOk.graduateokv2.repository;

import GraduateOk.graduateokv2.domain.Review;
import GraduateOk.graduateokv2.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> getReviewByUser(User user);
}
