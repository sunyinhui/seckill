package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.SuccessKilled;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 *
 * 首先需要配置spring和junit整合，是为了junit启动是加载springIOC容器，容器里边有dao的实现类 : @RunWith()注解的作用
 * spring-test,junit
 * Created by sunyinhui on 5/15/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
// 告诉junit spring的配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKilledDaoTest {

    @Resource
    private SuccessKilledDao successKilledDao;

    @Test
    public void testInsertSuccessKilled() throws Exception {
        long seckillId = 1001L;
        long userPhone = 18215581543L;
        int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
        System.out.println("insertCount = " + insertCount);
    }

    @Test
    public void testQueryByIdWithSeckill() throws Exception {
        long seckillId = 1001L;
        long userPhone = 18215581543L;
        SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
        System.out.println(successKilled);
        System.out.println(successKilled.getSeckill());
    }

}