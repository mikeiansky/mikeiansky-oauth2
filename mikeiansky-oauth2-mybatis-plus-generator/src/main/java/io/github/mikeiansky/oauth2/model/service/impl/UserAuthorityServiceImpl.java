package io.github.mikeiansky.oauth2.model.service.impl;

import io.github.mikeiansky.oauth2.model.entity.UserAuthority;
import io.github.mikeiansky.oauth2.model.mapper.UserAuthorityMapper;
import io.github.mikeiansky.oauth2.model.service.IUserAuthorityService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户权限表 服务实现类
 * </p>
 *
 * @author Mikeiansky
 * @since 2025-03-21
 */
@Service
public class UserAuthorityServiceImpl extends ServiceImpl<UserAuthorityMapper, UserAuthority> implements IUserAuthorityService {

}
