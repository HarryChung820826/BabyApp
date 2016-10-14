package com.example.babyapp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.util.Log;

public class DateProcess {

	private final static Date date = new Date(2015, 10, 17);

	private static Calendar today_calendar = Calendar.getInstance();
	private Date today = new Date();
	
	//�P�_ �{�b�ɶ� �O�_ �b �]�w���϶��� -- �P���A�� ��s��ƪ��϶� am8:00 ~ am9:00
	public static boolean checkServiceTime()
	{
		today_calendar = Calendar.getInstance();
		Log.e("Now_Hour",today_calendar.get(Calendar.HOUR_OF_DAY)+"");
		Log.e("Now_MINUTE",today_calendar.get(Calendar.MINUTE)+"");
		Log.e("Now_SECOND",today_calendar.get(Calendar.SECOND)+"");
		int hour = today_calendar.get(Calendar.HOUR_OF_DAY);
		int minute = today_calendar.get(Calendar.MINUTE);
		int second = today_calendar.get(Calendar.SECOND);
		
		
		if((hour>=8 & hour<12))
		{
			//�ɶ��� ���W�C�I��Q�@�I
			return true;
		}
		
		return false;
	}
	
	//���o�S�w��� �g�L�J���� �X�Ӥ�
	static int GetOverMonth(Date pareDate)
	{
		Calendar c_date = Calendar.getInstance();
		c_date.setTime(date);
		
		Calendar pare_date = Calendar.getInstance();
		pare_date.setTime(pareDate);
		
		int date_year = c_date.get(Calendar.YEAR)-1900;
		int today_year = pare_date.get(Calendar.YEAR);
		int Month=0;
		if(today_year>date_year)
		{
			Month=(12-c_date.get(Calendar.MONTH))+pare_date.get(Calendar.MONTH);
		}
		if(today_year<date_year){
			return -1;
		}
		
		if(today_year==date_year)
		{
			Month = (pare_date.get(Calendar.MONTH) - c_date.get(Calendar.MONTH));
		}
//		Log.e("GetOverMonth",Month+"");
		return Month;
	}
	
	//���o �Y�Ӥ� �g�L�X��
	public static int GetOverDayOfMonth(int month_index,Date parseDate)
	{
		Calendar c_parseDate = Calendar.getInstance();
		c_parseDate.setTime(parseDate);
		
		
		
		Calendar c_date = Calendar.getInstance();
		c_date.setTime(date);
		c_date.add(Calendar.YEAR, -1900);
		c_date.add(Calendar.MONTH, month_index); //���o�ĴX�Ӥ몺�_�l�ɶ�
		
		int OverDayOfMonth=0;
		if(GetDateToString(c_date.getTime()).equalsIgnoreCase(GetDateToString(c_parseDate.getTime()))){
			OverDayOfMonth = 1;
		}else if(c_date.before(c_parseDate))
		{
			Calendar c_nextMonth = Calendar.getInstance();
			c_nextMonth.setTime(c_date.getTime());
			Log.e("c_date.get(Calendar.MONTH)",c_date.get(Calendar.MONTH)+"");
			Log.e("c_parseDate.get(Calendar.MONTH)",c_parseDate.get(Calendar.MONTH)+"");
			if(Integer.toString(c_date.get(Calendar.MONTH)).equals(
					Integer.toString(c_parseDate.get(Calendar.MONTH)))){
				//�P�@�Ӥ�
				c_nextMonth.setTime(parseDate);
			}else{
				//���P�� �u�����U�Ӥ몺�Ĥ@��
				c_nextMonth.add(Calendar.MONTH, 1);
			}
			while(c_date.before(c_nextMonth))
			{
				c_date.add(Calendar.DAY_OF_MONTH, 1);
				//�]�j��줵��
				OverDayOfMonth++;
			}
		}else{
			OverDayOfMonth = -1;
		}
		return OverDayOfMonth;
	}
	//���o�Y�Ӥ�  �w�L������ �r�� (Spinner ��)
	static String[] GetOverDayOfMonth_DateStr(int month_index)
	{
		ArrayList<String> overdate_str = new ArrayList<String>();
		
		Calendar c_date = Calendar.getInstance();
		c_date.setTime(date);
		c_date.add(Calendar.YEAR, -1900);
		c_date.add(Calendar.MONTH, month_index); //���o�ĴX�Ӥ몺�_�l�ɶ�
		
		Log.e("GetOverDayOfMonth_DateStr_month_index", month_index+"");
		
		overdate_str.add(GetDateToString(c_date.getTime())); //�Ĥ@��
		
		Log.e("GetOverDayOfMonth_DateStr_", GetOverDayOfMonth(month_index,today_calendar.getTime())+"");
		
		for(int i = 0 ; i < GetOverDayOfMonth(month_index,today_calendar.getTime()) ; i ++)
		{
			
			if(i!=0){
				c_date.add(Calendar.DAY_OF_MONTH, 1);
				overdate_str.add(GetDateToString(c_date.getTime()));
			}
		}
		
		return overdate_str.toArray(new String[overdate_str.size()]);
	}
	
	//���o �L�X�Ӥ� �L�X�� �����T ���
	static String GetDate(int monthindex,int dayofmonth)
	{
		String date_str="";
		
		Calendar c_date = Calendar.getInstance();
		c_date.setTime(date);
		c_date.add(Calendar.YEAR, -1900);
		c_date.add(Calendar.MONTH,monthindex);
		c_date.add(Calendar.DAY_OF_MONTH,dayofmonth);
		date_str = GetDateToString(c_date.getTime());
		return date_str;
	}
	
	//���o�Y�Ӥ�� �O�b�g�L�X�Ӥ� �X��[0] => �� , [1] => ��
	static int[] GetOverMonthAndDay(String date_str)
	{
		Date theDate = GetStringToDate(date_str);
		int[] result = new int[2];
		int overMonth = GetOverMonth(theDate);
		result[0] = overMonth; //�g�L�X�Ӥ� 
		int overDayOfMonth = GetOverDayOfMonth(overMonth,theDate);
		result[1] = overDayOfMonth; //�g�L�X�� 
		return result;
	}
	
	//���o 12�몺 �϶��r��
	static ArrayList<String[]> GetMonthInterval()
	{
		ArrayList<String[]> interval_str = new ArrayList<String[]>();
		
		Calendar c_date = Calendar.getInstance();
		c_date.setTime(date);
		c_date.add(Calendar.YEAR, -1900);
		Log.e("c_date_init",GetDateToString(c_date.getTime()));
		for(int i = 0 ; i < 12 ; i++)
		{	
			String[] start_end = new String[2];
			if(i!=0){
				c_date.add(Calendar.DAY_OF_MONTH, 1);
			}
			start_end[0] = GetDateToString(c_date.getTime());
			Log.e("c_date_"+i,GetDateToString(c_date.getTime()));
			c_date.add(Calendar.MONTH, 1);
			c_date.add(Calendar.DAY_OF_MONTH, -1);
			start_end[1] = GetDateToString(c_date.getTime());
			
			interval_str.add(start_end);
		}
		
		return interval_str;
	}
	
	static String GetDateToString(Date date)
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		return format.format(date);
	}
	//�N����榡 format �� ���ɦW
	static String GetDateToImgName(Date date)
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		return format.format(date);
	}
	
	static Date GetStringToDate(String date_str)
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		try {
			return format.parse(date_str);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
