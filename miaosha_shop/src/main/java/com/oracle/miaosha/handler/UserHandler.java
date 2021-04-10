package com.oracle.miaosha.handler;

import com.oracle.miaosha.service.RedisService;
import com.oracle.miaosha.service.UserService;
import com.oracle.miaosha.vo.User;
import com.oracle.util.Tools;
import com.oracle.vo.CodeMsg;
import com.oracle.vo.RedisKey;
import com.oracle.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Controller
public class UserHandler {

    @Autowired
    UserService userService;

    @Autowired
    RedisService redisService;

    @RequestMapping("/")
    public String to_login(){
        return "login";
    }

    @RequestMapping("/login")
    @ResponseBody
    public Result<User> login(User user, HttpServletResponse response){
        // 登录成功，返回个user；登录失败，返回错误码和错误信息
        Result<User> result = userService.login(user.getTel(), user.getPassword());
        // 登录成功，将用户信息存在redis（分布式session）
        if (result.isSuccessful()){
            // 1.生成一个唯一标识（sessionId）
            String sessionId = Tools.uuid();
            // 2.将用户信息存在redis中
            redisService.set(RedisKey.USER_LOGIN, sessionId, result.getData(), 30 * 60);
            // 3.生成cookie
            Cookie cookie = new Cookie("token", sessionId);
            cookie.setMaxAge(60 * 60 * 24 * 2);
            cookie.setPath("/");
            response.addCookie(cookie);
        }
        return result;
    }

    @RequestMapping("/go_to_updatePassword")
    public String go_to_updatePassword(){
        return "updatePassword";
    }

    @RequestMapping(value = "/updatePassword")
    @ResponseBody
    public Result<User> updatePassword(@CookieValue(value = "token", required = false) String token, String password){
        User user = userService.updatePassword(token, password);
        System.out.println("user: " + user.getUserid() + "," +user.getPassword());
        if (user == null){
            return Result.error(CodeMsg.USER_PASSWORD_UPDATE_ERROR);
        }
        return Result.success(user);
    }
}
