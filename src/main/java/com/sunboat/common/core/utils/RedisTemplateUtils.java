package com.sunboat.common.core.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 基于RedisTemplate的通用工具类
 * 支持String、Hash、List、Set、ZSet等常用操作
 */
@Component
public class RedisTemplateUtils {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // ------------------- 通用操作 -------------------

    /**
     * 判断key是否存在
     */
    public boolean hasKey(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            logError("hasKey", key, e);
            return false;
        }
    }

    /**
     * 删除指定key
     */
    public boolean deleteKey(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.delete(key));
        } catch (Exception e) {
            logError("deleteKey", key, e);
            return false;
        }
    }

    /**
     * 批量删除key
     */
    public long deleteKeys(Collection<String> keys) {
        if (CollectionUtils.isEmpty(keys)) {
            return 0;
        }
        try {
            return redisTemplate.delete(keys);
        } catch (Exception e) {
            logError("deleteKeys", keys.toString(), e);
            return 0;
        }
    }

    /**
     * 设置key过期时间
     */
    public boolean expireKey(String key, long timeout, TimeUnit unit) {
        try {
            return Boolean.TRUE.equals(redisTemplate.expire(key, timeout, unit));
        } catch (Exception e) {
            logError("expireKey", key, e);
            return false;
        }
    }

    /**
     * 获取key剩余过期时间
     */
    public Long getKeyExpire(String key, TimeUnit unit) {
        try {
            return redisTemplate.getExpire(key, unit);
        } catch (Exception e) {
            logError("getKeyExpire", key, e);
            return -1L;
        }
    }

    // ------------------- String类型操作 -------------------

    /**
     * 存储String类型数据
     */
    public void setString(String key, Object value) {
        setString(key, value, -1, null);
    }

    /**
     * 存储String类型数据并设置过期时间
     */
    public void setString(String key, Object value, long timeout, TimeUnit unit) {
        try {
            ValueOperations<String, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            if (timeout > 0 && unit != null) {
                expireKey(key, timeout, unit);
            }
        } catch (Exception e) {
            logError("setString", key, e);
            throw new RuntimeException("Redis String存储失败", e);
        }
    }

    /**
     * 获取String类型数据
     */
    @SuppressWarnings("unchecked")
    public <T> T getString(String key) {
        try {
            ValueOperations<String, Object> operations = redisTemplate.opsForValue();
            return (T) operations.get(key);
        } catch (Exception e) {
            logError("getString", key, e);
            return null;
        }
    }

    /**
     * 字符串自增
     */
    public Long incrString(String key, long delta) {
        try {
            return redisTemplate.opsForValue().increment(key, delta);
        } catch (Exception e) {
            logError("incrString", key, e);
            throw new RuntimeException("Redis String自增失败", e);
        }
    }

    // ------------------- Hash类型操作 -------------------

    /**
     * 存储Hash类型数据
     */
    public void setHash(String key, String hashKey, Object value) {
        try {
            redisTemplate.opsForHash().put(key, hashKey, value);
        } catch (Exception e) {
            logError("setHash", key + ":" + hashKey, e);
            throw new RuntimeException("Redis Hash存储失败", e);
        }
    }

    /**
     * 批量存储Hash类型数据
     */
    public void setHashBatch(String key, Map<String, Object> map) {
        if (CollectionUtils.isEmpty(map)) {
            return;
        }
        try {
            redisTemplate.opsForHash().putAll(key, map);
        } catch (Exception e) {
            logError("setHashBatch", key, e);
            throw new RuntimeException("Redis Hash批量存储失败", e);
        }
    }

    /**
     * 获取Hash中的某个字段值
     */
    @SuppressWarnings("unchecked")
    public <T> T getHash(String key, String hashKey) {
        try {
            return (T) redisTemplate.opsForHash().get(key, hashKey);
        } catch (Exception e) {
            logError("getHash", key + ":" + hashKey, e);
            return null;
        }
    }

    /**
     * 获取Hash中所有字段和值
     */
    @SuppressWarnings("unchecked")
    public <T> Map<String, T> getHashAll(String key) {
        try {
            Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
            Map<String, T> result = new HashMap<>(entries.size());
            for (Map.Entry<Object, Object> entry : entries.entrySet()) {
                result.put(entry.getKey().toString(), (T) entry.getValue());
            }
            return result;
        } catch (Exception e) {
            logError("getHashAll", key, e);
            return Collections.emptyMap();
        }
    }

    /**
     * 删除Hash中的字段
     */
    public Long deleteHash(String key, Object... hashKeys) {
        try {
            return redisTemplate.opsForHash().delete(key, hashKeys);
        } catch (Exception e) {
            logError("deleteHash", key, e);
            return 0L;
        }
    }

    // ------------------- List类型操作 -------------------

    /**
     * 向List左侧添加元素
     */
    public Long addListLeft(String key, Object... values) {
        try {
            return redisTemplate.opsForList().leftPushAll(key, values);
        } catch (Exception e) {
            logError("addListLeft", key, e);
            throw new RuntimeException("Redis List左侧添加失败", e);
        }
    }

    /**
     * 向List右侧添加元素
     */
    public Long addListRight(String key, Object... values) {
        try {
            return redisTemplate.opsForList().rightPushAll(key, values);
        } catch (Exception e) {
            logError("addListRight", key, e);
            throw new RuntimeException("Redis List右侧添加失败", e);
        }
    }

    /**
     * 获取List指定范围的元素
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> getListRange(String key, long start, long end) {
        try {
            return (List<T>) redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            logError("getListRange", key, e);
            return Collections.emptyList();
        }
    }

    /**
     * 设置List中指定索引位置的元素
     */
    public void setListIndex(String key, long index, Object value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
        } catch (Exception e) {
            logError("setListIndex", key + ":" + index, e);
            throw new RuntimeException("Redis List设置索引元素失败", e);
        }
    }

    /**
     * 删除List中指定元素
     */
    public Long deleteListElement(String key, long count, Object value) {
        try {
            return redisTemplate.opsForList().remove(key, count, value);
        } catch (Exception e) {
            logError("deleteListElement", key, e);
            return 0L;
        }
    }

    // ------------------- Set类型操作 -------------------

    /**
     * 向Set添加元素
     */
    public Long addSet(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            logError("addSet", key, e);
            throw new RuntimeException("Redis Set添加元素失败", e);
        }
    }

    /**
     * 获取Set中所有元素
     */
    @SuppressWarnings("unchecked")
    public <T> Set<T> getSetAll(String key) {
        try {
            return (Set<T>) redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            logError("getSetAll", key, e);
            return Collections.emptySet();
        }
    }

    /**
     * 判断元素是否在Set中
     */
    public boolean isSetMember(String key, Object value) {
        try {
            return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, value));
        } catch (Exception e) {
            logError("isSetMember", key, e);
            return false;
        }
    }

    /**
     * 删除Set中的元素
     */
    public Long deleteSetElements(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().remove(key, values);
        } catch (Exception e) {
            logError("deleteSetElements", key, e);
            return 0L;
        }
    }

    // ------------------- ZSet类型操作 -------------------

    /**
     * 向ZSet添加元素
     */
    public Boolean addZSet(String key, Object value, double score) {
        try {
            return redisTemplate.opsForZSet().add(key, value, score);
        } catch (Exception e) {
            logError("addZSet", key, e);
            throw new RuntimeException("Redis ZSet添加元素失败", e);
        }
    }

    /**
     * 获取ZSet中元素的分数
     */
    public Double getZSetScore(String key, Object value) {
        try {
            return redisTemplate.opsForZSet().score(key, value);
        } catch (Exception e) {
            logError("getZSetScore", key, e);
            return null;
        }
    }

    /**
     * 获取ZSet中指定排名范围的元素（升序）
     */
    @SuppressWarnings("unchecked")
    public <T> Set<T> getZSetRange(String key, long start, long end) {
        try {
            return (Set<T>) redisTemplate.opsForZSet().range(key, start, end);
        } catch (Exception e) {
            logError("getZSetRange", key, e);
            return Collections.emptySet();
        }
    }

    /**
     * 获取ZSet中指定分数范围的元素
     */
    @SuppressWarnings("unchecked")
    public <T> Set<ZSetOperations.TypedTuple<T>> getZSetByScore(String key, double min, double max) {
        try {
            // 获取原始结果
            Set<ZSetOperations.TypedTuple<Object>> originalSet = redisTemplate.opsForZSet().rangeByScoreWithScores(key, min, max);
            Set<ZSetOperations.TypedTuple<T>> resultSet = new HashSet<>();

            // 逐个转换元素类型
            for (ZSetOperations.TypedTuple<Object> tuple : originalSet) {
                @SuppressWarnings("unchecked")
                T value = (T) tuple.getValue();
                resultSet.add(new DefaultTypedTuple<>(value, tuple.getScore()));
            }

            return resultSet;
        } catch (Exception e) {
            logError("getZSetByScore", key, e);
            return Collections.emptySet();
        }
    }

    // ------------------- 私有工具方法 -------------------

    /**
     * 统一错误日志处理
     */
    private void logError(String method, String key, Exception e) {
        // 实际项目中建议使用SLF4J日志框架
        System.err.printf("Redis操作失败 - 方法: %s, Key: %s, 异常: %s%n", method, key, e.getMessage());
    }
}
    