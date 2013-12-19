package com.ba.grain;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import util.FoodType;
import util.Grade;
import util.Site;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class Query extends Activity implements OnItemSelectedListener{
	Spinner cs,type,level,isReport;
	Button query,buttonFrom,buttonTo;
	Builder builder;
		DatePicker datePicker;
	   int mYear,mYear1;
	   int mMonth,mMonth1;
	   int mDay1;
	   int mDay;
	   int mHour=23,mHour1=0;
	   int mMinute=59,mMinute1=0;
	   int mSecond=59,mSecond1=0;
	   TextView tv;
	   TimePicker tp;
	 //  DatePicker dp1;
	  // DatePicker dp2;
	   SimpleDateFormat sdf;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.query);
		sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	    Calendar c=Calendar.getInstance();
	    mYear=c.get(Calendar.YEAR);
	    mMonth=c.get(Calendar.MONTH);
	    mDay=c.get(Calendar.DAY_OF_MONTH);
	    
	    Date date1=new Date( c.getTimeInMillis()-7*24*60*60*1000);
	    c.setTime(date1);
	    mYear1=c.get(Calendar.YEAR);
	    mMonth1=c.get(Calendar.MONTH);
	    mDay1=c.get(Calendar.DAY_OF_MONTH);
	   
	    buttonFrom=(Button) this.findViewById(R.id.btn_from_datetime);
	    buttonFrom.setText(updateDisplayCN(mYear1, mMonth1,mDay1, mHour1, mMinute1, mSecond1));
	    buttonFrom.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				changeDateButtonText(buttonFrom);
				
			}
	    	
	    });
	    buttonTo=(Button) this.findViewById(R.id.btn_to_datetime);
	    buttonTo.setText(updateDisplayCN(mYear, mMonth,mDay, mHour, mMinute, mSecond));
	    buttonTo.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				changeDateButtonText(buttonTo);
				
			}
	    	
	    });
	    
	    
		cs=(Spinner) this.findViewById(R.id.sp_collection_station);
		type=(Spinner) this.findViewById(R.id.sp_type);
		level=(Spinner) this.findViewById(R.id.sp_level);
		query=(Button) this.findViewById(R.id.btn_query);
		List<Site> sites=new ArrayList<Site>();
		Site site=new Site();
		site.id="00";
		site.name="全选";
		sites.add(site);
		sites.addAll(G.sSites);
		ArrayAdapter<Site> adapter = new ArrayAdapter<Site>(this,android.R.layout.simple_spinner_item,sites);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		cs.setAdapter(adapter);
		cs.setOnItemSelectedListener(this);
		type.setOnItemSelectedListener(this);
		onItemSelected(cs, null, 0, 0);
		level.setOnItemSelectedListener(this);
		onItemSelected(type, null, 0, 0);
		isReport=(Spinner) this.findViewById(R.id.sp_isReport);
		List<Report> list =new ArrayList<Report>();
		list.add(new Report("00","全选"));
		list.add(new Report("1","已上报的"));
		list.add(new Report("2","未上报的"));
		ArrayAdapter<Report> isReportAdapter = new ArrayAdapter<Report>(this,android.R.layout.simple_spinner_item,list);
		isReportAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		isReport.setAdapter(isReportAdapter);
		isReport.setSelection(2);
		isReport.setOnItemSelectedListener(this);
		query.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				Site site = (Site) cs.getSelectedItem();
				FoodType foodType=(FoodType)type.getSelectedItem();
				Grade grade=(Grade) level.getSelectedItem();
				Report report=(Report) isReport.getSelectedItem();
				Intent intent=new Intent(Query.this, ReportActivity.class);
				String from=updateDisplay(mYear1, mMonth1, mDay1, mHour1, mMinute1, mSecond1);
				intent.putExtra("from",from);
				String to=updateDisplay(mYear, mMonth, mDay, mHour, mMinute, mSecond);
				intent.putExtra("to",to );
				intent.putExtra("siteid",site.id );
				intent.putExtra("foodtypeid",foodType.id );
				intent.putExtra("gradeid",grade.id );
				intent.putExtra("reportid",report.id );
				System.out.println("..."+from+"-"+to);
				setResult(RESULT_OK, intent);
				finish();
				
			}
			
		});
		
		
		
		
	}
	public boolean onCreateOptionsMenu(Menu menu) {
		this.getMenuInflater().inflate(R.menu.back_main_activity_menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getTitle().equals("返回")) {
			 finish();
		}
		return super.onOptionsItemSelected(item);
	}

	public void onItemSelected(AdapterView<?> av, View v, int position,
			long id) {
		Site site = (Site) cs.getSelectedItem();
		if(av==cs){
			if (site == null) {
				onNothingSelected(av);
				return;
			}
			List<FoodType> foodTypes=new ArrayList<FoodType>();
			FoodType foodType=new FoodType();
			foodType.id="00";
			foodType.name="全选";
			foodTypes.add(foodType);
			foodTypes.addAll(site.foods);
			ArrayAdapter<FoodType> adapter = new ArrayAdapter<FoodType>(this,android.R.layout.simple_spinner_item, foodTypes);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			type.setAdapter(adapter);
			//Toast.makeText(getApplicationContext(), "..."+av.getItemAtPosition(position)+":"+id+"555"+site.id+site.name, Toast.LENGTH_LONG).show();
		}else if(av==type){
			FoodType type = (FoodType) av.getSelectedItem();
			if (type == null) {
				onNothingSelected(av);
				return;
			}
			List<Grade> grades=new ArrayList<Grade>();
			Grade grade=new Grade();
			grade.id="00";
			grade.name="全选";
			grades.add(grade);
			grades.addAll(type.grades);
			ArrayAdapter<Grade> adapter = new ArrayAdapter<Grade>(this,android.R.layout.simple_spinner_item, grades);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			level.setAdapter(adapter);
			
		}else if(av==isReport){
			//Toast.makeText(getApplicationContext(), "position="+position, Toast.LENGTH_LONG).show();

		}
		//Toast.makeText(getApplicationContext(), ".rrr.."+av.getItemAtPosition(position)+":"+id+"555"+site.id+site.name, Toast.LENGTH_LONG).show();
		
	
	}
	public void onNothingSelected(AdapterView<?> av) {
		//Toast.makeText(getApplicationContext(), "11111111111"+av.getCount(), Toast.LENGTH_LONG).show();
		if (av == cs) {
			ArrayAdapter<FoodType> adapter = new ArrayAdapter<FoodType>(this,
					android.R.layout.simple_spinner_item, new FoodType[0]);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			type.setAdapter(adapter);
		} else if (type == av) {}
			ArrayAdapter<Grade> adapter = new ArrayAdapter<Grade>(this,
					android.R.layout.simple_spinner_item, new Grade[0]);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			level.setAdapter(adapter);
		
		
	}
	
	  private static String updateDisplayCN(int year,int month,int day,int hour,int minute,int second)

	  {
		  StringBuilder sb=new StringBuilder().append(year).append("年")
	                         .append(format(month + 1)).append("月")
	                         .append(format(day)).append("日");

		  return sb.toString();
	  }
	  private static String updateDisplay(int year,int month,int day,int hour,int minute,int second)

	  {
		  StringBuilder sb=new StringBuilder().append(year).append("-")
	                         .append(format(month + 1)).append("-")
	                         .append(format(day)).append(" ")
	                         .append(format(hour)).append(":")
	                         .append(format(minute)).append(":")
	                         .append(format(second));

		  return sb.toString();
	  }
	  private static String updateDisplay(int year,int month,int day)

	  {
		  StringBuilder sb=new StringBuilder().append(year).append("-")
	                         .append(format(month + 1)).append("-")
	                         .append(format(day));

		  return sb.toString();
	  }

	  /*日期时间显示两位数的方法*/

	  private static String format(int x)

	  {
	    String s=""+x;
	    if(s.length()==1) s="0"+s;
	    return s;

	  }
	  	class Report{
		 public String id;
		  public String name;
		  public Report(String id,String name){
			  this.id=id;
			  this.name=name;
		  }
			public String toString() {
				return name;
			}

	  }
	  
		public static String selectDate(DatePicker datePicker){//选择日期
			int year=datePicker.getYear();
			int month=datePicker.getMonth();
			int date=datePicker.getDayOfMonth();
		    return 	updateDisplayCN(year, month, date,0,0,0);
		}
	  	
	  	
	  	private void changeDateButtonText(final Button button){
		   Calendar c=Calendar.getInstance();
		    int year=c.get(Calendar.YEAR);
		    int month=c.get(Calendar.MONTH);
		    int day=c.get(Calendar.DAY_OF_MONTH);
			if(button.getId()==R.id.btn_from_datetime){
				year=mYear1;
				month=mMonth1;
				day=mDay1;
				
			}else if(button.getId()==R.id.btn_to_datetime){
				year=mYear;
				month=mMonth;
				day=mDay;
				
			}   
		    builder=new Builder(this);
			LayoutInflater factory = LayoutInflater.from(this);
			final View textEntryView = factory.inflate(R.layout.builder_datepicker_view, null);
			datePicker=(DatePicker) textEntryView.findViewById(R.id.datePicker1);
			datePicker.init(year,month,day,new DatePicker.OnDateChangedListener(){
			      public void onDateChanged(DatePicker view,int year,
			                          int monthOfYear,int dayOfMonth)

			      {
			    	  
			    	  
			      }

			    });
			builder.setView(textEntryView);
			builder.setPositiveButton("设置", new AlertDialog.OnClickListener(){
					public void onClick(DialogInterface dialog, int which) {
						button.setText(selectDate(datePicker));
						if(button.getId()==R.id.btn_from_datetime){
						mYear1=datePicker.getYear();
						mMonth1=datePicker.getMonth();
						mDay1=datePicker.getDayOfMonth();
							
						}else if(button.getId()==R.id.btn_to_datetime){
							mYear=datePicker.getYear();
							mMonth=datePicker.getMonth();
							mDay=datePicker.getDayOfMonth();
							
						} 
						
						
					}

			 });
			builder.setNegativeButton("关闭", new AlertDialog.OnClickListener(){
				public void onClick(DialogInterface dialog, int which) {
					Toast.makeText(getApplicationContext(), "关闭", Toast.LENGTH_SHORT).show();
					return;
					
				}
				 
			 });
			builder.create().show();
			
		}
	
}
