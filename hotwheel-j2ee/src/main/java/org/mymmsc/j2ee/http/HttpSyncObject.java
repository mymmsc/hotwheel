/**
 *
 */
package org.mymmsc.j2ee.http;

import org.hotwheel.redis.RedisApi;
import org.hotwheel.redis.RedisSyncMethod;

/**
 * @author WangFeng
 */
public abstract class HttpSyncObject implements RedisSyncMethod {

    public Object exec(RedisApi api, String keyLock, int timeout) {
        return api.exec(keyLock, timeout, this);
    }
}
