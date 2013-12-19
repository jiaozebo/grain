package util;

import java.io.Serializable;

/*
 * 粮食等级 tail_grade
 * 
 */
public class Grade implements Serializable {
	public String id;
	public String name;
	public String toString() {
		return name;
	}       

} 
