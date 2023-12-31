package app;

import java.sql.*;
import java.time.LocalDateTime;
import org.json.*;

import util.DBMgr;

// TODO: Auto-generated Javadoc
/**
 * <p>
 * The Class MemberHelper<br>
 * MemberHelper類別（class）主要管理所有與Member相關與資料庫之方法（method）
 * </p>
 * 
 * @author IPLab
 * @version 1.0.0
 * @since 1.0.0
 */

public class MemberHelper {

	/**
	 * 實例化（Instantiates）一個新的（new）MemberHelper物件<br>
	 * 採用Singleton不需要透過new
	 */
	private MemberHelper() {

	}

	/** 靜態變數，儲存MemberHelper物件 */
	private static MemberHelper mh;

	/** 儲存JDBC資料庫連線 */
	private Connection conn = null;

	/** 儲存JDBC預準備之SQL指令 */
	private PreparedStatement pres = null;

	/**
	 * 靜態方法<br>
	 * 實作Singleton（單例模式），僅允許建立一個MemberHelper物件
	 *
	 * @return the helper 回傳MemberHelper物件
	 */
	public static MemberHelper getHelper() {
		/** Singleton檢查是否已經有MemberHelper物件，若無則new一個，若有則直接回傳 */
		if (mh == null)
			mh = new MemberHelper();

		return mh;
	}

	/**
	 * 透過會員編號（ID）刪除會員
	 *
	 * @param id 會員編號
	 * @return the JSONObject 回傳SQL執行結果
	 */
	public JSONObject deleteByID(int id) {
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
			String sql = "DELETE FROM `mydb`.`member` WHERE `member_id` = ? LIMIT 1";

			/** 將參數回填至SQL指令當中 */
			pres = conn.prepareStatement(sql);
			pres.setInt(1, id);
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

	/**
	 * 取回所有會員資料
	 *
	 * @return the JSONObject 回傳SQL執行結果與自資料庫取回之所有資料
	 */
	public JSONObject getAll() {
		/** 新建一個 Member 物件之 m 變數，用於紀錄每一位查詢回之會員資料 */
		Member m = null;
		/** 用於儲存所有檢索回之會員，以JSONArray方式儲存 */
		JSONArray jsa = new JSONArray();
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
			/** SQL指令: 所有會員資料 */
			String sql = "SELECT * FROM `mydb`.`member`";

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
				row += 1;

				/** 將 ResultSet 之資料取出 */
				int member_id = rs.getInt("member_id");
				String name = rs.getString("member_name");
				String member_account = rs.getString("member_account");
				String hash_pwd = rs.getString("hash_pwd");
				String member_bio = rs.getString("member_bio");
				int is_admin = rs.getInt("is_admin");
				String member_img_path = rs.getString("member_img_path");

				/** 將每一筆會員資料產生一名新Member物件 */
				m = new Member(member_id, member_account, hash_pwd, name, member_bio, is_admin, member_img_path);
				/** 取出該名會員之資料並封裝至 JSONsonArray 內 */
				jsa.put(m.getData());
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

		/** 紀錄程式結束執行時間 */
		long end_time = System.nanoTime();
		/** 紀錄程式執行時間 */
		long duration = (end_time - start_time);

		/** 將SQL指令、花費時間、影響行數與所有會員資料之JSONArray，封裝成JSONObject回傳 */
		JSONObject response = new JSONObject();
		// response.put("sql", exexcute_sql);
		response.put("row", row);
		// response.put("time", duration);
		response.put("data", jsa);
		System.out.println("memberhelper getall() response: " + response);

		return response;
	}

	/**
	 * 透過會員編號（ID）取得會員資料
	 *
	 * @param id 會員編號
	 * @return the JSON object 回傳SQL執行結果與該會員編號之會員資料
	 */
	public JSONObject getByID(String id) {
		/** 新建一個 Member 物件之 m 變數，用於紀錄每一位查詢回之會員資料 */
		Member m = null;
		/** 用於儲存所有檢索回之會員，以JSONArray方式儲存 */
		JSONArray jsa = new JSONArray();
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
			String sql = "SELECT * FROM `mydb`.`member` WHERE `member_id` = ? LIMIT 1";

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
				row += 1;

				/** 將 ResultSet 之資料取出 */
				int member_id = rs.getInt("member_id");
				String name = rs.getString("member_name");
				String member_account = rs.getString("member_account");
				String hash_pwd = rs.getString("hash_pwd");
				String member_bio = rs.getString("member_bio");
				int is_admin = rs.getInt("is_admin");
				String member_img_path = rs.getString("member_img_path");

				/** 將每一筆會員資料產生一名新Member物件 */
				m = new Member(member_id, member_account, hash_pwd, name, member_bio, is_admin, member_img_path);
				/** 取出該名會員之資料並封裝至 JSONsonArray 內 */
				jsa.put(m.getData());
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
		// response.put("time", duration);
		response.put("data", jsa);
		System.out.println("memberhelper getbyid response: " + jsa);

		return response;
	}

	/**
	 * 取得該名會員之更新時間與所屬之會員組別
	 *
	 * @param m 一名會員之Member物件
	 * @return the JSON object 回傳該名會員之更新時間與所屬組別（以JSONObject進行封裝）
	 */
	public JSONObject getLoginTimesStatus(Member m) {
		/** 用於儲存該名會員所檢索之更新時間分鐘數與會員組別之資料 */
		JSONObject jso = new JSONObject();
		/** 儲存JDBC檢索資料庫後回傳之結果，以 pointer 方式移動到下一筆資料 */
		ResultSet rs = null;

		try {
			/** 取得資料庫之連線 */
			conn = DBMgr.getConnection();
			/** SQL指令 */
			String sql = "SELECT * FROM `missa`.`member` WHERE `id` = ? LIMIT 1";

			/** 將參數回填至SQL指令當中 */
			pres = conn.prepareStatement(sql);
			pres.setInt(1, m.getID());
			/** 執行查詢之SQL指令並記錄其回傳之資料 */
			rs = pres.executeQuery();

			/** 透過 while 迴圈移動pointer，取得每一筆回傳資料 */
			/** 正確來說資料庫只會有一筆該電子郵件之資料，因此其實可以不用使用 while迴圈 */
			while (rs.next()) {
				/** 將 ResultSet 之資料取出 */
				int login_times = rs.getInt("login_times");
				String status = rs.getString("status");
				/** 將其封裝至JSONObject資料 */
				jso.put("login_times", login_times);
				jso.put("status", status);
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

		return jso;
	}

	/**
	 * 檢查該名會員之電子郵件信箱是否重複註冊
	 *
	 * @param m 一名會員之Member物件
	 * @return boolean 若重複註冊回傳False，若該信箱不存在則回傳True
	 */
	public boolean checkDuplicate(Member m) {
		/** 紀錄SQL總行數，若為「-1」代表資料庫檢索尚未完成 */
		int row = -1;
		/** 儲存JDBC檢索資料庫後回傳之結果，以 pointer 方式移動到下一筆資料 */
		ResultSet rs = null;

		try {
			/** 取得資料庫之連線 */
			conn = DBMgr.getConnection();
			/** SQL指令 */
			String sql = "SELECT count(*) FROM `mydb`.`member` WHERE `member_account` = ?";

			/** 取得所需之參數 */
			String member_account = m.getEmail();

			/** 將參數回填至SQL指令當中 */
			pres = conn.prepareStatement(sql);
			pres.setString(1, member_account);
			/** 執行查詢之SQL指令並記錄其回傳之資料 */
			rs = pres.executeQuery();

			/** 讓指標移往最後一列，取得目前有幾行在資料庫內 */
			rs.next();
			row = rs.getInt("count(*)");
			System.out.print(row);

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

		/**
		 * 判斷是否已經有一筆該電子郵件信箱之資料 若無一筆則回傳False，否則回傳True
		 */
		return (row == 0) ? false : true;
	}

	/**
	 * 建立該名會員至資料庫
	 *
	 * @param m 一名會員之Member物件
	 * @return the JSON object 回傳SQL指令執行之結果
	 */

	/* 有問題 */
	public JSONObject create(Member m) {
		/** 記錄實際執行之SQL指令 */
		String exexcute_sql = "";

		/** 紀錄SQL總行數 */
		int row = 0;

		try {
			/** 取得資料庫之連線 */
			conn = DBMgr.getConnection();
			/** SQL指令 */
			String sql = "INSERT INTO `mydb`.`member`(`member_name`, `member_account`, `hash_pwd`, `member_bio`, `is_admin`, `member_img_path`)"
					+ " VALUES(?, ?, ?, ?, ?, ?)";

			/** 取得所需之參數 */
			String name = m.getName();
			String member_account = m.getEmail();
			String hash_pwd = m.getPassword();
			int is_admin = m.getis_admin();
			String member_bio = m.getmember_bio();
			String member_img_path = m.getmember_img_path();

			/** 將參數回填至SQL指令當中 */
			pres = conn.prepareStatement(sql);
			// pres.setString(1, name);
			pres.setString(1, name);
			pres.setString(2, member_account);
			pres.setString(3, hash_pwd);
			pres.setString(4, "寫下自介吧!");
			pres.setInt(5, is_admin);
			pres.setString(6, "img/initial.png");

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


		/** 將SQL指令、花費時間與影響行數，封裝成JSONObject回傳 */
		JSONObject response = new JSONObject();

		return response;
	}

	/**
	 * 更新一名會員之會員資料
	 *
	 * @param m 一名會員之Member物件
	 * @return the JSONObject 回傳SQL指令執行結果與執行之資料
	 */
	public JSONObject update(Member m) {
		/** 紀錄回傳之資料 */
		JSONArray jsa = new JSONArray();
		/** 記錄實際執行之SQL指令 */
		String exexcute_sql = "";

		/** 紀錄SQL總行數 */
		int row = 0;

		try {
			/** 取得資料庫之連線 */
			conn = DBMgr.getConnection();
			/** SQL指令 */
			String sql = "Update `mydb`.`member` SET `name` = ? ,`hash_pwd` = ? ,`member_bio` = ?,`is_admin` = ?, `modified` = ? WHERE `member_account` = ?";
			/** 取得所需之參數 */
			String name = m.getName();
			String member_account = m.getEmail();
			String hash_pwd = m.getPassword();
			String member_bio = m.getmember_bio();

			/** 將參數回填至SQL指令當中 */
			pres = conn.prepareStatement(sql);
			pres.setString(1, name);
			pres.setString(2, hash_pwd);
			pres.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
			pres.setString(4, member_account);
			/** 執行更新之SQL指令並記錄影響之行數 */
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

		/** 將SQL指令、花費時間與影響行數，封裝成JSONObject回傳 */
		JSONObject response = new JSONObject();
		//response.put("sql", exexcute_sql);
		//response.put("row", row);
		response.put("data", jsa);
		System.out.println("memberhelper update response: " + response);
		return response;
	}

	public JSONObject changeAdmin(Member m) {
		/** 紀錄回傳之資料 */
		JSONArray jsa = new JSONArray();
		/** 記錄實際執行之SQL指令 */
		String exexcute_sql = "";

		/** 紀錄SQL總行數 */
		int row = 0;

		try {
			/** 取得資料庫之連線 */
			conn = DBMgr.getConnection();
			/** SQL指令 */
			String sql = "Update `mydb`.`member` SET `is_admin` = CASE WHEN `is_admin` = 1 THEN 0 ELSE 1 END WHERE `member_id` = ?";
			/** 取得所需之參數 */

			int member_id = m.getID();
			System.out.println("測試");
			/** 將參數回填至SQL指令當中 */
			pres = conn.prepareStatement(sql);

			pres.setInt(1, member_id);
			/** 執行更新之SQL指令並記錄影響之行數 */
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


		/** 將SQL指令、花費時間與影響行數，封裝成JSONObject回傳 */
		JSONObject response = new JSONObject();
		//response.put("sql", exexcute_sql);
		//response.put("row", row);
		response.put("data", jsa);

		return response;
	}

	public JSONObject changeInfo(Member m) {
		/** 紀錄回傳之資料 */
		JSONArray jsa = new JSONArray();
		/** 記錄實際執行之SQL指令 */
		String exexcute_sql = "";

		/** 紀錄SQL總行數 */
		int row = 0;

		try {
			/** 取得資料庫之連線 */
			conn = DBMgr.getConnection();
			/** SQL指令 */
			String sql = "Update `mydb`.`member` SET `member_name` = ? ,`member_bio` = ? WHERE `member_id` = ?";
			/** 取得所需之參數 */

			int member_id = m.getID();
			String member_name = m.getName();
			String member_bio = m.getmember_bio();
			/** 將參數回填至SQL指令當中 */
			pres = conn.prepareStatement(sql);

			pres.setString(1, member_name);
			pres.setString(2, member_bio);
			pres.setInt(3, member_id);

			/** 執行更新之SQL指令並記錄影響之行數 */
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


		/** 將SQL指令、花費時間與影響行數，封裝成JSONObject回傳 */
		JSONObject response = new JSONObject();
		//response.put("sql", exexcute_sql);
		//response.put("row", row);
		response.put("data", jsa);

		return response;
	}

	public JSONObject changePwd(Member m) {
		/** 紀錄回傳之資料 */
		JSONArray jsa = new JSONArray();
		/** 記錄實際執行之SQL指令 */
		String exexcute_sql = "";
		/** 紀錄SQL總行數 */
		int row = 0;

		try {
			/** 取得資料庫之連線 */
			conn = DBMgr.getConnection();
			/** SQL指令 */
			String sql = "Update `mydb`.`member` SET `hash_pwd` = ?  WHERE `member_id` = ?";
			/** 取得所需之參數 */

			int member_id = m.getID();
			String hash_pwd = m.getPassword();

			/** 將參數回填至SQL指令當中 */
			pres = conn.prepareStatement(sql);

			pres.setString(1, hash_pwd);
			pres.setInt(2, member_id);

			/** 執行更新之SQL指令並記錄影響之行數 */
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


		/** 將SQL指令、花費時間與影響行數，封裝成JSONObject回傳 */
		JSONObject response = new JSONObject();
		//response.put("sql", exexcute_sql);
		//response.put("row", row);
		response.put("data", jsa);

		return response;
	}

	public JSONObject getByEmail(String search_email, String search_password) {
		/** 新建一個 Member 物件之 m 變數，用於紀錄每一位查詢回之會員資料 */
		Member m = null;
		/** 用於儲存所有檢索回之會員，以JSONArray方式儲存 */
		JSONArray jsa = new JSONArray();
		/** 記錄實際執行之SQL指令 */
		String exexcute_sql = "";
		/** 紀錄SQL總行數 */
		int row = 0;
		/** 儲存JDBC檢索資料庫後回傳之結果，以 pointer 方式移動到下一筆資料 */
		ResultSet rs = null;

		try {
			/** 取得資料庫之連線 */
			conn = DBMgr.getConnection();
			/** SQL指令 */
			String sql = "SELECT * FROM `mydb`.`member` WHERE `member_account` = ? AND `hash_pwd` = ? LIMIT 1";

			/** 將參數回填至SQL指令當中 */
			pres = conn.prepareStatement(sql);
			pres.setString(1, search_email);
			pres.setString(2, search_password);
			/** 執行查詢之SQL指令並記錄其回傳之資料 */
			rs = pres.executeQuery();

			/** 紀錄真實執行的SQL指令，並印出 **/
			exexcute_sql = pres.toString();
			System.out.println(exexcute_sql);

			/** 透過 while 迴圈移動pointer，取得每一筆回傳資料 */
			/** 正確來說資料庫只會有一筆該會員編號之資料，因此其實可以不用使用 while 迴圈 */
			while (rs.next()) {
				/** 每執行一次迴圈表示有一筆資料 */
				row += 1;

				/** 將 ResultSet 之資料取出 */
				int member_id = rs.getInt("member_id");
				String name = rs.getString("member_name");
				String member_account = rs.getString("member_account");
				String hash_pwd = rs.getString("hash_pwd");
				String member_bio = rs.getString("member_bio");
				int is_admin = rs.getInt("is_admin");
				String member_img_path = rs.getString("member_img_path");

				/** 將每一筆會員資料產生一名新Member物件 */
				m = new Member(member_id, member_account, hash_pwd, name, member_bio, is_admin, member_img_path);
				/** 取出該名會員之資料並封裝至 JSONsonArray 內 */
				jsa.put(m.getData());
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

		// response.put("time", duration);
		response.put("data", jsa);

		return response;
	}

	public boolean checkAccount(String search_email) {
		/** 紀錄SQL總行數，若為「-1」代表資料庫檢索尚未完成 */
		int row = -1;
		/** 儲存JDBC檢索資料庫後回傳之結果，以 pointer 方式移動到下一筆資料 */
		ResultSet rs = null;

		try {
			/** 取得資料庫之連線 */
			conn = DBMgr.getConnection();
			/** SQL指令 */
			String sql = "SELECT count(*) FROM `mydb`.`member` WHERE `member_account` = ?";

			/** 取得所需之參數 */
			String member_account = search_email;

			/** 將參數回填至SQL指令當中 */
			pres = conn.prepareStatement(sql);
			pres.setString(1, member_account);
			/** 執行查詢之SQL指令並記錄其回傳之資料 */
			rs = pres.executeQuery();
			/** 讓指標移往最後一列，取得目前有幾行在資料庫內 */
			rs.next();
			row = rs.getInt("count(*)");
			// System.out.println(row);

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

		/**
		 * 判斷是否已經有一筆該電子郵件信箱之資料 若無一筆則回傳False，否則回傳True
		 */
		// System.out.print("rs return: "+rs.toString());
		return (row == 1) ? true : false;

	}

	public boolean checkPwd(int id, String old_pwd) {

		/** 儲存JDBC檢索資料庫後回傳之結果，以 pointer 方式移動到下一筆資料 */
		ResultSet rs = null;
		boolean checkResult = false;
		try {
			/** 取得資料庫之連線 */
			conn = DBMgr.getConnection();
			/** SQL指令 */
			String sql = "SELECT * FROM `mydb`.`member` WHERE `member_id` = ?";

			/** 取得所需之參數 */
			int member_id = id;

			/** 將參數回填至SQL指令當中 */
			pres = conn.prepareStatement(sql);
			pres.setInt(1, member_id);

			/** 執行查詢之SQL指令並記錄其回傳之資料 */
			rs = pres.executeQuery();
			rs.next();

			System.out.println("測試密碼: " + rs.getString("hash_pwd"));
			if (rs.getString("hash_pwd").equals(old_pwd)) {
				checkResult = true;
			} else {
				checkResult = false;
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
		return (checkResult) ? true : false;
	}

}
