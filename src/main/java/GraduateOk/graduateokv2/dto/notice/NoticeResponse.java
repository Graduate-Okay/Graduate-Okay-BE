package GraduateOk.graduateokv2.dto.notice;

import GraduateOk.graduateokv2.domain.Notice;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Page;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class NoticeResponse {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Brief {

        Long id;

        String title;

        String content;

        String createdAt;

        public static List<Brief> of(List<Notice> noticeList) {
            return noticeList.stream()
                    .map(Brief::of)
                    .collect(Collectors.toList());
        }

        public static Brief of(Notice notice) {
            return Brief.builder()
                    .id(notice.getId())
                    .title(notice.getTitle())
                    .content(notice.getContent())
                    .createdAt(notice.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class InfoList {

        long totalCount;

        long maxPageCount;

        List<Brief> noticeList;

        public static InfoList of(Page<Notice> noticePage) {
            return InfoList.builder()
                    .totalCount(noticePage.getTotalElements())
                    .maxPageCount(noticePage.getTotalPages())
                    .noticeList(Brief.of(noticePage.getContent()))
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Register {

        Long id;
    }
}
