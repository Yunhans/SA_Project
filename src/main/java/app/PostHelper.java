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
			String sql = "SELECT RANK()Over(PARTITION BY f.post_id ORDER BY f.file_id) as page, f.file_path, f.post_id,"
					+ " p.post_title, p.post_type, p.post_description, m.member_id, m.member_name, m.member_account "
					+ "FROM `mydb`.File f "
					+ "Left JOIN `mydb`.Post p ON f.post_id = p.post_id "
					+ "LEFT JOIN `mydb`.Member m ON f.member_id = m.member_id "
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
			String sql = "SELECT RANK()Over(PARTITION BY f.post_id ORDER BY f.file_id) as page, f.file_path, f.post_id, p.post_title, p.post_type, p.post_description, m.member_id, m.member_name, m.member_account"
					+ " FROM `mydb`.File f"
					+ " LEFT JOIN `mydb`.Post p ON f.post_id = p.post_id"
					+ " LEFT JOIN `mydb`.Member m ON f.member_id = m.member_id"
					+ " WHERE f.member_id = ?";

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
}
