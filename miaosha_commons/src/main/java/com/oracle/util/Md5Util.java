package com.oracle.util;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * MD5加密类
 * 两次MD5加密（前后端）
 */
public class Md5Util {

    // 定义客户端中JS的盐，为固定值
    private static String salt = "mjuflapywnduiwhh";

    /**
     * md5算法，工具类中已经包装好
     * @param password 原始密码字符串
     * @return MD5加密后的字符串
     */
    public static String md5(String password){
        return DigestUtils.md5Hex(password);
    }

    /**
     * 客户端的密码加密后传输给服务端（第一次MD5）
     * @param pass 客户端输入的密码
     * @return MD5加密后的密码
     */
    public static String inputPassToFormPass(String pass){
        String password = "" + salt.charAt(4) + salt.charAt(8) + pass + salt.charAt(1) + salt.charAt(6);
        return md5(password);
    }

    /**
     * 服务端的密码加密后存入数据库（第二次MD5）
     * @param pass 服务端接收到的密码
     * @param salt 用户自己的盐
     * @return MD5加密后的密码
     */
    public static String formPassToDBPass(String pass, String salt){
        String password = "" + salt.charAt(2) + salt.charAt(0) + pass + salt.charAt(3);
        return md5(password);
    }

    /**
     * 集成两个方法，讲客户端的密码两次加密后存入数据库
     * @param pass 客户端输入的密码
     * @param salt 客户端JS固定的盐
     * @return 两次加密后要存入数据库中的密码
     */
    public static String inputPassToDBPass(String pass, String salt){
        return formPassToDBPass(inputPassToFormPass(pass), salt);
    }
}
