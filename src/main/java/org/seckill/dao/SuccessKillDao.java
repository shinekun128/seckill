package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.SuccessKill;

public interface SuccessKillDao {

    /**
     * 插入购买明细，可过滤重复
     * @param secKillId
     * @param userPhone
     * @return
     */
    int insertSuccessKilled(@Param("secKillId") long secKillId, @Param("userPhone") long userPhone);

    /**
     * 根据id查询successKill并携带秒杀产品对象的实体
     * @param secKillId
     * @return
     */
    SuccessKill queryByIdWithSeckill(@Param("secKillId") long secKillId,@Param("userPhone") long userPhone);
}
