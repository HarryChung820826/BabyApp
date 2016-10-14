package com.example.babyapp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;


public class Utils {

	
	public static Bitmap GetBitmap(Resources res ,int res_id,int width)
	{
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds=true; //僅會傳回 圖片的長寬
		options.inPurgeable = true;
	    options.inInputShareable = true;
	    options.inSampleSize = 2;
		Bitmap bmp = BitmapFactory.decodeResource(res, res_id,options);
		int height = options.outHeight * width / options.outWidth;
		options.outWidth = width;
		options.outHeight = height;
		options.inJustDecodeBounds =false;
		
		return BitmapFactory.decodeResource(res, res_id,options);
	}
	
	//取得從server 接收的 圖片
	public static Bitmap GetBitmapFromServer(InputStream input,int width)
	{
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds=true; //僅會傳回 圖片的長寬
		options.inPurgeable = true;
	    options.inInputShareable = true;
	    options.inSampleSize = 2;
	    options.outWidth = width;
		int height = options.outHeight * width / options.outWidth;
		options.outHeight = height;
		options.inJustDecodeBounds =false;
		if(input==null)
		{
			Log.e("input ","is null");
		}else{
			Log.e("input ","is not null");
		}
		return BitmapFactory.decodeStream(input,null,options);
	}
	
	//查詢 sd 卡中 資料夾是否有 某日期 圖片 ImgName=> yyyyMMdd
	public Bitmap GetBitmapFromSDCard(String ImgName)
	{
		CheckDir();
		String path = GetDirPosition("BabyApp");
		path += "/"+ImgName+".jpg";
		File file = new File(path);
		if(file != null)
		{
			return BitmapFactory.decodeFile(path);
		}else{
			return null;
		}
	}
	
	//儲存圖片至sd卡中 某日期 圖片 ImgName=> yyyyMMdd
	public boolean SaveBitmapToSDCard(Bitmap bmp ,String ImgName)
	{
		CheckDir();
		String file_path = GetDirPosition("BabyApp/"+ImgName+".jpg");
		Log.e("SaveBitmapToSDCard_file_path",file_path);
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(file_path);
			if(fos!=null)
			{
				boolean c = bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
				Log.e("SaveBitmapToSDCard_c",c+"");
				fos.close();
				return true;
			}else{
				Log.e("SaveBitmapToSDCard_fos","is null");
			}
		} catch (Exception e1) {
			Log.e("SaveBitmapToSDCard",e1.getMessage());
		}
		
		return false;
	}
	
	//判斷 資料夾 是否存在
	public void CheckDir()
	{
		String path = GetDirPosition("BabyApp");
		File file = new File(path);
		if(!file.exists())
		{
			file.mkdir();
		}
	}
	
	//判斷 sd 卡中是否有 BabyApp 資料夾 沒有 則 建立資料夾 
	//假如沒有 sd 卡 儲存在 DCIM 資料夾中
	public String GetDirPosition(String path)
	{
		String fpath = "";
		if(CheckSDCard())
		{
			File sd = Environment.getExternalStorageDirectory();
			fpath = sd.getPath()+"/"+path;
		}else{
			//沒有sd卡 儲存到 DCIM 資料夾
			File f = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            if (!f.exists())
            {
                // 如果沒有DCIM目錄, 就放到sd卡
                f = Environment.getExternalStorageDirectory();
            }
            fpath = f.getPath()+"/"+path;
		}
		return fpath;
	}
	
	//判斷 sd卡是否存在
	public boolean CheckSDCard()
	{
		return Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	}

	
	
}
