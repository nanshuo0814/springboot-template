package com.nanshuo.project.utils.redis;

import com.nanshuo.project.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;


/**
 * redis utils 最全工具类
 *
 * @author nanshuo
 * @date 2024/03/28 10:43:01
 */
@Slf4j
@Component
public final class RedisUtils {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 可按自己需求生成"起始时间戳"
     */
    private static final long BEGIN_TIMESTAMP = 1060790400L;

    /**
     * 用于时间戳左移32位
     */
    public static final int MOVE_BITS = 32;

    //=============================common===================================

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     */
    public void expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            log.error("redis set expire error", e);
        }
    }

    /**
     * 指定缓存失效时间(自定义时间单位)
     *
     * @param key  键
     * @param time 时间(秒)
     * @return whether the key has expired
     */
    public boolean expire(String key, long time, TimeUnit unit) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, unit);
            }
            return true;
        } catch (Exception e) {
            log.error("redis set expire error", e);
            return false;
        }
    }

    /**
     * 根据key获取过期时间(默认获取的是秒单位)
     *
     * @param key 键(不能为null)
     * @return the remaining time, "0" means never expire
     */
    public long getExpire(String key) {
        Long time = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        if (time != null) {
            return time;
        }
        return -1L;
    }

    /**
     * 根据key获取过期时间(自定义时间单位)
     *
     * @param key 键(不能为null)
     * @return the remaining time, "0" means never expire
     */
    public long getExpire(String key, TimeUnit unit) {
        Long time = redisTemplate.getExpire(key, unit);
        if (time != null) {
            return time;
        }
        return -1L;
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return whether the key exist
     */
    public boolean hasKey(String key) {
        Boolean flag = redisTemplate.hasKey(key);
        try {
            return Boolean.TRUE.equals(flag);
        } catch (Exception e) {
            log.error("redis hasKey error", e);
            return false;
        }
    }

    /**
     * 删除缓存
     *
     * @param key 键,可以传递一个值或多个
     */
    public void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete(Arrays.asList(key));
            }
        }
    }


    //=============================String===================================

    /**
     * 普通缓存获取(泛型)
     *
     * @param key key键
     * @return the value corresponding the key
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }


    /**
     * 普通缓存获取(泛型)
     *
     * @param key        key键
     * @param targetType 目标类型
     * @param <T>        目标类型参数
     * @return the value corresponding the key and the generic value corresponding the key
     */
    public <T> T get(String key, Class<T> targetType) {
        return key == null ? null : JsonUtils.objParse(redisTemplate.opsForValue().get(key), targetType);
    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return whether true or false
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            log.error("redis set error", e);
            return false;
        }
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) --- time要大于0,如果time小于0,将设置为无期限
     * @return whether true or false
     */
    public boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            log.error("redis set error", e);
            return false;
        }
    }

    /**
     * 普通缓存放入并设置时间和时间单位
     *
     * @param key      键
     * @param value    值
     * @param time     时间(秒) --- time要大于0,如果time小于0,将设置为无期限
     * @param timeUnit 时间单位
     * @return whether true or false
     */
    public boolean set(String key, Object value, long time, TimeUnit timeUnit) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, timeUnit);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            log.error("redis set error", e);
            return false;
        }
    }

    /**
     * 递增
     *
     * @param key   键
     * @param delta 要增加几(大于0)
     * @return the value after increment
     */
    public long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        Long increment = redisTemplate.opsForValue().increment(key, delta);
        return increment != null ? increment : 0L;
    }

    /**
     * 递减
     *
     * @param key   键
     * @param delta 要增加几(小于0)
     * @return the value after decrement
     */
    public long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        Long increment = redisTemplate.opsForValue().increment(key, delta);
        return increment != null ? increment : 0L;
    }

    //=============================Map===================================

    /**
     * 根据hashKey获取hash列表有多少元素
     *
     * @param key 键(hashKey)
     * @return the size of map
     */
    public long hsize(String key) {
        try {
            return redisTemplate.opsForHash().size(key);
        } catch (Exception e) {
            log.error("redis hsize error", e);
            return 0L;
        }
    }

    /**
     * HashGet  根据"项 中的 键 获取列表"
     *
     * @param key  键(hashKey)能为null
     * @param item 项不能为null
     * @return the value of the corresponding key
     */
    public Object hget(String key, String item) {
        return redisTemplate.opsForHash().get(key, item);
    }

    /**
     * 获取HashKey对应的所有键值
     *
     * @param key 键(hashKey)
     * @return 对应的多个键值
     */
    public Map<Object, Object> hmget(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 获取HashKey对应的所有键值
     *
     * @param key       键(hashKey)
     * @param keyType   键类型
     * @param valueType 值类型
     * @param <K>       键类型参数
     * @param <V>       值类型参数
     * @return a map
     */
    public <K, V> Map<K, V> hmget(String key, Class<K> keyType, Class<V> valueType) {
        return JsonUtils.mapParse(redisTemplate.opsForHash().entries(key), keyType, valueType);
    }

    /**
     * HashSet  存入多个键值对
     *
     * @param key 键(hashKey)
     * @param map map 对应多个键值对
     */
    public void hmset(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
        } catch (Exception e) {
            log.error("redis hmset error", e);
        }

    }

    /**
     * HashSet存入并设置时间
     *
     * @param key  键(hashKey)
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return whether true or false
     */
    public boolean hmset(String key, Map<String, Object> map, long time) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("redis hmset error", e);
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键(hashKey)
     * @param item  项
     * @param value 值
     * @return whether true or false
     */
    public boolean hset(String key, String item, Object value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            log.error("redis hset error", e);
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建,并设置有效时间
     *
     * @param key   键(hashKey)
     * @param item  项
     * @param value 值
     * @param time  时间(秒)   注意: 如果已经在hash表有时间,这里将会替换所有的时间
     * @return whether true or false
     */
    public boolean hset(String key, String item, Object value, long time) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("redis hset error", e);
            return false;
        }
    }

    /**
     * 放入map集合数据,如果不存在将创建
     *
     * @param key   键(hashKey)
     * @param value map集合
     * @param <K>   map集合键参数类型
     * @param <V>   map集合值参数类型
     * @return whether true or false
     */
    public <K, V> boolean hsetMap(String key, Map<K, V> value) {
        try {
            redisTemplate.opsForHash().putAll(key, value);
            return true;
        } catch (Exception e) {
            log.error("redis hset error", e);
            return false;
        }
    }

    /**
     * 获取key对应的所有map键值对
     *
     * @param key 键(hashKey)
     * @return the Map
     */
    public Map<Object, Object> hgetMap(String key) {
        try {
            return redisTemplate.opsForHash().entries(key);
        } catch (Exception e) {
            log.error("redis hget error", e);
            return null;
        }
    }


    /**
     * 获取key对应的所有map键值对(泛型)
     *
     * @param key       键(hashKey)
     * @param keyType   键类型
     * @param valueType 值类型
     * @param <K>       键类型参数
     * @param <V>       值类型参数
     * @return the Map
     */
    public <K, V> Map<K, V> hgetMap(String key, Class<K> keyType, Class<V> valueType) {
        try {
            return JsonUtils.mapParse(redisTemplate.opsForHash().entries(key), keyType, valueType);
        } catch (Exception e) {
            log.error("redis hget error", e);
            return null;
        }
    }

    /**
     * 删除hash表中的值
     *
     * @param key  键(hashKey)   不能为null
     * @param item 项可以是多个    不能为null
     */
    public void hdel(String key, Object... item) {
        redisTemplate.opsForHash().delete(key, item);
    }

    /**
     * 判断hash表是否有该项的值
     *
     * @param key  键(hashKey)不能为null
     * @param item 项不能为null
     * @return whether true or false
     */
    public boolean hHasKey(String key, String item) {
        return redisTemplate.opsForHash().hasKey(key, item);
    }

    /**
     * hash递增,如果不存在,就会创建一个,并把新增后的值返回
     *
     * @param key  键(hashKey)
     * @param item 项
     * @param by   要增加几(大于0)
     * @return the value of the corresponding key after increment in one Map
     */
    public double hincr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, by);
    }

    /**
     * hash递减
     *
     * @param key  键(hashKey)
     * @param item 项
     * @param by   要减少几(小于0)
     * @return the value of the corresponding key after decrement in one Map
     */
    public double hdecr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, -by);
    }

    //=============================Set===================================

    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键
     * @return all values in one Set
     */
    public Set<Object> sGet(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            log.error("redis sGet error", e);
            return null;
        }
    }

    /**
     * 根据value从一个Set集合中查询一个值,是否存在
     *
     * @param key   键
     * @param value 值
     * @return whether true or false
     */
    public boolean sHasKey(String key, Object value) {
        try {
            Boolean flag = redisTemplate.opsForSet().isMember(key, value);
            return Boolean.TRUE.equals(flag);
        } catch (Exception e) {
            log.error("redis sHasKey error", e);
            return false;
        }
    }

    /**
     * 将数据放入set缓存
     *
     * @param key    键
     * @param values 值
     * @return the number of adding successfully
     */
    public long sSet(String key, Object... values) {
        try {
            Long nums = redisTemplate.opsForSet().add(key, values);
            return nums != null ? nums : 0L;
        } catch (Exception e) {
            log.error("redis sSet error", e);
            return 0L;
        }
    }

    /**
     * 将set数据放入缓存,并设置有效时间
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值,可以是多个
     * @return the number of adding successfully
     */
    public long sSetAndTime(String key, long time, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if (time > 0) {
                expire(key, time);
            }
            return count != null ? count : 0L;
        } catch (Exception e) {
            log.error("redis sSetAndTime error", e);
            return 0L;
        }
    }

    /**
     * 获取set缓存的长度
     *
     * @param key 键
     * @return the size of the Set
     */
    public long sGetSetSize(String key) {
        try {
            Long size = redisTemplate.opsForSet().size(key);
            return size != null ? size : 0L;
        } catch (Exception e) {
            log.error("redis sGetSetSize error", e);
            return 0L;
        }
    }

    /**
     * 移除值为values的
     *
     * @param key    键
     * @param values 值(可以是多个)
     * @return the number of removal
     */
    public long setRemove(String key, Object... values) {
        try {
            Long nums = redisTemplate.opsForSet().remove(key, values);
            return nums != null ? nums : 0L;
        } catch (Exception e) {
            log.error("redis setRemove error", e);
            return 0;
        }
    }

    //=============================List===================================

    /**
     * 获取list列表数据
     *
     * @param key 键
     * @return all values of one List
     */
    public List<Object> lget(String key) {
        try {
            return redisTemplate.opsForList().range(key, 0, -1);
        } catch (Exception e) {
            log.error("redis lget error", e);
            return null;
        }
    }

    /**
     * /**
     * 获取list列表数据(泛型)
     *
     * @param key        键
     * @param targetType 目标类型
     * @param <T>        目标类型参数
     * @return all values of one List
     */
    public <T> List<T> lget(String key, Class<T> targetType) {
        try {
            return JsonUtils.listParse(redisTemplate.opsForList().range(key, 0, -1), targetType);
        } catch (Exception e) {
            log.error("redis lget error", e);
            return null;
        }
    }

    /**
     * 获取list缓存的长度
     *
     * @param key 键
     * @return the length of the List
     */
    public long lGetListSize(String key) {
        try {
            Long size = redisTemplate.opsForList().size(key);
            return size != null ? size : 0L;
        } catch (Exception e) {
            log.error("redis lGetListSize error", e);
            return 0L;
        }
    }

    /**
     * 通过索引获取list中的值
     *
     * @param key   键
     * @param index 索引 index >= 0 时, 0:表头, 1:第二个元素,以此类推...    index < 0 时, -1:表尾, -2:倒数第二个元素,以此类推
     * @return the value of the specified index in one List
     */
    public Object lgetIndex(String key, long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            log.error("redis lgetIndex error", e);
            return null;
        }
    }

    /**
     * 通过索引获取list中的值(泛型)
     *
     * @param key        键
     * @param index      索引 index >= 0 时, 0:表头, 1:第二个元素,以此类推...    index < 0 时, -1:表尾, -2:倒数第二个元素,以此类推
     * @param targetType 目标类型
     * @param <T>        目标类型参数
     * @return the value of the specified index in one List and the generic value of the specified index in one List
     */
    public <T> T lgetIndex(String key, long index, Class<T> targetType) {
        try {
            return JsonUtils.objParse(redisTemplate.opsForList().index(key, index), targetType);
        } catch (Exception e) {
            log.error("redis lgetIndex error", e);
            return null;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return whether true or false
     */
    public boolean lSet(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            log.error("redis lSet error", e);
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return whether true or false
     */
    public boolean lSet(String key, Object value, long time) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("redis lSet error", e);
            return false;
        }
    }

    /**
     * 将list集合放入缓存
     *
     * @param key    键
     * @param values 值
     * @return whether true or false
     */
    public <T> boolean lSet(String key, List<T> values) {
        try {
            Long nums = redisTemplate.opsForList().rightPushAll(key, values);
            return nums != null;
        } catch (Exception e) {
            log.error("redis lSet error", e);
            return false;
        }
    }

    /**
     * 将list集合放入缓存,并设置有效时间
     *
     * @param key    键
     * @param values 值
     * @param time   时间(秒)
     * @return whether true or false
     */
    public boolean lSet(String key, List<Object> values, long time) {
        try {
            redisTemplate.opsForList().rightPushAll(key, values);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("redis lSet error", e);
            return false;
        }
    }

    /**
     * 根据索引修改list中的某条数据
     *
     * @param key   键
     * @param value 值
     * @param index 索引
     * @return whether true or false
     */
    public boolean lUpdateIndex(String key, Object value, long index) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            log.error("redis lUpdateIndex error", e);
            return false;
        }
    }

    /**
     * 移除N个值为value
     *
     * @param key    键
     * @param value  值
     * @param number 移除多少个
     * @return 返回移除的个数
     */
    public long lRemove(String key, Object value, long number) {
        try {
            Long count = redisTemplate.opsForList().remove(key, number, value);
            return count != null ? count : 0L;
        } catch (Exception e) {
            log.error("redis lRemove error", e);
            return 0L;
        }
    }

    //=============================Lock===================================

    /**
     * 解决缓存加锁问题
     *
     * @param key     锁名称
     * @param value   锁值
     * @param timeout 超时时间
     * @param unit    时间单位
     * @param <T>     锁值的数据类型
     * @return 返回加锁成功状态
     */
    public <T> boolean tryLock(String key, T value, long timeout, TimeUnit unit) {
        Boolean flag = redisTemplate.opsForValue().setIfAbsent(key, value, timeout, unit);
        return Boolean.TRUE.equals(flag);
    }

    /**
     * 解决缓存解锁操作
     *
     * @param key 锁名称
     * @return 返回解锁成功状态
     */
    public boolean unLock(String key) {
        Boolean flag = redisTemplate.delete(key);
        return Boolean.TRUE.equals(flag);
    }

    /**
     * 全局生成唯一ID策略
     * 设计: 符号位(1位) - 时间戳(32位) - 序列号(31位)
     *
     * @param keyPrefix key的前缀
     * @return 返回唯一ID
     */
    public long globalUniqueKey(String keyPrefix) {

        // 1. 生成时间戳
        LocalDateTime now = LocalDateTime.now();

        // 东八区时间
        long nowSecond = now.toEpochSecond(ZoneOffset.UTC);
        // 相减获取时间戳
        long timestamp = nowSecond - BEGIN_TIMESTAMP;

        // 2. 生成序列号(使用日期作为redis自增长超2^64限制,灵活使用年、月、日来存储)
        // 获取当天日期
        String date = now.format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
        // 自增长
        Long increment = redisTemplate.opsForValue().increment("icr:" + keyPrefix + ":" + date);
        long count = increment != null ? increment : 0L;

        // 3. 拼接并返回(使用二进制或运算)
        return timestamp << MOVE_BITS | count;
    }

}