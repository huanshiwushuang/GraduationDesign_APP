package com.guohao.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class IOUtil {
	/**
	 * @param path
	 * 		传入文件路径
	 * 
	 * @return 返回本地文件大小
	 * 
	 * */
	public static long getLocalFileSize(String path) {
		long fileSize = -1;
		File file = new File(path);
		if (file.exists()) {
			fileSize = file.length();
		}
		return fileSize;
	}
	
	/**
	 * @see 
	 * 		inputstream 转为 String
	 * @param in
	 * 		InputStream 流
	 * @param encode
	 * 		编码方式
	 * 
	 * */
	public static String inputstreamToString(InputStream in, String encode) {
		String line = null;
		StringBuilder builder = null;
		BufferedReader reader = null;
		try {
			builder = new StringBuilder();
			reader = new BufferedReader(new InputStreamReader(in, encode));
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return builder.toString();
	}
}
