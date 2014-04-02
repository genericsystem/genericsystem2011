package org.genericsystem.security.hachage;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Hashing {

	private static final String ALGORITHM = "MD5";

	/**
	 * @param password
	 * @return hashed password
	 * @throws NoSuchAlgorithmException
	 */
	public static String hashPassword(String password) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance(ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		}
		md.update(password.getBytes());
		byte byteData[] = md.digest();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < byteData.length; i++) {
			sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
		}

		return sb.toString();
	}

}
