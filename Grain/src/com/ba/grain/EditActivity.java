package com.ba.grain;

import java.util.Map;

import util.FoodType;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class EditActivity extends Activity {
	ListItem item;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_edit);
		ImageView img=(ImageView) this.findViewById(R.id.picture);
		TextView foodType=(TextView) this.findViewById(R.id.tv_foodtype);
		TextView isUpload=(TextView) this.findViewById(R.id.tv_isupload);
		
		EditText buyPrice=(EditText) this.findViewById(R.id.tv_buy_price);
		EditText tradePrice=(EditText) this.findViewById(R.id.tv_trade_price);
		EditText daywholesalePrice=(EditText) this.findViewById(R.id.tv_day_wholesale_price);
		EditText weekWholesalePrice=(EditText) this.findViewById(R.id.tv_week_wholesale_price);
		
		TextView collectStation=(TextView) this.findViewById(R.id.tv_collect_station);
		TextView grade=(TextView) this.findViewById(R.id.tv_grade);
		TextView collectDateTime=(TextView) this.findViewById(R.id.tv_collect_date_time);
		Intent intent=this.getIntent();
		String key=intent.getStringExtra("key");
		SharedPreferences sp = getSharedPreferences("records", MODE_PRIVATE);
		Map<String, ?> map = sp.getAll();
		String value=sp.getString(key, null);
		item = ListItem.fromString(value.toString());
		item.key = key;
		if(item.picture.equals("null") || item.picture.equals("") || item.picture==null){
			img.setImageResource(R.drawable.nophoto);
			
		}else{
			 //  ContentResolver cr = this.getContentResolver();  
				
			 //  Uri uri=Uri.parse(item.picture);
			Bitmap bm = BitmapFactory.decodeFile(item.picture);
			if(bm==null){
				img.setImageResource(R.drawable.nophoto);
			}else{
				img.setImageBitmap(bm);
			}
			
		}
		FoodType ft=item.getFood();
		foodType.setText(ft!=null?ft.name:"未知粮品");
		isUpload.setText(item.isUploaded()? "已上报":"未上报");
		
		buyPrice.setText(String.format("%s",String.format("%1$.2f", item.buyPrice)));
		tradePrice.setText(String.format("%s", String.format("%1$.2f", item.tradePrice)));
		daywholesalePrice.setText(String.format("%s",String.format("%1$.2f", item.dayRetailPrice)));
		weekWholesalePrice.setText(String.format("%s",String.format("%1$.2f", item.weekRetailPrice)));
		
		collectStation.setText(String.format("%s",item.site_name ));
		grade.setText(String.format("%s",item.grade_name ));
		collectDateTime.setText(item.mSaveDateTime);
		
		
		EditText buynumber=(EditText) this.findViewById(R.id.buy_number);
		EditText tradenumber=(EditText) this.findViewById(R.id.trade_number);
		EditText dayretail_number=(EditText) this.findViewById(R.id.day_retail_number);
		EditText weekretail_number=(EditText) this.findViewById(R.id.week_retail_number);
		
		buynumber.setText(""+item.buyNumber);
		tradenumber.setText(""+item.tradeNumber);
		dayretail_number.setText(""+item.dayRetailNumber);
		weekretail_number.setText(""+item.weekRetailNumber);
		
		
		EditText quality = (EditText) findViewById(R.id.et_quality);
		quality.setText(item.s_remark);
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
}

