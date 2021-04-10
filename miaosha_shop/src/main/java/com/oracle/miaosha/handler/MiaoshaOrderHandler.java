package com.oracle.miaosha.handler;

import com.oracle.miaosha.mapper.MiaoshaOrderMapper;
import com.oracle.miaosha.service.MiaoshaGoodsService;
import com.oracle.miaosha.service.MiaoshaOrderService;
import com.oracle.miaosha.service.RedisService;
import com.oracle.miaosha.vo.MiaoshaOrder;
import com.oracle.miaosha.vo.User;
import com.oracle.util.Md5Util;
import com.oracle.util.Tools;
import com.oracle.vo.CodeMsg;
import com.oracle.vo.RedisKey;
import com.oracle.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Controller
public class MiaoshaOrderHandler {

    @Autowired
    RedisService redisService;

    @Autowired
    MiaoshaOrderMapper miaoshaOrderMapper;

    @Autowired
    MiaoshaGoodsService miaoShaGoodsService;

    @Autowired
    MiaoshaOrderService miaoshaOrderService;

    Set<Integer> miaoshaStock = new HashSet<>();

    private User getUserForToken(String token){
        User user = redisService.get(RedisKey.USER_LOGIN, token, User.class);
        if (user != null){
            redisService.set(RedisKey.USER_LOGIN, token, user, 30 * 60);
        }
        return user;
    }

    @RequestMapping("/do_miaosha")
    @ResponseBody
    public Result<MiaoshaOrder> miaosha_old(@CookieValue(value = "token", required = false) String token, MiaoshaOrder order){
        //1.判断是否登录
        User user = getUserForToken(token);
        if (user==null){
            return Result.error(CodeMsg.USER_NOT_LOGIN_ERROR);
        }
        //2.1判断是该商品的id是否在set中，如果有则说明该商品库存不足，直接转到错误页（内存标识，减少Redis访问）
        if (miaoshaStock.contains(order.getMiaoshaGoodsId())){
            return Result.error(CodeMsg.MIAOSHA_STOCK_ZERO);
        } else {
            //2.2判断秒杀商品是否有库存，如果没有库存，直接跳转到错误页（优化）
            // ①从MySQL里读取
            // int count = miaoShaGoodsService.getGoodsStockCount(order.getMiaoshaGoodsId());
            // ②从初始化过后的Redis中读取
            Integer count = redisService.get(RedisKey.MIAOSHA_GOODS_STOCK, String.valueOf(order.getMiaoshaGoodsId()), Integer.class);
            if (count < 1){
                // 如果该商品没有库存，则将id加入到set中
                miaoshaStock.add(order.getMiaoshaGoodsId());
                return Result.error(CodeMsg.MIAOSHA_STOCK_ZERO);
            }
        }
        //3.设置订单信息
        order.setUserid(user.getUserid());
        order.setGoodsCount(1);
        order.setCreateTime(new Date());
        //4.下订单
        try{
            Result<MiaoshaOrder> result = miaoshaOrderService.miaosha(order);
            if (result.isSuccessful()){
                return Result.success(order);
            }
        } catch (Exception e){
            return Result.error(CodeMsg.MIAOSHA_MANY_TIMES);
        }

        return Result.error(CodeMsg.SERVER_ERROR);
    }

    @RequestMapping("/{path}/do_miaosha")
    @ResponseBody
    public Result<MiaoshaOrder> miaosha(@CookieValue(value = "token", required = false) String token, @PathVariable("path") String path, MiaoshaOrder order){
        //1.判断是否登录
        User user = getUserForToken(token);
        if (user==null){
            return Result.error(CodeMsg.USER_NOT_LOGIN_ERROR);
        }
        System.out.println("已登录");
        //1.1验证路径
        String pathInRedis = redisService.getString(RedisKey.MIAOSHA_PATH, user.getUserid() + ":" + order.getMiaoshaGoodsId());
        System.out.println("读取到路径：" + pathInRedis);
        if (!path.equals(pathInRedis)){
            return Result.error(CodeMsg.PATH_NOT_EXISTS);
        }
        System.out.println("路径正确");
        //2.1判断是该商品的id是否在set中，如果有则说明该商品库存不足，直接转到错误页（内存标识，减少Redis访问）
        if (miaoshaStock.contains(order.getMiaoshaGoodsId())){
            return Result.error(CodeMsg.MIAOSHA_STOCK_ZERO);
        } else {
            //2.2判断秒杀商品是否有库存，如果没有库存，直接跳转到错误页（优化）
            // ①从MySQL里读取
            // int count = miaoShaGoodsService.getGoodsStockCount(order.getMiaoshaGoodsId());
            // ②从初始化过后的Redis中读取
            Integer count = redisService.get(RedisKey.MIAOSHA_GOODS_STOCK, String.valueOf(order.getMiaoshaGoodsId()), Integer.class);
            if (count < 1){
                // 如果该商品没有库存，则将id加入到set中
                miaoshaStock.add(order.getMiaoshaGoodsId());
                return Result.error(CodeMsg.MIAOSHA_STOCK_ZERO);
            }
        }
        //3.设置订单信息
        order.setUserid(user.getUserid());
        order.setGoodsCount(1);
        order.setCreateTime(new Date());
        //4.下订单
        try{
            Result<MiaoshaOrder> result = miaoshaOrderService.miaosha(order);
            if (result.isSuccessful()){
                return Result.success(order);
            }
        } catch (Exception e){
            return Result.error(CodeMsg.MIAOSHA_MANY_TIMES);
        }

        return Result.error(CodeMsg.SERVER_ERROR);
    }

    @RequestMapping("/getOrderStatus")
    @ResponseBody
    public Result<MiaoshaOrder> getOrderStatus(@CookieValue(value = "token", required = false) String token, Integer miaoshaGoodsId){
        //判断是否登录
        User user = getUserForToken(token);
        if (user==null){
            return Result.error(CodeMsg.USER_NOT_LOGIN_ERROR);
        }
        return redisService.get(RedisKey.MIAOSHA_ORDER_STATUS, user.getUserid() + ":" + miaoshaGoodsId, Result.class);
    }


    @RequestMapping(value = "/getMiaoshaPath", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String getMiaoshaPath(@CookieValue(value = "token", required = false) String token, Integer miaoshaGoodsId){
        System.out.println("getMiaoshaPath接收的miaoshaGoodsId：" + miaoshaGoodsId);
        User user = getUserForToken(token);
        if (user==null){
            return "nologin";
        }
        String path = Md5Util.md5(Tools.uuid() + "tiger");
        System.out.println("存储请求path:" + path);
        redisService.setString(RedisKey.MIAOSHA_PATH, user.getUserid() + ":" + miaoshaGoodsId, path, 60);
        return path;
    }
}
