package controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import app.PostHelper;
import tools.JsonReader;

/**
 * Servlet implementation class SearchController
 */
@WebServlet("/api/search.do")
public class SearchController extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	
	private PostHelper ph = PostHelper.getHelper();
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		/** 透過JsonReader類別將Request之JSON格式資料解析並取回 */
		JsonReader jsr = new JsonReader(request);

		// String id = jsr.getParameter("id");
		String post_id = request.getParameter("post_id");
		String member_id = request.getParameter("member_id");
		System.out.println("jsr.getparameter id: " + member_id);

		/** 若直接透過前端AJAX之data以key=value之字串方式進行傳遞參數，可以直接由此方法取回資料 */
		// int id = -1;
		// id = jso.getInt("member_id");
		/** 判斷該字串是否存在，若存在代表要取回個別會員之資料，否則代表要取回全部資料庫內會員之資料 */
		if (member_id == null && post_id == null) {
			System.out.println("func: postcontroller get all post");
			/** 透過MemberHelper物件之getAll()方法取回所有會員之資料，回傳之資料為JSONObject物件 */
			JSONObject query = ph.getAllPost();

			/** 新建一個JSONObject用於將回傳之資料進行封裝 */
			JSONObject resp = new JSONObject();
			resp.put("status", "200");
			resp.put("message", "所有貼文取得成功");
			resp.put("response", query);

			/** 透過JsonReader物件回傳到前端（以JSONObject方式） */
			jsr.response(resp, response);
		}else if(post_id != null) {
			
			//System.out.println("func: postcontroller get by id");
			
			/** 透過MemberHelper物件的getByID()方法自資料庫取回該名會員之資料，回傳之資料為JSONObject物件 */
			JSONObject query = ph.getSinglePost(post_id);

			/** 新建一個JSONObject用於將回傳之資料進行封裝 */
			JSONObject resp = new JSONObject();
			resp.put("status", "200");
			resp.put("message", "成功取得單一貼文");
			resp.put("response", query);
			System.out.println("pctrller do get response:" + resp);
			/** 透過JsonReader物件回傳到前端（以JSONObject方式） */
			jsr.response(resp, response);
		}else {
			System.out.println("func: postcontroller get by id");
			/** 透過MemberHelper物件的getByID()方法自資料庫取回該名會員之資料，回傳之資料為JSONObject物件 */
			JSONObject query = ph.getByID(member_id);

			/** 新建一個JSONObject用於將回傳之資料進行封裝 */
			JSONObject resp = new JSONObject();
			resp.put("status", "200");
			resp.put("message", "貼文資料取得成功");
			resp.put("response", query);

			/** 透過JsonReader物件回傳到前端（以JSONObject方式） */
			jsr.response(resp, response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
