<%-- 
    Document   : TaskCreated
    Created on : 17 janv. 2013, 14:47:34
    Author     : Lu
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

        <!-- Contenu Page -->


        <div class="blog_box">
            <div class="dat">
                <div class="cleare"></div>
            </div>
            <div class="text">
                <article>
                    <h2>Nouvelle Tâche</h2> 
                    <br>
                    ${result}
                    <br>
                </article>

                <!-- Fin Contenu Page -->

                <div class="drop_line" style="height: 30px;"></div>
                <div class="cleare" style="height:20px"></div>
                <div class="drop_line_footer"></div>
                <div id="footer">
                    <p>Projet Sopra | <a href="#">BE 4IR</a></p>
                </div>

            </div>	
        </div>



    </body>
</html>
