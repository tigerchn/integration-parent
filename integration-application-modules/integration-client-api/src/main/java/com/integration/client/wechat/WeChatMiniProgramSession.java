package com.integration.client.wechat;

/**
 * 微信小程序 {@code jscode2session} 成功结果（不含 session_key 对外暴露）。
 */
public record WeChatMiniProgramSession(String openid, String unionid) {
}
