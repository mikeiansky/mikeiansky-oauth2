package io.github.mikeiansky.oauth2.authorization.server.service;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import io.github.mikeiansky.oauth2.model.entity.UserAccount;
import io.github.mikeiansky.oauth2.model.entity.UserAuthority;
import io.github.mikeiansky.utils.CollKit;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author mike ian
 * @date 2025/3/21
 * @desc
 **/
@Service
public class UserStoreService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount userAccount = Db.lambdaQuery(UserAccount.class)
                .eq(UserAccount::getUsername, username)
                .one();
        if (userAccount == null) {
            return null;
        }

        List<SimpleGrantedAuthority> authorityList = CollKit.toList(Db.lambdaQuery(UserAuthority.class)
                .eq(UserAuthority::getUserId, userAccount.getId())
                .list(), userAuthority -> new SimpleGrantedAuthority(userAuthority.getAuthority()));
        if (CollUtil.isEmpty(authorityList)) {
            authorityList = List.of(new SimpleGrantedAuthority("user"));
        }

        // 查询权限
        return User.withUsername(userAccount.getUsername())
                .password(userAccount.getPassword())
                .authorities(authorityList)
                .build();
    }

}
