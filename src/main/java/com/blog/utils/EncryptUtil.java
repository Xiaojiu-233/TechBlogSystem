package com.blog.utils;

import java.security.MessageDigest;
import java.util.Base64;

public class EncryptUtil {

    public static String MD5Encrypt(String s){
        String ret = null;
        try{
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(s.getBytes());
            byte[] digestBytes = md5.digest();
            ret= new String(Base64.getEncoder().encodeToString(digestBytes));
        }catch (Exception ignored){}
        return ret;
    }
}
