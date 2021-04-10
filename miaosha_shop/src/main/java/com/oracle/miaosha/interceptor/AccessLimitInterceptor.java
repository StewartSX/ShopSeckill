package com.oracle.miaosha.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oracle.miaosha.service.RedisService;
import com.oracle.miaosha.vo.User;
import com.oracle.util.AccessLimit;
import com.oracle.util.Tools;
import com.oracle.vo.CodeMsg;
import com.oracle.vo.RedisKey;
import com.oracle.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@Component
public class AccessLimitInterceptor implements HandlerInterceptor {

    @Autowired
    RedisService redisService;

    /**
     * Handler拦截器
     * @param request request对象
     * @param response response对象
     * @param handler 要拦截的handler
     * @return true为放行，false为截断
     * @throws Exception exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        /* 思路：
         * ①获得方法的Annotation是否有AccessLimit注解，如不存在则放行，如果存在就拦截处理
         * ②获得注解信息：second，maxCount，needLogin
         */
        if (handler instanceof HandlerMethod){
            //1.转成HandlerMethod
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            //2.是否使用了AccessLimit注解，如果没有则放行
            AccessLimit accessLimit = handlerMethod.getMethodAnnotation(AccessLimit.class);
            if (accessLimit == null){
                return true;
            }
            //3.获得AccessLimit的属性
            int seconds = accessLimit.seconds();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();
            //4.如果需要登录则判断是否已经登录
            if (needLogin){
                User user = getUser(request);
                //4.1如果未登录，则渲染，并截断
                if (user == null){
                    Result result = Result.error(CodeMsg.USER_NOT_LOGIN_ERROR);
                    render(request, response, result);
                    return false;
                }
            }
            /* 5.限流
             * 思路：
             * ①判断redis中是否存在limitToken + URL，如存在则获取访问次数，并且和maxCount比较，如果大于则限流
             * ②如果不存在，则将访问次数的数据存在redis中
             */
            //5.1、获取limitToken，URL
            String limitToken = getCookie(request, "limitToken");
            String url = request.getRequestURI().replace('/', '_');
            //5.2、判断limitToken是否为空
            if (limitToken == null){
                //如果为空则表明是第一次访问,则创建心得cookie存储到客户端
                limitToken = Tools.uuid();
                Cookie cookie = new Cookie("limitToken", limitToken);
                cookie.setPath("/");
                cookie.setMaxAge(60 * 60 * 24 * 2);
                response.addCookie(cookie);
            }
            //5.3、判断redis中是否存在limitToken + URL
            if (redisService.hasKey(RedisKey.LIMIT_TOKEN, limitToken + ":" + url)) {
                //如果存在，获得值，并判断是否大于等于maxCount
                Integer count = redisService.get(RedisKey.LIMIT_TOKEN, limitToken + ":" + url, Integer.class);
                if (count < maxCount){
                    // 如果小于则将次数+1，并放行
                    redisService.increment(RedisKey.LIMIT_TOKEN, limitToken + ":" + url);
                    return true;
                } else {
                    // 如果大于则渲染错误信息，并截断
                    render(request, response, Result.error(CodeMsg.ACCESS_TOO_MANY));
                    return false;
                }
            } else {
                //如果不存在则向redis中增加数据
                redisService.set(RedisKey.LIMIT_TOKEN, limitToken + ":" + url, 1, seconds);
            }
        }
        return true;
    }

    /**
     * 根据request对象获得登录用户对象
     * @param request request对象
     * @return User
     */
    private User getUser(HttpServletRequest request){
        //1.获得token-Cookie
        String token = getCookie(request, "token");
        if (token != null){
            User user = redisService.get(RedisKey.USER_LOGIN, token, User.class);
            if (user != null){
                redisService.set(RedisKey.USER_LOGIN, token, user, 60*30);
            }
            return user;
        }
        return null;
    }

    /**
     * 渲染视图
     * @param request request对象
     * @param response response对象
     * @param result Result类对象
     */
    public void render(HttpServletRequest request, HttpServletResponse response, Result result){
        //1.设置返回值的类型
        response.setContentType("application/json;charset=UTF-8");
        //2.将java对象转换成JSON格式的字符串
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(result);
            //3.渲染试图
            PrintWriter out = response.getWriter();
            out.print(json);
            out.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 获得name为key的cookie
     * @param request request对象
     * @param key 要获取的cookie的name
     * @return cookie
     */
    private String getCookie(HttpServletRequest request, String key){
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (key.equals(cookie.getName())){
                return cookie.getValue();
            }
        }
        return null;
    }
}
