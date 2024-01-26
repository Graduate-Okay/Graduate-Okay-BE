package GraduateOk.graduateokv2.controller;

import GraduateOk.graduateokv2.dto.common.BaseResponse;
import GraduateOk.graduateokv2.dto.subject.SubjectRequest;
import GraduateOk.graduateokv2.dto.subject.SubjectResponse;
import GraduateOk.graduateokv2.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/subject")
public class SubjectController {

    private final SubjectService subjectService;

    /**
     * NAME : 인기 교양 추천순 목록 조회 (과목명 검색, 학점별 조회)
     * DATE : 2023-10-25
     */
    @GetMapping("")
    public BaseResponse<SubjectResponse.Rank> getSubjectRank(@RequestParam(required = false) String searchWord,
                                                             @RequestParam(required = false) Integer credit,
                                                             @PageableDefault(size = 30)
                                                             @SortDefault.SortDefaults({
                                                                     @SortDefault(sort = "kyCount", direction = Sort.Direction.DESC),
                                                                     @SortDefault(sort = "name", direction = Sort.Direction.ASC)
                                                             })
                                                             Pageable pageable) {

        return BaseResponse.ok(HttpStatus.OK, subjectService.getSubjectRank(searchWord, credit, pageable));
    }

    /**
     * NAME : 인기 교양 상세 조회
     * DATE : 2023-10-25
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public BaseResponse<SubjectResponse.Detail> getSubjectDetail(@PathVariable("id") Long id) {
        return BaseResponse.ok(HttpStatus.OK, subjectService.getSubjectDetail(id));
    }

    /**
     * NAME : 과목 데이터 DB에 저장
     * DATE : 2023-10-27
     */
    @PostMapping("/store")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<SubjectResponse.Store> storeSubjectData(@Valid @RequestBody SubjectRequest.Store request) {
        return BaseResponse.ok(HttpStatus.OK, subjectService.storeSubjectData(request));
    }

    /**
     * NAME : 교양 수강횟수 초기화
     * DATE : 2024-01-26
     */
    @PatchMapping("/reset")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> resetKyCount() {
        subjectService.resetKyCount();
        return BaseResponse.ok(HttpStatus.OK);
    }
}
