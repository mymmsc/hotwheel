package org.hotwheel.redis;

import org.hotwheel.adapter.BaseObject;
import org.hotwheel.assembly.Api;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Redis API
 *
 * @author WangFeng
 * @version 5.3.14
 */
public class RedisApi extends BaseObject {
    /**
     * 同步执行成功状态
     */
    public static final String SUCCESS = "OK";
    public static final int GET = 1;
    public static final int SET = 2;
    // 加锁标志
    private static final String LOCKED = "TRUE";
    private static final long ONE_MILLI_NANOS = 1000000L;
    // 默认超时时间（毫秒）
    @SuppressWarnings("unused")
    private static final long DEFAULT_TIME_OUT = 3000;
    private static final Random r = new Random();
    // 锁的超时时间（秒），过期删除
    private static final int EXPIRE = 5 * 60;
    //private static RedisApi instance = null;
    //private static String dbHost = "redis.api.mymmsc.org";
    private String host = "127.0.0.1";
    private int port = 6379;
    // 锁状态标志
    //private boolean locked = false;
    //private String keyLock = null;
    private String name = null;
    private int databse = 0;
    private String auth = null;
    private ShardedJedisPool redisPool = null;

    public RedisApi(String hostname, int hostport, String name, int db, String pswd, RedisPoolConfig redisConfig) {
        this.host = hostname;
        this.port = hostport;
        this.name = name;
        this.databse = db;
        this.auth = pswd;

        if (redisConfig == null) {
            redisConfig = new RedisPoolConfig();
        }
        JedisPoolConfig conf = redisConfig.getConfig();

        JedisShardInfo info = null;
        // 作为默认参数, 其实是一段废代码, 以备后用
        int timeout = (int)redisConfig.getTimeout();
        if (timeout < 2000) {
            timeout = 2000;
        }
        if (!Api.isEmpty(host)) {
            host = host.trim();
        }
        if (!Api.isEmpty(this.name)) {
            this.name = this.name.trim();
        }
        if (!Api.isEmpty(auth)) {
            auth = auth.trim();
        }
        if (Api.isEmpty(this.name)) {
            info = new JedisShardInfo(host, port, timeout);
        } else {
            info = new JedisShardInfo(host, port, timeout, this.name);
        }

        if (!Api.isEmpty(auth)) {
            info.setPassword(auth);
        }
        if( databse > 0) {
            Api.setValue(info, "db", databse);
        }
        //info.setTimeout(timeout);
        info.setSoTimeout(timeout);
        List<JedisShardInfo> list = new ArrayList<JedisShardInfo>();
        list.add(info);
        redisPool = new ShardedJedisPool(conf, list);
    }

    /**
     * 初始化
     *
     * @param hostname 主机名
     * @param hostport 主机端口
     */
    public static synchronized RedisApi getInstance(String hostname,
                                                    int hostport, String name, String pswd) {
        RedisApi instance = null;
        if (instance == null) {
            instance = new RedisApi(hostname, hostport, name, 0, pswd, null);
        }
        return instance;
    }

    public static synchronized RedisApi getInstance(String hostname, int hostport, int db, String pswd) {
        RedisApi instance = null;
        if (instance == null) {
            instance = new RedisApi(hostname, hostport, null, db, pswd, null);
        }
        return instance;
    }

    public static synchronized RedisApi getInstance(String host, int port, int db, String pswd, RedisPoolConfig conf) {
        return new RedisApi(host, port, null, db, pswd, conf);
    }

    /**
     * 执行未收录的redis的方法
     *
     * @param key
     * @param callback
     * @return
     */
    public <T> T command(final String key, IRedisCallback<T> callback) {
        T bRet = null;
        ShardedJedis jedis = null;
        try {
            jedis = redisPool.getResource();
            bRet = callback.exec(jedis, key);
        } catch (Exception e) {
            logger.error("[host=" + host + ",port=" + port +"] command error", e);
        } finally {
            releaseResource(jedis);
        }
        return bRet;
    }

    /**
     * 缓存KV, 并设置超时秒数
     *
     * @param key
     * @param seconds
     * @param value
     * @return
     */
    public String set(String key, int seconds, String value) {
        String sRet = null;
        ShardedJedis jedis = null;
        try {
            jedis = redisPool.getResource();
            sRet = jedis.setex(key, seconds, value);
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            releaseResource(jedis);
        }
        return sRet;
    }

    /**
     * 保存一个对象
     *
     * @param key
     * @param value
     * @return
     */
    public String set(String key, String value) {
        String sRet = null;
        ShardedJedis jedis = null;
        try {
            jedis = redisPool.getResource();
            sRet = jedis.set(key, value);
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            releaseResource(jedis);
        }
        return sRet;
    }

    /**
     * 获取一个对象
     *
     * @param key
     * @return
     */
    public String get(String key) {
        String value = null;
        ShardedJedis jedis = null;
        try {
            jedis = redisPool.getResource();
            value = jedis.get(key);
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            releaseResource(jedis);
        }
        return value;
    }

    /**
     * 删除一个对象
     *
     * @param key
     */
    public boolean delete(String key) {
        boolean bRet = false;
        ShardedJedis jedis = null;
        try {
            jedis = redisPool.getResource();
            jedis.del(key);
            bRet = true;
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            releaseResource(jedis);
        }
        return bRet;
    }

    /**
     * 执行同步事务方法
     *
     * @param key
     * @param timeout
     * @param method
     * @return
     */
    public Object exec(String key, int timeout, RedisSyncMethod method) {
        Object obj = null;
        boolean bLocked = false;
        String keyLock = key + "_lock";
        ShardedJedis jedis = redisPool.getResource();
        try {
            bLocked = lock(jedis, keyLock, timeout * 1000);
            if (bLocked) {
                obj = method.service(jedis);
            }
        } catch (Exception e) {
            logger.error("执行任务失败", e);
        } finally {
            unlock(jedis, keyLock, bLocked);
        }

        return obj;
    }

    public boolean lock(ShardedJedis jedis, String keyLock, long timeout) {
        boolean bLocked = false;
        long nano = System.nanoTime();
        timeout *= ONE_MILLI_NANOS;
        //keyLock = Api.o3String(128);
        try {
            while ((System.nanoTime() - nano) < timeout) {
                if (jedis.setnx(keyLock, LOCKED) == 1) {
                    jedis.expire(keyLock, EXPIRE);
                    bLocked = true;
                    break;
                }
                // 短暂休眠，nano避免出现活锁
                Thread.sleep(3, r.nextInt(500));
            }
        } catch (Exception e) {
            //
        }
        return bLocked;
    }

    // 无论是否加锁成功，必须调用
    public void unlock(ShardedJedis jedis, String keyLock, boolean locked) {
        try {
            if (locked) {
                jedis.del(keyLock);
            }
        } finally {
            releaseResource(jedis);
        }
    }

    public void releaseResource(ShardedJedis jedis) {
        if(jedis != null) {
            //redisPool.returnResource(jedis);
            jedis.close();
        }
    }

    @Override
    public void close() {
        redisPool.close();
    }
}
