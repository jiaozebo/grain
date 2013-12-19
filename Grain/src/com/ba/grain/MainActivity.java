package com.ba.grain;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;

import org.w3c.dom.Node;

import util.Message;
import util.RequestResponse;
import util.XMLParser;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class MainActivity extends Activity implements OnClickListener {

	private static final int REQUEST_LOGIN_CHECK_VERSION = 0x1001;
	private static final int REQUEST_LOGIN_COLLECT = 0x1002;
	public static final int REQUEST_LOGIN_REPORT = 0x1003;
	private static final int REQUEST_COLLECT_COLLECT = 0x1004;
	public static final int REQUEST_QUERY_REPORT = 0x1005;//查询-主-上报
	public static final int C = 0x1006;
	private String mReportKey;
	private String mFileUploadPath;
	public static boolean isMessage=false;
	static TextView tvIsMessage;
	private Menu mmenu;
	SharedPreferences pref;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		String loginInfo = PreferenceManager.getDefaultSharedPreferences(this).getString(
				LoginActivity.KEY_STRING_LOGIN_INFORMATION, null);
		if (loginInfo != null) {
			XMLParser.parseString(loginInfo);
		}

		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		String val = sp.getString(LoginActivity.KEY_STRING_LOGIN_INFORMATION, null);
		if (val != null) {
			Node node = XMLParser.string2Node(val);
			G.initDatasFromNode(node, null, null);
		}

		findViewById(R.id.tuichu).setOnClickListener(this);
		findViewById(R.id.caiji).setOnClickListener(this);
		findViewById(R.id.shangbao).setOnClickListener(this);
		findViewById(R.id.chaxun).setOnClickListener(this);
		findViewById(R.id.daochu).setOnClickListener(this);
		findViewById(R.id.update).setOnClickListener(this);
		findViewById(R.id.ims).setOnClickListener(this);
		tvIsMessage=(TextView) findViewById(R.id.isMessage);
		this.findViewById(R.id.help).setOnClickListener(this);
		this.findViewById(R.id.peizhi).setOnClickListener(this);
		initTitle();
		pref=getSharedPreferences(LoginActivity.PREF_MESSAGE,MODE_PRIVATE);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_activity_menu, menu);
		this.mmenu=menu;
		initTitle();
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getTitle().equals("退出系统")) {
			onBackPressed();
		}else if(item.getTitle().equals("注销")){
			logoff();
			this.startActivity(new Intent(this,LoginActivity.class));
			Toast.makeText(getApplicationContext(), "注销成功", Toast.LENGTH_LONG).show();
		}else if(item.getTitle().equals("登录")){
			this.startActivity(new Intent(this,LoginActivity.class));
		}
		return super.onOptionsItemSelected(item);
	}

	public void onClick(View v) {
		if (v.getId() == R.id.tuichu) {
			onBackPressed();
		} else if (v.getId() == R.id.caiji) {
			if (G.sSites.isEmpty()) {
				if (G.sSocket == null) {
					startActivityForResult(new Intent(this, LoginActivity.class),
							REQUEST_LOGIN_COLLECT);
				} else {
					Toast.makeText(this, "该用户未分配采集站或未查询到！", Toast.LENGTH_LONG).show();
				}
			} else {
				startActivityForResult(new Intent(this, CollectActivity.class),REQUEST_COLLECT_COLLECT);
			}
		} else if (v.getId() == R.id.shangbao) {
			if (G.sSocket == null) {
				startActivityForResult(new Intent(this, LoginActivity.class), REQUEST_LOGIN_REPORT);
			} else {
				Intent intent=new Intent(this, ReportActivity.class);
				initIntent(intent);
				startActivity(intent);
			}
		} else if (v.getId() == R.id.chaxun) {		
			startActivityForResult(new Intent(this, Query.class),REQUEST_QUERY_REPORT);
		} else if (v.getId() == R.id.daochu) {
			startActivityForResult(new Intent(this, ExportActivity.class),REQUEST_QUERY_REPORT);
		} else if (v.getId() == R.id.update) {
			if (G.sSocket == null) {
				startActivityForResult(new Intent(this, LoginActivity.class),REQUEST_LOGIN_CHECK_VERSION);
			} else {
				doUpdate();
			}
		} else if (v.getId() == R.id.ims) {
			startActivity(new Intent(this, IMSActivity.class));
		}else if(v.getId()==R.id.help){
			startActivity(new Intent(this, HelpActivity.class));
			
		}else if(v.getId()==R.id.peizhi){
			startActivity(new Intent(this, PeizhiActivity.class));
			
		}
	}
	
	public void initIntent(Intent intent){//查询出所有未上报的数据
		intent.putExtra("from","00");
		intent.putExtra("to","00" );
		intent.putExtra("siteid","00" );
		intent.putExtra("foodtypeid","00" );
		intent.putExtra("gradeid","00" );
		intent.putExtra("reportid","2" );
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_LOGIN_CHECK_VERSION && resultCode == RESULT_OK) {
			doUpdate();
		} else if (REQUEST_LOGIN_COLLECT == requestCode && resultCode == RESULT_OK) {
			startActivity(new Intent(this, CollectActivity.class));
		} else if (requestCode == REQUEST_LOGIN_REPORT && resultCode == RESULT_OK) {
			final Intent intent = new Intent(this, ReportActivity.class);
			intent.putExtra(ReportActivity.KEY_REPORT_ITEM_KEY, mReportKey);
			intent.putExtra(ReportActivity.KEY_REPORT_FILE_PATH, mFileUploadPath);
			initIntent(intent);
			mReportKey = null;
			mFileUploadPath = null;
			startActivity(intent);
		} else if (requestCode == REQUEST_COLLECT_COLLECT && resultCode == RESULT_OK) { // 未登录时，从采集界面点击上报或者有图上报，会进来
			Assert.assertNull(G.sSocket);
			mReportKey = data.getStringExtra(ReportActivity.KEY_REPORT_ITEM_KEY);
			mFileUploadPath = data.getStringExtra(ReportActivity.KEY_REPORT_FILE_PATH);
			findViewById(R.id.shangbao).performClick();
		} else if( requestCode==REQUEST_QUERY_REPORT && resultCode == RESULT_OK ){
			Intent intent=data;
			System.out.println(intent.getStringExtra("from")+"--"+intent.getStringExtra("to"));
			intent.setClass(this, ReportActivity.class);
			startActivity(intent);
		}else if(requestCode==REQUEST_QUERY_REPORT && resultCode == RESULT_OK ){
			startActivityForResult(new Intent(this, ExportActivity.class),REQUEST_QUERY_REPORT);
			
		}
	}

	private void doUpdate() {
		final ProgressDialog pd = new ProgressDialog(MainActivity.this);
		pd.setMessage("正在检查更新...");
		pd.setCancelable(false);
		pd.show();
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				pd.dismiss();
				final int result = RequestResponse.checkNewVersion(G.sSocket, G.sNewVersionApk);
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						boolean hasNewVersion = result == 0
								&& LoginActivity.checkNewVersion(MainActivity.this, null);
						if (!hasNewVersion) {
							Toast.makeText(MainActivity.this, "您的版本已经是最新的了。", Toast.LENGTH_SHORT)
									.show();
						}
						final DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								if (which == DialogInterface.BUTTON_POSITIVE) {
									final ProgressDialog pd = new ProgressDialog(MainActivity.this);
									pd.setMessage("正在下载...");
									pd.setCancelable(false);
									pd.show();
									Runnable runnable = new Runnable() {

										@Override
										public void run() {
											File root = CollectActivity.makeRootDir();
											final File newApk = new File(root, String.format(
													"Grain_%s.apk", G.sNewVersionApk.mVersion));
											final int result = RequestResponse.downLoadNewFile(
													G.sSocket, G.sNewVersionApk.mPath,
													newApk.getPath());
											runOnUiThread(new Runnable() {

												@Override
												public void run() {
													if (result == 0) {
														LoginActivity.installNewVersion(
																MainActivity.this, newApk);
													} else {
														Toast.makeText(MainActivity.this, "下载未成功",
																Toast.LENGTH_SHORT).show();
													}

												}
											});

											pd.dismiss();
										}
									};
									if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
										AsyncTask.execute(runnable);
									} else {
										new Thread(runnable).start();
									}
								}
							}
						};
						LoginActivity.checkNewVersion(MainActivity.this, listener);
					}
				});
			}
		};
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			AsyncTask.execute(runnable);
		} else {
			new Thread(runnable).start();
		}
	}

	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this).setMessage("确定退出系统吗？").setTitle(R.string.app_name)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						//finish();
						try {
							if (G.sSocket != null) {
								G.sSocket.close();
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
						G.sSocket = null;
						System.exit(0);
					}
				}).setNegativeButton("取消", null).show();
		return;
	}

	public   void isMessage(){//是否有新的消息提示
		List<Message> list=new ArrayList<Message>();
		isMessage=false;
		getMessageFromLocal(list, pref);
		for(Message msg:list){
			if(msg.isRead==0){
				isMessage=true;
				break;
			}
		}
		
		if(isMessage){
			tvIsMessage.setVisibility(View.VISIBLE);
		}else{
			tvIsMessage.setVisibility(View.GONE);
		}
	}
	public static void showMsg(){
		tvIsMessage.setVisibility(View.VISIBLE);
	}

	protected void onResume() {
		isMessage();
		initTitle();
		super.onResume();
	}
	public void initTitle(){
		if(G.sSocket!=null){
			this.setTitle("粮情系统-"+G.sUser.cnName+"已登录");
		}else{
			this.setTitle("粮情系统-未登录");	
		}
		if(mmenu!=null){
		if(G.sSocket!=null){
			mmenu.getItem(1).setTitle("注销");
		}else{
			mmenu.getItem(1).setTitle("登录");	
		}
		}
	}
	
	
	public void logoff(){//注销
		if(G.sSocket!=null){
			try {
				G.sSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		G.sSocket = null;
		
	}
	public static void getMessageFromLocal(List<Message> list, SharedPreferences pref) {
		Iterator<String> it = pref.getAll().keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			String val = pref.getString(key, null);
			Message ims = string2Message(val);
			ims.sendTime = key;
			
			if (ims != null) {
				list.add(ims);
			}
		}
	}
	public static Message string2Message(String val) {
		if (val != null) {
			Node node = XMLParser.string2Node(val);
			Message msg = new Message();
			msg.content = XMLParser.getAttrVal(node, "c", null);
			String strStatus = XMLParser.getAttrVal(node, "s", String.valueOf(0));
			msg.isRead = Integer.parseInt(strStatus);
			msg.sender=XMLParser.getAttrVal(node, "sender", String.valueOf(0));
			return msg;
		}
		return null;
	}
	
}
