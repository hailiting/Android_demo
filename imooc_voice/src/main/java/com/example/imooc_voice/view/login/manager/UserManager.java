package com.example.imooc_voice.view.login.manager;

import com.example.imooc_voice.model.BaseModel;
import com.example.imooc_voice.view.login.user.User;
import com.example.imooc_voice.view.login.user.UserContent;

/**
 * 单例管理登录用户信息
 */
public class UserManager {
    private static UserManager mInstance;
    private User mUser;

    public static UserManager getInstance(){
        if(mInstance == null){
            // 加锁  保证线程安全
            // 类的字节码锁
            synchronized (UserManager.class) {
                if(mInstance == null) {
                    mInstance = new UserManager();
                }
            }
        }
        return mInstance;
    }
    /**
     * 保存用户信息到内存
     */
    public void saveUser(User user) {
        mUser = user;
        saveLocal(user);
    }

    /**
     * 持久化用户信息
     * @param user
     */
    private void saveLocal(User user) {

    }
    /**
     * 获取用户信息
     */
    public User getUser(){
        return mUser;
    }
    private User getLocal(){
        return null;
    }
    public boolean hasLogin(){
        return getUser() == null?false : true;
    }
    public void removeUser(){
        mUser = null;
        removeLocal();
    }
    private void removeLocal() {

    }
}
