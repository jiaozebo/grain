package com.ba.grain;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;

import junit.framework.Assert;
import util.FoodType;
import util.Grade;
import util.Quotation;
import util.Site;

public class ListItem extends Quotation implements Serializable, Comparable<ListItem> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Site mSite;
	public FoodType mFoodType;
	public Grade mGrade;
	public String key;
	public boolean isSelected = false;
	public String mFilePath;
	public String mSiteArea;
	public String mSiteType;

	public Site getSite() {
		if (mSite != null) {
			return mSite;
		}
		Iterator<Site> it = G.sSites.iterator();
		while (it.hasNext()) {
			Site site = (Site) it.next();
			if (site.id.equals(site_id)) {
				mSite = site;
				return site;
			}
		}
		return null;
	}

	public FoodType getFood() {
		if (mFoodType != null) {
			return mFoodType;
		}
		Site site = getSite();
		System.out.println("site is" + site);
		if (site != null) {
			Iterator<FoodType> it = site.foods.iterator();
			while (it.hasNext()) {
				FoodType foodType = (FoodType) it.next();
				if (foodType.id.equals(foodType_id)) {
					mFoodType = foodType;
					return foodType;
				}
			}
		}
		return null;
	}

	public static ListItem fromString(String desc) {
		ListItem gi = new ListItem();
		String att_values = desc.substring(1, desc.length() - 1);
		String[] att_val = att_values.split(",");
		for (int i = 0; i < att_val.length; i++) {
			try {
				String[] pair = att_val[i].split("=");
				if (pair.length != 2) {
					String[] newPair = new String[2];
					newPair[0] = pair[0];
					newPair[1] = "";
					pair = newPair;
				}
				Field field = gi.getClass().getField(pair[0].trim());
				if ("int".equalsIgnoreCase(field.getType().toString())) {
					field.setInt(gi, Integer.parseInt(pair[1]));
				} else if ("float".equalsIgnoreCase(field.getType().toString())) {
					field.setFloat(gi, Float.parseFloat(pair[1]));
				} else if ("boolean".equalsIgnoreCase(field.getType().toString())) {
					field.setBoolean(gi, Boolean.parseBoolean(pair[1]));
				} else {
					field.set(gi, pair[1]);
				}
			} catch (Exception e) {
				e.printStackTrace();
				Assert.assertTrue(e.getMessage(), false);
			}
		}
		return gi;
	}

	public boolean isUploaded() {
		return mUploaded;
	}

	@Override
	public int compareTo(ListItem o) {

		ListItem li = (ListItem) o;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		int result = 0;
		try {
			result = (sdf.parse(li.s_dtCreate).getTime() - sdf.parse(s_dtCreate).getTime()) >= 0 ? 1
					: -1;
			;
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return result;
	}

	public void setSite(Site site) {
		mSite = site;
		if (site != null) {
			mSiteArea = site.area;
			mSiteType = site.type;

			site_id = site.id;
			site_name = site.name;
		}
	}

	@Override
	public String toString() {
		return "[key=" + key + ", isSelected=" + isSelected + ", mFilePath=" + mFilePath
				+ ", mSiteArea=" + mSiteArea + ", mSiteType=" + mSiteType + ", id=" + id
				+ ", site_id=" + site_id + ", site_name=" + site_name + ", serinum=" + serinum
				+ ", foodType_id=" + foodType_id + ", footType_name=" + footType_name
				+ ", grade_id=" + grade_id + ", grade_name=" + grade_name + ", buyPrice="
				+ buyPrice + ", tradePrice=" + tradePrice + ", dayRetailPrice=" + dayRetailPrice
				+ ", buyNumber=" + buyNumber + ", tradeNumber=" + tradeNumber + ", lister_id="
				+ lister_id + ", s_remark=" + s_remark + ", s_dtCreate=" + s_dtCreate
				+ ", picture=" + picture + ", mUploaded=" + mUploaded + ", mSaveDateTime="
				+ mSaveDateTime + "]";
	}

}
