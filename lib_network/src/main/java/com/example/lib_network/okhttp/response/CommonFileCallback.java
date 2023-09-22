package com.example.lib_network.okhttp.response;

import android.os.Looper;
import android.os.Message;
import android.os.Handler;

import com.example.lib_network.okhttp.exception.OkHttpException;
import com.example.lib_network.okhttp.listener.DisposeDataHandle;
import com.example.lib_network.okhttp.listener.DisposeDownloadListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class CommonFileCallback implements Callback {
    protected final int NETWORK_ERROR = -1; // the network relative error
    protected final int IO_ERROR = -2; // the JSON relative error
    protected final String EMPTY_MSG = "";

    /**
     * 将其他线程数据转发到UI线程
     */
    private static final int PROGRESS_MESSAGE = 0x01;
    private Handler mDeliveryHandler;
    private DisposeDownloadListener mListener;
    private String mFilePath;
    private int mProgress;
    public CommonFileCallback(DisposeDataHandle handle) {
        this.mListener = (DisposeDownloadListener) handle.mListener;
        this.mFilePath = handle.mSource;
        this.mDeliveryHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case PROGRESS_MESSAGE:
                        mListener.onProgress((int) msg.obj);
                        break;
                }
            }
        };
    }
    @Override
    public void onFailure(Call call, IOException e) {
        mDeliveryHandler.post(new Runnable() {
            @Override
            public void run() {
                mListener.onFailure(new OkHttpException(NETWORK_ERROR, e));
            }
        });
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        final File file = handleResponse(response);
        mDeliveryHandler.post(new Runnable() {
            @Override
            public void run() {
                // 文件保存成功
                if(file!=null){
                    mListener.onSuccess(file);
                }else  {
                    mListener.onFailure(new OkHttpException(IO_ERROR, EMPTY_MSG));
                }
            }
        });
    }
    private File handleResponse(Response response) {
        //从服务端拿到流 并写到前端
        if (response == null){
            return null;
        }
        InputStream inputStream = null;
        File file = null;
        FileOutputStream fos = null;
        byte[] buffer = new byte[2048];
        // 计算进度
        int length;
        double sumLength;
        double currentLeng = 0;
        try {
            checkLocalFilePath(mFilePath);
            file = new File(mFilePath);
            fos = new FileOutputStream(file);
            inputStream = response.body().byteStream();
            sumLength = response.body().contentLength();
            while ((length = inputStream.read(buffer)) != -1){
                fos.write(buffer, 0, length);
                currentLeng += length;
                mProgress = (int) (currentLeng / sumLength)*100;
                mDeliveryHandler.obtainMessage(PROGRESS_MESSAGE, mProgress).sendToTarget();
            }
            // 关掉流
            fos.flush();
        } catch (Exception e){
            file = null;
        } finally {
            try {
                if(fos!=null){
                    fos.close();
                }
                if(inputStream != null){
                    inputStream.close();
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }
        return file;
    }
    private void checkLocalFilePath(String localFilePath){
        File path = new File(localFilePath.substring(0,
                localFilePath.lastIndexOf("/")+1));
        File file = new File(localFilePath);
        if(!path.exists()){
            path.mkdirs();
        }
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
