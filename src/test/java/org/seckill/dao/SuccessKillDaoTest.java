package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.SuccessKill;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKillDaoTest {

    @Resource
    private SuccessKillDao successKillDao;

    @Test
    public void insertSuccessKilled() {
        long id = 1000L;
        long phone = 15235177861L;
        int i = successKillDao.insertSuccessKilled(id, phone);
        System.out.println("insertCount=" + i);
    }

    @Test
    public void queryByIdWithSeckill() {
        long id = 1000L;
        long phone = 15235177861L;
        SuccessKill successKill = successKillDao.queryByIdWithSeckill(id, phone);
        System.out.println(successKill);
        System.out.println(successKill.getSeckill());
    }
}