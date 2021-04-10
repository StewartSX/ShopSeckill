package com.oracle.miaosha.rabbitmq;

import com.oracle.miaosha.service.MiaoshaOrderService;
import com.oracle.miaosha.service.RedisService;
import com.oracle.miaosha.vo.MiaoshaOrder;
import com.oracle.vo.CodeMsg;
import com.oracle.vo.RedisKey;
import com.oracle.vo.Result;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderReceiver {

    @Autowired
    MiaoshaOrderService miaoshaOrderService;

    @Autowired
    RedisService redisService;

    @RabbitListener(queues = "miaosha")
    public void receiveOrder(MiaoshaOrder order){
        System.out.println("处理一个订单：" + order);
        try {
            // 生成订单
            miaoshaOrderService.makeOrder(order);
            // 更新订单状态
            redisService.set(RedisKey.MIAOSHA_ORDER_STATUS, order.getUserid() + ":" + order.getMiaoshaGoodsId(), Result.success(order));
            // 更新商品库存
            redisService.decrement(RedisKey.MIAOSHA_GOODS_STOCK, String.valueOf(order.getMiaoshaGoodsId()));
        } catch (Exception e){
            System.out.println("重复秒杀");
            redisService.set(RedisKey.MIAOSHA_ORDER_STATUS, order.getUserid() + ":" + order.getMiaoshaGoodsId(), Result.error(CodeMsg.MIAOSHA_ORDER_STATUS_FAILED));

        }
    }
}
