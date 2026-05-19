package com.integration.client.wechat;

import com.integration.common.core.api.ResultCode;
import com.integration.common.core.exception.BizException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@ConditionalOnProperty(prefix = "integration.client.wechat.mini-program", name = "mock-enabled", havingValue = "true")
public class MockWeChatMiniProgramClient implements WeChatMiniProgramClient {

    @Override
    public WeChatMiniProgramSession code2Session(String jsCode) {
        if (!StringUtils.hasText(jsCode)) {
            throw new BizException(ResultCode.BAD_REQUEST, "微信登录 code 不能为空");
        }
        return new WeChatMiniProgramSession("mock-openid-" + jsCode, "mock-unionid-" + jsCode);
    }
}
