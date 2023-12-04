package GraduateOk.graduateokv2.dto.graduate;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GraduateResponseDto {

    Boolean isGraduateOk; // 졸업 가능 여부

    Integer totalCredit; // 총 취득 학점

    Integer kyCredit; // 총 교양 학점

    Integer majorCredit; // 주전공 학점

    Integer doubleMajorCredit; // 복수전공 학점 (복전 아닐 경우 0)

    Integer nonSubject; // 비교과 이수 학기

    Integer mileage; // 마일리지

    String failure; // 부족한 졸업요건 (없으면 null)
}
