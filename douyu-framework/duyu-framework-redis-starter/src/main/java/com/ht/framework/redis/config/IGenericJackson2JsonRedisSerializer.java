package com.ht.framework.redis.config;

/**
 * @ClassName IGenericJackson2JsonRedisSerializer
 * @Description: 序列化实现工具类
 * @Author: Torrey
 * @Date: 2024/4/7 16:17
 **/

import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

public class IGenericJackson2JsonRedisSerializer extends GenericJackson2JsonRedisSerializer {
    public IGenericJackson2JsonRedisSerializer() {
        super(MapperFactory.newInstance());
    }

    @Override
    public byte[] serialize(Object source) throws
            SerializationException {
        if (source != null && ((source instanceof String) ||
                (source instanceof Character))) {
            return source.toString().getBytes();
        }
        return super.serialize(source);
    }
}