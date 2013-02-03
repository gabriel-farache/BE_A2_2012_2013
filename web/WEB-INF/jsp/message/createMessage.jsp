<%-- 
    Document   : createMessage
    Created on : Jan 19, 2013, 9:34:26 PM
    Author     : gabriel
--%>

<!DOCTYPE html>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html lang="fr">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Envoyer un nouveau message</title>
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
            function fillNbMess(data)
            {
                document.getElementById('nbNewMess').innerHTML = data;
            }
            
            function checkNewMess()
            {
                Project_Management_Presenter_Intern_Methods.getNbMessagesForStatus('<%=session.getAttribute("token")%>', '', fillNbMess);
            }
            
            function lookupM(input) {
                Project_Management_Presenter_Intern_Methods.getDataFromDB('T_Membre', 'NOM', 'PRENOM',  input, 'IDMEMBRE', callbackM);
                removeListM();
                verifyBeforeSend();
            }

            function callbackM(msg) {

                if (msg.length > 0) {
                    document.getElementById("suggestionsM").style.display = "block";
                    var sListM = document.getElementById("suggestionsListM");

                    var ulM = document.createElement('ul');

                    for (var i = 0; i < msg.length; i++){
                        var liM = document.createElement('li'); 
                        liM.innerHTML = msg[i];
                        liM.onclick = bindFunctionM(msg[i]);
                        ulM.appendChild(liM);
                    }

                    sListM.appendChild(ulM);
                }
            }

            function bindFunctionM(txt) {
                return function () {fillTextFieldM(txt);};
            }

            function fillTextFieldM(txt) {
                
                document.getElementById("choixUtilsM").appendChild(document.createTextNode(txt+", ")); //
                document.getElementById("choixUtilsMBox").value="";  
                document.getElementById("suggestionsM").style.display = "none";
                document.getElementById("labelleM").innerHTML  += "<span class=\"label label-info\" id=\""+txt+"\" onclick=\"decoche('"+txt+"');\">"+txt+"  <input type=\"checkbox\"  name=\"choixUtilsMChk\" id=\""+txt+"_chk\" value=\""+txt+"\" checked=\"checked\" hidden ></span>   ";
                removeListM();
            }

            function removeListM() {
                var sListM = document.getElementById("suggestionsListM"); 
                var childrenM = sListM.childNodes; 
                for (var i = 0; i < childrenM.length; i++) {
                    sListM.removeChild(childrenM[i]);
                }
                verifyBeforeSend();
            }
            
            function lookupG(input) {
                Project_Management_Presenter_Intern_Methods.getDataFromDB('T_Groupe', 'NOM', input, 'IDGROUPE', callbackG);
                removeListG();
                verifyBeforeSend();
                
            }

            function callbackG(msg) {

                if (msg.length > 0) {
                    document.getElementById("suggestionsG").style.display = "block";
                    var sList = document.getElementById("suggestionsListG");

                    var ul = document.createElement('ul');

                    for (var i = 0; i < msg.length; i++){
                        var li = document.createElement('li'); 
                        li.innerHTML = msg[i];
                        li.onclick = bindFunctionG(msg[i]);
                        ul.appendChild(li);
                    }

                    sList.appendChild(ul);
                }
            }

            function bindFunctionG(txt) {
                return function () {fillTextFieldG(txt);};
            }

            function fillTextFieldG(txt) {
                document.getElementById("suggestionsG").style.display = "none";
                document.getElementById("choixUtilsG").appendChild(document.createTextNode(txt+", ")); //
                document.getElementById("choixUtilsGBox").value="";      
                document.getElementById("labelleG").innerHTML  += "<span class=\"label label-info\" id=\""+txt+"\" onclick=\"decoche('"+txt+"');\">"+txt+"  <input type=\"checkbox\"  name=\"choixUtilsGChk\" id=\""+txt+"_chk\" value=\""+txt+"\" hidden checked></span>   ";
                removeListG();
                
            }

            function removeListG() {
                var sList = document.getElementById("suggestionsListG"); 
                var children = sList.childNodes; 
                for (var i = 0; i < children.length; i++) {
                    sList.removeChild(children[i]);
                }
                verifyBeforeSend();
            }
            
            function decoche(id)
            {
                var btn = document.getElementById(id);
                
                document.getElementById(id+"_chk").checked = !document.getElementById(id+"_chk").checked;
                if(!document.getElementById(id+"_chk").checked)
                {
                    btn.className= "label";   
                }
                else
                {
                    btn.className= "label label-info";
                }
                
  
            }
            
            function verifyBeforeSend()
            {
                var checkboxes = document.getElementsByName("choixUtilsGChk");
                for (var i=0; i<checkboxes.length; i++) {
                    // And stick the checked ones onto an array...
                    var id = checkboxes[i].id;
                    
                    var btn = document.getElementById(id.replace("_chk", ""));
                    if(btn.className == 'label')
                    {
                        checkboxes[i].checked = false;
                    }              
                    else
                    {
                        checkboxes[i].checked = true;
                    }
                }
                
                var checkboxes = document.getElementsByName("choixUtilsMChk");
                for (var i=0; i<checkboxes.length; i++) {
                    // And stick the checked ones onto an array...
                    var id = checkboxes[i].id;
                    var btn = document.getElementById(id.replace("_chk", ""));
                    if(btn.className == 'label')
                    {
                        checkboxes[i].checked = false;
                    }
                    else
                    {
                        checkboxes[i].checked = true;
                    }
                }
            }
            
            function initPJs()
            {
                //<!-- Create an instance of the multiSelector class, pass it the output target and the max number of files -->
                var multi_selector = new MultiSelector( document.getElementById( 'files_list' ), 3 );
                //<!-- Pass in the file element -->
                multi_selector.addElement( document.getElementById( 'my_file_element' ) );
            }
        </script>
    </head>

    <body onload="initPJs();">

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
                            <li class="dropdown active">
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
                            <li class="dropdown">
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
                            <% if (session.getAttribute("isAdmin") != null) {%>     
                            <li class="dropdown">
                                <a id="drop5" href="#" role="button" class="dropdown-toggle" data-toggle="dropdown">Import XML <b class="caret"></b></a>
                                <ul class="dropdown-menu" role="menu" aria-labelledby="drop1">
                                    <li><a tabindex="-1" href="/BE_A2_2012_2013/user/createNewUser">Importer un fichier XML</a></li> 
                                </ul>
                            </li>
                            <% }%> 
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
                        <h1>Envoyer un message</h1>
                    </div>
                    <!-- Example row of columns -->
                    <div class="row-fluid"> 
                        <form method="POST" name="saisieNouveauMessage" id="saisieNouveauMessage" action="<c:url value="messageCreated"/>" enctype="multipart/form-data">
                            <fieldset>
                                <legend for="projet" class="rubrique">Membre(s)</legend>
                                <input type="text" id="choixUtilsMBox" name ="choixUtilsMBox" onkeyup='lookupM(this.value);' for="projet" />
                                <div class="suggestionsBox" id="suggestionsM" style="display: none;"></div>
                                <div class="suggestionList" id="suggestionsListM"></div>
                                <input type="hidden" id="choixUtilsM" name ="choixUtilsM"  />
                                <div><b id="labelleM">${utilsM} </b></div>
                            </fieldset>
                            <fieldset>
                                <legend for="projet" class="rubrique">Groupe(s)</legend>
                                <input type="text" id="choixUtilsGBox" name ="choixUtilsGBox" onkeyup='lookupG(this.value);' for="projet" />
                                <div class="suggestionsBox" id="suggestionsG" style="display: none;"></div>
                                <div class="suggestionList" id="suggestionsListG"></div>
                                <input type="hidden" id="choixUtilsG" name ="choixUtilsG"  />
                                <div><b id="labelleG">${utilsG} </b></div>
                            </fieldset>
                            <fieldset>
                                <legend for="import" class="rubrique">Fichiers joints</legend>
                                <!-- The file element -- NOTE: it has an ID -->
                                <input id="my_file_element" type="file" name="file" >
                                <br>
                                Fichiers  :
                                <!-- This is where the output will appear -->
                                <div id="files_list"></div>
                                <script>
                                    
                                </script>
                            </fieldset>
                            <fieldset>
                                <legend>Objet</legend>
                                <div>
                                    <textarea style="resize: none;" rows="2" class="span14 search-query"type="text" class="input" name="titreMessage" id="titreMessage"  required>${title}</textarea>
                                </div>
                                <legend>Message</legend>
                                <div>
                                    <textarea style="resize: none;" rows="16" class="span14  search-query" type="text" class="input" name="saisieMessage" id="saisieMessage" placeholder="message" >${message}</textarea>
                                </div>
                            </fieldset>
                            <div class="span1 pull-right">
                                <input class="btn btn-primary" type="submit" value="Envoyer"/>
                            </div>
                        </form>
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
            <script src="<c:url value="/resources/js/multifile.js"/>"></script>
        </div>
    </body>
</html>
