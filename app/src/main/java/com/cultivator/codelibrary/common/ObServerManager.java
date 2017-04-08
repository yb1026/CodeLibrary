package com.cultivator.codelibrary.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;


import com.cultivator.codelibrary.common.log.MyLog;

import java.util.HashMap;


/**
 * 观察者
 * 
 * @author Simon.xin
 * 
 */
public class ObServerManager {

	private static ObServerManager instance = null;
	private Context mContext = null;

	/**
	 * 监听器集合
	 */
	private HashMap<Integer, ObServerListener> messageListenerMap = new HashMap<Integer, ObServerListener>();

	public synchronized static ObServerManager getInstance(Context mContext) {
		if (instance == null) {
			instance = new ObServerManager(mContext);
		}
		return instance;
	}

	public void put(int msgTypes, ObServerListener messageListener) {
		messageListenerMap.put(msgTypes, messageListener);
	}

	private ObServerManager(Context mContext) {
		this.mContext = mContext;
		registerMessageBoradcastReceiver();
	}

	/**
	 * 注册消息接受广播
	 * 
	 * @returnType void
	 */
	private synchronized void registerMessageBoradcastReceiver() {
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(MessageType.MESSAGE_ACTION);
		mContext.registerReceiver(messageReceiver, myIntentFilter);
	}

	/**
	 * 设置连接消息监听器
	 * 
	 * @param msgTypes
	 * @param messageListener
	 * @returnType void
	 */
	public void setMessageListener(int msgTypes,
			ObServerListener messageListener) {
		messageListenerMap.put(msgTypes, messageListener);
	}

	/**
	 * 发送广播给BaseApplication来处理，在相应模块自己实现MessageListener接口，获取数据并且刷新UI
	 * @param msgType	消息类型
	 * @param intent	传递intent
     */
	public void sendMessageBroadcast(int msgType, Intent intent) {
		if (intent == null) {
			intent = new Intent();
		}
		intent.setAction(MessageType.MESSAGE_ACTION);
		intent.putExtra(MessageType.MESSAGE_TYPE_KEY, msgType);
		mContext.sendBroadcast(intent);
	}
	

	// 消息接受器
	public BroadcastReceiver messageReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			MyLog.d(getClass(), "onReceive");
			String action = intent.getAction();
			if (action.equals(MessageType.MESSAGE_ACTION)) {
				int msgType = intent.getIntExtra(MessageType.MESSAGE_TYPE_KEY,
						0);
				ObServerListener messageListener = messageListenerMap
						.get(msgType);
				if (null != messageListener) {
					messageListener.ObServerResult(msgType, intent);
				}
			}
		}
	};

	public void destory() {
		messageListenerMap.clear();
		mContext.unregisterReceiver(messageReceiver);
	}
}
