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
	 * <summary> �V���A���d�߬Y��� ���e </summary>
	 * <param name=Date> ��� �榡(yyyy/MM/dd) </param>
	 * <return variable="sb"> ���A���^�Ǫ��r�ꤺ�eJson�榡 </return>
	 */
	public String write_searchData(String date_str)
	{
		//�z�LPOST ��k ������ �d�� �榡 (yyyy/MM/dd)
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
	 * <summary> �V���A���^�� �T�{�X </summary>
	 * <param name=Date></param>
	 * <return variable="sb"> ���A���^�Ǫ��r�ꤺ�e 1=>���\ , 0=> �����\ </return>
	 */
	public String write_CheckCodeData(String date_str)
	{
		//�z�LPOST ��k  �ǰe �T�{ �w�������\
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
	 * <summary> �V���A���d�߬Y��� �Ϥ� </summary>
	 * <param name=Date> ��� �榡(yyyy/MM/dd) </param>
	 * <return variable="sb"> ���A���^�Ǫ��Ϥ����e Bitmap �榡 </return>
	 */
	public Bitmap write_searchDataIMG(String date_str,int width)
	{
		//�z�LPOST ��k ������ �d�� �榡 (yyyy/MM/dd)
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
		//1.���� �V���A�� ���  ��� ���e �B �Ӥ� 
		//2.�x�s ���e �� ��Ʈw 
		//3.�x�s �Ӥ��� BabyApp ��Ƨ���
		
		//write_searchData �d�߷�Ѫ����e
		//
		GlobalVariable.ConnectServer =true;
		
		boolean saveImg_status=false; //�x�s���ɬO�_���\
		int result_insert_code = 0; //�s�W���A 1=> ���\, 0=>���@�����  , -1=>���W�L�@�����
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
			//3.�x�s �Ӥ��� BabyApp ��Ƨ���
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
