package com.integration.client.auth;

import com.integration.client.security.ClientTokenService;
import com.integration.client.user.entity.ClientUser;
import com.integration.client.user.service.ClientUserService;
import com.integration.client.wechat.WeChatMiniProgramClient;
import com.integration.client.wechat.WeChatMiniProgramSession;
import org.springframework.stereotype.Service;

@Service
public class ClientAuthService {

    private final WeChatMiniProgramClient weChatMiniProgramClient;
    private final ClientUserService clientUserService;
    private final ClientTokenService clientTokenService;

    public ClientAuthService(WeChatMiniProgramClient weChatMiniProgramClient,
                             ClientUserService clientUserService,
                             ClientTokenService clientTokenService) {
        this.weChatMiniProgramClient = weChatMiniProgramClient;
        this.clientUserService = clientUserService;
        this.clientTokenService = clientTokenService;
    }

    public ClientTokenService.TokenIssueResult wxLogin(String code) {
        WeChatMiniProgramSession session = weChatMiniProgramClient.code2Session(code);
        ClientUser user = clientUserService.findOrCreateByWeChatSession(session);
        return clientTokenService.issueAccessToken(user.getId(), user.getOpenid());
    }
}
