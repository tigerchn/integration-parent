package com.integration.client.user.service;

import com.integration.client.user.entity.ClientUser;
import com.integration.client.user.mapper.ClientUserMapper;
import com.integration.client.wechat.WeChatMiniProgramSession;
import com.integration.common.core.api.ResultCode;
import com.integration.common.core.exception.BizException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class ClientUserService {

    private final ClientUserMapper clientUserMapper;

    public ClientUserService(ClientUserMapper clientUserMapper) {
        this.clientUserMapper = clientUserMapper;
    }

    public ClientUser getById(Long id) {
        return clientUserMapper.selectById(id);
    }

    public ClientUser requireEnabledUser(Long id) {
        ClientUser user = clientUserMapper.selectById(id);
        if (user == null) {
            throw new BizException(ResultCode.UNAUTHORIZED, "用户不存在");
        }
        if (user.getEnabled() == null || user.getEnabled() != 1) {
            throw new BizException(ResultCode.FORBIDDEN, "账号已禁用");
        }
        return user;
    }

    @Transactional
    public ClientUser findOrCreateByWeChatSession(WeChatMiniProgramSession session) {
        ClientUser existing = clientUserMapper.selectByOpenid(session.openid());
        LocalDateTime now = LocalDateTime.now();
        if (existing != null) {
            if (existing.getEnabled() == null || existing.getEnabled() != 1) {
                throw new BizException(ResultCode.FORBIDDEN, "账号已禁用");
            }
            if (StringUtils.hasText(session.unionid()) && !session.unionid().equals(existing.getUnionid())) {
                existing.setUnionid(session.unionid());
            }
            existing.setLastLoginAt(now);
            existing.setUpdatedAt(now);
            clientUserMapper.updateById(existing);
            return existing;
        }

        ClientUser created = new ClientUser();
        created.setOpenid(session.openid());
        created.setUnionid(session.unionid());
        created.setEnabled(1);
        created.setLastLoginAt(now);
        created.setCreatedAt(now);
        created.setUpdatedAt(now);
        clientUserMapper.insert(created);
        return created;
    }
}
