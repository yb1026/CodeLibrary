package com.cultivator.codelibrary.base;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.cultivator.codelibrary.MyApplication;
import com.cultivator.codelibrary.R;
import com.cultivator.codelibrary.widget.dialog.SimpleProgressDialog;
import com.cultivator.codelibrary.net.CallServer;
import com.cultivator.codelibrary.net.ConnectionController;
import com.cultivator.codelibrary.net.HttpListener;
import com.cultivator.codelibrary.net.NetworkManager;
import com.cultivator.codelibrary.net.model.BaseResp;


/**
 */
public abstract class BaseActivity extends AppCompatActivity implements HttpListener, ViewListener {

    private static final int LOGOUT = 888 ;
    private BaseView mBaseView;
    private TopBarManager toolBar;
    private ConnectionController controller;
    public boolean isfilter =true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        mBaseView = (BaseView) LayoutInflater.from(this).inflate(R.layout.activity_base, null);
        toolBar = mBaseView.getTopBarManager();
        controller = new ConnectionController(this, this, this);
    }


    @Override
    public void setContentView(int layoutResID) {
        addView(LayoutInflater.from(this).inflate(layoutResID, null));
        super.setContentView(mBaseView);
        initUI();
    }

    @Override
    public void setContentView(View view) {
        addView(view);
        super.setContentView(mBaseView);
        initUI();
    }



    @Override
    public void onShowDataView() {
        showDataView();
    }

    private void initUI() {
        initListener();
    }

    /**
     * @param v
     * @TODO 自定义actionBar
     */
    public void setToolBarView(View v) {
        getSupportActionBar().setCustomView(v);
    }

    /**
     * @param isHide true:隐藏 false:显示
     * @TODO 系统自带
     * 隐藏左上角图标
     */
    public void setLeftBtnVisibility(boolean isHide) {
        if (!isHide) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);// 给左上角图标的左边加上一个返回的图标
        } else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initListener() {
        if (toolBar != null) {
            toolBar.setLeftActionBarOnClickListener(onBackClickListener);
        }
    }



    private int getActionBarHeight() {
        int actionBarHeight = 0;
        final TypedValue tv = new TypedValue();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
                actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        } else if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true))
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        return actionBarHeight;
    }

    public TopBarManager getToolBar() {
        return toolBar;
    }

    public BaseView getBaseView() {
        return mBaseView;
    }


    private void addView(View view) {
        if (view != null) {
            mBaseView.addDataView(view);
        }
    }

    /**
     * 现实数据视图
     */
    public void showDataView() {
        if (mBaseView != null) {
            mBaseView.showData();
        }
    }

    /**
     * @param emptyMessage
     * @TODO: 异常视图
     */
    public void showEmptyView(String emptyMessage) {
        if (mBaseView != null) {
            mBaseView.setEmpty(emptyMessage, null, 0);
        }
    }

    /**
     * @param emptyMessage
     * @param errorImgResId
     */
    public void showEmptyView(String emptyMessage, int errorImgResId) {
        if (mBaseView != null) {
            mBaseView.setEmpty(emptyMessage, null, errorImgResId);
        }
    }

    /**
     * @param emptyMessage
     * @param emptyButtonClickListener
     */
    public void showEmptyView(String emptyMessage, View.OnClickListener emptyButtonClickListener) {
        if (mBaseView != null) {
            mBaseView.setEmpty(emptyMessage, emptyButtonClickListener);
        }
    }

    public void showEmptyView(View.OnClickListener emptyButtonClickListener) {
        if (mBaseView != null) {
            mBaseView.setEmpty(emptyButtonClickListener);
        }
    }

    /**
     * @param emptyMessage
     * @param emptyButtonClickListener
     * @param errorImgResId
     */
    public void showEmptyView(String emptyMessage, View.OnClickListener emptyButtonClickListener, int errorImgResId) {
        if (mBaseView != null) {
            mBaseView.setEmpty(emptyMessage, emptyButtonClickListener, errorImgResId);
        }
    }

    public void showProgressDialog() {
        SimpleProgressDialog.show(this);
    }

    public void dismissDialog() {
        SimpleProgressDialog.dismiss();
    }

    private View.OnClickListener onBackClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            onBack();
        }
    };

    public void onBack() {
        finish();
    }

    public void onBack(Intent intent) {
        startActivity(intent);
        onBack();
    }

    /**
     * @param messageType 任务类型
     * @param intent      value
     * @TODO 发送广播任务（详见FramenWorkActivity 中的 regReceiver()）
     */
    public void sendTaskReceiver(int messageType, Intent intent) {
        MyApplication.getInstance().sendMessageBroadcast(messageType, intent);
    }

    public void sendTaskReceiver(int messageType) {
        MyApplication.getInstance().sendMessageBroadcast(messageType, null);
    }

    public void startActivity(Class<?> cls) {
        startActivity(cls, null);
    }

    public void startActivity(String pAction, Bundle pBundle) {
        Intent intent = new Intent(pAction);
        if (pBundle != null) {
            intent.putExtras(pBundle);
        }
        startActivity(intent);
    }

    public void startActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isfilter) {
            if (!NetworkManager.getInstance(this).isConnected()) {
                showEmptyView("完成", R.mipmap.ic_error);
            } else {
                showDataView();
            }
        }

        if(getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destoryAll();
    }

    private void destoryAll() {
        CallServer.getRequestInstance().cancelAll();
        if (mBaseView != null) {
            mBaseView.onDestroy();
        }
    }






    /**
     * @return http
     */
    public ConnectionController getConnection() {
        return controller;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            onBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }



    /**
     * 关闭输入键盘
     */
    protected void closeInputMethod(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean isOpen = imm.isActive();
        if (isOpen) {
            // imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);//没有显示则显示
            imm.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }





    @Override
    public void onSucceed(int what, BaseResp result) {

    }

    @Override
    public void onFailed(int what, String errorMsg,String errorCode,Exception e) {

    }

}
