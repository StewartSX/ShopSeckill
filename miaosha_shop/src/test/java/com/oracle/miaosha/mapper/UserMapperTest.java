package com.oracle.miaosha.mapper;

import com.oracle.miaosha.vo.User;
import com.oracle.miaosha.MiaoshaShopApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MiaoshaShopApplication.class)
public class UserMapperTest {
    @Autowired
    private UserMapper userMapper;

    @Test
    public void testUser(){
        User user = userMapper.getUserByTel("181");
        System.out.println(user);
    }
}