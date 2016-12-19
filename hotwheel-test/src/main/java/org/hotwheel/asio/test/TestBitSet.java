package org.hotwheel.asio.test;

import java.util.BitSet;

/**
 * 测试BitSet
 *
 * Created by wangfeng on 2016/12/19.
 * @since 2.1.7
 */
public class TestBitSet {

    public static void main(String[] args) {
        int total = 10000;
        BitSet bitSet = new BitSet(total);
        bitSet.set(99);
        int len = bitSet.length();
        int num = 0;
        for (int i = 0; i < total; i++) {
            if (bitSet.get(i)) {
                num ++;
            }
        }
        System.out.println("len = " + len);
        System.out.println("num = " + num);
    }
}
