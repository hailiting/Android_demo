package com.example.imooc_voice.view.friend;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.viewmodel.CreationExtras;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.imooc_voice.R;
import com.example.imooc_voice.api.MockData;
import com.example.imooc_voice.api.RequestCenter;
import com.example.imooc_voice.view.friend.adapter.FriendRecyclerAdapter;
import com.example.imooc_voice.view.friend.model.BaseFriendModel;
import com.example.imooc_voice.view.friend.model.FriendBodyValue;
import com.example.lib_common_ui.recyclerview.wrapper.LoadMoreWrapper;
import com.example.lib_network.okhttp.listener.DisposeDataListener;
import com.example.lib_network.okhttp.utils.ResponseEntityToModule;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

public class FriendFragment extends Fragment implements OnRefreshListener, LoadMoreWrapper.OnLoadMoreListener {
    private Context mContext;
    // UI
    private RefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private FriendRecyclerAdapter mAdapter;
    private LoadMoreWrapper mLoadMoreWrapper;
    // data
    private BaseFriendModel mRecommendData;
    private List<FriendBodyValue> mDatum = new ArrayList<>(); // data 的复数形式  datum

    public static Fragment newInstance() {
        FriendFragment fragment = new FriendFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friend_layout, container, false);
        mSwipeRefreshLayout = rootView.findViewById(R.id.refresh_layout);
        mSwipeRefreshLayout.setRefreshHeader(new ClassicsHeader(mContext));
        mSwipeRefreshLayout.setRefreshFooter(new ClassicsFooter(mContext));
        mSwipeRefreshLayout.setPrimaryColors(getResources().getColor(android.R.color.holo_red_light));
        mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(2000/*,false*/); // 传入false表示刷新失败
            }
        });
        mSwipeRefreshLayout.autoRefresh();
        mRecyclerView = rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requestData();
    }
    @Override
    // 下拉刷新
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        requestData();
    }

    @Override
    public void onLoadMoreRequested() {
        loadMore();
    }

    @NonNull
    @Override
    public CreationExtras getDefaultViewModelCreationExtras() {
        return super.getDefaultViewModelCreationExtras();
    }


    private void requestData() {
        RequestCenter.requestFriendData(new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                mRecommendData = (BaseFriendModel) responseObj;
                updateView();
            }

            @Override
            public void onFailure(Object reasonObj) {
                //显示请求失败View,显示mock数据
                onSuccess(ResponseEntityToModule.parseJsonToModule(MockData.FRIEND_DATA,
                        BaseFriendModel.class));
            }
        });
    }

    // 更新UI
    private void updateView() {
        // 结束刷新状态
        mSwipeRefreshLayout.finishRefresh();
        mDatum = mRecommendData.data.list;
        mAdapter = new FriendRecyclerAdapter(mContext, mDatum);
        // 加载更多初始化
        mLoadMoreWrapper = new LoadMoreWrapper(mAdapter);
        mLoadMoreWrapper.setLoadMoreView(R.layout.default_loading);
        mLoadMoreWrapper.setOnLoadMoreListener(this);
        mRecyclerView.setAdapter(mLoadMoreWrapper);
    }

    private void loadMore() {
        RequestCenter.requestFriendData(new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                BaseFriendModel moreDate = (BaseFriendModel) responseObj;
                mDatum.addAll(moreDate.data.list);
                mLoadMoreWrapper.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Object reasonObj) {
                onSuccess(ResponseEntityToModule.parseJsonToModule(MockData.FRIEND_DATA, BaseFriendModel.class));
            }
        });
    }
}

