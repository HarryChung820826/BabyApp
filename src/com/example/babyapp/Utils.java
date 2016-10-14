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
		options.inJustDecodeBounds=true; //�ȷ|�Ǧ^ �Ϥ������e
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
	
	//���o�qserver ������ �Ϥ�
	public static Bitmap GetBitmapFromServer(InputStream input,int width)
	{
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds=true; //�ȷ|�Ǧ^ �Ϥ������e
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
	
	//�d�� sd �d�� ��Ƨ��O�_�� �Y��� �Ϥ� ImgName=> yyyyMMdd
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
	
	//�x�s�Ϥ���sd�d�� �Y��� �Ϥ� ImgName=> yyyyMMdd
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
	
	//�P�_ ��Ƨ� �O�_�s�b
	public void CheckDir()
	{
		String path = GetDirPosition("BabyApp");
		File file = new File(path);
		if(!file.exists())
		{
			file.mkdir();
		}
	}
	
	//�P�_ sd �d���O�_�� BabyApp ��Ƨ� �S�� �h �إ߸�Ƨ� 
	//���p�S�� sd �d �x�s�b DCIM ��Ƨ���
	public String GetDirPosition(String path)
	{
		String fpath = "";
		if(CheckSDCard())
		{
			File sd = Environment.getExternalStorageDirectory();
			fpath = sd.getPath()+"/"+path;
		}else{
			//�S��sd�d �x�s�� DCIM ��Ƨ�
			File f = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            if (!f.exists())
            {
                // �p�G�S��DCIM�ؿ�, �N���sd�d
                f = Environment.getExternalStorageDirectory();
            }
            fpath = f.getPath()+"/"+path;
		}
		return fpath;
	}
	
	//�P�_ sd�d�O�_�s�b
	public boolean CheckSDCard()
	{
		return Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	}

	
	
}
