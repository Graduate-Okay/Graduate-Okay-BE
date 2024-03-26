package GraduateOk.graduateokv2.repository;

import GraduateOk.graduateokv2.domain.Record;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordRepository extends JpaRepository<Record, Long> {
}
