package luna.util;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * 游戏服务器数据处理
 **/
public class LogicDao {

	private String SQL_LOAD_INDEX = " select count(*) from t_qihurechargelog ";

	// 修改http请求处理状态（出错的状态下sys_httpsvalue = 1）
	private String SQL_ORDERSTATUS_UPDATE = " UPDATE T_PlayerPayOrder SET ord_status = 1, ord_serialnum = ? WHERE ord_id = ? AND ord_status = 0 ";

	// 检查订单信息是否匹配
	public HashMap<String, String> loadOrder(String orderId)
			throws SQLException {

		Connection conn = DBConn.getConn();
		PreparedStatement pstm = null;
		ResultSet rs = null;

		HashMap<String, String> tmpOrder = new HashMap<String, String>();

		try {
			conn.setAutoCommit(false);
			CallableStatement callSelect = conn
					.prepareCall("{ ? = call playerorder_acquit () }");
			System.out.println("取得订单信息，订单号为:" + orderId);
			callSelect.setString(1, orderId);
			rs = callSelect.executeQuery();

			ResultSetMetaData rsmd = rs.getMetaData();

			int index = 1;
			while (rs.next()) {
				int count = rsmd.getColumnCount();
				for (int i = 1; i <= count; i++) {
					String colName = rsmd.getColumnName(index);
					String value = rs.getString(index);
					System.out.println("成功取得订单信息:" + colName + "->" + value);
					tmpOrder.put(colName, value);
					index++;
				}
			}
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (pstm != null) {
				pstm.close();
			}

			if (conn != null) {
				conn.close();
			}
		}
		return tmpOrder;
	}

	// 处理订单状态
	public boolean updateOrderStatus(String orderno, String platOrderNo)
			throws SQLException {

		Connection conn = DBConn.getConn();
		PreparedStatement pstm = null;
		boolean isSuccess = false;

		try {
			pstm = conn.prepareStatement(SQL_ORDERSTATUS_UPDATE);
			pstm.setString(1, platOrderNo);
			pstm.setString(2, orderno);

			int rs = pstm.executeUpdate();
			if (rs > 0) {
				isSuccess = true;
			} else {
				isSuccess = false;
			}

		} catch (SQLException e) {
			isSuccess = false;
			e.printStackTrace();
		} finally {
			if (pstm != null) {
				pstm.close();
			}

			if (conn != null) {
				conn.close();
			}
		}
		return isSuccess;
	}

	// 处理业务订单逻辑
	public boolean handleLogic(int plyno, int artno, int artnum)
			throws SQLException {

		Connection conn = DBConn.getConn();
		PreparedStatement pstm = null;
		ResultSet rs = null;

		boolean handleSuccess = false;

		try {
			conn.setAutoCommit(false);
			CallableStatement callSelect = conn
					.prepareCall("{ call player_recharge (?,?,?) }");
			callSelect.setInt(1, plyno);
			callSelect.setInt(2, artno);
			callSelect.setInt(3, artnum);
			rs = callSelect.executeQuery();

			while (rs.next()) {
				handleSuccess = true;
			}
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (pstm != null) {
				pstm.close();
			}

			if (conn != null) {
				conn.close();
			}
		}
		return handleSuccess;
	}

	public static void main(String[] args) throws SQLException {
		LogicDao lDao = new LogicDao();
		// HashMap<String, String> atest = lDao
		// .loadOrder("MIUISMW20140125205647466159");
		// lDao.handleLogic(8566, 64, 1);

	}
}
