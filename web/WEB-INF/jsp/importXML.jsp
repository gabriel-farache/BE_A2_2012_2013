<%-- 
    Document   : ImportXML
    Created on : 13 janv. 2013, 19:52:58
    Author     : Doudi
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html">
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
        <!-- Menu Gauche -->
        <div id="content" >
            <jsp:include page="menu.jsp" >
                <jsp:param name="utilisateursMenu" value="" />
                <jsp:param name="groupesMenu" value="" />
                <jsp:param name="MessagesMenu" value="" />
                <jsp:param name="tâchesMenu" value="" />
                <jsp:param name="XMLMenu" value="class=\"but_now\"" />
            </jsp:include>

            <!-- Fin Menu Gauche -->

            <!-- Contenu Page -->

            <div id="right_blog">
                <div class="box">
                    <div class="blog_box">
                        <div class="dat">
                            <div class="cleare"></div>
                        </div>
                        <div class="text">
                            <h2 id="menuImportXML">Importer un fichier XML</h2> 
                            <h2>${resultImport}</h2>
                        </div>
                        <article>
                            </br>
                            <form action="<c:url value="miam"/>" method="POST" enctype="multipart/form-data">
                                <input type="file" name="selectFile"/><br/>
                                <input name="ImporterXML" id="ImporterXML" type="submit" value="Importer" class="submit_but"/>
                            </form>
                        </article>
                    </div>

                    <!-- Fin Contenu Page -->

                    <div class="drop_line" style="height: 30px;"></div>
                    <div class="cleare" style="height:20px"></div>
                    <div class="drop_line_footer"></div>
                    <div id="footer">
                        <p>Projet Sopra | <a href="#">BE 4IR</a> </p>
                    </div>

                </div>	
            </div>
        </div>

    </body>
</html>
