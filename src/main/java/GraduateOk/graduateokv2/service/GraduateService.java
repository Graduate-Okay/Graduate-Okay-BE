package GraduateOk.graduateokv2.service;

import GraduateOk.graduateokv2.domain.Graduate;
import GraduateOk.graduateokv2.domain.Major;
import GraduateOk.graduateokv2.domain.Record;
import GraduateOk.graduateokv2.domain.User;
import GraduateOk.graduateokv2.dto.graduate.GraduateResponseDto;
import GraduateOk.graduateokv2.exception.CustomException;
import GraduateOk.graduateokv2.exception.Error;
import GraduateOk.graduateokv2.repository.MajorRepository;
import GraduateOk.graduateokv2.repository.RecordRepository;
import GraduateOk.graduateokv2.repository.SubjectRepository;
import GraduateOk.graduateokv2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class GraduateService {

    private final UserRepository userRepository;
    private final MajorRepository majorRepository;
    private final SubjectRepository subjectRepository;
    private final RecordRepository recordRepository;

    private final LoginService loginService;

    @Transactional
    public GraduateResponseDto isGraduateOk(MultipartFile multipartFile) {
        User user = getUser();

        String pdf = extractPdfContent(multipartFile);
        log.info("=== PDF EXTRACT TEXT ===\n" + pdf);
        validatePdf(pdf);

        Graduate graduate = extractBasicInfo(pdf);
        log.info("=== GRADUATE EXTRACT BASIC INFO ===\n" + graduate.toString());

        String failure = checkAndGetFailure(graduate);
        log.info("=== FAILURE ===\n" + failure);

        if (!user.getIsChecked()) {
            countKy(graduate.getAllKyList());

            increaseCount((long) graduate.getStudentId());
            log.info("[increase count]");

            setUserIsChecked(user);
        }

        return GraduateResponseDto.of(graduate, failure);
    }

    /**
     * 사용자 조회
     */
    private User getUser() {
        Long userId = loginService.getLoginUserId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(Error.NOT_FOUND_USER));
    }

    /**
     * PDF 파일 내용 추출
     */
    public String extractPdfContent(MultipartFile multipartFile) {
        String extractText;
        String contentType = multipartFile.getContentType();

        if (StringUtils.hasText(contentType) && !contentType.equals("application/pdf")) {
            throw new CustomException(Error.INCORRECT_FILE_TYPE);
        }

        // 임시 폴더 생성
        File tempFolder = new File("temp");
        if (!tempFolder.exists()) {
            if (!tempFolder.mkdirs()) {
                throw new CustomException(Error.CANNOT_CREATE_TEMPORARY_FOLDER);
            }
        }

        // MultipartFile to File
        File file = new File(tempFolder + "/" + multipartFile.getOriginalFilename());

        try {
            if (file.createNewFile()) {
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(multipartFile.getBytes());
                }
            }

            // 텍스트 추출
            PDDocument pdfDoc = PDDocument.load(file);
            extractText = new PDFTextStripper().getText(pdfDoc);
        } catch (IOException e) {
            throw new CustomException(Error.CANNOT_READ_PDF);
        }

        // 파일 삭제
        File[] files = tempFolder.listFiles();
        if (files != null) {
            for (File f : files) {
                if (!f.delete()) throw new CustomException(Error.CANNOT_DELETE_TEMPORARY_FOLDER);
            }
            if (!tempFolder.delete()) throw new CustomException(Error.CANNOT_DELETE_TEMPORARY_FOLDER);
        } else {
            throw new CustomException(Error.CANNOT_DELETE_TEMPORARY_FOLDER);
        }

        return extractText;
    }

    /**
     * PDF 검사
     */
    private void validatePdf(String pdf) {
        if (pdf.isEmpty()) {
            throw new CustomException(Error.NOT_FOUND_PDF_CONTENT);
        }

        // 한신대학교 학업성적확인서 PDF인지 검사
        if (!pdf.contains("포털>한신종합정보>성적")) {
            throw new CustomException(Error.BAD_PDF);
        }
    }

    /**
     * 기본 정보 추출
     */
    private Graduate extractBasicInfo(String pdf) {

        int studentId = 0; // 학번
        String studentMajor = null; // 주전공
        String studentDoubleMajor = null; // 복수전공
        String studentSubMajor = null; // 부전공

        double totalCredit = 0; // 총 취득 학점
        double kyCredit = 0; // 교양 학점
        double majorCredit = 0; // 전공 학점
        double doubleMajorCredit = 0; // 복수 전공 학점
        double subMajorCredit = 0; // 부전공 학점

        int nonSubject = 0; // 비교과 이수 학기
        int mileage = 0; // 비교과 마일리지
        boolean engCertification = false;

        List<String> requiredMajorList = new ArrayList<>();
        List<String> requiredKyList = new ArrayList<>();
        List<String> allKyList = new ArrayList<>();

        String[] pdfContent = pdf.split("\n");

        for (int i = 0; i < pdfContent.length; i++) {
            String line = pdfContent[i];
            log.info("[line - " + (i + 1) + "] : " + line);

            // 학번 추출
            try {
                if (line.contains("학 번")) {
                    studentId = Integer.parseInt(line.substring(4, 8));
                    if (studentId < 2017) {
                        throw new CustomException(Error.CANNOT_CHECK_STUDENT_ID);
                    }
                }
            } catch (Exception e) {
                throw new CustomException(Error.EXTRACT_STUDENT_ID_ERROR);
            }

            // 학과 추출 (주전공)
            try {
                if (line.contains("부전공Ⅰ")) {
                    String[] strings;
                    if (pdfContent[i - 1].contains("교과과정")) {
                        if (pdfContent[i - 2].equals("학") || pdfContent[i - 2].equals("학부")) { // 미영광홍
                            studentMajor = pdfContent[i - 3].trim() + pdfContent[i - 2];
                        } else {
                            strings = pdfContent[i - 2].split(" ");
                            studentMajor = strings[2];
                        }
                    } else {
                        strings = pdfContent[i - 1].split(" ");
                        studentMajor = strings[2];
                    }
                }
            } catch (Exception e) {
                throw new CustomException(Error.EXTRACT_MAJOR_ERROR);
            }

            // 학과 추출 (복수전공)
            try {
                if (line.contains("복수전공Ⅰ")) {
                    String[] strings = line.split(" ");
                    if (!strings[7].contains("복수전공Ⅱ")) {
                        studentDoubleMajor = strings[7];
                    }
                }
            } catch (Exception e) {
                throw new CustomException(Error.EXTRACT_DOUBLE_MAJOR_ERROR);
            }

            // 학과 추출 (부전공)
            try {
                if (line.contains("부전공Ⅱ")) {
                    String[] strings = line.split(" ");
                    if (!strings[0].contains("부전공Ⅱ")) {
                        studentSubMajor = strings[0];
                    }
                }
            } catch (Exception e) {
                throw new CustomException(Error.EXTRACT_SUB_MAJOR_ERROR);
            }

            // 총 취득학점 추출
            try {
                if (line.contains("총 취득학점")) {
                    String[] strings = line.split(" ");
                    totalCredit = Double.parseDouble(strings[2]);
                }
            } catch (Exception e) {
                throw new CustomException(Error.EXTRACT_TOTAL_CREDIT_ERROR);
            }

            // 교양, 전공 이수학점 추출
            try {
                if (line.contains("교양: ") && line.contains("전공: ")) {
                    String[] strings = line.split(" ");
                    kyCredit = Double.parseDouble(strings[1]);
                    majorCredit = Double.parseDouble(strings[3]);
                }
            } catch (Exception e) {
                throw new CustomException(Error.EXTRACT_CREDIT_ERROR);
            }

            // 복수전공 이수학점 추출
            try {
                if (line.contains("복수:")) {
                    String[] strings = line.split(" ");
                    doubleMajorCredit = Double.parseDouble(strings[1]);
                }
            } catch (Exception e) {
                throw new CustomException(Error.EXTRACT_DOUBLE_MAJOR_CREDIT_ERROR);
            }

            // 부전공 이수학점 추출
            try {
                if (line.contains("부전공:")) {
                    String[] strings = line.split(" ");
                    subMajorCredit = Double.parseDouble(strings[1]);
                }
            } catch (Exception e) {
                throw new CustomException(Error.EXTRACT_SUB_MAJOR_CREDIT_ERROR);
            }

            // 수강한 전필 과목 추출
            try {
                if (line.startsWith("전필") && !line.endsWith("F") && !line.endsWith("NP")) {
                    String[] strings = line.split(" ");
                    String majorSubject = strings[2];
                    requiredMajorList.add(majorSubject);
                }
            } catch (Exception e) {
                throw new CustomException(Error.EXTRACT_MAJOR_SUBJECT_ERROR);
            }

            // 수강한 교필 과목 추출 & 모든 교양 과목 추출 (for 교양 카운트 증가)
            try {
                if ((line.startsWith("교선") || line.startsWith("교필")) && !line.endsWith("F") && !line.endsWith("NP")) {
                    String[] strings = line.split("\\s+");
                    String kySubject = strings[2];
                    allKyList.add(kySubject);

                    if (line.startsWith("교필")) {
                        requiredKyList.add(kySubject);
                    }
                }
            } catch (Exception e) {
                throw new CustomException(Error.EXTRACT_KY_SUBJECT_ERROR);
            }

            // 비교과 이수 학기 카운트
            if (line.contains("학기 인정")) {
                nonSubject++;
            }

            // 마일리지 추출
            if (line.contains("마일리지")) {
                String mileageString = line.substring(22);
                mileage = Integer.parseInt(mileageString);
            }

            // 영어인증자 추출
            if (line.contains("영어인증")) {
                engCertification = true;
            }
        }

        return Graduate.builder()
                .studentId(studentId)
                .studentMajor(studentMajor)
                .studentDoubleMajor(studentDoubleMajor)
                .studentSubMajor(studentSubMajor)
                .isDoubleMajor(studentDoubleMajor != null)
                .totalCredit(totalCredit)
                .kyCredit(kyCredit)
                .majorCredit(majorCredit)
                .doubleMajorCredit(doubleMajorCredit)
                .subMajorCredit(subMajorCredit)
                .nonSubject(nonSubject)
                .mileage(mileage)
                .engCertification(engCertification)
                .requiredMajorList(requiredMajorList)
                .requiredKyList(requiredKyList)
                .allKyList(allKyList)
                .build();
    }

    /**
     * 졸업 가능 검사 및 부족한 졸업요건 저장
     */
    private String checkAndGetFailure(Graduate graduate) {
        String failure = ""; // 부족한 요건 담는 String

        // 졸업 학점, 교양 학점 검사
        failure += checkTotalAndKy(graduate);

        // 전공 학점, 전필 검사
        if (graduate.isDoubleMajor()) { // 복전일 경우
            failure += checkDoubleMajor(graduate);
        } else { // 복전 아닐 경우
            failure += checkMajor(graduate);
        }

        // 교필 검사
        failure += checkRequiredKy(graduate);

        // 비교과 검사
        failure += checkNonSubject(graduate);

        // 부전공 검사
        if (graduate.getStudentSubMajor() != null) {
            failure += checkSubMajor(graduate);
        }

        return failure;
    }

    /**
     * 졸업 학점, 교양 학점 검사
     */
    private String checkTotalAndKy(Graduate graduate) {
        StringBuilder failure = new StringBuilder();
        String addString;

        double totalCredit = graduate.getTotalCredit();
        double kyCredit = graduate.getKyCredit();
        double exceed = 0; // 교양 초과 학점

        // 해당 학번 과목 교양 학점 범위 (17~22학번 : 35~49학점, 23학번 : 35~45학점)
        int kyMaxCredit = 49;
        if (graduate.getStudentId() >= 2023) kyMaxCredit = 45;

        // 학과 정보 가져오기
        Major major = getMajor(graduate.getStudentMajor(), graduate.getStudentId());

        // 해당 학과 졸업학점 가져오기
        int graduateCredit = major.getGraduateCredit();

        // 교양 초과 학점 검사 (총 취득학점 - 초과한 교양 학점)
        if (kyCredit > kyMaxCredit) {
            exceed = kyCredit - kyMaxCredit;
            totalCredit -= exceed;
        }

        // 졸업 학점 검사
        if (totalCredit < graduateCredit) {
            if (exceed > 0) {
                addString = "교양학점 " + exceed + "학점 초과되어 총 취득 학점에서 " + exceed + "학점 제외 " +
                        "(총 취득학점 : " + totalCredit + "학점)\n";
                failure.append(addString);
            }
            addString = "졸업학점 " + (graduateCredit - totalCredit) + "학점 미달\n";
            failure.append(addString);
        }

        // 교양 최소 학점 검사 (17~22학번 : 35~49학점, 23학번 : 35~45학점)
        if (kyCredit < 35) {
            addString = "교양학점 " + (35 - kyCredit) + "학점 미달\n";
            failure.append(addString);
        }

        return failure.toString();
    }

    /**
     * 전공 학점, 전필 검사 (복전 아닐 경우)
     */
    private String checkMajor(Graduate graduate) {
        StringBuilder failure = new StringBuilder();
        String addString;

        double majorCredit = graduate.getMajorCredit();
        List<String> userRequiredMajorList = graduate.getRequiredMajorList();

        // 학과 정보 가져오기
        Major major = getMajor(graduate.getStudentMajor(), graduate.getStudentId());

        // 해당 학과 전공최소학점 가져오기
        int majorMinCredit = major.getMinCredit();

        // 해당 학과 전공필수 목록 가져오기
        List<String> requiredMajorList = subjectRepository.findRequiredMajorByMajorCode(major.getCode());

        // 전공 최소이수학점 검사
        if (majorCredit < majorMinCredit) {
            addString = "전공학점 " + (majorMinCredit - majorCredit) + "학점 미달\n";
            failure.append(addString);
        }

        // 전필 과목 검사
        requiredMajorList.removeAll(userRequiredMajorList);
        for (String subject : requiredMajorList) {
            addString = "전공필수 '" + subject + "' 미수강\n";
            failure.append(addString);
        }

        return failure.toString();
    }

    /**
     * 전공 학점, 전필 검사 (복전일 경우)
     */
    private String checkDoubleMajor(Graduate graduate) {
        StringBuilder failure = new StringBuilder();
        String addString;

        double majorCredit = graduate.getMajorCredit();
        double doubleMajorCredit = graduate.getDoubleMajorCredit();
        List<String> userRequiredMajorList = graduate.getRequiredMajorList();

        // 학과 정보 가져오기
        Major major = getMajor(graduate.getStudentMajor(), graduate.getStudentId());
        Major doubleMajor = getDoubleMajor(graduate.getStudentDoubleMajor(), graduate.getStudentId());

        // 해당 학과 전공필수 목록 가져오기
        List<String> requiredMajorList = subjectRepository.findRequiredMajorByMajorCode(major.getCode());
        requiredMajorList.addAll(subjectRepository.findRequiredMajorByMajorCode(doubleMajor.getCode()));

        // 주전공 최소이수학점(36학점) 검사
        if (majorCredit < 36) {
            addString = "주전공학점 " + (36 - majorCredit) + "학점 미달\n";
            failure.append(addString);
        }

        // 부전공 최소이수학점(36학점) 검사
        if (doubleMajorCredit < 36) {
            addString = "복수전공학점 " + (36 - doubleMajorCredit) + "학점 미달\n";
            failure.append(addString);
        }

        // 전필 과목 검사
        requiredMajorList.removeAll(userRequiredMajorList);
        for (String subject : requiredMajorList) {
            addString = "전공필수 '" + subject + "' 미수강\n";
            failure.append(addString);
        }

        return failure.toString();
    }

    /**
     * 교필 검사
     */
    private String checkRequiredKy(Graduate graduate) {
        StringBuilder failure = new StringBuilder();
        String addString;

        int studentId = graduate.getStudentId();
        String major = graduate.getStudentMajor();
        List<String> userRequiredKyList = graduate.getRequiredKyList();
        boolean engCertification = graduate.isEngCertification();

        int chapel = 0; // 채플 카운트
        boolean christian = false; // 기독교
        boolean bible = false; // 성서
        boolean collegeGuide = false; // 대생길
        boolean socialGuide = false; // 사생길
        boolean readDebate = false; // 독토
        boolean writing = false; // 글기
        boolean computing = false; // 컴퓨팅사고와SW코딩
        boolean eng1 = false; // 영어1
        boolean eng2 = false; // 영어2
        int counseling = 0; // 진로와상담 카운트

        for (String ky : userRequiredKyList) {
            // 채플 카운트
            if (ky.equals("채플")) chapel++;

            // 기독교 과목 카운트
            if (ky.contains("기독교")) christian = true;

            // 성서 과목 카운트
            if (ky.contains("성서")) bible = true;

            // 대학생활길잡이
            if (ky.equals("대학생활길잡이") || ky.equals("캠퍼스라이프")) collegeGuide = true;

            // 사회생활길잡이 검사 (아노덴 '인문강단' 대체)
            if (ky.equals("사회생활길잡이") || ky.equals("인문강단")) socialGuide = true;

            // 독서와토론 검사
            if (ky.equals("독서와토론")) readDebate = true;

            // 글쓰기의기초 검사 (19학번 이후 소프트웨어교과목(프로그래밍기초)으로 대체 가능 / 23학번부터 글기만 허용)
            if (ky.equals("글쓰기의기초")) writing = true;
            if (ky.contains("프로그래밍기초") && studentId < 2023) writing = true;

            // 영어Ⅰ,Ⅱ 검사 (아노덴 'Speaking EnglishⅠ,Ⅱ' 대체 / 19학번 이후 영어인증자 면제)
            if (ky.equals("영어Ⅰ") || ky.equals("EnglishⅠ") || engCertification || ky.equals("Essential English")) {
                eng1 = true;
            }
            if (ky.equals("영어Ⅱ") || ky.equals("EnglishⅡ") || engCertification || ky.equals("Essential English")) {
                eng2 = true;
            }

            // 컴퓨팅사고와SW코딩 검사
            if (ky.equals("컴퓨팅사고와SW코딩")) computing = true;

            // 진로와상담 검사
            if (ky.equals("진로와상담")) counseling++;
        }

        // 채플 검사
        if (chapel < 4) {
            addString = "교양필수 '채플' " + (4 - chapel) + "회 미수강\n";
            failure.append(addString);
        }

        // 기독교 과목 검사
        if (!christian) {
            failure.append("교양필수 '기독교 관련 과목' 미수강\n");
        }

        // 성서 과목 검사
        if (!bible) {
            failure.append("교양필수 '성서 관련 과목' 미수강\n");
        }

        // 대학생활길잡이 검사 (아노덴 '캠퍼스라이프' 대체)
        if (!collegeGuide) {
            if (major.contains("아노덴")) {
                failure.append("교양필수 '캠퍼스라이프' 미수강\n");
            } else {
                failure.append("교양필수 '대학생활길잡이' 미수강\n");
            }
        }

        // 사회생활길잡이 검사 (아노덴 '인문강단' 대체)
        if (!socialGuide) {
            if (major.contains("아노덴")) {
                failure.append("교양필수 '인문강단' 미수강\n");
            } else {
                failure.append("교양필수 '사회생활길잡이' 미수강\n");
            }
        }

        // 독서와토론 검사 (23학번부터 독토 안 들어도 됨)
        if (studentId < 2023 && !readDebate) {
            failure.append("교양필수 '독서와토론' 미수강\n");
        }

        // 글쓰기의기초 검사 (19학번 이후 소프트웨어 과목(프로그래밍기초)으로 대체 가능 / 23학번부터 글기만 허용)
        if (!writing) {
            if (studentId >= 2019 && studentId <= 2022) {
                failure.append("교양필수 '글쓰기의기초' 또는 '소프트웨어교과목' 미수강\n");
            } else {
                failure.append("교양필수 '글쓰기의기초' 미수강\n");
            }
        }

        // 2023학번부터 컴퓨팅사고와SW코딩 검사
        if (studentId >= 2023 && !computing) {
            failure.append("교양필수 '컴퓨팅사고와SW코딩' 미수강\n");
        }

        // 영어Ⅰ,Ⅱ 검사 (아노덴 'Speaking EnglishⅠ,Ⅱ' 대체 / 19학번 이후 영어인증자 면제 / 23학번부터 영어Ⅰ,Ⅱ 대신 Essential English)
        if (!eng1) {
            failure.append(getEnglishFailure(major, "영어Ⅰ", "Speaking EnglishⅠ", studentId));
        }
        if (!eng2) {
            failure.append(getEnglishFailure(major, "영어Ⅱ", "Speaking EnglishⅡ", studentId));
        }

        // 진로와상담 검사
        if (counseling < 4) {
            addString = "교양필수 '진로와상담' " + (4 - counseling) + "회 미수강\n";
            failure.append(addString);
        }

        return failure.toString();
    }

    /**
     * 교필 검사 - 영어 부족한 요건
     */
    private String getEnglishFailure(String major, String name, String subName, int studentId) {
        String engFailure;

        if (studentId >= 2023) {
            engFailure = "교양필수 'Essential English' 미수강";
        } else {
            if (major.contains("아노덴")) {
                engFailure = "교양필수 '" + subName + "' 미수강";
            } else {
                engFailure = "교양필수 '" + name + "' 미수강";
            }
        }

        if (studentId >= 2019) {
            engFailure += " 또는 영어인증 미인증\n";
        } else {
            engFailure += "\n";
        }

        return engFailure;
    }

    /**
     * 비교과 검사
     */
    private String checkNonSubject(Graduate graduate) {
        int studentId = graduate.getStudentId();
        int mileage = graduate.getMileage();
        int nonSubject = graduate.getNonSubject();

        if (studentId >= 2020) { // 20학번 이후 마일리지 300점 이상
            if (mileage < 300) {
                return "비교과 마일리지 " + (300 - mileage) + "점 미달\n";
            }
        } else if (studentId >= 2017) { // 17학번 이후 비교과 이수 학기 3학기 이상 또는 마일리지 300점 이상
            if (mileage < 300 && nonSubject < 3) {
                return "비교과 이수학기 " + (3 - nonSubject) + "학기 미이수 또는 비교과 마일리지 " + (300 - mileage) + "점 미달\n";
            }
        }

        return "";
    }

    /**
     * 부전공 검사
     */
    private String checkSubMajor(Graduate graduate) {
        double subMajorCredit = graduate.getSubMajorCredit();

        if (subMajorCredit < 21) {
            return "부전공학점 " + (21 - subMajorCredit) + "학점 미달\n";
        }

        return "";
    }

    /**
     * 교양 수강횟수 증가 (2023년 8월 졸업부터 교양배분이수제(인재상, 핵심역량) 폐지됨에 따라 검사 로직 삭제)
     */
    private void countKy(List<String> allKyList) {
        for (String name : allKyList) {
            subjectRepository.findByName(name).ifPresent(subject -> {
                subject.increaseKyCount();
                subjectRepository.save(subject);
            });
        }
    }

    /**
     * 이용 횟수 증가
     */
    private void increaseCount(Long year) {
        Record record = recordRepository.findById(year)
                .orElse(Record.builder().year(year).count(0).build());
        record.increaseCount();
        recordRepository.save(record);
    }

    /**
     * 사용자 졸업가능 검사 여부 변경
     */
    private void setUserIsChecked(User user) {
        user.checkGraduateOk();
        userRepository.save(user);
    }

    /**
     * 전공 조회
     */
    private Major getMajor(String studentMajor, Integer studentId) {
        List<Major> majorList = majorRepository.findByNameAndYear(studentMajor, studentId);

        if (ObjectUtils.isEmpty(majorList)) {
            throw new CustomException(Error.NOT_FOUND_YEAR_MAJOR);
        }

        return majorList.get(0);
    }

    /**
     * 복수전공 조회
     */
    private Major getDoubleMajor(String studentMajor, Integer studentId) {
        List<Major> doubleMajorList = majorRepository.findDoubleMajor(studentMajor, studentId);

        if (ObjectUtils.isEmpty(doubleMajorList)) {
            throw new CustomException(Error.NOT_FOUND_YEAR_MAJOR);
        }

        return doubleMajorList.get(0);
    }
}
