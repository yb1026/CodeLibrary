package com.cultivator.codelibrary.util;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;


/**
 * view工具
 *
 * @author Simon.xin
 */
public class ViewUtil {

    /**
     * 动态设置ListView的高度
     * 注: ListView子项元素必须为LinearLayout
     *
     * @param listView
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        if (listView == null) {
            return;
        }
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        // listAdapter.getCount()返回数据项的数目
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽,高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    public static boolean hasEmpty(TextView[] edits) {
        boolean has = false;
        for (TextView editText : edits) {
            if (TextUtils.isEmpty(editText.getText().toString().trim())) {
                return true;
            }
        }
        return has;
    }



    public static Animation getZoomAnimation() {
        ScaleAnimation animation = new ScaleAnimation(1f, 0.85f, 1f, 0.85f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        animation.setDuration(150);
        animation.setInterpolator(new CycleInterpolator(0.5f));
        return animation;
    }

    /**
     * 点击回弹动画
     * @param view
     * @param listener
     */
    public static void setAnimationListener(View view,final AnimationClickListener listener){
        Animation zoomout = getZoomAnimation();
        zoomout.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation paramAnimation) {
            }

            @Override
            public void onAnimationRepeat(Animation paramAnimation) {
            }

            @Override
            public void onAnimationEnd(Animation paramAnimation) {
                listener.onAnimationEnd();
            }
        });
        view.startAnimation(zoomout);
    }
    public interface AnimationClickListener{
        public void onAnimationEnd();
    }
}
