import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Node;

import util.FoodType;
import util.Grade;
import util.Message;
import util.NewVersionApk;
import util.RequestResponse;
import util.Site;
import util.User;
import util.XMLParser;

/**
 * @author
 * @version 1.0
 * @date 2012-5-24
 */
public class ClientRunnable extends Thread {

	private static final String TAIL_FOOD_TYPE_GRADE = "tail_foodTypeGrade";// 粮食类型对应的等级
	private static final String TAIL_GRADE = "tail_grade";// 等级
	private static final String TAIL_SITE_FOOD_TYPE = "tail_siteFoodType";// 采集站包含的品种
	public static final String TAIL_FOOD_TYPE = "tail_foodType";// 采集品种
	public static final String TAIL_SITE = "tail_site";// 采集站
	private Socket socket;
	private Statement statement;
	/**
	 * 客户端对应的用户
	 */
	private User mUser;
	private String mPwdHash;
	boolean[] oldVersion;

	public ClientRunnable(Socket s, Statement sql) {
		socket = s;
		statement = sql;
	}

	public void run() {
		super.run();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String time = formatter.format(new java.util.Date());
		System.out.println(String.format("%s : client (%s) connect", time, socket.getInetAddress()
				.toString() + ":" + socket.getPort()));
		while (true) {
			try {
				BufferedReader is = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				String content = is.readLine();
				if (content != null) {
					Node[] m = new Node[1];
					String id = RequestResponse.parseRequest(content, m);// 返回请求的类型
					if (RequestResponse.CHECK_USER.equals(id)) {// 用户登陆
						char c = content.charAt(0);
						if (c == '<') {
							oldVersion = new boolean[] { true, true };
						} else {
							oldVersion = new boolean[] { false };
						}
						String usrID = XMLParser.getAttrVal(m[0], RequestResponse.PARAM, "");
						String pwd = XMLParser.getAttrVal(m[0], RequestResponse.PASSWORD, "");
						int result = 0;
						String msg = "";
						List<Site> sites = null;
						User user = null;// 用户实体类
						synchronized (statement) {
							ResultSet rsUsr = null, rsSite = null, rsFood = null, rsGrade = null, rsDepartment = null;
							try {
								rsUsr = statement.executeQuery(String.format(
										// 查询用户表
										"select * from %s where %s='%s';", "tail_lister",
										"loginName", usrID));
								if (rsUsr.next()) {
									user = new User();
									user.id = rsUsr.getString("id");

									// user.department_id =
									// rsUsr.getString("department_id");
									user.cnName = rsUsr.getString("cnName");
									user.handPhone = rsUsr.getString("handPhone");
									user.telephone = rsUsr.getString("telephone");
									user.email = rsUsr.getString("email");
									user.loginName = rsUsr.getString("loginName");
									user.password = rsUsr.getString("password");
									boolean match = false;
									if (oldVersion[0]) {
										match = pwd.equals(user.password);
									} else {
										match = pwd.equals(RequestResponse.encPwd(user.password));
									}
									if (match) {
										// 查出部门
										rsDepartment = statement.executeQuery(String.format(
												"select * from %s where id='%s'", "rs_department",
												rsUsr.getString("department_id")));
										if (rsDepartment.next()) {
											user.department_id = rsDepartment.getString("name");
										} else {
											user.department_id = "未知部门";
										}
										// 查出一个用户所负责的采集站
										String sql = String
												.format("select * from %s where %s in (select site_id from tail_listerSite where lister_id='%s');",
														TAIL_SITE, "id", user.id);
										// System.out.println("查出一个用户所负责的采集站"+sql);
										rsSite = statement.executeQuery(sql);
										rsUsr = null;
										sites = new ArrayList<Site>();

										while (rsSite.next()) {
											Site st = new Site();
											st.id = rsSite.getString("id");
											st.name = rsSite.getString("name");
											st.type = rsSite.getString("type");
											st.address = rsSite.getString("address");
											st.area = rsSite.getString("area");
											st.linkPhone = rsSite.getString("linkPhone");
											st.fax = rsSite.getString("fax");
											sites.add(st);
										}
										Iterator<Site> it = sites.iterator();
										while (it.hasNext()) {
											Site site = (Site) it.next();
											// 找出一个人负责的各个粮食站的所包含的所有品种
											sql = String
													.format("select id,name from %s where %s in (select foodType_id from %s where site_id = '%s') group by id,name;",
															TAIL_FOOD_TYPE, "id",
															TAIL_SITE_FOOD_TYPE, site.id);
											// System.out.println("找出一个人负责的各个粮食站的所包含的所有品种"+sql);
											rsFood = statement.executeQuery(sql);
											rsSite = null;
											while (rsFood.next()) {// 组装各种粮食
												FoodType food = new FoodType();
												food.id = rsFood.getString("id");
												food.name = rsFood.getString("name");
												site.foods.add(food);
											}
											Iterator<FoodType> fd = site.foods.iterator();
											while (fd.hasNext()) {
												FoodType foodType = (FoodType) fd.next();
												// 查出该种粮食对应的等级
												sql = String
														.format("select * from %s where %s in (select grade_id from %s where id = '%s');",
																TAIL_GRADE, "id", TAIL_FOOD_TYPE,
																foodType.id);
												// System.out.println("查出该种粮食对应的等级"+sql);
												rsGrade = statement.executeQuery(sql);
												rsFood = null;
												while (rsGrade.next()) {
													Grade grade = new Grade();
													grade.id = rsGrade.getString("id");
													grade.name = rsGrade.getString("name");
													foodType.grades.add(grade);
												}
											}
										}
									} else { // 密码错误
										result = 1;
									}
								} else {
									result = 1;
								}
							} catch (SQLException e) {
								result = 0x1001;
								e.printStackTrace();
								msg = e.getMessage();
							} finally {
								try {
									if (rsUsr != null) {
										rsUsr.close();
									}
									if (rsSite != null) {
										rsSite.close();
									}
									if (rsFood != null) {
										rsFood.close();
									}
									if (rsGrade != null) {
										rsGrade.close();
									}
								} catch (SQLException e) {
									e.printStackTrace();
								}
							}
						}
						XMLParser parser = new XMLParser();
						Node msgNode = parser.addTag(RequestResponse.RESP, RequestResponse.NAME,
								id, RequestResponse.E, String.valueOf(result),
								RequestResponse.PARAM, msg);
						if (result == 0) {
							mPwdHash = pwd;
							mUser = user;
							parser.add_tag_parent(msgNode, "User", "id", String.valueOf(user.id),
									"cnName", user.cnName, "telePhone", user.telephone,
									"handPhone", user.handPhone, "email", user.email,
									"department_id", String.valueOf(user.department_id));
							for (Site site : sites) {
								Node nodeSite = parser.add_tag_parent(msgNode, "Site", "id",
										String.valueOf(site.id), "name", site.name, "type",
										site.type, "address", site.address, "linkPhone",
										site.linkPhone, "area", site.area, "fax", site.fax);
								for (FoodType food : site.foods) {
									Node nodeFood = parser.add_tag_parent(nodeSite, "FoodType",
											"id", String.valueOf(food.id), "name", food.name,
											"grade_id", String.valueOf(food.grade_id));
									for (Grade grade : food.grades) {
										parser.add_tag_parent(nodeFood, "Grade", "id",
												String.valueOf(grade.id), "name", grade.name);
									}
								}
							}
						}
						String resp = parser.toString();
						// System.out.println(resp);
						RequestResponse.send(socket, resp, oldVersion);
					} else if (RequestResponse.EXCUTE_INSERT.equals(id)) { // 插入操作
						synchronized (statement) {
							int result = 0;
							String msg = "";
							try {
								Node n = m[0].getFirstChild();
								String picid = "";
								String siteid = "";
								String serinum = "";
								String serinum1 = "";
								while (n != null) {
									if (n.getNodeName().equals(RequestResponse.SQL)) {
										Date date = new Date();
										String date1 = sdf.format(date);
										String date2 = sdf.format(new Date(date.getTime() + 24 * 60
												* 60 * 1000));
										String sql = XMLParser.getAttrVal(n, RequestResponse.VALUE,
												null);
										siteid = sql.split("values\\(")[1].split("','")[1];
										serinum1 = sql.split("values\\(")[1].split("','")[2];
										picid = sql.split("values\\('")[1].split("','")[0];
										String sql1 = String
												.format("select top 1 serinum from tail_quotation where site_id='%s' and s_dtCreate>'%s' and s_dtCreate<'%s' order by serinum;",
														siteid, date1, date2);
										ResultSet rs = statement.executeQuery(sql1);
										if (rs.next()) {
											serinum = rs.getString("serinum");
											System.out.println(serinum1 + "........" + serinum);
											sql = sql.replaceAll(serinum1, serinum);
										}
										statement.executeUpdate(sql);
										System.out.println("insert" + serinum + "::" + sql);
									} else if (n.getNodeName().equals(RequestResponse.FILE)) {
										String name = XMLParser.getAttrVal(n, RequestResponse.NAME,
												null);
										String fcontent = XMLParser.getNodeVal(n);
										File dirFile = new File(
												MobileSecretaryServer.properties
														.getProperty("picturesPath"));
										dirFile.mkdir();
										File file = new File(dirFile, "record" + name
												+ new Date().getTime() + ".jpg");

										RequestResponse.string2File(fcontent, file.getPath());
										String sql = String
												.format("insert into tail_pic (id,serinum, name, path) values('%s','%s', '%s', '%s')",
														picid,
														XMLParser.getAttrVal(n, "serinum", "0"),
														file.getName(),
														MobileSecretaryServer.properties
																.getProperty("picturesURL")
																+ file.getName() + ".jpg");
										statement.executeUpdate(sql);
										System.out.println("插入图片" + file.getAbsolutePath() + sql);
									}
									n = n.getNextSibling();
								}
							} catch (SQLException e) {
								result = 0x1001;
								e.printStackTrace();
								msg = e.getMessage();
							}
							XMLParser parser = new XMLParser();
							parser.addTag(RequestResponse.RESP, RequestResponse.NAME, id,
									RequestResponse.E, String.valueOf(result),
									RequestResponse.PARAM, msg);
							String resp = parser.toString();
							System.out.println(resp);
							RequestResponse.send(socket, resp, oldVersion);
						}
					} else if (RequestResponse.Q_REQUEST_IMS.equals(id)) {// 无更新，则param为空，否则param为更新ID，param子标签为更新详细内容
						ResultSet rsIms = null;
						List<Message> msg = new ArrayList<Message>();
						synchronized (statement) {
							try {
								// 查出未读的消息
								String sql = String
										.format("select content, sendTime, id,sender from tail_message where lister_id='%s' and isRead=%d;",
												mUser.id, 0);
								rsIms = statement.executeQuery(sql);

								while (rsIms.next()) {
									Message st = new Message();
									st.content = rsIms.getString("content");
									st.sendTime = rsIms.getString("sendTime");
									st.id = rsIms.getString("id");
									st.sender = rsIms.getString("sender");
									msg.add(st);
								}
								rsIms.close();
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
						XMLParser parser = new XMLParser();
						Node resp = parser.addTag(RequestResponse.RESP, RequestResponse.NAME, id,
								RequestResponse.E, String.valueOf(0), RequestResponse.PARAM,
								String.valueOf(msg.size()));
						StringBuilder sb = new StringBuilder();
						Iterator<Message> it = msg.iterator();
						while (it.hasNext()) {
							Message ims = (Message) it.next();
							it.remove();
							parser.add_tag_parent(resp, RequestResponse.Q_REQUEST_IMS,
									RequestResponse.IMS_CONTENT, ims.content,
									RequestResponse.IMS_TIME, ims.sendTime,
									RequestResponse.IMS_SENDER, ims.sender);
							sb.append("id=").append("'" + ims.id + "'");
							if (it.hasNext()) {
								sb.append(" or ");
							}
						}
						String resp1 = parser.toString();
						RequestResponse.send(socket, resp1, oldVersion);
						if (sb.length() != 0) {
							// System.out.println(resp1);
							// System.out.println("更新"+sb);
							synchronized (statement) {
								try {
									statement.executeUpdate(String.format(
											"update tail_message set isRead=1 where %s", sb));
								} catch (SQLException e) {
									e.printStackTrace();
								}
							}
						}
					} else if (RequestResponse.REQ_DOWNLOAD_FILE.equals(id)) {// APK下载
						String path = XMLParser.getAttrVal(m[0], RequestResponse.PARAM, "");
						int result = 0;
						String param = "no such file!";
						File file = new File(path);
						if (!file.exists()) {
							result = RequestResponse.ERROR_NO_SUCH_FILE;
							System.out.println(String.format("no such file : %s", path));
						} else {
							param = RequestResponse.file2String(path);
						}
						XMLParser parser = new XMLParser();
						parser.addTag(RequestResponse.RESP, RequestResponse.NAME, id,
								RequestResponse.E, String.valueOf(result), RequestResponse.PARAM,
								param);
						String resp = parser.toString();
						System.out.println(String.format("down load file :%s", path));
						RequestResponse.send(socket, resp, oldVersion);
					} else if (RequestResponse.REQ_NEW_VERSION.equals(id)) {
						ResultSet rsVersion = null;
						synchronized (statement) {
							try {
								// 查出是否有版本的更新
								rsVersion = statement
										.executeQuery(String
												.format("select top 1 * from %s where %s in (select max(%s) from %s);",
														"tail_client_version", "version",
														"version", "tail_client_version"));
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
						int result = 0x1001;
						NewVersionApk nva = new NewVersionApk();
						if (rsVersion != null) {
							try {
								if (rsVersion.next()) {
									nva.mVersion = rsVersion.getString("version");
									nva.mChangeLog = rsVersion.getString("changeLog");
									nva.mPath = rsVersion.getString("path");
									result = 0;
								} else {
									result = 0x1002;
								}
								rsVersion.close();
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
						XMLParser parser = new XMLParser();
						Node msgNode = parser
								.addTag(RequestResponse.RESP, RequestResponse.NAME, id,
										RequestResponse.E, String.valueOf(0),
										RequestResponse.PARAM, "");
						if (result == 0) {
							parser.add_tag_parent(msgNode, RequestResponse.VERSION,
									RequestResponse.VERSION, nva.mVersion,
									RequestResponse.CHANGE_LOG, nva.mChangeLog,
									RequestResponse.PATH, nva.mPath);
						}
						String resp = parser.toString();
						System.out.println(resp);
						RequestResponse.send(socket, resp, oldVersion);
					}
					Thread.sleep(100);
				} else {
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
				break;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		time = formatter.format(new Date());
		System.out.println(String.format("%s : server (%s)  off", time, socket.getInetAddress()
				.toString() + ":" + socket.getPort()));
	}
}
