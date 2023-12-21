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

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		// TODO Auto-generated method stub
    	try {
            // 取得檔案部分
            Part filePart = request.getPart("file");
//            String id = request.getParameter("member_id");
            // 取得檔案名稱
            String fileName = getSubmittedFileName(filePart);
            
            String relativePath = "img/" ;
            String savePath = getServletContext().getRealPath(relativePath);
            System.out.println(savePath);
            filePart.write(savePath + File.separator + fileName);
            
            response.getWriter().print("File uploaded successfully");
        } catch (Exception e) {
            response.getWriter().print("Failed to upload file");
            e.printStackTrace();
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
