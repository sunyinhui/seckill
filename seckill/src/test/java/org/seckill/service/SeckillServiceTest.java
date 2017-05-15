package org.seckill.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Created by sunyinhui on 5/15/17.
 */

@RunWith(SpringJUnit4ClassRunner.class)
// 告诉junit spring的配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml",
                        "classpath:spring/spring-service.xml"})
public class SeckillServiceTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());



    @Autowired
    private SeckillService seckillService;


    @Test
    public void testGetSeckillList() throws Exception {
        List<Seckill> seckillList  = seckillService.getSeckillList();
        logger.info("list={}", seckillList);
    }

    @Test
    public void testGetById() throws Exception {
        long id = 1000L;
        Seckill seckill = seckillService.getById(id);
        logger.info("seckill = {} ", seckill);

    }

    @Test
    public void testExportSeckillUrl() throws Exception {
        long id = 1000L;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        logger.info("exposer = {}", exposer);
    }

    @Test
    public void testExecuteSeckill() throws Exception {
        long id = 1000L;
        long userPhone = 18215581546L;
        String md5 = "69995ac7d32992068a4a9fe64b19acfa";
        try {
            SeckillExecution seckillExecution = seckillService.executeSeckill(id, userPhone, md5);
            logger.info("seckillExecution = {}", seckillExecution);
        } catch (RepeatKillException repeatKill) {
            System.out.println("--------------");
            logger.error(repeatKill.getMessage());
            System.out.println("--------------");
        } catch (SeckillCloseException seckillClose) {
            logger.error(seckillClose.getMessage());
        } catch (SeckillException seckill) {
            logger.error(seckill.getMessage());
        }

    }

    // 整合上面两个测试逻辑：暴露接口和执行秒杀
    @Test
    public void testSeckillLogic() {
        long id = 1002L;

        Exposer exposer = seckillService.exportSeckillUrl(id);
        if (exposer.isExposed()) {
            logger.info("exposer = {}", exposer);

            long userPhone = 18215581546L;
            String md5 = exposer.getMd5();
            try {
                SeckillExecution seckillExecution = seckillService.executeSeckill(id, userPhone, md5);
                logger.info("seckillExecution = {}", seckillExecution);
            } catch (RepeatKillException repeatKill) {
                logger.error(repeatKill.getMessage());
            } catch (SeckillCloseException seckillClose) {
                logger.error(seckillClose.getMessage());
            } catch (SeckillException seckill) {
                logger.error(seckill.getMessage());
            }

        } else {
            // 秒杀未开启
            logger.warn("exposer = {}", exposer);
        }
    }

}