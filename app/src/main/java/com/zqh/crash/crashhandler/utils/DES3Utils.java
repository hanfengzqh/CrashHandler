package com.zqh.crash.crashhandler.utils;

import android.text.TextUtils;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class DES3Utils {
	// 向量
	static byte[] iv_bytes = { 0, 0, 0, 0, 0, 0, 0, 0 };
	// 加解密统一使用的编码方式
	private final static String encoding = "utf-8";
	/**
	 * hex字符串转byte数组<br/>
	 * 2个hex转为一个byte
	 * 
	 * @param src
	 * @return
	 */
	public static byte[] hex2Bytes1(String src) {
		byte[] res = new byte[src.length() / 2];
		char[] chs = src.toCharArray();
		int[] b = new int[2];

		for (int i = 0, c = 0; i < chs.length; i += 2, c++) {
			for (int j = 0; j < 2; j++) {
				if (chs[i + j] >= '0' && chs[i + j] <= '9') {
					b[j] = (chs[i + j] - '0');
				} else if (chs[i + j] >= 'A' && chs[i + j] <= 'F') {
					b[j] = (chs[i + j] - 'A' + 10);
				} else if (chs[i + j] >= 'a' && chs[i + j] <= 'f') {
					b[j] = (chs[i + j] - 'a' + 10);
				}
			}

			b[0] = (b[0] & 0x0f) << 4;
			b[1] = (b[1] & 0x0f);
			res[c] = (byte) (b[0] | b[1]);
		}

		return res;
	}
	/**
	 * @param secretKey
	 *            密钥
	 * @param plainText
	 *            待加密数据
	 * @return
	 */
	public static String get3DESData(byte[] secretKey, String plainText) {
		Key deskey = null;
		String encrypt = "";
		int length = 8 - plainText.length()%8;
		if (length != 8) {
			for (int i = 0; i < length; i++) {
				plainText += new String(hex2Bytes1("00"));
			}
		}
		
		try {
//			DESedeKeySpec spec = new DESedeKeySpec(secretKey);
			SecretKeySpec spec = new SecretKeySpec(secretKey, "DESede");
			SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("DESede");// 定义加密算法，可用des,desede,blowfish
			deskey = keyfactory.generateSecret(spec);// 生成秘钥
			Cipher cipher = Cipher.getInstance("DESede/CBC/NOPadding");// 算法名称/加密模式/填充方式
			IvParameterSpec ips = new IvParameterSpec(iv_bytes);
			cipher.init(Cipher.ENCRYPT_MODE, deskey, ips);
			byte[] encryptData = cipher.doFinal(plainText.getBytes(encoding));
			encrypt = Base64Utils.encode(encryptData);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return encrypt;
	}

	/**
	 * 3DES解密
	 * 
	 * @param encryptText
	 *            密文
	 * @param secretKey
	 *            加密时的key
	 * @return 返回解密后的明文
	 * @throws Exception
	 */
	public static String decrypt(String encryptText, byte[] secretKey){
		Key deskey = null;
		byte[] decryptData;
		int length = 0;
		try {
//			DESedeKeySpec spec = new DESedeKeySpec(secretKey.getBytes());
			SecretKeySpec spec = new SecretKeySpec(secretKey, "DESede");
			SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("DESede");
			deskey = keyfactory.generateSecret(spec);
			Cipher cipher = Cipher.getInstance("DESede/CBC/NOPadding");
			IvParameterSpec ips = new IvParameterSpec(iv_bytes);
			cipher.init(Cipher.DECRYPT_MODE, deskey, ips);
//			cipher.init(Cipher.DECRYPT_MODE, deskey);
			decryptData = cipher.doFinal(Base64Utils.decode(encryptText));
			length = decryptData.length;
//			Logger.d("zqh", "length = "+length);
			for (int i = 0; i < decryptData.length; i++) {
				if (decryptData[i]==0) {
					length--;
				}
			}
			
			byte[] decryptDataResult = new byte[length];
//			for (int i = 0; i < decryptDataResult.length; i++) {
//				decryptDataResult[i]=decryptData[i];
//			}
//			Logger.d("zqh", "decryptDataResult = "+decryptDataResult.length);
			System.arraycopy(decryptData, 0, decryptDataResult, 0, length);
			return new String(decryptDataResult, encoding);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 将字符串转化为2进制字符串
	 * 
	 * @param str
	 * @return
	 */
	public static String StrToBinstr(String str) {
		if (TextUtils.isEmpty(str))
			return "";

		char[] strChar = str.toCharArray();
		String result = "";
		for (int i = 0; i < strChar.length; i++) {
			result += Integer.toBinaryString(strChar[i]) + "";
		}
		return result;
	}
}
