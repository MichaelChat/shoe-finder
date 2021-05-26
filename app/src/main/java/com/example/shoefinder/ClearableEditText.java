package com.example.shoefinder;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

public class ClearableEditText extends AppCompatEditText implements View.OnTouchListener, View.OnFocusChangeListener, TextWatcher {

    private static final int IC_CLEAR_WIDTH = 72;
    private static final int IC_CLEAR_HEIGHT = 72;
    private Drawable _clearTextIcon;
    private OnFocusChangeListener _onFocusChangeListener;
    private OnTouchListener _onTouchListener;

    public ClearableEditText(Context context) {
        super(context);
        init(context);
    }

    public ClearableEditText(Context context, final AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    public ClearableEditText(Context context, final AttributeSet attributeSet, final int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
        init(context);
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (isFocused()) {
            setClearIconVisible(s.length() > 0);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            setClearIconVisible(getText().length() > 0);
        } else {
            setClearIconVisible(false);
        }
        if (_onFocusChangeListener != null) {
            _onFocusChangeListener.onFocusChange(v, hasFocus);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        final int x = (int) event.getX();
        if (_clearTextIcon.isVisible() && x > getWidth() - getPaddingRight() - _clearTextIcon.getIntrinsicWidth()) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                setText("");
            }
            return true;
        }
        return _onTouchListener != null && _onTouchListener.onTouch(v, event);
    }

    @Override
    public void setOnFocusChangeListener(final OnFocusChangeListener onFocusChangeListener) {
        _onFocusChangeListener = onFocusChangeListener;
    }

    @Override
    public void setOnTouchListener(final OnTouchListener onTouchListener) {
        _onTouchListener = onTouchListener;
    }

    private void init(final Context context) {
        final Drawable drawable = ContextCompat.getDrawable(context, android.R.drawable.ic_menu_close_clear_cancel);
        final Drawable wrapDrawable = DrawableCompat.wrap(drawable); // for tinting pre-Lollipop

        DrawableCompat.setTint(wrapDrawable, getCurrentHintTextColor());
        _clearTextIcon = wrapDrawable;
        _clearTextIcon.setBounds(0, 0, IC_CLEAR_WIDTH, IC_CLEAR_HEIGHT);
        setClearIconVisible(false);

        super.setOnTouchListener(this);
        super.setOnFocusChangeListener(this);

        addTextChangedListener(this);
    }

    private void setClearIconVisible(boolean visible) {
        _clearTextIcon.setVisible(visible, false);
        final Drawable[] compoundDrawables = getCompoundDrawables();
        setCompoundDrawables(compoundDrawables[0], compoundDrawables[1], visible ? _clearTextIcon : null, compoundDrawables[3]);
    }
}
