package com.integration.common.tool.order;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderNoUtilTest {

    @Test
    void nextOrderNo_singleThread_unique() {
        OrderNoUtil util = new OrderNoUtil(3L);
        Set<String> seen = new HashSet<>();
        for (int i = 0; i < 20_000; i++) {
            String no = util.nextOrderNo();
            assertThat(seen.add(no)).as("duplicate at i=%d no=%s", i, no).isTrue();
        }
    }

    @Test
    void nextOrderNo_concurrent_unique() throws Exception {
        OrderNoUtil util = new OrderNoUtil(7L);
        int threads = 24;
        int perThread = 4_000;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        Set<String> global = new HashSet<>();
        @SuppressWarnings("unchecked")
        Future<Set<String>>[] futures = new Future[threads];
        for (int t = 0; t < threads; t++) {
            futures[t] = pool.submit(() -> {
                start.await();
                Set<String> local = new HashSet<>();
                for (int i = 0; i < perThread; i++) {
                    local.add(util.nextOrderNo());
                }
                return local;
            });
        }
        start.countDown();
        for (Future<Set<String>> f : futures) {
            for (String no : f.get()) {
                assertThat(global.add(no)).as("duplicate no=%s", no).isTrue();
            }
        }
        pool.shutdown();
        assertThat(global).hasSize(threads * perThread);
    }

    @Test
    void nextOrderNo_withPrefix() {
        OrderNoUtil util = new OrderNoUtil(2L);
        String a = util.nextOrderNo("SO");
        String b = util.nextOrderNo("SO");
        assertThat(a).startsWith("SO");
        assertThat(b).startsWith("SO");
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void invalidPrefix() {
        OrderNoUtil util = new OrderNoUtil(1L);
        assertThatThrownBy(() -> util.nextOrderNo("SO-1")).isInstanceOf(IllegalArgumentException.class);
    }
}
