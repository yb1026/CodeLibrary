package com.cultivator.codelibrary.common.util.image;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.IOException;

/**
 * 图片工具
 * 
 * @author cpz
 * 
 */
public class BitMapUtil {
	/**
	 * 正向旋转90度
	 */
	public static Integer rotateDgree = 90;

	// 直接载入图片
	public static Bitmap getBitmap(String path) {
		Bitmap bt = BitmapFactory.decodeFile(path);
		return bt;
	}

	// 指定大小載入圖片
	public static Bitmap getBitmap(String path, int size) {
		Options op = new Options();
		op.inSampleSize = size;
		Bitmap bt = BitmapFactory.decodeFile(path, op);
		return bt;
	}

	// 按寬高壓縮載入圖片
	public static Bitmap getBitmap(String path, int width, int heigh) {
		Options op = new Options();
		
		op.inJustDecodeBounds = true;
		Bitmap bt = BitmapFactory.decodeFile(path, op);
		int xScale = op.outWidth / width;
		int yScale = op.outHeight / heigh;
		op.inSampleSize = xScale > yScale ? xScale : yScale;
		op.inJustDecodeBounds = false;
		bt = BitmapFactory.decodeFile(path, op);
		return bt;
	}

	/**
	 * 旋转角度
	 * 
	 * @param b
	 * @param degrees
	 * @return
	 */
	public static Bitmap rotate(Bitmap b, int degrees) {
		if (degrees != 0 && b != null) {
			Matrix m = new Matrix();
			m.setRotate(degrees, (float) b.getWidth() / 2,
					(float) b.getHeight() / 2);
			try {
				Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(),
						b.getHeight(), m, true);
				if (b != b2) {
					b.recycle(); // Android开发网再次提示Bitmap操作完应该显示的释放
					b = b2;
				}
			} catch (OutOfMemoryError ex) {
				// Android123建议大家如何出现了内存不足异常，最好return 原始的bitmap对象。.
			}
		}
		return b;
	}


	/**
	 * 按照图片数据进行压缩
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int calculateInSampleSize(Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}
		return inSampleSize;
	}

	/**
	 * 保持长宽比缩小Bitmap
	 * 
	 * @param bitmap
	 * @param maxWidth
	 * @param maxHeight
	 * @return
	 */
	public static Bitmap resizeBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {

		int originWidth = bitmap.getWidth();
		int originHeight = bitmap.getHeight();

		// no need to resize
		if (originWidth < maxWidth && originHeight < maxHeight) {
			return bitmap;
		}

		int width = originWidth;
		int height = originHeight;

		// 若图片过宽, 则保持长宽比缩放图片
		Bitmap newBigMap = null;
		if (originWidth > maxWidth) {
			width = maxWidth;

			double i = originWidth * 1.0 / maxWidth;
			height = (int) Math.floor(originHeight / i);

			newBigMap = Bitmap.createScaledBitmap(bitmap, width, height, false);
		}

		// 若图片过长, 则从上端截取
		if (height > maxHeight) {
			height = maxHeight;
			newBigMap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
		}

		// Log.i(TAG, width + " width");
		// Log.i(TAG, height + " height");
		//释放原图片
		if(!bitmap.isRecycled()) {
			bitmap.recycle();
		}
		
		return newBigMap;
	}

	/**
	 * 读取图片的角度
	 * @param path
	 * @return
	 */
	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

}
