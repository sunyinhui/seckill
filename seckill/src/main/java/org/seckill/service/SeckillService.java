package org.seckill.service;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 业务接口： 站在是使用者的角度设计接口
 *              1. 方法定义的粒度
 *              2. 参数
 *              3. 返回类型
 * Created by sunyinhui on 5/15/17.
 */
@Service
public interface SeckillService {

    // 查询所有秒杀记录
    List<Seckill> getSeckillList();

    // 查询单个秒杀记录
    Seckill getById(long seckillId);

    // 秒杀开启时，输出秒杀接口地址， 否则输出系统时间和秒杀时间
    Exposer exportSeckillUrl(long seckillId);

    // 执行秒杀操作
    SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException, RepeatKillException, SeckillCloseException;

}
