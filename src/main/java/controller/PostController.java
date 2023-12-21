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
import tools.JsonReader;

/**
 * Servlet implementation class PostController
 */
@WebServlet("/api/post.do")
@MultipartConfig
public class PostController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
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
		//System.out.println("membercontroller/doget");
		/** 透過JsonReader類別將Request之JSON格式資料解析並取回 */
		JsonReader jsr = new JsonReader(request);

		// String id = jsr.getParameter("id");
		String id = request.getParameter("post_id");
		System.out.println("jsr.getparameter id: " + id);

		/** 若直接透過前端AJAX之data以key=value之字串方式進行傳遞參數，可以直接由此方法取回資料 */
		// int id = -1;
		// id = jso.getInt("member_id");
		/** 判斷該字串是否存在，若存在代表要取回個別會員之資料，否則代表要取回全部資料庫內會員之資料 */
		if (id == null) {
			System.out.println("func: get all post");
			/** 透過MemberHelper物件之getAll()方法取回所有會員之資料，回傳之資料為JSONObject物件 */
			JSONObject query = mh.getAll();

			/** 新建一個JSONObject用於將回傳之資料進行封裝 */
			JSONObject resp = new JSONObject();
			resp.put("status", "400");
			resp.put("message", "所有貼文取得成功");
			resp.put("response", query);

			/** 透過JsonReader物件回傳到前端（以JSONObject方式） */
			jsr.response(resp, response);
		} else {
			System.out.println("func: get by id");
			/** 透過MemberHelper物件的getByID()方法自資料庫取回該名會員之資料，回傳之資料為JSONObject物件 */
			JSONObject query = mh.getByID(id);

			/** 新建一個JSONObject用於將回傳之資料進行封裝 */
			JSONObject resp = new JSONObject();
			resp.put("status", "200");
			resp.put("message", "貼文資料取得成功");
			resp.put("response", query);
			System.out.println("pctrller do get response:" + resp);
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
		

    	

        String uploadDir = "eclipse-workspace/SA_Project/src/main/webapp/filepath/";
        
        //String cla = this.getClass().getClassLoader().getResource(".").getPath();
        //String img_path = cla.replace("/WEB-INF/classes/", "/img/");
        //System.out.println(cla);

        //InputStream inputStream = filePart.getInputStream();
        //OutputStream outputStream = new FileOutputStream(new File(uploadDir, id+"."+extension));
        
        System.out.println("Shit");
        try {
    		
            Collection<Part> parts = request.getParts();
            System.out.println(parts);

            // 迭代處理每個部分
            for (Part part : parts) {
                // 如果是檔案部分
                if (part.getSubmittedFileName() != null) {
                    String fileName = getFileName(part);
                    // InputStream fileContent = part.getInputStream();
                    String randomCode = generateRandomCode();
                    try (InputStream fileContent = part.getInputStream()) {
                        String uploadDirectory = "eclipse-workspace/SA_Project/src/main/webapp/filepath/";
						// Construct the path where you want to save the file
                        String filePath = uploadDirectory  +   randomCode+ "_" + fileName  ;

                        // Save the file
                        Files.copy(fileContent, new File(filePath).toPath(), StandardCopyOption.REPLACE_EXISTING);

                        // Print the file path for verification (you can remove this in production)
                        System.out.println("File saved to: " + filePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                        // Handle exception appropriately
                    }
                    
                    // 在這裡進行檔案處理，例如儲存到伺服器上
                    // saveFile(fileContent, fileName);
                    System.out.println(fileName);
                    
                } else {
                    // 如果是其他表單欄位
                    String fieldName = part.getName();
                    String fieldValue = request.getParameter(fieldName);
                    System.out.println("Field Name: " + fieldName + ", Field Value: " + fieldValue);
                }
            }

            
 
        }catch (Exception ex) {
            System.out.println("Upload fail!");
            ex.printStackTrace(); // 輸出詳細錯誤信息
        }
        	
	}
	
	private String getSubmittedFileName(Part part) {
        for (String cd : part.getHeader("content-disposition").split(";")) {
            if (cd.trim().startsWith("filename")) {
                String fileName = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
                return fileName.substring(fileName.lastIndexOf('/') + 1).substring(fileName.lastIndexOf('\\') + 1);
            }
        }
        return null;
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
	
	private String getFileExtension(String fileName) {
	    return fileName.substring(fileName.lastIndexOf('.') + 1);
	}
	
//    private boolean isFilePart(Part part) {
//        // 檢查 Content-Disposition header 是否包含 "filename"
//        return part.getHeader("content-disposition").contains("filename");
//    }

    private String getFileName(Part part) {
        for (String content : part.getHeader("content-disposition").split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }

    private void saveFile(InputStream fileContent, String uploadFolder, String fileName) throws IOException {
        Path uploadPath = Paths.get(uploadFolder);
        Files.createDirectories(uploadPath);

        // 使用 NIO 保存檔案
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(fileContent, filePath, StandardCopyOption.REPLACE_EXISTING);
    }

}
