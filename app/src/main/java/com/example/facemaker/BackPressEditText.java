package com.example.facemaker;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

public class BackPressEditText extends androidx.appcompat.widget.AppCompatEditText {
    private OnBackPressListener listener;

    public BackPressEditText(Context context) {
        super(context);
    }


    public BackPressEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public BackPressEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && listener != null) {
            listener.onBackPress(keyCode);
        }

        return super.onKeyPreIme(keyCode, event);
    }


    public void setOnBackPressListener(OnBackPressListener listener) {
        this.listener = listener;
    }

    public interface OnBackPressListener {
        void onBackPress(int keyCode);
    }
}