<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
 pageEncoding="ISO-8859-1"%>
<%@ page import="java.io.*,java.util.*, javax.servlet.*"%>
<%@ page import="javax.servlet.http.*"%>
<%@ page import="org.apache.commons.fileupload.*"%>
<%@ page import="org.apache.commons.fileupload.disk.*"%>
<%@ page import="org.apache.commons.fileupload.servlet.*"%>
<%@ page import="org.apache.commons.io.output.*"%>
<%@ page import="com.biom.DriverProgram" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>File Upload</title>
</head>
<body>
 <%
  String UPLOAD_DIRECTORY = "upload";
  File file;
 int maxFileSize = 5000 * 1024;
 int maxMemSize = 5000 * 1024;
 //String filePath = "~/";
 
 String filePath = "/home/srm/ew2/BIOMaaS/src/main/webapp/WEB-INF"+ File.separator + UPLOAD_DIRECTORY;
String uploadPath=filePath;
 File uploadDir = new File(uploadPath);
if (!uploadDir.exists()) uploadDir.mkdir();
 String contentType = request.getContentType();
 if ((contentType.indexOf("multipart/form-data") >= 0)) {
  DiskFileItemFactory factory = new DiskFileItemFactory();
  factory.setSizeThreshold(maxMemSize);
  factory.setRepository(new File("upload"));
  ServletFileUpload upload = new ServletFileUpload(factory);
  upload.setSizeMax(maxFileSize);
  try {
   List fileItems = upload.parseRequest(request);
   Iterator i = fileItems.iterator();
   
   while (i.hasNext() ) {
  FileItem fi = (FileItem) i.next();
  if (!fi.isFormField() && fi.getSize()>0) {
	  
   String fieldName = fi.getFieldName();
   String fileName = fi.getName();
   boolean isInMemory = fi.isInMemory();
   long sizeInBytes = fi.getSize();
   if(fileName!=null || fileName!=""){
   file = new File(filePath +File.separator+ fileName);
   out.write(uploadPath);
   fi.write(file);
   
   
   
   }
  }
   }
   
   
  } catch (Exception ex) {
   System.out.println(ex);
  }
 } 
 %>
 <%=DriverProgram.takeinput(uploadPath) %>
 
<!--  public static void unzipFolder(Path source, Path target) throws IOException {

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(source.toFile()))) {

            // list files in zip
            ZipEntry zipEntry = zis.getNextEntry();

            while (zipEntry != null) {

                boolean isDirectory = false;
                // example 1.1
                // some zip stored files and folders separately
                // e.g data/
                //     data/folder/
                //     data/folder/file.txt
                if (zipEntry.getName().endsWith(File.separator)) {
                    isDirectory = true;
                }

                Path newPath = zipSlipProtect(zipEntry, target);

                if (isDirectory) {
                    Files.createDirectories(newPath);
                } else {

                    // example 1.2
                    // some zip stored file path only, need create parent directories
                    // e.g data/folder/file.txt
                    if (newPath.getParent() != null) {
                        if (Files.notExists(newPath.getParent())) {
                            Files.createDirectories(newPath.getParent());
                        }
                    }

                    // copy files, nio
                    Files.copy(zis, newPath, StandardCopyOption.REPLACE_EXISTING);

                    // copy files, classic
                    /*try (FileOutputStream fos = new FileOutputStream(newPath.toFile())) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }*/
                }

                zipEntry = zis.getNextEntry();

            }
            zis.closeEntry();

        }

    }

    // protect zip slip attack
    public static Path zipSlipProtect(ZipEntry zipEntry, Path targetDir)
        throws IOException {

        // test zip slip vulnerability
        // Path targetDirResolved = targetDir.resolve("../../" + zipEntry.getName());

        Path targetDirResolved = targetDir.resolve(zipEntry.getName());

        // make sure normalized file still has targetDir as its prefix
        // else throws exception
        Path normalizePath = targetDirResolved.normalize();
        if (!normalizePath.startsWith(targetDir)) {
            throw new IOException("Bad zip entry: " + zipEntry.getName());
        }

        return normalizePath;
    }
    import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
 <dependency>
      <groupId>net.lingala.zip4j</groupId>
      <artifactId>zip4j</artifactId>
      <version>2.6.1</version>
  </dependency>
 
 -->
 </body>
</html>