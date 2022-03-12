<%@ page language="java" import="java.util.*"  pageEncoding="UTF-8"%>
<%@ page import="java.lang.*" %>
<%@ page import="java.util.Map.*" %>
<%@ page import="com.liantong.common.Constants"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
application.setAttribute("basePath", basePath);
String version=(String)application.getAttribute(Constants.Config.version);
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="content-type" content="text/html;charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>政务外网</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1, user-scalable=no">
    <meta name="description" content="">
    <meta name="author" content="">	               
</head>
<body>
	<div class="container">
		<div class="row">
			<div class="col-xs-3"></div>
			<div class="col-xs-9 not-found">
				<div>
					<h3>Welcome,Api System</h3>
					<p>
					<h3>version:<%=version%></h3>										
				</div>
			</div>
		</div>
	</div>
</body>
</html>