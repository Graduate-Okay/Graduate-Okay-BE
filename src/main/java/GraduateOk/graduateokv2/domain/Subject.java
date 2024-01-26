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
public class Subject extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name; // 과목 이름

    @Setter
    String subName; // 교양 과목명 변경된 경우 이전 과목명 저장

    String code; // 과목코드

    String classification; // 이수 구분

    Boolean isRequired; // 필수 여부 (교필, 전필)

    Float credit; // 학점

    @Enumerated(EnumType.STRING)
    SubjectModelType kyModelType; // 교양 인재상

    @Enumerated(EnumType.STRING)
    SubjectCoreType kyCoreType; // 교양 핵심역량

    @Builder.Default
    Integer kyCount = 0; // 교양 수강횟수

    @Builder.Default
    Boolean isDeleted = false; // 삭제 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "major_id")
    Major major;

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    List<Review> reviewList = new ArrayList<>();

    public void increaseKyCount() {
        this.kyCount++;
    }
}
