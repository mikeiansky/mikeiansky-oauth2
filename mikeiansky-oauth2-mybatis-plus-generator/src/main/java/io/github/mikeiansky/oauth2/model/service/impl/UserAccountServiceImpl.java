package io.github.mikeiansky.oauth2.model.service.impl;

import io.github.mikeiansky.oauth2.model.entity.UserAccount;
import io.github.mikeiansky.oauth2.model.mapper.UserAccountMapper;
import io.github.mikeiansky.oauth2.model.service.IUserAccountService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户账户表 服务实现类
 * </p>
 *
 * @author Mikeiansky
 * @since 2025-03-21
 */
@Service
public class UserAccountServiceImpl extends ServiceImpl<UserAccountMapper, UserAccount> implements IUserAccountService {

}
