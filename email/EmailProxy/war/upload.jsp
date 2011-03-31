<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>

<%
BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
%>

<html>

<head>
<link type="text/css" rel="stylesheet" href="/stylesheets/main.css" />
<meta name="robots" content="none" />
<title>BundSoft.net - Upload Page</title>
</head>

<body>
<p><span><b><%=request.getParameter("message") %></b></span></p>
<p><span>Upload Files:</span></p>
<form name="upload" action="<%= blobstoreService.createUploadUrl("/upload") %>" method="post" enctype="multipart/form-data">
Name:<input type="text" name="name"><br/>
Desc:<textarea name="desc" cols=30></textarea><br/>
File 1:<input type="file" name="file1"><br>
<input type="submit" name="submit" value="Upload Files">
</form>
</body>

</html> 