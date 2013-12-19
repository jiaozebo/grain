package com.ba.grain;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * @author John
 * @version 1.0
 * @date 2011-4-26
 */
public class GrainApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		try {
			PackageManager pm = getPackageManager();
			PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);
			G.sNewVersionApk.mVersion = pi.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Thread.setDefaultUncaughtExceptionHandler(new CrashHandler());
	}

	class CrashHandler implements UncaughtExceptionHandler {

		private final CharSequence ERROR_LINE = "\n\n\n\n";

		@Override
		public void uncaughtException(Thread arg0, Throwable e) {
			e.printStackTrace();
			// checkFfmpegDecoder(MCUApplication.this);
			File rootFile = CollectActivity.makeRootDir();
			if (rootFile != null) {
				File file = new File(rootFile, "log.txt");
				PrintStream err;
				OutputStream os;
				try {
					java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(
							"yy-MM-dd HH.mm.ss");
					String time = formatter.format(new java.util.Date());
					os = new FileOutputStream(file, true);
					err = new PrintStream(os);
					err.append(ERROR_LINE);
					err.append(time);
					err.append(ERROR_LINE);
					e.printStackTrace(err);
					os.close();
					err.close();
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}

			}
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(10);
		}
	}

}