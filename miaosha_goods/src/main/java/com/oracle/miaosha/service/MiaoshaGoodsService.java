package com.oracle.miaosha.service;

import com.oracle.miaosha.mapper.MiaoshaGoodsMapper;
import com.oracle.miaosha.vo.MiaoshaGoods;
import com.oracle.vo.RedisKey;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Service
public class MiaoshaGoodsService implements InitializingBean {
    @Autowired
    MiaoshaGoodsMapper miaoshaGoodsMapper;

    @Autowired
    RedisService redisService;

    public List<MiaoshaGoods> getAll(){
        return miaoshaGoodsMapper.getAll();
    }

    public MiaoshaGoods getGoodsById(Integer miaoshaGoodsId){
        return miaoshaGoodsMapper.getGoodsById(miaoshaGoodsId);
    }

    public int getGoodsStockCount(Integer miaoshaGoodsId){
        MiaoshaGoods goods = getGoodsById(miaoshaGoodsId);
        return goods.getStockCount();
    }

    @Transactional
    public boolean reduceStockCount(Integer miaoshaGoodsId){
        return miaoshaGoodsMapper.updateGoodCount(miaoshaGoodsId) > 0;
    }

    /**
     * 当我们初始化Bean的时候会执行afterPropertiesSet方法
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("秒杀商品库存数量初始化：");
        List<MiaoshaGoods> list = miaoshaGoodsMapper.getAll();
        for (MiaoshaGoods goods : list) {
            redisService.set(RedisKey.MIAOSHA_GOODS_STOCK, String.valueOf(goods.getMiaoshaGoodsId()), goods.getStockCount());
        }
    }
}
