package com.example.babyapp;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class ViewPager_Test extends FragmentActivity implements OnItemSelectedListener{

	private Spinner spin_date;
	private List<Fragment> mFragmentList = new ArrayList<Fragment>();
	private FragmentAdapter mFragmentAdapter;
	private ViewPager mPageVp;
	private int currentIndex; //目前頁面Index
	private int screenWidth; //畫面寬度
	private int overDay=0; //經過第幾天
	int month_index = 0 ;
	String[] date_array; //spinner 內容
	ArrayList<HashMap<String,String>> arraylist = new ArrayList<HashMap<String,String>>(); //每天的內容
	Date today = new Date();
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_pager__test);
		
		Intent intent = getIntent();
		if(intent.getStringExtra("Index") != null)
		{
			Log.e("Index",intent.getStringExtra("Index")+"");
			month_index = Integer.parseInt(intent.getStringExtra("Index"));
		}
		
		Log.e("month_index",month_index+"");
		init_spin();
		init_data();
		init();
		
		if(intent.getStringExtra("Date") != null)
		{
			Log.e("Date",intent.getStringExtra("Date")+"");
			mPageVp.setCurrentItem(Integer.parseInt(intent.getStringExtra("Date")));
		}
		
	}
	
	//處理 這個月的 已過的日期
	public void init_spin()
	{
		spin_date = (Spinner)findViewById(R.id.date_spin);
		date_array = DateProcess.GetOverDayOfMonth_DateStr(month_index);
		ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.datespinner,date_array);
		adapter.setDropDownViewResource(R.layout.datespinner);
		spin_date.setAdapter(adapter);
		
		spin_date.setOnItemSelectedListener(this);
	}
	
	//從資料庫收搜尋  這個月的 資料
	public void init_data()
	{
		for(int i = 0 ; i < date_array.length ; i++)
		{
//			Log.e("init_data_"+i,date_array[i]+"");
			String search_str = DateProcess.GetDateToImgName(DateProcess.GetStringToDate(date_array[i]));
			HashMap<String,String> hs = new MyDB(getApplicationContext()).get_data(search_str);
//			Log.e("init_data_HS_Date"+i,hs.get("Date")+"");
//			Log.e("init_data_HS_Content"+i,hs.get("Content")+"");
//			Log.e("init_data_HS_ImgName"+i,hs.get("ImgName")+"");
			arraylist.add(hs);	
		}
	}
	
	
	public void init()
	{
		
		//設定全畫面
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

		mPageVp = (ViewPager) this.findViewById(R.id.viewpager);
		
		for(int i = 0 ; i < DateProcess.GetOverDayOfMonth(month_index,today) ; i ++)
		{
			
			MyFragment mMyFragment = new MyFragment(mPageVp,R.layout.activity_page2,month_index,i,arraylist.get(i));
			mFragmentList.add(mMyFragment);
		}
		mFragmentAdapter = new FragmentAdapter(this.getSupportFragmentManager(), mFragmentList);
		mPageVp.setAdapter(mFragmentAdapter);
		mPageVp.setOnPageChangeListener(new OnPageChangeListener() {
			
			/*
			 * state 滑動狀態有三種 (0,1,2) 
			 * 1=> 正在滑動
			 * 2=> 滑動完畢
			 * 0=> 什麼都沒做
			 */
			@Override
			public void onPageSelected(int state) {
				
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				int pos = mPageVp.getCurrentItem();
				spin_date.setSelection(pos);
			}
		});
	}


	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		mPageVp.setCurrentItem(position);
		//Toast.makeText(this, date_array[position], Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	}
	
	
}
