package com.cultivator.codelibrary.common;

public class MessageType {


	/**
	 * 返回首页
	 */
	public static final int GO_HOME = 1000;


	/**
	 * 用户注销
	 */
	public static final int USER_LOGIN_OVER = 1002;

	/**
	 * 登录成功
	 */
	public static final int UPDATE_LOGIN = 1004;

	/**
	 * 自动登录
	 */
	public static final int USER_AUTOLOGIN = 1007;

	/**
	 * Fragment切换
	 */
	public static final int FRAGMENT = 1006;


	/**
	 * 刷新账户资料手机号码
	 */
	public static final int UPDATE_USER_PHONE = 1005;
	/**
	 * 网络
	 */
	public static final int NETWORK_TYPE = 100;

	public static final String MESSAGE_ACTION = "messageType_msg";

	public static final String MESSAGE_TYPE_KEY = "messageTypeKey";
	//i云猴跳转完善资料
	public static final int MESSAGE_ACTION_UESRINFO = 1;
}
