package com.google.zxing.client.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.client.android.camera.CameraManager;


import java.io.IOException;
import java.util.Collection;

public class QRActivity extends Activity implements SurfaceHolder.Callback {

	private static final String TAG = QRActivity.class.getSimpleName();

	public boolean hasSurface;
	public BeepManager beepManager;
	public CameraManager cameraManager;
	public CaptureActivityHandler handler;
	public Collection<BarcodeFormat> decodeFormats;

	public ViewfinderView viewfinderView;
	public FrameLayout frameLayout;
	private SurfaceView surfaceView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getResources().getIdentifier("com_qr_layout","layout",getPackageName()));
		hasSurface = false;
		beepManager = new BeepManager(this);
		frameLayout = (FrameLayout) findViewById(getResources().getIdentifier("com_framView_view","id",getPackageName()));

		Display disPlay = getWindowManager().getDefaultDisplay();
		DisplayMetrics outMetrics = new DisplayMetrics();
		disPlay.getMetrics(outMetrics);
		int topHeight = (int) (outMetrics.density * 75);

		CameraManager.init(getApplication());
		cameraManager = CameraManager.get();
		cameraManager.setTopHeight(topHeight);

		surfaceView = new SurfaceView(getApplicationContext());
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		frameLayout.addView(surfaceView);

		viewfinderView = new ViewfinderView(getApplicationContext(), null);
		frameLayout.addView(viewfinderView);

		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}

	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		if (surfaceHolder == null) {
			throw new IllegalStateException("No SurfaceHolder provided");
		}

		try {
			cameraManager.openDriver(surfaceHolder);
			if (handler == null) {
				handler = new CaptureActivityHandler(this, decodeFormats, "utf-8");
			}
		} catch (IOException ioe) {
			Log.w(TAG, ioe);
		} catch (RuntimeException e) {
			Log.w(TAG, "Unexpected error initializing camera", e);
		}

		if(!cameraManager.isOpenPermission()){
			cameraManager.closeDriver();
			viewfinderView.endAnimator();
			Toast.makeText(this,"摄像头打开失败",Toast.LENGTH_SHORT).show();
		}

	}

	/**
	 * @param rawResult
	 * @param barcode
	 */
	public void handleDecode(Result rawResult, Bitmap barcode) {
		beepManager.playBeepSoundAndVibrate();
		String couponResult = rawResult.getText();
		this.setResult(0,new Intent().putExtra("result",couponResult));
		finish();
	}

	@Override
	protected void onPause() {
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		// inactivityTimer.onPause();
		cameraManager.closeDriver();
		if (!hasSurface) {
			SurfaceHolder surfaceHolder = surfaceView.getHolder();
			surfaceHolder.removeCallback(this);
		}
		super.onPause();
	}


	public void surfaceChanged(SurfaceHolder holder, int format, int width,
							   int height) {

	}

	public void surfaceCreated(SurfaceHolder holder) {
		if (holder == null) {
			Log.e(TAG,
					"*** WARNING *** surfaceCreated() gave us a null surface!");
		}
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}

	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
	}


	public void drawViewfinder() {
		viewfinderView.drawViewfinder();
	}


	// byte转化成16进制，以:连接，如：52:62:3F:6C
	@SuppressLint("DefaultLocale")
	public String bytesToHexString(byte[] b) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (b == null || b.length <= 0) {
			return "";
		}
		for (int i = b.length - 1; i >= 0; i--) {
			int v = b[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			if (i == (b.length - 1))
				stringBuilder.append(hv.toUpperCase());
			else
				stringBuilder.append(":" + hv.toUpperCase());
		}
		return stringBuilder.toString();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
            /*
             * if(scanType.equals(QrScanType.ST_TXN_MESSAGE)){ HomePageActivity
			 * hn = (HomePageActivity) this.getParent(); hn.showSlider(); }else{
			 * this.finish(); }
			 */
			this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
