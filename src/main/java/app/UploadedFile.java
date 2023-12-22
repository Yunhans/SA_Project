package app;

import org.json.*;
import java.util.Calendar;

public class UploadedFile {
	
	//貼文id
	private int file_id;
		
	//貼文標題
	private String file_path;
		
	//貼文id
	private int post_id;

	//對應會員id
	private int member_id;

		
	
public UploadedFile(String file_path, int post_id,int member_id) {
	this.file_path = file_path;
	this.post_id = post_id;
	this.member_id = member_id;
}
	


	public int getfile_id() {
		return this.file_id;
	}
	
	public String getFile_Path() {
		return this.file_path;
	}
	
	public int getPost_ID() {
		return this.post_id;
	}
	
	public int getMember_ID() {
		return this.member_id;
	}
	

	
	public JSONObject getData() {
		//透過JSONObject將該名會員所需之資料全部進行封裝
		JSONObject jso = new JSONObject();
		
		jso.put("file_path", getFile_Path());
		jso.put("post_id", getPost_ID());
	
		jso.put("member_id", getMember_ID());
		jso.put("file_id", getfile_id());


		return jso;
	}
	
}
