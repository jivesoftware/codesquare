<%-- 
    Document   : index
    Created on : Jul 20, 2011, 2:27:32 PM
    Author     : diivanand.ramalingam
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <%
            String hostName = request.getServerName();
            String port = new Integer(request.getServerPort()).toString();
            System.out.println(hostName + ":" + port);
            String redirectURL = "http://" + hostName + ":" + port + "/CodeSquare/BackEndServlet";
            response.sendRedirect(redirectURL);

        %>
    </body>
</html>
