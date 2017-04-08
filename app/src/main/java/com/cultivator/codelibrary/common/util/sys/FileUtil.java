package com.cultivator.codelibrary.common.util.sys;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;

import com.cultivator.codelibrary.common.log.MyLog;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class FileUtil {

	public static String bitMapSaveFile(Bitmap bitMap, Context context,
			String fileName) {
		return bitMapSaveFile(bitMap, context, fileName, 100);
	}

	public static String bitMapSaveFile(Bitmap bitMap, Context context,
			String fileName, int quality) {

		FileOutputStream out = null;
		BufferedOutputStream bos = null;
		try {
			out = context.openFileOutput(fileName, Activity.MODE_PRIVATE);
			bos = new BufferedOutputStream(out);
			bitMap.compress(CompressFormat.JPEG, quality, bos);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (bos != null) {
					bos.close();
				} 
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return getFilePath(fileName, context);
	}

	public static String saveCameraData(String filePath, String fileName,
			byte[] data) {
		File photo = new File(filePath, fileName);
		if (photo.exists())
			photo.delete();
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(photo);
			fos.write(data);
			fos.close();
		} catch (Exception e) {
			MyLog.e("File Write Error", e);
		}
		return photo.getAbsolutePath();
	}

	public static Bitmap cutBitmap(Bitmap mBitmap, Rect r, Bitmap.Config config) {
		int width = r.width();
		int height = r.height();

		Bitmap croppedImage = Bitmap.createBitmap(width, height, config);

		Canvas canvas = new Canvas(croppedImage);
		Rect dr = new Rect(0, 0, width, height);
		canvas.drawBitmap(mBitmap, r, dr, null);
		return croppedImage;
	}

	/**
	 * 获取系统私有目录
	 * 
	 * @param fileName
	 * @param context
	 * @return
	 */
	public static String getFilePath(String fileName, Context context) {

		return context.getFilesDir().getAbsolutePath() + "/" + fileName;
	}

	public static String getMyUUID() {

		UUID uuid = UUID.randomUUID();
		String uniqueId = uuid.toString();
		return uniqueId;
	}
	

	public static void doCopyFile(File srcFile, File destFile) {

		FileInputStream input = null;
		FileOutputStream output = null;
		try {
			input = new FileInputStream(srcFile);
			output = new FileOutputStream(destFile);

			byte[] buffer = new byte[4096];
			int n = 0;
			while (-1 != (n = input.read(buffer))) {
				output.write(buffer, 0, n);
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (output != null) {
					output.close();
				}
				if (input != null) {
					input.close();
				}
			} catch (IOException ioe) {
				// ignore
			}
		}
	}

	public static String getExtDir() {

		boolean sdCardExist = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
		String extDir = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		if (sdCardExist == false) {
			extDir = extDir.replace("sdcard", "sdcard2");
		}
		return extDir;
	}

	public static String getExtDirPath(String path) {

		boolean sdCardExist = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
		File file = Environment.getExternalStoragePublicDirectory(path);
		String extDir = file.getAbsolutePath();
		if (sdCardExist == false) {
			extDir = extDir.replace("sdcard", "sdcard2");
		}
		return extDir;
	}

	public static boolean deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}

	/**
	 * 从drawable中读取图片
	 *
	 * @param context
	 * @param drawableId
	 * @return
	 */
	public static BitmapDrawable getResourceBitmap(Context context, int drawableId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		InputStream is = context.getResources().openRawResource(drawableId);
		Bitmap bm = BitmapFactory.decodeStream(is, null, opt);
		BitmapDrawable bd = new BitmapDrawable(context.getResources(), bm);
		return bd;
	}

}
