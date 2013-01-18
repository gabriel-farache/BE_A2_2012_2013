<%-- 
    Document   : Menu
    Created on : 13 janv. 2013, 23:21:42
    Author     : Doudi
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div id="left">
    <div id="buttons" class="box_left">
      <a href="<c:url value="welcome"/>" title="">Accueil</a>
      <a href="<c:url value="adminListOfUsers"/>" title="">Utilisateurs</a>
      <a href="<c:url value="adminListOfGroups"/>" title="">Groupes</a>
      <a href="<c:url value="adminListOfMessages"/>" title="">Messages</a>
      <a href="<c:url value="adminListOfTasks"/>" title="">Tâches</a>
      <a href="<c:url value="importXML"/>" title="">XML</a>
    </div>

    <div style="height: 10px; width: 100%;"></div>
    <%@ include file="searchMenu.jsp" %>
                
</div>
