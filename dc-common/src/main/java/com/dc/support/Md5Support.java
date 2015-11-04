package com.dc.support;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Support {
	public static String convertToMd5(String input) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(input.getBytes());
		byte[] digest = md.digest();
		StringBuffer sb = new StringBuffer();
		for (byte b : digest) {
			sb.append(String.format("%02x", b & 0xff));
		}
		return sb.toString();
	}
}
