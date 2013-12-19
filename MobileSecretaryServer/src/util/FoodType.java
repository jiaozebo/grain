package util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/**
 * 粮食品种  tail_foodType
 * @author Administrator
 *
 */
public class FoodType implements Serializable {
	public String id;
	public String grade_id;
	public String name;
	public final List<Grade> grades = new ArrayList<Grade>();

	@Override
	public String toString() {
		return name;
	} 
} 
