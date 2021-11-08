package com.mooc.libcommon.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CornerFrameLayout extends FrameLayout {
    public CornerFrameLayout(@NonNull Context context) {
        super(context);
    }

    public CornerFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CornerFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    public CornerFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        ViewHelper.setViewOutLine(this, attrs, defStyleAttr, defStyleRes);
    }

    public void setViewOutline(int radius,int radiusSide){
        ViewHelper.setViewOutLine(this,radius,radiusSide);
    }
}
