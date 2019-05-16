package com.mhd.superwifidirect.Util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;

public class ByteUtil {
    public static byte[] integerToBytes(int integer, int len) {
//   if (integer < 0) { throw new IllegalArgumentException("Can not cast negative to bytes : " + integer); }
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        for (int i = 0; i < len; i ++) {
            bo.write(integer);
            integer = integer >> 8;
        }
        return bo.toByteArray();
    }
    public static byte[] readBytes(InputStream in, long length) throws IOException {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int read = 0;
        while (read < length) {
            int cur = in.read(buffer, 0, (int)Math.min(1024, length - read));
            if (cur < 0) { break; }
            read += cur;
            bo.write(buffer, 0, cur);
        }
        return bo.toByteArray();
    }

    public static int bytesToInteger(byte[] bytes)
    {
        int offset=0;
        //return new BigInteger(bytes).intValue();
        int value;
        value = (int) ((bytes[offset] & 0xFF) | ((bytes[offset + 1] & 0xFF) << 8) | ((bytes[offset + 2] & 0xFF) << 16) | ((bytes[offset + 3] & 0xFF) << 24));
        return value;
    }
    public static  byte[] transFromObjectToByte(Object obj){
        byte[] bytes = null;
        try {
            // object to bytearray
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream oo = new ObjectOutputStream(bo);
            oo.writeObject(obj);

            bytes = bo.toByteArray();

            bo.close();
            oo.close();
        } catch (Exception e) {
            System.out.println("translation" + e.getMessage());
            e.printStackTrace();
        }
        return bytes;
    }
    public static String byteToStr(byte[] buffer) {
        try {
            int length = 0;
            for (int i = 0; i < buffer.length; ++i) {
                if (buffer[i] == 0) {
                    length = i;
                    break;
                }
            }
            return new String(buffer, 0, length, "UTF-8");
        } catch (Exception e) {
            return "";
        }
    }


}
