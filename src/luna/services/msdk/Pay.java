/**
 * 
 */

package luna.services.msdk;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import luna.services.payapp.ConstantsLuna;
import luna.services.payapp.PayAppInterface;
import luna.util.SysDao;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

public class Pay {

	private static final String VERIFY_URL = "http://msdk.mobilem.360.cn/pay/order_verify.json";
	private static final String VERIFIED = "verified";
	private String _appKey;
	private String _appSecret;
	private PayAppInterface _payApp;

	private String _errorMsg = "";

	public Pay(PayAppInterface payApp) {
		this._payApp = payApp;
		this._appKey = payApp.getAppKey();
		this._appSecret = payApp.getAppSecret();
	}

	/**
	 * 处理从360过来的支付订单通知请求
	 * 
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public String processRequest(HashMap<String, String> params,
			HttpServletRequest request) throws SQLException {

		SysDao sDao = new SysDao();
		if (!this._isValidRequest(params)) {
			if (!this._errorMsg.isEmpty()) {
				return this._errorMsg;
			}
			sDao.updateLog(params, request,
					ConstantsLuna.LUNA_STATUS_HTTPERROR, 1);
			return "invalid request ";
		}

		// 360请求，查询是否正确
//		if (!this._verifyOrder(params)) {
//			if (!this._errorMsg.isEmpty()) {
//				return this._errorMsg;
//			}
//			sDao.updateLog(params, request,
//					ConstantsLuna.LUNA_STATUS_PLATVALIDERROR, 1);
//			return "verify failed";
//		}

		if (this._payApp.isValidOrder(params)) {
			this._payApp.processOrder(params); // 订单处理
			sDao.updateLog(params, request,
					ConstantsLuna.LUNA_STATUS_HANDLESUCCESS, 0);
		} else {
			sDao.updateLog(params, request,
					ConstantsLuna.ORDER_STATUS_NOORDER, 0);
//			return "ThisOrderInfoIsError";
		}

		return "ok";
	}

	/**
	 * 向360服务器发起请求验证订单是否有效
	 * 
	 * @param params
	 * @return Boolean 是否有效
	 */
	private Boolean _verifyOrder(HashMap<String, String> params) {
		String url = VERIFY_URL;
		HashMap<String, String> requestParams = new HashMap();

		String field;
		Iterator<String> iterator = params.keySet().iterator();
		while (iterator.hasNext()) {
			field = iterator.next();
			if (field.equals("gateway_flag") || field.equals("sign")
					|| field.contains("sign_return")) {
				continue;
			}
			requestParams.put(field, params.get(field));
		}
		requestParams.put("sign", Util.getSign(requestParams, this._appSecret));

		String ret;
		try {
			ret = Util.requestUrl(url, requestParams);
		} catch (IOException e) {
			this._errorMsg = e.toString();
			return false;
		} catch (Exception e1) {
			this._errorMsg = e1.toString();
			return false;
		}

		JSONParser jsonParser = new JSONParser(
				JSONParser.DEFAULT_PERMISSIVE_MODE);
		JSONObject obj;
		try {
			obj = (JSONObject) jsonParser.parse(ret);

			Boolean verified = obj.get("ret").equals(VERIFIED);
			if (!verified) {
				this._errorMsg = obj.get("ret").toString();
			}
			return verified;
		} catch (ParseException e) {
			this._errorMsg = e.toString();
			return false;
		}
	}

	/**
	 * 检查request完整性
	 * 
	 * @param params
	 * @return Boolean
	 */
	private Boolean _isValidRequest(HashMap params) {

		// String arrFields[] = { "app_key", "product_id", "app_uid",
		// "order_id",
		// "sign_type", "gateway_flag", "sign", "sign_return", "amount" };

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

		if (!params.get("app_key").equals(this._appKey)) {
			this._errorMsg = "not my order";
			return false;
		}

		String sign = Util.getSign(params, this._appSecret);
		String paramSign = (String) params.get("sign");
		return sign.equals(paramSign);
	}
}
