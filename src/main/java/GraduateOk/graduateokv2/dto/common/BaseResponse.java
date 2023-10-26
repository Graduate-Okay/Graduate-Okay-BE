package GraduateOk.graduateokv2.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonPropertyOrder({"status", "code", "message", "data"}) // Json 으로 나갈 순서를 설정하는 어노테이션
@JsonInclude(JsonInclude.Include.NON_NULL) // Json으로 응답이 나갈 때 - null인 필드는 응답으로 포함시키지 않는 어노테이션
public class BaseResponse<T> {

    String status;

    int code;

    T data;

    private BaseResponse(HttpStatus httpStatus) {
        this.status = httpStatus.name();
        this.code = httpStatus.value();
    }

    private BaseResponse(HttpStatus httpStatus, T data) {
        this.status = httpStatus.name();
        this.code = httpStatus.value();
        this.data = data;
    }

    public static BaseResponse<?> ok(HttpStatus httpStatus) {
        return new BaseResponse<>(httpStatus);
    }

    public static <T> BaseResponse<T> ok(HttpStatus httpStatus, T data) {
        return new BaseResponse<>(httpStatus, data);
    }
}
