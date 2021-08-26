package com.mooc.myppjoke.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.mooc.libcommon.global.utils.PixUtils;
import com.mooc.myppjoke.R;
import com.mooc.myppjoke.exoplayer.IPlayTarget;

/**
 * 列表视频播放专用
 */
public class ListPlayerView extends FrameLayout implements IPlayTarget, PlayerControlView.VisibilityListener, Player.EventListener {
    public View bufferView;
    public PPImageView cover, blur;
    protected ImageView playBtn;
    protected String mCategory;
    protected String mVideoUrl;
    protected boolean isPlaying;
    protected int mWidthPx;
    protected int mHeightPx;


    public ListPlayerView(@NonNull Context context) {
        super(context);
    }

    public ListPlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ListPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ListPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        //将layout_player_view转换成View添加进来
        LayoutInflater.from(context).inflate(R.layout.layout_player_view, this, true);

        //缓冲转圈圈的view
        bufferView = findViewById(R.id.buffer_view);
        //封面view
        cover = findViewById(R.id.cover);
        //高斯模糊背景图,防止出现两边留嘿
        blur = findViewById(R.id.blur_background);
        //播放盒暂停的按钮
        playBtn = findViewById(R.id.play_btn);

        playBtn.setOnClickListener(v -> {
            if (isPlaying()){
                inActive();
            }else {
                onActive();
            }
        });
        this.setTransitionName("listPlayerView");
    }

    public void bindData(String category,int widthPx,int heightPx,String coverUrl,String videoUrl){
        mCategory=category;
        mVideoUrl=videoUrl;
        mWidthPx=widthPx;
        mHeightPx=heightPx;
        cover.setImageUrl(coverUrl);

        //如果该视频的宽度小于高度,则高斯模糊背景图显示出来
        if (widthPx<heightPx){
            PPImageView.setBlurImageUrl(blur,coverUrl,10);
            blur.setVisibility(VISIBLE);
        }else {
            blur.setVisibility(INVISIBLE);
        }
        setSize(widthPx,heightPx);
    }

    private void setSize(int widthPx, int heightPx) {
        //这里主要是做视频宽大与高,或者高大于宽时  视频的等比缩放
        int maxWidth= PixUtils.getScreenWidth();
        int maxHeight=maxWidth;
        int layoutWidth = maxWidth;
        int layoutHeight = 0;

        int coverWidth;
        int coverHeight;
        if (widthPx >= heightPx) {
            coverWidth = maxWidth;
            layoutHeight = coverHeight = (int) (heightPx / (widthPx * 1.0f / maxWidth));
        } else {
            layoutHeight = coverHeight = maxHeight;
            coverWidth = (int) (widthPx / (heightPx * 1.0f / maxHeight));
        }

        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = layoutWidth;
        params.height = layoutHeight;
        setLayoutParams(params);

        ViewGroup.LayoutParams blurParams = blur.getLayoutParams();
        blurParams.width = layoutWidth;
        blurParams.height = layoutHeight;
        blur.setLayoutParams(blurParams);

        FrameLayout.LayoutParams coverParams = (LayoutParams) cover.getLayoutParams();
        coverParams.width = coverWidth;
        coverParams.height = coverHeight;
        coverParams.gravity = Gravity.CENTER;
        cover.setLayoutParams(coverParams);

        FrameLayout.LayoutParams playBtnParams = (LayoutParams) playBtn.getLayoutParams();
        playBtnParams.gravity = Gravity.CENTER;
        playBtn.setLayoutParams(playBtnParams);
    }

    @Override
    public ViewGroup getOwner() {
        return null;
    }

    @Override
    public void onActive() {

    }

    @Override
    public void inActive() {

    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public void onVisibilityChange(int visibility) {

    }
}
