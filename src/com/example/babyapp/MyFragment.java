package com.example.babyapp;


import java.util.HashMap;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.babyapp.getBitmap.ImageCallback;

public class MyFragment extends Fragment{
	int layout = R.layout.activity_page2;
	int month_index = 0;
	int dayOfMonth = 0 ;
	HashMap<String,String> hs_data ;
	TextView txt_title,txt_day,txt_content;
	ImageView imageTitle,image_inside;
	ViewPager viewpager;
	
	final int ShowProgress = 0;
	final int ShowResult = 1;
	ProgressDialog progress ;
	
	public Handler handler = new Handler(){
		
		@Override
		public void handleMessage(Message msg)
		{
			Bundle bd = msg.getData();
			
			switch(msg.what)
			{
			case ShowResult:
				boolean result = bd.getBoolean("result");
				if(result)
				{
					reset_data();
					progress.dismiss();
					getAlertDialog("��s���G","��s���\").show();
				}else{
					progress.dismiss();
					getAlertDialog("��s���G","��s����").show();
				}
				break;
			case ShowProgress:
				boolean status = bd.getBoolean("status");
				if(status)
				{
					progress.setTitle("�s�u���A�� ");
					progress.show();
				}
				break;
			}
		}
	};
	MyFragment()
	{
		
	}
	MyFragment(ViewPager mviewpager,int layout_id,int month_index,int dayOfMonth,HashMap<String,String> hs)
	{
		this.month_index = month_index;
		this.dayOfMonth = dayOfMonth;
		this.layout = layout_id;
		this.hs_data = hs;
		this.viewpager = mviewpager;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater , ViewGroup container,Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		
		final String date_str = DateProcess.GetDate(month_index, dayOfMonth);
		
		View view = inflater.inflate(layout, container,false);
		
		progress  = new ProgressDialog(getActivity());
		
		txt_title = (TextView) view.findViewById(R.id.txt_title);
		txt_day = (TextView) view.findViewById(R.id.txt_day);
		txt_content = (TextView) view.findViewById(R.id.txt_content);
		txt_content.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
		
		imageTitle = (ImageView) view.findViewById(R.id.imageTitle);
		image_inside = (ImageView) view.findViewById(R.id.image_inside);
		imageTitle.setOnClickListener(new OnClickListener() {
			//�I�� Title �Ϥ� �qserver ��s �o�ӵe���� ��� 
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						Message msg_toMain = new Message();
						Bundle bd_toMain = new Bundle();
						bd_toMain.putBoolean("status", true);
						msg_toMain.setData(bd_toMain);
						msg_toMain.what=ShowProgress;
						handler.sendMessage(msg_toMain);
						
						boolean result = new WriteToServer().ProcessWork(DateProcess.GetStringToDate(date_str));
						
						Message msg = new Message();
						Bundle bd = new Bundle();
						bd.putBoolean("result", result);
						bd.putString("date_str", date_str);
						bd.putBoolean("status", false);
						msg.setData(bd);
						msg.what=ShowResult;
						handler.sendMessage(msg);
						Log.e("result",Boolean.toString(result));
					}
				}).start();
			}
		});
		
		imageTitle.setImageBitmap(getBitmap.GetBitmap(GlobalVariable.MonthImg[month_index], getActivity(), imageTitle.getWidth()));
//		image_inside.setImageBitmap(getBitmap.GetBitmap(R.drawable.twelve, getActivity(), image_inside.getWidth()));
		
		
		
		
		txt_title.setText("�� "+(month_index+1)+" �Ӥ�");
		txt_day.setText("�� "+(dayOfMonth+1)+" �� "+date_str);
		
		if(hs_data.containsKey("Date"))
		{
			txt_content.setText(hs_data.get("Content"));
			ImgSet(hs_data.get("ImgName"),image_inside);
		}else{
			String msg ="��Ʈw���L���e--�H�U��]�v�T : \n ";
			msg += "Honey���_��������� Honey�a�q���L�k�Ӹ�Honey�����, �ɭP �P�_����� �S���ǿ��Ʀ��\,";
			msg += "�_���W�Z�n�[�o��~ Honey�]�|�[�o ~ Honey�R�A~";
			txt_content.setText(msg);
			image_inside.setImageBitmap(getBitmap.GetBitmap(R.drawable.noimg, getActivity(), GlobalVariable.gWidth *80/100));
		}
		
		return view;
	}
	
	public void reset_data(){
		String date_str = DateProcess.GetDate(month_index, dayOfMonth);
		String search_str = DateProcess.GetDateToImgName(DateProcess.GetStringToDate(date_str));
		HashMap<String,String> hs = new MyDB(getContext()).get_data(search_str);
		Log.e("init_data_HS_Date",hs.get("Date")+"");
		Log.e("init_data_HS_Content",hs.get("Content")+"");
		Log.e("init_data_HS_ImgName",hs.get("ImgName")+"");
		
		if(hs.containsKey("Date"))
		{
			txt_content.setText(hs.get("Content"));
			ImgSet(hs.get("ImgName"),image_inside);
		}
	}
	
	
	public void ImgSet(String ImgName,ImageView imgview)
	{
		
		imgview.setTag(ImgName);
		getBitmap getbitmap = new getBitmap(getActivity());
		Bitmap cache = getbitmap.loadBitmap(ImgName,
				GlobalVariable.gWidth *80/100, new ImageCallback() {
					
					@Override
					public void imageCallback(Bitmap imageBitmap, String imageFile) {
						// �Q���ɮצW�٧�M��emHolder.icon
	                    ImageView imageViewByTag = (ImageView) viewpager.findViewWithTag(imageFile);  
	                    if (imageViewByTag != null) {  
	                        if(imageBitmap != null)
	                            imageViewByTag.setImageBitmap(imageBitmap);  
	                    } 
					}
				});
		
		if(cache != null)
			imgview.setImageBitmap(cache);
        else
        	imgview.setImageBitmap(getBitmap.GetBitmap(R.drawable.wait, getActivity(), GlobalVariable.gWidth *80/100)); 

		
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
	}	
	
	AlertDialog getAlertDialog(String title,String message){
        //���ͤ@��Builder����
        Builder builder = new AlertDialog.Builder(getContext());
        //�]�wDialog�����D
        builder.setTitle(title);
        //�]�wDialog�����e
        builder.setMessage(message);
        //�]�wPositive���s���
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //���U���s����ܧ���
            }
        });
        //�Q��Builder����إ�AlertDialog
        return builder.create();
    }
}
