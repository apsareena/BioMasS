<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
 <%

 String UPLOAD_DIRECTORY = "upload";
String filename = request.getParameter("fname");
//String filename="srm.pem";
String filepath = "/home/srm/ew2/BIOMaaS/src/main/webapp/WEB-INF/" + UPLOAD_DIRECTORY+"/";
 response.setContentType("APPLICATION/OCTET-STREAM");

 response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\""); 
java.io.FileInputStream fileInputStream = new java.io.FileInputStream(filepath + filename);
 int i;
 
 while ((i = fileInputStream.read()) != -1) {
  out.write(i);
 }
 fileInputStream.close();
 out.write("Successfully Downloaded");
 %>
</body>
</html>