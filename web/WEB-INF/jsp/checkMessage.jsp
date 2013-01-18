<%-- 
    Document   : CheckMessage
    Created on : 14 janv. 2013, 10:43:25
    Author     : Doudi
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
    <head>
        <!-- début header  -->
        <jsp:include page="header.jsp" >
            <jsp:param name="title" value="PESO - Administration" />
        </jsp:include>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="<c:url value="/css/main.css"/>" rel="stylesheet" type="text/css" />
        <!-- fin header -->
    </head>
    <body
        <!-- Menu Gauche -->
        <div id="content" >
            <jsp:include page="menu.jsp" >
                <jsp:param name="utilisateursMenu" value="" />
                <jsp:param name="groupesMenu" value="" />
                <jsp:param name="MessagesMenu" value="class=\"but_now\"" />
                <jsp:param name="tâchesMenu" value="" />
                <jsp:param name="XMLMenu" value="" />
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
                            <h2>Boite de réception</h2>

                            <div class="drop_line" style="height: 3px;"></div>
                            <div id="box_form">    
                                </br></br>
                                <fieldset>
                                    <legend class="rubrique">Auteur(s)</legend>
                                    <div class="form_line">
                                        <label>${sender}</label>
                                    </div>
                                </fieldset>

                                </br></br>
                                <fieldset>
                                    <legend class="rubrique">Message</legend>
                                    <div class="form_line">
                                        <label>${title}"</label>
                                    </div>
                                    <div class="form_line">
                                        <textarea value="${content}" readonly ="true" required></textarea>
                                    </div>

                                    <div class="form_line" style="padding-top: 20px;">
                                        <form action="<c:url value="supprMessage"/> method ="GET">
                                                <input type="text" value ="${idMess}" id="idMess" name ="idMess" hidden="true" />
                                                <input type="submit" class="submit_but" value="Supprimer" />
                                            </form>
                                        <span class="form_line" style="padding-top: 5px;">
                                            <form action="<c:url value="createMessage"/> method ="GET">
                                                <input type="text" value ="${sender}" id="idSender" name ="idSender" hidden="true" />
                                                <input type="submit" class="submit_but cleare_but" value="Répondre" />
                                            </form>
                                        </span>
                                    </div>
                                </fieldset>


                                <!-- Fin Contenu Page -->

                                <div class="drop_line" style="height: 30px;"></div>
                                <div class="cleare" style="height:20px"></div>
                                <div class="drop_line_footer"></div>
                                <div id="footer">
                                    <p>Projet Sopra | <a href="#">BE 4IR</a></p>
                                </div>

                            </div>	
                        </div>
                    </div>
                </div>
            </div>
        </div>

    </body>
</html>
