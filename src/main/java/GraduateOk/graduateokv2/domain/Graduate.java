package GraduateOk.graduateokv2.domain;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Graduate {

    int studentId; // 학번

    String studentMajor; // 주전공

    String studentDoubleMajor; // 복수전공

    String studentSubMajor; // 부전공

    @Builder.Default
    boolean isDoubleMajor = false; // 복수전공 여부

    @Builder.Default
    int totalCredit = 0; // 총 취득 학점

    @Builder.Default
    int kyCredit = 0; // 교양 학점

    @Builder.Default
    int majorCredit = 0; // 전공 학점

    @Builder.Default
    int doubleMajorCredit = 0; // 복수 전공 학점

    @Builder.Default
    int subMajorCredit = 0; // 부전공 학점

    @Builder.Default
    int nonSubject = 0; // 비교과 이수 학기

    @Builder.Default
    int mileage = 0; // 비교과 마일리지

    @Builder.Default
    boolean engCertification = false; // 영어인증자 (19학번 이후 영어인증자는 "영어1,2" 면제)

    List<String> requiredMajorList; // 전필 담는 List

    List<String> requiredKyList; // 교필 담는 List

    List<String> allKyList; // 모든 교양 담는 List

    public String toString() {
        return "학번 : " + studentId + "\n" +
                "주전공 : " + studentMajor + " \n" +
                "복수전공 : " + studentDoubleMajor + "\n" +
                "부전공 : " + studentSubMajor + "\n" +
                "복수전공 여부 : " + isDoubleMajor + "\n\n" +
                "총 취득 학점 : " + totalCredit + "\n" +
                "교양 학점 : " + kyCredit + "\n" +
                "전공 학점 : " + majorCredit + "\n" +
                "복수 전공 학점 : " + doubleMajorCredit + "\n" +
                "부전공 학점 : " + subMajorCredit + "\n\n" +
                "비교과 이수 학기 : " + nonSubject + "\n" +
                "비교과 마일리지 : " + mileage + "\n" +
                "영어인증자 : " + engCertification + "\n" +
                "전필 리스트 : " + requiredMajorList.toString() + "\n\n" +
                "교필 리스트 : " + requiredKyList.toString() + "\n" +
                "전체 교양 리스트 : " + allKyList.toString();
    }
}
