package com.mooc.myppjoke.ui.login;

//import androidx.lifecycle.MutableLiveData;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mooc.libnetwork.cache.CacheManager;
import com.mooc.myppjoke.model.User;

public class UserManager {
    private static final String KEY_CACHE_USER = "cache_user";
    private static UserManager mUserManager = new UserManager();
    private MutableLiveData<User> userLiveData;
    private User mUser;

    public static UserManager get() {
        return mUserManager;
    }

    public long getUserId() {
        return mUser.userId;
    }

    public void save(User user) {
        mUser=user;
        CacheManager.save(KEY_CACHE_USER,user);
        if (getUserLiveData().hasObservers()){
            getUserLiveData().postValue(user);
        }
    }

    public LiveData<User> login(Context context){
        Intent intent=new Intent(context,LoginActivity.class);
        if (!(context instanceof Activity)){
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
        return getUserLiveData();
    }

    private MutableLiveData<User> getUserLiveData() {
        if (userLiveData==null){
            userLiveData=new MutableLiveData<>();
        }
        return userLiveData;
    }

    public boolean isLogin() {
        return mUser!=null;
    }
}
