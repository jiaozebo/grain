package com.ba.grain;

import java.io.File;
import java.io.IOException;

import org.w3c.dom.Node;

import util.RequestResponse;
import util.XMLParser;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditExportActivity extends EditActivity implements OnClickListener {
	Builder builder;
	Button shangbao,delete;
	EditText filename;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		shangbao=(Button) this.findViewById(R.id.btn_shangbao);
		shangbao.setText("导出该项");
		shangbao.setOnClickListener(this);
		delete=(Button) this.findViewById(R.id.btn_delete);
		delete.setOnClickListener(this);
	}
	public void onClick(View v) {
		if(v.getId()==R.id.btn_shangbao){
			 builder = new Builder(EditExportActivity.this);
			 builder.setTitle("请输入导出文件名：");
			 LayoutInflater factory = LayoutInflater.from(this);
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
		}else if(v.getId()==R.id.btn_delete){
			doDelete(item);
		}
		
	}
	void export(String str) {
		File dir = CollectActivity.makeRootDir();
		if(str==null || str.trim().equals("")){
			str="default";
		}
	//	File file = new File(dir, String.format("%s.xml", G.sUser.loginName));
		File file = new File(dir, String.format("%s.xml", str));
		try {
			file.createNewFile();

			XMLParser parser = new XMLParser();
			parser.setEncoder(XMLParser.UTF_8);
			Node root = parser.addTag("Records", "LoginName",
					G.sUser.loginName, "CNName", G.sUser.cnName);
			parser.add_tag_parent(root, "Record", "Value",
					item.toString());
			String val = parser.toString();
			RequestResponse.save2File(parser.toString(), file.getPath(), false);
			Toast.makeText(this, String.format("该记录已成功导出至%s", file.getPath()),
					Toast.LENGTH_LONG).show();
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
						for(int i=0;i<items.length;i++){
							editor.remove(items[i].key);
						}
						editor.commit();
						Toast.makeText(getApplicationContext(), "成功删除该记录", Toast.LENGTH_SHORT).show();
						finish();
					}
									
				});
				
			}
		}.start();
		
		
	}

}
