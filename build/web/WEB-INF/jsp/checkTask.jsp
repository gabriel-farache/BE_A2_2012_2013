<%-- 
    Document   : CheckTask
    Created on : 14 janv. 2013, 09:24:32
    Author     : Doudi
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
            <jsp:param name="title" value="PESO - Consultation d'une tâche" />
        </jsp:include>
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
                        <h2>Fiche de Tâche</h2> 

                        <fieldset>
                            <legend id="destinataire" name="destinataire" class="rubrique">Destinataires</legend>
                            </br>
                            <p id="groupes" >Groupe(s)</p>
                            </br>
                            ${groupeTache}
                            </br></br>	
                            ${utilisateursTache}
                            <p id="utilisateurs">Utilisateur(s)</p>
                            </br>

                            </br></br>
                        </fieldset>

                        <fieldset>
                            <label for="projet" class="rubrique">Projet - </label>
                            <label for="projet">${projetTache}</label>
                            </br></br>
                        </fieldset>

                        <fieldset>
                            <label for="titre" class="rubrique">Titre - </label>
                            <label for="titre" >${Titre}</label>

                            </br></br>
                        </fieldset>

                        <fieldset>
                            <legend id="description" class="rubrique">Description</legend>
                            <label for="descr" ><small>${descriptionTache}</small></label>
                            </br>
                            </br>
                        </fieldset>

                        <fieldset>
                            <label for="dateDebut" class="rubrique">Date de Début - </label>
                            <label for="dateDebut" >${dateDebut}</label>
                            </br>
                        </fieldset>
                        </br>

                        <fieldset>
                            <label for="dateFin" class="rubrique">Date de Fin - </label>
                            <label for="dateFin" >${dateFin}</label>
                            </br>
                        </fieldset>
                        </br>

                        <fieldset>
                            <label for="statut" class="rubrique">Statut - </label>
                            <label for="statut">${statut}</label>

                            <br />
                        </fieldset>
                        </br>

                        <fieldset>
                            <label for="budget" class="rubrique">Budget - </label>
                            <label for="budget">${budget}</label>
                        </fieldset>
                        </br></br>

                        </br>

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
    </div>

</body>
</html>
