package io.github.mikeiansky.oauth2.authorization.server.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 *
 * @author mike ian
 * @date 2025/6/17
 * @desc
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {

    private int page;
    private int pageSize;
    private long total;
    private List<T> list;

    public static <T> PageResult<T> empty(int page, int pageSize) {
        return new PageResult<>(page, pageSize, 0, null);
    }

    public static <T> PageResult<T> of(int page, int pageSize, long total, List<T> data) {
        return new PageResult<>(page, pageSize, total, data);
    }

    public static <T> PageResult<T> empty(int page, int pageSize, long total) {
        return new PageResult<>(page, pageSize, total, null);
    }

}
