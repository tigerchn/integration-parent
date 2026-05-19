package com.integration.client.wechat;

/**
 * 微信小程序登录：用 {@code wx.login()} 返回的 code 换取 openid。
 */
public interface WeChatMiniProgramClient {

    WeChatMiniProgramSession code2Session(String jsCode);
}
