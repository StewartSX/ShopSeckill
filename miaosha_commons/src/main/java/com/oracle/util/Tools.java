package com.oracle.util;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.Random;
import java.util.UUID;

public class Tools {

    private static char[] operator = new char[]{'+', '-', '*'};
    private static char[] number = "0123456789".toCharArray();

    private static Random random = new Random();

    /**
     * 生成UUID码，不重复
     * @return 随机的UUID码
     */
    public static String uuid(){
        String str = UUID.randomUUID().toString();
        return str.replace("-", "");
    }

    /**
     * 随机生成一个表达式
     * @return 表达式
     */
    public static String makeExp(){
        return "" + number[random.nextInt(number.length)] + operator[random.nextInt(operator.length)] + number[random.nextInt(number.length)];
    }

    /**
     * 计算表达式的结果
     * @param exp 表达式
     * @return 输出的结果
     */
    public static Integer eval(String exp){
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("javascript");
        try {
            return (Integer) scriptEngine.eval(exp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        String exp = makeExp();
        System.out.println(exp);
        System.out.println(eval(exp));
    }
}
