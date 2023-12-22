package app;

import org.json.*;
import java.util.Calendar;

public class Post {
	
	//貼文id
	private int page;
		
	//貼文標題
	private String file_path;
		
	//貼文id
	private int post_id;

	//貼文標題
	private String post_title;

	// 貼文類型
	private String post_type;

	//貼文介紹
	private String post_description;

	//對應會員id
	private int member_id;
	
	//貼文標題
	private String member_name;

	// 貼文類型
	private String member_account;
		
	
	public Post(int page, String file_path, int post_id, String post_title, String post_type, String post_description, int member_id, String member_name, String member_account) {
		
		this.page = page;
		this.file_path = file_path;
		this.post_id = post_id;
		this.post_title = post_title;
		this.post_type = post_type;
		this.post_description = post_description;
		this.member_id = member_id;
		this.member_name = member_name;
		this.member_account = member_account;
	}
	
public Post(String post_title, String post_type, String post_description, int member_id) {
		
		this.post_title = post_title;
		this.post_type = post_type;
		this.post_description = post_description;
		this.member_id = member_id;
	}
	


	public int getPage() {
		return this.page;
	}
	
	public String getFile_Path() {
		return this.file_path;
	}
	
	public int getPost_ID() {
		return this.post_id;
	}
	
	public String getPost_Title() {
		return this.post_title;
	}
	
	public String getPost_Type() {
		return this.post_type;
	}
	
	public String getPost_Description() {
		return this.post_description;
	}
	
	public int getMember_ID() {
		return this.member_id;
	}
	
	public String getMember_Name() {
		return this.member_name;
	}
	
	public String getMember_Account() {
		return this.member_account;
	}
	
	
	public JSONObject getData() {
		//透過JSONObject將該名會員所需之資料全部進行封裝
		JSONObject jso = new JSONObject();
		
		jso.put("page", getPage());
		jso.put("file_path", getFile_Path());
		jso.put("post_id", getPost_ID());
		jso.put("post_title", getPost_Title());
		jso.put("post_type", getPost_Type());
		jso.put("post_description", getPost_Description());
		jso.put("member_id", getMember_ID());
		jso.put("member_name", getMember_ID());
		jso.put("member_account", getMember_Account());

		return jso;
	}
	
}
