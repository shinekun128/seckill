package org.seckill.service.impl;

import org.seckill.dao.SeckillDao;
import org.seckill.dao.SuccessKillDao;
import org.seckill.dao.cache.RedisDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKill;
import org.seckill.enums.SeckillStatEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class SeckillServiceImpl implements SeckillService {

    private Logger logger = LoggerFactory.getLogger(SeckillServiceImpl.class);

    @Resource
    private SeckillDao seckillDao;
    @Resource
    private SuccessKillDao successKillDao;
    @Resource
    private RedisDao redisDao;

    //MD5盐值字符串，用于md5
    private final String slat = "(*HF9ah(HF)Hgq3409hfOH9rP8H8H9FEIO98HFF";

    @Override
    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0,4);
    }

    @Override
    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    @Override
    public Exposer exportSeckillUrl(long seckillId) {
        Seckill seckill = redisDao.getSeckill(seckillId);
        if(seckill == null){
            seckill = seckillDao.queryById(seckillId);
            if(seckill == null){
                return new Exposer(false,seckillId);
            }else{
                redisDao.putSeckill(seckill);
            }
        }
        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        Date nowTime = new Date();
        if(nowTime.getTime() < startTime.getTime() || nowTime.getTime()>endTime.getTime()){
            return new Exposer(false,seckillId,nowTime.getTime(), startTime.getTime(), endTime.getTime());
        }
        //转化特定字符串的结果
        String md5 = getMD5(seckillId);
        return new Exposer(true,md5,seckillId);
    }

    @Override
    @Transactional
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException, RepeatKillException, SeckillCloseException {
        if(md5 == null || !md5.equals(getMD5(seckillId))){
            throw new SeckillException("seckill data rewrite");
        }
        Date nowTime = new Date();


        try {
            int updateCount = seckillDao.reduceNumber(seckillId, nowTime);
            if(updateCount<=0){
                throw new SeckillCloseException("seckill is close");
            }else{
                int insertCount = successKillDao.insertSuccessKilled(seckillId, userPhone);
                if(insertCount<=0){
                    throw new RepeatKillException("seckill repeated");
                }else{
                    SuccessKill successKill = successKillDao.queryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS , successKill);
                }
            }
        } catch (SeckillCloseException | RepeatKillException e1){
            throw e1;
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            throw new SeckillException("seckill inner error:" + e.getMessage());
        }
    }

    private String getMD5(long seckillId){
        String base = seckillId + "/" + slat;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }
}
