package org.hotwheel.crypto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;

/**
 * Created by wangfeng on 16/8/29.
 * <p>
 * 算法：
 * <p>
 * byte转换：
 * <p>
 * 1、将原byte 由3个，3个分成一组， 不足3个的为一组
 * 2、将3个byte，一次统一右移两位，然后高位补两个0 ， 剩下的补上前一个字节移出来的字符（2个 4个或者6个bit）。这样3个byte一组会统一转成4个byte一组。
 * 3、不足3个的一组 出来的 在4个byte中以‘=’的byte值填充。
 * <p>
 * 4个一组的byte 到 string的转换：
 * <p>
 * 1、构造码表（加密表与解密表）
 * 2、加密表构造原则：加密表的长度是64，加密表的值（ascii码值）是解密表的索引
 * 3、解密表构造原则：长度一般为128，解密表的值是加密表的索引
 * 4、根据4个一组（由于前两位为0，所以最大值是 2^6 - 1）的byte值 用加密表得到字母值
 * 5、通过字母值作为索引用解密表 得到4个一组的byte
 * 6、将4个一组的byte 逆变换会 3个一组的byte
 * 7、然后byte再到其他数据结构如（字符串，文件等等）
 * </p>
 */
public class BasicBase64 {
    private Logger logger = LoggerFactory.getLogger(BasicBase64.class);

    private static final int RANGE = 0xff;
    //自定义码表 可随意变换字母排列顺序，然后会自动生成解密表
    private char[] Base64ByteToStr = new char[]{
            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',// 0 ~ 9
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',// 10 ~ 19
            'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd',// 20 ~ 29
            'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',// 30 ~ 39
            'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',// 40 ~ 49
            'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7',// 50 ~ 59
            '8', '9', '+', '/'// 60 ~ 63
    };

    private static byte[] StrToBase64Byte = new byte[128];

    public BasicBase64() {
        try {
            generateDecoder();
        } catch (Exception e) {
            logger.info("generateDecoder failed: ", e);
        }
    }

    public BasicBase64(String codeList) {
        Base64ByteToStr = codeList.toCharArray();
        try {
            generateDecoder();
        } catch (Exception e) {
            logger.info("generateDecoder failed: ", e);
        }
    }

    public BasicBase64(char[] codes) {
        Base64ByteToStr = codes;
        try {
            generateDecoder();
        } catch (Exception e) {
            logger.info("generateDecoder failed: ", e);
        }
    }

    private void generateDecoder() throws Exception {
        for (int i = 0; i <= StrToBase64Byte.length - 1; i++) {
            StrToBase64Byte[i] = -1;
        }
        for (int i = 0; i <= Base64ByteToStr.length - 1; i++) {
            StrToBase64Byte[Base64ByteToStr[i]] = (byte) i;
        }
    }

    private void showDecoder() throws Exception {
        String str = "";
        for (int i = 1; i <= StrToBase64Byte.length; i++) {
            str += (int) StrToBase64Byte[i - 1] + ",";
            if (i % 10 == 0 || i == StrToBase64Byte.length) {
                logger.info(str);
                str = "";
            }
        }
    }

    public String Base64Encode(byte[] bytes) throws Exception {
        StringBuilder res = new StringBuilder();
        //per 3 bytes scan and switch to 4 bytes
        for (int i = 0; i <= bytes.length - 1; i += 3) {
            byte[] enBytes = new byte[4];
            byte tmp = (byte) 0x00;// save the right move bit to next position's bit
            //3 bytes to 4 bytes
            for (int k = 0; k <= 2; k++) {// 0 ~ 2 is a line
                if ((i + k) <= bytes.length - 1) {
                    enBytes[k] = (byte) (((((int) bytes[i + k] & RANGE) >>> (2 + 2 * k))) | (int) tmp);//note , we only get 0 ~ 127 ???
                    tmp = (byte) (((((int) bytes[i + k] & RANGE) << (2 + 2 * (2 - k))) & RANGE) >>> 2);
                } else {
                    enBytes[k] = tmp;
                    tmp = (byte) 64;//if tmp > 64 then the char is '=' hen '=' -> byte is -1 , so it is EOF or not print char
                }
            }
            enBytes[3] = tmp;//forth byte
            //4 bytes to encode string
            for (int k = 0; k <= 3; k++) {
                if ((int) enBytes[k] <= 63) {
                    res.append(Base64ByteToStr[(int) enBytes[k]]);
                } else {
                    res.append('=');
                }
            }
        }
        return res.toString();
    }

    public byte[] Base64Decode(String val) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();//destination bytes, valid string that we want
        byte[] srcBytes = val.getBytes();
        byte[] base64bytes = new byte[srcBytes.length];
        //get the base64 bytes (the value is -1 or 0 ~ 63)
        for (int i = 0; i <= srcBytes.length - 1; i++) {
            int ind = (int) srcBytes[i];
            base64bytes[i] = StrToBase64Byte[ind];
        }
        //base64 bytes (4 bytes) to normal bytes (3 bytes)
        for (int i = 0; i <= base64bytes.length - 1; i += 4) {
            byte[] deBytes = new byte[3];
            int delen = 0;// if basebytes[i] = -1, then debytes not append this value
            byte tmp;
            for (int k = 0; k <= 2; k++) {
                if ((i + k + 1) <= base64bytes.length - 1 && base64bytes[i + k + 1] >= 0) {
                    tmp = (byte) (((int) base64bytes[i + k + 1] & RANGE) >>> (2 + 2 * (2 - (k + 1))));
                    deBytes[k] = (byte) ((((int) base64bytes[i + k] & RANGE) << (2 + 2 * k) & RANGE) | (int) tmp);
                    delen++;
                }
            }
            for (int k = 0; k <= delen - 1; k++) {
                bos.write((int) deBytes[k]);
            }
        }
        return bos.toByteArray();
    }

    private final static int kBase64Len = 64;
    private final static String kBase64Code = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_-";

    public static char[] genMeter() {
        char[] cl = new char[kBase64Len];
        for (int i = 0; i < kBase64Len; i++) {
            cl[i] = kBase64Code.charAt(i);
        }
        for (int i = 0; i < kBase64Len; i++) {
            int x = kBase64Len - i;
            int t = (int) (Math.random() * x);
            char temp = cl[t];
            cl[t] = cl[x - 1];
            cl[x - 1] = temp;
        }
        return cl;
    }

    public static void main(String[] args) throws Exception {
        String token = "cfI3A4v6_-jR9otrK1QYBx0yVbdlGNzDisJuhFSHTUCkwWEXM2ZgLaeqnOp8m5P7";
        BasicBase64 nb = new BasicBase64(token);
        String enStr = "zZ-hVy1s_uU8_SOsl0BJtJ_29u9L_JMJNv5LV0MJtuA29Y45RI-gNv4LNy9Jtucw_SaFGqosbeBJtJ-YxBo31xoY_JMJNvFWbyoLV0aM_uTJ9uc2oJLMtQLMoZc29gTLo3TZtQnq9gcJRI-TlqoL_uTJ9YcMRuGgRuAqRu9JRI-sVq-XGqoBd0aF_uTZDK==";
        // client解码
        String deStr = new String(nb.Base64Decode(enStr));
        System.out.println("decoder:" + deStr);
        //String srcStr = "testfewa,./;'p[097&^%$$##!@#FDGSERH中国测试中文";
//        String srcStr = "{\\\"name\\\":\\\"vicken\\\",\\\"age\\\":20   }";
        String srcStr = "中文输入";
        System.out.println(" source:" + srcStr);
        enStr = nb.Base64Encode(srcStr.getBytes());
        System.out.println("encoder:" + enStr);
        deStr = new String(nb.Base64Decode(enStr));
        System.out.println("decoder:" + deStr);

        for (int i = 0; i < 10; i++) {
            System.out.println("---------------------------------------------------------");
            char[] cl = BasicBase64.genMeter();
            String codeList = new String(cl);
            System.out.println("base64-code: " + codeList);
            nb = new BasicBase64(codeList);
            // json原文
            System.out.println(" source:" + srcStr);

            // server端编码
            enStr = nb.Base64Encode(srcStr.getBytes());
            System.out.println("encoder:" + enStr);

            // client解码
            deStr = new String(nb.Base64Decode(enStr));
            System.out.println("decoder:" + deStr);
        }
    }

}
