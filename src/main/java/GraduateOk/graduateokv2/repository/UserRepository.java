package GraduateOk.graduateokv2.repository;

import GraduateOk.graduateokv2.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
