package com.example.imooc_voice.view.home;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.example.imooc_voice.R;
import com.example.imooc_voice.view.common.widget.ViewPager2Helper;
import com.example.imooc_voice.view.home.adpater.HomePagerAdapter;
import com.example.imooc_voice.view.home.model.CHANNEL;
import com.example.lib_common_ui.base.BaseActivity;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

public class HomeActivity extends BaseActivity implements View.OnClickListener {
    private static final CHANNEL[] CHANNELS = new CHANNEL[]{CHANNEL.MY, CHANNEL.DISCORY,CHANNEL.FRIEND};

    /**
     * View
     */
    private DrawerLayout mDrawerLayout;
    private View mToggleView;
    private View mSearchView;
    private ViewPager2 mViewPager;

    // ViewPage 初始化
    private HomePagerAdapter mAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();
        initData();
    }
    private void initData() {

    }
    private void initView() {
        // 找出id
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mToggleView = findViewById(R.id.toggle_view);
        mToggleView.setOnClickListener(this);
        mSearchView=findViewById(R.id.search_view);
        mViewPager=findViewById(R.id.view_pager);
        mAdapter = new HomePagerAdapter(this, CHANNELS);
        mViewPager.setAdapter(mAdapter);
        initMagicIndicator();
    }
    // 初始化指示器
    private void initMagicIndicator(){
        MagicIndicator magicIndicator = findViewById(R.id.magic_indicator);
        magicIndicator.setBackgroundColor(Color.WHITE);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        // 初始化
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return CHANNELS == null ? 0 : CHANNELS.length;
            }
            @Override
            public IPagerTitleView getTitleView(Context context, int index) {
                SimplePagerTitleView simplePagerTitleView = new SimplePagerTitleView(context);
                simplePagerTitleView.setText(CHANNELS[index].getKey());
                simplePagerTitleView.setTextSize(19);
                simplePagerTitleView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                simplePagerTitleView.setNormalColor(Color.parseColor("#999999"));
                simplePagerTitleView.setSelectedColor(Color.parseColor("#333333"));
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v){
                        mViewPager.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                return null;
            }
        });
        // 绑定
        magicIndicator.setNavigator(commonNavigator);
        ViewPager2Helper.bind(magicIndicator, mViewPager);
    }
    @Override
    public void onClick(View v) {}
}