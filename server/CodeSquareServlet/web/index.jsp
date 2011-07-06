<%-- 
    Document   : index
    Created on : Jul 5, 2011, 3:33:30 PM
    Author     : diivanand.ramalingam
--%>

<%
    String hostName = request.getServerName();
    String port = new Integer(request.getServerPort()).toString();
    System.out.println(hostName + ":" + port);
    String redirectURL = "http://" + hostName + ":" + port + "/CodeSquareServlet/FrontEndServlet";
    response.sendRedirect(redirectURL);
%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <a href="http://localhost:8080/CodeSquareServlet/FrontEndServlet"><h1>This shouldn't appear</h1></a>
    </body>
</html>
