<%-- 
    Document   : checkUser
    Created on : Jan 20, 2013, 12:20:47 AM
    Author     : gabriel
--%>

<!DOCTYPE html>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="display" uri="http://displaytag.sf.net" %>
<html lang="fr">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Consulter les informations d'un membre</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta name="description" content="">
        <meta name="author" content="">

        <!-- Le styles -->
        <link href="<c:url value="/css/bootstrap.css"/>" rel="stylesheet">
        <style type="text/css">
            body {
                padding-top: 60px;
                padding-bottom: 40px;
            }
            .sidebar-nav-fixed .accordion-body {
                max-height: 500px;
                overflow-y: auto;
                overflow-x:auto;
            }
            .navbar .accordion-body:before {
                content:normal;
            }
            .sidebar-nav-fixed {
                padding: 9px 0;
                position:fixed;
                left:20px;
                top:60px;
                width:250px;
            }

            .row-fluid > .span-fixed-sidebar {
                margin-left: 290px;
            }

        </style>

        <!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
        <!--[if lt IE 9]>
          <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
        <![endif]-->

        <!-- Fav and touch icons -->
        <link rel="apple-touch-icon-precomposed" sizes="144x144" href="ico/apple-touch-icon-144-precomposed.png">
        <link rel="apple-touch-icon-precomposed" sizes="114x114" href="ico/apple-touch-icon-114-precomposed.png">
        <link rel="apple-touch-icon-precomposed" sizes="72x72" href="ico/apple-touch-icon-72-precomposed.png">
        <link rel="apple-touch-icon-precomposed" href="ico/apple-touch-icon-57-precomposed.png">
        <link rel="shortcut icon" href="ico/favicon.png">
        <script type='text/javascript' src='/BE_A2_2012_2013/dwr/engine.js'></script>
        <script type='text/javascript' src='/BE_A2_2012_2013/dwr/interface/Project_Management_Presenter_Intern_Methods.js'></script>
        <script type='text/javascript' src='/BE_A2_2012_2013/dwr/util.js'></script>
        <script language="javascript">          
            function fillNbMess(data)
            {
                document.getElementById('nbNewMess').innerHTML = data;
            }
            
            function checkNewMess()
            {
                Project_Management_Presenter_Intern_Methods.getNbMessagesForStatus('<%=session.getAttribute("token")%>', '', fillNbMess);
            }
            
            function addRowHandlersInit() {
                addRowHandlersGroups('rowGroups', '/BE_A2_2012_2013/group/checkGroup', 'id_groupe', 0);
                addRowHandlersTasks('row','/BE_A2_2012_2013/task/checkTask', 'idTask', 0);
            }
            function addRowHandlersGroups(tableId, url, paramName, columnIndex) {
                var table = document.getElementById(tableId);
                var rows = table.getElementsByTagName("tr");
                for (i = 1; i < rows.length; i++) {      
                    rows[i].onclick = function () {
                        var cell = this.getElementsByTagName("td")[columnIndex];
                        var paramValue = cell.innerHTML;
                        window.open(url + "?" + paramName + "=" + paramValue,'_blank');
                    };
                }
            }
            
            function addRowHandlersTasks(tableId, url, paramName, columnIndex) {
                var table = document.getElementById(tableId);
                var rows = table.getElementsByTagName("tr");

                for (i = 1; i < rows.length; i++) {
                

                    if (rows[i].getElementsByTagName("td")[3].innerHTML == 'URGENT')
                    {
                        rows[i].className = 'error';
                    }
                    else if (rows[i].getElementsByTagName("td")[3].innerHTML == 'OPEN')
                    {
                        rows[i].className = 'success';
                    }
                    else if (rows[i].getElementsByTagName("td")[3].innerHTML == 'CLOSED')
                    {
                        rows[i].className = 'default';
                    }
                    else
                    {
                        rows[i].className = 'warning';
                    }
                    rows[i].onclick = function () {
                        var cell = this.getElementsByTagName("td")[columnIndex];
                        var paramValue = cell.innerHTML;
                        window.open(url + "?" + paramName + "=" + paramValue,'_blank');
                    };
                }
            }
        </script>
    </head>

    <body onload="addRowHandlersInit()">

        <div class="navbar navbar-inverse navbar-fixed-top">
            <div class="navbar-inner">
                <div class="container-fluid">
                    <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                    </a>
                    <a class="brand pull-left" href="#">PESO</a>
                    <div class="nav-collapse collapse">
                        <ul class="nav">
                            <li><a href="/BE_A2_2012_2013/welcome">Acceuil</a></li>
                            <li class="dropdown">
                                <a id="drop1" href="#" role="button" class="dropdown-toggle" data-toggle="dropdown">T&acirc;ches <b class="caret"></b></a>
                                <ul class="dropdown-menu" role="menu" aria-labelledby="drop1">
                                    <li><a tabindex="-1" href="/BE_A2_2012_2013/task/myTasks">Mes t&acirc;ches</a></li>
                                    <li><a tabindex="-1" href="/BE_A2_2012_2013/task/listOfTasks">Toutes les t&acirc;ches</a></li>
                                    <li class="divider"></li>
                                    <% if (session.getAttribute("isAdmin") != null) {%>                                                                             
                                    <li><a tabindex="-1" href="/BE_A2_2012_2013/task/createTask">Cr&eacute;er une nouvelle t&acirc;che</a></li>
                                    <% }%>
                                </ul>
                            </li>
                            <li class="dropdown">
                                <a id="drop2" href="#" role="button" class="dropdown-toggle" onclick="checkNewMess();" data-toggle="dropdown">Messagerie <b class="caret"></b></a>
                                <ul class="dropdown-menu" role="menu" aria-labelledby="drop1">
                                    <li><a tabindex="-1" href="/BE_A2_2012_2013/message/inbox" >Bo&icirc;te de r&eacute;c&eacute;ption <span class="badge badge-info" ><b id="nbNewMess"></b></span></a></li> 
                                    <li><a tabindex="-1" href="/BE_A2_2012_2013/message/createMessage">Envoyer un message</a></li>
                                </ul>
                            </li>
                            <li class="dropdown">
                                <a id="drop3" href="#" role="button" class="dropdown-toggle" data-toggle="dropdown">Groupes <b class="caret"></b></a>
                                <ul class="dropdown-menu" role="menu" aria-labelledby="drop1">
                                    <li><a tabindex="-1" href="/BE_A2_2012_2013/group/listOfGroup">Consulter liste groupes</a></li>
                                    <li class="divider"></li>
                                    <% if (session.getAttribute("isAdmin") != null) {%>                                                                             
                                    <li><a tabindex="-1" href="/BE_A2_2012_2013/group/createGroup">Ajouter un nouveau groupe</a></li>
                                    <% }%>                           
                                </ul>
                            </li>
                            <li class="dropdown active">
                                <a id="drop4" href="#" role="button" class="dropdown-toggle" data-toggle="dropdown">Utilisateurs <b class="caret"></b></a>
                                <ul class="dropdown-menu" role="menu" aria-labelledby="drop1">
                                    <li><a tabindex="-1" href="/BE_A2_2012_2013/user/listOfUser">Consulter liste utilisateurs</a></li>
                                    <li class="divider"></li>
                                    <li><a tabindex="-1" href="/BE_A2_2012_2013/user/updateProfileInfos">Mettre &agrave; jour mon profil</a></li>
                                    <% if (session.getAttribute("isAdmin") != null) {%>                                                                             
                                    <li><a tabindex="-1" href="/BE_A2_2012_2013/user/createNewUser">Ajouter un nouvel utilisateur</a></li> 
                                    <% }%>                                  
                                </ul>
                            </li>
                        </ul>
                        <% if (session.getAttribute("token") != null) {%> 
                        <a href="/BE_A2_2012_2013/deconnection"><input type="button" class="btn btn-danger pull-right" value="D&eacute;connexion"/></a>
                            <%} else {%>
                        <ul class="nav pull-right">
                            <li class="dropdown">
                                <a id="drop5" href="#" role="button" class="dropdown-toggle" data-toggle="dropdown">Se connecter <b class="caret"></b></a>
                                <ul class="dropdown-menu" role="menu" aria-labelledby="drop1">
                                    <li>
                                        <form class="form-actions" action="welcome" method="post">
                                            <div class="control-group">
                                                <label class="control-label" for="utilisateur">Nom d'utilisateur</label>
                                                <div class="controls">
                                                    <input class="span2" type="text" placeholder="Email" id="utilisateur" name="utilisateur">
                                                </div>
                                            </div>
                                            <div class="control-group">
                                                <label class="control-label" for="pass">Mot de passe</label>
                                                <div class="controls">
                                                    <input class="span2" type="password" placeholder="password" id="pass" name="pass">
                                                </div>
                                            </div>
                                            <div class="control-group">
                                                <div class="controls">
                                                    <button type="submit" class="btn btn-primary">Se connecter</button>
                                                </div>
                                            </div>
                                        </form>
                                    </li>
                                </ul>
                            </li>
                        </ul>
                        <% }%>

                    </div><!--/.nav-collapse -->
                </div>
            </div>
        </div>

        <div class="container-fluid">
            <div class="row-fluid">
                <div class="span2">
                    <div class="well sidebar-nav-fixed">
                        <div class="accordion" id="accordion">
                            <div class="accordion-group">
                                <div class="accordion-heading">
                                    <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion" href="#tasksDetail">
                                        Mes t&acirc;ches
                                    </a>
                                </div>
                                <div id="tasksDetail" class="accordion-body  collapse" style="height: 0px; ">
                                    <div class="accordion-inner">
                                        <ul>
                                            ${myTasks}
                                        </ul>
                                    </div>
                                </div>
                            </div>

                            <div class="accordion-group">
                                <div class="accordion-heading">
                                    <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion" href="#messages">
                                        Messagerie
                                    </a>
                                </div>
                                <div id="messages" class="accordion-body collapse" style="height: 0px; ">
                                    <div class="accordion-inner">
                                        <ul>
                                            ${myMessages}
                                        </ul>
                                    </div>
                                </div>
                            </div>

                            <div class="accordion-group">
                                <div class="accordion-heading">
                                    <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion" href="#users">
                                        Utilisateurs
                                    </a>
                                </div>
                                <div id="users" class="accordion-body collapse" style="height: 0px; ">
                                    <div class="accordion-inner">
                                        <ul>
                                            ${users}
                                        </ul>
                                    </div>
                                </div>
                            </div>

                            <div class="accordion-group">
                                <div class="accordion-heading">
                                    <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion" href="#groups">
                                        Groupes
                                    </a>
                                </div>
                                <div id="groups" class="accordion-body collapse" style="height: 0px; ">
                                    <div class="accordion-inner">
                                        <ul>
                                            ${groups}
                                        </ul>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div><!--/.well -->
                </div><!--/span-->

                <div class="span9 offset2">
                    <div class="hero-unit">
                        <h1>Consulter le profil d'un membre</h1>
                    </div>
                    <!-- Example row of columns -->
                    <div class="row-fluid"> 
                        <fieldset>
                            <legend>ID</legend>
                            <p>${id}</p>
                        </fieldset>
                        <fieldset>
                            <legend>Nom(s)</legend>
                            <p>${nom}</p>
                        </fieldset>
                        <fieldset>
                            <legend>Prénom</legend>
                            <p>${prenom}</p>
                        </fieldset>
                        <fieldset>
                            <legend>Adresse mail</legend>
                            <p>${mail}</p>
                        </fieldset>

                        <fieldset>
                            <legend>T&acirc;che(s)</legend>
                            <display:table class="table table-hover" id="row" name="tasksTable" defaultsort="3" defaultorder="descending" decorator="dataObjects.TaskDecorator" pagesize="5" requestURI="">
                                <display:column property="id" title="ID" sortable="true" />
                                <display:column property="project_topic"  title ="Projet" sortable="true" />
                                <display:column property="title" title="Nom de la tâche" sortable="true" />
                                <display:column property="status" title="Statut" sortable="true" />
                                <display:column property="creationDate" title="Date début" sortable="true" />
                                <display:column property="dueDate" title="Date limite" sortable="true" />
                            </display:table>
                        </fieldset>
                        <fieldset>
                            <legend>Groupe(s)</legend>
                            <display:table class="table table-hover" id="rowGroups" name="groupsTable" defaultsort="1" defaultorder="descending" decorator="" pagesize="5" requestURI="">
                                <display:column property="id_group" title="ID" sortable="true" />
                                <display:column property="group_name"  title ="Nom du groupe" sortable="true" />
                                <display:column property="descr" title="Description du groupe" sortable="false" />
                            </display:table>
                        </fieldset>

                        <% if (session.getAttribute("isAdmin") != null) {%> 
                        <div class="span1 pull-right">
                            <a href="updateUser?idUser=${id}"><input class="btn btn-primary" type="submit" value="Modifier" /></a>
                        </div>
                        <% }%>
                    </div>
                    <hr>
                    <footer>
                        <p>&copy; PESO 2012</p>
                    </footer>
                </div>
                <!-- /container -->
            </div>
            <!-- Le javascript
            ================================================== -->
            <!-- Placed at the end of the document so the pages load faster -->
            <script src="<c:url value="/resources/js/jquery.js"/>"></script>
            <script src="<c:url value="/resources/js/bootstrap.js"/>"></script>
            <script src="<c:url value="/resources/js/bootstrap-transition.js"/>"></script>
            <script src="<c:url value="/resources/js/bootstrap-alert.js"/>"></script>
            <script src="<c:url value="/resources/js/bootstrap-modal.js"/>"></script>
            <script src="<c:url value="/resources/js/bootstrap-dropdown.js"/>"></script>
            <script src="<c:url value="/resources/js/bootstrap-scrollspy.js"/>"></script>
            <script src="<c:url value="/resources/js/bootstrap-tab.js"/>"></script>
            <script src="<c:url value="/resources/js/bootstrap-tooltip.js"/>"></script>
            <script src="<c:url value="/resources/js/bootstrap-popover.js"/>"></script>
            <script src="<c:url value="/resources/js/bootstrap-button.js"/>"></script>
            <script src="<c:url value="/resources/js/bootstrap-collapse.js"/>"></script>
            <script src="<c:url value="/resources/js/bootstrap-carousel.js"/>"></script>
            <script src="<c:url value="/resources/js/bootstrap-typeahead.js"/>"></script>
        </div>
    </body>
</html>
