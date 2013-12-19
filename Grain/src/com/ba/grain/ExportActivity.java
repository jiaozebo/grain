package com.ba.grain;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;

import util.RequestResponse;
import util.XMLParser;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

public class ExportActivity extends RecordActivity implements OnItemClickListener	{
	private Builder builder;
	private EditText filename;
	Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		intent=this.getIntent();
		super.onCreate(savedInstanceState);
		context=this;
		listView.setOnItemClickListener(this);
		shangbao.setText("导出");
		shangbao.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				if(shangbao.getText().equals("导出")){
				boolean bool=false;
				for(int i = 0; i<list.size(); i++){
					if(list.get(i).isSelected){
						bool=true;
						break;
					}
				}
				if(!bool){
					Toast.makeText(getApplicationContext(), "请最少勾选一条记录", Toast.LENGTH_SHORT).show();
					return ;
				}
				 builder = new Builder(ExportActivity.this);
				 builder.setTitle("请输入导出文件名：");
				 LayoutInflater factory = LayoutInflater.from(context);
				 final View textEntryView = factory.inflate(R.layout.filename, null);
				 builder.setView(textEntryView);
				 filename= (EditText) textEntryView.findViewById(R.id.et_filename);
				 builder.setPositiveButton("确认导出", new AlertDialog.OnClickListener(){
						public void onClick(DialogInterface dialog, int which) {
							export(filename.getText().toString().trim());
						}

				 });
				 builder.setNegativeButton("取消", new AlertDialog.OnClickListener(){
					public void onClick(DialogInterface dialog, int which) {
						Toast.makeText(getApplicationContext(), "已取消操作", Toast.LENGTH_SHORT).show();
						return;
						
					}
					 
				 });
				builder.create().show();
			}else if(shangbao.getText().equals("删除")){
				final List<ListItem> items = new ArrayList<ListItem>();
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i).isSelected) {
						items.add(list.get(i));
				
					}
				}
				if(items.size()<=0){
					Toast.makeText(getApplicationContext(), "请最少勾选一项！", Toast.LENGTH_SHORT).show();
					return;
				}else{
					builder=new Builder(ExportActivity.this);
					builder.setTitle("注意：删除后将不能恢复！");
					builder.setMessage("确定删除吗？");
					builder.setPositiveButton("确定删除", new AlertDialog.OnClickListener(){
						public void onClick(DialogInterface dialog, int which) {
							ListItem[] arr = new ListItem[items.size()];
							for(int i=0;i<arr.length;i++){
								arr[i]=items.get(i);
								
							}
							doDelete(arr);
						}

						
					});
					builder.setNegativeButton("取消", new AlertDialog.OnClickListener(){
						public void onClick(DialogInterface dialog, int which) {
							Toast.makeText(getApplicationContext(), "已取消删除操作", Toast.LENGTH_SHORT).show();
							return;	
						}
						
					});
					
				}

				builder.create().show();
				
			}
			
		}					
	  });
	}
	
	void export(String str) {
		File dir = CollectActivity.makeRootDir();
		if(str==null || str.trim().equals("")){
			str="default";
		}
		File file = new File(dir, String.format("%s.xml", str));
		try {
			file.createNewFile();

			XMLParser parser = new XMLParser();
			parser.setEncoder(XMLParser.UTF_8);
			Node root = parser.addTag("Records", "LoginName",
					G.sUser.loginName, "CNName", G.sUser.cnName);
			int temp=0;
			for (int i = 0; i < list.size(); i++) {
				if(list.get(i).isSelected){
					ListItem li=list.get(i);
					parser.add_tag_parent(root, "Record", "Value",li.toString());
					temp++;
				}
			}
			String val = parser.toString();
			Log.d(TAG, val);
			RequestResponse.save2File(parser.toString(), file.getPath(), false);
			Toast.makeText(this, String.format("记录"+temp+"条已成功导出至%s", file.getPath()),Toast.LENGTH_LONG).show();
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}
	
	
	public void doDelete(final ListItem... items){
		new Thread(){		
			public void run(){				
				runOnUiThread(new Runnable() {
					public void run() {
						SharedPreferences sp = getSharedPreferences(RecordActivity.NAME_RECORDS,MODE_PRIVATE);
						Editor editor = sp.edit();
						int count=0;
						for(int i=0;i<items.length;i++){
							editor.remove(items[i].key);
							count++;
						}
						editor.commit();
						dua.removeAll();
						if(intent.getStringExtra("siteid")==null){
							addLocalRecords(dua);;
						}else{
							addLocalRecords(dua,intent.getStringExtra("siteid"),intent.getStringExtra("from"),intent.getStringExtra("to"),intent.getStringExtra("foodtypeid"),intent.getStringExtra("gradeid"),intent.getStringExtra("reportid"));;

						}
						dua.notifyDataSetChanged();
						Toast.makeText(getApplicationContext(), "成功删除 "+count+" 条记录", Toast.LENGTH_LONG).show();
					}
									
				});
				
			}
		}.start();
		
		
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		this.getMenuInflater().inflate(R.menu.export_edit_menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getTitle().equals("返回")) {
			 finish();
		}else if(item.getTitle().equals("操作")){
			item.setTitle("取消操作");
			shangbao.setText("删除");
			for(ListItem li:list){
				if(li.isSelected){
					li.isSelected=false;
				}
			}
			dua.notifyDataSetChanged();
		}else if(item.getTitle().equals("取消操作")){
			item.setTitle("操作");
			shangbao.setText("导出");
			for(ListItem li:list){
				if(li.isSelected){
					li.isSelected=false;
				}
			}
			dua.notifyDataSetChanged();
		}
		return super.onOptionsItemSelected(item);
	}
	public void onItemClick(AdapterView<?> av, View view, int position, long id) {
		Intent intent=new Intent(this,EditExportActivity.class);
		ListItem item=(ListItem) dua.getItem(position);
		intent.putExtra("key",item.key);
		this.startActivity(intent);
	}
	protected void onResume() {
		dua.removeAll();
		if(intent.getStringExtra("siteid")==null){
			addLocalRecords(dua);;
		}else{
			addLocalRecords(dua,intent.getStringExtra("siteid"),intent.getStringExtra("from"),intent.getStringExtra("to"),intent.getStringExtra("foodtypeid"),intent.getStringExtra("gradeid"),intent.getStringExtra("reportid"));;

		}
		dua.notifyDataSetChanged();
		super.onResume();
	}
	
}
