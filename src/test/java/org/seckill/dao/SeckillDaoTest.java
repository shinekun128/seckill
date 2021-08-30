package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.Seckill;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * 配置整合spring和junit，junit启动加载springIOC容器
 * spring-test
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SeckillDaoTest {
    //注入
    @Resource
    private SeckillDao seckillDao;

    @Test
    public void reduceNumber() {
        Date killTime = new Date();
        int number = seckillDao.reduceNumber(1000L, killTime);
        System.out.println(number);
    }

    @Test
    public void queryById() {
        long id = 1001;
        Seckill seckill = seckillDao.queryById(id);
        System.out.println(seckill);
    }

    @Test
    public void queryAll() {
        List<Seckill> seckills = seckillDao.queryAll(0, 2);
        for(Seckill seckill : seckills){
            System.out.println(seckill);
        }
    }
}