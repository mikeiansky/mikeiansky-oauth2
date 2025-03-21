package io.github.mikeiansky.oauth2.authorization.server.service;

import com.baomidou.mybatisplus.extension.toolkit.Db;
import io.github.mikeiansky.oauth2.model.entity.UserAccount;
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
        // 查询权限
        User user = new User(userAccount.getUsername(),
                userAccount.getPassword(),
                List.of(new SimpleGrantedAuthority("user")));
        return user;
    }

}
