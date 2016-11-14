/**
 * @(#)RegExp.java 6.3.9 09/10/02
 * <p>
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.api.assembly;

import org.hotwheel.algorithms.ConsistentHash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * 正则表达式
 *
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-api 6.3.9
 */
public final class RegExp {
    private static Logger logger = LoggerFactory.getLogger(RegExp.class);

    public static boolean valid(String s, String exp) {
        boolean bRet = false;
        try {
            Pattern p = Pattern.compile(exp, Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(s);
            if (m.find()) {
                bRet = true;
            }
        } catch (PatternSyntaxException e) {
            logger.error("", e);
        } catch (IllegalArgumentException e) {
            logger.error("", e);
        }

        return bRet;
    }

    /**
     * 匹配正则表达式
     *
     * @param s
     * @param exp
     * @return list
     */
    public static ArrayList<String> match(String s, String exp) {
        ArrayList<String> sList = null;
        Pattern p = Pattern.compile(exp, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(s);
        boolean rs = m.find();
        if (rs) {
            for (int i = 1; i <= m.groupCount(); i++) {
                if (sList == null) {
                    sList = new ArrayList<String>();
                }
                sList.add(m.group(i));
            }
        }

        return sList;
    }

    /**
     * 提取正则表达式匹配字符串
     *
     * @param s
     * @param exp
     * @param defaultValue
     * @return String
     */
    public static String get(String s, String exp, String defaultValue) {
        String sRet = defaultValue;
        Pattern p = Pattern.compile(exp, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(s);
        boolean rs = m.find();
        if (rs) {
            sRet = m.group(0);
        }

        return sRet;
    }

    public static Map<String, String> match(String s) {
        Map<String, String> map = new HashMap<String, String>();
        String[] sl = s.split(",");
        for (int i = 0; i < sl.length; i++) {
            String[] vsl = sl[i].split("-");
            if (vsl.length == 2) {
                map.put(vsl[0], vsl[1]);
            }
        }
        return map;
    }

    /**
     * 解析CSV格式文本行
     *
     * @param string csv格式文本行
     * @param debug  接受一个布尔类型参数, 是否打开调试模式输出每个元素
     * @return 字符串组
     */
    public static List<String> parseCsv(String string, boolean debug) {
        List<String> lRet = null;
        final String regex = "(?:^|,\\s{0,})([\"]?)\\s{0,}((?:.|\\n|\\r)*?)\\1(?=[,]\\s{0,}|$)";
        try {
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(string);
            String value = null;
            while (m != null && m.find()) {
                for (int i = 2; i <= m.groupCount(); i++) {
                    value = m.group(i);
                    if (debug) {
                        System.out.println(value);
                    }
                    if (lRet == null) {
                        lRet = new ArrayList<String>();
                    }
                    lRet.add(value);
                }
            }
        } catch (PatternSyntaxException e) {
            logger.error("", e);
        }

        return lRet;
    }

    /**
     * 解析CSV格式文本行
     *
     * @param string csv格式文本行
     * @return 字符串组
     */
    public static List<String> parseCsv(String string) {
        return parseCsv(string, false);
    }

    public static void main(String[] args) {

        ConsistentHash<String> conHash = new ConsistentHash<String>(100);
        for (int i = 0; i < 3; i++) {
            conHash.addNode("10.1.15.1" + i);
        }
        System.out.println("----------------------------------------------------------------");
        for (int i = 0; i < 10; i++) {
            String user = "cookie:" + i;
            String value = conHash.getShardInfo(user);
            System.out.println("user=[" + user + "]\t" + "node=" + value);
        }
        String rmHost = "10.1.15.12";
        System.out.println("删除: " + rmHost);
        conHash.delNode(rmHost);
        System.out.println("----------------------------------------------------------------");
        for (int i = 0; i < 10; i++) {
            String user = "cookie:" + i;
            String value = conHash.getShardInfo(user);
            System.out.println("user=[" + user + "]\t" + "node=" + value);
        }

        rmHost = "10.1.15.12";
        System.out.println("增加: " + rmHost);
        conHash.addNode(rmHost);
        System.out.println("----------------------------------------------------------------");
        rmHost = "10.1.15.13";
        System.out.println("增加: " + rmHost);
        conHash.addNode(rmHost);
        for (int i = 0; i < 10; i++) {
            String user = "cookie:" + i;
            String value = conHash.getShardInfo(user);
            System.out.println("user=[" + user + "]\t" + "node=" + value);
        }

        String str = "00-未审核,01-正常,11-应聘信息,12-招聘信息";

        Map<String, String> sl = RegExp.match(str);
        System.out.println(sl.get("01"));
    }
}
