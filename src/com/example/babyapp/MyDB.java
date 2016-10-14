package com.example.babyapp;

import java.util.Date;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDB extends SQLiteOpenHelper{

	private SQLiteDatabase db;
	private final static int DB_Version = 1;
	private final static String DB_Name = "BabyApp_DB";
	
	public MyDB(Context c){
		super(c, DB_Name, null, DB_Version);
		db = this.getWritableDatabase();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		final String create_BabyApp_DB = "CREATE TABLE IF NOT EXISTS "+ DB_Name +"("
				+"_id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+"Date TEXT, "
				+"Content TEXT,"
				+"ImgName TEXT"
				+");";
		
		db.execSQL(create_BabyApp_DB);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
	//新增資料 (yyyyMMdd)
	public int insert_data(String date_str,String content)
	{
		
		//先查詢 資料庫內有無 此日期資料
		Cursor c =  select_data(date_str);
		int result_code = 0;
		Log.e("Cursor",c.getCount()+"");
		if(c!=null)
		{
			if(c.getCount()==1){
				result_code = 0; //有一筆資料
			}
			if(c.getCount()>1){
				result_code = -1; //有超過一筆資料
			}
			if(c.getCount()==0)
			{
				String ImgName = date_str; //圖片檔案名稱 第幾個月 + 第幾天
				ContentValues values = new ContentValues();
				values.put("Date", date_str);
				values.put("Content", content);
				values.put("ImgName", ImgName);
				db.insert(DB_Name, null, values);
				result_code = 1; //新增成功
			}
		}else{
			String ImgName = date_str; //圖片檔案名稱 第幾個月 + 第幾天
			ContentValues values = new ContentValues();
			values.put("Date", date_str);
			values.put("Content", content);
			values.put("ImgName", ImgName);
			db.insert(DB_Name, null, values);
			result_code = 1; //新增成功
		}
		return result_code;
	}
	
	//查詢特定日期的內容 取得Cursor (yyyyMMdd)
	public Cursor select_data(String date)
	{
		SQLiteDatabase db = getReadableDatabase();
		String sql = "SELECT * FROM "+DB_Name+" WHERE Date="+date;
		Cursor c = db.rawQuery(sql, null);
		return c;
	}
	//判斷 資料庫內是否已存在此資料
	public boolean chekcdata_IFexist(Date date)
	{
		String date_str = DateProcess.GetDateToImgName(date);
		Cursor c = select_data(date_str);
		Log.e("chekcdata_IFexist",c.getCount()+"");
		if(c.getCount()>=1)
		{
			return true;
		}else{
			return false;
		}
	}
	
	//查詢特定日期的內容 取得HashMap<String,String>
	public HashMap<String,String> get_data(String date)
	{
		Cursor c =  select_data(date);
		if(c.getCount()==1)
		{
			c.moveToFirst();
			HashMap<String,String> hs = new HashMap<String,String>();
			hs.put("Date", c.getString(c.getColumnIndex("Date")));
			hs.put("Content", c.getString(c.getColumnIndex("Content")));
			hs.put("ImgName", c.getString(c.getColumnIndex("ImgName")));
			return hs;
		}else{
			return new HashMap<String,String>();
		}
	}
	
	public void getAllData()
	{
		SQLiteDatabase db = getReadableDatabase();
		String sql = "SELECT * FROM "+DB_Name;
		Cursor c = db.rawQuery(sql, null);
		c.moveToFirst();
		while(c.moveToNext())
		{
			Log.e("Position:",c.getPosition()+"");
			Log.e("Date_"+c.getPosition(),c.getColumnIndex("Date")+"");
			Log.e("Content_"+c.getPosition(),c.getColumnIndex("Content")+"");
		}
	}
}
