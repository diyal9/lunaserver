package luna.servlet.other;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class rollBack extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//��ȡ������
		ServletInputStream sis = request.getInputStream();
		BufferedReader in = new BufferedReader(new InputStreamReader(sis));
		String line;
		if((line = in.readLine()) != null){
			System.out.println(line);
		}
		in.close();
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doGet(request, response);
	}
}