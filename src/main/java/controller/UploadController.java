package controller;

import java.io.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

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

import util.DBMgr;

@WebServlet("/api/upload.do")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2 MB
		maxFileSize = 1024 * 1024 * 10, // 10 MB
		maxRequestSize = 1024 * 1024 * 50 // 50 MB
)

public class UploadController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/** 儲存JDBC資料庫連線 */
	private Connection conn = null;

	/** 儲存JDBC預準備之SQL指令 */
	private PreparedStatement pres = null;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        try { // 取得檔案部分
        	Part filePart = request.getPart("file");
        	String id = request.getParameter("member_id");
            System.out.println(request);

            String uploadDir = "eclipse-workspace/SA_Project/src/main/webapp/img/";
            String extension = getFileExtension(filePart.getSubmittedFileName());
            //String cla = this.getClass().getClassLoader().getResource(".").getPath();
            //String img_path = cla.replace("/WEB-INF/classes/", "/img/");
            //System.out.println(cla);

            InputStream inputStream = filePart.getInputStream();
            OutputStream outputStream = new FileOutputStream(new File(uploadDir, id+"."+extension));
            
            Path targetPath = Path.of(uploadDir+id+"."+extension, id+"."+extension);
   
            byte[] buffer = new byte[1024];
            int bytesRead;
            
            while ((bytesRead = inputStream.read(buffer)) != -1) {
            	
                outputStream.write(buffer, 0, bytesRead);
            }
            //System.out.println(item.getName());
            
            
            try {
                /** 取得資料庫之連線 */
                conn = DBMgr.getConnection();
                /** SQL指令 */
                String sql = "Update `mydb`.`member` SET `member_img_path` = ? WHERE `member_id` = ?";
                /** 取得所需之參數 */
                
               

     
                
                /** 將參數回填至SQL指令當中 */
                
                pres = conn.prepareStatement(sql);
                pres.setString(1,"img/" + id + "." + extension );
                pres.setString(2, id);

                /** 執行更新之SQL指令並記錄影響之行數 */
                int row = -1;
                row = pres.executeUpdate();

                /** 紀錄真實執行的SQL指令，並印出 **/
                String exexcute_sql = null;
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
            
            
            System.out.println("Upload Success!");
            }catch(Exception ex){
                System.out.println(ex);
                System.out.println("Upload fail!");
            }

        }
    	
//    	try {
//            // 取得檔案部分
//            Part filePart = request.getPart("file");
//            String id = request.getParameter("member_id");
//            // 取得檔案名稱
//            String fileName = getSubmittedFileName(filePart);
//            //String fileName = getFileName(filePart);
//            System.out.println(fileName);
//
//            // 請指定您想要保存檔案的目標資料夾路徑
//            //String relativePath = "img/" ;
//            String uploadFolder = "112_SA_project_G2/img";
//            
//            //String savePath = getServletContext().getRealPath(relativePath);
//            //System.out.println(savePath);
//            filePart.write(uploadFolder + id);
//
//            // 使用 NIO 保存檔案
//            /* saveFile(filePart.getInputStream(), uploadFolder, fileName);
//            System.out.println(System.getProperty("user.dir"));*/
//            
//            response.getWriter().print("File uploaded successfully");
//        } catch (Exception e) {
//            response.getWriter().print("Failed to upload file");
//            e.printStackTrace();
//        }

      
    private String getSubmittedFileName(Part part) {
        for (String cd : part.getHeader("content-disposition").split(";")) {
            if (cd.trim().startsWith("filename")) {
                String fileName = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
                return fileName.substring(fileName.lastIndexOf('/') + 1).substring(fileName.lastIndexOf('\\') + 1);
            }
        }
        return null;
    }

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");

        // 如果找到了点且不在文件名的开头或结尾
        if (lastDotIndex != -1 && lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            // 返回点后的字符串，即文件扩展名
            return fileName.substring(lastDotIndex + 1);
        } else {
            // 如果找不到点或点在文件名的开头或结尾，则文件没有扩展名
            return "No Extension";
        }
    }
}
