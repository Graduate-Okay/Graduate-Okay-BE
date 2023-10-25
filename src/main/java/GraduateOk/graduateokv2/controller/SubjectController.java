package GraduateOk.graduateokv2.controller;

import GraduateOk.graduateokv2.dto.subject.SubjectResponse;
import GraduateOk.graduateokv2.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/subject")
public class SubjectController {

    private final SubjectService subjectService;

    /**
     * NAME : 인기 교양 추천순 목록 조회
     * 과목명 검색, 인재상/핵심역량별 탭, 학점별 조회
     * DATE : 2023-10-25
     */
    @GetMapping("")
    public ResponseEntity<SubjectResponse.Rank> getSubjectRank(@RequestParam(required = false) String keyword,
                                                               @RequestParam(required = false) String type,
                                                               @RequestParam(required = false) Integer credit,
                                                               @PageableDefault(size = 30, sort = "kyCount", direction = Sort.Direction.DESC)
                                                               Pageable pageable) {

        return ResponseEntity.ok(subjectService.getSubjectRank(keyword, type, credit, pageable));
    }

    /**
     * NAME : 인기 교양 상세 조회
     * DATE : 2023-10-25
     * todo: 권한 추가
     */
    @GetMapping("/{id}")
    public ResponseEntity<SubjectResponse.Detail> getSubjectDetail(@PathVariable("id") Long id) {
        return ResponseEntity.ok(subjectService.getSubjectDetail(id));
    }
}
