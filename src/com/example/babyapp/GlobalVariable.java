package com.example.babyapp;

import java.util.ArrayList;

public class GlobalVariable {
	
	public static boolean ConnectServer = false; //�P�_�ثe�O�_ ���P ���A�����q��
	public static int gHeight;
	public static int gWidth;
	public static float gDpi;
	public static int[] MonthImg = new int[]
			{R.drawable.one,R.drawable.two,R.drawable.three,
			R.drawable.four,R.drawable.five,R.drawable.six,
			R.drawable.seven,R.drawable.eight,R.drawable.nine,
			R.drawable.ten,R.drawable.eleven,R.drawable.twelve,
			};
	
	public static ArrayList<String[]> interval_month = DateProcess.GetMonthInterval();
}
