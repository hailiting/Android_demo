package com.example.imooc_voice.view.friend;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.imooc_voice.R;
import com.example.imooc_voice.view.mine.MineFragment;

public class FriendFragment extends Fragment {
    public static Fragment newInstance(){
        FriendFragment fragment = new FriendFragment();
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mine_layout, container, false);

        TextView textView = rootView.findViewById(R.id.textView);
        textView.setText("Friend, Your text goes here"); // 设置文本内容

        return rootView;
    }
}

