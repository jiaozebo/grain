package com.ba.grain;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.TextureView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class SplashActivity extends Activity {

	private Handler handler;
	private Runnable r;
	public static int TIMES=0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.splash);

		PackageManager pm = getPackageManager();
		try {
			PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);
			TextView tvVersion = (TextView) findViewById(R.id.textView2);
			tvVersion.setText(String.format("版本:%s", pi.versionName));
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		handler = new Handler();
		r = new Runnable() {

			@Override
			public void run() {
				TIMES=PreferenceManager.getDefaultSharedPreferences(SplashActivity.this).getInt(LoginActivity.KEY_TIMES, 0);
				if(TIMES<=0){
					PreferenceManager.getDefaultSharedPreferences(SplashActivity.this).edit()
					.putString(LoginActivity.KEY_ADDR, "59.41.9.217")
					.putInt(LoginActivity.KEY_PORT, 20000)
					.putInt(LoginActivity.KEY_TIMES, TIMES+1)
					.putBoolean(LoginActivity.KEY_IS_SAVE_COLLECTOR, true)
					.putBoolean(LoginActivity.KEY_IS_SAVE_PASSWORD, false)
					.putBoolean(LoginActivity.KEY_IS_AUTO_LOGIN, false)
					.commit();
				}
				startActivity(new Intent(SplashActivity.this, MainActivity.class));
				finish();
			}
		};
		handler.postDelayed(r, 1000);

	}

	@Override
	protected void onDestroy() {
		handler.removeCallbacks(r);
		super.onDestroy();
	}

}
