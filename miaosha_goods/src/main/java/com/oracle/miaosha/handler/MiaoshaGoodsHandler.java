package com.oracle.miaosha.handler;

import com.oracle.miaosha.service.GoodsService;
import com.oracle.miaosha.service.MiaoshaGoodsService;
import com.oracle.miaosha.vo.MiaoshaGoods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MiaoshaGoodsHandler implements GoodsService {

    @Autowired
    MiaoshaGoodsService miaoshaGoodsService;

    @RequestMapping(value = "/list")
    @ResponseBody
    @Override
    public List<MiaoshaGoods> shopList() {
        return miaoshaGoodsService.getAll();
    }

    @RequestMapping(value = "/miaoshaGoodsDetail/{miaoshaGoodsId}")
    @ResponseBody
    @Override
    public MiaoshaGoods goodsDetails(Integer miaoshaGoodsId) {
        return miaoshaGoodsService.getGoodsById(miaoshaGoodsId);
    }
}
