package com.ba.grain;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Node;

import util.Message;
import util.RequestResponse;
import util.XMLParser;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
@SuppressLint("NewApi")
public class LoginActivity extends Activity implements View.OnClickListener {
	static final String $$$$ = "[XXXYYYZZZ]";

	public static final String KEY_COLLECTOR = "KEY_COLLECTOR";

	public static final String KEY_PWD = "KEY_PWD";

	public static final String KEY_ADDR = "key_addr";

	public static final String KEY_IS_SAVE_COLLECTOR = "key_is_save_collector";

	public static final String KEY_IS_SAVE_PASSWORD = "key_is_save_password";

	public static final String KEY_IS_AUTO_LOGIN = "key_is_auto_login";

	public static final String KEY_TIMES = "key_times";

	public static final String KEY_PORT = "key_port";

	private static final String KEY_IMS_TIME = "key_ims_time";

	private static final String KEY_IMS_CONTENT = "key_ims_content";

	private static final String KEY_IMS_STATUS = "key_ims_status";

	public static boolean sShowNotify = true;

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	private String mEmail;
	private String mPassword;
	private EditText mEmailView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
	private Button msetting;
	private CheckBox mIsSavePassword;
	private CheckBox mIsAutoLogin;
	public String mAddr = "10.0.2.2";
	public int mPort = 20000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);
		init();

	}

	public void init() {
		msetting = (Button) this.findViewById(R.id.btn_setting);// 跳转到系统配置界面
		msetting.setOnClickListener(this);
		EditText addr = (EditText) findViewById(R.id.addr);
		EditText port = (EditText) findViewById(R.id.port);
		this.mAddr = PreferenceManager.getDefaultSharedPreferences(
				LoginActivity.this).getString(LoginActivity.KEY_ADDR, null);
		addr.setText(mAddr);
		this.mPort = PreferenceManager.getDefaultSharedPreferences(
				LoginActivity.this).getInt(LoginActivity.KEY_PORT, 0);
		port.setText(String.valueOf(this.mPort));
		mEmailView = (EditText) findViewById(R.id.email);
		mEmail = PreferenceManager.getDefaultSharedPreferences(
				LoginActivity.this)
				.getString(LoginActivity.KEY_COLLECTOR, null);
		mEmailView.setText(mEmail);
		mPasswordView = (EditText) findViewById(R.id.password);
		mIsSavePassword = (CheckBox) this.findViewById(R.id.is_save_password);
		if (PreferenceManager.getDefaultSharedPreferences(LoginActivity.this)
				.getBoolean(LoginActivity.KEY_IS_SAVE_PASSWORD, false)) {
			mPasswordView.setText(PreferenceManager
					.getDefaultSharedPreferences(LoginActivity.this).getString(
							LoginActivity.KEY_PWD, null));
			mIsSavePassword.setChecked(true);

		} else {
			mIsSavePassword.setChecked(false);
		}
		mIsAutoLogin = (CheckBox) this.findViewById(R.id.is_auto_login);
		if (PreferenceManager.getDefaultSharedPreferences(LoginActivity.this)
				.getBoolean(LoginActivity.KEY_IS_AUTO_LOGIN, false)) {
			mIsAutoLogin.setChecked(true);
		} else {
			mIsAutoLogin.setChecked(false);
		}
		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {

						EditText addr = (EditText) findViewById(R.id.addr);
						EditText port = (EditText) findViewById(R.id.port);

						mAddr = addr.getText().toString();
						if (TextUtils.isEmpty(mAddr)) {
							addr.setError("地址不能为空！请到系统配置设置你的IP地址！");
							addr.requestFocus();
							return;
						}

						if (TextUtils.isEmpty(port.getText().toString())) {
							port.setError("端口不能为空！请到系统配置设置你的端口");
							port.requestFocus();
							return;
						}

						mPort = Integer.parseInt(port.getText().toString());

						mEmail = mEmailView.getText().toString();
						if (TextUtils.isEmpty(mEmail)) {
							mEmailView.setError("用户名不能为空！");
							mEmailView.requestFocus();
							return;
						}
						mPassword = mPasswordView.getText().toString();
						if (TextUtils.isEmpty(mPassword)) {
							mEmailView.setError("密码不能为空！");
							mEmailView.requestFocus();
							return;
						}

						PreferenceManager
								.getDefaultSharedPreferences(LoginActivity.this)
								.edit()
								.putString(KEY_ADDR, mAddr)
								.putInt(KEY_PORT, mPort)
								.putString(LoginActivity.KEY_COLLECTOR, mEmail)
								.putString(KEY_PWD, mPassword)
								.putBoolean(KEY_IS_SAVE_PASSWORD,
										mIsSavePassword.isChecked())
								.putBoolean(KEY_IS_AUTO_LOGIN,
										mIsAutoLogin.isChecked()).commit();
						attemptLogin();
					}
				});

		if (mIsAutoLogin.isChecked()) {
			findViewById(R.id.sign_in_button).performClick();
		}
	}

	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		mEmailView.setError(null);
		mPasswordView.setError(null);

		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError("用户名不能为空");
			focusView = mEmailView;
			cancel = true;
		}

		if (cancel) {
			focusView.requestFocus();
		} else {
			mLoginStatusMessageView.setText("正在登录...");
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
		}
	}

	@Override
	protected void onResume() {
		init();
		super.onResume();
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
		mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
	}

	@Override
	public void onBackPressed() {

		final UserLoginTask ult = mAuthTask;
		if (ult != null) {
			mAuthTask = null;
			ult.cancel(true);
			showProgress(false);
			return;
		}
		setResult(RESULT_CANCELED);
		finish();
	}

	public static void saveMessage2Local(SharedPreferences pref,
			List<Message> msg) {
		Editor editor = pref.edit();
		Iterator<Message> it = msg.iterator();
		while (it.hasNext()) {
			Message message = (Message) it.next();
			String key = message.sendTime;
			XMLParser parser = new XMLParser();
			Node imsNode = parser.addTag("Msg", "c", message.content, "s",
					String.valueOf(message.isRead), "sender", message.sender);
			String val = XMLParser.node2string(imsNode);
			editor.putString(key, val);
		}
		editor.commit();
	}

	public static void removeMessageFromLocal(List<Message> msg,
			SharedPreferences pref) {
		Iterator<String> it = pref.getAll().keySet().iterator();
		Editor editor = pref.edit();
		while (it.hasNext()) {
			String key = (String) it.next();
			Iterator<Message> itMsg = msg.iterator();
			while (itMsg.hasNext()) {
				Message message = (Message) itMsg.next();
				if (key.equals(message.sendTime)) {
					itMsg.remove();
					it.remove();
					editor.remove(key);
					break;
				}
			}
		}
		editor.commit();
	}

	public static void getMessageFromLocal(List<Message> msg,
			SharedPreferences pref) {
		Iterator<String> it = pref.getAll().keySet().iterator();
		if (msg.size() > 0) {
			for (Message m : msg) {
				msg.remove(m);
			}
		}
		while (it.hasNext()) {
			String key = (String) it.next();
			String val = pref.getString(key, null);
			Message ims = string2Message(val);
			ims.sendTime = key;

			if (ims != null) {
				msg.add(ims);
			}
		}
	}

	public static Message string2Message(String val) {
		if (val != null) {
			Node node = XMLParser.string2Node(val);
			Message msg = new Message();
			msg.content = XMLParser.getAttrVal(node, "c", null);
			String strStatus = XMLParser.getAttrVal(node, "s",
					String.valueOf(0));
			msg.isRead = Integer.parseInt(strStatus);
			msg.sender = XMLParser
					.getAttrVal(node, "sender", String.valueOf(0));
			return msg;
		}
		return null;
	}

	public static final String KEY_STRING_LOGIN_INFORMATION = "key_string_login_information";

	public static final String PREF_MESSAGE = "pref_message";

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Integer, Integer> {
		Thread mCurrentThread;
		Node[] node = new Node[1];
		Socket mSocket = null;

		@Override
		protected Integer doInBackground(Void... params) {
			mCurrentThread = Thread.currentThread();
			mSocket = new Socket();
			SocketAddress remoteAddr = new InetSocketAddress(mAddr, mPort);
			try {
				mSocket.connect(remoteAddr, 10 * 1000);
				if (isCancelled()) {
					mSocket.close();
					mSocket = null;
					return 0;
				}
				int result = RequestResponse.auth(mSocket, mEmail, mPassword,
						node);
				if (isCancelled()) {
					mSocket.close();
					return 0;
				}
				if (result != 0) {
					mSocket.close();
					result = 1;
				}
				return result;
			} catch (IllegalArgumentException exception) {
				exception.printStackTrace();
				return -1;
			} catch (IOException e1) {
				e1.printStackTrace();
				return -2;
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			mAuthTask = null;
			showProgress(false);
			if (result == 0) {
				G.sSocket = mSocket;
				G.sSites.clear();
				Editor editor = PreferenceManager.getDefaultSharedPreferences(
						LoginActivity.this).edit();
				String val = XMLParser.node2string(node[0]);
				editor.putString(KEY_STRING_LOGIN_INFORMATION, val);
				editor.commit();

				G.initDatasFromNode(node[0], mEmail, mPassword);
				setResult(RESULT_OK);
				finish();
				Runnable runnable = new Runnable() {
					CRTimer mTimer = null;
					WeakReference<Socket> mSocketRef = new WeakReference<Socket>(
							G.sSocket);
					List<Message> msg = new ArrayList<Message>();

					private Looper mLooper;

					@Override
					public void run() {
						Looper.prepare();
						mLooper = Looper.myLooper();
						mTimer = new CRTimer(1000) {

							@Override
							protected void onTimer() {
								Socket skt = mSocketRef.get();
								if (skt != null) {
									final int result = RequestResponse
											.queryMsg(skt, msg);
									runOnUiThread(new Runnable() {

										@Override
										public void run() {
											if (result != 0) {
												stop();
												G.sSocket = null;
												Toast.makeText(
														LoginActivity.this,
														"与服务器的连接已断开",
														Toast.LENGTH_SHORT)
														.show();
											} else {
												if (!msg.isEmpty()) {
													SharedPreferences mPreferences = getSharedPreferences(
															PREF_MESSAGE,
															MODE_PRIVATE);

													saveMessage2Local(
															mPreferences, msg);
													showNotification();
													msg.clear();
												}
											}
										}
									});

								} else {
									stop();
								}
							}

							@Override
							public void stop() {
								super.stop();
								mLooper.quit();
							}
						};
						mTimer.start();
						Looper.loop();
					}
				};
				new Thread(runnable).start();
			} else {
				String msg = "地址或端口不可到达";
				if (result > 0) {
					msg = "用户不存在或密码错误";
				}
				Toast.makeText(LoginActivity.this,
						String.format("登录未成功:%s", msg), Toast.LENGTH_SHORT)
						.show();
			}
		}

		@Override
		protected void onCancelled() {
			if (G.sSocket != null) {
				try {
					G.sSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				G.sSocket = null;
			}
			if (mCurrentThread != null) {
				mCurrentThread.interrupt();
			}
			showProgress(false);
		}
	}

	public static boolean checkNewVersion(Context context,
			OnClickListener listener) {
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			if (pi.versionName.compareTo(G.sNewVersionApk.mVersion) < 0) {
				if (listener == null) {
					return true;
				}
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				String message = String.format("版本号：\t%s\n更新日志：\t%s",
						G.sNewVersionApk.mVersion, G.sNewVersionApk.mChangeLog);
				builder.setTitle("发现新版本").setIcon(R.drawable.ic_launcher)
						.setMessage(message)
						.setPositiveButton("现在更新", listener)
						.setNegativeButton("稍后手动更新", listener).show();
				return true;
			}
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public static void installNewVersion(Activity activity, File newApk) {
		Uri uri = Uri.fromFile(newApk);
		// 创建Intent意图
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 启动新的activity
		// 设置Uri和类型
		intent.setDataAndType(uri, "application/vnd.android.package-archive");
		// 执行安装
		activity.startActivity(intent);
	}

	private void showNotification() {
		if (!sShowNotify) {
			return;
		}
		MainActivity.showMsg();
		// expanded notification
		CharSequence text = "接收到短消息，点击查看。";
		// Set the icon, scrolling text and timestamp
		Notification notification = new Notification(R.drawable.ic_launcher,
				getText(R.string.app_name), System.currentTimeMillis());
		Intent intent = new Intent(this, IMSActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				intent, 0);
		// Set the info for the views that show in the notification panel.
		notification.setLatestEventInfo(this, getText(R.string.app_name), text,
				contentIntent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		// Send the notification.
		// We use a layout id because it is a unique number. We use it later to
		// cancel.
		NotificationManager notifyMng = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notifyMng.notify(R.string.app_name, notification);
	}

	public void onClick(View v) {
		if (v.getId() == R.id.btn_setting) {
			startActivity(new Intent(this, PeizhiActivity.class));
		}

	}

}
