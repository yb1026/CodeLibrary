
package com.cultivator.codelibrary.base;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cultivator.codelibrary.R;


/**
 * @TTOD 主视图
 */
public class BaseView extends LinearLayout {

    public final static int TYPE_EMPTY = 1;
    public final static int TYPE_LOADING = 2;   //已停用
    public final static int TYPE_DATA = 3;
    private int mEmptyType = TYPE_DATA;
    private String mEmptyMessage;
    private Context mContext;
    private boolean mShowEmptyButton = true;
    public boolean statusBar = true;
    private LinearLayout mDataView;
    private ViewStub mEmptyView;
    private TextView emptyTextView;
    private Button emptyViewButton;
    private ImageView emptyImageView;
    private TopBarManager actionBar;


    public BaseView(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public BaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initAttrs(attrs);
        initView();
    }

    private void initView(){
        ViewStub stubData = new ViewStub(mContext);
        stubData.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        stubData.setLayoutResource(R.layout.view_data);
        if (statusBar) {
            actionBar = new TopBarManager(mContext);
            addView(actionBar.getView());
        }
        addView(stubData);
        stubData.inflate();
        mDataView = (LinearLayout) findViewById(R.id.layout_data);
    }

    public void initAttrs(AttributeSet attrs){
        TypedArray ta = mContext.obtainStyledAttributes(attrs, R.styleable.ToolBar);
        statusBar = ta.getBoolean(R.styleable.ToolBar_visibility_bar,true);

    }

    public TopBarManager getTopBarManager(){
        return actionBar;
    }


    @Override
    public void setFitsSystemWindows (boolean fitSystemWindows) {
        super.setFitsSystemWindows(false);
    }

    public void addDataView (View child) {
        //        child.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mDataView.addView(child);
    }

    /**
     * 初始化View
     */
    private void changeEmptyType () {
        // change empty type
        switch (mEmptyType) {
            case TYPE_DATA:
                if (mDataView != null) {
                    mDataView.setEnabled(true);
                    mDataView.setVisibility(View.VISIBLE);
                }
                if (mEmptyView != null) {
                    mEmptyView.setEnabled(false);
                    mEmptyView.setVisibility(View.GONE);
                }
                break;
            case TYPE_EMPTY:
                if (mEmptyView != null) {
                    mEmptyView.setEnabled(true);
                    mEmptyView.setVisibility(View.VISIBLE);
                }
                if (mDataView != null) {
                    mDataView.setEnabled(false);
                    mDataView.setVisibility(View.GONE);
                }
                if (emptyTextView != null && mEmptyMessage!=null) {
                    emptyTextView.setText(mEmptyMessage);
                }
                break;
            case TYPE_LOADING:
                if (mEmptyView != null)
                    mEmptyView.setVisibility(View.GONE);
                if (mDataView != null)
                    mDataView.setVisibility(View.GONE);
                break;
        }
    }


    private void initEmptyView () {
        if (mEmptyView == null) {
            mEmptyView = (ViewStub) findViewById(R.id.layout_empty);
            mEmptyView.inflate();
            emptyViewButton = ((Button) findViewById(R.id.empty_refresh_btn));
            emptyTextView = ((TextView) findViewById(R.id.empty_tv));
            emptyImageView = ((ImageView) findViewById(R.id.empty_iv));
        }
        if (mShowEmptyButton && emptyViewButton!=null) {
            emptyViewButton.setVisibility(View.VISIBLE);
        } else {
            emptyViewButton.setVisibility(View.GONE);
        }
    }

    private static Animation getRotateAnimation () {
        final RotateAnimation rotateAnimation = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF,
                .5f);
        rotateAnimation.setDuration(1500);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        return rotateAnimation;
    }


    /**
     *
     * @param emptyMessage  错误消息
     * @param emptyButtonClickListener  刷新按钮数据
     * @param resId     错误图片icon
     */
    public void setEmpty (String emptyMessage, OnClickListener emptyButtonClickListener, int resId) {
        this.mEmptyMessage = emptyMessage;
        this.mEmptyType = TYPE_EMPTY;
        initEmptyView();
        if (emptyButtonClickListener != null) {
            setEmptyButtonClickListener(emptyButtonClickListener);
        } else {
            emptyViewButton.setVisibility(View.INVISIBLE);
        }
        if (resId != 0 && emptyImageView!=null) {
            emptyImageView.setImageResource(resId);
        }
        changeEmptyType();
    }

    public void setEmpty (String emptyMessage,OnClickListener emptyButtonClickListener) {
        setEmpty(emptyMessage, emptyButtonClickListener, 0);
    }
    public void setEmpty (OnClickListener emptyButtonClickListener) {
        setEmpty(null, emptyButtonClickListener, 0);
    }

    public void setEmpty (String emptyMessage) {
        setEmpty(emptyMessage, null, 0);
    }

    public void setEmpty (String emptyMessage, int errorImgResId) {
        setEmpty(emptyMessage, null, errorImgResId);
    }

    public void showData () {
        this.mEmptyType = TYPE_DATA;
        changeEmptyType();
    }

    public void setEmptyType (int emptyType) {
        this.mEmptyType = emptyType;
        changeEmptyType();
    }


    public void setEmptyButtonClickListener (OnClickListener emptyButtonClickListener) {
        if (emptyViewButton != null) {
            emptyViewButton.setOnClickListener(emptyButtonClickListener);
        }
    }

    public void onDestroy () {
        removeAllViews();
        removeAllViewsInLayout();
    }

    @Override
    protected void onRestoreInstanceState (Parcelable state) {
        try {
            super.onRestoreInstanceState(state);
        } catch (Exception e) {
        }
        state = null;
    }
}
