package io.github.mikeiansky.oauth2.model.service.impl;

import io.github.mikeiansky.oauth2.model.entity.Oauth2Client;
import io.github.mikeiansky.oauth2.model.mapper.Oauth2ClientMapper;
import io.github.mikeiansky.oauth2.model.service.IOauth2ClientService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * OAuth2 客户端信息表 服务实现类
 * </p>
 *
 * @author Mikeiansky
 * @since 2025-04-02
 */
@Service
public class Oauth2ClientServiceImpl extends ServiceImpl<Oauth2ClientMapper, Oauth2Client> implements IOauth2ClientService {

}
