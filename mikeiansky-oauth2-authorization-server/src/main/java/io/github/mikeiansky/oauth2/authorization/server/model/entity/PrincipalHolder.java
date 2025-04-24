package io.github.mikeiansky.oauth2.authorization.server.model.entity;

import lombok.Data;

import java.security.Principal;
import java.util.Set;

/**
 * @author mike ian
 * @date 2025/4/24
 * @desc
 **/
@Data
public class PrincipalHolder implements Principal {

    private int type;

    private String name;

    private Set<String> authorities;

    @Override
    public String getName() {
        return name;
    }

}
