package controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.*;
import javax.servlet.http.*;
import org.json.*;
import app.Member;
import app.MemberHelper;
import tools.JsonReader;

/**
 * Servlet implementation class RegisterController
 */
@WebServlet("/api/register.do")
public class RegisterController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private MemberHelper mh = MemberHelper.getHelper();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RegisterController() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		JsonReader jsr = new JsonReader(request);
		JSONObject jso = jsr.getObject();

		String member_account = jso.getString("member_account");
		String hash_pwd = jso.getString("hash_pwd");
		String member_name = jso.getString("member_name");

		if (member_account.isEmpty() || hash_pwd.isEmpty() || member_name.isEmpty()) {

			String resp = "{\"status\": \'400\', \"message\": \'欄位不能有空值\', \'response\': \'\'}";

			jsr.response(resp, response);
		} else {

			Member m = new Member(member_account, hash_pwd, member_name, 0, "nothing", "img/initial.png");
			if (!mh.checkDuplicate(m)) {

				JSONObject data = mh.create(m);

				JSONObject resp = new JSONObject();

				resp.put("message", "註冊成功!");
				resp.put("status", "200");
				resp.put("response", data);

				jsr.response(resp, response);
				System.out.println("check response: " + resp.toString());
			} else {
				String resp = "{ \"message\": \"帳號重複！\", \"status\": \"400\"}";

				jsr.response(resp, response);
				System.out.println("check response: " + resp.toString());
			}
		}

	}

	/**
	 * @see HttpServlet#doPGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	/*
	 * protected void doGet(HttpServletRequest request, HttpServletResponse
	 * response) throws ServletException, IOException { // TODO Auto-generated
	 * method stub doGet(request, response); }
	 */

}
