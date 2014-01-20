package com.ba.grain;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;

import util.FoodType;
import util.Grade;
import util.Site;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CollectActivity extends Activity implements OnItemSelectedListener, OnClickListener {
	private static final int REQUEST_IMAGE = 0x1000;
	/**
	 * 收购价
	 */
	private EditText mSGPrice;
	/**
	 * 批发价
	 */
	private EditText mPFPrice;
	/**
	 * 零售价
	 */
	private EditText mLSPrice;
	/**
	 * 收购数量
	 */
	private EditText mSGNumber;
	/**
	 * 批发数量
	 */
	private EditText mPFNumber;
	private TextView mCollector, mCompany;
	private Spinner mFoodType, mGrade, mCollectionStation;
	private Button mButtonSave, mButtonReport, mButtonPicReport, mExporer;
	public ActionMode mActionMode;
	private ImageView mPrePhoto;
	private EditText filePath;
	private EditText mQuality;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.data_collection);
		initViews();
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

	private void initViews() {
		mCompany = (TextView) findViewById(R.id.company);
		mCompany.setText(String.valueOf(G.sUser.department_id));
		mSGPrice = (EditText) findViewById(R.id.buy_price);
		mPFPrice = (EditText) findViewById(R.id.trade_price);
		mLSPrice = (EditText) findViewById(R.id.day_retail_price);

		TextWatcher watcher = new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				boolean empty = TextUtils.isEmpty(s.toString());
				mSGNumber.setEnabled(!empty);
				if (empty) {
					mSGNumber.setText("");
				}
			}
		};

		TextWatcher watcher1 = new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				boolean empty = TextUtils.isEmpty(s.toString());
				mPFNumber.setEnabled(!empty);
				if (empty) {
					mPFNumber.setText("");
				}
			}
		};
		mSGPrice.addTextChangedListener(watcher);
		mPFPrice.addTextChangedListener(watcher1);

		mSGNumber = (EditText) findViewById(R.id.buy_number);
		mSGNumber.setEnabled(false);
		mPFNumber = (EditText) findViewById(R.id.trade_number);
		mPFNumber.setEnabled(false);
		mCollectionStation = (Spinner) findViewById(R.id.sp_collection_station);
		ArrayAdapter<?> adapter = new ArrayAdapter<Site>(this,
				android.R.layout.simple_spinner_item, G.sSites);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mCollectionStation.setAdapter(adapter);
		mCollectionStation.setOnItemSelectedListener(this);

		mCollector = (TextView) findViewById(R.id.collector);
		mFoodType = (Spinner) findViewById(R.id.sp_type);
		mFoodType.setOnItemSelectedListener(this);
		onItemSelected(mCollectionStation, null, 0, 0);

		mGrade = (Spinner) findViewById(R.id.sp_level);
		onItemSelected(mFoodType, null, 0, 0);
		mCollector.setText(PreferenceManager.getDefaultSharedPreferences(this).getString(
				LoginActivity.KEY_COLLECTOR, null));

		mButtonSave = (Button) findViewById(R.id.save);
		mButtonReport = (Button) findViewById(R.id.report);
		mButtonPicReport = (Button) findViewById(R.id.pic_report);
		mButtonSave.setOnClickListener(this);
		mButtonPicReport.setOnClickListener(this);
		mButtonReport.setOnClickListener(this);
		filePath = (EditText) this.findViewById(R.id.et_filepath);
		mExporer = (Button) this.findViewById(R.id.btn_explorer);
		mExporer.setOnClickListener(this);
		mPrePhoto = (ImageView) findViewById(R.id.pre_photo);
		mPrePhoto.setImageResource(R.drawable.nophoto);

		mQuality = (EditText) findViewById(R.id.et_quality);
	}

	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		if (parent == mCollectionStation) {
			Site site = (Site) mCollectionStation.getSelectedItem();
			if (site == null) {
				onNothingSelected(parent);
				return;
			}
			ArrayAdapter<FoodType> adapter = new ArrayAdapter<FoodType>(this,
					android.R.layout.simple_spinner_item, site.foods);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			mFoodType.setAdapter(adapter);
		} else if (mFoodType == parent) {
			FoodType type = (FoodType) mFoodType.getSelectedItem();
			if (type == null) {
				onNothingSelected(parent);
				return;
			}
			ArrayAdapter<Grade> adapter = new ArrayAdapter<Grade>(this,
					android.R.layout.simple_spinner_item, type.grades);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			mGrade.setAdapter(adapter);

			if (type.grades.isEmpty()) {
				onNothingSelected(mGrade);
			}
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		if (parent == mCollectionStation) {
			ArrayAdapter<FoodType> adapter = new ArrayAdapter<FoodType>(this,
					android.R.layout.simple_spinner_item, new FoodType[0]);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			mFoodType.setAdapter(adapter);
		} else if (mFoodType == parent) {
			ArrayAdapter<Grade> adapter = new ArrayAdapter<Grade>(this,
					android.R.layout.simple_spinner_item, new Grade[0]);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			mGrade.setAdapter(adapter);
		} else if (mGrade == parent) {
			;
		}
		// 什么也没选中，那么不能上报~！
	}

	@Override
	public void onClick(View v) {
		if (v == mButtonReport) {
			ListItem item = new ListItem();
			if (!initItem(item)) {
				return;
			}
			final String key = doSave(item);
			if (G.sSocket == null) {
				noLoginReport(key, item.picture);
			} else {
				report(key, item.picture);
			}
		} else if (v == mButtonSave) {
			ListItem item = new ListItem();
			if (!initItem(item)) {
				return;
			}
			doSave(item);
			// Log.e("item.key",item.key);
			Toast.makeText(this, "保存成功。", Toast.LENGTH_SHORT).show();
		} else if (mButtonPicReport == v) {
			// ListItem item = new ListItem();
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			File dir = makeRootDir();// nmt/sdcard/Grain/
			String name = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date(System
					.currentTimeMillis()));
			name = String.format("%s_%s", RecordActivity.NAME_RECORDS, name); // (record,...)
			try {
				mFileUpload = File.createTempFile(name, ".jpg", dir);
			} catch (IOException e) {
				e.printStackTrace();
			}
			uri = Uri.fromFile(mFileUpload);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			intent.putExtra("NAME", name);
			intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
			startActivityForResult(intent, REQUEST_IMAGE);

		} else if (v.getId() == R.id.btn_explorer) {
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(intent, 1);
		}
	}

	public void test1(Uri uri) {
		if (uri.getScheme().equals("file")) {
			String path = uri.getEncodedPath();
			if (path != null) {
				path = Uri.decode(path);
				ContentResolver cr = this.getContentResolver();
				StringBuffer buff = new StringBuffer();
				buff.append("(").append(Images.ImageColumns.DATA).append("=")
						.append("'" + path + "'").append(")");
				Cursor cur = cr.query(Images.Media.EXTERNAL_CONTENT_URI,
						new String[] { Images.ImageColumns._ID }, buff.toString(), null, null);
				int index = 0;
				for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
					index = cur.getColumnIndex(Images.ImageColumns._ID);
					index = cur.getInt(index);
				}
				if (index == 0) {

				} else {
					Uri uri_temp = Uri.parse("content://media/external/images/media/" + index);
					Log.d("TAG", "uri_temp is " + uri_temp);
					if (uri_temp != null) {
						uri = uri_temp;
					}

				}
			}

		} else if (uri.getScheme().equals("content")) {

		}
	}

	public String test2(Uri uri) {
		String picPath = uri.getEncodedPath().split("/images/media/")[1];
		Uri mUri = Uri.parse("content://media/external/images/media");
		Cursor cursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null,
				null, MediaStore.Images.Media.DEFAULT_SORT_ORDER);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			String data = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
			if (picPath.equals(data)) {
				return cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));

			}
			cursor.moveToNext();
		}
		return null;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && requestCode == 1) {// 本地图片浏览后回显
			Uri uri = data.getData();
			String path = test2(uri);
			Bitmap bitmap = BitmapFactory.decodeFile(path);
			filePath.setText(path);
			if (bitmap != null) {
				mPrePhoto.setImageBitmap(bitmap);
			} else {
				mPrePhoto.setImageResource(R.drawable.nophoto);
			}

		}

		if (resultCode == RESULT_OK && REQUEST_IMAGE == requestCode) {// 拍照后返回
			filePath.setText(mFileUpload.getAbsolutePath());
			Bitmap bm = BitmapFactory.decodeFile(mFileUpload.getPath());
			if (bm == null) {
				mPrePhoto.setImageResource(R.drawable.nophoto);
			} else {
				mPrePhoto.setImageBitmap(bm);
			}
		}
		if (resultCode == RESULT_CANCELED && REQUEST_IMAGE == requestCode) {// 取消拍照
			filePath.setText(null);
			if (mFileUpload != null) {
				mFileUpload.delete();
			}
		}

	}

	// 把该工作转给ReportActivity
	private void report(String key, String filePath) {
		Intent intent = new Intent(this, ReportActivity.class);
		intent.putExtra(ReportActivity.KEY_REPORT_ITEM_KEY, key);
		intent.putExtra(ReportActivity.KEY_REPORT_FILE_PATH, filePath);
		startActivity(intent);
	}

	// 把该工作转给MainActivity
	private void noLoginReport(String key, String path) {
		Intent intent = new Intent();
		intent.putExtra(ReportActivity.KEY_REPORT_ITEM_KEY, key);
		intent.putExtra(ReportActivity.KEY_REPORT_FILE_PATH, path);
		setResult(RESULT_OK, intent);
		finish();
	}

	public static void doSave(Context context, ListItem... items) {
		SharedPreferences sp = context.getSharedPreferences(RecordActivity.NAME_RECORDS,
				MODE_PRIVATE);
		Editor editor = sp.edit();
		for (ListItem item : items) {
			item.lister_id = G.sUser.id;
			item.id = item.key;
			if (item.key == null) {
				item.key = String.valueOf(System.currentTimeMillis());
				String.format("", 0);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH.mm");
				item.mSaveDateTime = sdf.format(new java.util.Date());
				// 新增加字段s_dtCreate,相当于保存时间
				item.s_dtCreate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
						.format(new java.util.Date());
			}

			editor.putString(item.key, item.toString());
		}
		editor.commit();
	}

	private String doSave(ListItem item) {
		doSave(this, item);
		clear(mSGPrice, mPFPrice, mLSPrice, mSGNumber, mPFNumber, filePath);
		mPrePhoto.setImageResource(R.drawable.nophoto);
		return item.key;
	}

	/**
	 * 拍照上傳時的文件
	 */
	private File mFileUpload;
	private Uri uri;

	private boolean checkLegal(EditText... views) {
		for (int i = 0; i < views.length; i++) {
			EditText view = views[i];
			if (TextUtils.isEmpty(view.getText().toString())) {
				view.setError("请输入完整信息");
				view.requestFocus();
				return false;
			}
		}
		return true;
	}

	private boolean initItem(ListItem item) {
		boolean lsLegal = checkLegal(mLSPrice);
		boolean pfLegal = checkLegal(mPFPrice);
		boolean sgLegal = checkLegal(mSGPrice);
		if (!(sgLegal || pfLegal || lsLegal)) {
			return false;
		}
		mLSPrice.setError(null);
		mPFPrice.setError(null);
		mSGPrice.setError(null);
		try {
			item.buyPrice = Float.parseFloat(mSGPrice.getText().toString());
		} catch (Exception e) {
			item.buyPrice = 0;
			e.printStackTrace();
		}
		try {
			item.tradePrice = Float.parseFloat(mPFPrice.getText().toString());
		} catch (Exception e) {
			item.tradePrice = 0;
			e.printStackTrace();
		}
		try {
			item.dayRetailPrice = Float.parseFloat(mLSPrice.getText().toString());
		} catch (Exception e) {
			item.dayRetailPrice = 0;
			e.printStackTrace();
		}
		try {
			item.buyNumber = Integer.parseInt(mSGNumber.getText().toString());
		} catch (Exception e) {
			item.buyNumber = 0;
			e.printStackTrace();
		}
		try {
			item.tradeNumber = Integer.parseInt(mPFNumber.getText().toString());
		} catch (Exception e) {
			item.tradeNumber = 0;
			e.printStackTrace();
		}
		Site site = (Site) mCollectionStation.getSelectedItem();
		if (site == null) {
			mLSPrice.requestFocus();
			Toast.makeText(this, "请选择采集站。", Toast.LENGTH_SHORT).show();
			return false;
		}
		item.setSite(site);
		FoodType ft = (FoodType) mFoodType.getSelectedItem();
		if (ft == null) {
			mFoodType.requestFocus();
			Toast.makeText(this, "请选择监测品种。", Toast.LENGTH_SHORT).show();
			return false;
		}
		item.foodType_id = ft.id;
		item.footType_name = ft.name;
		item.mFoodType = ft;
		Grade gd = (Grade) mGrade.getSelectedItem();
		if (gd == null) {
			mGrade.requestFocus();
			Toast.makeText(this, "请选择等级。", Toast.LENGTH_SHORT).show();
			return false;
		}
		item.grade_id = gd.id;
		item.grade_name = gd.name;
		item.mGrade = gd;
		String str = filePath.getText().toString().trim();
		if (str.equals("") || str == null || str.equals("null")) {
			item.picture = null;
		} else {
			item.picture = filePath.getText().toString().trim();
		}
		item.s_remark = mQuality.getText().toString();
		return true;
	}

	public static void clear(EditText... views) {
		for (int i = 0; i < views.length; i++) {
			EditText view = views[i];
			view.setText("");
		}
	}

	private static final String ROOT = "Grain";

	public static File makeRootDir() {
		File dir = new File(Environment.getExternalStorageDirectory(), ROOT);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}
}
