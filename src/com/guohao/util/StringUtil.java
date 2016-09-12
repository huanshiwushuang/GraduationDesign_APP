package com.guohao.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.R.integer;
import android.util.Log;

public class StringUtil {
	public static Boolean checkAccount(String account) {
		Matcher matcher = Pattern.compile("^\\d{11}$").matcher(account);
		if (!matcher.find()) {
			matcher = Pattern.compile("^[a-z0-9]+([._\\-]*[a-z0-9])*@([a-z0-9]+[-a-z0-9]*[a-z0-9]+.){1,63}[a-z0-9]+$").matcher(account);
			if (!matcher.find()) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * @author ����
	 * 
	 * @param address
	 * 			��Ҫ����� url ��ַ
	 * @param encodeType
	 * 			url ����ʹ��ʲô�������ͣ�����UTF-8��GBK��
	 * @return ���ر����� url ��ַ
	 * 
	 * */
	public static String urlEncode(String address, String encodeType) {
		String encodedString = null;
		try {
			encodedString = URLEncoder.encode(address, encodeType).replaceAll("%3A", ":").replaceAll("%2F", "/").replaceAll("%3F", "?").replaceAll("%26", "&").replaceAll("%3D", "=");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return encodedString;
	}
	/**
	 * @author ����
	 * 
	 * @param address
	 * 			��Ҫ����� url ��ַ
	 * @param decodeType
	 * 			url ����ʹ��ʲô�������ͣ�����UTF-8��GBK��
	 * @return ���ؽ����� url ��ַ
	 * 
	 * */
	public static String urlDecode(String address, String decodeType) {
		String decodedString = null;
		try {
			decodedString = URLDecoder.decode(address, decodeType);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return decodedString;
	}
	
	//list ȥ��
	public static List<String> listValueOnly(List<String> list) {
		List<Integer> l = new ArrayList<Integer>();
		for (int i = 0; i < list.size()-1; i++) {
			for (int j = i+1; j < list.size(); j++) {
				if (list.get(i).equals(list.get(j)) || list.get(i) == list.get(j)) {
					l.add(i);
					break;
				}
			}
		}
		for (int i = 0; i < l.size(); i++) {
			list.remove(l.get(i));
		}
		return list;
	}
	
	//����תʱ���ʽ 00:00
	public static String millsecondsToTime(long longMillseconds) {
		long secondsTime = longMillseconds/1000;
		int hours = (int) (secondsTime/60/60);
		int minutes = (int) (secondsTime/60%60);
		int seconds = (int) (secondsTime%60);
		String returnString = "";
		
		if (hours > 9) {
			returnString += hours+":";
		}else {
			if (hours > 0) {
				returnString += "0"+hours+":";
			}
		}
		if (minutes > 9) {
			returnString += minutes+":";
		}else {
			if (minutes > 0) {
				returnString += "0"+minutes+":";
			}else {
				returnString += "00"+":";
			}
		}
		if (seconds > 9) {
			returnString += seconds;
		}else {
			if (seconds > 0) {
				returnString += "0"+seconds;
			}else {
				returnString += "00";
			}
		}
		return returnString;
	}
}
