package GraduateOk.graduateokv2.domain;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Major {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "college_id")
    College college;

    String name; // 학과 이름

    String code; // 학과 코드

    Integer year; // 연도 (연도별로 학점 조건이 다름)

    Integer minCredit; // 전공 최소 이수 학점

    Integer graduateCredit; // 전공 졸업 이수 학점

    @OneToMany(mappedBy = "major", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Subject> subjectList = new ArrayList<>(); // 전필 목록
}
