package com.ba.grain;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class HelpActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_help);
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
