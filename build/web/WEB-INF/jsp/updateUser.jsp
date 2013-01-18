<%-- 
    Document   : UpdateUser
    Created on : 14 janv. 2013, 11:28:55
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
                    <h2>Fiche Utilisateur</h2>
                    <article>
                        <br />
                        <form action="<c:url value=""/>" method="post">
                            <fieldset>   
                                <p>
                                    <label for="id">ID</label>
                                    <label for="id">${id}</label>
                                    
                                    </br>
                                    </br>
                                    <label for="nom">Nom</label>
                                    <label for="nom">${nom}</label>
                                    
                                    </br>
                                    </br>
                                    <label for="prenom">Prénom</label>
                                    <label for="prenom">${prenom}</label>
                                    
                                    </br></br>
                                    <label for="mail">Adresse mail</label>
                                    <label for="mail">${mail}</label>
                                    
          
                                    </br></br>
                                    <input type="submit" class="submit_but" value="Valider" />

                                </p>
                            </fieldset>   
                        </form>
                    </article>
                </div>




            </div>
            <div class="cleare"></div>



            <div class="cleare" style="height:20px"></div>
            <div class="drop_line_footer"></div>
            <div id="footer">

                <p>Projet Sopra | <a href="#">BE 4IR</a> </p>
            </div>
        </div>	
    </body>
</html>