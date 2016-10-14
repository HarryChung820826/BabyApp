package com.example.babyapp;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class BabyAppService extends Service{

	public static final String TAG = "BabyAppService";  
	int sleep_time = 20*60*1000; //20����
	public static final int Notification_start = 0 ;
	
	Timer timer = new Timer(true);
	Timer timer_porcess = new Timer(true);
	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg)
		{
			Bundle bd = msg.getData();
			switch(msg.what)
			{
				case Notification_start:
					showNotification(bd.getString("Date"));
					break;
			}
		}
	};
	
	@Override  
    public void onCreate() {  
        super.onCreate(); 
        Log.e("BabyAppService","start");
        timer.schedule(new MyTimerTask(), 5000, sleep_time);
    }
	
	@Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        timer.schedule(new MyTimerTask(), 5000, sleep_time);
    }
  
	public class MyTimerTask extends TimerTask
	{
		public void run()
		{
			Log.e("MyTimerTask","Process");
			if((!GlobalVariable.ConnectServer) & DateProcess.checkServiceTime())
			{
				timer_porcess = new Timer(true);
				timer_porcess.schedule(new MyProcessTimerTask(), 500, 10000);
			}else{
//				sendHandler("MyTimerTask�϶�");
			}
		}
	}
	
	public class MyProcessTimerTask extends TimerTask
	{
		public void run()
		{
			Log.e("MyProcessTimerTask","Process");
			//�P�_ ��Ʈw���O�_�w�g�� ���骺����
			//�ثe�S���P���A�����q �åB �O�b�]�w���q�ɶ� �϶��� ����u�@ 
			Date now = new Date();
			if(!new MyDB(getApplicationContext()).chekcdata_IFexist(now)){
				
				if(ProcessWork(now))
				{
					sendHandler(DateProcess.GetDateToString(now));
					timer_porcess.cancel();
				}
			}else{
//				sendHandler("MyProcessTimerTask�϶�");
			}
		}
	}
	
	
	public void sendHandler(String date_str)
	{
		Message msg = new Message();
		Bundle bd = new Bundle();
		bd.putString("Date", date_str);
		msg.setData(bd);
		msg.what=Notification_start;
		handler.sendMessage(msg);
	}
	
	//��� ���A�� �q���T�� ����榡 "yyyy/MM/dd"
	public void showNotification(String date_str)
	{
		NotificationManager barManager= (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

		Intent intent = new Intent(getApplicationContext(),MainActivity.class);

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		String ImgName = DateProcess.GetDateToImgName(DateProcess.GetStringToDate(date_str));
		
		Notification.Builder notification_builder = new Notification.Builder(this)
		.setAutoCancel(true)
		.setContentTitle(date_str+" ���w ~  ")
		.setContentText("���w ~ ")
		.setContentIntent(contentIntent)
		.setSmallIcon(R.drawable.ic_launcher)
		.setLargeIcon(new Utils().GetBitmapFromSDCard(ImgName))
		.setWhen(System.currentTimeMillis())
		.setOngoing(true);
		final Notification barMsg = notification_builder.getNotification();
		barMsg.defaults = Notification.DEFAULT_ALL;
		barManager.notify(0, barMsg);
	}
	
    @Override  
    public int onStartCommand(Intent intent, int flags, int startId) { 
        return super.onStartCommand(intent, flags, startId);  
    }  
      
    @Override  
    public void onDestroy() {  
        super.onDestroy();  
    }  
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}


	
	public boolean ProcessWork(Date now)
	{
		//1.���� �V���A�� ���  ��� ���e �B �Ӥ� 
		//2.�x�s ���e �� ��Ʈw 
		//3.�x�s �Ӥ��� BabyApp ��Ƨ���
		
		//write_searchData �d�߷�Ѫ����e
		//
		GlobalVariable.ConnectServer =true;
		
		boolean saveImg_status=false; //�x�s���ɬO�_���\
		int result_insert_code = 0; //�s�W���A 1=> ���\, 0=>���@�����  , -1=>���W�L�@�����
		String[] result = null;
		result = ParseJsonStr(new WriteToServer().write_searchData(DateProcess.GetDateToString(now)));
		if(result!=null){
			result_insert_code = new MyDB(getApplicationContext()).insert_data(result[0], result[1]); 
		}
		Log.e("search_date",DateProcess.GetDateToString(now).toString());
		Bitmap bmp = new WriteToServer().write_searchDataIMG(DateProcess.GetDateToString(now).toString(),768);
		if(bmp!=null)
		{
			Log.e("bmp","not null");
			//3.�x�s �Ӥ��� BabyApp ��Ƨ���
			saveImg_status = new Utils().SaveBitmapToSDCard(bmp,DateProcess.GetDateToImgName(now));
		}else{
			Log.e("bmp","is null");
		}
		
		if(saveImg_status & (result_insert_code==1))
		{
			String server_response = new WriteToServer().write_CheckCodeData(DateProcess.GetDateToString(now));
			Log.e("server_response",server_response+"");
			GlobalVariable.ConnectServer = false;
			return true;	
		}else
		{
			GlobalVariable.ConnectServer = false;
			Log.e("�s�W��ƪ��A",result_insert_code+"");
			Log.e("�x�s�Ӥ�����",saveImg_status+"");
			return false;
		}
	}
	
	//�ѪR WriteToServer().write_searchData �^�Ǫ�Json�r�ꤺ�e
	public String[] ParseJsonStr(String json_str)
	{
		String[] result = new String[2];
		try {
			JSONObject mJSONObject = new JSONObject(json_str);
			result[0] = mJSONObject.getString("Date");
			Log.e("ParseJsonStr_Date",result[0]);
			result[1] = mJSONObject.getString("Content");
			Log.e("ParseJsonStr_Content",result[1]);
			return result;
		} catch (JSONException e) {
			return null;
		}
	}

}
