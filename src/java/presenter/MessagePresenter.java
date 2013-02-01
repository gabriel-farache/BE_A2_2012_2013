/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package presenter;

import attachmentsManagement.ManageAttachements;
import attachmentsManagement.SaveAttachments;
import dataObjects.Attachment;
import dataObjects.Message;
import dataObjects.MessageHeader;
import dataObjects.MessageStatus;
import dataObjects.Recipient;
import dataObjects.RecipientType;
import dataObjects.Task;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import peopleObjects.Group;
import peopleObjects.Member;

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
            String autoAccordi = (String) request.getParameter("displayAccordi");
            this.generateAndAddMessagesInbox(token, request);
            this.generateAndAddMessagesOutbox(token, request);
            if (autoAccordi != null && !autoAccordi.equalsIgnoreCase("")) {
                if (!autoAccordi.equalsIgnoreCase("autoOut")) {
                    mm.addAttribute("autoIn", "auto");
                }
                mm.addAttribute(autoAccordi, "auto");
            }
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
            if (request.getParameter("fwd") == null) {
                Member memb = MessagePresenter.model.getInfosMember(mess.getSender());
                String libMemb = "<span class=\"label label-info\" id=\"" + memb.getName() + " (" + memb.getId_member() + ")\" onclick=\"decoche('" + memb.getName() + " (" + memb.getId_member() + ")');\">" + memb.getName() + " (" + memb.getId_member() + ")"
                        + "  <input type=\"checkbox\"  name=\"choixUtilsMChk\" id=\"" + memb.getName() + " (" + memb.getId_member() + ")_chk\" value=\"" + memb.getName() + " (" + memb.getId_member() + ")\" checked=true hidden></span>   ";
                m.addAttribute("utilsM", libMemb);
                m.addAttribute("title", "Réponse au message : " + mess.getTitle());
                m.addAttribute("message", "\n\n------- Message original envoyé le " + mess.getStringCreationDate() + " -------\n" + mess.getContent());
            } else {
                this.updateMessageStatus(token, (String) request.getParameter("idMess"), MessageStatus.FORWARDED, true);
                m.addAttribute("title", "Fwd : " + mess.getTitle());
                m.addAttribute("message", "\n\n------- Message original envoyé le " + mess.getStringCreationDate() + " par " + mess.getSender() + " -------\n" + mess.getContent());
            }
        } catch (Exception e) {
        }
        try {
            if (request.getParameter("fromTask") != null) {
                String idTask = request.getParameter("idTask");
                Task t = MessagePresenter.model.getInfosTask(Integer.parseInt(idTask));
                String libMemb = "";
                String libGps = "";
                Group g;
                Member memb;

                for (Recipient r : t.getRecipients()) {
                    if (!r.getType().equals(RecipientType.GROUP)) {
                        memb = MessagePresenter.model.getInfosMember(r.getId());
                        libMemb += "<span class=\"label label-info\" id=\"" + memb.getName() + " (" + memb.getId_member() + ")\" onclick=\"decoche('" + memb.getName() + " (" + memb.getId_member() + ")');\">" + memb.getName() + " (" + memb.getId_member() + ")"
                                + "  <input type=\"checkbox\"  name=\"choixUtilsMChk\" id=\"" + memb.getName() + " (" + memb.getId_member() + ")_chk\" value=\"" + memb.getName() + " (" + memb.getId_member() + ")\" checked=true hidden></span>   ";
                    } else {
                        g = MessagePresenter.model.getGroupInfos(r.getId());
                        libGps += "<span class=\"label label-info\" id=\"" + g.getGroup_name() + " (" + g.getId_group() + ")\" onclick=\"decoche('" + g.getGroup_name() + " (" + g.getId_group() + ")');\">" + g.getGroup_name() + " (" + g.getId_group() + ")"
                                + "  <input type=\"checkbox\"  name=\"choixUtilsGChk\" id=\"" + g.getGroup_name() + " (" + g.getId_group() + ")_chk\" value=\"" + g.getGroup_name() + " (" + g.getId_group() + ")\" checked=true  hidden></span>   ";
                    }
                }
                m.addAttribute("utilsM", libMemb);
                m.addAttribute("utilsG", libGps);
                m.addAttribute("title", "Message à propos de la tâche : " + t.getTitle() + " (" + idTask + ").");
            }
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
        String alertMess = "<div class=\"alert alert-error\">"
                + "<a class=\"close\" data-dismiss=\"alert\">×</a>"
                + "<strong>Erreur fatale ! </strong></div>";
        //Récupération des utilisateurs et des groupes
        ArrayList<String> members = new ArrayList<String>();
        //Collections.addAll(members, allParams.get("selectUtilisateur"));
        ArrayList<String> groups = new ArrayList<String>();
        String title = "";
        String message = "";
        HashMap<String, InputStream> assocFileName_InStrm = new HashMap<String, InputStream>();
        ArrayList<Attachment> pj = new ArrayList<Attachment>();
        try {
            ServletFileUpload upload = new ServletFileUpload();
            FileItemIterator iterator = upload.getItemIterator(request);
            while (iterator.hasNext()) {
                FileItemStream item = iterator.next();
                String name = item.getFieldName();
                InputStream strm = item.openStream();
                String value = Streams.asString(strm);
                if (item.isFormField()) {
                    System.err.println("Got a form field: " + name + " " + value);
                    if (name.equals("choixUtilsMChk")) {
                        members.add((value.split("[(]")[1].split("[)]")[0]).trim());
                    } else if (name.equals("choixUtilsGChk")) {
                        groups.add((value.split("[(]")[1].split("[)]")[0]).trim());
                    } else if (name.equals("titreMessage")) {
                        title = value;
                        title = title.substring(0, (title.length() > 50 ? 49 : title.length()));
                    } else if (name.equals("saisieMessage")) {
                        message = value == null ? "" : value;
                    }
                } else {
                    String filename = item.getName();
                    if (filename != null && !filename.equals("")) {
                        System.err.println("Got an uploaded file: " + item.getFieldName()
                                + ", name = " + item.getName());
                        pj.add(new Attachment(filename, filename));//pj stockée sous ce nom a cette adresse
                        assocFileName_InStrm.put(filename, strm);

                    }
                }
            }
            //enregistrement pjs


            if (groups.isEmpty()) {
                groups.add("");
            }
            if (members.isEmpty()) {
                members.add("");
            }

            String idSender = Project_Management_Presenter.model.isValidToken(token);
            System.err.println("interceptMessageToCreate       " + token + " ---  " + idSender);

            //Use ManageAttachments qui upload les fichiers
            //Creation des Attachments(nom,nom)

            Message mes = this.saveMessage(idSender, groups, members, title, message, null, pj, token);
            if (mes == null) {
                alertMess = "<div class=\"alert alert-error\">"
                        + "<a class=\"close\" data-dismiss=\"alert\">×</a>"
                        + "<strong>Envoi du message \"" + title + " Une erreur est survenue. Le message a pu être envoyé mais partielement seulement ! </strong></div>";

            } else {
                System.err.println("---------fdgsfqhdgjtdukyjhbngjhygdgjhyrsfdjwhrsfdjghg-------------           " + mes.getId());
                // new Thread(new SaveAttachmentsThread(request, mes)).start();
                for (Entry<String, InputStream> e : assocFileName_InStrm.entrySet()) {
                    String fName = e.getKey();
                    InputStream inptStrm = e.getValue();
                    ManageAttachements.createAndSaveAttachments(mes, fName, inptStrm);
                }

                this.fillAccordionMenu(token, m);
                alertMess = "<div class=\"alert alert-success\">"
                        + "<a class=\"close\" data-dismiss=\"alert\">×</a>"
                        + "<strong>Envoi du message \"" + title + "\" terminé ! </strong></div>";
            }


        } catch (Exception ex) {
            Logger.getLogger(Project_Management_Presenter.class
                    .getName()).log(Level.SEVERE, null, ex);

        }
        this.generateAndAddMessagesInbox(token, request);
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
            if (m.hasAttachments()) {
                String attch = "";
                String dir = (SaveAttachments.prepareDirectories(m)).getAbsolutePath();
                for (Attachment a : m.getAttachments()) {
                    attch += "<a href=\"download?dir=" + dir + File.separatorChar + a.getName() + "&name="+a.getName()+"\">" + a.getName() + "</a><br>";
                }

                modelMap.addAttribute("attch", attch.equals("") ? "Pas de fichiers joints" : attch);
            }
            modelMap.addAttribute("recipientsM", memberRcpt);
            modelMap.addAttribute("recipientsG", groupRcpt);
        } else {
            pageToLoad = "error";
        }

        return pageToLoad;

    }

    @RequestMapping(value = {"download"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String dowloadAtt(HttpServletResponse response, HttpServletRequest request) {
        InputStream is = null;
        try {
            String filepath = request.getParameter("dir");
            String filename = request.getParameter("name");
            File file = new File(filepath);
            response.setContentType(new MimetypesFileTypeMap().getContentType(file));
            response.setContentLength((int) file.length());
            response.setHeader("content-disposition", "attachment; filename=" + URLEncoder.encode(filename, "UTF-8"));
            is = new FileInputStream(file);
            FileCopyUtils.copy(is, response.getOutputStream());
            return null;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MessagePresenter.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                is.close();
            } catch (IOException ex) {
                Logger.getLogger(MessagePresenter.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }
    }

    /**
     *
     * @param token
     * @param messageList
     * @return Array : [0] : Important, [1] : Have to answer, [2] : Urgent, [3]
     * : Forwarded, [4] : Read, [5] unread
     */
    private ArrayList<ArrayList<MessageHeader>> fillInboxMessages(String token, ArrayList<MessageHeader> messageList) {
        ArrayList<ArrayList<MessageHeader>> mess = new ArrayList<ArrayList<MessageHeader>>();

        for (int i = 0; i < 6; i++) {
            mess.add(i, new ArrayList<MessageHeader>());
        }
        try {
            if (messageList != null) {
                //Creation de la table en html contenant tous les headers de toutes les taches

                for (MessageHeader m : messageList) {
                    int[] hasLine = {0, 0, 0, 0, 0, 0};
                    ArrayList<MessageStatus> mst = m.getStatus();
                    if (mst != null && !mst.isEmpty()) {
                        for (MessageStatus ms : mst) {
                            if (ms.equals(MessageStatus.IMPORTANT) && hasLine[0] < 1) {
                                mess.get(0).add(m);
                                hasLine[0]++;
                            } else if (ms.equals(MessageStatus.HAVE_TO_ANSWER) && hasLine[1] < 1) {
                                mess.get(1).add(m);
                                hasLine[1]++;
                            } else if (ms.equals(MessageStatus.URGENT) && hasLine[2] < 1) {
                                mess.get(2).add(m);
                                hasLine[2]++;
                            } else if (ms.equals(MessageStatus.FORWARDED) && hasLine[3] < 1) {
                                mess.get(3).add(m);
                                hasLine[3]++;
                            } else if (hasLine[4] < 1) {
                                mess.get(4).add(m);
                                hasLine[4]++;
                            }
                        }
                    } else {
                        mess.get(5).add(m);
                        hasLine[5]++;
                    }
                }

            }
        } catch (Exception ex) {
            Logger.getLogger(Project_Management_Presenter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return mess;
    }

    private void generateAndAddMessagesInbox(String token, HttpServletRequest request) {
        ArrayList<MessageHeader> mh = this.getHeaderMessages(token, true);
        //[0] : Important, [1] : Have to answer, [2] : Urgent, [3] : Forwarded, [4] : Read, [5] unread

        ArrayList<ArrayList<MessageHeader>> messInbox = this.fillInboxMessages(token, mh);

        ArrayList<MessageHeader> messRead = (messInbox == null ? null : messInbox.get(4));
        ArrayList<MessageHeader> messUnRead = (messInbox == null ? null : messInbox.get(5));
        ArrayList<MessageHeader> messUrg = (messInbox == null ? null : messInbox.get(2));
        ArrayList<MessageHeader> messImp = (messInbox == null ? null : messInbox.get(0));
        ArrayList<MessageHeader> messToAnsw = (messInbox == null ? null : messInbox.get(1));
        ArrayList<MessageHeader> messFwd = (messInbox == null ? null : messInbox.get(3));
        //Mise en place de la table 
        request.setAttribute("messRead", messRead);
        request.setAttribute("messUnRead", messUnRead);
        request.setAttribute("messUrg", messUrg);
        request.setAttribute("messImp", messImp);
        request.setAttribute("messToAnsw", messToAnsw);
        request.setAttribute("messFwd", messFwd);
    }

    private void generateAndAddMessagesOutbox(String token, HttpServletRequest request) {
        ArrayList<MessageHeader> mh = this.getHeaderMessages(token, false);
        request.setAttribute("messOutbox", mh);
    }
}
