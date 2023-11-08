package GraduateOk.graduateokv2.domain;

import GraduateOk.graduateokv2.exception.CustomException;
import GraduateOk.graduateokv2.exception.Error;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum SubjectModelType {

    // 인재상
    INTELLIGENCE("소통하는 지성인"),
    ORIGINALITY("도전하는 창의인"),
    PEACE("실천하는 평화인");

    private final String description;

    private static final Map<String, String> STYLE_MAP = Collections.unmodifiableMap(
            Arrays.stream(values()).collect(Collectors.toMap(SubjectModelType::getDescription, SubjectModelType::name))
    );

    public static SubjectModelType descriptionToSubjectModelType(String description) {
        String name = STYLE_MAP.get(description);
        if (name == null) {
            throw new CustomException(Error.BAD_REQUEST);
        }
        return SubjectModelType.valueOf(name);
    }
}
