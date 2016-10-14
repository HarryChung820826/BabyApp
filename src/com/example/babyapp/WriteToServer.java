package com.example.babyapp;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.util.Log;

public class WriteToServer {


	HttpClient client =new DefaultHttpClient();
	List<NameValuePair> para;
	HttpPost request_post;
	HttpEntity entity;
	StringBuilder sb = new StringBuilder();
	String server_url;
	
	public WriteToServer()
	{
		server_url = "http://harrychung.no-ip.org:8080/";
		GlobalVariable.ConnectServer = true;	
	}
	
	/*
	 * <summary> 向伺服器查詢某日期 內容 </summary>
	 * <param name=Date> 日期 格式(yyyy/MM/dd) </param>
	 * <return variable="sb"> 伺服器回傳的字串內容Json格式 </return>
	 */
	public String write_searchData(String date_str)
	{
		//透過POST 方法 今日日期 查詢 格式 (yyyy/MM/dd)
		server_url += "search";
		request_post = new HttpPost(server_url);
		
		
		para = new ArrayList<NameValuePair>();
		para.add(new BasicNameValuePair("Date", date_str));
		
		try {
			request_post.setEntity(new UrlEncodedFormEntity(para,"UTF-8"));
			entity = client.execute(request_post).getEntity();
			InputStream input = entity.getContent();
			InputStreamReader input_read = new InputStreamReader(input);
			BufferedReader buf_re = new BufferedReader(input_read);
			String temp ="";
			while((temp = buf_re.readLine()) != null )
			{
				sb.append(temp);
			}
			Log.e("write_searchData_Response :",sb.toString());
			return sb.toString();
		} catch (Exception e) {
			return "write_searchData_catch : "+e.getMessage();
		}
	}
	
	/*
	 * <summary> 向伺服器回傳 確認碼 </summary>
	 * <param name=Date></param>
	 * <return variable="sb"> 伺服器回傳的字串內容 1=>成功 , 0=> 不成功 </return>
	 */
	public String write_CheckCodeData(String date_str)
	{
		//透過POST 方法  傳送 確認 已接收成功
		server_url += "check";
		request_post = new HttpPost(server_url);
		
		
		para = new ArrayList<NameValuePair>();
		para.add(new BasicNameValuePair("CheckCode", "ok"));
		para.add(new BasicNameValuePair("Date", date_str));
		
		try {
			request_post.setEntity(new UrlEncodedFormEntity(para,"UTF-8"));
			entity = client.execute(request_post).getEntity();
			InputStream input = entity.getContent();
			InputStreamReader input_read = new InputStreamReader(input);
			BufferedReader buf_re = new BufferedReader(input_read);
			String temp ="";
			while((temp = buf_re.readLine()) != null )
			{
				sb.append(temp);
			}
			Log.e("write_CheckCodeData_Response :",sb.toString());
			return sb.toString();
		} catch (Exception e) {
			return "write_CheckCodeData_catch : "+e.getMessage();
		}
	}
	
	/*
	 * <summary> 向伺服器查詢某日期 圖片 </summary>
	 * <param name=Date> 日期 格式(yyyy/MM/dd) </param>
	 * <return variable="sb"> 伺服器回傳的圖片內容 Bitmap 格式 </return>
	 */
	public Bitmap write_searchDataIMG(String date_str,int width)
	{
		//透過POST 方法 今日日期 查詢 格式 (yyyy/MM/dd)
		server_url += "showImg";
		request_post = new HttpPost(server_url);
		
		
		para = new ArrayList<NameValuePair>();
		para.add(new BasicNameValuePair("Date", date_str));
		
		try {
			request_post.setEntity(new UrlEncodedFormEntity(para,"UTF-8"));
			entity = client.execute(request_post).getEntity();
			InputStream input = entity.getContent();
//			InputStreamReader input_read = new InputStreamReader(input);
			Bitmap bp = Utils.GetBitmapFromServer(input, width);
			if(bp==null)
			{
				Log.e("write_searchDataIMG_bp","is null");	
			}else{
				Log.e("write_searchDataIMG_bp","is not null");	
			}
			return bp;
		} catch (Exception e) {
			Log.e("write_searchDataIMG",e.getMessage()+"null");
			return null;
		}
	}
	
	public boolean ProcessWork(Date now)
	{
		//1.執行 向伺服器 抓取  當天 內容 、 照片 
		//2.儲存 內容 至 資料庫 
		//3.儲存 照片至 BabyApp 資料夾中
		
		//write_searchData 查詢當天的內容
		//
		GlobalVariable.ConnectServer =true;
		
		boolean saveImg_status=false; //儲存圖檔是否成功
		int result_insert_code = 0; //新增狀態 1=> 成功, 0=>有一筆資料  , -1=>有超過一筆資料
		String[] result = null;
		result = ParseJsonStr(write_searchData(DateProcess.GetDateToString(now)));
		if(result!=null){
			result_insert_code = new MyDB(MainActivity.context).insert_data(result[0], result[1]); 
		}
		Log.e("search_date",DateProcess.GetDateToString(now).toString());
		Bitmap bmp = new WriteToServer().write_searchDataIMG(DateProcess.GetDateToString(now).toString(),768);
		if(bmp!=null)
		{
			Log.e("bmp","not null");
			//3.儲存 照片至 BabyApp 資料夾中
			Log.e("bmp_ImgName",DateProcess.GetDateToImgName(now));
			saveImg_status = new Utils().SaveBitmapToSDCard(bmp,DateProcess.GetDateToImgName(now));
		}else{
			Log.e("bmp","is null");
		}
		
		if(saveImg_status & (result_insert_code==1))
		{
			String server_response = write_CheckCodeData(DateProcess.GetDateToString(now));
			Log.e("server_response",server_response+"");
			GlobalVariable.ConnectServer = false;
			return true;	
		}else
		{
			GlobalVariable.ConnectServer = false;
			Log.e("新增資料狀態",result_insert_code+"");
			Log.e("儲存照片失敗",saveImg_status+"");
			return false;
		}
		
	}
	
	//解析 WriteToServer().write_searchData 回傳的Json字串內容
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
