package com.oracle.miaosha.service;

import com.oracle.miaosha.mapper.MiaoshaOrderMapper;
import com.oracle.miaosha.mapper.OrderInfoMapper;
import com.oracle.miaosha.rabbitmq.MQSender;
import com.oracle.miaosha.vo.MiaoshaOrder;
import com.oracle.vo.CodeMsg;
import com.oracle.vo.RedisKey;
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

    @Autowired
    MQSender mqSender;


    /**
     * 生成秒杀订单
     * @param order 订单对象
     * @return Result
     */
    @Transactional
    public Result<MiaoshaOrder> miaosha(MiaoshaOrder order){
        //1.向redis中存取订单状态，
        // 如果返回false代表redis没有该订单状态，
        // 如果返回true则代表redis中没有该状态，则存入“排队中”的状态。
        boolean flag = redisService.setIfAbsent(RedisKey.MIAOSHA_ORDER_STATUS, order.getUserid() + ":" + order.getMiaoshaGoodsId(), Result.error(CodeMsg.MIAOSHA_ORDER_STATUS_QUEUING));
        System.out.println("------------------------:::" + flag);
        if (!flag){
            return Result.error(CodeMsg.MIAOSHA_MANY_TIMES);
        }
        //2.将订单发送给消息队列
        mqSender.sendMessage(order);
        return Result.success(order);
    }

//    @Transactional
//    public Result<MiaoshaOrder> miaosha(MiaoshaOrder order){
//        //1.减库存
//        // if库存不足，秒杀失败返回错误信息
//        System.out.println("MiaoshaOrderService: " + order);
//        System.out.println("MiaoshaOrderService: miaoShaGoodsService.reduceStockCount(order.getMiaoshaGoodsId())): " + miaoShaGoodsService.reduceStockCount(order.getMiaoshaGoodsId()));
//        if (!miaoShaGoodsService.reduceStockCount(order.getMiaoshaGoodsId())){
//            return Result.error(CodeMsg.MIAOSHA_STOCK_ZERO);
//        }
//        //2.库存充足，产生订单和秒杀订单
//        orderInfoMapper.insert(order);
//        //3.产生秒杀订单；如抛出异常，回退事务
//        miaoshaOrderMapper.insert(order);
//        //4.秒杀成功
//        return Result.success(order);
//    }
}
