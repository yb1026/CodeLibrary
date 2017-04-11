package com.cultivator.codelibrary.widget.softkeyboard;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.Selection;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.cultivator.codelibrary.R;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SolfNumberKeyBoardView {

    private int clickcolor;

    private int nomalcolor;

    private Context mContext;

    private View softInputView;

    private EditText currentView;

    private LinearLayout btn_delete;

    private LinearLayout sure_btn;

    private TextView sureText;

    private View footView;

    public void setFootView(View footView) {
        this.footView = footView;
    }

    /**
     * 隐藏键盘按钮
     */
    private ImageView hintImgeBtn;

    private  Map<Integer, Integer> idmap = new HashMap<Integer, Integer>();

    private  Map<Integer, Integer> numKeyMap = new HashMap<Integer, Integer>();


    public static SolfNumberKeyBoardView instance(Context context,
                                                  RelativeLayout foot, OnKeyboardListener listener) {
        View softKeyBoardView = View.inflate(context,
                R.layout.view_com_keyboard, null);
        SolfNumberKeyBoardView solfKeyBoard = new SolfNumberKeyBoardView(context,
                softKeyBoardView);
        solfKeyBoard.setOnKeyboardListener(listener);

        LayoutParams softLayoutParams = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);

        foot.addView(softKeyBoardView, softLayoutParams);
        // foot.setVisibility(View.GONE);
        solfKeyBoard.setFootView(foot);
        solfKeyBoard.hideKeyboard();

        // 捕捉冒泡事件
        foot.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                return;
            }
        });
        return solfKeyBoard;
    }

    private SolfNumberKeyBoardView(Context context, View _view) {
        mContext = context;
        softInputView = _view;

        nomalcolor = context.getResources().getColor(R.color.white);
        clickcolor = context.getResources().getColor(R.color.color_gray_cccccc);

        initView();
        initListener();

        initNumKeyMap(false);
        setupKeypad(_view);
    }


    private Integer[] getLayoutIds() {
        Integer[] layArr = new Integer[]{R.id.com_zero_lay, R.id.com_one_lay,
                R.id.com_two_lay, R.id.com_three_lay, R.id.com_four_lay,
                R.id.com_five_lay, R.id.com_six_lay, R.id.com_seven_lay,
                R.id.com_eight_lay, R.id.com_nine_lay};
        return layArr;
    }

    // 无序
    private void initNumKeyMap(boolean nosort) {

        Integer[] arr = new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        List<Integer> list = Arrays.asList(arr);

        if (nosort) {
            Collections.shuffle(list);
        }


        Integer[] layArr = getLayoutIds();

        Integer[] textArr = new Integer[]{R.id.com_zero_text,
                R.id.com_one_text, R.id.com_two_text, R.id.com_three_text,
                R.id.com_four_text, R.id.com_five_text, R.id.com_six_text,
                R.id.com_seven_text, R.id.com_eight_text, R.id.com_nine_text};

        Integer[] eventArr = new Integer[]{KeyEvent.KEYCODE_0,
                KeyEvent.KEYCODE_1, KeyEvent.KEYCODE_2, KeyEvent.KEYCODE_3,
                KeyEvent.KEYCODE_4, KeyEvent.KEYCODE_5, KeyEvent.KEYCODE_6,
                KeyEvent.KEYCODE_7, KeyEvent.KEYCODE_8, KeyEvent.KEYCODE_9};

        for (int i = 0; i <= 9; i++) {
            TextView text = (TextView) softInputView.findViewById(textArr[i]);
            View lay = softInputView.findViewById(layArr[i]);

            text.setText(String.valueOf(list.get(i)));
            lay.setBackgroundColor(nomalcolor);

            idmap.put(layArr[i], eventArr[list.get(i)]);

            numKeyMap.put(eventArr[i], i);
        }
        idmap.put(R.id.com_sure_lay, KeyEvent.KEYCODE_ENTER);
        idmap.put(R.id.com_delete_imgbt, KeyEvent.KEYCODE_DEL);

    }


    private void initView() {
    }

    private void initListener() {
    }

    private void setupKeypad(View _view) {
        _view.findViewById(R.id.com_one_lay).setOnTouchListener(
                onTouchListener);
        _view.findViewById(R.id.com_two_lay).setOnTouchListener(
                onTouchListener);
        _view.findViewById(R.id.com_three_lay).setOnTouchListener(
                onTouchListener);
        _view.findViewById(R.id.com_four_lay).setOnTouchListener(
                onTouchListener);
        _view.findViewById(R.id.com_five_lay).setOnTouchListener(
                onTouchListener);
        _view.findViewById(R.id.com_six_lay).setOnTouchListener(
                onTouchListener);
        _view.findViewById(R.id.com_seven_lay).setOnTouchListener(
                onTouchListener);
        _view.findViewById(R.id.com_eight_lay).setOnTouchListener(
                onTouchListener);
        _view.findViewById(R.id.com_nine_lay).setOnTouchListener(
                onTouchListener);

        _view.findViewById(R.id.com_zero_lay).setOnTouchListener(
                onTouchListener);

        sure_btn = (LinearLayout) _view.findViewById(R.id.com_sure_lay);
        sure_btn.setOnTouchListener(onTouchListener);


        sureText = (TextView) _view.findViewById(R.id.com_sure_text);
        // softInputView.findViewById(R.id.com_keyboard).setOnClickListener(this);

        btn_delete = (LinearLayout) _view
                .findViewById(R.id.com_delete_imgbt);
        btn_delete.setOnTouchListener(onTouchListener);

        _view.findViewById(R.id.com_sure_lay).setOnTouchListener(
                onTouchListener);

        hintImgeBtn = (ImageView) _view.findViewById(R.id.com_board_hint_btn);
        hintImgeBtn.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                hideKeyboard();
            }
        });

    }

    OnTouchListener onTouchListener = new OnTouchListener() {

        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {

                v.setBackgroundColor(clickcolor);
                keyPressed(idmap.get(v.getId()));
            } else {
                v.setBackgroundColor(nomalcolor);
                return false;
            }
            return true;
        }

    };

    private void keyPressed(int keyCode) {
        Editable etext = null;
        if (keyCode == KeyEvent.KEYCODE_DEL) {
            if (currentView.length() == 0) {
                return;
            }
            String text = currentView.getText().toString();
            // currentView.setText(text);
            currentView.setText(text.subSequence(0, currentView.length() - 1));
            etext = currentView.getText();
        } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
            sure_btn.setEnabled(false);
            if (mListener != null) {
                mListener.sureClick();
            }
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    sure_btn.setEnabled(true);
                }
            }, 500l);
            return;
        } else {
            String text = currentView.getText().toString();
            // currentView.setText(text);
            currentView.setText(new StringBuffer(text).append(numKeyMap.get(
                    keyCode).toString()));
            etext = currentView.getText();
        }

        Selection.setSelection(etext, currentView.length());

    }

    private void setBackGroundNormal() {

        try {
            Integer[] layArr = getLayoutIds();

            for (Integer id : layArr) {
                softInputView.findViewById(id).setBackgroundColor(nomalcolor);
            }
        } catch (Exception e) {
        }

    }


    public void showKeyboard(EditText view) {
        currentView = view;
        setBackGroundNormal();
        footView.setVisibility(View.VISIBLE);
    }


    public void randomSort() {
        initNumKeyMap(true);
    }


    public boolean isShown() {
        return softInputView.isShown();
    }

    public void hideKeyboard() {
        footView.setVisibility(View.GONE);
        // softInputView.setVisibility(View.GONE);
    }

    public interface OnKeyboardListener {
        // 确认按钮事件
        void sureClick();
    }

    private OnKeyboardListener mListener;

    public void setOnKeyboardListener(OnKeyboardListener listener) {
        mListener = listener;
    }

    public View getCurrentView() {
        return currentView;
    }

    public ImageView getHintImgeBtn() {
        return hintImgeBtn;
    }


    public LinearLayout getSure_btn() {
        return sure_btn;
    }

    public TextView getSureText() {
        return sureText;
    }


}
