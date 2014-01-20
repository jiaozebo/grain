package util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/**
 * 采集站实体类 tail_site
 * @author Administrator
 *
 */
public class Site implements Serializable {
	public String id;
	public String name;
	public String type;
	public String address;
	public String linkPhone;
	public String fax;
	public String area; 
	
	public final List<FoodType> foods = new ArrayList<FoodType>();
	public String toString() {
		return name;
	}

}
