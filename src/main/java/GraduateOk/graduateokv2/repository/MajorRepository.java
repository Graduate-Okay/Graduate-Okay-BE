package GraduateOk.graduateokv2.repository;

import GraduateOk.graduateokv2.domain.Major;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MajorRepository extends JpaRepository<Major, Long> {

    Optional<Major> findByName(String name);

    @Query("select m from Major m " +
            "where (m.name = :name or m.name like :name%) and m.year = :year " +
            "order by case when m.name = :name then 1 else 2 end")
    List<Major> findByNameAndYear(@Param("name") String name, @Param("year") Integer year);

    @Query("select m from Major m " +
            "where m.name like :name% and m.year >= :year " +
            "order by m.year asc")
    List<Major> findDoubleMajor(@Param("name") String name, @Param("year") Integer year);
}
