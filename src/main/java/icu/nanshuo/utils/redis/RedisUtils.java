package icu.nanshuo.utils.redis;

import icu.nanshuo.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * redis 最全工具类
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/03/28
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

    // region Base

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
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 根据key获取过期时间(自定义时间单位)
     *
     * @param key 键(不能为null)
     * @return the remaining time, "0" means never expire
     */
    public long getExpire(String key, TimeUnit unit) {
        return redisTemplate.getExpire(key, unit);
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
        Long increment = redisTemplate.opsForValue().decrement(key, delta);
        return increment != null ? increment : 0L;
    }

    // endregion

    // region Hash

    /**
     * 根据hashKey获取hash列表有多少元素
     *
     * @param key 键(hashKey)
     * @return the size of map
     */
    public long hashSize(String key) {
        try {
            return redisTemplate.opsForHash().size(key);
        } catch (Exception e) {
            log.error("redis hashSize error", e);
            return 0L;
        }
    }

    /**
     * HashGet  根据"项 中的 键 获取列表"
     *
     * @param key  键能为null
     * @param item 项不能为null
     * @return the value of the corresponding key
     */
    public Object hashGet(String key, String item) {
        return redisTemplate.opsForHash().get(key, item);
    }

    /**
     * HashSet存入并设置时间
     *
     * @param key  键(hashKey)
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return whether true or false
     */
    public boolean hashPut(String key, Map<String, Object> map, long time) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("redis hashPut error", e);
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
    public boolean hashPut(String key, String item, Object value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            log.error("redis hashPut error", e);
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
    public boolean hashPut(String key, String item, Object value, long time) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("redis hashPut error", e);
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
    public <K, V> boolean hashPut(String key, Map<K, V> value) {
        try {
            redisTemplate.opsForHash().putAll(key, value);
            return true;
        } catch (Exception e) {
            log.error("redis hashPut error", e);
            return false;
        }
    }

    /**
     * 获取key对应的所有map键值对
     *
     * @param key 键(hashKey)
     * @return the Map
     */
    public Map<Object, Object> hashGetMap(String key) {
        try {
            return redisTemplate.opsForHash().entries(key);
        } catch (Exception e) {
            log.error("redis hashPut error", e);
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
    public <K, V> Map<K, V> hashGetMap(String key, Class<K> keyType, Class<V> valueType) {
        try {
            return JsonUtils.mapParse(redisTemplate.opsForHash().entries(key), keyType, valueType);
        } catch (Exception e) {
            log.error("redis hashGetMap error", e);
            return null;
        }
    }

    /**
     * 删除hash表中的值
     *
     * @param key  键(hashKey)   不能为null
     * @param item 项可以是多个    不能为null
     */
    public void hashDelete(String key, Object... item) {
        redisTemplate.opsForHash().delete(key, item);
    }

    /**
     * 判断hash表是否有该项的值
     *
     * @param key  键(hashKey)不能为null
     * @param item 项不能为null
     * @return whether true or false
     */
    public boolean hashHasKey(String key, String item) {
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
    public double hashIncr(String key, String item, double by) {
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
    public double hashDecr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, -by);
    }

    // endregion

    // region Set

    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键
     * @return all values in one Set
     */
    public Set<Object> sGetAllValues(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            log.error("redis sGetAllValues error", e);
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
    public long sAdd(String key, Object... values) {
        try {
            Long nums = redisTemplate.opsForSet().add(key, values);
            return nums != null ? nums : 0L;
        } catch (Exception e) {
            log.error("redis sAdd error", e);
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
    public long sAddAndTime(String key, long time, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if (time > 0) {
                expire(key, time);
            }
            return count != null ? count : 0L;
        } catch (Exception e) {
            log.error("redis sAddAndTime error", e);
            return 0L;
        }
    }

    /**
     * 获取set缓存的长度
     *
     * @param key 键
     * @return the size of the Set
     */
    public long sGetSize(String key) {
        try {
            Long size = redisTemplate.opsForSet().size(key);
            return size != null ? size : 0L;
        } catch (Exception e) {
            log.error("redis sGetSize error", e);
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
    public long sRemoveValues(String key, Object... values) {
        try {
            Long nums = redisTemplate.opsForSet().remove(key, values);
            return nums != null ? nums : 0L;
        } catch (Exception e) {
            log.error("redis sRemoveValues error", e);
            return 0;
        }
    }

    /**
     * 根据key获取Sorted Set中的所有值
     *
     * @param key 键
     * @return all values in Sorted Set
     */
    public Set<Object> zSetGetAllValues(String key) {
        try {
            return redisTemplate.opsForZSet().range(key, 0, -1);
        } catch (Exception e) {
            log.error("redis zSetGetAllValues error", e);
            return null;
        }
    }

    /**
     * 根据value从一个Sorted Set集合中查询一个值是否存在
     *
     * @param key   键
     * @param value 值
     * @return whether true or false
     */
    public boolean zSetHasKey(String key, Object value) {
        try {
            Double score = redisTemplate.opsForZSet().score(key, value);
            return score != null;
        } catch (Exception e) {
            log.error("redis zSetHasKey error", e);
            return false;
        }
    }

    /**
     * 将数据放入Sorted Set缓存，若值存在，会替换掉
     * 为 values 全部设置相同的 score 分数，并合成一个 Set
     *
     * @param key    键
     * @param score  分数
     * @param values 值
     * @return the number of adding successfully
     */
    public boolean zSetAdd(String key, double score, Object... values) {
        try {
            return Boolean.TRUE.equals(redisTemplate.opsForZSet().add(key, values, score));
        } catch (Exception e) {
            log.error("redis zSetAdd error", e);
            return false;
        }
    }

    /**
     * 将数据放入Sorted Set缓存，若值存在，会替换掉
     * 为 values 里的值分别设置不同等等 score 分数
     *
     * @param key    钥匙
     * @param values 数值
     * @return boolean
     */
    public boolean zSetAddCustom(String key, Map<Object, Double> values) {
        try {
            // 创建一个 Set 存储 TypedTuple
            Set<ZSetOperations.TypedTuple<Object>> tuples = new HashSet<>();
            // 遍历 Map 添加到 Set
            for (Map.Entry<Object, Double> entry : values.entrySet()) {
                tuples.add(new DefaultTypedTuple<>(entry.getKey(), entry.getValue()));
            }
            // 添加到 Redis，会覆盖原值的 score 分数，有新的 value 也会添加进去
            Long add = redisTemplate.opsForZSet().add(key, tuples);
            // add 的值变化是根据 value 的修改或添加新的 value，即使 score 修改了或覆盖了旧值，也不会影响 add 的值（不会 add + 1 )
            return add != null && add > 0L;
        } catch (Exception e) {
            log.error("redis zSetAdd error", e);
            return false;
        }
    }

    /**
     * 将数据放入Sorted Set缓存，若值不存在，则设置添加，反之保留原值
     * 为 values 全部设置相同的 score 分数，并合成一个 Set
     *
     * @param key    钥匙
     * @param score  得分
     * @param values 数值
     * @return boolean
     */
    public boolean zSetAddIfAbsent(String key, double score, Object... values) {
        try {
            return Boolean.TRUE.equals(redisTemplate.opsForZSet().addIfAbsent(key, values, score));
        } catch (Exception e) {
            log.error("redis zSetAdd error", e);
            return false;
        }
    }

    /**
     * 将数据放入Sorted Set缓存，若值不存在，则设置添加，反之保留原值
     * 根据 values key 对应的 value（score）分别设置不同的分数
     *
     * @param key    钥匙
     * @param values 数值
     * @return boolean
     */
    public boolean zSetAddIfAbsentCustom(String key, Map<Object, Double> values) {
        try {
            // 创建一个 Set 存储 TypedTuple
            Set<ZSetOperations.TypedTuple<Object>> tuples = new HashSet<>();
            // 遍历 Map 添加到 Set
            for (Map.Entry<Object, Double> entry : values.entrySet()) {
                tuples.add(new DefaultTypedTuple<>(entry.getKey(), entry.getValue()));
            }
            // 添加到 Redis，不会覆盖原值的 score 分数，有新的 value 会添加进去
            Long add = redisTemplate.opsForZSet().addIfAbsent(key, tuples);
            // add 的值变化是根据 value 的修改或添加新的 value，即使 score 修改了或覆盖了旧值，也不会影响 add 的值（不会 add + 1 )
            return add != null && add > 0L;
        } catch (Exception e) {
            log.error("redis zSetAdd error", e);
            return false;
        }
    }

    /**
     * 获取Sorted Set缓存的长度
     *
     * @param key 键
     * @return the size of the Sorted Set
     */
    public long zSetGetSize(String key) {
        try {
            Long size = redisTemplate.opsForZSet().size(key);
            return size != null ? size : 0L;
        } catch (Exception e) {
            log.error("redis zSetGetSize error", e);
            return 0L;
        }
    }

    /**
     * 移除Sorted Set中指定值的元素
     *
     * @param key    键
     * @param values 值(可以是多个)
     * @return the number of removal
     */
    public long zSetRemoveValues(String key, Object... values) {
        try {
            Long nums = redisTemplate.opsForZSet().remove(key, values);
            return nums != null ? nums : 0L;
        } catch (Exception e) {
            log.error("redis zSetRemoveValues error", e);
            return 0;
        }
    }

    /**
     * 根据分数获取Sorted Set中指定范围的元素
     *
     * @param key    键
     * @param min    最小分数
     * @param max    最大分数
     * @return Sorted Set中指定范围内的元素
     */
    public Set<Object> zSetRangeByScore(String key, double min, double max) {
        try {
            return redisTemplate.opsForZSet().rangeByScore(key, min, max);
        } catch (Exception e) {
            log.error("redis zSetRangeByScore error", e);
            return null;
        }
    }

    /**
     * 根据分数获取Sorted Set中指定范围内的元素（按分数升序排序）
     *
     * @param key    键
     * @param min    最小分数
     * @param max    最大分数
     * @param offset 起始位置
     * @param count  获取数量
     * @return Sorted Set中指定范围内的元素
     */
    public Set<Object> zSetRangeByScore(String key, double min, double max, long offset, long count) {
        try {
            return redisTemplate.opsForZSet().rangeByScore(key, min, max, offset, count);
        } catch (Exception e) {
            log.error("redis zSetRangeByScore with offset and count error", e);
            return null;
        }
    }

    /**
     * 获取Sorted Set中指定元素的分数
     *
     * @param key   键
     * @param value 值
     * @return the score of the value
     */
    public Double zSetGetScore(String key, Object value) {
        try {
            return redisTemplate.opsForZSet().score(key, value);
        } catch (Exception e) {
            log.error("redis zSetGetScore error", e);
            return null;
        }
    }

    /**
     * 获取Sorted Set中指定索引范围的元素（按分数升序排序）
     *
     * @param key    键
     * @param start  起始索引
     * @param end    结束索引
     * @return Sorted Set中指定范围内的元素
     */
    public Set<Object> zSetRange(String key, long start, long end) {
        try {
            return redisTemplate.opsForZSet().range(key, start, end);
        } catch (Exception e) {
            log.error("redis zSetRange error", e);
            return null;
        }
    }

    /**
     * 获取Sorted Set中指定索引范围的元素及其分数（按分数升序排序）
     *
     * @param key    键
     * @param start  起始索引
     * @param end    结束索引
     * @return Sorted Set中指定范围内的元素及其分数
     */
    public Set<ZSetOperations.TypedTuple<Object>> zSetRangeWithScores(String key, long start, long end) {
        try {
            return redisTemplate.opsForZSet().rangeWithScores(key, start, end);
        } catch (Exception e) {
            log.error("redis zSetRangeWithScores error", e);
            return null;
        }
    }

    /**
     * 获取Sorted Set中指定范围内的元素个数
     *
     * @param key    键
     * @param min    最小分数
     * @param max    最大分数
     * @return Sorted Set中指定范围内的元素个数
     */
    public long zSetRangeCount(String key, double min, double max) {
        try {
            Long count = redisTemplate.opsForZSet().count(key, min, max);
            return count != null ? count : 0L;
        } catch (Exception e) {
            log.error("redis zSetRangeCount error", e);
            return 0L;
        }
    }

    // endregion

    // region List

    /**
     * 获取list列表数据
     *
     * @param key 键
     * @return all values of one List
     */
    public List<Object> listGetValues(String key) {
        try {
            return redisTemplate.opsForList().range(key, 0, -1);
        } catch (Exception e) {
            log.error("redis listGetValues error", e);
            return null;
        }
    }

    /**
     * 获取list列表数据(泛型)
     *
     * @param key        键
     * @param targetType 目标类型
     * @param <T>        目标类型参数
     * @return all values of one List
     */
    public <T> List<T> listGetValues(String key, Class<T> targetType) {
        try {
            return JsonUtils.listParse(redisTemplate.opsForList().range(key, 0, -1), targetType);
        } catch (Exception e) {
            log.error("redis listGetValues error", e);
            return null;
        }
    }

    /**
     * 获取list缓存的长度
     *
     * @param key 键
     * @return the length of the List
     */
    public long listGetSize(String key) {
        try {
            Long size = redisTemplate.opsForList().size(key);
            return size != null ? size : 0L;
        } catch (Exception e) {
            log.error("redis listGetSize error", e);
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
    public Object listGetIndexValue(String key, long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            log.error("redis listGetIndexValue error", e);
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
    public <T> T listGetIndexValue(String key, long index, Class<T> targetType) {
        try {
            return JsonUtils.objParse(redisTemplate.opsForList().index(key, index), targetType);
        } catch (Exception e) {
            log.error("redis listGetIndexValue error", e);
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
    public boolean listRightPush(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            log.error("redis listRightPush error", e);
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
    public boolean listRightPush(String key, Object value, long time) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("redis listRightPush error", e);
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
    public <T> boolean listRightPush(String key, List<T> values) {
        try {
            Long nums = redisTemplate.opsForList().rightPushAll(key, values);
            return nums != null;
        } catch (Exception e) {
            log.error("redis listRightPush error", e);
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
    public boolean listRightPush(String key, List<Object> values, long time) {
        try {
            redisTemplate.opsForList().rightPushAll(key, values);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("redis listRightPush error", e);
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
    public boolean listUpdateIndexValue(String key, Object value, long index) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            log.error("redis listUpdateIndexValue error", e);
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
    public long listRemoveValues(String key, Object value, long number) {
        try {
            Long count = redisTemplate.opsForList().remove(key, number, value);
            return count != null ? count : 0L;
        } catch (Exception e) {
            log.error("redis listRemoveValues error", e);
            return 0L;
        }
    }

    // endregion

    // region Lock

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

    // endregion

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