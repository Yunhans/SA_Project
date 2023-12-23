package controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;



import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.json.JSONObject;

import app.Member;
import app.MemberHelper;
import app.Post;

import app.UploadedFile;

import app.PostHelper;
import tools.JsonReader;

/**
 * Servlet implementation class PostController
 */
@WebServlet("/api/post.do")
@MultipartConfig
public class PostController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	/** mh，MemberHelper之物件與Member相關之資料庫方法（Sigleton） */
	private PostHelper ph = PostHelper.getHelper();
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PostController() {
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
//		JsonReader jsr = new JsonReader(request);
//		JSONObject jso = jsr.getObject();
		

		//Collection<Part> parts = request.getParts();
        //String uploadDir = "eclipse-workspace/SA_Project/src/main/webapp/filepath/";
       
        
        //String cla = this.getClass().getClassLoader().getResource(".").getPath();
        //String img_path = cla.replace("/WEB-INF/classes/", "/img/");
        //System.out.println(cla);

        //InputStream inputStream = filePart.getInputStream();
        //OutputStream outputStream = new FileOutputStream(new File(uploadDir, id+"."+extension));
        
        System.out.println("Shit");
        try {
    		
            Collection<Part> parts = request.getParts();
            System.out.println(parts);

            
            //貼文存進資料庫
            
          	
            
            int id = Integer.parseInt(request.getParameter("member_id"))  ;
            String post_title = request.getParameter("post_name");
            String post_type = request.getParameter("post_type");
            String post_description = request.getParameter("post_description");
        	
            Post p = new Post(post_title,post_type,post_description,id);
            int latestid =  ph.createPost(p);
            

            
            // 檔案存置資料庫
            for (Part part : parts) {
            	
      
                // 如果是檔案部分
                if (part.getSubmittedFileName() != null) {
                    String fileName = getFileName(part);
                    // InputStream fileContent = part.getInputStream();
                    String randomCode = generateRandomCode();
                    try (InputStream fileContent = part.getInputStream()) {
                    	
						// Construct the path where you want to save the file
                        String filePathname = randomCode+ "_" + fileName  ;
                        
                       //file path in folder
                        String uploadDirectory = "eclipse-workspace/SA_Project/src/main/webapp/filepath/" + filePathname;
                        // file path in sql
                        String sql_store = "filepath/" + filePathname;
                        
                        // Save the file
                        Files.copy(fileContent, new File(uploadDirectory).toPath(), StandardCopyOption.REPLACE_EXISTING);
                        
                        UploadedFile uf = new UploadedFile(sql_store,latestid,id);
                        
                      	ph.createfile(uf);
                        
                       
                        
                        // Print the file path for verification (you can remove this in production)
                        System.out.println("Filein (folder) saved to: " + uploadDirectory);
                    } catch (IOException e) {
                        e.printStackTrace();
                        // Handle exception appropriately
                    }
                    
                    // 在這裡進行檔案處理，例如儲存到伺服器上
                    // saveFile(fileContent, fileName);
                    System.out.println(fileName);
                    
                }
                
                     
            }
        	JSONObject resp = new JSONObject();

    		resp.put("message", "上傳成功!");
    		resp.put("status", "200");
    		
            
        }catch (Exception ex) {
        	
        	JSONObject resp = new JSONObject();

    		resp.put("message", "上傳失敗!");
    		resp.put("status", "400");
    		
        	
            System.out.println("Upload fail!");
            ex.printStackTrace(); // 輸出詳細錯誤信息
        }
        
    
        	
	}
	
	public void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/** 透過JsonReader類別將Request之JSON格式資料解析並取回 */
		JsonReader jsr = new JsonReader(request);
		JSONObject jso = jsr.getObject();

		/** 取出經解析到JSONObject之Request參數 */
		int post_id = jso.getInt("post_id");
		System.out.println("get id (from test_delete): " + post_id);

		/** 透過MemberHelper物件的deleteByID()方法至資料庫刪除該名會員，回傳之資料為JSONObject物件 */
		JSONObject query = ph.deletePostbyId(post_id);
		ph.deleteFile(post_id);

		/** 新建一個JSONObject用於將回傳之資料進行封裝 */
		JSONObject resp = new JSONObject();
		resp.put("message", "貼文移除成功！");
		resp.put("status", "200");

		resp.put("response", query);

		/** 透過JsonReader物件回傳到前端（以JSONObject方式） */
		jsr.response(resp, response);
	}
	
	//更新貼文
	public void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		/** 透過JsonReader類別將Request之JSON格式資料解析並取回 */
		JsonReader jsr = new JsonReader(request);
		JSONObject jso = jsr.getObject();

		//System.out.println(jso.getString("post_id"));

			/** 取出經解析到JSONObject之Request參數 */
			int post_id = jso.getInt("post_id");
			String post_title = jso.getString("post_title");
			String post_description = jso.getString("post_description");
			
			//System.out.println("postcrtl do put id:" + post_id);
			//System.out.println("postcrtl do put id:" + post_title);
			//System.out.println("postcrtl do put id:" + post_description);
			/** 透過Member物件的update()方法至資料庫更新該名會員資料，回傳之資料為JSONObject物件 */
			JSONObject data = ph.updatePost(post_id, post_title,post_description);

			/** 新建一個JSONObject用於將回傳之資料進行封裝 */
			JSONObject resp = new JSONObject();
			resp.put("status", "200");
			resp.put("message", "成功更改貼文");
			resp.put("response", data);

			/** 透過JsonReader物件回傳到前端（以JSONObject方式） */
			jsr.response(resp, response);
			// 更新會員自介、帳號、暱稱
		
	}
	
	
	
	
	

	private static String generateRandomCode() {
        // 定義亂數產生的範圍（這裡是0到9999）
        int min = 0;
        int max = 9999;

        // 創建 Random 物件
        Random random = new Random();

        // 生成亂碼
        int randomNum = random.nextInt((max - min) + 1) + min;

        // 將亂碼格式化為四位數字串
        String randomCode = String.format("%04d", randomNum);

        return randomCode;
    }

    private String getFileName(Part part) {
        for (String content : part.getHeader("content-disposition").split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }

//	private String getFileExtension(String fileName) {
//    return fileName.substring(fileName.lastIndexOf('.') + 1);
//}

//private boolean isFilePart(Part part) {
//    // 檢查 Content-Disposition header 是否包含 "filename"
//    return part.getHeader("content-disposition").contains("filename");
//}
    
//    private void saveFile(InputStream fileContent, String uploadFolder, String fileName) throws IOException {
//        Path uploadPath = Paths.get(uploadFolder);
//        Files.createDirectories(uploadPath);
//
//        // 使用 NIO 保存檔案
//        Path filePath = uploadPath.resolve(fileName);
//        Files.copy(fileContent, filePath, StandardCopyOption.REPLACE_EXISTING);
//    }

}
