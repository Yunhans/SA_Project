package app;

import java.sql.*;
import java.time.LocalDateTime;
import org.json.*;

import util.DBMgr;

public class PostHelper {

	
	 // 實例化（Instantiates）一個新的（new）MemberHelper物件<br>
	 // 採用Singleton不需要透過new

	private PostHelper() {

	}

	// 靜態變數，儲存MemberHelper物件
	private static PostHelper ph;

	// 儲存JDBC資料庫連線
	private Connection conn = null;

	// 儲存JDBC預準備之SQL指令
	private PreparedStatement pres = null;


	 // 靜態方法<br>
	 // 實作Singleton（單例模式），僅允許建立一個MemberHelper物件
	 // @return the helper 回傳MemberHelper物件

	public static PostHelper getHelper() {
		// Singleton檢查是否已經有MemberHelper物件，若無則new一個，若有則直接回傳 
		if (ph == null)
			ph = new PostHelper();

		return ph;
	}
	
	
	
	public JSONObject getAllPost() {
		// 新建一個 Member 物件之 m 變數，用於紀錄每一位查詢回之會員資料
		Post p = null;
		// 用於儲存所有檢索回之會員，以JSONArray方式儲存 
		JSONArray jsa = new JSONArray();
		// 記錄實際執行之SQL指令
		String exexcute_sql = "";
		/** 紀錄SQL總行數 */
		//int row = 0;
		/** 儲存JDBC檢索資料庫後回傳之結果，以 pointer 方式移動到下一筆資料 */
		ResultSet rs = null;

		try {
			/** 取得資料庫之連線 */
			conn = DBMgr.getConnection();
			/** SQL指令 */
			String sql = "SELECT RANK()Over(PARTITION BY f.post_id ORDER BY f.file_id) as page, f.file_path, p.post_id,p.post_title, p.post_type, p.post_description, m.member_id, m.member_name, m.member_account "
					+ "FROM Post p "
					+ "Left JOIN file f ON p.post_id = f.post_id "
					+ "LEFT JOIN Member m ON p.member_id = m.member_id "
					+ "ORDER BY f.post_id;";

			/** 將參數回填至SQL指令當中，若無則不用只需要執行 prepareStatement */
			pres = conn.prepareStatement(sql);
			/** 執行查詢之SQL指令並記錄其回傳之資料 */
			rs = pres.executeQuery();

			/** 紀錄真實執行的SQL指令，並印出 **/
			exexcute_sql = pres.toString();
			System.out.println(exexcute_sql);

			/** 透過 while 迴圈移動pointer，取得每一筆回傳資料 */
			while (rs.next()) {
				/** 每執行一次迴圈表示有一筆資料 */
				//row += 1;

				/** 將 ResultSet 之資料取出 */
				int page = rs.getInt("page");
				String file_path = rs.getString("file_path");
				int post_id = rs.getInt("post_id");
				String post_title = rs.getString("post_title");
				String post_type = rs.getString("post_type");
				String post_description = rs.getString("post_description");
				int member_id = rs.getInt("member_id");
				String member_name = rs.getString("member_name");
				String member_account = rs.getString("member_account");

				/** 將每一筆會員資料產生一名新Member物件 */
				p = new Post(page, file_path, post_id, post_title, post_type, post_description, member_id, member_name, member_account);
				/** 取出該名會員之資料並封裝至 JSONsonArray 內 */
				jsa.put(p.getData());
			}

		} catch (SQLException e) {
			/** 印出JDBC SQL指令錯誤 **/
			System.err.format("SQL State: %s\n%s\n%s", e.getErrorCode(), e.getSQLState(), e.getMessage());
		} catch (Exception e) {
			/** 若錯誤則印出錯誤訊息 */
			e.printStackTrace();
		} finally {
			/** 關閉連線並釋放所有資料庫相關之資源 **/
			DBMgr.close(rs, pres, conn);
		}

		/** 將SQL指令、花費時間、影響行數與所有會員資料之JSONArray，封裝成JSONObject回傳 */
		JSONObject response = new JSONObject();
		// response.put("sql", exexcute_sql);
		// response.put("row", row);
		response.put("data", jsa);
		System.out.println("Posthelper getallPost() response: " + response);

		return response;
	}
	
	
	
	public int createPost(Post p) {
		/** 記錄實際執行之SQL指令 */
		String exexcute_sql = "";
		/** 紀錄程式開始執行時間 */
		long start_time = System.nanoTime();
		/** 紀錄SQL總行數 */
		int row = 0;
		
		int post_id = -1;
		
		/** 儲存JDBC檢索資料庫後回傳之結果，以 pointer 方式移動到下一筆資料 */
		ResultSet rs = null;

		try {
			/** 取得資料庫之連線 */
			conn = DBMgr.getConnection();
			/** SQL指令 */
			String sql = "INSERT INTO `mydb`.`post`(`post_title`,`post_type`,`post_description`,`member_id`)"
					+ " VALUES(?, ?, ?, ?)";

			/** 取得所需之參數 */
			String post_title = p.getPost_Title();
			String post_type = p.getPost_Type();
			String post_description = p.getPost_Description();
			int member_id = p.getMember_ID();

			/** 將參數回填至SQL指令當中 */
			pres = conn.prepareStatement(sql);
			// pres.setString(1, name);
			pres.setString(1, post_title);
			pres.setString(2, post_type);
			pres.setString(3, post_description);
			pres.setInt(4, member_id);

			/** 執行新增之SQL指令並記錄影響之行數 */
			row = pres.executeUpdate();
			//
			pres = conn.prepareStatement("SELECT post_id FROM `mydb`.`post` ORDER BY post_id DESC LIMIT 1");
			
			rs = pres.executeQuery();

			/** 紀錄真實執行的SQL指令，並印出 **/
			exexcute_sql = pres.toString();
			//System.out.println("posthelper get new post id: "+rs.getInt("post_id"));
			System.out.println(exexcute_sql);

			if (rs.next()) {
		        post_id = rs.getInt("post_id");
		        // 處理 ID 或執行其他操作
		    }
			
		} catch (SQLException e) {
			/** 印出JDBC SQL指令錯誤 **/
			System.err.format("SQL State: %s\n%s\n%s", e.getErrorCode(), e.getSQLState(), e.getMessage());
		} catch (Exception e) {
			/** 若錯誤則印出錯誤訊息 */
			e.printStackTrace();
		} finally {
			/** 關閉連線並釋放所有資料庫相關之資源 **/
			DBMgr.close(pres, conn);
		}

		/** 紀錄程式結束執行時間 */
		long end_time = System.nanoTime();
		/** 紀錄程式執行時間 */
		long duration = (end_time - start_time);

		/** 將SQL指令、花費時間與影響行數，封裝成JSONObject回傳 */
		JSONObject response = new JSONObject();
		// response.put("sql", exexcute_sql);
		// response.put("time", duration);
		// response.put("row:", row);
		System.out.println("posthelper get new post id: "+post_id);

		return post_id;
	}
	
	public JSONObject createfile(UploadedFile uf) {
		/** 記錄實際執行之SQL指令 */
		String exexcute_sql = "";
		/** 紀錄程式開始執行時間 */
		long start_time = System.nanoTime();
		/** 紀錄SQL總行數 */
		int row = 0;

		try {
			/** 取得資料庫之連線 */
			conn = DBMgr.getConnection();
			/** SQL指令 */
			String sql = "INSERT INTO `mydb`.`file`(`file_path`, `post_id`, `member_id`)"
					+ " VALUES(?, ?, ?)";

			/** 取得所需之參數 */
			String filePath = uf.getFile_Path();
			int post_id = uf.getPost_ID();
			int member_id = uf.getMember_ID();

			/** 將參數回填至SQL指令當中 */
			pres = conn.prepareStatement(sql);
			// pres.setString(1, name);
			pres.setString(1, filePath);
			pres.setInt(2, post_id);
			pres.setInt(3, member_id);

			/** 執行新增之SQL指令並記錄影響之行數 */
			row = pres.executeUpdate();

			/** 紀錄真實執行的SQL指令，並印出 **/
			exexcute_sql = pres.toString();
			System.out.println(exexcute_sql);

		} catch (SQLException e) {
			/** 印出JDBC SQL指令錯誤 **/
			System.err.format("SQL State: %s\n%s\n%s", e.getErrorCode(), e.getSQLState(), e.getMessage());
		} catch (Exception e) {
			/** 若錯誤則印出錯誤訊息 */
			e.printStackTrace();
		} finally {
			/** 關閉連線並釋放所有資料庫相關之資源 **/
			DBMgr.close(pres, conn);
		}

		/** 紀錄程式結束執行時間 */
		long end_time = System.nanoTime();
		/** 紀錄程式執行時間 */
		long duration = (end_time - start_time);

		/** 將SQL指令、花費時間與影響行數，封裝成JSONObject回傳 */
		JSONObject response = new JSONObject();
		// response.put("sql", exexcute_sql);
		// response.put("time", duration);
		// response.put("row:", row);

		return response;
	}
	
	public JSONObject deleteFile(int post_id) {
		/** 記錄實際執行之SQL指令 */
		String exexcute_sql = "";
		/** 紀錄程式開始執行時間 */
		long start_time = System.nanoTime();
		/** 紀錄SQL總行數 */
		int row = 0;
		/** 儲存JDBC檢索資料庫後回傳之結果，以 pointer 方式移動到下一筆資料 */
		ResultSet rs = null;

		try {
			/** 取得資料庫之連線 */
			conn = DBMgr.getConnection();

			/** SQL指令 */
			String sql = "DELETE FROM `mydb`.`file` WHERE `post_id` = ? LIMIT 1";

			/** 將參數回填至SQL指令當中 */
			pres = conn.prepareStatement(sql);
			pres.setInt(1, post_id);
			/** 執行刪除之SQL指令並記錄影響之行數 */
			row = pres.executeUpdate();

			/** 紀錄真實執行的SQL指令，並印出 **/
			exexcute_sql = pres.toString();
			System.out.println(exexcute_sql);

		} catch (SQLException e) {
			/** 印出JDBC SQL指令錯誤 **/
			System.err.format("SQL State: %s\n%s\n%s", e.getErrorCode(), e.getSQLState(), e.getMessage());
		} catch (Exception e) {
			/** 若錯誤則印出錯誤訊息 */
			e.printStackTrace();
		} finally {
			/** 關閉連線並釋放所有資料庫相關之資源 **/
			DBMgr.close(rs, pres, conn);
		}

		/** 紀錄程式結束執行時間 */
		long end_time = System.nanoTime();
		/** 紀錄程式執行時間 */
		long duration = (end_time - start_time);

		/** 將SQL指令、花費時間與影響行數，封裝成JSONObject回傳 */
		JSONObject response = new JSONObject();
		response.put("sql", exexcute_sql);
		response.put("row", row);
		// response.put("time", duration);

		return response;
	}
	
	

	public JSONObject getSinglePost(String id) {
		
		Post p = null;
		/** 用於儲存所有檢索回之會員，以JSONArray方式儲存 */
		JSONArray jsa = new JSONArray();
		/** 記錄實際執行之SQL指令 */
		String exexcute_sql = "";
		/** 紀錄SQL總行數 */
		//int row = 0;
		/** 儲存JDBC檢索資料庫後回傳之結果，以 pointer 方式移動到下一筆資料 */
		ResultSet rs = null;

		try {
			/** 取得資料庫之連線 */
			conn = DBMgr.getConnection();
			/** SQL指令 */
			String sql = "SELECT RANK()Over(PARTITION BY f.post_id ORDER BY f.file_id) as page, f.file_path, p.post_id,p.post_title, p.post_type, p.post_description, m.member_id, m.member_name, m.member_account "
					+ "FROM Post p "
					+ "Left JOIN file f ON p.post_id = f.post_id "
					+ "LEFT JOIN Member m ON p.member_id = m.member_id "
					+ "WHERE p.post_id = ?";

			/** 將參數回填至SQL指令當中 */
			pres = conn.prepareStatement(sql);
			pres.setString(1, id);
			/** 執行查詢之SQL指令並記錄其回傳之資料 */
			rs = pres.executeQuery();

			/** 紀錄真實執行的SQL指令，並印出 **/
			exexcute_sql = pres.toString();
			System.out.println(exexcute_sql);

			/** 透過 while 迴圈移動pointer，取得每一筆回傳資料 */
			/** 正確來說資料庫只會有一筆該會員編號之資料，因此其實可以不用使用 while 迴圈 */
			while (rs.next()) {
				/** 每執行一次迴圈表示有一筆資料 */
				//row += 1;

				/** 將 ResultSet 之資料取出 */
				int page = rs.getInt("page");
				String file_path = rs.getString("file_path");
				int post_id = rs.getInt("post_id");
				String post_title = rs.getString("post_title");
				String post_type = rs.getString("post_type");
				String post_description = rs.getString("post_description");
				int member_id = rs.getInt("member_id");
				String member_name = rs.getString("member_name");
				String member_account = rs.getString("member_account");

				/** 將每一筆會員資料產生一名新Post物件 */
				p = new Post(page, file_path, post_id, post_title, post_type, post_description, member_id, member_name, member_account);
				/** 取出該名會員之資料並封裝至 JSONsonArray 內 */
				jsa.put(p.getData());
			}

		} catch (SQLException e) {
			/** 印出JDBC SQL指令錯誤 **/
			System.err.format("SQL State: %s\n%s\n%s", e.getErrorCode(), e.getSQLState(), e.getMessage());
		} catch (Exception e) {
			/** 若錯誤則印出錯誤訊息 */
			e.printStackTrace();
		} finally {
			/** 關閉連線並釋放所有資料庫相關之資源 **/
			DBMgr.close(rs, pres, conn);
		}


		/** 將SQL指令、花費時間、影響行數與所有會員資料之JSONArray，封裝成JSONObject回傳 */
		JSONObject response = new JSONObject();
		// response.put("sql", exexcute_sql);
		// response.put("row", row);
		response.put("data", jsa);
		System.out.println("posthelper getSinglePost response: " + jsa);
		
		
		
		return response;
	}
	
 	public JSONObject getByID(String id) {
		/** 新建一個 Member 物件之 m 變數，用於紀錄每一位查詢回之會員資料 */
		Post p = null;
		/** 用於儲存所有檢索回之會員，以JSONArray方式儲存 */
		JSONArray jsa = new JSONArray();
		/** 記錄實際執行之SQL指令 */
		String exexcute_sql = "";
		/** 紀錄SQL總行數 */
		//int row = 0;
		/** 儲存JDBC檢索資料庫後回傳之結果，以 pointer 方式移動到下一筆資料 */
		ResultSet rs = null;

		try {
			/** 取得資料庫之連線 */
			conn = DBMgr.getConnection();
			/** SQL指令 */
			String sql = "SELECT RANK()Over(PARTITION BY f.post_id ORDER BY f.file_id) as page, f.file_path, p.post_id,p.post_title, p.post_type, p.post_description, m.member_id, m.member_name, m.member_account "
					+ "FROM Post p "
					+ "Left JOIN file f ON p.post_id = f.post_id "
					+ "LEFT JOIN Member m ON p.member_id = m.member_id "
					+ "WHERE m.member_id = ?";

			/** 將參數回填至SQL指令當中 */
			pres = conn.prepareStatement(sql);
			pres.setString(1, id);
			/** 執行查詢之SQL指令並記錄其回傳之資料 */
			rs = pres.executeQuery();

			/** 紀錄真實執行的SQL指令，並印出 **/
			exexcute_sql = pres.toString();
			System.out.println(exexcute_sql);

			/** 透過 while 迴圈移動pointer，取得每一筆回傳資料 */
			/** 正確來說資料庫只會有一筆該會員編號之資料，因此其實可以不用使用 while 迴圈 */
			while (rs.next()) {
				/** 每執行一次迴圈表示有一筆資料 */
				//row += 1;

				/** 將 ResultSet 之資料取出 */
				int page = rs.getInt("page");
				String file_path = rs.getString("file_path");
				int post_id = rs.getInt("post_id");
				String post_title = rs.getString("post_title");
				String post_type = rs.getString("post_type");
				String post_description = rs.getString("post_description");
				int member_id = rs.getInt("member_id");
				String member_name = rs.getString("member_name");
				String member_account = rs.getString("member_account");

				/** 將每一筆會員資料產生一名新Member物件 */
				p = new Post(page, file_path, post_id, post_title, post_type, post_description, member_id, member_name, member_account);
				/** 取出該名會員之資料並封裝至 JSONsonArray 內 */
				jsa.put(p.getData());
			}

		} catch (SQLException e) {
			/** 印出JDBC SQL指令錯誤 **/
			System.err.format("SQL State: %s\n%s\n%s", e.getErrorCode(), e.getSQLState(), e.getMessage());
		} catch (Exception e) {
			/** 若錯誤則印出錯誤訊息 */
			e.printStackTrace();
		} finally {
			/** 關閉連線並釋放所有資料庫相關之資源 **/
			DBMgr.close(rs, pres, conn);
		}


		/** 將SQL指令、花費時間、影響行數與所有會員資料之JSONArray，封裝成JSONObject回傳 */
		JSONObject response = new JSONObject();
		// response.put("sql", exexcute_sql);
		// response.put("row", row);
		response.put("data", jsa);
		System.out.println("posthelper getbyid response: " + jsa);

		return response;
	}
 	
 	public JSONObject deletePostbyId(int post_id) {
		/** 記錄實際執行之SQL指令 */
		String exexcute_sql = "";
		/** 紀錄程式開始執行時間 */
		long start_time = System.nanoTime();
		/** 紀錄SQL總行數 */
		int row = 0;
		/** 儲存JDBC檢索資料庫後回傳之結果，以 pointer 方式移動到下一筆資料 */
		ResultSet rs = null;

		try {
			/** 取得資料庫之連線 */
			conn = DBMgr.getConnection();

			/** SQL指令 */
			String sql = "DELETE FROM `mydb`.`post` WHERE `post_id` = ? LIMIT 1";

			/** 將參數回填至SQL指令當中 */
			pres = conn.prepareStatement(sql);
			pres.setInt(1, post_id);
			/** 執行刪除之SQL指令並記錄影響之行數 */
			row = pres.executeUpdate();

			/** 紀錄真實執行的SQL指令，並印出 **/
			exexcute_sql = pres.toString();
			System.out.println(exexcute_sql);

		} catch (SQLException e) {
			/** 印出JDBC SQL指令錯誤 **/
			System.err.format("SQL State: %s\n%s\n%s", e.getErrorCode(), e.getSQLState(), e.getMessage());
		} catch (Exception e) {
			/** 若錯誤則印出錯誤訊息 */
			e.printStackTrace();
		} finally {
			/** 關閉連線並釋放所有資料庫相關之資源 **/
			DBMgr.close(rs, pres, conn);
		}

		/** 紀錄程式結束執行時間 */
		long end_time = System.nanoTime();
		/** 紀錄程式執行時間 */
		long duration = (end_time - start_time);

		/** 將SQL指令、花費時間與影響行數，封裝成JSONObject回傳 */
		JSONObject response = new JSONObject();
		response.put("sql", exexcute_sql);
		response.put("row", row);
		// response.put("time", duration);

		return response;
	}
 	
 	
 	public JSONObject updatePost(int post_id, String post_title, String post_description) {
		/** 紀錄回傳之資料 */
		JSONArray jsa = new JSONArray();
		/** 記錄實際執行之SQL指令 */
		String exexcute_sql = "";

		try {
			/** 取得資料庫之連線 */
			conn = DBMgr.getConnection();
			/** SQL指令 */
			String sql = "Update `mydb`.`post` SET `post_title` = ? ,`post_description` = ?  WHERE `post_id` = ?";

			/** 將參數回填至SQL指令當中 */
			pres = conn.prepareStatement(sql);
			pres.setString(1, post_title);
			pres.setString(2, post_description);
			pres.setInt(3, post_id);
			
			/** 執行更新之SQL指令並記錄影響之行數 */
			pres.executeUpdate();

			/** 紀錄真實執行的SQL指令，並印出 **/
			exexcute_sql = pres.toString();
			System.out.println("posthelper updatepost: " + exexcute_sql);

		} catch (SQLException e) {
			/** 印出JDBC SQL指令錯誤 **/
			System.err.format("SQL State: %s\n%s\n%s", e.getErrorCode(), e.getSQLState(), e.getMessage());
		} catch (Exception e) {
			/** 若錯誤則印出錯誤訊息 */
			e.printStackTrace();
		} finally {
			/** 關閉連線並釋放所有資料庫相關之資源 **/
			DBMgr.close(pres, conn);
		}

		/** 將SQL指令、花費時間與影響行數，封裝成JSONObject回傳 */
		JSONObject response = new JSONObject();
		//response.put("sql", exexcute_sql);
		response.put("data", jsa);
		System.out.println("posthelper update response: " + response);
		return response;
	}
}
