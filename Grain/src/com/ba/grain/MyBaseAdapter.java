package com.ba.grain;

import java.util.ArrayList;

import util.FoodType;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class MyBaseAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater layoutInflater;
	ArrayList<ListItem> list;
	Button btnLeft, btnRight;
	int VIEW_COUNT = 10;// 每页显示个数
	int index = 0;// 用于显示页号的索引

	public MyBaseAdapter(Context mcontext) {
		this.context = mcontext;
		list = new ArrayList<ListItem>();
	}

	@Override
	public int getCount() { // 当前页显示的条目数
		int ori = VIEW_COUNT * index;
		if (list.size() - ori < VIEW_COUNT) {
			return list.size() - ori;
		} else {
			return VIEW_COUNT;// 10条
		}
	}

	public int getAllCount() { // 总条目
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position + index * VIEW_COUNT);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			layoutInflater = LayoutInflater.from(context);
			convertView = layoutInflater.inflate(R.layout.newupload_item, parent, false);
			holder = new ViewHolder();
			holder.img = (ImageView) convertView.findViewById(R.id.picture);
			holder.count = (TextView) convertView.findViewById(R.id.tv_count);
			holder.foodType = (TextView) convertView.findViewById(R.id.tv_foodtype);
			holder.isUpload = (TextView) convertView.findViewById(R.id.tv_isupload);
			holder.isSelect = (CheckBox) convertView.findViewById(R.id.cb_isselect);
			holder.buyPrice = (TextView) convertView.findViewById(R.id.tv_buy_price);
			holder.tradePrice = (TextView) convertView.findViewById(R.id.tv_trade_price);
			holder.daywholesalePrice = (TextView) convertView
					.findViewById(R.id.tv_day_wholesale_price);
			holder.weekWholesalePrice = (TextView) convertView
					.findViewById(R.id.tv_week_wholesale_price);
			holder.collectStation = (TextView) convertView.findViewById(R.id.tv_collect_station);
			holder.grade = (TextView) convertView.findViewById(R.id.tv_grade);
			holder.collectDateTime = (TextView) convertView.findViewById(R.id.tv_collect_date_time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final ListItem item = (ListItem) list.get(position + index * VIEW_COUNT);
		if (item.picture.equals("null") || item.picture == null || item.picture.equals("")) {
			holder.img.setImageResource(R.drawable.nophoto);
		} else {
// Bitmap bm=null;
// File file=new File(item.picture);
// if(file.exists()){
// bm = BitmapFactory.decodeFile(item.picture);
// }
// if(bm==null){
// holder.img.setImageResource(R.drawable.nophoto);
// }else{
// holder.img.setImageBitmap(bm);
// }

			holder.img.setImageResource(R.drawable.hasphoto);
		}
		FoodType ft = item.getFood();
		holder.foodType.setText(ft != null ? ft.name : "未知粮品");
		holder.isUpload.setText(item.isUploaded() ? "已上报" : "未上报");
		holder.isSelect.setChecked(item.isSelected);
		holder.count.setText("" + (position + VIEW_COUNT * index + 1));
		holder.isSelect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (item.isSelected) {
					item.isSelected = false;
				} else {
					item.isSelected = true;
				}

			}
		});
		holder.buyPrice.setText(String.format("%s：%s", "购  买  价",
				String.format("%1$.2f", item.buyPrice)));
		holder.tradePrice.setText(String.format("%s：%s", "",
				String.format("%1$.2f", item.tradePrice)));
		holder.daywholesalePrice.setText(String.format("%s：%s", "日批发价",
				String.format("%1$.2f", item.dayRetailPrice)));
		holder.weekWholesalePrice.setText(String.format("%s：%s", "周批发价",
				String.format("%1$.2f", item.weekRetailPrice)));

// holder.buyPrice.setText(String.format("%s: %.2f %s: %d", "购买价",item.buyPrice,
// "购买数量",item.buyNumber));
// holder.tradePrice.setText(String.format("%s: %.2f %s: %d",
// "交易价",item.tradePrice, "交易数量",item.tradeNumber));
// holder.daywholesalePrice.setText(String.format("%s: %.2f %s: %d",
// "日批发价",item.dayRetailPrice, "日批发数量",item.dayRetailNumber));
// holder.weekWholesalePrice.setText(String.format("%s: %.2f %s: %d",
// "周批发价",item.weekRetailPrice, "周批发数量",item.weekRetailNumber));
		holder.collectStation.setText(String.format("%s:%s", "采集站", item.site_name));
		holder.grade.setText(String.format("%s:%s", "等级", item.grade_name));
		holder.collectDateTime.setText(item.mSaveDateTime);
		return convertView;
	}

	public void add(ListItem item) {
		list.add(item);
	}

	public void removeAll() {
		list.removeAll(list);

	}

	public void selectPageAll(boolean bool) {
		for (int i = 0; i < getCount(); i++) {
			list.get(i + index * VIEW_COUNT).isSelected = bool;
		}
		this.notifyDataSetChanged();
	}

	class ViewHolder {
		ImageView img;
		TextView count;
		TextView foodType;
		TextView isUpload;
		CheckBox isSelect;
		TextView buyPrice;
		TextView tradePrice;
		TextView daywholesalePrice;
		TextView weekWholesalePrice;
		TextView collectStation;
		TextView grade;
		TextView collectDateTime;

	}
}
