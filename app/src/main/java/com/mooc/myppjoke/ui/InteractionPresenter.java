package com.mooc.myppjoke.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.alibaba.fastjson.JSONObject;
import com.mooc.libcommon.extention.LiveDataBus;
import com.mooc.libcommon.global.AppGlobals;
import com.mooc.libnetwork.ApiResponse;
import com.mooc.libnetwork.ApiService;
import com.mooc.libnetwork.JsonCallback;
import com.mooc.libnetwork.JsonConvert;
import com.mooc.myppjoke.model.Comment;
import com.mooc.myppjoke.model.Feed;
import com.mooc.myppjoke.model.User;
import com.mooc.myppjoke.ui.login.UserManager;
import com.mooc.myppjoke.ui.share.ShareDialog;

public class InteractionPresenter {

    public static final String DATA_FROM_INTERACTION = "data_from_interaction";

    private static final String URL_TOGGLE_FEED_LIK = "/ugc/toggleFeedLike";

    private static final String URL_TOGGLE_FEED_DISS = "/ugc/dissFeed";
    private static final String URL_SHARE = "/ugc/increaseShareCount";
    private static final String URL_TOGGLE_COMMENT_LIKE = "/ugc/toggleCommentLike";


    //给一个帖子点赞/取消点赞，他和给帖子点踩一踩是互斥的
    public static void toggleFeedLike(LifecycleOwner owner, Feed feed){
        if (!isLogin(owner, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                toggleFeedLikeInternal(feed);
            }
        })) {
        }else {
            toggleFeedLikeInternal(feed);
        }
    }

    private static void toggleFeedLikeInternal(Feed feed) {
        ApiService.get(URL_TOGGLE_FEED_LIK)
                .addParam("userId",UserManager.get().getUserId())
                .addParam("itemId",feed.itemId)
                .execute(new JsonCallback<JSONObject>() {
                    @Override
                    public void onSuccess(ApiResponse<JSONObject> response) {
                        if (response.body!=null){
                            boolean hasLiked = response.body.getBoolean("hasLiked").booleanValue();
                            feed.getUgc().setHasLiked(hasLiked);
//                            LiveDataBus.get().
                        }
                    }

                    @Override
                    public void onError(ApiResponse<JSONObject> response) {
                        showToast(response.message);
                    }
                });
    }

    //给一个帖子点踩一踩/取消踩一踩，他和给帖子点赞是互斥的
    public  static void toggleFeedDiss(LifecycleOwner owner,Feed feed){
        if (!isLogin(owner,user -> {
            toggleFeedDissInternal(feed);
        })){}else {
            toggleFeedDissInternal(feed);
        }
    }

    private static void toggleFeedDissInternal(Feed feed) {
        ApiService.get(URL_TOGGLE_FEED_DISS)
                .addParam("userId",UserManager.get().getUserId())
                .addParam("itemId",feed.itemId)
                .execute(new JsonCallback<JSONObject>() {
                    @Override
                    public void onSuccess(ApiResponse<JSONObject> response) {
                        if (response.body!=null){
                            boolean hasLiked=response.body.getBoolean("hasLiked").booleanValue();
                            feed.getUgc().setHasdiss(hasLiked);
                        }
                    }

                    @Override
                    public void onError(ApiResponse<JSONObject> response) {
                        showToast(response.message);
                    }
                });
    }



    //打开分享面板
    public static void openShare(Context context,Feed feed){
        String url= "http://h5.aliyun.ppjoke.com/item/%s?timestamp=%s&user_id=%s";

        String shareContent=feed.feeds_text;
        if (!TextUtils.isEmpty(feed.url)){
            shareContent=feed.url;
        }else if (!TextUtils.isEmpty(feed.cover)){
            shareContent=feed.cover;
        }

        ShareDialog shareDialog=new ShareDialog(context);
        shareDialog.setShareContent(shareContent);
        shareDialog.setShareItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiService.get(URL_SHARE)
                        .addParam("itemId",feed.itemId)
                        .execute(new JsonCallback<JSONObject>(){
                            @Override
                            public void onSuccess(ApiResponse<JSONObject> response) {
                                if (response.body!=null){
                                    int count=response.body.getIntValue("count");
                                    feed.getUgc().setShareCount(count);
                                }
                            }

                            @Override
                            public void onError(ApiResponse<JSONObject> response) {
                                showToast(response.message);
                            }
                        });
            }
        });

        shareDialog.show();
    }

    //给一个帖子的评论点赞/取消点赞
    public static void toggleCommentLike(LifecycleOwner owner, Comment comment){
        if (!isLogin(owner, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                toggleCommentLikeInternal(comment);
            }
        })){}else {
            toggleCommentLikeInternal(comment);
        }
    }

    private static void toggleCommentLikeInternal(Comment comment) {
        ApiService.get(URL_TOGGLE_COMMENT_LIKE)
                .addParam("commentId",comment.commentId)
                .addParam("userId",UserManager.get().getUserId())
                .execute(new JsonCallback<JSONObject>() {
                    @Override
                    public void onSuccess(ApiResponse<JSONObject> response) {
                        if (response.body!=null){
                            boolean hasLike=response.body.getBooleanValue("hasLiked");
                            comment.getUgc().setHasLiked(hasLike);
                        }
                    }

                    @Override
                    public void onError(ApiResponse<JSONObject> response) {
                        showToast(response.message);
                    }
                });
    }

    @SuppressLint("RestrictedApi")
    private static void showToast(String message) {
        ArchTaskExecutor.getMainThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(AppGlobals.getApplication(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static boolean isLogin(LifecycleOwner owner,Observer<User> observer){
        if (UserManager.get().isLogin()){
            return true;
        }else {
            LiveData<User> liveData=UserManager.get().login(AppGlobals.getApplication());
            if (owner==null){
                liveData.observeForever(loginObserver(observer,liveData));
            }else {
                liveData.observe(owner,loginObserver(observer,liveData));
            }
            return false;
        }
    }

    private static Observer<User> loginObserver(Observer<User> observer, LiveData<User> liveData) {
        return new Observer<User>() {
            @Override
            public void onChanged(User user) {
                liveData.removeObserver(this);
                if (user!=null&&observer!=null){
                    observer.onChanged(user);
                }
            }
        };
    }
}
