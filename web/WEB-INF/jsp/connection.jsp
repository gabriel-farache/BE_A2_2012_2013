<%-- 
  Document   : Connection
  Created on : 13 janv. 2013, 19:52:58
  Author     : Fangli
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="<c:url value="/css/main.css"/>" rel="stylesheet" type="text/css" /> 
         <!-- début header -->
        <jsp:include page="header.jsp" >
            <jsp:param name="title" value="PESO - Connexion" />
        </jsp:include>
        <!-- fin header -->
    </head>
    <body>
       


        <!--Contenu -->
        </br></br></br></br>

        <h3>Connexion</h3>
        <form id="connexionForm" action="<c:url value="welcome"/>" method="post">
            <p></br>
                <label for="utilisateur">Utilisateur</label> : <input type="text" name="utilisateur" id="utilisateur" placeholder="Login"/>
                <br>
                <label for="pass">Mot de passe</label> : <input type="password" name="pass" id="pass" placeholder="Password" />
                </br></br>
                <input type="submit" value="Connexion" />
            </p>
        </form>
    </body>

</html>