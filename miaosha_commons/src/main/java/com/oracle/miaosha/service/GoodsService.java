package com.oracle.miaosha.service;

import com.oracle.miaosha.vo.MiaoshaGoods;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient("MIAOSHA-GOODS-PROVIDER")
public interface GoodsService {

    @RequestMapping(value = "/list")
    public List<MiaoshaGoods> shopList();

    @RequestMapping(value = "/miaoshaGoodsDetail/{miaoshaGoodsId}")
    public MiaoshaGoods goodsDetails(@PathVariable("miaoshaGoodsId") Integer miaoshaGoodsId);

}
