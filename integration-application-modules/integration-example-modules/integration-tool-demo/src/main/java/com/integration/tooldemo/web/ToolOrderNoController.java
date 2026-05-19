package com.integration.tooldemo.web;

import com.integration.common.core.api.ApiResult;
import com.integration.common.tool.order.OrderNoUtil;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单号生成测试：调用容器中的 {@link OrderNoUtil} Bean。
 */
@RestController
@RequestMapping("/api/tool-demo")
public class ToolOrderNoController {

    private static final int MAX_BATCH = 100;

    private final OrderNoUtil orderNoUtil;

    public ToolOrderNoController(OrderNoUtil orderNoUtil) {
        this.orderNoUtil = orderNoUtil;
    }

    /**
     * 生成一笔订单号；可选业务前缀（字母数字，最长 16）。
     *
     * @param prefix 可选，为空则无前缀
     * @return data 为订单号字符串
     */
    @GetMapping("/order-no")
    public ApiResult<String> nextOrderNo(@RequestParam(value = "prefix", required = false) String prefix) {
        if (prefix == null || prefix.isBlank()) {
            return ApiResult.ok(orderNoUtil.nextOrderNo());
        }
        return ApiResult.ok(orderNoUtil.nextOrderNo(prefix));
    }

    /**
     * 批量生成订单号（同一 JVM 单例序列递增，便于观察唯一性）。
     *
     * @param count 1～100，默认 10
     * @param prefix 可选业务前缀
     * @return data 含 workerId、count、orderNos
     */
    @GetMapping("/order-nos")
    public ApiResult<Map<String, Object>> nextOrderNos(
            @RequestParam(value = "count", defaultValue = "10") int count,
            @RequestParam(value = "prefix", required = false) String prefix) {
        if (count < 1 || count > MAX_BATCH) {
            throw new IllegalArgumentException("count must be between 1 and " + MAX_BATCH);
        }
        List<String> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            list.add(prefix == null || prefix.isBlank() ? orderNoUtil.nextOrderNo() : orderNoUtil.nextOrderNo(prefix));
        }
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("workerId", orderNoUtil.getWorkerId());
        body.put("count", count);
        body.put("orderNos", list);
        return ApiResult.ok(body);
    }

    /**
     * 当前实例 Snowflake workerId（0～31），便于排查多机冲突。
     */
    @GetMapping("/worker-id")
    public ApiResult<Long> workerId() {
        return ApiResult.ok(orderNoUtil.getWorkerId());
    }
}
