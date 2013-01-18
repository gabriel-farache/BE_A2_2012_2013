<%-- 
    Document   : OpenUsers
    Created on : 14 janv. 2013, 00:02:52
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
                    <h2>Liste d'Utilisateurs</h2>
                    <article>

                        <form id="selectUsersForm" action="<c:url value="createMessage"/>" method="post" ></br>
                            ${listeDesUtilisateurs}
                            </br></br>
                            <input type="submit" class="submit_but" value="Valider" />
                        </form></br>


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
