package com.mooc.myppjoke.ui.login;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.mooc.libnetwork.ApiResponse;
import com.mooc.libnetwork.ApiService;
import com.mooc.libnetwork.JsonCallback;
import com.mooc.myppjoke.R;
import com.mooc.myppjoke.model.User;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private View actionClose;
    private View actionLogin;
    private Tencent tencent;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout_login);

        actionClose=findViewById(R.id.action_close);
        actionLogin=findViewById(R.id.action_login);

        actionClose.setOnClickListener(this);
        actionLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.action_close){
            finish();
        }else if (v.getId()==R.id.action_login){
            login();
        }
    }

    private void login() {
        if (tencent==null){
            tencent=Tencent.createInstance("101794421", getApplicationContext());
        }
        tencent.login(this,"all",loginListener);
    }

    IUiListener loginListener=new IUiListener() {
        @Override
        public void onComplete(Object o) {
            JSONObject response= (JSONObject) o;
            try {
                String openid=response.getString("openid");
                String access_token=response.getString("access_token");
                String expires_in=response.getString("expires_in");
                long expires_time=response.getLong("expires_time");

                tencent.setOpenId(openid);
                tencent.setAccessToken(access_token,expires_in);
                QQToken qqToken=tencent.getQQToken();
                getUserInfo(qqToken,expires_time,openid);
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onError(UiError uiError) {
            Toast.makeText(getApplicationContext(), "登录失败：reason"+uiError.toString(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(getApplicationContext(), "登录取消", Toast.LENGTH_SHORT).show();
        }
    };

    //通过该方法获取用户的信息
    private void getUserInfo(QQToken qqToken, long expires_time, String openid) {
        UserInfo userInfo=new UserInfo(getApplicationContext(),qqToken);
        userInfo.getUserInfo(new IUiListener() {
            @Override
            public void onComplete(Object o) {
                //登录成功就需要调用save方法对用户信息进行持久化
                JSONObject response= (JSONObject) o;
                try {
                    String nickname=response.getString("nickname");
                    String figureurl_2=response.getString("figureurl_2");

                    //登录成功就需要调用save方法对用户信息进行持久化
                    save(nickname,figureurl_2,openid,expires_time);
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(UiError uiError) {

            }

            @Override
            public void onCancel() {

            }
        });
    }

    private void save(String nickname, String avatar, String openid, long expires_time) {
        ApiService.get("/user/insert")
                .addParam("name",nickname)
                .addParam("avatar",avatar)
                .addParam("qqOpenId",openid)
                .addParam("expires_time",expires_time)
                .execute(new JsonCallback<User>() {
                    @Override
                    public void onSuccess(ApiResponse<User> response) {
                        if (response.body!=null){
                            UserManager.get().save(response.body);
                            finish();
                        }else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "登录失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(ApiResponse response) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "登录失败，msg"+response.message, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
    }
}
