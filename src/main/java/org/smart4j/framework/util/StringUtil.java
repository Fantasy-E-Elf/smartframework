package org.smart4j.framework.util;

/**
 * 字符串工具类
 * @author wang yi zhe
 * @since 1.0.0
 */
public final class StringUtil {

    /**
     * 字符串分隔符
     */
    public static final String SEPARATOR = String.valueOf((char) 29);
    /**
     *判断字符串是否为空
     */
    public static boolean isEmpty(String str){
        if (str != null){
            str=str.trim();
        }
        return StringUtil.isEmpty(str);
    }
    /**
     *判断字符串是否非空
     */
    public static boolean isNotEmpty(String str){
        return !isEmpty(str);
    }

    public static String[] splitString(String body, String s) {

        String[]str = body.split(s);
        return str;
    }
}
