package com.example.babyapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class Page2 extends Activity {

	String index;
	TextView txt_title,txt_day,txt_content;
	ImageView img_title,img_inside;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_page2);
		
		init_Compoment();
		init_data();
	}
	
	public void init_data()
	{
		Intent intent = getIntent();
		index = intent.getStringExtra("Index");
		
		txt_title.setText("第 "+index+" 個月");
		txt_day.setText("第一天");
		
		img_title.setImageBitmap(Utils.GetBitmap(getResources(), R.drawable.ic_launcher, img_title.getWidth()));
		img_inside.setImageBitmap(Utils.GetBitmap(getResources(), R.drawable.ic_launcher, img_inside.getWidth()));
		
	}
	
	public void init_Compoment()
	{
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		txt_title = (TextView)findViewById(R.id.txt_title);
		txt_day = (TextView)findViewById(R.id.txt_day);
		txt_content = (TextView)findViewById(R.id.txt_content);
		img_title = (ImageView)findViewById(R.id.imageTitle);
		img_inside = (ImageView)findViewById(R.id.image_inside);
	}
}
