package me.yimu.wexxar.utils;

import java.security.MessageDigest;

/**
 * Created by linwei on 2018/3/5.
 */

public class MD5Utils {

    public static String getMd5(byte[] bytes) {
        try {
            byte[] bs = MessageDigest.getInstance("MD5").digest(bytes);
            StringBuilder sb = new StringBuilder(40);
            for (byte x : bs) {
                if ((x & 0xff) >> 4 == 0) {
                    sb.append("0")
                            .append(Integer.toHexString(x & 0xff));
                } else {
                    sb.append(Integer.toHexString(x & 0xff));
                }
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return new String(bytes);
        }
    }
}
