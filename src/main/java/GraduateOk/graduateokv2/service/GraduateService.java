package GraduateOk.graduateokv2.service;

import GraduateOk.graduateokv2.domain.Graduate;
import GraduateOk.graduateokv2.dto.graduate.GraduateResponseDto;
import GraduateOk.graduateokv2.exception.CustomException;
import GraduateOk.graduateokv2.exception.Error;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional
    public GraduateResponseDto isGraduateOk(MultipartFile multipartFile) {
        // 파일 읽어오기
        String pdf = extractPdfContent(multipartFile);
        if (pdf.isEmpty()) throw new CustomException(Error.NOT_FOUND_PDF_CONTENT);

        // 한신대학교 학업성적확인서 PDF인지 검사
        if (!pdf.contains("포털>한신종합정보>성적")) {
            throw new CustomException(Error.BAD_PDF);
        }

        // 기본 정보 추출
        Graduate graduate = extractBasicInfo(pdf);

        // 검사 및 부족한 졸업요건 저장
        String failure = checkAndGetFailure(graduate);

        return GraduateResponseDto.builder()
                .isGraduateOk(failure.isEmpty())
                .totalCredit(graduate.getTotalCredit())
                .kyCredit(graduate.getKyCredit())
                .majorCredit(graduate.getMajorCredit())
                .doubleMajorCredit(graduate.getDoubleMajorCredit())
                .nonSubject(graduate.getNonSubject())
                .mileage(graduate.getMileage())
                .failure(failure)
                .build();
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
            tempFolder.mkdirs();
            System.out.println("Temp folder created.");
        }

        // MultipartFile to File
        File file = new File(tempFolder + "/" + multipartFile.getOriginalFilename());
        System.out.println("임시 폴더에 파일 생성");

        try {
            if (file.createNewFile()) {
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(multipartFile.getBytes());
                }
            }
            System.out.println("파일 생성");

            // 텍스트 추출
            PDDocument pdfDoc = PDDocument.load(file);
            extractText = new PDFTextStripper().getText(pdfDoc);
            System.out.println("텍스트 추출");
        } catch (IOException e) {
            throw new CustomException(Error.CANNOT_READ_PDF);
        }

        // 파일 삭제 todo: 파일 삭제 안 되는 것 해결
        File[] files = tempFolder.listFiles();
        for (File f : files) {
            f.delete();
        }
        System.out.println("파일 삭제");
        tempFolder.delete();
        System.out.println("임시 폴더 삭제");

        return extractText;
    }

    /**
     * 기본 정보 추출
     */
    private Graduate extractBasicInfo(String pdf) {
        String[] pdfContent = pdf.split("\n");
        Graduate graduate = new Graduate();
        List<String> requiredMajor = new ArrayList<>();
        List<String> requiredKy = new ArrayList<>();
        List<String> allKy = new ArrayList<>();

        for (int i = 0; i < pdfContent.length; i++) {
            String line = pdfContent[i];

            // 학번 추출
            if (line.contains("학 번")) {
                graduate.setStudentId(Integer.parseInt(line.substring(4, 8)));
            }

            // 학과 추출 (주전공)
            if (line.contains("부전공Ⅰ")) {
                String[] strings = pdfContent[i - 1].split(" ");
                graduate.setStudentMajor(strings[2]);
            }

            // 학과 추출 (복수전공)
            if (line.contains("복수전공Ⅰ")) {
                String[] strings = line.split(" ");
                if (!strings[7].contains("복수전공Ⅱ")) {
                    graduate.setStudentDoubleMajor(strings[7]);
                }
            }

            // 학과 추출 (부전공)
            if (line.contains("부전공Ⅱ")) {
                String[] strings = line.split(" ");
                if (!strings[0].contains("부전공Ⅱ")) {
                    graduate.setStudentSubMajor(strings[0]);
                }
            }

            // 총 취득학점 추출
            if (line.contains("총 취득학점")) {
                graduate.setTotalCredit(line.length());
            }

            // 교양, 전공 이수학점 추출
            if (line.contains("교양: ") && line.contains("전공: ")) {
                graduate.setKyCredit(Integer.parseInt(line.substring(4, 6).trim()));
                graduate.setMajorCredit(Integer.parseInt(line.substring(11, 13).trim()));
            }

            // 복수전공 이수학점 추출
            if (line.contains("복수:")) {
                graduate.setDoubleMajorCredit(Integer.parseInt(line.substring(4, 6).trim()));
            }

            // 부전공 이수학점 추출
            if (line.contains("부전공:")) {
                graduate.setSubMajorCredit(Integer.parseInt(line.substring(5, 7).trim()));
            }

            // 수강한 전필 과목 추출
            if (line.startsWith("전필") && !line.contains("F") && !line.contains("NP")) {
                String[] strings = line.split(" ");
                requiredMajor.add(strings[2]);
            }

            // 수강한 교필 과목 추출
            if (line.startsWith("교필") && !line.contains("NP")) {
                String[] strings = line.split("\\s+");
                if (!(strings[4].contains("F"))) {
                    requiredKy.add(strings[2]);
                }
            }

            // 비교과 이수 학기 카운트
            if (line.contains("학기 인정")) {
                graduate.setNonSubject(graduate.getNonSubject() + 1);
            }

            // 마일리지 추출
            if (line.contains("마일리지")) {
                graduate.setMileage(Integer.parseInt(line.substring(22)));
            }

            // 영어인증자 추출
            if (line.contains("영어인증")) {
                graduate.setEngCertification(true);
            }

            // 모든 교양 과목 추출 (for 인재상 & 핵심역량 검사, 교양 카운트 증가)
            if ((line.startsWith("교선") || line.startsWith("교필")) && !line.contains("NP")) {
                String[] strings = line.split("\\s+");
                if (strings.length < 5) {
                    allKy.add(strings[2]);
                } else if (!(strings[4].contains("F"))) {
                    allKy.add(strings[2]);
                }
            }
        }

        graduate.setRequiredMajor(requiredMajor);
        graduate.setRequiredKy(requiredKy);
        graduate.setAllKy(allKy);

        // 복전 여부 체크
        if (!graduate.getStudentDoubleMajor().isEmpty()) graduate.setDoubleMajor(true);

        return graduate;
    }

    /**
     * 졸업 가능 검사 및 부족한 졸업요건 저장
     */
    private String checkAndGetFailure(Graduate graduate) {
        String failure = ""; // 부족한 요건 담는 String

        // 전공 학점, 전필 검사
        // ㄴ 복전 아닐 경우
        failure += checkMajor(graduate);
        // ㄴ 복전일 경우
        failure += checkDoubleMajor(graduate);

        // 교필 검사
        failure += checkRequiredKy(graduate);

        // 비교과 검사
        failure += checkNonSubject(graduate);

        // 교선 검사 todo: 교양 카운트 증가
        // ㄴ 인재상 검사
        failure += checkModelKy(graduate);
        // ㄴ 핵심역량 검사
        failure += checkCoreKy(graduate);

        // 부전공 검사
        failure += checkSubMajor(graduate);

        return failure;
    }

    /**
     * 전공 학점, 전필 검사 (복전 아닐 경우)
     */
    private String checkMajor(Graduate graduate) {
        String failure = "";
        int totalCredit = graduate.getTotalCredit();
        int kyCredit = graduate.getKyCredit();
        int majorCredit = graduate.getMajorCredit();

        // 해당 학번 과목 교양 학점 범위 (17~22학번 : 35~49학점, 23학번 : 35~45학점)
        int kyMaxCredit = 49;
        if (graduate.getStudentId() >= 2023) kyMaxCredit = 45;
        // 해당 학과 졸업학점 가져오기
        int graduateCredit = 130; // todo: 수정
        // 해당 학과 전공최소학점 가져오기
        int majorMinCredit = 72; // todo: 수정
        // 해당 학과 전공필수 목록 가져오기
        List<String> requiredMajorList = new ArrayList<>(); // todo: 수정

        // (총 취득학점 - 초과한 교양 학점)
        if (kyCredit > kyMaxCredit) {
            int exceed = kyCredit - kyMaxCredit;
            totalCredit -= exceed;
            failure += "교양학점 " + exceed + "학점 초과되어 총 취득 학점에서 " +
                    exceed + "학점 제외 (총 취득학점 : " + totalCredit + "학점)\n";
        }

        // 졸업 학점 검사
        if (totalCredit < graduateCredit) {
            failure += "졸업학점 " + (graduateCredit - totalCredit) + "학점 미달\n";
        }

        // 교양 최소 학점 검사
        if (kyCredit < 35) {
            failure += "교양학점 " + (35 - kyCredit) + "학점 미달\n";
        }

        // 3. 전공 최소이수학점 검사
        if (majorCredit < majorMinCredit) {
            failure += "전공학점 " + (majorMinCredit - majorCredit) + "학점 미달\n";
        }

        return failure;
    }

    /**
     * 전공 학점, 전필 검사 (복전일 경우)
     */
    private String checkDoubleMajor(Graduate graduate) {
        return "";
    }

    /**
     * 교필 검사
     */
    private String checkRequiredKy(Graduate graduate) {
        return "";
    }

    /**
     * 비교과 검사
     */
    private String checkNonSubject(Graduate graduate) {
        return "";
    }

    /**
     * 인재상 검사
     */
    private String checkModelKy(Graduate graduate) {
        return "";
    }

    /**
     * 핵심역량 검사
     */
    private String checkCoreKy(Graduate graduate) {
        return "";
    }

    /**
     * 부전공 검사
     */
    private String checkSubMajor(Graduate graduate) {
        return "";
    }
}
