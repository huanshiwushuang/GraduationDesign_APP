package com.guohao.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class IOUtil {
	/**
	 * @param path
	 * 		�����ļ�·��
	 * 
	 * @return ���ر����ļ���С
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
	 * 		inputstream תΪ String
	 * @param in
	 * 		InputStream ��
	 * @param encode
	 * 		���뷽ʽ
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
