package util;

/**
 * 台帐 表tail_quotation
 * 
 * @author John
 * 
 */
public class Quotation {
	public String id;
	public String site_id;
	public String site_name;
	public String serinum;
	public String foodType_id;;
	public String footType_name;
	public String grade_id;
	public String grade_name;
	public float buyPrice;
	public float tradePrice;
	public float dayRetailPrice;
	public int buyNumber;
	public int tradeNumber;
	public String lister_id;
	public String s_remark;
	public String s_dtCreate;
	public String picture;
	
	public boolean mUploaded = false;//对应state字段 是否上传
	/**
	 * 保存时间，即采集时间
	 */
	public String mSaveDateTime;
	@Override
	public String toString() {
		return "[id=" + id + ", site_id=" + site_id + ", site_name=" + site_name
				+ ", serinum=" + serinum + ", foodType_id=" + foodType_id + ", footType_name="
				+ footType_name + ", grade_id=" + grade_id + ", grade_name=" + grade_name
				+ ", buyPrice=" + buyPrice + ", tradePrice=" + tradePrice + ", dayRetailPrice="
				+ dayRetailPrice + ", buyNumber=" + buyNumber + ", tradeNumber=" + tradeNumber
				+ ", lister_id=" + lister_id + ", s_remark=" + s_remark + ", s_dtCreate="
				+ s_dtCreate + ", picture=" + picture + ", mUploaded=" + mUploaded
				+ ", mSaveDateTime=" + mSaveDateTime + "]";
	}

//	@Override
//	public String toString() {
//		return "[site_id=" + site_id +",site_name="+site_name+ ", serinum=" + serinum + ", foodType_id=" + foodType_id+",footType_name="+footType_name
//				+ ", grade_id=" + grade_id +",grade_name="+grade_name+ ", buyPrice=" + buyPrice + ", tradePrice="
//				+ tradePrice + ", dayRetailPrice=" + dayRetailPrice + ", weekRetailPrice="
//				+ weekRetailPrice + ", mUploaded=" + mUploaded +",buyNumber="+buyNumber+",tradeNumber="+tradeNumber+",dayRetailNumber="+dayRetailNumber+",weekRetailNumber="+weekRetailNumber+",lister_id="+lister_id+ ", mSaveDateTime=" + mSaveDateTime
//				+",s_remark="+s_remark+",s_dtCreate="+s_dtCreate+",picture="+picture+"]";
//	}
	
	
}
