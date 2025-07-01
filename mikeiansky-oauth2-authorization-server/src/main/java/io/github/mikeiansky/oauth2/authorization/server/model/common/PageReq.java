package io.github.mikeiansky.oauth2.authorization.server.model.common;

import lombok.Data;

/**
 *
 * @author mike ian
 * @date 2025/6/17
 * @desc
 **/
@Data
public class PageReq {

    private Integer page = 1;

    private Integer pageSize = 10;

}
