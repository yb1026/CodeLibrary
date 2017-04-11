package com.cultivator.codelibrary.activity;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.cultivator.codelibrary.R;
import com.cultivator.codelibrary.base.BaseActivity;
import com.cultivator.codelibrary.util.ToastUtil;
import com.cultivator.codelibrary.widget.password.PasswordInputView;
import com.cultivator.codelibrary.widget.ripple.RippleLayout;
import com.cultivator.codelibrary.widget.softkeyboard.SolfNumberKeyBoardView;

import java.lang.reflect.Method;

public class ViewDemoActivity extends BaseActivity {

    private static final int Nou = 1;

    private SolfNumberKeyBoardView solfKeyBoard;
    private RelativeLayout rootView;
    private PasswordInputView passwordInputView;
    private RippleLayout ripplelay;

    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_views_demo);
        getToolBar().setTitle("自定义1");
        initView();
    }


    public void initView() {
        passwordInputView = (PasswordInputView) this.findViewById(R.id.password);
        rootView = (RelativeLayout)findViewById(R.id.root_view);
        solfKeyBoard = SolfNumberKeyBoardView.instance(this,
                rootView, null);
        solfKeyBoard.getHintImgeBtn().setVisibility(View.VISIBLE);
        solfKeyBoard.showKeyboard(passwordInputView);
        solfKeyBoard.getSureText().setText("");


        hiddenSoftInput(passwordInputView);

        passwordInputView.setInputListener(new PasswordInputView.InputListener() {
            @Override
            public void firstInput(String pwd) {
                ToastUtil.show(getBaseContext(), "pwd:" + pwd);
                passwordInputView.setText("");
                startAnimator();
            }

            @Override
            public void comfirm(String pwd) {
                passwordInputView.clearFirstInput();
                stopAnimator();

            }

            @Override
            public void comfirmDifferent() {
                ToastUtil.show(getBaseContext(), "输入不一致");
            }
        });

        passwordInputView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                solfKeyBoard.showKeyboard(passwordInputView);
            }
        });

        ripplelay = (RippleLayout)findViewById(R.id.ripplelay);
        initAnimator();
    }


    private void initAnimator(){
        ripplelay.post(new Runnable() {
            public void run() {
                ripplelay.init(ripplelay.getWidth() / 2,//中心点x
                        ripplelay.getHeight() / 2,//中心点y
                        10,//波纹的初始半径
                        Math.min(ripplelay.getWidth(), ripplelay.getHeight()) / 2,//波纹的结束半径
                        2100,//duration
                        getResources().getColor(R.color.color_main_blue),//颜色..
                        new DecelerateInterpolator());//开始快,后来慢
            }
        });


        handler = new Handler(){

            @Override
            public void handleMessage(Message msg){
                if(Nou==msg.what){
                    ripplelay.doRipple();
                    handler.sendEmptyMessageDelayed(Nou,700);
                }
            }

        };

    }

    private void startAnimator(){
        handler.sendEmptyMessage(Nou);
    }

    private void stopAnimator(){
        if(handler!=null){
            handler.removeMessages(Nou);
        }

    }




    private void hiddenSoftInput(EditText et) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            et.setShowSoftInputOnFocus(false);
        } else {
            try {
                Class<EditText> cls = EditText.class;
                Method setShowSoftInputOnFocus;
                setShowSoftInputOnFocus = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(et, false);
                //edit为EditText对象
            } catch (Exception e) {
            }
        }
    }


    @Override
    public void onBack() {
        super.onBack();

    }
}
