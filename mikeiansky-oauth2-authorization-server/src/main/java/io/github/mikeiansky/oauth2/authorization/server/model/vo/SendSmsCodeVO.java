package io.github.mikeiansky.oauth2.authorization.server.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author mike ian
 * @date 2025/7/2
 * @desc
 **/
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SendSmsCodeVO {

    private String code;

}
