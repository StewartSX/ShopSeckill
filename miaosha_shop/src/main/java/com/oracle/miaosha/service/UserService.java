package com.oracle.miaosha.service;

import com.oracle.miaosha.mapper.UserMapper;
import com.oracle.miaosha.vo.User;
import com.oracle.util.Md5Util;
import com.oracle.vo.CodeMsg;
import com.oracle.vo.RedisKey;
import com.oracle.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Service
public class UserService {
    @Autowired
    UserMapper userMapper;

    @Autowired
    RedisService redisService;

    private User getUserForToken(String token){
        User user = redisService.get(RedisKey.USER_LOGIN, token, User.class);
        if (user != null){
            redisService.set(RedisKey.USER_LOGIN, token, user, 30 * 60);
        }
        return user;
    }

    @Transactional
    public Result<User> login(String tel, String pass){
        User user = userMapper.getUserByTel(tel);
        // 判断用户是否存在
        if (user == null){
            return Result.error(CodeMsg.USER_NOT_FOUND_ERROR);
        }
        // 判断密码是否正确
        String salt = user.getSalt();
        String passwd = Md5Util.formPassToDBPass(pass, salt);
        if (passwd.equals(user.getPassword())){
            // 登录成功后，登录次数+1
            // 更新最后访问时间
            return Result.success(user);
        } else {
            return Result.error(CodeMsg.USER_PASSWORD_ERROR);
        }
    }

    @Transactional
    public User updatePassword(String token, String password){
        //1.查找用户
        User user = getUserForToken(token);
        if (user == null){
            return null;
        }
        //2.MD5加盐加密
        User newUser = new User();
        String md5Password = Md5Util.formPassToDBPass(password, user.getSalt());
        newUser.setUserid(user.getUserid());
        newUser.setPassword(md5Password);
        //3.更新数据库
        userMapper.updateByPrimaryKeySelective(newUser);
        //4.将User对象写入Redis缓存
        user.setPassword(md5Password);
        redisService.set(RedisKey.USER_LOGIN, token, user, 30 * 60);

        return user;
    }
}
