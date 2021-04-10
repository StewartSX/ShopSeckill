package com.oracle.miaosha.service;

import com.oracle.miaosha.mapper.MiaoshaOrderMapper;
import com.oracle.miaosha.mapper.OrderInfoMapper;
import com.oracle.miaosha.vo.MiaoshaOrder;
import com.oracle.vo.CodeMsg;
import com.oracle.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Service
public class MiaoshaOrderService {
    @Autowired
    MiaoshaOrderMapper miaoshaOrderMapper;

    @Autowired
    OrderInfoMapper orderInfoMapper;

    @Autowired
    MiaoshaGoodsService miaoShaGoodsService;

    @Autowired
    RedisService redisService;

    /**
     * 生成秒杀订单
     * @param order 订单对象
     * @return Result对象
     */
    @Transactional
    public Result<MiaoshaOrder> makeOrder(MiaoshaOrder order){
        //1.减库存
        // if库存不足，秒杀失败返回错误信息
        if (!miaoShaGoodsService.reduceStockCount(order.getMiaoshaGoodsId())){
            return Result.error(CodeMsg.MIAOSHA_STOCK_ZERO);
        }
        //2.库存充足，产生订单和秒杀订单
        orderInfoMapper.insert(order);
        //3.产生秒杀订单；如抛出异常，回退事务
        miaoshaOrderMapper.insert(order);
        //4.秒杀成功
        return Result.success(order);
    }
}
