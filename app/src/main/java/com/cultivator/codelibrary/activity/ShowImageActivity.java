package com.cultivator.codelibrary.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cultivator.codelibrary.base.BaseActivity;

/**
 * 
 */
public class ShowImageActivity extends BaseActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		ImageView imageView =new ImageView(this);

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.setMargins(10, 10, 10, 10);
		imageView.setLayoutParams(params);

		super.setContentView(imageView);

		getToolBar().setTitle("图片");
		getToolBar().getView().setVisibility(View.VISIBLE);

		byte[] b = getIntent().getByteArrayExtra("bitmap");
		Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
		if (bitmap != null)
		{
			imageView.setImageBitmap(bitmap);
		}
	}
}
