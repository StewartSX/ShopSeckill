package com.oracle.vo;

import java.io.Serializable;

public class CodeMsg implements Serializable {
    private int code;
    private String msg;

    public static CodeMsg SERVER_ERROR = new CodeMsg(500500, "服务器内容错误！");
    public static CodeMsg USER_NOT_FOUND_ERROR = new CodeMsg(500501, "用户未找到！");
    public static CodeMsg USER_NOT_LOGIN_ERROR = new CodeMsg(500502, "用户未登录！");
    public static CodeMsg USER_PASSWORD_ERROR = new CodeMsg(500503, "用户密码错误！");
    public static CodeMsg USER_PASSWORD_UPDATE_ERROR = new CodeMsg(500504, "用户密码修改失败！");
    public static CodeMsg MIAOSHA_STOCK_ZERO = new CodeMsg(500601, "商品库存不足，秒杀失败！");
    public static CodeMsg MIAOSHA_MANY_TIMES = new CodeMsg(500602, "不能重复秒杀！");
    public static CodeMsg MIAOSHA_ORDER_STATUS_QUEUING = new CodeMsg(500701, "排队中......");
    public static CodeMsg MIAOSHA_ORDER_STATUS_FAILED = new CodeMsg(500702, "秒杀失败！");
    public static CodeMsg PATH_NOT_EXISTS = new CodeMsg(500703, "请求路径不存在！");
    public static CodeMsg ACCESS_TOO_MANY = new CodeMsg(500704, "用户访问太频繁！请稍后再试！");

    public CodeMsg() {
    }

    public CodeMsg(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "CodeMsg{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }
}
