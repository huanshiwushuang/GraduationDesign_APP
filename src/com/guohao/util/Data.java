package com.guohao.util;

import io.vov.vitamio.MediaPlayer;

public class Data {
//	public static final String URL_USER_TABLE = "http://80smovie.tk/user.gh";
//	public static final String URL_MOVIE_INFO_TABLE = "http://80smovie.tk/movieInfo.gh";
//	public static final String URL_MOVIE_PLAY_URL = "http://80smovie.tk/moviePlay.gh";
	
	public static final String URL_USER_TABLE = "http://115.159.111.179/user.gh";
	public static final String URL_MOVIE_INFO_TABLE = "http://115.159.111.179/movieInfo.gh";
	public static final String URL_MOVIE_PLAY_URL = "http://115.159.111.179/moviePlay.gh";
	
//	public static final String URL_USER_TABLE = "http://192.168.0.101:8080/GraduationDesign/user.gh";
//	public static final String URL_MOVIE_INFO_TABLE = "http://192.168.0.101:8080/GraduationDesign/movieInfo.gh";
//	public static final String URL_MOVIE_PLAY_URL = "http://192.168.0.101:8080/GraduationDesign/moviePlay.gh";
	
	public static final String ENCODE = "UTF-8";
	
	public static final String KEY_CODE = "code";
	public static final String KEY_DATA = "data";
	public static final String VALUE_OK = "ok";
	public static final String VALUE_ERROR = "error";
	public static final String VALUE_CONTENT = "content";
	
	public static final String Recommend = "recommend";
	public static final String Hot = "hot";
	public static final String Newest = "newest";
	public static final String HighMark = "highMark";
	public static final String Search = "search";
	
	public static final String MOVIE_TV = "TV";
	public static final String MOVIE_1024P = "1024P";
	public static final String MOVIE_640P = "640P";
	public static final String MOVIE_480P = "480P";
	
	
	
	//---最后进入的时间
	public static final String K_Time_Last_Into = "3";
	
	//---用户 id 和 用户 name
	public static final String K_User_Id = "4";
	public static final String K_User_Name = "5";
	//---用户上次登录account
	public static final String K_User_Last_Login_Account = "6";
	
	//保存图片的宽度和高度
	public static final String K_IMAGE_WIDTH = "image_width";
	public static final String K_IMAGE_HEIGHT = "image_height";
	
	
	
	//----------------------------------------可以进行设置的-----------------------------------------------
	//配置文件
	//---加载大图还是小图
	public static final String K_Pic_Loading = "0";
	public static final int V_Pic_Loading_Big = 1;
	public static final int V_Pic_Loading_Small = 2;
	//视频播放质量的高度
	public static final String K_Video_Quality = "video_quality";
	public static final int V_Video_Quality_High = MediaPlayer.VIDEOQUALITY_HIGH;
	public static final int V_Video_Quality_Medium = MediaPlayer.VIDEOQUALITY_MEDIUM;
	public static final int V_Video_Quality_Low = MediaPlayer.VIDEOQUALITY_LOW;
	//选择android 原生播放还是 vitamio 播放
//	public static final String K_Player = "0";
//	public static final int V_Player_Default = 1;
//	public static final int V_Player_Vitamio = 2;
	//视频播放缓冲大小
	
	
}
