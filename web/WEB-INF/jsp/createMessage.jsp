<%-- 
    Document   : CreateMessage
    Created on : 13 janv. 2013, 19:52:58
    Author     : Doudi
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
    <head>
        <!-- début header  -->
        <jsp:include page="header.jsp" >
            <jsp:param name="title" value="PESO - Administration" />
        </jsp:include>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="<c:url value="/css/main.css"/>" rel="stylesheet" type="text/css" />
        <!-- fin header -->
    </head>
    <body>
        <!-- Menu Gauche -->
        <div id="content" >
            <jsp:include page="menu.jsp" >
                <jsp:param name="utilisateursMenu" value="" />
                <jsp:param name="groupesMenu" value="" />
                <jsp:param name="MessagesMenu" value="class=\"but_now\"" />
                <jsp:param name="tâchesMenu" value="" />
                <jsp:param name="XMLMenu" value="class=\"but_now\"" />
            </jsp:include>

            <!-- Fin Menu Gauche -->


            <!-- Contenu Page -->

            <div id="right_blog">
                <div class="box">
                    <div class="blog_box">
                        <div class="dat">
                            <div class="cleare"></div>
                        </div>

                        <div class="text">
                            <h2>Nouveau message</h2>

                            <div class="drop_line" style="height: 3px;"></div>
                            <div id="box_form">
                                <form method="POST" name="saisieNouveauMessage" id="saisieNouveauMessage" action="<c:url value="MessageCreated"/>">

                                    <input type="button" class="submit_but" name="choisirPieceJointe" id="choisirPieceJointe" value="Joindre Fichier" onclick="window.open('attachment', 'exemple', 'height=600, width=800, top=90, left=350, toolbar=no, menubar=no, location=yes, resizable=yes, scrollbars=yes, status=no');"/>
                                    </br></br></br>
                                    <fieldset>
                                        <legend class="rubrique">Destinataire(s)</legend></br>
                                        <input type="button" class="submit_but" name="selectGroupeDestinataire" id="selectGroupeDestinataire" value="Choisir Groupe(s)" onclick="window.open('openUsers', 'exemple', 'height=600, width=800, top=90, left=350, toolbar=no, menubar=no, location=yes, resizable=yes, scrollbars=yes, status=no');"/>
                                        <input type="button" class="submit_but" name="selectUtilisateurDestinataire" id="selectUtilisateurDestinataire" value="Choisir Utilisateur(s)" onclick="window.open('openUsers', 'exemple', 'height=600, width=800, top=90, left=350, toolbar=no, menubar=no, location=yes, resizable=yes, scrollbars=yes, status=no');"/>
                                        </br></br>
                                        <div class="form_line">
                                            <input type="text" class="input" name="saisieGroupeDestinataire" id="saisieGroupeDestinataire" placeholder="Groupe(s) " />
                                        </div>
                                        <div class="form_line">
                                            <input type="text" class="input" name="saisieUtilisateurDestinataire" value =${user} id="saisieUtilisateurDestinataire" placeholder="Utilisateur(s) " required />
                                        </div>
                                    </fieldset>

                                    </br></br>
                                    <fieldset>
                                        <legend class="rubrique">Message</legend></br>
                                        <div class="form_line">
                                            <input type="text" class="input" name="titreMessage" id="titreMessage" placeholder="Titre " required/>
                                        </div>
                                        <div class="form_line">
                                            <textarea name="saisieMessage" id="saisieMessage" cols="0" rows="0" placeholder="Message" required></textarea>
                                        </div>

                                        <div class="form_line" style="padding-top: 20px;">
                                            <input type="reset" class="submit_but" value="Effacer Message" />
                                            <span class="form_line" style="padding-top: 5px;">
                                                <input type="submit" class="submit_but cleare_but" value="Envoyer" />
                                            </span>
                                        </div>
                                    </fieldset>

                                </form>


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
            </div>
        </div>
    </body>
</html>
