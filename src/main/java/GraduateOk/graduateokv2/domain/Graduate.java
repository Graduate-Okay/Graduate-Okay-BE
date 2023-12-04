package GraduateOk.graduateokv2.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Graduate {

    int studentId; // 학번
    String studentMajor; // 주전공
    String studentDoubleMajor; // 복수전공
    String studentSubMajor; // 부전공

    boolean isDoubleMajor; // 복수전공 여부

    int totalCredit; // 총 취득 학점
    int kyCredit; // 교양 학점
    int majorCredit; // 전공 학점
    int doubleMajorCredit; // 복수 전공 학점
    int subMajorCredit; // 부전공 학점

    List<String> requiredMajor; // 전필 담는 List
    List<String> requiredKy; // 교필 담는 List
    List<String> allKy; // 모든 교양 담는 List

    int nonSubject; // 비교과 이수 학기
    int mileage; // 비교과 마일리지
    boolean engCertification; // 영어인증자 (19학번 이후 영어인증자는 "영어1,2" 면제)

    public Graduate() {
        this.studentId = 0;
        this.studentMajor = null;
        this.studentDoubleMajor = null;
        this.studentSubMajor = null;
        this.isDoubleMajor = false;
        this.totalCredit = 0;
        this.kyCredit = 0;
        this.majorCredit = 0;
        this.doubleMajorCredit = 0;
        this.subMajorCredit = 0;
        this.requiredMajor = new ArrayList<>();
        this.requiredKy = new ArrayList<>();
        this.allKy = new ArrayList<>();
        this.nonSubject = 0;
        this.mileage = 0;
        this.engCertification = false;
    }
}
