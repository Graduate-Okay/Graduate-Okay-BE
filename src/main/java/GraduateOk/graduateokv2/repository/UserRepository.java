package GraduateOk.graduateokv2.repository;

import GraduateOk.graduateokv2.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByJwt(String jwt);
}
