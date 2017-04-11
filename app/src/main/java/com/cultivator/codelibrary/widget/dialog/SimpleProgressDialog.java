package com.cultivator.codelibrary.widget.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.view.Gravity;
import android.view.KeyEvent;


/**
 * loading
 * 
 * @author simon.xin
 * @date 2012-10-12 下午3:18:19
 * @version V1.0
 */
public class SimpleProgressDialog {

	public static Context mContext;
	private static CustomProgressDialog mDialog;

	public static void show(Context context) {
		mContext = context;

		try {
			if (mDialog==null|| !mDialog.isShowing()) {
				mDialog = CustomProgressDialog.createDialog(mContext);
				mDialog.setCancelable(true);
				mDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
				mDialog.setOnKeyListener(new OnKeyListener() {

					@Override
					public boolean onKey(DialogInterface arg0, int keyCode,
							KeyEvent event) {
						if (keyCode == KeyEvent.KEYCODE_BACK) {
							// if (!Utils.isNull(mContext)) {
							// // ((BaseActivity) mContext).stopTask();
							// }
							/**
							 * 此处stop当前请求（扩展 ）
							 */
							dismiss();
						}
						return false;
					}
				});
				mDialog.show();
			}
		} catch (Exception e) {
			e.printStackTrace();
			mDialog = null;
		} catch (Error error) {
			error.printStackTrace();
			mDialog = null;
		}

	}

	public static void dismiss() {
		try {
			if (mDialog != null) {
				mDialog.dismiss();
			}
			mDialog = null;
		} catch (Exception e) {
			e.printStackTrace();
			mDialog = null;
		} catch (Error error) {
			error.printStackTrace();
			mDialog = null;
		}

	}

}
