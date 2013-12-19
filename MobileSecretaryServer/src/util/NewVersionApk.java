package util;

import java.io.Serializable;

/*
 * 版本更新实体类
 */
public class NewVersionApk implements Serializable {
	public String mVersion;
	public String mChangeLog;
	/**
	 * 客户端下载时填入的路径（服务器的本地路径）
	 */
	public String mPath;
}
