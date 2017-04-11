package com.cultivator.codelibrary.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cultivator.codelibrary.R;
import com.cultivator.codelibrary.base.TopBarManager;
import com.cultivator.codelibrary.util.Utils;
import com.cultivator.codelibrary.widget.dialog.WaitDialog;
import com.cultivator.codelibrary.util.ToastUtil;
import com.google.zxing.Result;
import com.google.zxing.client.android.QRActivity;
import com.google.zxing.client.android.decoder.Decoder;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.loader.ImageLoader;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;
//import com.lzy.imagepicker.ImagePicker;
//import com.lzy.imagepicker.bean.ImageItem;
//import com.lzy.imagepicker.ui.ImageGridActivity;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by yb1026 on 2017/4/10.
 */
public class MyQrActivity extends QRActivity implements Decoder.DecoderCallBack{

    private RelativeLayout titleLay;
    private TopBarManager actionBar = null;

    private ImagePicker imagePicker;

    private WaitDialog dialog;



    public boolean decoding = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        decoding = false;
        actionBar = new TopBarManager(this);
        super.onCreate(savedInstanceState);

        titleLay =(RelativeLayout) findViewById(R.id.qr_title);
        titleLay.addView(actionBar.getView());
        titleLay.setVisibility(View.VISIBLE);
        actionBar.setTitleRight("相册");
        actionBar.setLeftActionBarOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        actionBar.setRightActionBarOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyQrActivity.this, ImageGridActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        initImageLoader();
    }


    private void initImageLoader() {

        int[] screenSize = Utils.getScreenDispaly(this);

        int border = screenSize[0]>screenSize[1]?screenSize[1]:screenSize[0];

        imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new ImageLoader(){

            @Override
            public void displayImage(Activity activity, String path, ImageView imageView, int width, int height) {
                Glide.with(activity)                             //配置上下文
                        .load(Uri.fromFile(new File(path)))      //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                        .error(R.mipmap.default_image)           //设置错误图片
                        .placeholder(R.mipmap.default_image)     //设置占位图片
                        .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存全尺寸
                        .into(imageView);
            }

            @Override
            public void clearMemoryCache() {

            }
        });   //设置图片加载器
        imagePicker.setShowCamera(false);  //显示拍照按钮
        imagePicker.setCrop(true);        //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true); //是否按矩形区域保存
        imagePicker.setSelectLimit(1);    //选中数量限制
        imagePicker.setMultiMode(false);
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(border);   //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(border);  //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(border);//保存文件的宽度。单位像素
        imagePicker.setOutPutY(border);//保存文件的高度。单位像素
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {

            if (data != null && requestCode == 0) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (images != null && !images.isEmpty()) {
                    String path = images.get(0).path;
                    decoder.decodeQrImagePath(path,this);
                    return;
                }
            }
            Toast.makeText(this, "识别错误", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * @param rawResult
     */
    public void handleDecode(Result rawResult) {
        super.handleDecode(rawResult);
        String couponResult = rawResult.getText();
        this.setResult(0, new Intent().putExtra("result", couponResult));
        finish();
    }


    public void decodeViewStart() {
        //停止扫描识别
        decoding = true;
        viewfinderView.setHideAnima(true);
        if (dialog == null) {
            dialog = new WaitDialog(this, "处理中");
        }
        dialog.show();
    }


    public void decodeViewEnd(boolean showAnima) {
        decoding = false;
        viewfinderView.setHideAnima(!showAnima);
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }



    @Override
    public void startDecode() {
        decodeViewStart();
    }

    @Override
    public void completeDecoer(Result rawResult) {

        if (null != rawResult) {
            handleDecode(rawResult);
        } else {
            decodeViewEnd(true);
            ToastUtil.show(this, "图片二维码识别失败");
        }
    }
}
