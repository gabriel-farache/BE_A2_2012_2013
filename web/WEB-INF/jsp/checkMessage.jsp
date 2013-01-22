<%-- 
    Document   : checkMessage
    Created on : Jan 19, 2013, 8:22:46 PM
    Author     : gabriel
--%>

<!DOCTYPE html>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html lang="fr">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Bootstrap, from Twitter</title>
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
        <link href="css/bootstrap.css" rel="stylesheet">

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
            var lu = true;
            var fwd = true;
            var impo = true;
            var urg = true;
            var toAnw = true;
            
            function initLabels()
            {
                Project_Management_Presenter_Intern_Methods.messageHasStatusAssociatedWithAMember('<%=session.getAttribute("token")%>', '<%=session.getAttribute("idMessStatus")%>', 'READ', affectLu);
                Project_Management_Presenter_Intern_Methods.messageHasStatusAssociatedWithAMember('<%=session.getAttribute("token")%>', '<%=session.getAttribute("idMessStatus")%>', 'FORWARD', affectFwd);
                Project_Management_Presenter_Intern_Methods.messageHasStatusAssociatedWithAMember('<%=session.getAttribute("token")%>', '<%=session.getAttribute("idMessStatus")%>', 'IMPORTANT', affectImpo);
                Project_Management_Presenter_Intern_Methods.messageHasStatusAssociatedWithAMember('<%=session.getAttribute("token")%>', '<%=session.getAttribute("idMessStatus")%>', 'URGENT', affectUrg);
                Project_Management_Presenter_Intern_Methods.messageHasStatusAssociatedWithAMember('<%=session.getAttribute("token")%>', '<%=session.getAttribute("idMessStatus")%>', 'HAVE_TO_ANSWER', affectToAnw);
                checkNewMess();
            }
            
            function fillNbMess(data)
            {
                document.getElementById('nbNewMess').innerHTML = data;
            }
            
            function checkNewMess()
            {
                Project_Management_Presenter_Intern_Methods.getNbMessagesForStatus('<%=session.getAttribute("token")%>', '', fillNbMess);
            }
            function change_class(id) { 
                var btn = document.getElementById(id);
                if(id == "lu")
                {
                    if(!lu)
                    {
                        btn.className= "label label-inverse";
                    }
                    else
                    {
                        btn.className= "label";
                    }
                    lu = !lu;
                    Project_Management_Presenter_Intern_Methods.updateMessageStatusString('<%=session.getAttribute("token")%>', '<%=session.getAttribute("idMessStatus")%>', 'READ', lu, ok);
                    
                }
                else if(id == "forward")
                {
                    if(!fwd)
                    {
                        btn.className= "label label-success";
                    }
                    else
                    {
                        btn.className= "label";
                    }
                    fwd = !fwd;
                    Project_Management_Presenter_Intern_Methods.updateMessageStatusString('<%=session.getAttribute("token")%>', '<%=session.getAttribute("idMessStatus")%>', 'FORWARD', fwd, ok);
                    
                }
                else if(id == "important")
                {
                    if(!impo)
                    {
                        btn.className= "label label-warning";
                    }
                    else
                    {
                        btn.className= "label";
                    }
                    impo = !impo;
                    Project_Management_Presenter_Intern_Methods.updateMessageStatusString('<%=session.getAttribute("token")%>', '<%=session.getAttribute("idMessStatus")%>', 'IMPORTANT', impo, ok);
                    
                }
                else if(id == "urgent")
                {
                    if(!urg)
                    {
                        btn.className= "label label-important";
                    }
                    else
                    {
                        btn.className= "label";
                    }
                    urg = !urg;
                    Project_Management_Presenter_Intern_Methods.updateMessageStatusString('<%=session.getAttribute("token")%>', '<%=session.getAttribute("idMessStatus")%>', 'URGENT', urg, ok);
                    
                }
                else if(id == "toAnswer")
                {
                    if(!toAnw)
                    {
                        btn.className= "label label-info";
                    }
                    else
                    {
                        btn.className= "label";
                    }
                    toAnw = !toAnw;
                    Project_Management_Presenter_Intern_Methods.updateMessageStatusString('<%=session.getAttribute("token")%>', '<%=session.getAttribute("idMessStatus")%>', 'HAVE_TO_ANSWER', toAnw, ok);
                    
                }
                else {
                    alert(id);
                }
            } 
            function ok (data) { }
            
            function affectLu(data)
            {
                var btn = document.getElementById('lu');
                lu = data;
                if(lu)
                {
                    btn.className= "label label-inverse";
                }
                else
                {
                    btn.className= "label";
                }
            }
            function affectFwd(data)
            {
                var btn = document.getElementById('forward');
                fwd = data;
                if(fwd)
                {
                    btn.className= "label label-success";
                }
                else
                {
                    btn.className= "label";
                }
            }
            function affectImpo(data)
            {
                var btn = document.getElementById('important');
                impo = data;
                if(impo)
                {
                    btn.className= "label label-warning";
                }
                else
                {
                    btn.className= "label";
                }
            }
            function affectUrg(data)
            {
                var btn = document.getElementById('urgent');
                urg = data;
                if(urg)
                {
                    btn.className= "label label-important";
                }
                else
                {
                    btn.className= "label";
                }
            }
            function affectToAnw(data)
            {
                var btn = document.getElementById('toAnswer');
                toAnw = data;
                if(toAnw)
                {
                    btn.className= "label label-info";
                }
                else
                {
                    btn.className= "label";
                }
            }
            
            
            
        </script>
        <!---->
    </head>

    <body onload="initLabels();">

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
                            <li class="active"><a href="welcome">Acceuil</a></li>
                            <li class="dropdown">
                                <a id="drop1" href="#" role="button" class="dropdown-toggle" data-toggle="dropdown">T&acirc;ches <b class="caret"></b></a>
                                <ul class="dropdown-menu" role="menu" aria-labelledby="drop1">
                                    <li><a tabindex="-1" href="myTasks">Mes t&acirc;ches</a></li>
                                    <li><a tabindex="-1" href="listOfTasks">Toutes les t&acirc;ches</a></li>
                                    <li class="divider"></li>
                                    <% if (session.getAttribute("isAdmin") != null) {%>                                                                             
                                    <li><a tabindex="-1" href="createTask">Cr&eacute;er une nouvelle t&acirc;che</a></li>
                                    <% }%>
                                </ul>
                            </li>
                            <li class="dropdown">
                                <a id="drop2" href="#" role="button" class="dropdown-toggle" onclick="checkNewMess();" data-toggle="dropdown">Messagerie <b class="caret"></b></a>
                                <ul class="dropdown-menu" role="menu" aria-labelledby="drop1">
                                    <li><a tabindex="-1" href="inbox" >Bo&icirc;te de r&eacute;c&eacute;ption <span class="badge badge-info" ><b id="nbNewMess">${nbNewMessages}</b></span></a></li>
                                    <li><a tabindex="-1" href="createMessage">Envoyer un message</a></li>
                                </ul>
                            </li>
                            <li class="dropdown">
                                <a id="drop3" href="#" role="button" class="dropdown-toggle" data-toggle="dropdown">Groupes <b class="caret"></b></a>
                                <ul class="dropdown-menu" role="menu" aria-labelledby="drop1">
                                    <li><a tabindex="-1" href="listOfGroup">Consulter liste groupes</a></li>
                                    <li class="divider"></li>
                                    <% if (session.getAttribute("isAdmin") != null) {%>                                                                             
                                    <li><a tabindex="-1" href="createGroup">Ajouter un nouveau groupe</a></li>
                                    <% }%>                           
                                </ul>
                            </li>
                            <li class="dropdown">
                                <a id="drop4" href="#" role="button" class="dropdown-toggle" data-toggle="dropdown">Utilisateurs <b class="caret"></b></a>
                                <ul class="dropdown-menu" role="menu" aria-labelledby="drop1">
                                    <li><a tabindex="-1" href="listOfUser">Consulter liste utilisateurs</a></li>
                                    <li class="divider"></li>
                                    <% if (session.getAttribute("isAdmin") != null) {%>                                                                             
                                    <li><a tabindex="-1" href="createNewUser">Ajouter un nouvel utilisateur</a></li> 
                                    <% }%>                                  
                                </ul>
                            </li>
                        </ul>
                        <% if (session.getAttribute("token") != null) {%> 
                        <a href="deconnection"><input type="button" class="btn btn-danger pull-right" value="D&eacute;connexion"/></a>
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
                    <!-- Example row of columns -->
                    <div class="row-fluid"> 

                        <fieldset>
                            <legend>Auteur(s)</legend>
                            <div>
                                <p>${sender}</p>
                            </div>
                        </fieldset>
                        <fieldset>
                            <legend>Destinataire(s)</legend>
                            <div>
                                <p>Membre(s)</p> <hr>
                                <p>${recipientsM}</p>
                                <p>Groupe(s)</p> <hr>
                                <p>${recipientsG}</p>
                            </div>
                        </fieldset>
                        <fieldset>

                            <legend>Objet</legend>
                            <div>
                                <p>${title}</p>
                            </div>
                            <hr>
                            <div class="row-fluid">
                                <span id="lu" class="label" onclick="change_class('lu');">Lu</span>
                                <span id="forward" class="label" onclick="change_class('forward');">Transf&eacute;r&eacute;</span>
                                <span id="important" class="label" onclick="change_class('important');">Important</span>
                                <span id="urgent" class="label" onclick="change_class('urgent');">Urgent</span>
                                <span id="toAnswer" class="label" onclick="change_class('toAnswer');">A r&eacute;pondre</span>
                            </div>
                            <legend>Message</legend>
                            <div>
                                <p>${content}</p>
                            </div>
                        </fieldset>
                    </div>
                    <hr>
                    <div class="row">
                        <div class="span1 pull-left">
                            <a href="<c:url value="supprMessage?idMess=${idMess}"/>" class="btn  btn-danger" type="submit"/>Supprimer</a>
                        </div>
                        <div class="span1 pull-right">
                            <a href="<c:url value="createMessage?idMess=${idMess}&fromInbox=${fromInbox}"/>" class="btn  btn-success" type="submit"/>R&eacute;pondre</a>
                        </div>
                        <input type="hidden" id="idMessStatus" value="${idMess}"/>
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

