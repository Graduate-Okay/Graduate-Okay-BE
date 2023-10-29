package GraduateOk.graduateokv2.service;

import GraduateOk.graduateokv2.domain.Notice;
import GraduateOk.graduateokv2.dto.notice.NoticeRequest;
import GraduateOk.graduateokv2.dto.notice.NoticeResponse;
import GraduateOk.graduateokv2.exception.CustomException;
import GraduateOk.graduateokv2.exception.Error;
import GraduateOk.graduateokv2.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@Service
public class NoticeService {

    private final NoticeRepository noticeRepository;

    /**
     * 공지사항 목록 조회
     */
    @Transactional(readOnly = true)
    public NoticeResponse.InfoList getNoticeList(Pageable pageable) {
        return NoticeResponse.InfoList.of(noticeRepository.findAll(pageable));
    }

    /**
     * 공지사항 상세 조회
     */
    @Transactional(readOnly = true)
    public NoticeResponse.Brief getNoticeDetail(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new CustomException(Error.NOT_FOUND_NOTICE));

        return NoticeResponse.Brief.builder()
                .id(id)
                .title(notice.getTitle())
                .content(notice.getContent())
                .createdAt(notice.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .build();
    }

    /**
     * 공지사항 작성 (관리자)
     */
    @Transactional
    public NoticeResponse.Register registerNotice(NoticeRequest.Register request) {
        Notice notice = Notice.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .build();
        noticeRepository.save(notice);

        return NoticeResponse.Register.builder().id(notice.getId()).build();
    }

    /**
     * 공지사항 수정 (관리자)
     */
    @Transactional
    public NoticeResponse.Brief updateNotice(Long id, NoticeRequest.Update request) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new CustomException(Error.NOT_FOUND_NOTICE));

        if (request.getTitle() != null) notice.changeTitle(request.getTitle());
        if (request.getContent() != null) notice.changeContent(request.getContent());

        return NoticeResponse.Brief.of(notice);
    }

    /**
     * 공지사항 삭제 (관리자)
     */
    @Transactional
    public void deleteNotice(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new CustomException(Error.NOT_FOUND_NOTICE));

        noticeRepository.delete(notice);
    }
}
