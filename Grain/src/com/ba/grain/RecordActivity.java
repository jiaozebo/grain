package com.ba.grain;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class RecordActivity extends ListActivity implements OnClickListener {
	public static final String TAG = "NewRecordActivity";
	static List<ListItem> list;
	static SparseBooleanArray isSelected;
	ListView listView;
	static MyBaseAdapter dua;
	SimpleDateFormat sdf;
	Intent intent;
	Button btnLeft, btnRight, selectAll, shangbao;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.newrecord);
		sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		dua = new MyBaseAdapter(this);
		// list=dua.list;
		// System.out.println(intent.getStringExtra("from")+"-from-ro-"+intent.getStringExtra("to"));
		if (intent.getStringExtra("siteid") == null) {
			addLocalRecords(dua);
		} else {
			addLocalRecords(dua, intent.getStringExtra("siteid"), intent.getStringExtra("from"),
					intent.getStringExtra("to"), intent.getStringExtra("foodtypeid"),
					intent.getStringExtra("gradeid"), intent.getStringExtra("reportid"));
		}
		setListAdapter(dua);
		shangbao = (Button) this.findViewById(R.id.btn_shangbao);
		listView = (ListView) this.findViewById(android.R.id.list);
		btnLeft = (Button) this.findViewById(R.id.btnLeft);
		btnRight = (Button) this.findViewById(R.id.btnRight);
		btnLeft.setOnClickListener(this);
		btnRight.setOnClickListener(this);
		checkButton();
		selectAll = (Button) this.findViewById(R.id.btn_select_all);
		selectAll.setOnClickListener(this);
		Toast.makeText(getApplication(), "查询到" + dua.getAllCount() + "条符合条件的记录!",
				Toast.LENGTH_SHORT).show();
	}

	private void rightView() {
		dua.index++;
		dua.notifyDataSetChanged();
		checkButton();
		selectAllControl1();
	}

	@Override
	protected void onResume() {
		checkButton();
		super.onResume();
	}

	private void leftView() {
		dua.index--;
		dua.notifyDataSetChanged();
		checkButton();
		selectAllControl1();
	}

	void checkButton() {
		if (dua.index <= 0) {
			btnLeft.setEnabled(false);
		} else {
			btnLeft.setEnabled(true);
		}
		if (dua.list.size() - dua.index * dua.VIEW_COUNT <= dua.VIEW_COUNT) {
			btnRight.setEnabled(false);
		} else {
			btnRight.setEnabled(true);
		}
	}

	public static String NAME_RECORDS = "records";

	public void addLocalRecords(MyBaseAdapter dua) {// 添加所有数据
		SharedPreferences sp = getSharedPreferences(NAME_RECORDS, MODE_PRIVATE);
		Map<String, ?> map = sp.getAll();
		list = new ArrayList<ListItem>();
		for (String key : map.keySet()) {
			Object value = map.get(key);
			ListItem li = ListItem.fromString(value.toString());
			li.key = key;
			list.add(li);
		}
		Collections.sort(list);
		for (ListItem li : list) {
			dua.add(li);
		}

	}

	public void addLocalRecords(MyBaseAdapter dua, String siteid, String from, String to,
			String foodtypeid, String gradeid, String reportid) {// 根据时间，采集站，粮品，等级，是否上报添加

		SharedPreferences sp = getSharedPreferences(NAME_RECORDS, MODE_PRIVATE);
		Map<String, ?> map = sp.getAll();
		list = new ArrayList<ListItem>();
		for (String key : map.keySet()) {
			Object value = map.get(key);
			ListItem li = ListItem.fromString(value.toString());
			li.key = key;
			list.add(li);
		}
		Collections.sort(list);
		for (ListItem li : list) {
			if (addBySiteId(li, siteid)) {
				if (addByFoodTypeId(li, foodtypeid)) {
					if (addByGradeId(li, gradeid)) {
						if (addByTime(li, from, to)) {
							if (addByIsReport(li, reportid)) {
								dua.add(li);
							}
						}
					}

				}
			}
		}
	}

	public boolean addBySiteId(ListItem li, String id) {
		if (id.equals("00") || id.equals(li.site_id)) {
			return true;
		}
		return false;

	}

	public boolean addByFoodTypeId(ListItem li, String id) {
		if (id.equals("00") || id.equals(li.foodType_id)) {
			return true;
		}
		return false;
	}

	public boolean addByGradeId(ListItem li, String id) {
		if (id.equals("00") || id.equals(li.grade_id)) {
			return true;
		}
		return false;
	}

	public boolean addByTime(ListItem li, String from, String to) {
		if (from.equals("00") || to.equals("00")) {
			return true;
		}
		long dateFrom = 0, dateTo = 0, dtCreate = 0;
		try {
			dateFrom = sdf.parse(from).getTime();
			dateTo = sdf.parse(to).getTime();
			dtCreate = sdf.parse(li.s_dtCreate).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (dtCreate >= dateFrom && dtCreate < dateTo) {
			return true;
		}
		return false;
	}

	public boolean addByIsReport(ListItem li, String id) {
		if (id.equals("00")) {
			return true;
		} else if (id.equals("1")) {
			if (li.isUploaded()) {
				return true;
			}
		} else if (id.equals("2")) {
			if (li.isUploaded()) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnLeft:
			leftView();
			break;
		case R.id.btnRight:
			rightView();
			break;
		case R.id.btn_select_all:
			selectAllControl();
			break;
		}

	}

	public void selectAllControl() {
		if (selectAll.getText().equals("全选")) {
			dua.selectPageAll(true);
			selectAll.setText("反选");
		} else if (selectAll.getText().equals("反选")) {
			dua.selectPageAll(false);
			selectAll.setText("全选");
		}
	}

	public void selectAllControl1() {
		selectAll.setText("全选");
	}

}
