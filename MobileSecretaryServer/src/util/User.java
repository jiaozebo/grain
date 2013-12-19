package util;

import java.io.Serializable;


/**
 * 用户实体类   对应tail_lister
 * @author Reman
 *
 */

public class User implements Serializable{  
	public String id;
	public String department_id;
	public String loginName;
	public String password;
	public String cnName;
	public String telephone;
	public String handPhone;
	public String email;

}
