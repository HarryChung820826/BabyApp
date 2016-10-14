package com.example.babyapp;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

import android.R;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class getBitmap {

	private static String TAG = "AsyncImageFileLoader";
	private HashMap<String, SoftReference<Bitmap>> imageCache;
	private Context c;

	public getBitmap(Context context) {
		this.c = context;
		imageCache = new HashMap<String, SoftReference<Bitmap>>();
	}

	//MainActivity ���� ���J�Ϥ���
	public Bitmap loadBitmap(final int tag, final int show_width,
			final ImageCallback imageCallback) {
		final String imageFile = Integer.toString(GlobalVariable.MonthImg[tag]);
		// �p�G���Ϥ��wŪ���L���ܡA�N�|�Ȧs�bcache���A�ҥH�i�H�����qcache��Ū��
		if (imageCache.containsKey(imageFile)) {
			SoftReference<Bitmap> softReference = imageCache.get(imageFile);
			Bitmap bmp = softReference.get();
			if (bmp != null) {
				return bmp;
			}
		}
		final Handler handler = new Handler() {
			public void handleMessage(Message message) {
				imageCallback.imageCallback((Bitmap) message.obj,
						Integer.toString(tag));
			}
		};

		new Thread() {
			@Override
			public void run() {
				// sleep 500ms ��GridView�i�H����ܩҦ�item�A�_�h�|�]���}�lŪ���Ϥ��ӨϨt�Φ��L
				// �y���Ϥ�Ū��������~��ܩҦ�item
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				Bitmap bmp = loadImageFromFile(imageFile, show_width);
				imageCache.put(Integer.toString(tag),
						new SoftReference<Bitmap>(bmp));
				Message message = handler.obtainMessage(0, bmp);
				handler.sendMessage(message);
			}
		}.start();

		return null;
	}
	
	//MyFragment ���� ���J�Ϥ���
	public Bitmap loadBitmap(final String ImgName, final int show_width,
			final ImageCallback imageCallback) {
		// �p�G���Ϥ��wŪ���L���ܡA�N�|�Ȧs�bcache���A�ҥH�i�H�����qcache��Ū��
		if (imageCache.containsKey(ImgName)) {
			SoftReference<Bitmap> softReference = imageCache.get(ImgName);
			Bitmap bmp = softReference.get();
			if (bmp != null) {
				return bmp;
			}
		}
		final Handler handler = new Handler() {
			public void handleMessage(Message message) {
				imageCallback.imageCallback((Bitmap) message.obj,
						ImgName);
			}
		};

		new Thread() {
			@Override
			public void run() {

				Bitmap bmp = loadImageFromSDFile(ImgName, show_width);
				imageCache.put(ImgName,
						new SoftReference<Bitmap>(bmp));
				Message message = handler.obtainMessage(0, bmp);
				handler.sendMessage(message);
			}
		}.start();

		return null;
	}
	
	//load �O�Хd���� ����
	public Bitmap loadImageFromSDFile(String ImgName, int display_width) {
		// Log.e("loadImageFromFile..",tag);
		Bitmap bmp = GetBitmapFromSD(ImgName, c, display_width);
		if (bmp != null) {
			// Log.e("loadImageFromFile..bmp","not null");
		} else {
			// Log.e("loadImageFromFile..bmp","is null");
		}
		return bmp;
	}
	//loadResource ������
	public Bitmap loadImageFromFile(String tag, int display_width) {
		// Log.e("loadImageFromFile..",tag);
		Bitmap bmp = GetBitmap(Integer.parseInt(tag), c, display_width);
		if (bmp != null) {
			// Log.e("loadImageFromFile..bmp","not null");
		} else {
			// Log.e("loadImageFromFile..bmp","is null");
		}
		return bmp;
	}

	public interface ImageCallback {
		public void imageCallback(Bitmap imageBitmap, String imageFile);
	}

	// tag => Resource id
	public static Bitmap GetBitmap(int tag, Context c, int width) {

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true; // �ȷ|�Ǧ^ �Ϥ������e
		options.inPurgeable = true;
		options.inInputShareable = true;
		options.inSampleSize = 2;
		Bitmap bmp = BitmapFactory.decodeResource(c.getResources(), tag,
				options);
		// Bitmap bmp = BitmapFactory.decodeResource(res, res_id,options);
		int height = options.outHeight * width / options.outWidth;
		options.outWidth = width;
		options.outHeight = height;
		options.inJustDecodeBounds = false;

		return BitmapFactory.decodeResource(c.getResources(), tag, options);
	}
	
	// ImgName from sdcard
	public static Bitmap GetBitmapFromSD(String ImgName, Context c, int width) {

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true; // �ȷ|�Ǧ^ �Ϥ������e
		options.inPurgeable = true;
		options.inInputShareable = true;
		options.inSampleSize = 4;
		
		Utils utils = new Utils();
		utils.CheckDir();
		String path = utils.GetDirPosition("BabyApp");
		path += "/"+ImgName+".jpg";
		
		Bitmap bmp = BitmapFactory.decodeFile(path, options);
		// Bitmap bmp = BitmapFactory.decodeResource(res, res_id,options);
		int height = options.outHeight * width / options.outWidth;
		options.outWidth = width;
		options.outHeight = height;
		options.inJustDecodeBounds = false;

		return BitmapFactory.decodeFile(path, options);
	}
}
