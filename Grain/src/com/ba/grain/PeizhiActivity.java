package com.ba.grain;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class PeizhiActivity extends Activity implements OnClickListener{
	private EditText serverAddress;
	private EditText serverPort;
	private Button saveDafault;
	private Button recoverDefault;
	private CheckBox isAutoLogin;
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_peizhi);
		serverAddress=(EditText) this.findViewById(R.id.editTextServerAddress);
		serverPort=(EditText) this.findViewById(R.id.editTextServerPort);
		
		saveDafault=(Button) this.findViewById(R.id.buttonSaveDefault);
		recoverDefault=(Button) this.findViewById(R.id.buttonRecoverDefault);
		
		serverAddress.setText(PreferenceManager.getDefaultSharedPreferences(PeizhiActivity.this).getString(LoginActivity.KEY_ADDR, "10.0.2.2"));
		serverPort.setText(""+PreferenceManager.getDefaultSharedPreferences(PeizhiActivity.this).getInt(LoginActivity.KEY_PORT, 20000));
		saveDafault.setOnClickListener(this);
		recoverDefault.setOnClickListener(this);
		isAutoLogin=(CheckBox) this.findViewById(R.id.is_auto_login);
		if(PreferenceManager.getDefaultSharedPreferences(PeizhiActivity.this).getBoolean(LoginActivity.KEY_IS_AUTO_LOGIN, false)){
			isAutoLogin.setChecked(true);
		}else{
			isAutoLogin.setChecked(false);
		}
		
		
		
		
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
	
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.buttonRecoverDefault:
			initDefault();
			break;
		case R.id.buttonSaveDefault:
			saveDefault();
			break;
		default:
		}
		
	}
	
	@SuppressLint("NewApi")
	public void initDefault(){
			serverAddress.setText("59.41.9.217");
			serverPort.setText("20000");
			Toast.makeText(getApplicationContext(), "恢复成功！恢复默认后，点保存设置才会生效", Toast.LENGTH_LONG).show();
	
	}
	public void saveDefault(){
		PreferenceManager.getDefaultSharedPreferences(PeizhiActivity.this).edit()
		.putString(LoginActivity.KEY_ADDR, serverAddress.getText().toString().trim())
		.putInt(LoginActivity.KEY_PORT, Integer.parseInt(serverPort.getText().toString().trim()))
		.putBoolean(LoginActivity.KEY_IS_AUTO_LOGIN, isAutoLogin.isChecked())
		.commit();
		Toast.makeText(getApplicationContext(), "设置保存成功", Toast.LENGTH_LONG).show();
	}
	
	
	
}
