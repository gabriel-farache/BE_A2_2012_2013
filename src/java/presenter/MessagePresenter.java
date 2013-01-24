/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package presenter;

import dataObjects.Attachment;
import dataObjects.Message;
import dataObjects.MessageHeader;
import dataObjects.MessageStatus;
import dataObjects.Recipient;
import dataObjects.RecipientType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author gabriel
 */
@Controller
@RequestMapping("message")
public class MessagePresenter extends Project_Management_Presenter {

    public final static String urlDomain = "message/";

    /**
     * Fonction qui load la liste des messages dans un jolie tableau pour
     * l'admin
     *
     * @param request
     * @param mm
     */
    @RequestMapping(value = {"inbox"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String interceptPageinbox(HttpServletRequest request, ModelMap mm) {
        //Récupération du token de la session
        String token = this.getTokenSession(request.getSession(), mm);
        String pageToLoad = null;
        String id = Project_Management_Presenter.model.isValidToken(token);
        if (id != null) {
            this.generateAndAddMessagesInbox(mm, token);
            this.generateAndAddMessagesOutbox(mm, token);
            mm.addAttribute("nbNewMessages", this.getNbMessagesForStatus(token, ""));

        } else {
            pageToLoad = "connection";
        }
        return pageToLoad;
    }

    @RequestMapping(value = {"createMessage"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String createMessage(HttpServletRequest request, ModelMap m) {
        String token = this.getTokenSession(request.getSession(), m);
        try {
            String[] userss = request.getParameterValues("selectUsersForm");
            String uss = "";
            for (String u : userss) {
                uss += u + ", ";
            }
            m.addAttribute("user", uss);
        } catch (Exception e) {
        }
        try {
            Message mess = this.getMessageBody((String) request.getParameter("idMess"), token, (((String) request.getParameter("fromInbox")).compareToIgnoreCase("yes") == 0 ? true : false));
            m.addAttribute("recipient", mess.getSender());
            m.addAttribute("title", "Réponse au message : " + mess.getTitle());
            m.addAttribute("message", "\n\n------- Message original envoyé le " + mess.getStringCreationDate() + " -------\n" + mess.getContent());
        } catch (Exception e) {
        }
        if (token != null) {
            if ((Project_Management_Presenter_Intern_Methods.model.isValidToken(token)) != null) {
                return null;
            }
        }
        return "connection";
    }

    @RequestMapping(value = {"supprMessage"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String interceptDeleteMess(HttpServletRequest request, ModelMap m) {
        //Récupération du token de la session
        String token = this.getTokenSession(request.getSession(), m);
        boolean fromInbox = request.getParameter("fromInbox").compareToIgnoreCase("yes") == 0;
        String alertMess = "<div class=\"alert alert-error\">"
                + "<a class=\"close\" data-dismiss=\"alert\">×</a>"
                + "<strong>Vous ne pouvez pas supprimer un message que vous avez envoyé ! </strong>"
                + "</div>";
        //Récuperation de l'id de la tâche
        String idMess = request.getParameter("idMess").trim();
        //Appel méthode interne correspondante
        if (Project_Management_Presenter.model.isValidToken(token) != null) {
            if (fromInbox) {
                if (this.deleteMessage(token, idMess)) {
                    m.addAttribute("errorMessage", "");
                    alertMess = "<div class=\"alert alert-success\">"
                            + "<a class=\"close\" data-dismiss=\"alert\">×</a>"
                            + "<strong>Suppression terminé avec succès.</strong>"
                            + "</div>";
                } else {
                    m.addAttribute("errorMessage", "Erreur lors de la suppression du message.");
                    alertMess = "<div class=\"alert alert-error\">"
                            + "<a class=\"close\" data-dismiss=\"alert\">×</a>"
                            + "<strong>\"Erreur lors de la suppression du message.\"</strong>"
                            + "</div>";
                }
            }
        } else {
            alertMess = "<div class=\"alert alert-error\">"
                    + "<a class=\"close\" data-dismiss=\"alert\">×</a>"
                    + "<strong>Vous n'êtes pas identifié ! </strong>"
                    + "</div>";
        }
        m.addAttribute("alert", alertMess);
        return MessagePresenter.urlDomain + "inbox";
        //Retour
    }

    /*
     * PAGE JSP A FAIRE !!!!!!!!!!!!!!!
     */
    @RequestMapping(value = {"messageCreated"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String interceptMessageToCreate(HttpServletRequest request, ModelMap m) {
        //Récupération du token de la session
        String token = this.getTokenSession(request.getSession(), m);
        String alertMess = "<div class=\"alert alert-success\">"
                + "<a class=\"close\" data-dismiss=\"alert\">×</a>"
                + "<strong>Erreur fatale ! </strong></div>";
        try {
            //IL FAUT RECUPERER LES GROUPES AUSSI !!!!
            String title = request.getParameter("titreMessage");
            title = title.substring(0, (title.length() > 50 ? 49 : title.length()));
            String message = request.getParameter("saisieMessage");
            String[] cheminsPj = request.getParameterValues("choisirPieceJointe");
            String memberss[] = request.getParameter("saisieUtilisateurDestinataire").replaceAll(" ", "").split(",");
            String groups[] = request.getParameter("saisieGroupeDestinataire").replaceAll(" ", "").split(",");

            ArrayList<String> members = new ArrayList<String>();

            Collections.addAll(members, memberss);
            String idSender = Project_Management_Presenter.model.isValidToken(token);
            System.err.println("interceptMessageToCreate       " + token + " ---  " + idSender);

            ArrayList<String> groupsL = new ArrayList<String>();
            Collections.addAll(groupsL, groups);
            //Use ManageAttachments qui upload les fichiers
            //Creation des Attachments(nom,nom)
            ArrayList<Attachment> pj = new ArrayList<Attachment>();
            if (pj != null && pj.size() > 0) {
                for (int i = 0; i < cheminsPj.length; i++) {
                    pj.add(new Attachment(cheminsPj[i], cheminsPj[i]));//pj stockée sous ce nom a cette adresse
                }
            }
            if (!this.saveMessage(idSender, groupsL, members, title, message, null, pj, token)) {
                alertMess = "<div class=\"alert alert-error\">"
                        + "<a class=\"close\" data-dismiss=\"alert\">×</a>"
                        + "<strong>Envoi du message \"" + title + " échoué au niveau de l'envoi au(x) destinataire(s) groupes ! </strong></div>";

            } else {
                this.fillAccordionMenu(token, m);
                alertMess = "<div class=\"alert alert-success\">"
                        + "<a class=\"close\" data-dismiss=\"alert\">×</a>"
                        + "<strong>Envoi du message \"" + title + "\" terminé ! </strong></div>";
            }


        } catch (Exception ex) {
            Logger.getLogger(Project_Management_Presenter.class
                    .getName()).log(Level.SEVERE, null, ex);

        }
        this.generateAndAddMessagesInbox(m, token);
        m.addAttribute("alert", alertMess);
        return MessagePresenter.urlDomain + "inbox";
    }

    /*
     * PAGE JSP A FAIRE !!!!!!!!!!!!!!!
     */
    /**
     * Page appellée lorsque l'utilisateur appuit sur le bouton "Valider update"
     *
     * @param request
     * @param modelMap
     */
    @RequestMapping(value = {"checkMessage"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String interceptMessageToDisplay(HttpServletRequest request, ModelMap modelMap) {
        String pageToLoad = null;

        //Récupération du token de la session
        String token = this.getTokenSession(request.getSession(), modelMap);
        //Creation de la liste des attributs & Creation d'une map contenant la valeur associé à un champ

        Message m = this.getMessageBody(request.getParameter("idMessage"), token, (((String) request.getParameter("fromInbox")).compareToIgnoreCase("yes") == 0 ? true : false));
        try {
            Project_Management_Presenter.model.updateMessageStatus(token, m.getId(), MessageStatus.READ, true);
        } catch (SQLException ex) {
            Logger.getLogger(Project_Management_Presenter.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        if (m != null) {
            String groupRcpt = "";
            String memberRcpt = "";
            modelMap.addAttribute("title", m.getTitle());
            modelMap.addAttribute("sender", m.getSender());
            modelMap.addAttribute("content", m.getContent());
            modelMap.addAttribute("idMess", request.getParameter("idMessage"));
            request.getSession().setAttribute("idMessStatus", request.getParameter("idMessage"));
            modelMap.addAttribute("fromInbox", request.getParameter("fromInbox"));
            for (Recipient r : m.getRecipients()) {
                if (r.getType().equals(RecipientType.USER)) {
                    memberRcpt += r.getId() + ", ";
                } else {
                    if (r.getType().equals(RecipientType.GROUP)) {
                        groupRcpt += r.getId() + ", ";
                    }
                }
            }
            /* memberRcpt = memberRcpt.substring(0, memberRcpt.length() - 2);
             groupRcpt = groupRcpt.substring(0, groupRcpt.length() - 2);

             memberRcpt.trim().replaceAll(",", ", ");
             groupRcpt.trim().replaceAll(",", ", ");*/
            System.err.println("--- *** --- *** " + memberRcpt + " -- " + groupRcpt);

            modelMap.addAttribute("recipientsM", memberRcpt);
            modelMap.addAttribute("recipientsG", groupRcpt);
        } else {
            pageToLoad = "error";
        }

        return pageToLoad;

    }

    /**
     *
     * @param token
     * @param messageList
     * @return Array : [0] : Important, [1] : Have to answer, [2] : Urgent, [3]
     * : Forwarded, [4] : Read, [5] unread
     */
    private String[] fillInboxMessages(String token, ArrayList<MessageHeader> messageList) {
        String[] table = {null, null, null, null, null, null};
        int[] hasMsgs = {0, 0, 0, 0, 0, 0};
        try {
            if (messageList != null) {
                //Creation de la table en html contenant tous les headers de toutes les taches
                for (int i = 0; i < 6; i++) {
                    table[i] =
                            "<table class=\"table table-hover\" >"
                            + "<tr id='entete'>"
                            + "<th>Titre du message</th>"
                            + "<th>Auteur</th>"
                            + "<th>Date de création</th>"
                            + "</tr>";
                }
                for (MessageHeader m : messageList) {
                    int[] hasLine = {0, 0, 0, 0, 0, 0};
                    ArrayList<MessageStatus> mst = m.getStatus();
                    if (mst != null && !mst.isEmpty()) {
                        for (MessageStatus ms : mst) {
                            if (ms.equals(MessageStatus.IMPORTANT) && hasLine[0] < 1) {
                                table[0] += "<tr class=\"warning\" id='groupe' ";
                                hasMsgs[0]++;
                                hasLine[0]++;
                            } else if (ms.equals(MessageStatus.HAVE_TO_ANSWER) && hasLine[1] < 1) {
                                table[1] += "<tr class=\"info\" id='groupe' ";
                                hasMsgs[1]++;
                                hasLine[1]++;
                            } else if (ms.equals(MessageStatus.URGENT) && hasLine[2] < 1) {
                                table[2] += "<tr class=\"error\" id='groupe' ";
                                hasMsgs[2]++;
                                hasLine[2]++;
                            } else if (ms.equals(MessageStatus.FORWARDED) && hasLine[3] < 1) {
                                table[3] += "<tr class=\"success\" id='groupe' ";
                                hasMsgs[3]++;
                                hasLine[3]++;
                            } else if(hasLine[4] < 1){
                                table[4] += "<tr id='groupe' ";
                                hasMsgs[4]++;
                                hasLine[4]++;
                            }
                        }
                    } else {
                        table[5] += "<tr id='groupe' class=\"default\" ";
                        hasMsgs[5]++;
                        hasLine[5]++;
                    }
                    for (int i = 0; i < 6; i++) {
                        table[i] += hasLine[i] > 0 ? "onclick='window.location.href = \"" + Project_Management_Presenter.domain + "/message/checkMessage?idMessage=" + m.getId() + "&fromInbox=yes\";'>" + "<td>" + m.getTitle() + "</td>"
                                + "<td>" + m.getSender() + "</td>"
                                + "<td>" + m.getStringCreationDate() + "</td>"
                                + "</tr>" : "";
                    }
                }
                for (int i = 0; i < 6; i++) {
                    table[i] = hasMsgs[i] > 0 ? table[i] + "</table>" : null;
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(Project_Management_Presenter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return table;
    }

    private void generateAndAddMessagesInbox(ModelMap mm, String token) {
        ArrayList<MessageHeader> mh = this.getHeaderMessages(token, true);
        //[0] : Important, [1] : Have to answer, [2] : Urgent, [3] : Forwarded, [4] : Read, [5] unread

        String[] messInbox = this.fillInboxMessages(token, mh);
                                    System.err.println("/*-/-*/-*/-*/-*/-/-*/-*/*- " + messInbox[5]);

        String messRead = (messInbox == null ? null : messInbox[4]);
        String messUnRead = (messInbox == null ? null : messInbox[5]);
        String messUrg = (messInbox == null ? null : messInbox[2]);
        String messImp = (messInbox == null ? null : messInbox[0]);
        String messToAnsw = (messInbox == null ? null : messInbox[1]);
        String messFwd = (messInbox == null ? null : messInbox[3]);
        //Mise en place de la table 
        mm.addAttribute("messRead", messRead == null ? "Pas de messages." : messRead);
        mm.addAttribute("messUnRead", messUnRead == null ? "Pas de messages." : messUnRead);
        mm.addAttribute("messUrg", messUrg == null ? "Pas de messages." : messUrg);
        mm.addAttribute("messImp", messImp == null ? "Pas de messages." : messImp);
        mm.addAttribute("messToAnsw", messToAnsw == null ? "Pas de messages." : messToAnsw);
        mm.addAttribute("messFwd", messFwd == null ? "Pas de messages." : messFwd);
    }

    private String fillOutboxMessages(String token, ArrayList<MessageHeader> messageList) {
        String table = null;

        try {
            if (messageList != null) {
                //Creation de la table en html contenant tous les headers de toutes les taches

                table =
                        "<table class=\"table table-hover\" >"
                        + "<tr id='entete'>"
                        + "<th>Titre du message</th>"
                        + "<th>Auteur</th>"
                        + "<th>Date de création</th>"
                        + "</tr>";

                for (MessageHeader m : messageList) {
                    table += "<tr id='groupe' onclick='window.location.href = \"" + Project_Management_Presenter.domain + "/message/checkMessage?idMessage=" + m.getId() + "&fromInbox=no\";'>" + "<td>" + m.getTitle() + "</td>"
                            + "<td>" + m.getSender() + "</td>"
                            + "<td>" + m.getStringCreationDate() + "</td>"
                            + "</tr>";
                }
                table += "</table>";
            }
        } catch (Exception ex) {
            Logger.getLogger(Project_Management_Presenter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return table;
    }

    private void generateAndAddMessagesOutbox(ModelMap mm, String token) {
        ArrayList<MessageHeader> mh = this.getHeaderMessages(token, false);
        //[0] : Important, [1] : Have to answer, [2] : Urgent, [3] : Forwarded, [4] : Read, [5] unread
        String messOutbox = this.fillOutboxMessages(token, mh);
        mm.addAttribute("messOutbox", messOutbox == null ? "Pas de messages." : messOutbox);
    }
}
