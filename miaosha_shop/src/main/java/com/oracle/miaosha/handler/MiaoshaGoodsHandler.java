package com.oracle.miaosha.handler;

import com.oracle.miaosha.service.GoodsService;
import com.oracle.miaosha.service.RedisService;
import com.oracle.miaosha.vo.MiaoshaGoods;
import com.oracle.miaosha.vo.User;
import com.oracle.util.AccessLimit;
import com.oracle.vo.RedisKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Controller
public class MiaoshaGoodsHandler {

    @Autowired
    GoodsService miaoShaGoodsService;

    @Autowired
    RedisService redisService;

    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;

    private User getUserForToken(String token){
        User user = redisService.get(RedisKey.USER_LOGIN, token, User.class);
        if (user != null){
            redisService.set(RedisKey.USER_LOGIN, token, user, 30 * 60);
        }
        return user;
    }

    @RequestMapping(value = "/list", produces = "text/html;charset=UTF-8")// produces——表明返回的内容的格式
    @ResponseBody
    @AccessLimit(seconds = 60, maxCount = 10, needLogin = false)
    public String shopList(HttpServletRequest request, HttpServletResponse response, Map<String, Object> map){
        //1.从redis缓存中获取数据
        String html = redisService.getString(RedisKey.PAGE_GOODS_LIST, "miaosha");
        if (html != null){
            return html;
        }
        //2.如无缓存，则从MySQL获取数据
        List<MiaoshaGoods> list = miaoShaGoodsService.shopList();
        map.put("list", list);
        // 手动渲染一个页面（HTML）
        WebContext context = new WebContext(request, response, request.getServletContext(), request.getLocale(), map);
        html = thymeleafViewResolver.getTemplateEngine().process("list", context);
        // 将手动渲染的页面存储到Redis
        redisService.setString(RedisKey.PAGE_GOODS_LIST, "miaosha", html, 10*60);
        //3.返回HTML页
        return html;
    }


    @RequestMapping(value = "/miaoshaGoodsDetail/{miaoshaGoodsId}", produces = "text/html;charset=UTF-8")
    @ResponseBody
    @AccessLimit(seconds = 20, maxCount = 10)
    public String goodsDetail(@PathVariable("miaoshaGoodsId") Integer miaoshaGoodsId, Map<String, Object> map, HttpServletRequest request, HttpServletResponse response){
        //1.从redis缓存中获取数据
        String html = redisService.getString(RedisKey.PAGE_GOODS_DETAILS, String.valueOf(miaoshaGoodsId));
        if (html != null){
            return html;
        }
        //2.如无缓存，则获得秒杀的商品,生成页面存入Redis中
        // 获得秒杀商品
        MiaoshaGoods goods = miaoShaGoodsService.goodsDetails(miaoshaGoodsId);
        // 生成状态信息，并存储到map中
        long start = goods.getStartDate().getTime();
        long end = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();
        // 设置秒杀状态，默认正在进行秒杀
        int status = 1;
        // 多久后开始秒杀
        long reMain = -1;
        // now < start: 说明秒杀未开始
        // now > end: 说明秒杀已结束
        if (now < start){
            // 秒杀未开始
            status = 0;
            reMain = (end - start) / 1000;
        } else if (now > end) {
            status = 2;
        }
        map.put("goods", goods);
        map.put("status", status);
        map.put("reMain", reMain);
        // 手动渲染一个页面（HTML）
        WebContext context = new WebContext(request, response, request.getServletContext(), request.getLocale(), map);
        html = thymeleafViewResolver.getTemplateEngine().process("miaoshaGoodsDetail", context);
        // 将HTML页面存入Redis中
        redisService.setString(RedisKey.PAGE_GOODS_DETAILS, String.valueOf(miaoshaGoodsId), html, 10*60);
        return html;
    }
}
