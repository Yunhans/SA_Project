package controller;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.json.*;
import app.Member;
import app.MemberHelper;
import tools.JsonReader;

// TODO: Auto-generated Javadoc
/**
 * <p>
 * The Class MemberController<br>
 * MemberController類別（class）主要用於處理Member相關之Http請求（Request），繼承HttpServlet
 * </p>
 * 
 * @author IPLab
 * @version 1.0.0
 * @since 1.0.0
 */


public class MemberController extends HttpServlet {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** mh，MemberHelper之物件與Member相關之資料庫方法（Sigleton） */
	private MemberHelper mh = MemberHelper.getHelper();

	/**
	 * 處理Http Method請求POST方法（新增資料）
	 *
	 * @param request  Servlet請求之HttpServletRequest之Request物件（前端到後端）
	 * @param response Servlet回傳之HttpServletResponse之Response物件（後端到前端）
	 * @throws ServletException the servlet exception
	 * @throws IOException      Signals that an I/O exception has occurred.
	 */
	/*
	  public void doPost(HttpServletRequest request, HttpServletResponse response)
	 * throws ServletException, IOException { /**
	 * 透過JsonReader類別將Request之JSON格式資料解析並取回 JsonReader jsr = new
	 * JsonReader(request); JSONObject jso = jsr.getObject();
	 * 
	 * /** 取出經解析到JSONObject之Request參數 String member_account =
	 * jso.getString("member_account"); String hash_pwd = jso.getString("hash_pwd");
	 * String member_name = jso.getString("member_name");
	 * 
	 * /** 建立一個新的會員物件 Member m = new Member(member_account, hash_pwd, member_name);
	 * 
	 * /** 後端檢查是否有欄位為空值，若有則回傳錯誤訊息 if(member_account.isEmpty() || hash_pwd.isEmpty()
	 * || member_name.isEmpty()) { /** 以字串組出JSON格式之資料 String resp =
	 * "{\"status\": \'400\', \"message\": \'欄位不能有空值\', \'response\': \'\'}"; /**
	 * 透過JsonReader物件回傳到前端（以字串方式） jsr.response(resp, response); } /**
	 * 透過MemberHelper物件的checkDuplicate()檢查該會員電子郵件信箱是否有重複 else if
	 * (!mh.checkDuplicate(m)) { /** 透過MemberHelper物件的create()方法新建一個會員至資料庫
	 * JSONObject data = mh.create(m);
	 * 
	 * /** 新建一個JSONObject用於將回傳之資料進行封裝 JSONObject resp = new JSONObject();
	 * resp.put("status", "200"); resp.put("message", "成功! 註冊會員資料...");
	 * resp.put("response", data);
	 * 
	 * /** 透過JsonReader物件回傳到前端（以JSONObject方式） jsr.response(resp, response); } else {
	 * /** 以字串組出JSON格式之資料 String resp =
	 * "{\"status\": \'400\', \"message\": \'新增帳號失敗，此E-Mail帳號重複！\', \'response\': \'\'}"
	 * ; /** 透過JsonReader物件回傳到前端（以字串方式） jsr.response(resp, response); } }
	 */

	/**
	 * 處理Http Method請求GET方法（取得資料）
	 *
	 * @param request  Servlet請求之HttpServletRequest之Request物件（前端到後端）
	 * @param response Servlet回傳之HttpServletResponse之Response物件（後端到前端）
	 * @throws ServletException the servlet exception
	 * @throws IOException      Signals that an I/O exception has occurred.
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("membercontroller/doget");
		/** 透過JsonReader類別將Request之JSON格式資料解析並取回 */
		JsonReader jsr = new JsonReader(request);

		// String id = jsr.getParameter("id");
		String id = request.getParameter("member_id");
		System.out.println("jsr.getparameter id: " + id);

		/** 若直接透過前端AJAX之data以key=value之字串方式進行傳遞參數，可以直接由此方法取回資料 */
		// int id = -1;
		// id = jso.getInt("member_id");
		/** 判斷該字串是否存在，若存在代表要取回個別會員之資料，否則代表要取回全部資料庫內會員之資料 */
		if (id == null) {
			System.out.println("func: memebercontroller get all member");
			/** 透過MemberHelper物件之getAll()方法取回所有會員之資料，回傳之資料為JSONObject物件 */
			JSONObject query = mh.getAll();

			/** 新建一個JSONObject用於將回傳之資料進行封裝 */
			JSONObject resp = new JSONObject();
			resp.put("status", "200");
			resp.put("message", "所有會員資料取得成功");
			resp.put("response", query);

			/** 透過JsonReader物件回傳到前端（以JSONObject方式） */
			jsr.response(resp, response);
		} else {
			System.out.println("func: membercontroller get by id");
			/** 透過MemberHelper物件的getByID()方法自資料庫取回該名會員之資料，回傳之資料為JSONObject物件 */
			JSONObject query = mh.getByID(id);

			/** 新建一個JSONObject用於將回傳之資料進行封裝 */
			JSONObject resp = new JSONObject();
			resp.put("status", "200");
			resp.put("message", "會員資料取得成功");
			resp.put("response", query);
			System.out.println("mctrller do get response:" + resp);
			/** 透過JsonReader物件回傳到前端（以JSONObject方式） */
			jsr.response(resp, response);
		}
	}

	/**
	 * 處理Http Method請求DELETE方法（刪除）
	 *
	 * @param request  Servlet請求之HttpServletRequest之Request物件（前端到後端）
	 * @param response Servlet回傳之HttpServletResponse之Response物件（後端到前端）
	 * @throws ServletException the servlet exception
	 * @throws IOException      Signals that an I/O exception has occurred.
	 */
	public void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/** 透過JsonReader類別將Request之JSON格式資料解析並取回 */
		JsonReader jsr = new JsonReader(request);
		JSONObject jso = jsr.getObject();

		/** 取出經解析到JSONObject之Request參數 */
		int id = jso.getInt("member_id");
		System.out.println("get id (from test_delete): " + id);

		/** 透過MemberHelper物件的deleteByID()方法至資料庫刪除該名會員，回傳之資料為JSONObject物件 */
		JSONObject query = mh.deleteByID(id);

		/** 新建一個JSONObject用於將回傳之資料進行封裝 */
		JSONObject resp = new JSONObject();
		resp.put("message", "會員移除成功！");
		resp.put("status", "200");

		resp.put("response", query);

		/** 透過JsonReader物件回傳到前端（以JSONObject方式） */
		jsr.response(resp, response);
	}

	/**
	 * 處理Http Method請求PUT方法（更新）
	 *
	 * @param request  Servlet請求之HttpServletRequest之Request物件（前端到後端）
	 * @param response Servlet回傳之HttpServletResponse之Response物件（後端到前端）
	 * @throws ServletException the servlet exception
	 * @throws IOException      Signals that an I/O exception has occurred.
	 */
	public void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		/** 透過JsonReader類別將Request之JSON格式資料解析並取回 */
		JsonReader jsr = new JsonReader(request);
		JSONObject jso = jsr.getObject();

		System.out.println(jso.getString("action"));

		// 更改會員身分
		if ("chAdmin".equals(jso.getString("action"))) {

			/** 取出經解析到JSONObject之Request參數 */
			int id = jso.getInt("member_id");

			/** 透過傳入之參數，新建一個以這些參數之會員Member物件 */
			Member m = new Member(id, "", "", "", "", -1, "");

			/** 透過Member物件的update()方法至資料庫更新該名會員資料，回傳之資料為JSONObject物件 */
			JSONObject data = m.changeAdmin();

			/** 新建一個JSONObject用於將回傳之資料進行封裝 */
			JSONObject resp = new JSONObject();
			resp.put("status", "200");
			resp.put("message", "成功更改會員身分");
			resp.put("response", data);

			/** 透過JsonReader物件回傳到前端（以JSONObject方式） */
			jsr.response(resp, response);
			// 更新會員自介、帳號、暱稱
		} else if ("chInfo".equals(jso.getString("action"))) {

			/** 取出經解析到JSONObject之Request參數 */
			int member_id = Integer.parseInt(jso.getString("member_id"));
			String member_name = jso.getString("member_name");
			String member_bio = jso.getString("member_bio");

			/** 透過傳入之參數，新建一個以這些參數之會員Member物件 */
			Member m = new Member(member_id, "", "", member_name, member_bio);

			/** 透過Member物件的update()方法至資料庫更新該名會員資料，回傳之資料為JSONObject物件 */
			JSONObject data = m.changeInfo();

			/** 新建一個JSONObject用於將回傳之資料進行封裝 */
			JSONObject resp = new JSONObject();
			resp.put("status", "200");
			resp.put("message", "成功更改資料");
			resp.put("response", data);

			/** 透過JsonReader物件回傳到前端（以JSONObject方式） */
			jsr.response(resp, response);
			// 會員更改密碼
		} else if ("chPassword".equals(jso.getString("action"))) {

			/** 取出經解析到JSONObject之Request參數 */
			int member_id = Integer.parseInt(jso.getString("member_id"));
			String old_pwd = jso.getString("old_pwd");
			String new_pwd = jso.getString("hash_pwd");

			boolean checkPwd = mh.checkPwd(member_id, old_pwd);

			if (checkPwd) {
				/** 透過傳入之參數，新建一個以這些參數之會員Member物件 */
				Member m = new Member(member_id, "", new_pwd, "", "");

				/** 透過Member物件的update()方法至資料庫更新該名會員資料，回傳之資料為JSONObject物件 */
				JSONObject data = m.changePwd();

				/** 新建一個JSONObject用於將回傳之資料進行封裝 */
				JSONObject resp = new JSONObject();
				resp.put("status", "200");
				resp.put("message", "成功更改密碼");
				resp.put("response", data);

				/** 透過JsonReader物件回傳到前端（以JSONObject方式） */
				jsr.response(resp, response);
			} else {

				/** 新建一個JSONObject用於將回傳之資料進行封裝 */
				JSONObject resp = new JSONObject();
				resp.put("status", "400");
				resp.put("message", "舊密碼不符");

				/** 透過JsonReader物件回傳到前端（以JSONObject方式） */
				jsr.response(resp, response);
			}

		}

		else {

			/** 取出經解析到JSONObject之Request參數 */
			int id = jso.getInt("id");
			String member_account = jso.getString("member_account");
			String hash_pwd = jso.getString("hash_pwd");
			String member_name = jso.getString("member_name");
			String member_bio = jso.getString("member_bio");

			/** 透過傳入之參數，新建一個以這些參數之會員Member物件 */
			Member m = new Member(id, member_account, hash_pwd, member_name, member_bio);

			/** 透過Member物件的update()方法至資料庫更新該名會員資料，回傳之資料為JSONObject物件 */
			JSONObject data = m.update();

			/** 新建一個JSONObject用於將回傳之資料進行封裝 */
			JSONObject resp = new JSONObject();
			resp.put("status", "200");
			resp.put("message", "成功! 更新會員資料...");
			resp.put("response", data);

			/** 透過JsonReader物件回傳到前端（以JSONObject方式） */
			jsr.response(resp, response);
		}

	}

//    public void ChAdmin(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//            /** 透過JsonReader類別將Request之JSON格式資料解析並取回 */
//            JsonReader jsr = new JsonReader(request);
//            JSONObject jso = jsr.getObject();
//            
//            /** 取出經解析到JSONObject之Request參數 */
//            int id = jso.getInt("member_id");
//            String member_account = jso.getString("member_account");
//            String hash_pwd = jso.getString("hash_pwd");
//            String member_name = jso.getString("member_name");
//            String member_bio = jso.getString("member_bio");
//            int is_admin = jso.getInt("is_admin");
//            String member_img_path = jso.getString("member_img_path");
//
//            /** 透過傳入之參數，新建一個以這些參數之會員Member物件 */
//            Member m = new Member(id, member_account, hash_pwd, member_name,member_bio,is_admin, member_img_path);
//            
//            /** 透過Member物件的update()方法至資料庫更新該名會員資料，回傳之資料為JSONObject物件 */
//            JSONObject data = m.changeAdmin();
//            
//            /** 新建一個JSONObject用於將回傳之資料進行封裝 */
//            JSONObject resp = new JSONObject();
//            resp.put("status", "200");
//            resp.put("message", "成功更改會員身分");
//            resp.put("response", data);
//            
//            /** 透過JsonReader物件回傳到前端（以JSONObject方式） */
//            jsr.response(resp, response);
//        }

}