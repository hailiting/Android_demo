package com.example.lib_network.okhttp.exception;

public class OkHttpException extends Exception {
    private static final long serialVersionUID = 1L;
    private int ecode;
    private Object emsg;

    public OkHttpException(int ecode, Object emsg){
        this.ecode = ecode;
        this.emsg = emsg;
    }

    public int getEcode() {
        return ecode;
    }
    public Object getEmsg(){
        return emsg;
    }
}
