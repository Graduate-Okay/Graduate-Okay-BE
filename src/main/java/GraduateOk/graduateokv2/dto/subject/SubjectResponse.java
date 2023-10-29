package GraduateOk.graduateokv2.dto.subject;

import GraduateOk.graduateokv2.domain.Subject;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class SubjectResponse {

    @Getter
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Brief {

        Long subjectId;

        String name; // 과목명

        String subName;

        Boolean isRequired;

        Float credit; // 학점

        String kyModelType; // 교양 인재상

        String kyCoreType; // 교양 핵심역량

        Integer kyCount; // 수강횟수

        public static Brief of(Subject subject) {
            return Brief.builder()
                    .subjectId(subject.getId())
                    .name(subject.getName())
                    .subName(subject.getSubName())
                    .isRequired(subject.getIsRequired())
                    .credit(subject.getCredit())
                    .kyModelType(subject.getKyModelType() == null ? null : subject.getKyModelType().getDescription())
                    .kyCoreType(subject.getKyCoreType() == null ? null : subject.getKyCoreType().getDescription())
                    .kyCount(subject.getKyCount())
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Rank {

        long totalCount;

        long maxPageCount;

        List<Brief> subjectList;

        public static SubjectResponse.Rank of(Page<Subject> subjectList) {
            return Rank.builder()
                    .totalCount(subjectList.getTotalElements())
                    .maxPageCount(subjectList.getTotalPages())
                    .subjectList(subjectList.getContent().stream().map(Brief::of).collect(Collectors.toList()))
                    .build();
        }
    }

    @Getter
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Detail extends Brief {

        ReviewSummary reviewSummary; // 리뷰 정보

        public static SubjectResponse.Detail of(Subject subject, ReviewSummary reviewSummary) {
            return Detail.builder()
                    .subjectId(subject.getId())
                    .name(subject.getName())
                    .subName(subject.getSubName())
                    .isRequired(subject.getIsRequired())
                    .credit(subject.getCredit())
                    .kyModelType(subject.getKyModelType().getDescription())
                    .kyCoreType(subject.getKyCoreType().getDescription())
                    .kyCount(subject.getKyCount())
                    .reviewSummary(reviewSummary)
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class ReviewSummary {

        List<Long> reviewIdList;

        Integer totalCount; // 총 리뷰 개수

        Double avgStarScore; // 리뷰 평점
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Store {

        Integer totalCount; // 총 저장 개수
    }
}
