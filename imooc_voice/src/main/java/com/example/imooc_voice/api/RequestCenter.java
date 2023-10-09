package com.example.imooc_voice.api;

import com.example.imooc_voice.R;
import com.example.imooc_voice.view.login.user.User;
import com.example.lib_network.okhttp.CommonOkHttpClient;
import com.example.lib_network.okhttp.listener.DisposeDataHandle;
import com.example.lib_network.okhttp.listener.DisposeDataListener;
import com.example.lib_network.okhttp.request.CommonRequest;
import com.example.lib_network.okhttp.request.RequestParams;

public class RequestCenter {
//    static class HttpConstants {
////        private static final String ROOT_URL = "http://39.97.122.129";
//         private static final String ROOT_URL = "http://imooc.com/api";
//
////        public static String LOGIN = ROOT_URL + "/module_voice/login_phone";
//
//    }
    public static void getRequest(String url, RequestParams params, DisposeDataListener listener, Class<?> clazz) {
        CommonOkHttpClient.get(CommonRequest.createGetRequest(url, params), new DisposeDataHandle(listener, clazz));
    }
    public static void postRequest(String url, RequestParams params,DisposeDataListener listener, Class<?> clazz){
        CommonOkHttpClient.post(CommonRequest.createPostRequest(url, params), new DisposeDataHandle(listener, clazz));
    }
    /**
     * 用户登陆请求
     */
    public static void login(DisposeDataListener listener) {
        RequestParams params = new RequestParams();
        params.put("mb", "18734924592");
        params.put("pwd", "999999q");
        RequestCenter.postRequest("http://192.168.0.7:3000/api/example", params, listener, User.class);
    }
}
