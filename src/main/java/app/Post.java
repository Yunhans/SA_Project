package app;

import org.json.*;
import java.util.Calendar;

public class Post {
	
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
	
	
	public Post(int post_id, String post_title, String post_description, String post_type, int member_id) {
		this.post_id = post_id;
		this.post_title = post_title;
		this.post_type = post_type;
		this.post_description = post_description;
		this.member_id = member_id;

	}
	
	public int getPost_ID() {
		return this.post_id;
	}
	
	public String getPost_Title() {
		return this.post_title;
	}
	
	public String getpost_description() {
		return this.post_description;
	}
	
	public String getPost_Type() {
		return this.post_type;
	}
	
	public int getMember_ID() {
		return this.member_id;
	}
	
	
	public JSONObject getData() {
		//透過JSONObject將該名會員所需之資料全部進行封裝
		JSONObject jso = new JSONObject();
		
		jso.put("post_id", getPost_ID());
		jso.put("post_title", getPost_Title());
		jso.put("post_description", getpost_description());
		jso.put("post_type", getPost_Type());
		jso.put("member_id", getMember_ID());

		return jso;
	}
	
}
