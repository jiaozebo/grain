package com.ba.grain;

import java.util.ArrayList;
import java.util.List;

import util.Message;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

public class IMSAdapter extends BaseAdapter implements SharedPreferences.OnSharedPreferenceChangeListener {

	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private ListView mList;
	public static  List<Message> mItems = new ArrayList<Message>();
	public IMSAdapter(Context context, ListView list) {
		mList = list;
		mContext = context;
		mLayoutInflater = LayoutInflater.from(mContext);
		mItems = new ArrayList<Message>();
		SharedPreferences pref = context.getSharedPreferences(LoginActivity.PREF_MESSAGE,Context.MODE_PRIVATE);
		LoginActivity.getMessageFromLocal(mItems, pref);
	}


	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public Object getItem(int index) {
		return mItems.get(index);
	}

	@Override
	public long getItemId(int index) {
		return index;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.ims_item, parent, false);
		}
		initView((Message) getItem(position), convertView);
		return convertView;
	}

	protected void initView(final Message ims, View view) {
		TextView content = (TextView) view.findViewById(R.id.tv_ims_content);
		CheckBox isSelect = (CheckBox) view.findViewById(R.id.cb_isselect);
		TextView time = (TextView) view.findViewById(R.id.tv_ims_time);
		TextView sender=(TextView) view.findViewById(R.id.tv_ims_sender);
		TextView isRead=(TextView) view.findViewById(R.id.tv_ims_isread);
		content.setText("【内容】"+ims.content);
		sender.setText("【来自】"+ims.sender);
		isRead.setText("【"+(ims.isRead==1?"已读":"未读")+"】");
		time.setText("【发送时间】"+ims.sendTime);
		isSelect.setChecked(ims.isSelect);
		isSelect.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				if(ims.isSelect){
					ims.isSelect=false;
				}else{
					ims.isSelect=true;
				}
				
			}		
		});
		
	}

	public void removeItems(List<Message> items) {
		mItems.removeAll(items);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		boolean removed = !sharedPreferences.contains(key);
		int indexOf = 0;
		/**
		 * 表示更新或者增加或者删除的那一个
		 */
		Message msgModifyed = null;
		for (int i = 0; i < mItems.size(); i++) {
			Message message = mItems.get(i);
			if (message.sendTime.equals(key)) {
				msgModifyed = message;
				indexOf = i;
				if (removed) { // 删除
					mItems.remove(i);
				} else {// 修改
					indexOf = mItems.indexOf(message);
					break;
				}
			}
		}
		if (!removed) {
			// 添加

			String val = sharedPreferences.getString(key, null);
			Message aMsg = LoginActivity.string2Message(val);
			if (aMsg == null) {
				return;
			}
			boolean newAdd = msgModifyed == null;
			if (newAdd) {
				msgModifyed = aMsg;
				msgModifyed.sendTime = key;
				mItems.add(msgModifyed);
				notifyDataSetChanged();
			} else {
				msgModifyed.content = aMsg.content;
				msgModifyed.isRead = aMsg.isRead;
				int fvIdx = mList.getFirstVisiblePosition();
				int lvIdx = mList.getLastVisiblePosition();
				if (indexOf >= fvIdx && indexOf <= lvIdx) {
					View viewItem = mList.getChildAt(indexOf - fvIdx);
					initView(msgModifyed, viewItem);
				}
			}
		} else {
			// notifyDataSetChanged();
			// 不在这里更新，删除完了手动调用。否则容易造成多次重复调用
		}
	}
}
