package com.example.apple.voicetest;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;
import com.example.apple.voicetest.fragment.fragment_index;
import com.example.apple.voicetest.fragment.fragment_map;
import com.example.apple.voicetest.fragment.fragment_person;
import com.example.apple.voicetest.fragment.fragment_weather;

public class TabLayoutPage extends AppCompatActivity {
    private TabLayout mTabLayout;
    //Tab 文字
    private final int[] TAB_TITLES = new int[]{R.string.index,R.string.weather,R.string.person};
    //Tab 图片
    private final int[] TAB_IMGS = new int[]{R.drawable.ic_home_yellow_24dp,R.drawable.ic_cloud_blank_24dp,R.drawable.ic_person_blank_24dp};
    //Fragment 数组
    private final Fragment[] TAB_FRAGMENTS = new Fragment[] {new fragment_index(),new fragment_weather(),new fragment_person()};
    //Tab 数目
    private final int COUNT = TAB_TITLES.length;
    private MyViewPagerAdapter mAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_layout_page);
        initViews();
        SDKInitializer.initialize(getApplicationContext());
    }

    private void initViews() {
        mTabLayout = (TabLayout)findViewById(R.id.tab_layout);
        setTabs(mTabLayout,this.getLayoutInflater(),TAB_TITLES,TAB_IMGS);

        mAdapter = new MyViewPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View customView = tab.getCustomView();
                TextView tabText = (TextView) customView.findViewById(R.id.tv_tab);
                ImageView tabIcon = (ImageView) customView.findViewById(R.id.img_tab);
                Resources resource = (Resources) getBaseContext().getResources();
                ColorStateList csl = (ColorStateList) resource.getColorStateList(R.color.yellow);
                if (csl != null) {
                    tabText.setTextColor(csl);
                }
                String s = tabText.getText().toString();
                if (getString(R.string.index).equals(s)) {
                    tabIcon.setImageResource(R.drawable.ic_home_yellow_24dp);
//                } else if (getString(R.string.map).equals(s)) {
//                    tabIcon.setImageResource(R.drawable.ic_location_yellow_24dp);
                } else if (getString(R.string.weather).equals(s)) {
                    tabIcon.setImageResource(R.drawable.ic_cloud_yellow_24dp);
                } else if (getString(R.string.person).equals(s)) {
                    tabIcon.setImageResource(R.drawable.ic_person_yellow_24dp);
                }
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View customView = tab.getCustomView();
                TextView tabText = (TextView) customView.findViewById(R.id.tv_tab);
                ImageView tabIcon = (ImageView) customView.findViewById(R.id.img_tab);
                Resources resource = (Resources) getBaseContext().getResources();
                ColorStateList csl = (ColorStateList) resource.getColorStateList(R.color.white);
                if (csl != null) {
                    tabText.setTextColor(csl);
                }
                String s = tabText.getText().toString();
                if (getString(R.string.index).equals(s)) {
                    tabIcon.setImageResource(R.drawable.ic_home_blank_24dp);
//                } else if (getString(R.string.map).equals(s)) {
//                    tabIcon.setImageResource(R.drawable.ic_location_blank_24dp);
                } else if (getString(R.string.weather).equals(s)) {
                    tabIcon.setImageResource(R.drawable.ic_cloud_blank_24dp);
                } else if (getString(R.string.person).equals(s)) {
                    tabIcon.setImageResource(R.drawable.ic_person_blank_24dp);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    /**
     * @description: 设置添加Tab
     */
    private void setTabs(TabLayout tabLayout, LayoutInflater inflater, int[] tabTitlees, int[] tabImgs){
        for (int i = 0; i < tabImgs.length; i++) {
            TabLayout.Tab tab = tabLayout.newTab();
            View view = inflater.inflate(R.layout.tab_custom,null);
            tab.setCustomView(view);

            TextView tvTitle = (TextView)view.findViewById(R.id.tv_tab);
            tvTitle.setText(tabTitlees[i]);
            ImageView imgTab = (ImageView) view.findViewById(R.id.img_tab);
            imgTab.setImageResource(tabImgs[i]);
            tabLayout.addTab(tab);

        }
    }

    /**
     * @description: ViewPager 适配器
     */
    private class MyViewPagerAdapter extends FragmentPagerAdapter {
        public MyViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return TAB_FRAGMENTS[position];
        }

        @Override
        public int getCount() {
            return COUNT;
        }
    }
}
