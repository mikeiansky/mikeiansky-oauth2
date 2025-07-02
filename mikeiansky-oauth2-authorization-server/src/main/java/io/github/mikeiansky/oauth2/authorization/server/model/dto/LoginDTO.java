package io.github.mikeiansky.oauth2.authorization.server.model.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 *
 * @author mike ian
 * @date 2025/7/2
 * @desc 登录请求参数
 **/
@Data
public class LoginDTO {

//    @Size(min = 2, max = 10, message = "国际码长度在2到10个字符")
//    @NotEmpty(message = "国际码不能为空")
////    @Schema(description = "国际码")
//    private String nationCode;

    @Size(min = 4, max = 20, message = "手机号码长度在4到20个字符")
    @NotEmpty(message = "手机号码不能为空")
//    @Schema(description = "手机号码")
    private String mobile;

    @Size(min = 2, max = 10, message = "验证码长度在2到10个字符")
    @NotEmpty(message = "验证码不能为空")
//    @Schema(description = "验证码")
    private String code;

    @Pattern(regexp = "(pc|web|h5|app|wechat)", message = "不合法的渠道")
    @NotEmpty(message = "渠道标识不能为空")
//    @Schema(description = "渠道标识目前支持渠道(pc|web|h5|app|wechat)", example = "web")
    private String channel;

}
