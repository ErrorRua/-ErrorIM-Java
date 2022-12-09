package com.errorim.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamInfo;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author ErrorRua
 * @date 2022/11/19
 * @description:
 */
@SuppressWarnings(value = {"unchecked", "rawtypes"})
@Component
public class RedisCache {
    @Autowired
    public RedisTemplate redisTemplate;

    /**
     *  缓存基本的对象， Integer，String、实体类等
     *
     * @param key 缓存的键值
     * @param value 缓存的值
     */
    public <T> void setCacheObject(final String key, final T value){
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     *  缓存基本的对象 ，Integer 、String 、实体类等
     * @param key       缓存的键值
     * @param value     缓存的值
     * @param timeout   时间
     * @param timeUnit  时间颗粒度
     * @param <T>
     */
    public <T> void setCacheObject(final String key, final T value, final Integer timeout, final TimeUnit timeUnit){
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    /**
     *  设置有效时间
     * @param key    Redis键
     * @param timeout 超时时间
     * @return true=设置成功 ，false = 设置失败
     */
    public boolean expire(final String key, final long timeout){
        return expire(key, timeout,TimeUnit.SECONDS);
    }

    /**
     *  设置有效时间
     * @param key      Redis键
     * @param timeout  超时时间
     * @param unit    时间单位
     * @return true=设置成功 ，false = 设置失败
     */
    public boolean expire(final String key, final long timeout, final TimeUnit unit){
        return redisTemplate.expire(key,timeout,unit);
    }

    /**
     *  获取缓存的基本对象
     * @param key  缓存键值
     * @param <T>   缓存键值对应的数据
     * @return
     */
    public <T> T getCacheObject(final  String key){
        ValueOperations<String, T> operation = redisTemplate.opsForValue();
        return operation.get(key);
    }

    /**
     * 删除单个对象
     * @param key
     * @return
     */
    public boolean deleteObject(final String key){
        return redisTemplate.delete(key);
    }

    /**
     * 删除集合对象
     * @param collection 多个对象
     * @return
     */
    public long deleteObject(final Collection collection){
        return redisTemplate.delete(collection);
    }

    /**
     * 缓存List数据
     * @param key       缓存的键值
     * @param dataList  待缓存的List数据
     * @param <T>
     * @return  缓存的对象
     */
    public <T> long setCacheList(final String key, final List<T> dataList){
        Long count = redisTemplate.opsForList().rightPushAll(key, dataList);
        return count == null ? 0 : count;
    }

    /**
     *  获得缓存的list对象
     * @param key   缓存的键值
     * @param <T>
     * @return   缓存键值对应的数据
     */
    public <T> List<T> getCacheList(final String key){
        return redisTemplate.opsForList().range(key,0,-1);
    }

    public <T> BoundSetOperations<String, T> setCacheSet(final String key, final Set<T> dataSet){
        BoundSetOperations<String ,T> setOperation = redisTemplate.boundSetOps(key);
        Iterator<T> it = dataSet.iterator();
        while (it.hasNext()){
            setOperation.add(it.next());
        }
        return setOperation;
    }

    /**
     *  获得缓存的set
     * @param key
     * @param <T>
     * @return
     */
    public <T> Set<T> getCacheSet(final String key){
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 缓存Map
     * @param key
     * @param dataMap
     * @param <T>
     */
    public <T> void setCacheMap(final String key, final Map<String, T> dataMap){
        if (dataMap!= null){
            redisTemplate.opsForHash().putAll(key, dataMap);
        }
    }

    /**
     * 获得缓存的Map
     * @param key
     * @param <T>
     * @return
     */
    public <T> Map<String , T> getCacheMap(final String key){
        return  redisTemplate.opsForHash().entries(key);
    }

    /**
     * 往hash中存入数据
     * @param key   Redis键
     * @param hkey  Hash键
     * @param value   值
     * @param <T>
     */
    public <T> void setCacheMapValue(final String key, final String hkey, final T value){
        redisTemplate.opsForHash().put(key,hkey,value);
    }

    /**
     *  获取hash中的数据
     * @param key  Redis键
     * @param hkey Hash键
     * @param <T>
     * @return   hash中的对象
     */
    public <T> T getCacheMapValue(final String key, final String hkey){
        HashOperations<String, String, T> opsForHash = redisTemplate.opsForHash();
        return opsForHash.get(key, hkey);
    }

    /**
     * 删除Hash中的数据
     * @param key Redis键
     * @param hkey Hash键
     */
    public void delCacheMapValue(final String key, final String hkey){
        HashOperations hashOperations = redisTemplate.opsForHash();
        hashOperations.delete(key,hkey);
    }

    /**
     *  获取多个hash中的数据
     * @param key   Redis键
     * @param hkeys Hash键集合
     * @param <T>
     * @return  Hash对象集合
     */
    public <T> List<T> getMultiCacheMapValue(final String key, final Collection<Object> hkeys){
        return redisTemplate.opsForHash().multiGet(key,hkeys);
    }

    /**
     * 获得缓存的基本对象列表
     * @param pattern 字符串前缀
     * @return 对象列表
     */
    public Collection<String> keys(final String pattern){
        return redisTemplate.keys(pattern);
    }

    /**
     * 文章浏览量加1
     * @param key
     * @param hKey
     * @param v
     */
    public void incrementCacheMapValue(String key, String hKey, int v) {
        redisTemplate.opsForHash().increment(key, hKey, v);
    }

    /**
     * 创建消费组
     * @param key
     * @param group
     * @return
     */
    public String creartGroup(String key, String group){
        return redisTemplate.opsForStream().createGroup(key, group);
    }

    /**
     * 消费组信息
     * @param key
     * @param group
     * @return
     */
    public StreamInfo.XInfoConsumers consumers(String key, String group){
        return redisTemplate.opsForStream().consumers(key, group);
    }

    /**
     * 确认已消费
     * @param key
     * @param group
     * @param recordIds
     * @return
     */
    public Long ack(String key, String group, String... recordIds){
        return redisTemplate.opsForStream().acknowledge(key, group, recordIds);
    }

    /**
     * 追加消息
     * @param key
     * @param field
     * @param value
     * @return
     */
    public String add(String key, String field, Object value){
        Map<String, Object> content = new HashMap<>(1);
        content.put(field, value);
        return add(key, content);
    }

    public String add(String key, Map<String, Object> content){
        return redisTemplate.opsForStream().add(key, content).getValue();
    }

    /**
     * 删除消息，这里的删除仅仅是设置了标志位，不影响消息总长度
     * 消息存储在stream的节点下，删除时仅对消息做删除标记，当一个节点下的所有条目都被标记为删除时，销毁节点
     * @param key
     * @param recordIds
     * @return
     */
    public Long del(String key, String... recordIds){
        return redisTemplate.opsForStream().delete(key, recordIds);
    }

    /**
     * 消息长度
     * @param key
     * @return
     */
    public Long len(String key){
        return redisTemplate.opsForStream().size(key);
    }

    /**
     * 从头开始读
     * @param key
     * @return
     */
    public List<MapRecord<String, Object, Object>> read(String key){
        return redisTemplate.opsForStream().read(StreamOffset.fromStart(key));
    }

    /**
     * 从指定的ID开始读
     * @param key
     * @param recordId
     * @return
     */
    public List<MapRecord<String, Object, Object>> read(String key, String recordId){
        return redisTemplate.opsForStream().read(StreamOffset.from(MapRecord.create(key, new HashMap<>(1)).withId(RecordId.of(recordId))));
    }

}

