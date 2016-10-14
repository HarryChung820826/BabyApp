package com.example.babyapp;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class FragmentAdapter extends FragmentPagerAdapter{

	List<Fragment> fragmentList = new ArrayList<Fragment>();

	public FragmentAdapter(FragmentManager fm,List<Fragment> fragmentlist) {
		super(fm);
		this.fragmentList = fragmentlist;
	}

	@Override
	public Fragment getItem(int pos) {
		// TODO Auto-generated method stub
		return fragmentList.get(pos);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return fragmentList.size();
	}

}
