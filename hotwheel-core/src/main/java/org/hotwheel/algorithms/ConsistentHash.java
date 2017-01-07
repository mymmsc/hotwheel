/**
 *
 */
package org.hotwheel.algorithms;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 一致性哈希算法
 *
 * @author WangFeng
 * @date 2015年1月1日 下午5:27:41
 */
public class ConsistentHash<T> {
    /**
     * 每个机器节点关联的虚拟节点个数
     */
    private int numberOfReplicas = 100;
    // S类封装了机器节点的信息 , 如name, password, ip, port等
    /**
     * 虚拟节点
     */
    private TreeMap<Long, T> vnodes = null;
    /**
     * 真实机器节点
     */
    private List<T> peers = null;

    public ConsistentHash(List<T> peers, int numberOfReplicas) {
        super();
        if (numberOfReplicas > 4096) {
            this.numberOfReplicas = 4096;
        } else if (numberOfReplicas < 1) {
            this.numberOfReplicas = 1;
        } else {
            this.numberOfReplicas = numberOfReplicas;
        }
        this.peers = peers;
        init();
    }

    public ConsistentHash(int numberOfReplicas) {
        this(new ArrayList<T>(), numberOfReplicas);
    }

    public void addNode(T node) {
        peers.add(node);
        for (int n = 0; n < numberOfReplicas; n++) {
            // 一个真实机器节点关联NODE_NUM个虚拟节点
            String key = String.format("%s-%04d", node.toString(), n);
            // System.out.println(key);
            vnodes.put(hash(key), node);
        }
    }

    public void delNode(T node) {
        peers.add(node);
        for (int n = 0; n < numberOfReplicas; n++) {
            // 一个真实机器节点关联NODE_NUM个虚拟节点
            String key = String.format("%s-%04d", node.toString(), n);
            // System.out.println(key);
            vnodes.remove(hash(key));
        }
    }

    /**
     * 初始化一致性hash环
     */
    private void init() {
        vnodes = new TreeMap<Long, T>();
        // 每个真实机器节点都需要关联虚拟节点
        for (int i = 0; i != peers.size(); ++i) {
            final T shardInfo = peers.get(i);
            addNode(shardInfo);
        }
    }

    public T getShardInfo(String key) {
        // 沿环的顺时针找到一个虚拟节点
        SortedMap<Long, T> tail = vnodes.tailMap(hash(key));
        if (tail.size() == 0) {
            return vnodes.get(vnodes.firstKey());
        }
        // 返回该虚拟节点对应的真实机器节点的信息
        return tail.get(tail.firstKey());
    }

    /**
     * MurMurHash算法, 是非加密HASH算法, 性能很高, 比传统的CRC32,MD5, SHA-1（这两个算法都是加密HASH算法,
     * 复杂度本身就很高, 带来的性能上的损害也不可避免） 等HASH算法要快很多, 而且据说这个算法的碰撞率很低.
     *
     * @see <url>http://murmurhash.googlepages.com/</url>
     */
    @SuppressWarnings("unused")
    private Long hash64(String key) {

        ByteBuffer buf = ByteBuffer.wrap(key.getBytes());
        int seed = 0x1234ABCD;

        ByteOrder byteOrder = buf.order();
        buf.order(ByteOrder.LITTLE_ENDIAN);

        long m = 0xc6a4a7935bd1e995L;
        int r = 47;

        long h = seed ^ (buf.remaining() * m);

        long k;
        while (buf.remaining() >= 8) {
            k = buf.getLong();

            k *= m;
            k ^= k >>> r;
            k *= m;

            h ^= k;
            h *= m;
        }

        if (buf.remaining() > 0) {
            ByteBuffer finish = ByteBuffer.allocate(8).order(
                    ByteOrder.LITTLE_ENDIAN);
            // for big-endian version, do this first:
            // finish.position(8-buf.remaining());
            finish.put(buf).rewind();
            h ^= finish.getLong();
            h *= m;
        }

        h ^= h >>> r;
        h *= m;
        h ^= h >>> r;

        buf.order(byteOrder);
        return h;
    }

    private long hash(String key) {
        int hash = 0;
        byte data[] = key.getBytes();
        int len = data.length;
        int k;
        int seed = 0;
        int m = 0x5bd1e995;
        int r1 = 13;
        int r2 = 15;
        hash = seed ^ len;
        int pos = 0;
        while (len >= 4) {
            k = data[pos + 0];
            k |= data[pos + 1] << 8;
            k |= data[pos + 2] << 16;
            k |= data[pos + 3] << 24;

            k *= m;
            k ^= k >> 24;
            k *= m;

            hash *= m;
            hash ^= k;

            pos += 4;
            len -= 4;
        }

        switch (len) {
            case 3:
                hash ^= data[pos + 2] << 16;
            case 2:
                hash ^= data[pos + 1] << 8;
            case 1:
                hash ^= data[pos + 0];
                hash *= m;
        }

        hash ^= hash >> r1;
        hash *= m;
        hash ^= hash >> r2;
        // System.out.println("s=" + key + ", hash=" +hash);
        // hash &= 0x00000000FFFFFFFFL;
        return hash;
    }
}
