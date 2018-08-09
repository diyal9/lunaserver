/**
 * 支付接口
 * @author diyalyin
 */
package luna.servlet.connserver;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import luna.services.msdk.Pay;
import luna.services.payapp.PayAppDemo;
import luna.services.payapp.PayAppInterface;
import luna.util.SysDao;

public class PayCallback extends HttpServlet {

	/**
	 * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
	 * methods.
	 * 
	 * @param request
	 *            servlet request
	 * @param response
	 *            servlet response
	 * @throws ServletException
	 *             if a servlet-specific error occurs
	 * @throws IOException
	 *             if an I/O error occurs
	 * @throws SQLException 
	 */
	protected void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException, SQLException {
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		try {
			SysDao dao = new SysDao();
			try {
				dao.httpRequestLog(request); //http请求log
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			PayAppInterface payApp = new PayAppDemo();
			Pay qihooPay = new Pay(payApp);

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
			out.println(qihooPay.processRequest(params, request));
		} finally {
			out.close();
		}
	}

	// <editor-fold defaultstate="collapsed"
	// desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
	/**
	 * Handles the HTTP <code>GET</code> method.
	 * 
	 * @param request
	 *            servlet request
	 * @param response
	 *            servlet response
	 * @throws ServletException
	 *             if a servlet-specific error occurs
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		try {
			processRequest(request, response);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Handles the HTTP <code>POST</code> method.
	 * 
	 * @param request
	 *            servlet request
	 * @param response
	 *            servlet response
	 * @throws ServletException
	 *             if a servlet-specific error occurs
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		try {
			processRequest(request, response);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
