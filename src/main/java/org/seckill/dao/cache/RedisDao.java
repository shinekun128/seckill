package org.seckill.dao.cache;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.seckill.entity.Seckill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisDao {

    private final Logger logger = LoggerFactory.getLogger(RedisDao.class);
    private final JedisPool jedisPool;
    private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);

    public RedisDao(String ip,int port){
        jedisPool = new JedisPool(ip,port);
    }

    public Seckill getSeckill(long seckillId){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String key = "seckill:" + seckillId;
            byte[] bytes = jedis.get(key.getBytes());
            if(bytes != null){
                Seckill seckill = schema.newMessage();
                ProtobufIOUtil.mergeFrom(bytes,seckill,schema);
                return seckill;
            }
        } catch (Exception e) {
            logger.error("读取缓存异常{}" , e);
        } finally {
            if(jedis != null){
                jedis.close();
            }
        }
        return null;
    }

    public String putSeckill(Seckill seckill){
        //Object --> bytes --> redis
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String key = "seckill:" + seckill.getSeckillId();
            byte[] bytes = ProtobufIOUtil.toByteArray(seckill,schema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
            //超时缓存
            long timeout = 60 * 60;//1小时
            String result = jedis.setex(key.getBytes(), timeout, bytes);
            return result;
        } catch (Exception e) {
            logger.error("写缓存异常{}" , e);
        } finally {
            if(jedis != null){
                jedis.close();
            }
        }
        return null;
    }
}
