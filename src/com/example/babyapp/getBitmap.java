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

	//MainActivity 頁面 載入圖片用
	public Bitmap loadBitmap(final int tag, final int show_width,
			final ImageCallback imageCallback) {
		final String imageFile = Integer.toString(GlobalVariable.MonthImg[tag]);
		// 如果此圖片已讀取過的話，將會暫存在cache中，所以可以直接從cache中讀取
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
				// sleep 500ms 讓GridView可以先顯示所有item，否則會因為開始讀取圖片而使系統忙碌
				// 造成圖片讀取完畢後才顯示所有item
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
	
	//MyFragment 頁面 載入圖片用
	public Bitmap loadBitmap(final String ImgName, final int show_width,
			final ImageCallback imageCallback) {
		// 如果此圖片已讀取過的話，將會暫存在cache中，所以可以直接從cache中讀取
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
	
	//load 記憶卡內的 圖檔
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
	//loadResource 的圖檔
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
		options.inJustDecodeBounds = true; // 僅會傳回 圖片的長寬
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
		options.inJustDecodeBounds = true; // 僅會傳回 圖片的長寬
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
