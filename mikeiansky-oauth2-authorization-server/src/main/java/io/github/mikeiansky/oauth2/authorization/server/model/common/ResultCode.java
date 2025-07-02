package io.github.mikeiansky.oauth2.authorization.server.model.common;


/**
 * @author mike ian
 * @date 2024/8/7
 * @desc 响应数据状态枚举
 **/
public enum ResultCode {

    OK(1, "success"),
    ERROR(0, "error"),
    INVALID_TOKEN(9, "invalid token"),
    INVALID_REQUEST_PARAM(0, "Invalid request parameters"),
    LOGIN_ERROR(1001, "用户名密码错误"),
    PERMISSION_DENY(0, "当前用户无权限"),
    NO_LOGIN(0, "用户未登录"),
    WECHAT_LOGIN_ERROR(0, "微信授权登录失败"),
    HAVE_NO_RESUME(5001, "未建立用户档案"),
    SHORT_OF_ENTERPRISE_FRIEND(5002, "未添加企业好友"),
    SHORT_OF_ENTERPRISE_TAG(5003, "未添加企业标签"),
    ;

    private int code;
    private String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }

}
