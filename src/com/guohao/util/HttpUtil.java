package com.guohao.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import com.guohao.inter.HttpCallBackListenerBitmap;
import com.guohao.inter.HttpCallBackListenerString;
import com.guohao.model.UserTable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class HttpUtil {
	public static final int VISIT_USER_TABLE_LOGIN = 0;
	public static final int VISIT_USER_TABLE_REGISTE = 1;
	public static final int VISIT_USER_TABLE_UPDATE_USERNAME = 2;
	public static final int VISIT_USER_TABLE_UPDATE_PWD = 3;
	public static final int VISIT_USER_TABLE_FIND_ACCOUNT = 6;

	public static final int VISIT_MOVIE_INFO_DETAIL_OK = 4;
	public static final int VISIT_MOVIE_INFO_DETAIL_ERROR = 5;

	public static void getMovieDetail(final Handler handler, final String movieId) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				String param = "?want=movieDetail&movieInfoTableId=" + movieId;
				HttpURLConnection connection = getGetConnection(Data.URL_MOVIE_INFO_TABLE + param);
				Message msg = handler.obtainMessage();
				try {
					if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
						String result = IOUtil.inputstreamToString(connection.getInputStream(), "UTF-8");
						msg.what = VISIT_MOVIE_INFO_DETAIL_OK;
						msg.obj = result;
					} else {
						msg.what = VISIT_MOVIE_INFO_DETAIL_OK;
						msg.obj = connection.getResponseCode();
					}
				} catch (IOException e) {
					e.printStackTrace();
					msg.what = VISIT_MOVIE_INFO_DETAIL_ERROR;
//					msg.obj = e.toString();
					msg.obj = "网络异常";
				} finally {
					handler.sendMessage(msg);
				}
			}
		}).start();
	}

	/**
	 * @param flag
	 *            区分是注册还是登录，参数为此类的静态变量
	 * @param url
	 *            请求的 url 地址，此处访问用户表
	 * @param user
	 *            参数对象，注册 与 登录 不同
	 * @param listener
	 *            监听的回调函数
	 * 
	 */
	public static void visitUserTable(final int flag, final String url, final UserTable user,
			final HttpCallBackListenerString listener) {
		new Thread(new Runnable() {

			public void run() {
				Log.d("guohao", "更新请求地址："+url);
				HttpURLConnection connection = HttpUtil.getPostConnection(url);
				BufferedReader reader = null;
				try {
					DataOutputStream out = new DataOutputStream(connection.getOutputStream());

					// 根据flag的不同，表示 注册 和 登录
					String content = "";
					switch (flag) {
					case HttpUtil.VISIT_USER_TABLE_REGISTE:
						content = "want=registe&userId=" + URLEncoder.encode(user.getUserId(), Data.ENCODE) + "&pwd="
								+ URLEncoder.encode(user.getUserPwd(), Data.ENCODE);
						break;
					case HttpUtil.VISIT_USER_TABLE_LOGIN:
						content = "want=login&userId=" + URLEncoder.encode(user.getUserId(), Data.ENCODE) + "&pwd="
								+ URLEncoder.encode(user.getUserPwd(), Data.ENCODE);
						break;
					case HttpUtil.VISIT_USER_TABLE_UPDATE_USERNAME:
						content = "want=update_username&userId=" + URLEncoder.encode(user.getUserId(), Data.ENCODE) + "&username="+ URLEncoder.encode(user.getUsername(), Data.ENCODE);
						break;
					case HttpUtil.VISIT_USER_TABLE_UPDATE_PWD:
						content = "want=update_pwd&userId=" + URLEncoder.encode(user.getUserId(), Data.ENCODE) + "&pwd="+ URLEncoder.encode(user.getUserPwd(), Data.ENCODE);
						break;
					case HttpUtil.VISIT_USER_TABLE_FIND_ACCOUNT:
						content = "want=find_account&userId="+URLEncoder.encode(user.getUserId(), Data.ENCODE);
						break;
					}
					
					out.writeBytes(content);
					out.flush();
					out.close();
					connection.connect();

					if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
						reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
						String line = "";
						String response = "";
						while ((line = reader.readLine()) != null) {
							response += line;
						}
						listener.onFinish(response);
					} else {
//						listener.onError("ResponseCode：" + connection.getResponseCode());
						listener.onError("网络异常");
					}
				} catch (IOException e) {
					listener.onError("网络异常");
				} finally {
					connection.disconnect();
					if (reader != null) {
						try {
							reader.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}

			}
		}).start();
	}

	public static void visitMovieInfoTable(final String want, final String startLine, final String endLine,
			final String searchCondition, final String searchValue, final HttpCallBackListenerString listener) {
		new Thread(new Runnable() {

			public void run() {
				String address = Data.URL_MOVIE_INFO_TABLE + "?want=" + want + "&startLine=" + startLine + "&endLine="
						+ endLine;
				if (want.equals(Data.Search)) {
					try {
						address += "&searchCondition=" + searchCondition + "&searchValue="
								+ URLEncoder.encode(searchValue, Data.ENCODE);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
				HttpURLConnection connection = HttpUtil.getGetConnection(StringUtil.urlEncode(address, Data.ENCODE));
				BufferedReader reader = null;
				Log.d("guohao", "请求地址：" + address);
				try {
					if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
						reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
						String line = "";
						String response = "";
						while ((line = reader.readLine()) != null) {
							response += line;
						}
						listener.onFinish(response);
					} else {
						listener.onError("网络异常");
					}

				} catch (IOException e) {
					listener.onError("网络异常");
				} finally {
					connection.disconnect();
					if (reader != null) {
						try {
							reader.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}).start();
	}

	public static void visitMovieInfoTableCondition(final int startLine, final int endLine, final int language,
			final int type, final int date, final int region, final int order,
			final HttpCallBackListenerString listener) {
		new Thread(new Runnable() {

			public void run() {
				String address = Data.URL_MOVIE_INFO_TABLE + "?want=condition&startLine=" + startLine + "&endLine="
						+ endLine + "&language=" + language + "&type=" + type + "&date=" + date + "&region=" + region
						+ "&order=" + order;
				HttpURLConnection connection = HttpUtil.getGetConnection(StringUtil.urlEncode(address, Data.ENCODE));
				BufferedReader reader = null;
				Log.d("guohao", "我的请求：" + address);
				try {
					if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
						reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
						String line = "";
						String response = "";
						while ((line = reader.readLine()) != null) {
							response += line;
						}
						listener.onFinish(response);
					} else {
						listener.onError("网络异常");
					}
				} catch (IOException e) {
					listener.onError("网络异常");
				} finally {
					connection.disconnect();
					if (reader != null) {
						try {
							reader.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}).start();
	}

	public static HttpURLConnection getPostConnection(String address) {
		HttpURLConnection connection = null;
		try {
			URL url = new URL(address);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(true);
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setConnectTimeout(20 * 1000);
			connection.setReadTimeout(20 * 1000);
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return connection;
	}

	public static HttpURLConnection getGetConnection(String address) {
		HttpURLConnection connection = null;
		try {
			URL url = new URL(address);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(10 * 1000);
			connection.setReadTimeout(10 * 1000);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return connection;
	}
}
