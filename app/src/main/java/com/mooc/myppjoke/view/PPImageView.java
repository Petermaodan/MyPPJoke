package com.mooc.myppjoke.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.mooc.libcommon.global.utils.PixUtils;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class PPImageView extends AppCompatImageView {
    public PPImageView(@NonNull Context context) {
        super(context);
    }

    public PPImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PPImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @BindingAdapter(value = {"blur_url", "radius"})
    public static void setBlurImageUrl(ImageView imageView, String blurUrl, int radius) {
        Glide.with(imageView).load(blurUrl).override(radius)
                .transform(new BlurTransformation())
                .dontAnimate()
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        imageView.setBackground(resource);
                    }
                });
    }

    public void setImageUrl(String imageUrl) {
        setImageUrl(this, imageUrl, false);
    }

    @BindingAdapter(value = {"image_url","isCircle"})
    public static void setImageUrl(PPImageView view,String imageUrl,boolean isCircle){
        view.setImageUrl(view, imageUrl, isCircle, 0);
    }


    @BindingAdapter(value = {"image_url","isCircle","radius"},requireAll = false)
    public static void setImageUrl(PPImageView view, String imageUrl, boolean isCircle, int radius) {
        RequestBuilder<Drawable> builder= Glide.with(view).load(imageUrl);
        if (isCircle){
            builder.transform(new CircleCrop());
        }else if (radius>0){
            builder.transform(new RoundedCornersTransformation(PixUtils.dp2px(radius), 0));
        }
        ViewGroup.LayoutParams layoutParams=view.getLayoutParams();
        if (layoutParams!=null&&layoutParams.width>0&&layoutParams.height>0){
            builder.override(layoutParams.width,layoutParams.height);
        }
        builder.into(view);
    }


    public void bindData(int widthPx,int heightPx,int marginLeft,String imageUrl){
        bindData(widthPx,heightPx,marginLeft, PixUtils.getScreenWidth(),PixUtils.getScreenHeight(),imageUrl);
    }

    private void bindData(int widthPx, int heightPx, int marginLeft,final int screenWidth,final int screenHeight, String imageUrl) {

    }
}
