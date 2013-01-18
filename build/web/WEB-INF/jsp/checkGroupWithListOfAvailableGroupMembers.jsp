<%-- 
    Document   : CheckGroupWithListOfAvailableGroupMembers
    Created on : 17 janv. 2013, 11:07:50
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
                    <h2>Fiche du groupe</h2>
                    <article>

                        <form id="selectUsersForm" action="<c:url value="checkGroup"/> method="post" ></br>
                            <label for="id_groupe">ID</label> <input type="text" name="id_groupe" id="id_groupe" placeholder="${id_groupe}" readonly="true" required/></br></br>
                            <label for="nom_groupe">Nom</label> <input type="text" name="nom_groupe" id="nom_groupe" placeholder="${nom_groupe}" readonly="true" required/></br></br>
                            <label for="desc_groupe" >Description</label></br><textarea ROWS=4 COLS=40 name="desc_groupe" id="desc_groupe" readonly="true">${desc_groupe}</textarea></br>
                            <br>
                            <label for="chef_groupe">Chef</label> <input type="text" name="chef_groupe" id="chef_groupe" placeholder="${chef_groupe}" readonly="true" required/></br></br></br>
                            <fieldset>
                                <legend>Membres du groupes</legend>
                                </br></br>
                                ${liste_membres_groupe}
                                <input type="submit" name="supprimer_membre_groupe" id="supprimer_membre_groupe" class="submit_but" value="Supprimer membre" /></br></br></br>
                                <div id="divListeAjouterMembres">
                                    <form action="<c:url value="checkGroup"/>">
                                        ${liste_membres_ajouter}
                                        <input type="submit" name="ajouter_membre_groupe" id="ajouter_membre_groupe" class="submit_but" value="Ajouter" /><br><br><br>
                                    </form>
                                </div>
                            </fieldset>
                            </br></br>
                        </form></br>


                    </article>   
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
