package GraduateOk.graduateokv2.repository;

import GraduateOk.graduateokv2.domain.Major;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MajorRepository extends JpaRepository<Major, Long> {

    Optional<Major> findByName(String name);

    @Query("select m from Major m where m.name like %:name% and m.year = :year")
    Optional<Major> findByNameAndYear(@Param("name") String name, @Param("year") Integer year);
}
