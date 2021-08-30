package org.seckill.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.SeckillException;
import org.seckill.service.impl.SeckillServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml","classpath:spring/spring-service.xml"})
public class SeckillServiceTest {
    private Logger logger = LoggerFactory.getLogger(SeckillServiceTest.class);

    @Resource
    private SeckillService seckillService;

    @Test
    public void getSeckillList() {
        List<Seckill> seckillList = seckillService.getSeckillList();
        for(Seckill seckill : seckillList){
            System.out.println(seckill);
        }
    }

    @Test
    public void getById() {
        Seckill seckill = seckillService.getById(1000);
        System.out.println(seckill);
    }

    @Test
    public void exportSeckillUrl() {
        long id = 1002;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        if(exposer.getExposed()){
            long phone = 15235177861l;
            String md5 =exposer.getMd5();
            try {
                SeckillExecution seckillExecution = seckillService.executeSeckill(id, phone, md5);
                logger.info("执行秒杀={}",seckillExecution);
            } catch (SeckillException e) {
                logger.error("秒杀异常" ,e);
            }
        }else{
            logger.warn("expose="+exposer);
        }
    }
}