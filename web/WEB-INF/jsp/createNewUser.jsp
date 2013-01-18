<%-- 
    Document   : CreateNewUser
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
            <jsp:param name="title" value="PESO - Administration" />
        </jsp:include>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="<c:url value="/css/main.css"/>" rel="stylesheet" type="text/css" />
        <script language="text/javascript">
            function checkPass(){
                windows.alert(document.getElementById("pass"))
                windows.alert(document.getElementById("pass_confi"))
                if(document.getElementById("pass") == document.getElementById("pass_confi"))
                {
                    document.maforme.Submit.disabled = false; 
                }
                else
                {
                    document.maforme.Submit.disabled = true; 
                }
            }
        </script>
        <!-- fin header -->
    </head>
    <body onload="checkPass();">



        <div id="popUpMenu">    	

            <div class="blog_box">
                <div class="dat">
                    <div class="cleare"></div>
                </div>
                <div class="text">
                    <h2>Nouvel utilisateur</h2>
                    <article>
                        <br />
                        <form name ="maform" action="<c:url value="userCreated"/>" method="post">
                            <fieldset>   
                                <p>
                                    <label for="id">ID (optionnel)</label> : <input type="text" name="id" id="id" placeholder="Identifiant"/>
                                    </br>
                                    </br>
                                    <label for="nom">Nom</label> : <input type="text" name="nom" id="nom" placeholder="Nom" required/>
                                    </br>
                                    </br>
                                    <label for="prenom">Prénom</label> : <input type="text" name="prenom" id="prenom" placeholder="Prénom" required/>
                                    </br></br>
                                    <label for="mail">Adresse mail</label> : <input type="email" name="mail" id="mail" placeholder="Email" required/>
                                    </br></br>
                                    <label for="pass">Mot de passe</label> : <input type="password" name="pass" id="pass" placeholder="*****" required="required"/>
                                    </br></br>
                                    <label for="pass_confi">Confirmer le mot de passe</label> : <input type="password" name="pass_confi" id="pass_confi"  placeholder="*****" required="required" onkeyup="checkPass();"/>
                                    </br></br>
                                    <input type="submit" class="submit_but" value="Créer" />

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
    </div>
</body>
</html>