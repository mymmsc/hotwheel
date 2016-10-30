/**
 *
 */
package org.mymmsc.j2ee.http;

import org.mymmsc.api.redis.RedisApi;
import org.mymmsc.api.redis.RedisSyncMethod;

/**
 * @author WangFeng
 */
public abstract class HttpSyncObject implements RedisSyncMethod {

    public Object exec(RedisApi api, String keyLock, int timeout) {
        return api.exec(keyLock, timeout, this);
    }
}
