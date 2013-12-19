package com.ba.grain;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;

import util.Message;
import util.XMLParser;

import android.R.color;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class IMSActivity extends ListActivity implements OnItemClickListener,OnClickListener{
	public static final String TAG = "RecordActivity";
	protected OnItemLongClickListener mOnItemLongClickListener;
	public ActionMode mActionMode;
	IMSAdapter dua;
	Button delete;
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.msg_record);
		dua = new IMSAdapter(this, getListView());
		setListAdapter(dua);
		if (dua.isEmpty()) {
			getListView().setVisibility(View.GONE);
		}
		SharedPreferences pref = getSharedPreferences(LoginActivity.PREF_MESSAGE, MODE_PRIVATE);
		pref.registerOnSharedPreferenceChangeListener(dua);
		getListView().setOnItemClickListener(this);
		delete=(Button) this.findViewById(R.id.btn_delete);
		delete.setOnClickListener(this);
	}

	public static String NAME_RECORDS = "records";
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getTitle().equals("返回")) {
			 finish();
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.getMenuInflater().inflate(R.menu.back_main_activity_menu, menu);
		return true;
	}
	@Override
	protected void onDestroy() {
		SharedPreferences pref = getSharedPreferences(LoginActivity.PREF_MESSAGE, MODE_PRIVATE);
		IMSAdapter dua = (IMSAdapter) getListAdapter();
		pref.unregisterOnSharedPreferenceChangeListener(dua);
		LoginActivity.sShowNotify = true;
		super.onDestroy();
	}

	public void readMessage(int position){
		int temp=0;
		List<Message> items = new ArrayList<Message>();
		Message li = (Message) getListView().getItemAtPosition(position);
		li.isRead=1;
		items.add(li);

		SharedPreferences pref = getSharedPreferences(LoginActivity.PREF_MESSAGE,Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		for(Message message:items){
			XMLParser parser = new XMLParser();
			Node imsNode = parser.addTag("Msg", "c", message.content, "s",
					String.valueOf(message.isRead),
					"sender",message.sender
					);
			String val = XMLParser.node2string(imsNode);
			editor.putString(message.sendTime, val);
			temp++;
			
		}
		
		editor.commit();
		IMSAdapter adapter = (IMSAdapter) getListAdapter();
		adapter.notifyDataSetChanged();
		Toast.makeText(getApplicationContext(), "成功读取"+temp+"条记录!", Toast.LENGTH_SHORT).show();
	}
	@SuppressLint("NewApi")
	public void deleteMessage(int position){

		List<Message> items = new ArrayList<Message>();
		Message li = (Message) getListView().getItemAtPosition(position);
		items.add(li);
		SharedPreferences pref = getSharedPreferences(LoginActivity.PREF_MESSAGE,
				Context.MODE_PRIVATE);
		LoginActivity.removeMessageFromLocal(items, pref);
		IMSAdapter adapter = (IMSAdapter) getListAdapter();
		adapter.notifyDataSetChanged();
		Toast.makeText(getApplicationContext(), "删除成功!", Toast.LENGTH_SHORT).show();

	}
	public void deleteMessage(){
		int count=0;
		List<Message> items = new ArrayList<Message>();
		items.addAll(IMSAdapter.mItems);
		SharedPreferences pref = getSharedPreferences(LoginActivity.PREF_MESSAGE,
				Context.MODE_PRIVATE);
		for(Message msg:items){
			if(msg.isSelect){
				List<Message> item =new ArrayList<Message>();
				item.add(msg);
				LoginActivity.removeMessageFromLocal(item, pref);
				count++;
			}
		}
		IMSAdapter adapter = (IMSAdapter) getListAdapter();
		adapter.notifyDataSetChanged();
		Toast.makeText(getApplicationContext(), "成功删除"+count+"条记录!", Toast.LENGTH_SHORT).show();

	}
	public void onItemClick(AdapterView<?> av, View view, final int position, long id) {
		Message message=(Message) dua.getItem(position);
		Builder builder;
		builder=new Builder(this);
		builder.setTitle("发件人【"+message.sender+"】【"+(message.isRead==1?"已读":"未读")+"】");	
		 LayoutInflater factory = LayoutInflater.from(this);
		 final View textEntryView = factory.inflate(R.layout.ims_item1, null);
		 builder.setView(textEntryView);
		 TextView content= (TextView) textEntryView.findViewById(R.id.tv_ims_content);
		 content.setText("【内容】"+message.content);
		 TextView time= (TextView) textEntryView.findViewById(R.id.tv_ims_time);
		 time.setText("【发信时间】："+message.sendTime);
		 builder.setNegativeButton("关闭", new AlertDialog.OnClickListener(){
			public void onClick(DialogInterface dialog, int which) {
				readMessage(position);
				
			}
			 
		 });
		builder.create().show();
		
	}
	public void onClick(View v) {
		
		if(v.getId()==R.id.btn_delete){
			deleteMessage();
		}
		
	}
}
