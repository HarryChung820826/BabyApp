package com.example.babyapp;

import java.util.Date;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.babyapp.getBitmap.ImageCallback;

public class MainActivity extends Activity implements OnClickListener{

	
	ScrollView mScrollView;
	LinearLayout mLinearLayout,mLinearLayout_interval,outLinearLayout;
	ImageView mImageView;
	TextView txt_Title,txt_interval;
	Date today = new Date();
	static Context context ;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		GlobalVariable.interval_month = DateProcess.GetMonthInterval();
		
		context = this;
		
		init_environment();
		
		int i = DateProcess.GetOverMonth(today);
		Log.e("Month_Over",i+"");
		
		init();
		init_Compoment();
		
		
		new MyDB(this).getAllData();
	}
	
	public void onResume()
	{
		super.onResume();
		//判斷 Service 是否還存在
		if(!IsBabyAppServiceRunning(BabyAppService.class)){
			Intent server = new Intent(this,BabyAppService.class);
			startService(server);
		}
	}
	
	//初始化 環境  1.建立資料庫 2.判斷服務 3.判斷資料夾
	public void init_environment()
	{
		//建立資料庫
		new MyDB(this);
		
		//判斷 Service 是否還存在
		if(!IsBabyAppServiceRunning(BabyAppService.class)){
			Intent server = new Intent(this,BabyAppService.class);
			startService(server);
		}
		
		//判斷 BabyApp 資料夾 是否存在
		new Utils().CheckDir();
	}
	
	//判斷服務 是否還存在
	public boolean IsBabyAppServiceRunning(Class<?> serviceClass)
	{
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for(RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
		{
			if (serviceClass.getName().equals(service.service.getClassName())) {
	            return true;
	        }
		}
		return false;
	}
	
	
	public void init()
	{
		//設定全畫面
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//取得螢幕 寬 、高 、dpi
		DisplayMetrics mDisplayMetrics = this.getResources().getDisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
		GlobalVariable.gHeight = mDisplayMetrics.heightPixels;
		GlobalVariable.gWidth = mDisplayMetrics.widthPixels;
		GlobalVariable.gDpi = mDisplayMetrics.density;
	}
	
	public void init_Compoment()
	{
		
		String[] interval_info ;
		
		mScrollView = (ScrollView)findViewById(R.id.scroll_view);
		mScrollView.setBackgroundColor(getResources().getColor(R.color.pink));
		
		outLinearLayout = new LinearLayout(this);
		LinearLayout.LayoutParams outLinearLayout_params = new LinearLayout.LayoutParams(
				GlobalVariable.gWidth,
				GlobalVariable.gHeight);
		
		outLinearLayout.setLayoutParams(outLinearLayout_params);
		outLinearLayout.setOrientation(LinearLayout.VERTICAL);
		
		
		for(int i = 0 ; i < DateProcess.GetOverMonth(today)+1 ; i++){
			mLinearLayout = new LinearLayout(this);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					GlobalVariable.gHeight / 2 );
			mLinearLayout.setLayoutParams(params);
			mLinearLayout.setOrientation(LinearLayout.VERTICAL);
			
			mLinearLayout_interval = new LinearLayout(this);
			params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			mLinearLayout_interval.setLayoutParams(params);
			mLinearLayout_interval.setOrientation(LinearLayout.HORIZONTAL);
			
			txt_Title = new TextView(this);
			txt_Title.setText("第 "+(i+1)+" 個月");
			txt_Title.setTextSize(TypedValue.COMPLEX_UNIT_SP,24);
			
			interval_info = GlobalVariable.interval_month.get(i);
			
			txt_interval = new TextView(this);
			txt_interval.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
			txt_interval.setText(interval_info[0]+" ~ "+interval_info[1]);
			
			mLinearLayout_interval.addView(txt_Title);
			mLinearLayout_interval.addView(txt_interval);
			//Title + IntervalInfo _ Layout End

			mImageView = new ImageView(this);
			params = new LinearLayout.LayoutParams(
					GlobalVariable.gWidth *80/100,
					GlobalVariable.gHeight / 2 - txt_Title.getHeight());
			params.gravity = Gravity.CENTER_HORIZONTAL;
			mImageView.setLayoutParams(params);
			ImgSet(i,mImageView);
			
			mLinearLayout.addView(mLinearLayout_interval);
			mLinearLayout.addView(mImageView);
			outLinearLayout.addView(mLinearLayout);
		}
		mScrollView.addView(outLinearLayout);
	}

	public void ImgSet(int i,ImageView imgview)
	{
		
		imgview.setTag(Integer.toString(i));
		getBitmap getbitmap = new getBitmap(this);
		Bitmap cache = getbitmap.loadBitmap(i,
				GlobalVariable.gWidth *80/100, new ImageCallback() {
					
					@Override
					public void imageCallback(Bitmap imageBitmap, String imageFile) {
						// 利用檔案名稱找尋當前mHolder.icon
	                    ImageView imageViewByTag = (ImageView) mScrollView.findViewWithTag(imageFile);  
	                    if (imageViewByTag != null) {  
	                        if(imageBitmap != null)
	                            imageViewByTag.setImageBitmap(imageBitmap);  
	                    } 
					}
				});
		
		if(cache != null)
			imgview.setImageBitmap(cache);
        else
        	imgview.setImageBitmap(getBitmap.GetBitmap(R.drawable.wait, this, GlobalVariable.gWidth *80/100)); 
		
		
        imgview.setOnClickListener(this);
		
	}
	
	@Override
	public void onClick(View v) {
		Intent intent = new Intent(MainActivity.this,ViewPager_Test.class);
		intent.putExtra("Index", v.getTag().toString());
		startActivity(intent);
	}

}
