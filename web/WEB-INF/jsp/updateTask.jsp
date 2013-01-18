<%-- 
    Document   : UpdateTask
    Created on : 14 janv. 2013, 09:01:53
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

        <!-- Contenu Page -->

        <div id="popUpMenu">
            <div class="blog_box">
                <div class="dat">
                    <div class="cleare"></div>
                </div>
                <div class="text">
                    <article>
                        <h2>Nouvelle Tâche</h2> 

                        <form method="post" action="<c:url value="taskUpdated"/>" id="creationTacheForm">	
                            <input type="hidden" name="IDTache" id="IDTache" placeholder="ID">
                            <fieldset>
                                <legend id="selectDestinataire" name="selectDestinataire" class="rubrique">Choix des destinataires</legend>
                                </br>
                                <p id="groupes" >Groupe(s)</p>
                                </br>
                                <input type="button" class="submit_but" name="selectGroupe" id="selectGroupe" value="Sélectionner" onclick="window.open('openGroups', 'exemple', 'height=600, width=800, top=90, left=350, toolbar=no, menubar=no, location=yes, resizable=yes, scrollbars=yes, status=no');"/>
                                <input type="button" class="submit_but" name="creerGroupe" id="creerGroupe" value="Nouveau" onclick="window.open('createGroup', 'exemple', 'height=600, width=800, top=90, left=350, toolbar=no, menubar=no, location=yes, resizable=yes, scrollbars=yes, status=no');"/></br>
                                </br></br>	

                                <p id="utilisateurs">Utilisateur(s)</p>
                                </br>
                                <input type="button" class="submit_but" name="selectUtilisateur" id="selectUtilisateur" value="Sélectionner" onclick="window.open('openUsers', 'exemple', 'height=600, width=800, top=90, left=350, toolbar=no, menubar=no, location=yes, resizable=yes, scrollbars=yes, status=no');"/>
                                <input type="button" class="submit_but" name="creerUtilisateur" id="creerUtilisateur" value="Nouveau" onclick="window.open('createNewUser', 'exemple', 'height=600, width=800, top=90, left=350, toolbar=no, menubar=no, location=yes, resizable=yes, scrollbars=yes, status=no');"/></br>
                                </br></br>
                                <input type="input"  name="choixUtils" id="choixUtils" value="${utils}"/>
                            </fieldset>

                            <fieldset>
                                <label for="projet" class="rubrique">Projet - </label>
                                <input type="text" name="projetTache" id="projetTache" value="${projetTache}" required/>
                                </br></br>
                            </fieldset>

                            <fieldset>
                                <label for="titre" class="rubrique">Titre - </label>
                                <input type="text" name="titreTitre" id="titreTache" value="${Titre}" required/>
                                </br></br>
                            </fieldset>

                            <fieldset>
                                <legend id="description" class="rubrique">Description</legend><textarea ROWS=4 COLS=40 name="descriptionTache" id="descriptionTache"> ${descriptionTache}</textarea></br>
                                </br>
                            </fieldset>

                            <fieldset>
                                <label for="dateDebut" class="rubrique">Date de Début - </label>
                                <input type="date" name="dateDebut" id="dateDebut" value = "${dateDebut}" required/>
                                </br>
                            </fieldset>
                            </br>

                            <fieldset>
                                <label for="dateFin" class="rubrique">Date de Fin - </label>
                                <input type="date" name="dateFin" id="dateFin" value = "${dateFin}" required/>
                                </br>
                            </fieldset>
                            </br>

                            <fieldset>
                                <label for="statut" class="rubrique">Statut - </label>
                                <select name="statutTache" id="statutTache">
                                    <option value="OPEN">OPEN</option>
                                    <option  value="URGENT">URGENT</option>
                                    <option  value="CLOSED">CLOSED</option>
                                </select>
                                <br />
                            </fieldset>
                            </br>

                            <fieldset>
                                <label for="budget" class="rubrique">Budget - </label>
                                <input type="number" name="budget" id="budgetTache" value ="${budget}" required/><br />
                                 <label for="consumed" class="rubrique">Consomme - </label>
                                <input type="number" name="consumed" id="consumed"  value ="${consumed}"  required/><br />
                                <label for="rae" class="rubrique">RAE - </label>
                                <input type="number" name="rae" id="rae"  value ="${rae}"  required/><br />
                            </fieldset>
                            </br></br>
                            <input type="text" id="idTask" name ="idTask" hidden="true" value="${idTask}" />
                            <input type="submit" class="submit_but"  value="Valider" />
                            </br>
                        </form>

                    </article>

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

    </body>
</html>
