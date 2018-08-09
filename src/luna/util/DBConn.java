package luna.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConn {
	// 驱动名
	private static String CLASSNAME = "org.postgresql.Driver";
	// // 数据库连接URL
	// private static String URL =
	// "jdbc:postgresql://192.168.1.198:5432/smwinner";
	// private static String DBUSER = "smwinner";
	// private static String DBPWD = "smwinner";

	// local
	// private static String URL = "jdbc:postgresql://localhost:5432/smwinner";
	// private static String DBUSER = "postgres";
	// private static String DBPWD = "lunabox";

	// 正式服务器
	private static String URL = "jdbc:postgresql://192.168.100.61:5432/smwinner";
	private static String DBUSER = "smwinner";
	private static String DBPWD = "smwinner";

	public static Connection getConn() {
		Connection conn = null;
		try {
			Class.forName(CLASSNAME);
			conn = DriverManager.getConnection(URL, DBUSER, DBPWD);
		} catch (ClassNotFoundException cnf) {
			// 找不到驱动
			System.out.println("找不到驱动");
			cnf.printStackTrace();
		} catch (SQLException sqe) {
			// 连接失败8
			System.out.println("数据库连接失败，连接信息不正确");
		} catch (Exception e) {
			// 未知异常
			System.out.println("连接数据库出现未知异常");
		}
		return conn;
	}

	// private static String driverName = ""; // 数据库驱动
	// private static String url = ""; // 连接URL
	// private static String username = ""; // 用户名
	// private static String password = ""; // 密码
	// public static Connection connection = null; // 定义连接对象
	//
	// public static Connection getConn() {
	// driverName = ConfigBean.getDrivername().trim();
	// url = ConfigBean.getConnectURL().trim();
	// username = ConfigBean.getUserName().trim();
	// password = ConfigBean.getPassword().trim();
	// try {
	// Class.forName(driverName).newInstance(); // 加载数据库驱动
	// connection = DriverManager.getConnection(url, username, password); //
	// 加载数据库
	// } catch (Exception ex) {
	// System.out.println("数据库连接失败!" + ex.toString());
	// }
	// return connection;
	// }

	public static Statement createStatement(Connection conn) {
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return stmt;
	}

	public static PreparedStatement prepare(Connection conn, String sql) {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ps;
	}

	public static void close(Connection conn) {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void close(Statement statement) {
		try {
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void close(PreparedStatement statement) {
		try {
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void close(ResultSet rs) {
		try {
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		Connection conn = DBConn.getConn();
		java.sql.PreparedStatement pstm = null;

	}

}
