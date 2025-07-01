package io.github.mikeiansky.oauth2.authorization.server.model.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author mike ian
 * @date 2025/6/12
 * @desc
 **/
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RespResult<T> {

    private int code;
    private String msg;
    private T data;

    public static <T> RespResult<T> ok() {
        return new RespResult<>(ResultCode.OK.code(), ResultCode.OK.message(), null);
    }

    public static <T> RespResult<T> ok(T data) {
        return new RespResult<>(ResultCode.OK.code(), ResultCode.OK.message(), data);
    }

    public static <T> RespResult<T> fail() {
        return new RespResult<>(ResultCode.ERROR.code(), ResultCode.ERROR.message(), null);
    }

    public static <T> RespResult<T> fail(String msg) {
        return new RespResult<>(ResultCode.ERROR.code(), msg, null);
    }

    public static <T> RespResult<T> fail(ResultCode code) {
        return new RespResult<>(code.code(), code.message(), null);
    }

    public static <T> RespResult<T> fail(int code, String msg) {
        return new RespResult<>(code, msg, null);
    }

    public static <T> RespResult<T> of(ResultCode code) {
        return new RespResult<>(code.code(), code.message(), null);
    }

    public static <T> RespResult<T> of(ResultCode code, String msg) {
        return new RespResult<>(code.code(), msg, null);
    }

    public static <T> RespResult<T> of(ResultCode code, int codeValue) {
        return new RespResult<>(codeValue, code.message(), null);
    }

    public static <T> RespResult<T> of(ResultCode code, T data) {
        return new RespResult<>(code.code(), code.message(), data);
    }

    public static <T> RespResult<T> of(int code, String message, T data) {
        return new RespResult<>(code, message, data);
    }

}
