<%-- 
    Document   : adminListOfGroups
    Created on : 13 janv. 2013, 19:52:58
    Author     : Fangli
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
    <head>
        <!-- début header -->
        <jsp:include page="header.jsp" >
            <jsp:param name="title" value="PESO - Administration des groupes" />
        </jsp:include>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="<c:url value="/css/main.css"/>" rel="stylesheet" type="text/css" />
        <!-- fin header -->
    </head>
    <body>
        <!-- Menu Gauche -->
        <div id="content" >
            <jsp:include page="menu.jsp" >
                <jsp:param name="utilisateursMenu" value="" />
                <jsp:param name="groupesMenu" value="class=\"but_now\"" />
                <jsp:param name="MessagesMenu" value="" />
                <jsp:param name="tâchesMenu" value="" />
                <jsp:param name="XMLMenu" value="" />
            </jsp:include>

            <!-- Fin Menu Gauche -->


            <div id="right_blog">    	
                <div class="box">
                    <div class="blog_box">
                        <div class="dat">
                            <div class="cleare"></div>
                        </div>
                        <div class="text">
                            <h2>Liste des Groupes</h2>
                            <article>

                                <form action="<c:url value="#"/> method="post" ></br>
                                    ${groupList}
                                </form></br>
                                <input type="${display}" class="submit_but" value="Créer" href ="createGroup" />

                            </article>   
                        </div> 
                    </div>
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
