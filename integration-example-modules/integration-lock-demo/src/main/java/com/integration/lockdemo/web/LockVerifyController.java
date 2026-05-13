package com.integration.lockdemo.web;

import com.integration.common.core.api.ApiResult;
import com.integration.lockdemo.lock.LockVerifyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Postman 测试入口：对 {@code /verify} <strong>并发</strong>发两笔 GET（同一锁 key），第二笔应在约 2s 后返回 409（抢锁超时）。
 * <p>若<strong>串行</strong>调用（等上一笔结束再发下一笔），则每笔约 5s 且均为 200，不会抛异常。
 */
@RestController
@RequestMapping("/api/lock-demo")
public class LockVerifyController {

    private final LockVerifyService lockVerifyService;

    public LockVerifyController(LockVerifyService lockVerifyService) {
        this.lockVerifyService = lockVerifyService;
    }


    @GetMapping("/verify")
    public ApiResult<Void> verify() {
        lockVerifyService.transfer("resourceKey");
        return ApiResult.ok();
    }
}
