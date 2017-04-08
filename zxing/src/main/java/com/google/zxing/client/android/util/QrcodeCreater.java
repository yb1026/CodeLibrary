package com.google.zxing.client.android.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.Hashtable;

public class QrcodeCreater {
	
	public QrcodeCreater(Activity activity){
		this.activity = activity;
	}
	
	private Activity activity;


	// 插入到二维码里面的图片对象
	private Bitmap mBitmap;

	// 二维码
	private Bitmap qrBitmap;
	
	private void createCenterBitmap(int imageHalfWidth,int iconId) {
		mBitmap = BitmapFactory.decodeResource(activity.getResources(), iconId);
		// 缩放图片
		Matrix m = new Matrix();
		float sx = (float) 2 * imageHalfWidth / mBitmap.getWidth();
		float sy = (float) 2 * imageHalfWidth / mBitmap.getHeight();
		m.setScale(sx, sy);
		// 重新构造一个40*40的图片
		mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(),
				mBitmap.getHeight(), m, false);
	}
	
	
	
	
	/**
	 * 生成二维码
	 * 
	 * @return Bitmap
	 * @throws WriterException
	 */
	public void createQrBitmap(String content,int qrWidth,int imageHalfWidth,int iconId) throws WriterException {

		createCenterBitmap(imageHalfWidth,iconId);
		
		Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
		hints.put(EncodeHintType.MARGIN, 1);

		// 生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
		BitMatrix matrix = new MultiFormatWriter().encode(content,
				BarcodeFormat.QR_CODE, qrWidth, qrWidth, hints);
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		// 二维矩阵转为一维像素数组,也就是一直横着排了
		int halfW = width / 2;
		int halfH = height / 2;
		int[] pixels = new int[width * height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (x > halfW - imageHalfWidth && x < halfW + imageHalfWidth
						&& y > halfH - imageHalfWidth
						&& y < halfH + imageHalfWidth) {
					pixels[y * width + x] = mBitmap.getPixel(x - halfW
							+ imageHalfWidth, y - halfH + imageHalfWidth);
				} else {
					if (matrix.get(x, y)) {
						pixels[y * width + x] = 0xff000000;
					} else { // 无信息设置像素点为白色
						pixels[y * width + x] = 0xffffffff;
					}
				}

			}
		}
		qrBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		// 通过像素数组生成bitmap
		qrBitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		
		
	}
	
	public Bitmap  getQrBitmap(){
		return qrBitmap;
	}
	
	
	public void onDestroy() {
		if (mBitmap != null && !mBitmap.isRecycled()) {
			mBitmap.recycle();
		}
		if (qrBitmap != null && !qrBitmap.isRecycled()) {
			qrBitmap.recycle();
		}
	}
	
}
