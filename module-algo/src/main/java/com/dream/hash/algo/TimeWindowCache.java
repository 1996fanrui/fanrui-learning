package com.dream.hash.algo;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

/**
 * LRU cache 是通过 数据 条数来控制 cache 的淘汰策略。这里希望通过 时间来控制 cache 的淘汰策略。
 * - cache size 变成了 window size（Time window，例如：只缓存最近一个小时的数据）
 * - value 是 long 类型，增加一个接口，返回所有 value 的平均值
 * - timestamp 使用系统时间即可
 */
public class TimeWindowCache {

    private final long windowSizeMs;
    private final Map<String, Node> map;

    // All Nodes are sorted by timestamp, head is the latest data, and tail is the oldest data.
    private final Node head;
    private final Node tail;

    // For get avg
    private long sum = 0;
    private long count = 0;

    // Use clock is easy to debug or test.
    private Clock clock;

    private static class Node {
        String key;
        long value;
        long timestamp;
        Node previous;
        Node next;
    }

    public TimeWindowCache(long windowSizeMs) {
        this.windowSizeMs = windowSizeMs;
        this.map = new HashMap<>();
        this.head = new Node();
        this.tail = new Node();
        head.next = tail;
        tail.previous = head;

        this.clock = Clock.systemDefaultZone();
    }

    public void put(String key, long value) {
        expire();
        Node node = map.get(key);
        if (node == null) {
            node = new Node();
            node.key = key;
            map.put(key, node);
        } else {
            // remove from deque
            node.previous.next = node.next;
            node.next.previous = node.previous;
            sum -= node.value;
            count--;
        }
        node.value = value;
        node.timestamp = clock.millis();
        sum += node.value;
        count++;

        // insert to head
        node.next = head.next;
        node.next.previous = node;
        head.next = node;
        node.previous = head;
    }

    public long get(String key) {
        expire();
        Node node = map.get(key);
        if (node == null) {
            return -1;
        }
        return node.value;
    }

    public double getAverage() {
        expire();
        if (count == 0) {
            return Double.NaN;
        }
        return 1.0 * sum / count;
    }

    private void expire() {
        Node cur = tail.previous;
        final long curMs = clock.millis();
        while (cur != head && cur.timestamp + windowSizeMs < curMs) {
            // remove cur from map
            map.remove(cur.key);
            // update the sum and count
            sum -= cur.value;
            count--;

            cur = cur.previous;

            // remove cur from Deque
            cur.next = tail;
            tail.previous = cur;
        }
    }

    // Only for test
    private void setClock(Clock clock) {
        this.clock = clock;
    }

    public static void main(String[] args) {
        TimeWindowCache windowCache = new TimeWindowCache(10_000);
        checkState(Double.isNaN(windowCache.getAverage()));

        final Instant now = Instant.now();
        windowCache.setClock(Clock.fixed(now, ZoneId.systemDefault()));

        windowCache.put("foo", 42);
        checkState(windowCache.get("foo") == 42);
        checkState(windowCache.getAverage() == 42);

        // plus 5 seconds
        windowCache.setClock(Clock.fixed(now.plusMillis(5000), ZoneId.systemDefault()));

        windowCache.put("bar", 30);
        checkState(windowCache.get("bar") == 30);
        checkState(windowCache.get("foo") == 42);
        checkState(windowCache.getAverage() == 36);

        // plus 7 seconds
        windowCache.setClock(Clock.fixed(now.plusMillis(7000), ZoneId.systemDefault()));

        windowCache.put("foo", 22);
        checkState(windowCache.get("bar") == 30);
        checkState(windowCache.get("foo") == 22);
        checkState(windowCache.getAverage() == 26);

        // plus 15 seconds
        windowCache.setClock(Clock.fixed(now.plusMillis(15000), ZoneId.systemDefault()));

        checkState(windowCache.get("bar") == 30);
        checkState(windowCache.get("foo") == 22);
        checkState(windowCache.getAverage() == 26);

        // plus 15001 MS, the bar is expired.
        windowCache.setClock(Clock.fixed(now.plusMillis(15001), ZoneId.systemDefault()));

        checkState(windowCache.get("bar") == -1);
        checkState(windowCache.get("foo") == 22);
        checkState(windowCache.getAverage() == 22);

        // plus 17000 MS, the bar is expired, and foo is not expired
        windowCache.setClock(Clock.fixed(now.plusMillis(17000), ZoneId.systemDefault()));

        checkState(windowCache.get("bar") == -1);
        checkState(windowCache.get("foo") == 22);
        checkState(windowCache.getAverage() == 22);

        // plus 17000 MS, both of the bar and foo are expired.
        windowCache.setClock(Clock.fixed(now.plusMillis(17001), ZoneId.systemDefault()));

        checkState(windowCache.get("bar") == -1);
        checkState(windowCache.get("foo") == -1);
        checkState(Double.isNaN(windowCache.getAverage()));
    }

    private static void checkState(boolean state) {
        if (state) {
            return;
        }
        throw new IllegalStateException();
    }
}
