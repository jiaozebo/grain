import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author John
 * @version 1.0
 * @date 2012-5-24
 */
public class MobileSecretaryServer {	
	public static final Properties properties=new Properties();
	private static String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";   //加载JDBC驱动      
	private static String dbURL = "jdbc:sqlserver://172.16.1.108:1433; DatabaseName=tail";   //URL   
	private static int port=20000;//默认端口
	private static String dbUserName = "sa";   //默认用户名      
	private static String dbPassword = "bacmp123";   //默认密码     
	private static Connection con;
	private static Statement sql;
	private static ServerSocket ss;
	public static void main(String[] args) {
		
		File file=new File("config\\server-config.dat");//服务器配置文件路径
		try {
			properties.load(new FileInputStream(file));
			driverName=properties.getProperty("driverName");
			dbURL=properties.getProperty("dbURL");
			port=Integer.parseInt(properties.getProperty("port"));
			dbUserName=properties.getProperty("dbUserName");
			dbPassword=properties.getProperty("dbPassword");
			
		} catch (FileNotFoundException e) {
			System.out.println("Config file is not found!");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Config file is loaded!");
		
		try {      
			Class.forName(driverName);      
			con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);      
			System.out.println("Connection Successful!");   //如果连接成功 控制台输出Connection Successful!     
			 sql=con.createStatement();
				final ExecutorService pool = Executors.newCachedThreadPool();
				try {
					ss = new ServerSocket(port); 
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
				Thread server = new Thread() {
				@Override
					public void run() { 
						while (ss != null) {
							ServerSocket sss = ss;
							try { 
								Socket s = sss.accept();
								ClientRunnable cr = new ClientRunnable(s, sql);
								pool.execute(cr);
							} catch (Exception e) {
								e.printStackTrace();
								if (ss != null) {
									try {
										ss.close();
									} catch (IOException e1) {
										e1.printStackTrace();
									}
								}
								break;
							}
						}
					}
				};
				server.start();
				while (server.isAlive()) {
					BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
					String s;
					try {
						while ((s = in.readLine()) != null){
						}
							
					} catch (IOException e) {
						e.printStackTrace();
						ss = null;
						try {
							server.join();
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					}
				}
						
		} catch (Exception e) {      
				e.printStackTrace(); 
		} finally{
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (sql != null) {
				try {
					sql.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
		} 
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		/*
		
		if (args.length < 2) {
			System.out.println("参数不正确！");
			System.exit(2);
		}
		String strPort = args[0];
		final int port = Integer.parseInt(strPort);
		String dbPath = args[1];

		String url = "jdbc:odbc:driver={Microsoft Access Driver (*.mdb)};DBQ=" + dbPath;

		try {
			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return;
		}
		try {
			Properties prop = new Properties();
			prop.put("charSet", "gbk");
			// 然后如果数据库有密码，我们必须把用户名和密码的prop写完整
			prop.put("user", "");
			prop.put("password", "");
			con = DriverManager.getConnection(url, prop);
			sql = con.createStatement();
			
			
			final ExecutorService pool = Executors.newCachedThreadPool();
			try {
				ss = new ServerSocket(port);
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			Thread server = new Thread() {

				@Override
				public void run() {

					while (ss != null) {
						ServerSocket sss = ss;
						try {
							Socket s = sss.accept();
							ClientRunnable cr = new ClientRunnable(s, sql);
							pool.execute(cr);
						} catch (Exception e) {
							e.printStackTrace();
							if (ss != null) {
								try {
									ss.close();
								} catch (IOException e1) {
									e1.printStackTrace();
								}
							}
							break;
						}
					}
				}
			};

			server.start();
			while (server.isAlive()) {
				BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
				String s;
				try {
					while ((s = in.readLine()) != null){
					}
						
				} catch (IOException e) {
					e.printStackTrace();
					ss = null;
					try {
						server.join();
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}

		} catch (SQLException e1) {
			e1.printStackTrace();
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (sql != null) {
				try {
					sql.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		*/
		
		
		
		
		
	}	
}
