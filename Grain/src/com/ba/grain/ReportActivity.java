package com.ba.grain;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;

import util.RequestResponse;
import util.Site;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class ReportActivity extends RecordActivity implements
		OnItemClickListener {
	/**
	 * 要上报的一项的key
	 */
	public static final String KEY_REPORT_ITEM_KEY = "key_report_item_key";
	public static final String KEY_REPORT_FILE_PATH = "key_report_file_path";
	public static int REQUEST_REPORT_EDIT = 0x1000;
	public static int REQUEST_REPORT_EDIT_REPORT = 0x1001;
	public static int REQUEST_REPORT_LOGIN_REPORT = 0x1002;
	private ProgressDialog mDialog;
	Context context;
	private ListItem tempItem;// 临时Item

	protected void onCreate(Bundle savedInstanceState) {
		intent = getIntent();
		super.onCreate(savedInstanceState);
		context = this;
		mDialog = new ProgressDialog(this);
		mDialog.setCancelable(false);
		reportFromIntent();
		listView.setOnItemClickListener(this);
		shangbao.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (shangbao.getText().equals("上报")) {
					List<ListItem> items = new ArrayList<ListItem>();
					for (int i = 0; i < list.size(); i++) {
						if (list.get(i).isSelected && !list.get(i).isUploaded()) {
							items.add(list.get(i));
						}

					}
					final ListItem[] arr = new ListItem[items.size()];
					for (int i = 0; i < arr.length; i++) {
						arr[i] = items.get(i);
					}
					if (arr.length == 0) {
						Toast.makeText(context, "没有需要上报的数据", Toast.LENGTH_SHORT)
								.show();
						return;
					}
					if (G.sSocket == null) {
						startActivityForResult(new Intent(ReportActivity.this,
								LoginActivity.class),
								REQUEST_REPORT_LOGIN_REPORT);
						Toast.makeText(getApplicationContext(),
								"你还没登录，请先登录...", Toast.LENGTH_SHORT).show();
					} else {
						doReport(arr);
					}

				} else if (shangbao.getText().equals("删除")) {
					final List<ListItem> items = new ArrayList<ListItem>();
					for (int i = 0; i < list.size(); i++) {
						if (list.get(i).isSelected) {
							items.add(list.get(i));

						}
					}
					if (items.size() <= 0) {
						Toast.makeText(getApplicationContext(), "请最少勾选一项！",
								Toast.LENGTH_SHORT).show();
						return;
					} else {
						Builder builder = new Builder(ReportActivity.this);
						builder.setTitle("注意：删除后将不能恢复！");
						builder.setMessage("确定删除吗？");
						builder.setPositiveButton("确定删除",
								new AlertDialog.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										ListItem[] arr = new ListItem[items
												.size()];
										for (int i = 0; i < arr.length; i++) {
											arr[i] = items.get(i);

										}
										doDelete(arr);
									}

								});
						builder.setNegativeButton("取消",
								new AlertDialog.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										Toast.makeText(getApplicationContext(),
												"已取消删除操作", Toast.LENGTH_SHORT)
												.show();
										return;
									}

								});

						builder.show();
					}

				}

			}

		});

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == MainActivity.REQUEST_QUERY_REPORT
				&& resultCode == RESULT_OK) {// 跳转到查询页返回
			intent = data;
			dua.removeAll();
			if (intent.getStringExtra("siteid") == null) {
				addLocalRecords(dua);
			} else {
				addLocalRecords(dua, intent.getStringExtra("siteid"),
						intent.getStringExtra("from"),
						intent.getStringExtra("to"),
						intent.getStringExtra("foodtypeid"),
						intent.getStringExtra("gradeid"),
						intent.getStringExtra("reportid"));
			}
			dua.notifyDataSetChanged();
			// System.out.println("msg"+intent.getStringExtra("siteid"));
			// Log.e(".........", "msg"+intent.getStringExtra("siteid"));
		} else if (requestCode == REQUEST_REPORT_EDIT
				&& resultCode == RESULT_OK) {// 在详细信息点删除返回
			doDelete(tempItem);

			// Intent intent=this.getIntent();
			// dua.removeAll();
			// if(intent.getStringExtra("siteid")==null){
			// addLocalRecords(dua);;
			// }else{
			// addLocalRecords(dua,intent.getStringExtra("siteid"),intent.getStringExtra("from"),intent.getStringExtra("to"),intent.getStringExtra("foodtypeid"),intent.getStringExtra("gradeid"),intent.getStringExtra("reportid"));;
			//
			// }
			// dua.notifyDataSetChanged();
		} else if (requestCode == REQUEST_REPORT_EDIT
				&& resultCode == REQUEST_REPORT_EDIT_REPORT) {// 在详细信息点上报返回
			String filePath = tempItem.picture.equals("null")
					|| tempItem.picture.equals("") || tempItem.picture == null ? null
					: tempItem.picture;
			doReport(filePath, tempItem);
		} else if (requestCode == REQUEST_REPORT_LOGIN_REPORT
				&& resultCode == RESULT_OK) {// 跳转到登陆页面返回

		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.report, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getTitle().equals("返回")) {
			finish();
		} else if (item.getTitle().equals("查询")) {
			Intent intent = new Intent(context, Query.class);
			startActivityForResult(intent, MainActivity.REQUEST_QUERY_REPORT);
		} else if (item.getTitle().equals("操作")) {
			item.setTitle("取消操作");
			shangbao.setText("删除");
			for (ListItem li : list) {
				if (li.isSelected) {
					li.isSelected = false;
				}
			}
			dua.notifyDataSetChanged();
		} else if (item.getTitle().equals("取消操作")) {
			item.setTitle("操作");
			shangbao.setText("上报");
			for (ListItem li : list) {
				if (li.isSelected) {
					li.isSelected = false;
				}
			}
			dua.notifyDataSetChanged();
		}
		return super.onOptionsItemSelected(item);
	}

	protected void onRestart() {
		super.onRestart();
	}

	protected void onResume() {
		/*
		 * Intent intent=this.getIntent(); dua.removeAll();
		 * if(intent.getStringExtra("siteid")==null){ addLocalRecords(dua);;
		 * }else{
		 * addLocalRecords(dua,intent.getStringExtra("siteid"),intent.getStringExtra
		 * (
		 * "from"),intent.getStringExtra("to"),intent.getStringExtra("foodtypeid"
		 * )
		 * ,intent.getStringExtra("gradeid"),intent.getStringExtra("reportid"));
		 * ;
		 * 
		 * } dua.notifyDataSetChanged();
		 */
		checkButton();
		super.onResume();
	}

	private void doReport(final String filePath, final ListItem... items) {

		mDialog.setMessage("上报中...");
		mDialog.show();
		new Thread() {
			@Override
			public void run() {
				super.run();
				Map<String, Long> map = new HashMap<String, Long>();
				int temp = 0;
				Date date = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_hhmmss");
				String username = PreferenceManager
						.getDefaultSharedPreferences(ReportActivity.this)
						.getString(LoginActivity.KEY_COLLECTOR, null);
				String serinum = "";

				String[] attr = new String[] { "id", "site_id", "serinum",
						"foodType_id", "grade_id", "buyPrice", "tradePrice",
						"dayRetailPrice", "weekRetailPrice", "buyNumber",
						"tradeNumber", "dayRetailNumber", "weekRetailNumber",
						"lister_id", "state", "s_dtCreate" ,"s_remark"};
				String[][] values = new String[items.length][attr.length];
				for (int i = 0; i < items.length; i++) {
					if (!map.containsKey(items[i].site_id)) {
						map.put(items[i].site_id, date.getTime() + temp);
						temp += 1000;
					}
					serinum = sdf.format(new Date(map.get(items[i].site_id)))
							+ username;
					values[i][0] = String.valueOf(items[i].key);
					values[i][1] = String.valueOf(items[i].site_id);
					values[i][2] = serinum;
					values[i][3] = String.valueOf(items[i].foodType_id);
					values[i][4] = String.valueOf(items[i].grade_id);
					values[i][5] = String.valueOf(items[i].buyPrice);
					values[i][6] = String.valueOf(items[i].tradePrice);
					values[i][7] = String.valueOf(items[i].dayRetailPrice);
					values[i][8] = String.valueOf(items[i].weekRetailPrice);
					values[i][9] = String.valueOf(items[i].buyNumber);
					values[i][10] = String.valueOf(items[i].tradeNumber);
					values[i][11] = String.valueOf(items[i].dayRetailNumber);
					values[i][12] = String.valueOf(items[i].weekRetailNumber);
					values[i][13] = String.valueOf(items[i].lister_id);
					values[i][14] = "已报";
					values[i][15] = String.valueOf(items[i].s_dtCreate);
					values[i][16] = String.valueOf(items[i].s_remark);
				}
				final Node[] node = new Node[1];
				InputStream is = null;
				String fileName = null;
				if (filePath != null) {
					ContentResolver cr = context.getContentResolver();
					File f = new File(filePath);
					Uri uri = Uri.fromFile(f);
					fileName = f.getName();
					try {
						is = cr.openInputStream(uri);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
				final int result = RequestResponse.excuteInsert(G.sSocket,
						"tail_quotation", attr, values, fileName, is, serinum,
						node);
				final InputStream theFis = is;
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						if (null != theFis) {
							try {
								theFis.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						mDialog.dismiss();
						if (result == 0) {
							Toast.makeText(ReportActivity.this,
									String.format("成功上报了%d项记录", items.length),
									Toast.LENGTH_SHORT).show();
							for (ListItem listItem : items) {
								listItem.mUploaded = true;
							}
							CollectActivity.doSave(ReportActivity.this, items);
							MyBaseAdapter dua = (MyBaseAdapter) getListAdapter();
							dua.notifyDataSetChanged();
						} else {
							Toast.makeText(ReportActivity.this,
									String.format("%s(%d)", "上传未成功", result),
									Toast.LENGTH_SHORT).show();
						}
					}
				});
			}

		}.start();
	}

	private void doReport(final ListItem... items) {
		mDialog.setMessage("上报中...");
		mDialog.show();
		new Thread() {
			int SUCCESS = 0;
			String filePath = null;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_hhmmss");
			String username = PreferenceManager.getDefaultSharedPreferences(
					ReportActivity.this).getString(LoginActivity.KEY_COLLECTOR,
					null);

			public void run() {
				super.run();
				int i;
				Map<String, Long> map = new HashMap<String, Long>();
				int temp = 0;
				Date date = new Date();
				String serinum = "";
				String[] attr = new String[] { "id", "site_id", "serinum",
						"foodType_id", "grade_id", "buyPrice", "tradePrice",
						"dayRetailPrice", "weekRetailPrice", "buyNumber",
						"tradeNumber", "dayRetailNumber", "weekRetailNumber",
						"lister_id", "state", "s_dtCreate" ,"s_remark"};
				String[][] values = new String[items.length][attr.length];
				for (i = 0; i < items.length; i++) {
					if (!map.containsKey(items[i].site_id)) {
						map.put(items[i].site_id, date.getTime() + temp);
						temp += 1000;
					}
					serinum = sdf.format(new Date(map.get(items[i].site_id)))
							+ username;
					values[i][0] = String.valueOf(items[i].key);
					values[i][1] = String.valueOf(items[i].site_id);
					values[i][2] = serinum;
					values[i][3] = String.valueOf(items[i].foodType_id);
					values[i][4] = String.valueOf(items[i].grade_id);
					values[i][5] = String.valueOf(items[i].buyPrice);
					values[i][6] = String.valueOf(items[i].tradePrice);
					values[i][7] = String.valueOf(items[i].dayRetailPrice);
					values[i][8] = String.valueOf(items[i].weekRetailPrice);
					values[i][9] = String.valueOf(items[i].buyNumber);
					values[i][10] = String.valueOf(items[i].tradeNumber);
					values[i][11] = String.valueOf(items[i].dayRetailNumber);
					values[i][12] = String.valueOf(items[i].weekRetailNumber);
					values[i][13] = String.valueOf(items[i].lister_id);
					values[i][14] = "已报";
					values[i][15] = String.valueOf(items[i].s_dtCreate);
					values[i][16] = String.valueOf(items[i].s_remark);
					filePath = items[i].picture;
					final Node[] node = new Node[1];
					InputStream is = null;
					String fileName = null;
					if (filePath != null && !filePath.equalsIgnoreCase("null")
							&& !filePath.equalsIgnoreCase("")) {
						ContentResolver cr = context.getContentResolver();
						File f = new File(filePath);
						fileName = f.getName();
						Uri uri = Uri.fromFile(f);
						try {
							is = cr.openInputStream(uri);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
					}
					String[][] value = new String[1][attr.length];
					value[0] = values[i];
					final int result = RequestResponse.excuteInsert(G.sSocket,
							"tail_quotation", attr, value, fileName, is,
							serinum, node);
					if (result == 0) {
						items[i].mUploaded = true;
						CollectActivity.doSave(ReportActivity.this, items[i]);
						SUCCESS += 1;
					}
					final InputStream theFis = is;
					runOnUiThread(new Runnable() {
						public void run() {
							if (null != theFis) {
								try {
									theFis.close();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
							mDialog.setMessage("上报中..."
									+ String.format("总上报%d,已成功%d项",
											items.length, SUCCESS, items.length
													- SUCCESS));
							MyBaseAdapter dua = (MyBaseAdapter) getListAdapter();
							dua.notifyDataSetChanged();
						}
					});
					if (i >= items.length - 1) {
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						mDialog.dismiss();
					}

				}
			}
		}.start();
	}

	public void doDelete(final ListItem... items) {
		new Thread() {
			public void run() {
				runOnUiThread(new Runnable() {
					public void run() {
						SharedPreferences sp = getSharedPreferences(
								RecordActivity.NAME_RECORDS, MODE_PRIVATE);
						Editor editor = sp.edit();
						int count = 0;
						for (int i = 0; i < items.length; i++) {
							editor.remove(items[i].key);
							count++;
						}
						editor.commit();
						dua.removeAll();
						if (intent.getStringExtra("siteid") == null) {
							addLocalRecords(dua);
							;
						} else {
							addLocalRecords(dua,
									intent.getStringExtra("siteid"),
									intent.getStringExtra("from"),
									intent.getStringExtra("to"),
									intent.getStringExtra("foodtypeid"),
									intent.getStringExtra("gradeid"),
									intent.getStringExtra("reportid"));
							;

						}
						dua.notifyDataSetChanged();
						checkButton();
						Toast.makeText(getApplicationContext(),
								"成功删除 " + count + " 条记录", Toast.LENGTH_LONG)
								.show();
					}

				});

			}
		}.start();

	}

	public void doDelete() {
		new Thread() {
			public void run() {
				runOnUiThread(new Runnable() {
					public void run() {
						SharedPreferences sp = getSharedPreferences(
								RecordActivity.NAME_RECORDS, MODE_PRIVATE);
						Editor editor = sp.edit();
						int count = 1;
						editor.remove(tempItem.key);
						editor.commit();
						dua.list.remove(tempItem);
						dua.notifyDataSetChanged();
						checkButton();
						Toast.makeText(getApplicationContext(),
								"成功删除 " + count + " 条记录", Toast.LENGTH_LONG)
								.show();
					}

				});

			}
		}.start();

	}

	private void reportFromIntent() {// 从采集页面跳转过来的上报
		String key = getIntent().getStringExtra(KEY_REPORT_ITEM_KEY);
		if (key != null) {
			String filePath = getIntent().getStringExtra(KEY_REPORT_FILE_PATH);
			ListItem item = null;
			for (int i = 0; i < getListAdapter().getCount(); i++) {
				ListItem li = (ListItem) getListAdapter().getItem(i);
				if (key.equals(li.key)) {
					item = li;
				}
			}
			if (item == null) {
				return;
			}
			doReport(filePath, item);
		}
	}

	public void onItemClick(AdapterView<?> av, View view, int position, long id) {// 跳转到详细信息页面
		Intent intent = new Intent(this, EditReportActivity.class);
		tempItem = (ListItem) dua.getItem(position);
		intent.putExtra("key", tempItem.key);
		this.startActivityForResult(intent, REQUEST_REPORT_EDIT);
	}

}
