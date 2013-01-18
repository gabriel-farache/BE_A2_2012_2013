<%-- 
    Document   : MessageCreated
    Created on : 17 janv. 2013, 11:48:44
    Author     : Doudi
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
    <head>

        <!-- début header -->
        <jsp:include page="header.jsp" >
            <jsp:param name="title" value="PESO - Administration" />
        </jsp:include>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="<c:url value="/css/main.css"/>" rel="stylesheet" type="text/css" />
        <!-- fin header -->
    </head>
    <body>   


        <div id="popUpMenu">    	

            <div class="blog_box">
                <div class="dat">
                    <div class="cleare"></div>
                </div>
                <div class="text">
                    <h2>Nouveau Message</h2>
                    <article>

                        <p>
                        <h3>Votre message a bien été envoyé</h3>

                        </p>

                    </article>   
                </div> 


            </div>


            <div class="cleare" style="height:20px"></div>
            <div class="drop_line_footer"></div>
            <div id="footer">

                <p>Projet Sopra | <a href="#">BE 4IR</a> </p>


            </div>
        </div>	
    </body>
</html>
