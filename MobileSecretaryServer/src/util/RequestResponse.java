package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class RequestResponse {
	/**
	 * 服务端请求ID，即时消息
	 */
	public static final String Q_REQUEST_IMS = "I";
	public static final String REQ = "REQ";
	public static final String PARAM = "Param";
	public static final String NAME = "Name";
	public static final String CHECK_USER = "CheckUser";
	public static final String E = "E";
	public static final String EXCUTE_INSERT = "ExcuteInsert";
	public static final String VALUE = "V";
	public static final String SQL = "SQL";
	public static final String TABLE = "T";
	public static final String IMS_CONTENT = "IC";
	public static final String IMS_TIME = "IT";
	public static final String PASSWORD = "P";
	public static final String RESP = "RESP";
	public static final String UPLOAD_FILE = "UploadFile";
	public static final String FILE = "File";
	public static final String TAIL_PIC = "tail_pic";
	public static final String VERSION = "VER";
	public static final String CHANGE_LOG = "CL";
	public static final String PATH = "P";
	public static final String REQ_DOWNLOAD_FILE = "Dlf";
	public static final int ERROR_NO_SUCH_FILE = 0x2001;
	public static final String REQ_NEW_VERSION = "Rnv";
	public static final String IMS_SENDER = "IS";
	/**
	 * 解码密钥，客户端会用到。
	 */
	private static String sPwdHash;

	public static int auth(Socket socket, String userID, String pwd, Node[] resp) {

		String md5Pwd = encPwd(pwd);
		XMLParser parser = new XMLParser();
		resp[0] = parser.addTag(REQ, NAME, CHECK_USER, PARAM, userID, PASSWORD, md5Pwd);
		int result = requestWithResponse(socket, parser.toString(), resp);
		if (result != 0) {
			String err = XMLParser.getAttrVal(resp[0], PARAM, String.valueOf(0x1000));
			System.out.println(err);
		} else {
			sPwdHash = md5Pwd;
		}
		return result;
	}

	public static String encPwd(String pwd) {
		byte[] buffer = MD5.encrypt(pwd.getBytes());
		String md5Pwd = MD5.byteArray2String(buffer);
		return md5Pwd;
	}

	/**
	 * 
	 * @param socket
	 * @param table
	 * @param attr
	 * @param values
	 * @param fileName
	 * @param fis
	 *            用完记得关闭
	 * @param serinum
	 * @param result
	 * @return
	 */
	public static int excuteInsert(Socket socket, String table, String[] attr, String[][] values,
			String fileName, InputStream fis, String serinum, Node[] result) {
		XMLParser parser = new XMLParser();
		Node m = parser.addTag(REQ, NAME, EXCUTE_INSERT, TABLE, table);
		result[0] = m;
		StringBuilder attrs = new StringBuilder();
		for (int i = 0; i < attr.length; i++) {
			attrs.append(attr[i]);
			if (i != attr.length - 1) {
				attrs.append(',');
			}
		}
		for (int i = 0; i < values.length; i++) {
			StringBuilder aValues = new StringBuilder();
			for (int j = 0; j < attr.length; j++) {
				aValues.append(String.format("'%s'", values[i][j]));
				if (j != attr.length - 1) {
					aValues.append(',');
				}
			}
			String sql = String.format("insert into %s (%s) values(%s);", table, attrs, aValues);
			parser.add_tag_parent(m, SQL, VALUE, sql);
		}
		if (fileName != null) {
			try {
				byte[] bytes = new byte[fis.available()];
				fis.read(bytes);
				fis.close();
				String fc = Base64.encodeToString(bytes, Base64.NO_WRAP);
				Node content = parser.add_tag_parent_val(m, FILE, fc);
				parser.appendAttrValues(content, NAME, fileName, "serinum", String.valueOf(serinum));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return -1;
			} catch (IOException e) {
				e.printStackTrace();
				return -1;
			}
		}
		String req = parser.toString();
		File file = new File("/mnt/sdcard/Grain//log.txt");
		if (!file.exists()) {
			file.mkdirs();
		}
		save2File(req, file.getPath(), true);
		// 不为null的时候不加密。因为这时候已经加过一次密了
		int ret = requestWithResponse(socket, parser.toString(), result, fileName != null);
		// System.out.println("ret"+ret+":"+parser.toString()+":::;"+result);
		return ret;

	}

	public static String file2String(String path) throws IOException {
		FileInputStream fis = new FileInputStream(path);
		byte[] bytes = new byte[fis.available()];
		fis.read(bytes);
		fis.close();
		return Base64.encodeToString(bytes, Base64.NO_WRAP);
	}

	/**
	 * 将base64后的字符串转换，并存为文件
	 * 
	 * @return
	 * @throws IOException
	 */
	public static void string2File(String content, String path) throws IOException {
		FileOutputStream fos = new FileOutputStream(path);
		byte[] bytes = Base64.decode(content, Base64.NO_WRAP);
		fos.write(bytes);
		fos.close();
	}

	/**
	 * 
	 * @param socket
	 * @param 消息队列
	 * @return
	 */
	public static int queryMsg(Socket socket, List<Message> msgs) {
		// requestWithResponse(socket, request, node);
		XMLParser parser = new XMLParser();
		Node[] resp = new Node[] { parser.addTag(REQ, NAME, Q_REQUEST_IMS) };
		int result = requestWithResponse(socket, parser.toString(), resp);
		if (result == 0) {
			String strNum = XMLParser.getAttrVal(resp[0], PARAM, String.valueOf(0));
			int num = Integer.parseInt(strNum);
			if (num > 0) {
				Node ims = resp[0].getFirstChild();
				while (ims != null) {
					if (ims.getNodeName().equals(Q_REQUEST_IMS)) {
						Message msg = new Message();
						msg.content = XMLParser.getAttrVal(ims, IMS_CONTENT, null);
						msg.sendTime = XMLParser.getAttrVal(ims, IMS_TIME, null);
						msg.sender = XMLParser.getAttrVal(ims, IMS_SENDER, null);
						msgs.add(msg);
					}
					ims = ims.getNextSibling();
				}
			}
		}
		return result;
	}

	/*
	 * 历史记录清掉u界面感先保存，有网络时再上报，上报时登录
	 * 
	 * 
	 * 历史记录采集数据查询
	 */

	public static int downLoadNewFile(Socket socket, String remotePath, String localPath) {
		XMLParser parser = new XMLParser();
		Node[] resp = new Node[] { parser.addTag(REQ, NAME, REQ_DOWNLOAD_FILE, PARAM, remotePath) };
		int result = requestWithResponse(socket, parser.toString(), resp);
		if (result == 0) {
			String fileContent = XMLParser.getAttrVal(resp[0], PARAM, null);
			try {
				string2File(fileContent, localPath);
			} catch (IOException e) {
				e.printStackTrace();
				result = -1;
			}
		}
		return result;
	}

	public static int checkNewVersion(Socket socket, NewVersionApk nva) {
		XMLParser parser = new XMLParser();
		Node[] resp = new Node[] { parser.addTag(REQ, NAME, REQ_NEW_VERSION, PARAM, "") };
		int result = requestWithResponse(socket, parser.toString(), resp);
		if (result == 0) {
			Node version = resp[0].getFirstChild();
			while (version != null) {
				if (version.getNodeName().equals(VERSION)) {
					nva.mVersion = XMLParser.getAttrVal(version, VERSION, null);
					nva.mChangeLog = XMLParser.getAttrVal(version, CHANGE_LOG, null);
					nva.mPath = XMLParser.getAttrVal(version, PATH, null);
					break;
				}
			}
		}
		return result;
	}

	public static void send(Socket socket, String content, boolean... no_encode) throws IOException {
		boolean oldversion = no_encode.length == 2;
		if (oldversion) {
			OutputStream os = socket.getOutputStream();
			os.write(content.getBytes());
			os.write("\n".getBytes());
			return;
		}
		boolean encode = no_encode.length == 0 || (!no_encode[0]); // 默认加密
		if (encode) {
			content = new String(Base64.encode(content.getBytes(), Base64.NO_WRAP), "UTF-8");
		}
		StringBuffer sb = new StringBuffer();
		sb.append(encode ? 'E' : 'N');
		sb.append(content);
		sb.append("\n");
		OutputStream os = socket.getOutputStream();
		os.write(sb.toString().getBytes());
	}

	private static Node getMsgNode(String content, String name) {
		NodeList nodes = XMLParser.parseString(content);
		Node msg = null;
		if (nodes != null) {
			int num = nodes.getLength();
			for (int i = 0; i < num; i++) {
				msg = nodes.item(i);
				short type = msg.getNodeType();
				String NodeName = msg.getNodeName();
				if (name.equals(NodeName) && type == Node.ELEMENT_NODE) {
					break;
				}
				msg = null;
			}
		}
		return msg;
	}

	public static String parseRequest(String content, Node[] m) {
		char c = content.charAt(0);
		boolean oldVersion = c == '<';// 兼容未加密老版本
		if (!oldVersion) { // 新版本，检测是不是加密了
			content = content.substring(1);
			boolean encode = c == 'E';
			if (encode) {
				try {
					content = new String(Base64.decode(content.getBytes(), Base64.NO_WRAP), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		m[0] = getMsgNode(content, REQ);
		if (m[0] == null) {
			return null;
		}
		return XMLParser.getAttrVal(m[0], NAME, null);
	}

	/**
	 * 
	 * @param socket
	 * @param request
	 * @param b
	 * @return 负数表示通信错误，否则表示服务器返回来的错误码
	 */
	private static synchronized int requestWithResponse(Socket socket, String request, Node[] node,
			boolean... no_encode) {
		try {
			send(socket, request, no_encode);
			String content = receive(socket);
			// System.out.println("String content = receive(socket);"+content);
			Node req = node[0];
			node[0] = getMsgNode(content, RESP);
			if (!XMLParser.getAttrVal(req, NAME, "")
					.equals(XMLParser.getAttrVal(node[0], NAME, ""))) {
				return 0x1000;
			}
			String e = XMLParser.getAttrVal(node[0], E, String.valueOf(0x1000));
			int errorCode = Integer.parseInt(e);
			return errorCode;
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	}

	private static String receive(Socket socket) throws IOException {
		BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		String content = is.readLine();

		// boolean encode = content.charAt(0) == 'E';
		// content = content.substring(1);
		// if (encode) {
		// try {
		// content = new String(Base64.decode(content.getBytes(),
		// Base64.NO_WRAP), "UTF-8");
		// } catch (UnsupportedEncodingException e) {
		// e.printStackTrace();
		// }
		// }
		// return content;

		char c = content.charAt(0);
		boolean oldVersion = c == '<';// 兼容未加密老版本
		if (!oldVersion) { // 新版本，检测是不是加密了
			content = content.substring(1);
			boolean encode = c == 'E';
			if (encode) {
				try {
					content = new String(Base64.decode(content.getBytes(), Base64.NO_WRAP), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return content;
	}

	public static synchronized void save2File(String content, String path, boolean append) {
		File file = new File(path);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			FileOutputStream fos = new FileOutputStream(file, append);
			fos.write(content.getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
