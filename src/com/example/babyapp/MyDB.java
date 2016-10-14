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
	
	//�s�W��� (yyyyMMdd)
	public int insert_data(String date_str,String content)
	{
		
		//���d�� ��Ʈw�����L ��������
		Cursor c =  select_data(date_str);
		int result_code = 0;
		Log.e("Cursor",c.getCount()+"");
		if(c!=null)
		{
			if(c.getCount()==1){
				result_code = 0; //���@�����
			}
			if(c.getCount()>1){
				result_code = -1; //���W�L�@�����
			}
			if(c.getCount()==0)
			{
				String ImgName = date_str; //�Ϥ��ɮצW�� �ĴX�Ӥ� + �ĴX��
				ContentValues values = new ContentValues();
				values.put("Date", date_str);
				values.put("Content", content);
				values.put("ImgName", ImgName);
				db.insert(DB_Name, null, values);
				result_code = 1; //�s�W���\
			}
		}else{
			String ImgName = date_str; //�Ϥ��ɮצW�� �ĴX�Ӥ� + �ĴX��
			ContentValues values = new ContentValues();
			values.put("Date", date_str);
			values.put("Content", content);
			values.put("ImgName", ImgName);
			db.insert(DB_Name, null, values);
			result_code = 1; //�s�W���\
		}
		return result_code;
	}
	
	//�d�߯S�w��������e ���oCursor (yyyyMMdd)
	public Cursor select_data(String date)
	{
		SQLiteDatabase db = getReadableDatabase();
		String sql = "SELECT * FROM "+DB_Name+" WHERE Date="+date;
		Cursor c = db.rawQuery(sql, null);
		return c;
	}
	//�P�_ ��Ʈw���O�_�w�s�b�����
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
	
	//�d�߯S�w��������e ���oHashMap<String,String>
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
