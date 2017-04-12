package com.cultivator.codelibrary.activity;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.cultivator.codelibrary.R;
import com.cultivator.codelibrary.base.BaseActivity;
import com.cultivator.codelibrary.util.ToastUtil;
import com.cultivator.codelibrary.util.sys.FileUtil;
import com.cultivator.codelibrary.widget.signature.SignatureView;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * @author: han.zhang
 * @create time: 2016/5/6 11:24
 * @TODO: 签名
 */
public class SignActivity extends BaseActivity {


    private Button clearBtn;
    private Button submitBtn;
    private TextView text;
    private SignatureView signature;
    private RelativeLayout signPromptLay;

    private int strokesNum=0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign);

        initTopBar();
        initView();

        ViewTreeObserver viewTreeObserver = signature.getViewTreeObserver();
        viewTreeObserver.addOnPreDrawListener(new OnPreDrawListener() {
            public boolean onPreDraw() {
                signature.ensureSignatureBitmap();
                return true;
            }
        });

    }


    private void initTopBar() {

        getToolBar().setTitle("请签名");

    }


    private void initView() {
        submitBtn= (Button) this.findViewById(R.id.submit);
        clearBtn = (Button) this.findViewById(R.id.clear_btn);
        text=(TextView)this.findViewById(R.id.text);
        signature = (SignatureView) this.findViewById(R.id.content);
        signPromptLay = (RelativeLayout) this.findViewById(R.id.prompt_lay);


        submitBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });

        clearBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                signClear();
            }
        });

        signature.setOnSignedListener(new SignatureView.OnGestureListener() {
            @Override
            public void onGestureStarted(SignatureView signature, MotionEvent event) {
                strokesNum+=1;
                text.setText(strokesNum+"");
            }

            @Override
            public void onGesture(SignatureView signature, MotionEvent event) {
                strokesNum+=1;
                text.setText(strokesNum+"");
            }

            @Override
            public void onGestureEnded(SignatureView signature, MotionEvent event) {
                clearBtn.setEnabled(true);
            }

            @Override
            public void onGestureCancelled(SignatureView signature, MotionEvent event) {
                ToastUtil.show(getBaseContext(),"onGestureCancelled");
            }
        });


        signPromptLay.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                signPromptLay.setVisibility(View.GONE);
                return false;
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        // 自动横屏
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        getToolBar().showImageLeftBar();

    }


    @Override
    public void setRequestedOrientation(int requestedOrientation) {
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        signature.recycle();
    }

    public void signClear() {
        strokesNum=0;
        text.setText(strokesNum+"");
        // signature.setFadeOffset(10);// 清除前设置时间间隔缩小
        signature.clear();
        signature.ensureSignatureBitmap();
        // signature.setFadeOffset(3600000);// 清楚后恢复时间间隔
        signPromptLay.setVisibility(View.VISIBLE);
    }


    public void submit() {


		/* 获得位图，和存储的临时文件 */
        Bitmap bitmap = this.signature.getSignatureBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] datas = baos.toByteArray();

        Intent intent = new Intent(this, ShowImageActivity.class);
        intent.putExtra("bitmap", datas);
        startActivity(intent);
        signClear();
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                return false;
        }
        return super.onKeyDown(keyCode, event);
    }


}
