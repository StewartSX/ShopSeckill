package com.oracle.miaosha.service;

import com.oracle.miaosha.mapper.MiaoshaGoodsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Service
public class MiaoshaGoodsService {
    @Autowired
    MiaoshaGoodsMapper miaoshaGoodsMapper;

    @Autowired
    RedisService redisService;

    @Transactional
    public boolean reduceStockCount(Integer miaoshaGoodsId){
        return miaoshaGoodsMapper.updateGoodCount(miaoshaGoodsId) > 0;
    }

}
