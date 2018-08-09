package luna.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.management.modelmbean.RequiredModelMBean;
import javax.servlet.http.HttpServletRequest;

/**
 * 游戏服务器数据处理
 **/
public class SysDao {

	private String SQL_LOAD_INDEX = " select count(*) from t_qihurechargelog ";

	// 插入Log SQL
	private String SQL_LOAD_PROINFO = " insert into T_QihuRechargeLog (PLY_UserID,sys_ipadress,SYS_Url,SYS_HttpsValue,sys_httpdate) values (?,?,?,?,NOW()) ";
	// 修改http请求处理状态（出错的状态下sys_httpsvalue = 1）
	private String SQL_HTTPSTATUS_UPDATE = " update t_qihurechargelog set sys_handerflg = ?, ply_userid = ?,sys_httpsvalue = ? where sys_url = ? ";

	// 取得Index
	public int getIndex() throws SQLException {

		Connection conn = DBConn.getConn();
		PreparedStatement pstm = null;
		ResultSet rs = null;
		boolean isInsert = false;

		int nowIndex = 0; // 数据库总数加1
		try {
			pstm = conn.prepareStatement(SQL_LOAD_INDEX);
			rs = pstm.executeQuery();
			while (rs.next()) {
				nowIndex = rs.getInt(1);
			}

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
		return nowIndex;
	}

	// 请求LOG
	public boolean httpRequestLog(HttpServletRequest request)
			throws SQLException {

		Connection conn = DBConn.getConn();
		PreparedStatement pstm = null;
		boolean isInsert = false;

		Map<String, String[]> paramterMap = request.getParameterMap();
		HashMap params = new HashMap<String, String>();
		String k, v;
		Iterator<String> iterator = paramterMap.keySet().iterator();
		while (iterator.hasNext()) {
			k = iterator.next();
			String arr[] = paramterMap.get(k);
			v = (String) arr[0];
			params.put(k, v);
		}

		StringBuffer url3 = request.getRequestURL();
		String ipstr = request.getRemoteAddr() + ":" + request.getRemotePort();
		String queryStr = request.getQueryString();
		url3.append("?" + queryStr);

		String userid = null;// 平台的id
		if (paramterMap.get("user_id") != null) {
			userid = paramterMap.get("user_id").toString();
		}

		// String plyid = null;// 游戏用户的id
		// if (paramterMap.get("app_uid") != null) {
		// plyid = paramterMap.get("app_uid").toString();
		// }

		try {
			pstm = conn.prepareStatement(SQL_LOAD_PROINFO);
			pstm.setString(1, userid);
			pstm.setString(2, ipstr);
			pstm.setString(3, url3.toString());
			pstm.setInt(4, 0); // http请求状态默认为0

			int rs = pstm.executeUpdate();
			if (rs > 0) {
				isInsert = true;
			}

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
		return isInsert;
	}

	// 处理状态更新
	public boolean updateLog(HashMap<String, String> params,
			HttpServletRequest request, String handleFlg, int httpstatus)
			throws SQLException {

		Connection conn = DBConn.getConn();
		PreparedStatement pstm = null;
		boolean isInsert = false;

		String arrFields[] = { "order_id", "app_key", "product_id", "amount",
				"app_uid", "user_id", "sign_type", "app_order_id",
				"gateway_flag" };

		ArrayList fields = new ArrayList(Arrays.asList(arrFields));

		String key;
		String value;
		Iterator iterator = fields.iterator();
		while (iterator.hasNext()) {
			key = (String) iterator.next();
			value = (String) params.get(key);
			System.out.println(key + ":" + value);
			if (value == null || value.equals("")) {
				return false;
			}
		}

		StringBuffer url3 = request.getRequestURL();
		String ipstr = request.getRemoteAddr() + ":" + request.getRemotePort();
		String queryStr = request.getQueryString();
		url3.append("?" + queryStr);

		String userid = null;// 平台的id
		if (params.get("user_id") != null) {
			userid = params.get("user_id").toString();
		}

		String plyid = null;// 游戏用户的id
		if (params.get("app_uid") != null) {
			plyid = params.get("app_uid").toString();
		}

		try {
			pstm = conn.prepareStatement(SQL_HTTPSTATUS_UPDATE);
			pstm.setInt(1, Integer.valueOf(handleFlg)); // 处理状态
			pstm.setString(2, userid);
			pstm.setInt(3, httpstatus);
			pstm.setString(4, url3.toString());

			int rs = pstm.executeUpdate();
			if (rs > 0) {
				isInsert = true;
			}

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
		return isInsert;
	}
}
