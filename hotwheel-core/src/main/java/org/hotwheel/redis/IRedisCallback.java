package org.hotwheel.redis;

import redis.clients.jedis.ShardedJedis;

/**
 * Redis回调方法
 *
 * @author wangfeng
 * @since 2016年3月9日 上午7:13:02
 * @remark 不需要释放资源, 关注业务就好
 */
public abstract interface IRedisCallback<T> {

    public T exec(final ShardedJedis jedis, String key);

}
