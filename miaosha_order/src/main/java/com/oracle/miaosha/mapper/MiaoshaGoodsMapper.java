package com.oracle.miaosha.mapper;

import com.oracle.miaosha.vo.MiaoshaGoods;
import com.oracle.miaosha.vo.MiaoshaGoodsExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MiaoshaGoodsMapper {
    long countByExample(MiaoshaGoodsExample example);

    int deleteByExample(MiaoshaGoodsExample example);

    int deleteByPrimaryKey(Integer miaoshaGoodsId);

    int insert(MiaoshaGoods record);

    int insertSelective(MiaoshaGoods record);

    List<MiaoshaGoods> selectByExample(MiaoshaGoodsExample example);

    MiaoshaGoods selectByPrimaryKey(Integer miaoshaGoodsId);

    int updateByExampleSelective(@Param("record") MiaoshaGoods record, @Param("example") MiaoshaGoodsExample example);

    int updateByExample(@Param("record") MiaoshaGoods record, @Param("example") MiaoshaGoodsExample example);

    int updateByPrimaryKeySelective(MiaoshaGoods record);

    int updateByPrimaryKey(MiaoshaGoods record);

    //获得所有的秒杀商品
    List<MiaoshaGoods> getAll();
    //秒杀商品的详情页，根据商品ID获取信息
    public MiaoshaGoods getGoodsById(@Param("miaoshaGoodsId") Integer miaoshaGoodsId);
    // 对某个秒杀商品做减库存操作
    public int updateGoodCount(@Param("miaoshaGoodsId") Integer miaoshaGoodsId);
}