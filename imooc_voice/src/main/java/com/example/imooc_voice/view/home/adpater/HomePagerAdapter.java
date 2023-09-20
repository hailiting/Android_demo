package com.example.imooc_voice.view.home.adpater;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.imooc_voice.view.discory.DiscoryFragment;
import com.example.imooc_voice.view.friend.FriendFragment;
import com.example.imooc_voice.view.home.model.CHANNEL;
import com.example.imooc_voice.view.mine.MineFragment;

/**
 * 首页viewpageAdapter
 */
public class HomePagerAdapter extends FragmentStateAdapter {
    private CHANNEL[] mList;
    public HomePagerAdapter(@NonNull FragmentActivity fragmentActivity, CHANNEL[] datas) {
        super(fragmentActivity);
        mList = datas;
    }

    @Override
    public int getItemCount() {
        return mList==null ? 0 : mList.length;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
       int type = mList[position].getValue();
       // 逐一创建，提高内存使用率
       switch (type){
           case CHANNEL.MINE_ID:
               return MineFragment.newInstance();
           case CHANNEL.DISCORY_ID:
               return DiscoryFragment.newInstance();
           case CHANNEL.FRIEND_ID:
               return FriendFragment.newInstance();
       }
       return  null;
    }
}
