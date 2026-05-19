package com.integration.client.wechat;

import com.fasterxml.jackson.databind.JsonNode;
import com.integration.common.core.api.ResultCode;
import com.integration.common.core.exception.BizException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@ConditionalOnProperty(prefix = "integration.client.wechat.mini-program", name = "mock-enabled", havingValue = "false", matchIfMissing = true)
public class RestWeChatMiniProgramClient implements WeChatMiniProgramClient {

    private static final String CODE2SESSION_URL = "https://api.weixin.qq.com/sns/jscode2session";

    private final WeChatMiniProgramProperties properties;
    private final RestClient restClient;

    public RestWeChatMiniProgramClient(WeChatMiniProgramProperties properties, RestClient.Builder restClientBuilder) {
        this.properties = properties;
        this.restClient = restClientBuilder.build();
    }

    @Override
    public WeChatMiniProgramSession code2Session(String jsCode) {
        if (!StringUtils.hasText(jsCode)) {
            throw new BizException(ResultCode.BAD_REQUEST, "微信登录 code 不能为空");
        }
        if (!StringUtils.hasText(properties.getAppId()) || !StringUtils.hasText(properties.getAppSecret())) {
            throw new BizException(ResultCode.INTERNAL_ERROR,
                    "未配置微信小程序 app-id / app-secret（integration.client.wechat.mini-program）");
        }

        String uri = UriComponentsBuilder.fromHttpUrl(CODE2SESSION_URL)
                .queryParam("appid", properties.getAppId())
                .queryParam("secret", properties.getAppSecret())
                .queryParam("js_code", jsCode)
                .queryParam("grant_type", "authorization_code")
                .toUriString();

        JsonNode body = restClient.get().uri(uri).retrieve().body(JsonNode.class);
        if (body == null) {
            throw new BizException(ResultCode.INTERNAL_ERROR, "微信登录接口无响应");
        }

        if (body.hasNonNull("errcode") && body.get("errcode").asInt() != 0) {
            String errmsg = body.has("errmsg") ? body.get("errmsg").asText() : "unknown";
            throw new BizException(ResultCode.UNAUTHORIZED, "微信登录失败: " + errmsg);
        }

        String openid = body.has("openid") ? body.get("openid").asText(null) : null;
        if (!StringUtils.hasText(openid)) {
            throw new BizException(ResultCode.UNAUTHORIZED, "微信登录失败: 未返回 openid");
        }
        String unionid = body.has("unionid") ? body.get("unionid").asText(null) : null;
        return new WeChatMiniProgramSession(openid, unionid);
    }
}
