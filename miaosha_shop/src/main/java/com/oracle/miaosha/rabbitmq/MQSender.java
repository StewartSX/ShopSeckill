package com.oracle.miaosha.rabbitmq;


import com.oracle.miaosha.vo.MiaoshaOrder;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQSender {

    @Autowired
    AmqpTemplate amqpTemplate;

    /**
     * 向RabbitMQ消息队列发送消息
     * @param order 发送订单信息
     */
    public void sendMessage(MiaoshaOrder order){
        System.out.println("发送order到消息队列：" + order);
        amqpTemplate.convertAndSend("miaosha.direct", "miaosha", order);
    }
}
