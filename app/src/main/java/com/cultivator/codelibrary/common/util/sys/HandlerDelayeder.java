package com.cultivator.codelibrary.common.util.sys;

import android.os.Handler;

public class HandlerDelayeder {

	private static final long DEFAULTDELAYMILLIS = 120000l;

	private long currentDelayMillis = 0;
	
	private Handler hander;

	public static interface DelayederCallback {
		void callback();
	}


	private DelayederCallback callback;

	public void setCallback(DelayederCallback callback) {
		this.callback = callback;
	}


	public HandlerDelayeder(){
		this.hander = new Handler(){

		};
	}

	private Runnable runnable = new Runnable(){
		@Override
		public void run() {
				if (callback != null) {
					callback.callback();
				}
		}
	};

	public void setCurrentDelayMillis(long currentDelayMillis) {
		this.currentDelayMillis = currentDelayMillis;
	}

	public synchronized void doDelayeder() {

		if(currentDelayMillis<=0){
			currentDelayMillis = DEFAULTDELAYMILLIS;
		}

		hander.postDelayed(runnable, currentDelayMillis);
	}
	
	
	public synchronized void removeDelayeder(){
		hander.removeCallbacks(runnable);
	}
}
