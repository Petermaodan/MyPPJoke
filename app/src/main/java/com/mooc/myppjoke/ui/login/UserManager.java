package com.mooc.myppjoke.ui.login;

//import androidx.lifecycle.MutableLiveData;

import androidx.lifecycle.MutableLiveData;

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
}
