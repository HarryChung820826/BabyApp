package com.example.babyapp;


import java.util.HashMap;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.babyapp.getBitmap.ImageCallback;

public class MyFragment extends Fragment{
	int layout = R.layout.activity_page2;
	int month_index = 0;
	int dayOfMonth = 0 ;
	HashMap<String,String> hs_data ;
	TextView txt_title,txt_day,txt_content;
	ImageView imageTitle,image_inside;
	ViewPager viewpager;
	
	final int ShowProgress = 0;
	final int ShowResult = 1;
	ProgressDialog progress ;
	
	public Handler handler = new Handler(){
		
		@Override
		public void handleMessage(Message msg)
		{
			Bundle bd = msg.getData();
			
			switch(msg.what)
			{
			case ShowResult:
				boolean result = bd.getBoolean("result");
				if(result)
				{
					reset_data();
					progress.dismiss();
					getAlertDialog("更新結果","更新成功").show();
				}else{
					progress.dismiss();
					getAlertDialog("更新結果","更新失敗").show();
				}
				break;
			case ShowProgress:
				boolean status = bd.getBoolean("status");
				if(status)
				{
					progress.setTitle("連線伺服器 ");
					progress.show();
				}
				break;
			}
		}
	};
	MyFragment()
	{
		
	}
	MyFragment(ViewPager mviewpager,int layout_id,int month_index,int dayOfMonth,HashMap<String,String> hs)
	{
		this.month_index = month_index;
		this.dayOfMonth = dayOfMonth;
		this.layout = layout_id;
		this.hs_data = hs;
		this.viewpager = mviewpager;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater , ViewGroup container,Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		
		final String date_str = DateProcess.GetDate(month_index, dayOfMonth);
		
		View view = inflater.inflate(layout, container,false);
		
		progress  = new ProgressDialog(getActivity());
		
		txt_title = (TextView) view.findViewById(R.id.txt_title);
		txt_day = (TextView) view.findViewById(R.id.txt_day);
		txt_content = (TextView) view.findViewById(R.id.txt_content);
		txt_content.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
		
		imageTitle = (ImageView) view.findViewById(R.id.imageTitle);
		image_inside = (ImageView) view.findViewById(R.id.image_inside);
		imageTitle.setOnClickListener(new OnClickListener() {
			//點擊 Title 圖片 從server 更新 這個畫面的 資料 
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						Message msg_toMain = new Message();
						Bundle bd_toMain = new Bundle();
						bd_toMain.putBoolean("status", true);
						msg_toMain.setData(bd_toMain);
						msg_toMain.what=ShowProgress;
						handler.sendMessage(msg_toMain);
						
						boolean result = new WriteToServer().ProcessWork(DateProcess.GetStringToDate(date_str));
						
						Message msg = new Message();
						Bundle bd = new Bundle();
						bd.putBoolean("result", result);
						bd.putString("date_str", date_str);
						bd.putBoolean("status", false);
						msg.setData(bd);
						msg.what=ShowResult;
						handler.sendMessage(msg);
						Log.e("result",Boolean.toString(result));
					}
				}).start();
			}
		});
		
		imageTitle.setImageBitmap(getBitmap.GetBitmap(GlobalVariable.MonthImg[month_index], getActivity(), imageTitle.getWidth()));
//		image_inside.setImageBitmap(getBitmap.GetBitmap(R.drawable.twelve, getActivity(), image_inside.getWidth()));
		
		
		
		
		txt_title.setText("第 "+(month_index+1)+" 個月");
		txt_day.setText("第 "+(dayOfMonth+1)+" 天 "+date_str);
		
		if(hs_data.containsKey("Date"))
		{
			txt_content.setText(hs_data.get("Content"));
			ImgSet(hs_data.get("ImgName"),image_inside);
		}else{
			String msg ="資料庫中無內容--以下原因影響 : \n ";
			msg += "Honey對寶貝思念滿滿 Honey家電腦無法承載Honey的思念, 導致 與寶貝手機 沒有傳輸資料成功,";
			msg += "寶貝上班要加油唷~ Honey也會加油 ~ Honey愛你~";
			txt_content.setText(msg);
			image_inside.setImageBitmap(getBitmap.GetBitmap(R.drawable.noimg, getActivity(), GlobalVariable.gWidth *80/100));
		}
		
		return view;
	}
	
	public void reset_data(){
		String date_str = DateProcess.GetDate(month_index, dayOfMonth);
		String search_str = DateProcess.GetDateToImgName(DateProcess.GetStringToDate(date_str));
		HashMap<String,String> hs = new MyDB(getContext()).get_data(search_str);
		Log.e("init_data_HS_Date",hs.get("Date")+"");
		Log.e("init_data_HS_Content",hs.get("Content")+"");
		Log.e("init_data_HS_ImgName",hs.get("ImgName")+"");
		
		if(hs.containsKey("Date"))
		{
			txt_content.setText(hs.get("Content"));
			ImgSet(hs.get("ImgName"),image_inside);
		}
	}
	
	
	public void ImgSet(String ImgName,ImageView imgview)
	{
		
		imgview.setTag(ImgName);
		getBitmap getbitmap = new getBitmap(getActivity());
		Bitmap cache = getbitmap.loadBitmap(ImgName,
				GlobalVariable.gWidth *80/100, new ImageCallback() {
					
					@Override
					public void imageCallback(Bitmap imageBitmap, String imageFile) {
						// 利用檔案名稱找尋當前mHolder.icon
	                    ImageView imageViewByTag = (ImageView) viewpager.findViewWithTag(imageFile);  
	                    if (imageViewByTag != null) {  
	                        if(imageBitmap != null)
	                            imageViewByTag.setImageBitmap(imageBitmap);  
	                    } 
					}
				});
		
		if(cache != null)
			imgview.setImageBitmap(cache);
        else
        	imgview.setImageBitmap(getBitmap.GetBitmap(R.drawable.wait, getActivity(), GlobalVariable.gWidth *80/100)); 

		
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
	}	
	
	AlertDialog getAlertDialog(String title,String message){
        //產生一個Builder物件
        Builder builder = new AlertDialog.Builder(getContext());
        //設定Dialog的標題
        builder.setTitle(title);
        //設定Dialog的內容
        builder.setMessage(message);
        //設定Positive按鈕資料
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //按下按鈕時顯示快顯
            }
        });
        //利用Builder物件建立AlertDialog
        return builder.create();
    }
}
