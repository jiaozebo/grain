package com.ba.grain;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class EditReportActivity extends EditActivity implements OnClickListener{
	Button shangbao,delete;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		shangbao=(Button) this.findViewById(R.id.btn_shangbao);
		shangbao.setOnClickListener(this);
		delete=(Button) this.findViewById(R.id.btn_delete);
		delete.setOnClickListener(this);
	}
	public void onClick(View v) {
		if(v.getId()==R.id.btn_shangbao){
			doReport();
		}else if(v.getId()==R.id.btn_delete){
			doDelete(item);
		}
		
		
	}
	public void doDelete(final ListItem... items){
		setResult(RESULT_OK);
		finish();
//		new Thread(){		
//			public void run(){				
//				runOnUiThread(new Runnable() {
//					public void run() {
//						SharedPreferences sp = getSharedPreferences(RecordActivity.NAME_RECORDS,MODE_PRIVATE);
//						Editor editor = sp.edit();
//						for(int i=0;i<items.length;i++){
//							editor.remove(items[i].key);
//						}
//						editor.commit();
//						Toast.makeText(getApplicationContext(), "成功删除该记录", Toast.LENGTH_SHORT).show();
//						setResult(RESULT_OK);
//						finish();
//					}
//									
//				});
//				
//			}
//		}.start();
//		
		
	}
	public void doReport(){
		if(item.isUploaded()){
			Toast.makeText(getApplicationContext(), "该项已上报无需重复上报！", Toast.LENGTH_SHORT).show();
			return;
		}
		setResult(ReportActivity.REQUEST_REPORT_EDIT_REPORT);
		finish();
	}

}
