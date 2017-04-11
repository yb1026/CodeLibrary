package com.cultivator.codelibrary.base;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.cultivator.codelibrary.R;
import com.cultivator.codelibrary.util.Utils;


/**
 *  顶部导航管理器
 */
public class TopBarManager implements ActionBarListener {
    private ImageView iv_right;
    private ImageView iv_left;
    private View rl;
    private View layout_left;
    private View layout_right;
    private TextView tv_title;
    private TextView tv_title_left;
    private TextView tv_right;
    private Context mContext;
    private View mView;
    private OnClickListener leftOnClickListener;


    public TopBarManager (Context context) {
        mContext = context;
        mView = LayoutInflater.from(mContext).inflate(R.layout.view_titlebar, null);
        initUI();
    }

    public void initUI () {
        rl =  findViewById(R.id.title_bar);
        layout_right =  findViewById(R.id.layout_bar_right);
        layout_left =  findViewById(R.id.layout_bar_left);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title_left = (TextView) findViewById(R.id.tv_title_left);
        iv_left = (ImageView) findViewById(R.id.iv_back);
        iv_right = (ImageView) findViewById(R.id.iv_right);
        tv_right = (TextView) findViewById(R.id.tv_right);

    }

    public View getView () {
        return mView;
    }

    @Override
    public void setBackground (int resid) {
        if (!Utils.isNull(rl)) {
            rl.setBackgroundResource(resid);
        }
    }


    @Override
    public void setImageLeftIcon (int resid) {
        if (!Utils.isNull(iv_left)) {
            iv_left.setVisibility(View.VISIBLE);
            iv_left.setImageResource(resid);
        }
    }

    /**
     * hide right TextView
     */
    public void hidetRightTextView(){
        if (!Utils.isNull(tv_right) && tv_right.getVisibility() == View.VISIBLE) {
            tv_right.setVisibility(View.GONE);
        }
    }

    /**
     * hide right button
     */
    public void hidetRightButton(){
        if (!Utils.isNull(iv_right) && iv_right.getVisibility() == View.VISIBLE) {
            iv_right.setVisibility(View.GONE);
        }
    }

    @Override
    public void setImageRightIcon (int resid) {
        if (!Utils.isNull(iv_right)) {
            hidetRightTextView();
            layout_right.setEnabled(false);
            iv_right.setVisibility(View.VISIBLE);
            iv_right.setImageResource(resid);
        }
    }

    @Override
    public void setTitle (int resid) {
        if (!Utils.isNull(tv_title)) {
            tv_title.setText(resid);
            findViewById(R.id.iv_mainpage).setVisibility(View.GONE);
        }
    }

    @Override
    public void setTitle (String title) {
        if (!Utils.isNull(tv_title)) {
            tv_title.setText(title);
            findViewById(R.id.iv_mainpage).setVisibility(View.GONE);
        }
    }

    @Override
    public void setTitleLeft (String title) {
        if (!Utils.isNull(tv_title_left)) {
            tv_title_left.setVisibility(View.VISIBLE);
            tv_title_left.setText(title);
        }
    }

    @Override
    public void setTitleLeft (int resid) {
        if (!Utils.isNull(tv_title_left)) {
            layout_left.setVisibility(View.VISIBLE);
            tv_title_left.setVisibility(View.VISIBLE);
            tv_title_left.setText(resid);
        }
    }

    @Override
    public void setTitleRight (String title) {
        if (!Utils.isNull(tv_right)) {
            hidetRightButton();
            tv_right.setVisibility(View.VISIBLE);
            tv_right.setText(title);
        }
    }

    @Override
    public void setTitleRight (int resId) {
        if (!Utils.isNull(tv_right)) {
            hidetRightButton();
            tv_right.setVisibility(View.VISIBLE);
            tv_right.setText(resId);
        }
    }

    @Override
    public void setColorRight (int resid) {
        if (!Utils.isNull(tv_right)) {
            hidetRightButton();
            ColorStateList colors = mContext.getResources().getColorStateList(resid);
            tv_right.setTextColor(colors);
        }
    }


    @Override
    public void setTitleColor (int resid) {
        if (!Utils.isNull(tv_title)) {
            ColorStateList colors = mContext.getResources().getColorStateList(resid);
            tv_title.setTextColor(colors);
        }
    }

    private Resources getResources () {
        return mContext.getResources();
    }



    @Override
    public void hideTitleLeft () {
        if (!Utils.isNull(tv_title_left)) {
            tv_title_left.setVisibility(View.GONE);
        }
    }

    @Override
    public void hideImageRightBar () {
        if (!Utils.isNull(iv_right)) {
            iv_right.setVisibility(View.INVISIBLE);
        }
        if(!Utils.isNull(layout_right)){
            layout_right.setEnabled(false);
        }
    }

    @Override
    public void showImageRightBar () {
        if (!Utils.isNull(iv_right)) {
            iv_right.setVisibility(View.VISIBLE);
        }
        hidetRightTextView();
    }

    @Override
    public void showImageLeftBar () {
        if (!Utils.isNull(layout_left)) {
            layout_left.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideImageLeftBar () {
        if (!Utils.isNull(layout_left)) {
            layout_left.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void hideLayoutRight () {
        hidetRightButton();
        hidetRightTextView();
    }

    @Override
    public void setLeftActionBarOnClickListener (
            OnClickListener onClickListener) {
        if (!Utils.isNull(layout_left)) {
            this.leftOnClickListener = onClickListener;
            layout_left.setOnClickListener(leftOnClickListener);
        }
    }


    @Override
    public void setRightActionBarOnClickListener (
            OnClickListener onClickListener) {
        if (!Utils.isNull(layout_right)) {
            layout_right.setEnabled(true);
            layout_right.setVisibility(View.VISIBLE);
            layout_right.setOnClickListener(onClickListener);
        }
    }


    public View findViewById (int id) {
        return mView.findViewById(id);
    }

    @Override
    public void setBarVisibility (boolean bool) {
        if (mView != null) {
            if (bool) {
                mView.setVisibility(View.VISIBLE);
            } else {
                mView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void hideActionBar () {
        if (mView != null) {
            mView.setVisibility(View.GONE);
        }
    }
}
