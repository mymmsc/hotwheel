/**
 *
 */
package org.hotwheel.redis;

import redis.clients.jedis.ShardedJedis;

/**
 * HTTP同步方法
 *
 * @author WangFeng
 * @since 1.0.0
 */
public abstract interface RedisSyncMethod {
    public abstract Object service(ShardedJedis redis);
}
