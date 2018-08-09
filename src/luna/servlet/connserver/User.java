/**
 * 用户接口
   * 
   1.code换token和用户信息
   * http://www.example.com/qihoo/user?code=182717763c335a107b5d25a325ebef3c14a8e6b8966327d17&app_key=8689e00460eabb1e66277eb4232fde6f&scope=pay&act=get_info
   * 
   * {
		"access_token": "182717763f6d2887698f56675124513ac78f091855f8d0ddb",
		"user": {
			"id": "182717763",
			"name": "adamsunyu",
			"avatar": "http://u1.qhimg.com/qhimg/quc/48_48/29/02/73/290273aq114f3.92d56f.jpg?f=8689e00460eabb1e66277eb4232fde6f"
		}
	}
	* 
  2.token换用户信息
  * http://www.example.com/qihoo/user?token=182717763bbe69f2b71cf85047dea90f5f54e0883cc060d8c&app_key=8689e00460eabb1e66277eb4232fde6f&act=get_user
  * 
  * {

    "id": "182717763",
    "name": "adamsunyu",
    "avatar": "http://u1.qhimg.com/qhimg/quc/48_48/29/02/73/290273aq114f3.92d56f.jpg?f=8689e00460eabb1e66277eb4232fde6f"
	}

* 3.从token中获取相关信息
* http://www.example.com/qihoo/user?token=182717763bbe69f2b71cf85047dea90f5f54e0883cc060d8c&app_key=8689e00460eabb1e66277eb4232fde6f&act=get_token_info
* {
    "expres_in": "35965",
    "expires_at": "1370810343",
    "user_id": "182717763",
    "app_key": "8689e00460eabb1e66277eb4232fde6f"
}
 */
package luna.servlet.connserver;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import luna.services.msdk.QException;
import luna.services.msdk.QOAuth2;
import luna.services.payapp.PayAppDemo;
import luna.services.payapp.PayAppInterface;
import net.minidev.json.JSONObject;

public class User extends HttpServlet {
	/**
	 * Processes requests for both HTTP
	 * <code>GET</code> and
	 * <code>POST</code> methods.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json;charset=UTF-8");
        QOAuth2 _qOauth2;
        PrintWriter _writer = response.getWriter();
		try {
			String act = request.getParameter("act");
            String scope = request.getParameter("scope");
			PayAppInterface payApp = new PayAppDemo();
			_qOauth2 = new QOAuth2(payApp.getAppKey(), payApp.getAppSecret(), scope);

			String resp = "";
			Map<String, Object> obj = new HashMap<String, Object>();
			try {
				if (act == null) {
					obj.put("error_code", QException.CODE_BAD_PARAM);
					obj.put("error", "需要传入act参数");
				} else if (act.equals("get_info")) {
					HashMap<String, HashMap<String, Object>> info;
					info = _qOauth2.getInfoByCode(request.getParameter("code"));
					obj.put("user", info.get("user"));
					obj.put("token", info.get("token"));
				} else if (act.equals("get_token")) {
					obj = _qOauth2.getAccessTokenByCode(request.getParameter("code"), null);
				} else if (act.equals("get_user")) {
					obj = _qOauth2.userMe(request.getParameter("token"));
				} else if (act.equals("refresh_token")) {
					obj = _qOauth2.getAccessTokenByRefreshToken(request.getParameter("refresh_token"));
				} else if (act.equals("get_token_info")) {
					obj = _qOauth2.getTokenInfo(request.getParameter("token"));
				} else {
					obj.put("error_code", QException.CODE_BAD_PARAM);
					obj.put("error", "act参数不正确");
				}
			} catch (QException e) {
				obj.put("error_code", e.getCode());
				obj.put("error", e.getMessage());
			} catch (Exception e1) {
				e1.printStackTrace(_writer);
			}
			resp = JSONObject.toJSONString(obj);

			_writer.write(resp);
		} catch (QException e) {
			_writer.println("got exception");
			_writer.println(e.getMessage());
		} finally {
			_writer.close();
		}
	}

	// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
	/**
	 * Handles the HTTP
	 * <code>GET</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Handles the HTTP
	 * <code>POST</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Returns a short description of the servlet.
	 *
	 * @return a String containing servlet description
	 */
	@Override
	public String getServletInfo() {
		return "Short description";
	}// </editor-fold>
}
