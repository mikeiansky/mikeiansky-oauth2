package io.github.mikeiansky.oauth2.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * <p>
 * 用户权限表
 * </p>
 *
 * @author Mikeiansky
 * @since 2025-03-21
 */
@Getter
@Setter
@ToString
@TableName("user_authority")
public class UserAuthority implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 权限
     */
    private String authority;
}
