package com.integration.common.tool.order;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 订单号生成：Snowflake（时间 + 机器 + 序列）经异或、十六进制与随机后缀混淆后输出。
 * <p>机器号默认由本机 IPv4 + MAC 哈希得到（0～31）；请保证 Spring 容器内<strong>单例</strong>使用。
 * 时钟回拨将抛出 {@link IllegalStateException}。
 */
public class OrderNoUtil {

    private static final long EPOCH = 1609459200000L;
    private static final long WORKER_ID_BITS = 5L;
    private static final long SEQUENCE_BITS = 12L;

    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);

    private static final long XOR_SALT = 20250509123456L;
    private static final String CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int RANDOM_LENGTH = 4;

    private final long workerId;
    private long lastTimestamp = -1L;
    private long sequence;

    /**
     * 默认根据本机 IP、MAC 推导 {@code workerId}。
     */
    public OrderNoUtil() {
        this.workerId = initWorkerIdByIpAndMac();
    }

    /**
     * 测试或显式指定机器号（0～31），0 会规范为 1。
     */
    OrderNoUtil(long forcedWorkerId) {
        long id = forcedWorkerId & MAX_WORKER_ID;
        this.workerId = id == 0 ? 1L : id;
    }

    private long initWorkerIdByIpAndMac() {
        try {
            return generateWorkerIdByIpAndMac();
        } catch (Exception e) {
            long w = ThreadLocalRandom.current().nextInt((int) MAX_WORKER_ID + 1);
            return w == 0 ? 1L : w;
        }
    }

    private long generateWorkerIdByIpAndMac() throws Exception {
        String ip = getLocalIp();
        byte[] mac = getLocalMac();
        long hash = 17L;
        hash = hash * 31 + (ip == null ? 0 : ip.hashCode());
        hash = hash * 31 + (mac == null ? 0 : Arrays.hashCode(mac));
        long id = hash & MAX_WORKER_ID;
        return id == 0 ? 1L : id;
    }

    private String getLocalIp() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                Enumeration<InetAddress> addrs = ni.getInetAddresses();
                while (addrs.hasMoreElements()) {
                    InetAddress addr = addrs.nextElement();
                    if (!addr.isLoopbackAddress() && !addr.isLinkLocalAddress() && addr instanceof Inet4Address) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (Exception ignored) {
            // fall through
        }
        return "127.0.0.1";
    }

    private byte[] getLocalMac() {
        try {
            NetworkInterface ni = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
            if (ni != null) {
                return ni.getHardwareAddress();
            }
        } catch (Exception ignored) {
            // fall through
        }
        return new byte[]{1, 2, 3, 4, 5, 6};
    }

    private synchronized long nextSnowflakeId() {
        long now = System.currentTimeMillis();
        if (now < lastTimestamp) {
            throw new IllegalStateException("系统时间回拨，无法生成ID");
        }
        if (now == lastTimestamp) {
            sequence = (sequence + 1) & SEQUENCE_MASK;
            if (sequence == 0) {
                now = waitNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }
        lastTimestamp = now;
        return ((now - EPOCH) << TIMESTAMP_SHIFT) | (workerId << WORKER_ID_SHIFT) | sequence;
    }

    private static long waitNextMillis(long last) {
        long t = System.currentTimeMillis();
        while (t <= last) {
            t = System.currentTimeMillis();
        }
        return t;
    }

    private static String obscureLong(long id) {
        long mixed = id ^ XOR_SALT;
        String hex = Long.toHexString(mixed).toUpperCase();
        return hex + randomSuffix();
    }

    private static String randomSuffix() {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        StringBuilder sb = new StringBuilder(RANDOM_LENGTH);
        for (int i = 0; i < RANDOM_LENGTH; i++) {
            sb.append(CHARS.charAt(rnd.nextInt(CHARS.length())));
        }
        return sb.toString();
    }

    /**
     * @return 混淆后的订单号（十六进制大写 + 4 位随机字母数字）
     */
    public String nextOrderNo() {
        return obscureLong(nextSnowflakeId());
    }

    /**
     * @param bizPrefix 业务前缀，1～16 位字母数字；为空则等同 {@link #nextOrderNo()}
     */
    public String nextOrderNo(String bizPrefix) {
        if (bizPrefix == null || bizPrefix.isBlank()) {
            return nextOrderNo();
        }
        String p = bizPrefix.trim();
        if (p.length() > 16) {
            throw new IllegalArgumentException("bizPrefix length must be <= 16");
        }
        for (int i = 0; i < p.length(); i++) {
            char c = p.charAt(i);
            if (!Character.isLetterOrDigit(c)) {
                throw new IllegalArgumentException("bizPrefix must be alphanumeric: " + bizPrefix);
            }
        }
        return p + obscureLong(nextSnowflakeId());
    }

    /** @return 当前实例的机器号（0～31），便于排查冲突 */
    public long getWorkerId() {
        return workerId;
    }
}
