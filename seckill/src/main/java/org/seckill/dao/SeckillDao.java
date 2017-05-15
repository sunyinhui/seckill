package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.Seckill;

import java.util.Date;
import java.util.List;

/**
 * Created by sunyinhui on 5/15/17.
 */
public interface SeckillDao {

    // 减库存
    int reduceNumber(@Param("seckillId") long seckillId, @Param("killTime") Date killTime);

    // 根据id查询秒杀对象
    Seckill queryById(long seckillId);

    // 根据偏移量查询秒杀商品列表

    /**
     *
     * List<Seckill> queryAll(int offset, int limit);
     * 当不加@Param注解时，就会报错！
     * org.mybatis.spring.MyBatisSystemException: nested exception is org.apache.ibatis.binding.BindingException: Parameter 'offet' not found.
     * Available parameters are [1, 0, param1, param2]
     *
     * Java 没有保存形参的记录,所以多个参数就丢失了形参名信息 queryAll(int offset, int limit) 就会变成 queryAll(arg0, arg1)
     *
     */
    List<Seckill> queryAll(@Param("offset") int offset, @Param("limit") int limit);
}
