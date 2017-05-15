package org.seckill.service.impl;

import org.seckill.dao.SeckillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by sunyinhui on 5/15/17.
 */
@Component
public class SeckillServiceImpl implements SeckillService {

    // 加盐
    private final String salt = "aj1244gaioghjaoiyquiotuqyi4-062&*(&*(%^&%))";

    private org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private SeckillDao seckillDao;

    @Autowired
    private SuccessKilledDao successKilledDao;


    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0, 4);
    }

    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    @Override
    public Exposer exportSeckillUrl(long seckillId) {
        Seckill seckill = seckillDao.queryById(seckillId);
        if (seckill == null) {
            return new Exposer(false, seckillId);
        }

        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        Date nowTime = new Date();
        if (nowTime.getTime() < startTime.getTime() || nowTime.getTime() > endTime.getTime()) {
            return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime());
        }

        // 转化特定字符串的过程， 不可逆
        String md5 = getMD5(seckillId); // TODO
        return new Exposer(true, md5, seckillId);
    }


    /**
     * @Transactional
     * 使用注解控制事务方法的优点：
     * 1. 开发团队达成一致约定，明确标注事务方法的编程风格
     * 2. 保证事务方法得到执行时间爱你尽可能短，不要穿插其他网络操作RPC/HTTP请求，或者 剥离到事务方法之外！
     * 3. 不是所有的方法都需要事务。
     */
    @Override
    @Transactional
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException, RepeatKillException, SeckillCloseException {

        if (md5 == null || !md5.equals(getMD5(seckillId))) {
            throw new SeckillException(" seckill data rewrite ");
        }

        // 执行秒杀逻辑： 减库存 + 记录购买行为
        Date nowTime = new Date(); // 用系统时间记录用户秒杀的时间
        try {
            int updateCount = seckillDao.reduceNumber(seckillId, nowTime);
            if (updateCount <= 0) {
                // 没有更新到记录,意味着秒杀结束了
                throw new SeckillCloseException(" seckill is closed ");
            } else {
                // 记录购买行为
                int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
                // 联合主键：seckillId, userPhone
                if (insertCount <= 0) {
                    // 如果insertCount=0, 说明重复秒杀了
                    throw new RepeatKillException(" seckill repeated ");
                } else {
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, successKilled);
                }
            }

        } catch (SeckillCloseException seckillClose) {
            throw seckillClose;
        } catch (RepeatKillException repeatKill){
            throw repeatKill;
        }catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new SeckillException("seckill inner error : " + e.getMessage()); // 把检查期异常转换为运行期异常
        }
    }

    // 生成md5
    private String getMD5(long seckillId) {
        String base = seckillId + "/" + salt;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }
}
