/**
 * 
 */

package luna.services.payapp;

import java.sql.SQLException;
import java.util.HashMap;

import luna.util.LogicDao;

public class PayAppDemo implements PayAppInterface {
	// TODO::需要修改为应用自身的app_key
	private String _appKey = "be79bdb23b44d7c18e5abce6e58fe1b9";
	// TODO::需要修改为应用自身的app_secret(服务器之间通讯使用)
	private String _appSecret = "a772cdde115fb2114784d91ec5440c6b";
	// TODO::人民币-游戏货币的兑换比例
	private int _cashRate = 10;

	private HashMap<String, String> order = null;

	public PayAppDemo() {
	}

	public Boolean isValidOrder(HashMap<String, String> orderParams) {
		String orderId;

		orderId = orderParams.get("app_order_id");
		if (orderId == null || orderId.equals("")) {
			orderId = orderParams.get("app_order_id");
		}

		try {
			order = this._getOrder(orderId);
			if ((order == null) || (order.size() == 0)) {
				// 数据库中获取不到该订单
				// SysDao sDao = new SysDao();
				return false;
			} else {
				int dbOrderNo = Integer.valueOf(order.get("ord_memory"));
				if (_getAmount(orderParams) != dbOrderNo * 100) {
					// http订单金额跟数据库不一致
					return false;
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		String orderProcessed = order.get("processed");
		if (orderProcessed == null) {
			return true;
		}

		if (orderProcessed.equals("")) {
			return true;
		}

		return false;
	}

	// TODO::从数据库中获取订单
	private HashMap<String, String> _getOrder(String orderId)
			throws SQLException {
		LogicDao lDao = new LogicDao();
		HashMap<String, String> order = lDao.loadOrder(orderId);
		return order;
	}

	// 处理订单，发货或者增加游戏中的游戏币
	public void processOrder(HashMap<String, String> orderParams) {
		Boolean re = this._updateOrder(orderParams); // 首先更新订单状态
		if (!re) {
			// 如果更新没有结果
			return;
		}

		// 成功更新到数据，添加用户金钱信息
		this._addCash(orderParams);
	}

	// TODO::更新数据库中的订单状态。
	private Boolean _updateOrder(HashMap<String, String> orderParams) {

		boolean handleFlg = true;
		String orderId = orderParams.get("app_order_id");
		if (orderId == null || orderId.equals("")) {
			orderId = orderParams.get("app_order_id");
		}

		String platOrderNo = orderParams.get("order_id");
		if (orderId == null || orderId.equals("")) {
			orderId = orderParams.get("order_id");
		}

		// 更新订单,标识为已经处理，避免重复处理
		LogicDao lDao = new LogicDao();
		try {
			handleFlg = lDao.updateOrderStatus(orderId, platOrderNo);
		} catch (SQLException e) {
			// 如果更新订单状态失败,记录异常，以便再次处理。再次处理的逻辑需应用自己处理
			e.printStackTrace();
			return false;
		}

		if (handleFlg) {
			return true;
		} else {
			return false;
		}
	}

	private int _getAmount(HashMap<String, String> orderParams) {
		String isSms = orderParams.get("is_sms");
		int amount = 0;
		if (isSms == null || isSms.equals("") || isSms.equals("0")) {
			String strAmount = orderParams.get("amount");
			amount = Integer.parseInt(strAmount);
		} else {
			String payExtStr = orderParams.get("pay_ext");
			// TODO::根据consumeCode反推amount
			// json_decode(payExtStr),然后取payExt.get("notify_data").get("consumeCode")
			// TODO::从consumeCode反推amount，注意amount单位为分
		}
		return amount;
	}

	// TODO::发货或者增加游戏中的货币
	private Boolean _addCash(HashMap<String, String> orderParams) {

		// 如果发货失败，记录异常，以便再次处理。处理的逻辑需应用自己处理。
		// // 充值金额，以人民币分为单位。例如2000代表20元
		// int amount = this._getAmount(orderParams);
		// // int gameCashNum = amount / 100 * this._cashRate;

		int plyno = Integer.valueOf(order.get("ply_no").toString());
		int artno = Integer.valueOf(order.get("art_no").toString());
		int artnum = Integer.valueOf(order.get("art_num").toString());
		System.out.println("订单加钱更新Key:" + plyno + "-" + artno + "-" + artnum);

		LogicDao lDao = new LogicDao();
		try {
			lDao.handleLogic(plyno, artno, artnum);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return true;
	}

	public String getAppKey() {
		// TODO Auto-generated method stub
		return _appKey;
	}

	public String getAppSecret() {
		// TODO Auto-generated method stub
		return _appSecret;
	}
}
