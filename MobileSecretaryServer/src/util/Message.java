package util;

import java.io.Serializable;

public class Message implements Serializable{
	public String id;
	public String lister_id;
	public String content;
	public String sender;
	public String sendTime;
	public int isRead;
	public boolean isSelect=false;
	
	
	
	/*id
	message ==[content]          
	time==sendTime
	target==lister_id
	status==isRead
	*/
}
