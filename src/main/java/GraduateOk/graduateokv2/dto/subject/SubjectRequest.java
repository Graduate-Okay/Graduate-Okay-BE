package GraduateOk.graduateokv2.dto.subject;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class SubjectRequest {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Store {

        List<Data> dataList;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Data {

        String ESTB_SBJT_CD;

        String major; // ESTB_SBJT_NM 학과 ***

        String SHYR; // 연도

        String SMST_GBCD; // 학기

        String courseCode; // COURSE_CD 과목코드 (ex - KY021) ***

        String courseName; // COURSE_NM 과목명 ***

        String CLAS; // 분반

        String classification; // COPL_GBNM 이수구분 (전필,전선,교필,교선) ***

        String LISTAGG_GRADE; // 학년...?

        String credit; // PNT 학점 ***

        String LISTAGG_TEHIN; // 강의 시간

        String LISTAGG_ROOM; // 강의실

        String LISTAGG_PESN_GEND_NM; // 교수명

        String PESN_NO; // 교수 번호

        String INWON; // 인원

        String NOTE; // 비고

        String kyType; // MMDIST_GBNM 인재상 ***

        String PLAN_YN;

        String BUID_NM; // 건물명

        String CYBER_GBCD; // 싸강 구분 코드
    }
}
