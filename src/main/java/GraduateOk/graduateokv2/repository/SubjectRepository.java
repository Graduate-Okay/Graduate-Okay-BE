package GraduateOk.graduateokv2.repository;

import GraduateOk.graduateokv2.domain.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SubjectRepository extends JpaRepository<Subject, Long> {

    @Query("select s from Subject s where " +
            "(:keyword is null or (s.name like %:keyword% or s.subName like %:keyword%)) and " +
            "(:type is null or s.kySubjectType = :type) and " +
            "(:credit is null or s.credit = :credit) and " +
            "s.isDeleted = false and s.major.name = '교양'")
    Page<Subject> getSubjectRank(@Param("keyword") String keyword,
                                 @Param("type") String type,
                                 @Param("credit") Integer credit,
                                 Pageable pageable);
}
