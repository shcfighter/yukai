package com.zero.common.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5 {

	public static byte[] digest(byte[] b){
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        return md5.digest(b); 
	}
	
	public static String digest(String beforeDigest){
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        byte[] bytes = md5.digest(beforeDigest.getBytes()); 
        StringBuffer stringBuffer = new StringBuffer();  
        for (byte b : bytes){  
            int bt = b&0xff;  
            if (bt < 16){  
                stringBuffer.append(0);  
            }   
            stringBuffer.append(Integer.toHexString(bt));  
        }  
        return stringBuffer.toString();
	}
}
