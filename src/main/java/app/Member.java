package app;

import org.json.*;
import java.util.Calendar;

// TODO: Auto-generated Javadoc
/**
 * <p>
 * The Class Member Member類別（class）具有會員所需要之屬性與方法，並且儲存與會員相關之商業判斷邏輯<br>
 * </p>
 * 
 * @author IPLab
 * @version 1.0.0
 * @since 1.0.0
 */

public class Member {

	/** id，會員編號 */
	private int member_id;

	/** member_account，會員電子郵件信箱 */
	private String member_account;

	/** member_name，會員姓名 */
	private String member_name;

	/** hash_pwd，會員密碼 */
	private String hash_pwd;

	/** member_bio，會員自我介紹 */
	private String member_bio;

	/** status，會員之組別 */
	private int is_admin;

	/** member_bio，會員大頭貼 */
	private String member_img_path;;

	/** mh，MemberHelper之物件與Member相關之資料庫方法（Sigleton） */
	private MemberHelper mh = MemberHelper.getHelper();

	/**
	 * 實例化（Instantiates）一個新的（new）Member物件<br>
	 * 採用多載（overload）方法進行，此建構子用於建立會員資料時，產生一名新的會員
	 *
	 * @param member_account 會員電子信箱
	 * @param hash_pwd       會員密碼
	 * @param member_name    會員姓名
	 * @param is_admin       會員身分
	 */
	public Member(String member_account, String hash_pwd, String member_name, int is_admin, String member_bio,
			String member_img_path) {
		this.member_account = member_account;
		this.hash_pwd = hash_pwd;
		this.member_name = member_name;
		this.is_admin = is_admin;
		this.member_bio = member_bio;
		this.member_img_path = member_img_path;
		update();
	}

	/**
	 * 實例化（Instantiates）一個新的（new）Member物件<br>
	 * 採用多載（overload）方法進行，此建構子用於更新會員資料時，產生一名會員同時需要去資料庫檢索原有更新時間分鐘數與會員組別
	 * 
	 * @param id             會員編號
	 * @param member_account 會員電子信箱
	 * @param hash_pwd       會員密碼
	 * @param member_name    會員姓名
	 * @param member_bio     會員自我介紹
	 */
	public Member(int id, String member_account, String hash_pwd, String member_name, String member_bio) {
		this.member_id = id;
		this.member_account = member_account;
		this.hash_pwd = hash_pwd;
		this.member_name = member_name;
		this.member_bio = member_bio;

	}

	/**
	 * 實例化（Instantiates）一個新的（new）Member物件<br>
	 * 採用多載（overload）方法進行，此建構子用於查詢會員資料時，將每一筆資料新增為一個會員物件
	 *
	 * @param id             會員編號
	 * @param member_account 會員電子信箱
	 * @param hash_pwd       會員密碼
	 * @param member_name    會員姓名
	 * @param member_bio     會員自我介紹
	 * @param is_admin       會員之身分
	 */
	public Member(int id, String member_account, String hash_pwd, String member_name, String member_bio, int is_admin,
			String member_img_path) {
		this.member_id = id;
		this.member_account = member_account;
		this.hash_pwd = hash_pwd;
		this.member_name = member_name;
		this.member_bio = member_bio;
		this.is_admin = is_admin;
		this.member_img_path = member_img_path;
	}

	/**
	 * 取得會員之編號
	 *
	 * @return the id 回傳會員編號
	 */
	public int getID() {
		return this.member_id;
	}

	/**
	 * 取得會員之電子郵件信箱
	 *
	 * @return the member_account 回傳會員電子郵件信箱
	 */
	public String getEmail() {
		return this.member_account;
	}

	/**
	 * 取得會員之姓名
	 *
	 * @return the member_name 回傳會員姓名
	 */
	public String getName() {
		return this.member_name;
	}

	/**
	 * 取得會員之密碼
	 *
	 * @return the hash_pwd 回傳會員密碼
	 */
	public String getPassword() {
		return this.hash_pwd;
	}

	/**
	 * 取得更新之會員自介
	 *
	 * @return the member_bio 回傳會員自介
	 */
	public String getmember_bio() {
		return this.member_bio;
	}

	/**
	 * 取得會員資料之會員組別
	 *
	 * @return the status 回傳會員組別
	 */
	public int getis_admin() {
		return this.is_admin;
	}

	/**
	 * 取得會員頭貼之路徑
	 *
	 * @return path 回傳圖片路徑
	 */
	public String getmember_img_path() {
		return this.member_img_path;
	}

	/**
	 * 更新會員資料
	 *
	 * @return the JSON object 回傳SQL更新之結果與相關封裝之資料
	 */
	public JSONObject update() {
		/** 新建一個JSONObject用以儲存更新後之資料 */
		JSONObject data = new JSONObject();

		/** 檢查該名會員是否已經在資料庫 */
		if (this.member_id != 0) {
			/** 若有則將目前更新後之資料更新至資料庫中 */

			/** 透過MemberHelper物件，更新目前之會員資料置資料庫中 */
			data = mh.update(this);
		}

		return data;
	}

	/**
	 * 更改該名會員身分
	 *
	 * @return the JSON object 回傳SQL更新之結果與相關封裝之資料
	 */
	public JSONObject changeAdmin() {
		JSONObject data = new JSONObject();

		/** 檢查該名會員是否已經在資料庫 */
		if (this.member_id != 0) {
			/** 若有則將其身分更改至資料庫中 */

			/** 透過MemberHelper物件，更新目前之會員資料置資料庫中 */
			data = mh.changeAdmin(this);
		}

		return data;

	}

	/**
	 * 更改該名會員帳號、暱稱、自介
	 *
	 * @return the JSON object 回傳SQL更新之結果與相關封裝之資料
	 */
	public JSONObject changeInfo() {

		JSONObject data = new JSONObject();

		/** 檢查該名會員是否已經在資料庫 */
		if (this.member_id != 0) {
			/** 若有則將其身分更改至資料庫中 */

			/** 透過MemberHelper物件，更新目前之會員資料置資料庫中 */
			data = mh.changeInfo(this);
		}

		return data;
	}

	/**
	 * 更改該名會員密碼
	 *
	 * @return the JSON object 回傳SQL更新之結果與相關封裝之資料
	 */
	public JSONObject changePwd() {
		JSONObject data = new JSONObject();

		/** 檢查該名會員是否已經在資料庫 */
		if (this.member_id != 0) {
			/** 若有則將其身分更改至資料庫中 */

			/** 透過MemberHelper物件，更新目前之會員資料置資料庫中 */
			data = mh.changePwd(this);
		}

		return data;

	}

	/**
	 * 取得該名會員所有資料
	 *
	 * @return the data 取得該名會員之所有資料並封裝於JSONObject物件內
	 */
	public JSONObject getData() {
		/** 透過JSONObject將該名會員所需之資料全部進行封裝 */
		JSONObject jso = new JSONObject();
		jso.put("member_id", getID());
		jso.put("member_name", getName());
		jso.put("member_account", getEmail());
		// jso.put("hash_pwd", getPassword());
		jso.put("member_bio", getmember_bio());
		jso.put("is_admin", getis_admin());
		jso.put("member_img_path", getmember_img_path());

		return jso;
	}

}