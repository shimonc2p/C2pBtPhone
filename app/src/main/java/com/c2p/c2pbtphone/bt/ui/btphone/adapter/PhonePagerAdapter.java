package com.c2p.c2pbtphone.bt.ui.btphone.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
	* 蓝牙电话界面Fragment的适配器
	* Created by heyu on 17/7/6.
	*/
public class PhonePagerAdapter extends FragmentPagerAdapter {

				private List<Fragment> fragmentList = new ArrayList<>();

				public PhonePagerAdapter(FragmentManager fm, List<Fragment> fragmentList){
								super(fm);
								setFragmentList(fragmentList);
				}

				public void setFragmentList(List<Fragment> fragmentList){
								if (!this.fragmentList.isEmpty()){
												this.fragmentList.clear();
								}
								this.fragmentList.addAll(fragmentList);
								notifyDataSetChanged();
				}

				@Override
				public Fragment getItem(int position){
								return fragmentList.get(position);
				}

				@Override
				public int getCount(){
								return fragmentList.size();
				}

				@Override
				public CharSequence getPageTitle(int position){
								return "";
				}

				@Override
				public int getItemPosition(Object object) {
								return POSITION_NONE;
				}
}
