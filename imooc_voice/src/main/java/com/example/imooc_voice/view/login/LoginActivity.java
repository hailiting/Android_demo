package com.example.imooc_voice.view.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.imooc_voice.R;
import com.example.imooc_voice.api.RequestCenter;
import com.example.imooc_voice.view.login.manager.UserManager;
import com.example.imooc_voice.view.login.user.LoginEvent;
import com.example.imooc_voice.view.login.user.User;
import com.example.lib_common_ui.base.BaseActivity;
import com.example.lib_network.okhttp.listener.DisposeDataListener;

import org.greenrobot.eventbus.EventBus;

public class LoginActivity extends BaseActivity implements DisposeDataListener {
    // 外部启动方法
    public static void start(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_layout);
        findViewById(R.id.login_view).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.d("dddd", "111");
                RequestCenter.login(LoginActivity.this);
            }
        });
    }
    @Override
    public void onSuccess(Object responseObj) {
        User user = (User) responseObj;
        UserManager.getInstance().saveUser(user);
        // 发送一个用户登录事件
        EventBus.getDefault().post(new LoginEvent());
        finish();
    }

    @Override
    public void onFailure(Object reasonObj) {
    }
}
