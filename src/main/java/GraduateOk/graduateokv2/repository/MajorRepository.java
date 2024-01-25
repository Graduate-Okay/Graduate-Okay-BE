package GraduateOk.graduateokv2.repository;

import GraduateOk.graduateokv2.domain.Major;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MajorRepository extends JpaRepository<Major, Long> {

    Optional<Major> findByName(String name);

    Optional<Major> findByNameAndYear(String name, Integer year);
}
