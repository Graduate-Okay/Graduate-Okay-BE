package GraduateOk.graduateokv2.repository;

import GraduateOk.graduateokv2.domain.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SubjectRepository extends JpaRepository<Subject, Long> {

    @Query("select s from Subject s where " +
            "(:searchWord is null or (s.name like %:searchWord% or s.subName like %:searchWord%)) and " +
            "(:credit is null or s.credit = :credit) and " +
            "s.isDeleted = false and s.classification = '교양선택'")
    Page<Subject> getSubjectRank(@Param("searchWord") String searchWord,
                                 @Param("credit") Integer credit,
                                 Pageable pageable);

    @Query("select s from Subject s where s.name = :name or s.code = :code")
    Optional<Subject> findByNameOrCode(@Param("name") String name, @Param("code") String code);

    @Query("select s.name from Subject s where s.classification = '전공필수' and s.code like ':majorCode%'")
    List<String> findRequiredMajorByMajorCode(@Param("majorCode") String majorCode);

    Optional<Subject> findByName(String name);
}
