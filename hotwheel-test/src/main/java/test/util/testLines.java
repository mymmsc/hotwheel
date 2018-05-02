package test.util;

/**
 * 手势密码连线测试
 *
 * @author wangfeng
 * @date 2018/4/25
 */
public class testLines {

    private String name;
    private final static int points[][] = {{0,1,2},{3,4,5},{6,7,8}};
    private final static int minLength = 3;
    private final static String invalidPattern[] = {"13","31","46","64","79","97","17","71","28","82","39","93","19","91","37","73",
    "34","43","16","61","67","76","49","94","27","72","29","92","18","81","38","83"};
    private static long count = 0;

    public static void main(String[] argv) {

        for (int i = minLength; i <=9; i++) {
            permutation("123456789", "", i);
            System.out.println(i + "个长度密码, count=" + count);
            count = 0;
        }


        for (int i = minLength + 1; i <=9; i++) {
            int pswdLen = i;
            long times = 1;
            times = pa(9) / pa(9 - pswdLen);
            System.out.println("len[" + pswdLen + "]=" + times);
        }
    }

    private static long pa(int n) {
        long ret = 1;
        for (int i = 1; i <= n; i++) {
            ret *= i;
        }
        return ret;
    }

    public static void permutation(String input, String prefix, int len){
        if(/*input.length()==0*/prefix.length() == len){
            boolean found = false;
            for (String invalid : invalidPattern) {
                if (prefix.indexOf(invalid) >= 0) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                count ++;
                //System.out.println(prefix);
            }
        } else {
            for(int i=0;i<input.length();i++){
                StringBuilder sb = new StringBuilder(input);
                Character c = sb.charAt(i);
                sb.deleteCharAt(i);
                permutation(sb.toString(), prefix+c, len);
            }
        }
        //return count;
    }
}
