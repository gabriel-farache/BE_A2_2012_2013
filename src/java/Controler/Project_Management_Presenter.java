/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Controler;

import attachmentsManagement.ManageAttachements;
import dataObjects.*;
import errorsLogging.LogErrors;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import peopleObjects.*;

/**
 *
 * @author gabriel
 */
@Controller
@RequestMapping("*")
public class Project_Management_Presenter extends Project_Management_Presenter_Intern_Methods {

    private static Project_Management_Presenter me = null;
    private static String groupes = "";
    private static String users = "";
    private static String messages = "";
    private static String tasks = "";

    public static Project_Management_Presenter getInstance() {
        if (Project_Management_Presenter.me == null) {
            Project_Management_Presenter.me = new Project_Management_Presenter();
        }
        return Project_Management_Presenter.me;
    }

    public Project_Management_Presenter() {
        System.out.println("Lancement du serveur web");

        /*try {
         Endpoint.publish(
         "http://localhost:8081/BE_A2_2012_2013/Project_Management_Presenter_Intern_Methods",
         Project_Management_Presenter_Intern_Methods.getInstance());
         Project_Management_Presenter_Intern_Methods.getInstance();
         } catch (Exception ex) {
         Logger.getLogger(Project_Management_Presenter_Intern_Methods.class.getName()).log(Level.SEVERE, null, ex);

         }*/
    }

    @RequestMapping(value = {"*"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String defaultPage(ModelMap m) {
        m.addAttribute("errorMessage", "<h1>404 Page not found</h1>");
        return "error";
    }

    @RequestMapping(value = {"error"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String errorPage(HttpServletRequest request, ModelMap m) {
        String token = this.getTokenSession(request.getSession(), m);
        if (token != null) {
            if ((Project_Management_Presenter_Intern_Methods.model.isValidToken(token)) != null) {
                return null;
            }
        }
        return "connection";
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

    @RequestMapping(value = {"connection", "index", "/"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String displayConnexionPage(HttpSession session, ModelMap m) {
        Project_Management_Presenter_Intern_Methods.getInstance();
        String token;
        this.addAttribute(m);
        token = this.getTokenSession(session, m);
        String id = Project_Management_Presenter.model.isValidToken(token);
        if (token == null || id == null) {
            return "connection";
        } else {

            return "redirect:welcome";
        }
    }

    /**
     * Affiche la tâche à modifier avec les champs pré-remplis
     *
     * @param request
     * @param mm
     */
    @RequestMapping(value = {"checkTask"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String interceptPageToUpdate(HttpServletRequest request, ModelMap mm) {

        //Récupération du token de la session
        String token = this.getTokenSession(request.getSession(), mm);
        if (token != null) {
            this.fillFormUpTask(request, mm, token);
            return null;
        } else {
            mm.addAttribute("errorMessage", "Vous n'êtes pas identifié");
            return "error";
        }

    }

    /**
     * Affiche la tâche à modifier avec les champs pré-remplis
     *
     * @param request
     * @param mm
     */
    @RequestMapping(value = {"checkUser"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String interceptPageToCheckUser(HttpServletRequest request, ModelMap mm) {

        //Récupération du token de la session
        String token = this.getTokenSession(request.getSession(), mm);
        String id = Project_Management_Presenter.model.isValidToken(token);
        if (id != null) {
            Member memb = this.getInfoUser(request.getParameter("idUser"), token);
            mm.addAttribute("id", memb.getId_member());
            mm.addAttribute("nom", memb.getName());
            mm.addAttribute("prenom", memb.getFirst_name());
            mm.addAttribute("mail", memb.getEmail());
            mm.addAttribute("groups", this.getMemberGroups(request.getParameter("idUser")));
            mm.addAttribute("tasks", this.getMemberTasks(request.getParameter("idUser")));
            this.fillAccordionMenu(token, mm);
            return "checkUser";
        } else {
            mm.addAttribute("errorMessage", "Vous n'êtes pas identifé.");
            return "error";
        }

    }

    @RequestMapping(value = {"updateProfileInfos"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String interceptPageToUpdateProfileInfos(HttpServletRequest request, ModelMap mm) {

        //Récupération du token de la session
        String token = this.getTokenSession(request.getSession(), mm);
        String id = Project_Management_Presenter.model.isValidToken(token);
        if (id != null) {
            Member memb = this.getInfoUser(id, token);
            mm.addAttribute("id", memb.getId_member());
            mm.addAttribute("nom", memb.getName());
            mm.addAttribute("prenom", memb.getFirst_name());
            mm.addAttribute("mail", memb.getEmail());
            this.fillAccordionMenu(token, mm);
            return "updateProfileInfos";
        } else {
            mm.addAttribute("errorMessage", "Vous n'êtes pas identifé.");
            return "error";
        }

    }

    /**
     * Intercept the request load the create task page
     *
     * @param request
     * @return
     */
    @RequestMapping(value = {"updateTask", "createTask"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String interceptCreateTask(HttpServletRequest request, ModelMap m) {
        //Récupération du token de la session
        String token = this.getTokenSession(request.getSession(), m);
        //Appel méthode interne correspondante
        if (token != null && Project_Management_Presenter.model.getCreateTaskForm()) {
            try {
                this.fillFormUpTask(request, m, token);
            } catch (Exception ex) {
                Logger.getLogger(Project_Management_Presenter.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        } else {
            //Retour
            return "error";
        }
    }

    /**
     * Intercept the request to create task : load the task in the DB
     *
     * @param request
     * @param modelMap
     * @return
     */
    @RequestMapping(value = {"taskCreated"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String interceptTaskCreated(HttpServletRequest request, ModelMap map) {
        //Récupération du token de la session
        System.err.println("----------------------546465342534--------------------");

        String token = this.getTokenSession(request.getSession(), map);
        System.err.println("-------------------------------------------------------------------");
        //Appel méthode interne correspondante
        if (token != null) {
            //Récupération des utilisateurs et des groupes
            ArrayList<String> members = new ArrayList<String>();
            //Collections.addAll(members, allParams.get("selectUtilisateur"));
            ArrayList<String> groups = new ArrayList<String>();
            //Collections.addAll(groups, allParams.get("selectGroupe"));
            String[] memberss = request.getParameter("choixUtilsM").replaceAll(" ", "").split(",");
            String[] groupss = request.getParameter("choixUtilsG").replaceAll(" ", "").split(",");
            Collections.addAll(members, memberss);
            Collections.addAll(groups, groupss);
            System.err.println(" -- " + request.getParameter("titreTache") + ", " + request.getParameter("projetTache") + ", " + request.getParameter("dateDebut") + ", " + request.getParameter("dateFin") + ", " + request.getParameter("statutTache") + ", " + request.getParameter("budget") + ", " + request.getParameter("consumed") + ", " + request.getParameter("rae") + ", " + memberss + ", " + request.getParameter("descriptionTache"));

            //On créé la tache
            String result = this.createNewTask((String) request.getParameter("descriptionTache"), (String) request.getParameter("titreTache"), (String) request.getParameter("projetTache"), (String) request.getParameter("dateDebut") + " 00:00:00", (String) request.getParameter("dateFin") + " 00:00:00", (String) request.getParameter("statutTache"), Float.parseFloat((String) request.getParameter("budget")), Float.parseFloat((String) request.getParameter("consumed")), Float.parseFloat((String) request.getParameter("rae")), members, groups, token);
            //Result == null si il y a eu une erreur, sinon un message de succès est donné avec (s'il y en a) les membres/groupes qui n'ont pas été trouvé dans la BDD
            if (result != null) {
                this.fillAccordionMenu(token, map);
                map.addAttribute("errorMessage", result);
                return "error";
            } else {
                return "error";
            }
        } else {
            //Retour
            return "error";
        }
    }

    @RequestMapping(value = {"taskUpdated"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String interceptUpdateCreated(HttpServletRequest request, ModelMap map) {
        try {
            //Récupération du token de la session
            String alertMess;
            boolean result;
            String token = this.getTokenSession(request.getSession(), map);
            //Appel méthode interne correspondante
            if (token != null) {
                try {
                    String id = Project_Management_Presenter.model.isValidToken(token);
                    if (id != null && Project_Management_Presenter.model.isAdmin(token)) {
                        //Récupération des utilisateurs et des groupes
                        ArrayList<String> members = new ArrayList<String>();
                        //Collections.addAll(members, allParams.get("selectUtilisateur"));
                        ArrayList<String> groups = new ArrayList<String>();
                        //Collections.addAll(groups, allParams.get("selectGroupe"));
                        String[] memberss = request.getParameter("choixUtilsM").replaceAll(" ", "").split(",");
                        String[] groupss = request.getParameter("choixUtilsG").replaceAll(" ", "").split(",");
                        Collections.addAll(members, memberss);
                        Collections.addAll(groups, groupss);

                        System.err.println(" -*- " + request.getParameter("titreTache") + ", " + request.getParameter("projetTache") + ", " + request.getParameter("dateDebut") + ", " + request.getParameter("dateFin") + ", " + request.getParameter("statutTache") + ", " + request.getParameter("budget") + ", " + request.getParameter("consumed") + ", " + request.getParameter("rae") + ", " + memberss + ", " + request.getParameter("descriptionTache"));

                        //On créé la tache
                        //public boolean updateTask(Task newTask, int id_task, ArrayList<String> idsNewMembers, ArrayList<String> idsNewGroups, String chief)
                        Task newTask = new Task(request.getParameter("idTask").trim(), id, (String) request.getParameter("titreTache"), (String) request.getParameter("dateDebut") + " 00:00:00", (String) request.getParameter("descriptionTache"), (String) request.getParameter("dateFin") + " 00:00:00", (String) request.getParameter("projetTache"), Float.parseFloat((String) request.getParameter("budget")), Float.parseFloat((String) request.getParameter("consumed")), Float.parseFloat((String) request.getParameter("rae")), (String) request.getParameter("statutTache"), (String) request.getParameter("chef").split(",")[0]);
                        System.err.println(" *-*" + newTask.getStringCreationDate() + " --- " + newTask.getStringDueDate());

                        result = this.updateTask(newTask, Integer.parseInt(request.getParameter("idTask").trim()), members, groups, token);

                        //Result == null si il y a eu une erreur, sinon un message de succès est donné avec (s'il y en a) les membres/groupes qui n'ont pas été trouvé dans la BDD
                        this.createTaskConsult(map, token, true);
                        if (result) {
                            this.fillAccordionMenu(token, map);
                            alertMess = "<div class=\"alert alert-success\">"
                                    + "<a class=\"close\" data-dismiss=\"alert\">×</a>"
                                    + "<strong>Mise à jour de la tâche \"" + request.getParameter("titreTache").trim() + "\" (" + request.getParameter("idTask").trim() + ") réussie ! </strong></div>";
                        } else {
                            alertMess = "<div class=\"alert alert-error\">"
                                    + "<a class=\"close\" data-dismiss=\"alert\">×</a>"
                                    + "<strong>Echec de la mise à jour de la tâche \"" + request.getParameter("titreTache").trim() + "\" (" + request.getParameter("idTask").trim() + ") ! </strong></div>";
                        }
                        map.addAttribute("alert", alertMess);
                        map.addAttribute("typeTask", "Mes tâches");
                        return "listOfTasks";
                    } else {
                        //Retour
                        map.addAttribute("errorMessage", "Vous n'êtes pas identifier ou vous ne possèdez pas les droits suffisants.");
                        return "error";
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(Project_Management_Presenter.class.getName()).log(Level.SEVERE, null, ex);
                    map.addAttribute("errorMessage", "Erreur base de données.");
                    return "error";
                }
            } else {
                //Retour
                map.addAttribute("errorMessage", "Vous n'êtes pas identifier");
                return "error";
            }
        } catch (Exception ex) {
            Logger.getLogger(Project_Management_Presenter.class
                    .getName()).log(Level.SEVERE, null, ex);
            String alertMess = "<div class=\"alert alert-error\">"
                    + "<a class=\"close\" data-dismiss=\"alert\">×</a>"
                    + "<strong>Echec de la mise à jour ! </strong>"
                    + "</div>";

            map.addAttribute("alert", alertMess);
            return "redirect:checkTask?idTask=" + request.getParameter("idTask").trim() + "";
        }

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
        return "inbox";
        //Retour
    }

    @RequestMapping(value = {"/deleteTask"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String interceptDeleteTask(HttpServletRequest request, ModelMap m) {
        //Récupération du token de la session
        String token = this.getTokenSession(request.getSession(), m);

        Map<String, String[]> allParams = request.getParameterMap();
        //Récuperation de l'id de la tâche
        String[] id_task_params = allParams.get("id_task");
        int idTask = Integer.parseInt(id_task_params[0].trim());
        //Appel méthode interne correspondante
        if (this.deleteTask(token, idTask)) {
            return null;
        } else {
            return "error";
        }
        //Retour
    }

    @RequestMapping(value = {"openUsers"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String displayUserToDisplay(HttpServletRequest request, ModelMap mm) {
        //Récupération du token de la session
        String token = this.getTokenSession(request.getSession(), mm);
        String mmm = "";
        for (Member m : this.getUsers()) {
            mmm += "<input type=\"checkbox\" name=\"" + m.getId_member() + "\" value=\"" + m.getId_member() + "\"> " + m.getId_member() + " - " + m.getFirst_name() + "- " + m.getName() + "<br>";
        }
        mm.addAttribute("listeDesUtilisateurs", mmm);
        return null;
        //Retour
    }

    @RequestMapping(value = {"welcome"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String interceptPageListOfTask(HttpServletRequest request, ModelMap model) {
        //Récupération du token de la session
        String token;
        //if(this.getTokenSession(request.getSession()) == null) {
        System.out.println("ici");
        try {
            token = this.login((String) request.getParameter("utilisateur"), (String) request.getParameter("pass"));
            this.setTokenSession(request.getSession(), token);
            try {
                Member memb = this.getInfoUser(Project_Management_Presenter.model.isValidToken(token), token);
                model.addAttribute("welcomeMessage", "<div class=\"alert alert-info\"> <a class=\"close\" data-dismiss=\"alert\">×</a> Bienvenue " + memb.getFirst_name() + " " + memb.getName() + " (" + memb.getId_member() + ") !</div> ");
            } catch (Exception ex) {
            }
        } catch (Exception ex) {
            Logger.getLogger(Project_Management_Presenter_Intern_Methods.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        token = this.getTokenSession(request.getSession(), model);
        try {
            this.fillAccordionMenu(token, model);
            this.addAttribute(model);

        } catch (Exception ex) {
            Logger.getLogger(Project_Management_Presenter.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        //} else {
        System.out.println("ici1");
        try {
            // token = this.getTokenSession(request.getSession());
            // }
            if (Project_Management_Presenter.model.isAdmin(token)) {
                request.getSession().setAttribute("isAdmin", true);

            }
        } catch (SQLException ex) {
            Logger.getLogger(Project_Management_Presenter.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        if (Project_Management_Presenter.model.isValidToken(token) != null) {
            this.createTaskConsult(model, token, true);
            return null;
        } else {
            return "redirect:connection";
        }

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
        return "inbox";
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
     * @param request
     * @param mm
     * @return
     */
    @RequestMapping(value = {"createNewUser", "updateUser"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String userCreationRequest(HttpServletRequest request, ModelMap mm) {
        String token;

        // on récupère l'id de la session (token) contenu dans le cookie
        token = getTokenSession(request.getSession(), mm);
        try {
            if (Project_Management_Presenter.model.isAdmin(token)) {
                try {
                    Member memb = this.getInfoUser((String) request.getParameter("idUser"), token);
                    mm.addAttribute("nom", memb.getName());
                    mm.addAttribute("prenom", memb.getFirst_name());
                    mm.addAttribute("mail", memb.getEmail());
                    mm.addAttribute("ro", "hidden");
                    mm.addAttribute("id", memb.getId_member());

                } catch (Exception e) {
                    mm.addAttribute("ro", "password");
                }
                return null;
            } else {
                mm.addAttribute("errorMessage", "Vous ne disposez pas de droits suffisants pour effectuer cette opération.");
                return "error";






            }
        } catch (SQLException ex) {
            Logger.getLogger(Project_Management_Presenter.class
                    .getName()).log(Level.SEVERE, null, ex);
            mm.addAttribute(
                    "errorMessage", "Erreur Base de donnée.");



            return "error";
        }
    }

    /*
     * PAGE JSP A FAIRE !!!!!!!!!!!!!!!
     * 
     */
    @RequestMapping(value = {"userCreated"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String userCreationFormSubmitted(HttpServletRequest request, ModelMap mm) {
        String alertMess;
        try {
            String token, user_id;

            // on récupère l'id de la session (token) contenu dans le cookie
            token = getTokenSession(request.getSession(), mm);

            if (Project_Management_Presenter.model.isAdmin(token)) {
                Member new_user = new Member(request.getParameter("id"), request.getParameter("nom"), request.getParameter("prenom"), request.getParameter("mail"));
                String id = this.createNewUser(new_user);
                System.err.println("-- " + id + " - " + new_user.getName() + " -- " + new_user.getFirst_name() + " -- " + new_user.getEmail());
                this.fillAccordionMenu(token, mm);
                //System.out.println
                if (id != null) {
                    alertMess = "<div class=\"alert alert-success\">"
                            + "<a class=\"close\" data-dismiss=\"alert\">×</a>"
                            + "<strong>Succès de la création de l'utilisateur " + request.getParameter("nom") + " " + request.getParameter("prenom") + " (" + id + ")</strong></div>";

                } else {
                    alertMess = "<div class=\"alert alert-error\">"
                            + "<a class=\"close\" data-dismiss=\"alert\">×</a>"
                            + "<strong>Échec de la création de l'utilisateur " + request.getParameter("nom") + " " + request.getParameter("prenom") + "</strong></div>";
                }

            } else {
                mm.addAttribute("errorMessage", "Vous ne disposez pas de droits suffisants pour effectuer cette opération.");
                alertMess = "<div class=\"alert alert-error\">"
                        + "<a class=\"close\" data-dismiss=\"alert\">×</a>"
                        + "<strong>Vous ne disposez pas de droits suffisants pour effectuer cette opération.</strong></div>";
            }
        } catch (SQLException ex) {
            Logger.getLogger(Project_Management_Presenter.class
                    .getName()).log(Level.SEVERE, null, ex);
            alertMess = "<div class=\"alert alert-error\">"
                    + "<a class=\"close\" data-dismiss=\"alert\">×</a>"
                    + "<strong>Erreur Base de donnée.</strong></div>";
        }
        mm.addAttribute("alert", alertMess);
        return "listOfUser";
    }

    /*
     * PAGE JSP A FAIRE !!!!!!!!!!!!!!!
     * 
     */
    @RequestMapping(value = {"userUpdated"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String userUpdateFormSubmitted(HttpServletRequest request, ModelMap mm) {
        String alertMess;
        try {
            String token, user_id;
            // on récupère l'id de la session (token) contenu dans le cookie
            token = getTokenSession(request.getSession(), mm);
            user_id = Project_Management_Presenter.model.isValidToken(token);
            if (user_id != null && Project_Management_Presenter.model.isAdmin(token)) {
                Member new_user = new Member(request.getParameter("idUser"), request.getParameter("nom"), request.getParameter("prenom"), request.getParameter("mail"));
                System.err.println("------------------------------------------------   " + request.getParameter("idUser") + " - " + new_user.getName() + " -- " + new_user.getFirst_name() + " -- " + new_user.getEmail());
                this.fillAccordionMenu(token, mm);
                //System.out.println
                if (this.updateUser(new_user)) {
                    alertMess = "<div class=\"alert alert-success\">"
                            + "<a class=\"close\" data-dismiss=\"alert\">×</a>"
                            + "<strong>Succès de la création de l'utilisateur " + request.getParameter("nom") + " " + request.getParameter("prenom") + " (" + request.getParameter("idUser") + ")</strong></div>";

                } else {
                    alertMess = "<div class=\"alert alert-error\">"
                            + "<a class=\"close\" data-dismiss=\"alert\">×</a>"
                            + "<strong>Échec de la création de l'utilisateur " + request.getParameter("nom") + " " + request.getParameter("prenom") + "</strong></div>";
                }
            } else {
                mm.addAttribute("errorMessage", "Vous ne disposez pas de droits suffisants pour effectuer cette opération.");
                alertMess = "<div class=\"alert alert-error\">"
                        + "<a class=\"close\" data-dismiss=\"alert\">×</a>"
                        + "<strong>Vous ne disposez pas de droits suffisants pour effectuer cette opération.</strong></div>";
            }
        } catch (SQLException ex) {
            Logger.getLogger(Project_Management_Presenter.class
                    .getName()).log(Level.SEVERE, null, ex);
            alertMess = "<div class=\"alert alert-error\">"
                    + "<a class=\"close\" data-dismiss=\"alert\">×</a>"
                    + "<strong>Erreur Base de donnée.</strong></div>";
        }
        mm.addAttribute("alert", alertMess);
        return "listOfUser";
    }

    @RequestMapping(value = {"profileInfosUpdated"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String updatedProfileInfosIntercept(HttpServletRequest request, ModelMap mm) {
        String alertMess;
        try {
            String token;

            // on récupère l'id de la session (token) contenu dans le cookie
            token = getTokenSession(request.getSession(), mm);
            String user_id = Project_Management_Presenter.model.isValidToken(token);
            if (user_id != null) {
                Member new_user = new Member(user_id, request.getParameter("nom"), request.getParameter("prenom"), request.getParameter("mail"));
                System.err.println("-- " + user_id + " - " + new_user.getName() + " -- " + new_user.getFirst_name() + " -- " + new_user.getEmail());
                this.fillAccordionMenu(token, mm);
                //System.out.println
                if (this.updateUserProfile(new_user, request.getParameter("pswd"))) {
                    alertMess = "<div class=\"alert alert-success\">"
                            + "<a class=\"close\" data-dismiss=\"alert\">×</a>"
                            + "<strong>Succès de la mise à jour de votre profile ! " + request.getParameter("nom") + " " + request.getParameter("prenom") + " (" + user_id + ")</strong></div>";

                } else {
                    alertMess = "<div class=\"alert alert-error\">"
                            + "<a class=\"close\" data-dismiss=\"alert\">×</a>"
                            + "<strong>Échec de la mise à jour de votre profile ! " + request.getParameter("nom") + " " + request.getParameter("prenom") + "</strong></div>";
                }
            } else {
                mm.addAttribute("errorMessage", "Vous ne disposez pas de droits suffisants pour effectuer cette opération.");
                alertMess = "<div class=\"alert alert-error\">"
                        + "<a class=\"close\" data-dismiss=\"alert\">×</a>"
                        + "<strong>Vous ne disposez pas de droits suffisants pour effectuer cette opération.</strong></div>";
            }
        } catch (Exception ex) {
            Logger.getLogger(Project_Management_Presenter.class
                    .getName()).log(Level.SEVERE, null, ex);
            alertMess = "<div class=\"alert alert-error\">"
                    + "<a class=\"close\" data-dismiss=\"alert\">×</a>"
                    + "<strong>Erreur fatale.</strong></div>";
        }
        mm.addAttribute("alert", alertMess);
        return "listOfUser";
    }

    @RequestMapping(value = {"createGroup", "updateGroup"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String groupCreationRequest(HttpServletRequest request, ModelMap mm) {
        try {
            String token;
            String html_liste;

            // on récupère l'id de la session (token) contenu dans le cookie
            token = getTokenSession(request.getSession(), mm);

            if (Project_Management_Presenter.model.isAdmin(token)) {
                ArrayList<Member> liste_membres = getAvailableMembers();

                html_liste = "<select id=\"ListeMembres\" name=\"ListeMembres\" multiple=\"multiple\" size=30>";
                for (int i = 0; i < liste_membres.size(); i++) {
                    html_liste += "<option value=" + liste_membres.get(i).getId_member() + ">" + liste_membres.get(i).getName() + " " + liste_membres.get(i).getFirst_name() + "</option>";
                }
                html_liste += "</select>";
                mm.addAttribute("liste_membres_disponibles", html_liste);
                try {
                    Group gp = this.getGroupInfos(token, request.getParameter("idGroup"));
                    ArrayList<Member> memb = gp.getMembers();
                    String membres = "";
                    for (Member m : memb) {
                        membres += m.getId_member() + ", ";
                    }
                    mm.addAttribute("nom_groupe", gp.getGroup_name());
                    mm.addAttribute("desc_groupe", gp.getDescr());
                    mm.addAttribute("liste_membres_groupe", membres);
                    mm.addAttribute("idGroup", request.getParameter("idGroup"));
                    mm.addAttribute("chef_groupe", gp.getChief() != null ? gp.getChief().getId_member() : "");
                    

                } catch (Exception ex) {
                    Logger.getLogger(Project_Management_Presenter.class
                    .getName()).log(Level.SEVERE, null, ex);
                }

                return null;
            } else {
                mm.addAttribute("errorMessage", "Vous ne disposez pas de droits suffisants pour effectuer cette opération.");
                return "error";

            }
        } catch (SQLException ex) {
            Logger.getLogger(Project_Management_Presenter.class
                    .getName()).log(Level.SEVERE, null, ex);
            mm.addAttribute("errorMessage", "Erreur Base de donnée.");

            return "error";
        }
    }

    /*
     * PAGE JSP A FAIRE !!!!!!!!!!!!!!!
     * 
     */
    @RequestMapping(value = {"groupCreated"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String groupCreationFormSubmitted(HttpServletRequest request, ModelMap mm) {
        String alertMess;
        try {
            String token;

            // on récupère l'id de la session (token) contenu dans le cookie
            token = getTokenSession(request.getSession(), mm);
            this.fillAccordionMenu(token, mm);
            if (Project_Management_Presenter.model.isAdmin(token)) {
                String[] membres_select = request.getParameter("ListeMembres").replaceAll(" ", "").split(",");
                ArrayList<Member> liste_membres = new ArrayList<Member>();
                for (int i = 0; i < membres_select.length; i++) {
                    liste_membres.add(Project_Management_Presenter.model.getInfosMember(membres_select[i].trim()));
                }

                if (this.createGroup(liste_membres, token, request.getParameter("nomGroupe").replace("'", "`"), request.getParameter("descriptionGroupe").replace("'", "`"))) {
                    mm.addAttribute("nomGroupe", request.getParameter("nomGroupe"));
                    this.fillAccordionMenu(token, mm);
                    alertMess = "<div class=\"alert alert-success\">"
                            + "<a class=\"close\" data-dismiss=\"alert\">×</a>"
                            + "<strong>Succès de la création du groupe " + request.getParameter("nomGroupe") + "</strong></div>";
                } else {
                    alertMess = "<div class=\"alert alert-error\">"
                            + "<a class=\"close\" data-dismiss=\"alert\">×</a>"
                            + "<strong>Echec lors de la création du groupe " + request.getParameter("nomGroupe") + "</strong></div>";
                }
            } else {
                alertMess = "<div class=\"alert alert-error\">"
                        + "<a class=\"close\" data-dismiss=\"alert\">×</a>"
                        + "<strong>Vous ne disposez pas de droits suffisants pour effectuer cette opération.</strong></div>";
            }
        } catch (SQLException ex) {
            Logger.getLogger(Project_Management_Presenter.class
                    .getName()).log(Level.SEVERE, null, ex);

            alertMess = "<div class=\"alert alert-error\">"
                    + "<a class=\"close\" data-dismiss=\"alert\">×</a>"
                    + "<strong>\"Erreur Base de donnée.\"</strong></div>";
        }
        mm.addAttribute("alert", alertMess);

        return "listOfGroup";
    }

    /* Affiche la page qui présente la liste des groupes à sélectionner
     */
    @RequestMapping(value = {"/openGroups"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String groupDisplayRequest(HttpServletRequest request, ModelMap mm) {
        //todo : implementation 
        String token = getTokenSession(request.getSession(), mm);
        String html_liste;

        if (Project_Management_Presenter.model.isValidToken(token) != null) {
            ArrayList<GroupHeader> liste_groupes = getGroups();

            html_liste = "<select id=\"ListeGroupes\" name=\"ListeGroupes\" size=20>";
            for (int i = 0; i < liste_groupes.size(); i++) {
                html_liste += "<option value=" + liste_groupes.get(i).getId_group() + ">" + liste_groupes.get(i).getGroup_name() + "</option>";
            }
            html_liste += "</select>";
            mm.addAttribute("liste_groupes_existants", html_liste);

            return null;
        } else {
            return "error";
        }

    }

    /* Interception de la page qui affiche les infos du groupe
     * et la liste des utilisateurs ajoutables
     * checkGroup
     */
    @RequestMapping(value = {"/checkGroup"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String interceptPageAdminCheckGroup(HttpServletRequest request, ModelMap mm) {

        String token = this.getTokenSession(request.getSession(), mm);
        String html_liste, table;
        Group g;
        String id_group;
        if ((Project_Management_Presenter.model.isValidToken(token) != null)) {// && Project_Management_Presenter.model.isAdmin(token)) {
            if (request.getParameter("ajouter_membre_groupe") != null) {
                id_group = request.getParameter("id_groupe");
                List<String> membres_a_ajouter;
                membres_a_ajouter = Arrays.asList(request.getParameterValues("ListeMembresAjouter"));
                if (addMembersGroup(token, membres_a_ajouter, id_group)) {
                } else {
                    mm.addAttribute("errorMessage", "Échec de l'ajout des membres.");
                    return "error";
                }
            } else if (request.getParameter("supprimer_membre_groupe") != null) {
                id_group = request.getParameter("id_groupe");
                List<String> membres_a_supprimer;
                membres_a_supprimer = Arrays.asList(request.getParameterValues("ListeMembresGroupe"));

                if (deleteMembersfromGroup(token, membres_a_supprimer, id_group)) {
                } else {
                    mm.addAttribute("errorMessage", "Échec de la suppression des membres.");
                    return "error";
                }
            } else {
                id_group = request.getParameter("id_groupe");
            }
            g = getGroupInfos(token, id_group);
            mm.addAttribute("id_groupe", g.getId_group());
            mm.addAttribute("nom_groupe", g.getGroup_name());
            mm.addAttribute("desc_groupe", g.getDescr());
            mm.addAttribute("chef_groupe", g.getChief() != null ? g.getChief().getId_member() : "");

            ArrayList<Member> liste_membres = g.getMembers();
            table = "<select id=\"ListeMembresGroupe\" name=\"ListeMembresGroupe\" size=20>";
            for (int i = 0; i < liste_membres.size(); i++) {
                table += "<option value=" + liste_membres.get(i).getId_member() + ">" + liste_membres.get(i).getName() + " " + liste_membres.get(i).getFirst_name() + "</option>";
            }
            table += "</select>";
            mm.addAttribute("liste_membres_groupe", table);

            ArrayList<Member> liste_utilisateurs = substractUserList(liste_membres, getUsers());
            html_liste = "<select id=\"ListeMembresAjouter\" name=\"ListeMembresAjouter\" size=20>";
            for (int i = 0; i < liste_utilisateurs.size(); i++) {
                html_liste += "<option value=" + liste_utilisateurs.get(i).getId_member() + ">" + liste_utilisateurs.get(i).getName() + " " + liste_utilisateurs.get(i).getFirst_name() + "</option>";
            }
            html_liste += "</select>";
            mm.addAttribute("liste_membres_ajouter", html_liste);

            return null;

            //deleteMemberInGroup(String idMember, String idGroup)
        } else {
            mm.addAttribute("errorMessage", "Vous ne disposez pas de droits suffisants pour effectuer cette opération.");
            return "error";
        }
    }

    /**
     * Fonction qui load la liste des utilisateurs dans un jolie tableau pour
     * l'admin
     *
     * @param request
     * @param mm
     */
    @RequestMapping(value = {"listOfUser"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String interceptPageListOfUsers(HttpSession session, HttpServletRequest request, ModelMap mm) {
        String pageToLoad = null;

        //Récupération du token de la session
        String token = this.getTokenSession(session, mm);
        System.out.println("Token : " + session);
        if ((Project_Management_Presenter.model.isValidToken(token) != null)) {
            ArrayList<Member> memberList;
            //Récuperation de toutes les taches
            memberList = this.getUsers();
            if (memberList != null) {
                //Creation de la table en html contenant tous les headers de toutes les taches
                String table =
                        "<table class=\"table table-hover\">"
                        + "<tr id='entete'>"
                        + "<th>Nom</th>"
                        + "<th>Prénom</th>"
                        + "<th>Email</th>"
                        + "</tr>";
                for (Member m : memberList) {
                    table +=
                            "<tr id='utilisateur1' onclick='window.location.href=\"checkUser?idUser=" + m.getId_member() + "\";'>"
                            + "<td>" + m.getName() + "</td>"
                            + "<td>" + m.getFirst_name() + "</td>"
                            + "<td>" + m.getEmail() + "</td>"
                            + "</tr>";

                }
                table += "</table>";
                //Mise en place de la table 
                mm.addAttribute("listOfUsers", table);
            } else {
                mm.addAttribute("errorMessage", "Aucun utilisateur.");
                pageToLoad = "error";
            }

            try {
                if (Project_Management_Presenter.model.isAdmin(token)) {
                    mm.addAttribute("display", "submit");
                } else {
                    mm.addAttribute("display", "hidden");






                }
            } catch (SQLException ex) {
                Logger.getLogger(Project_Management_Presenter.class
                        .getName()).log(Level.SEVERE, null, ex);
                mm.addAttribute(
                        "errorMessage", "Erreur Base de donnée.");



                return "error";
            }
        }
        return pageToLoad;
    }

    /**
     * Fonction qui load la liste des groupes dans un jolie tableau pour l'admin
     *
     * @param request
     * @param mm
     */
    @RequestMapping(value = {"listOfGroup"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String interceptPageListOfGroups(HttpServletRequest request, ModelMap mm) {
        //Récupération du token de la session
        String token = this.getTokenSession(request.getSession(), mm);
        if (Project_Management_Presenter.model.isValidToken(token) != null) {
            ArrayList<GroupHeader> groupList = this.getGroups();
            //Creation de la table en html contenant tous les headers de toutes les taches
            String table =
                    "<table class=\"table table-hover\">"
                    + "<tr id='entete'>"
                    + "<th>Nom du groupe</th>"
                    + "<th>Description du groupe</th>"
                    + "</tr>";
            for (GroupHeader g : groupList) {
                table +=
                        "<tr id='groupe' onclick='window.location.href = \"checkGroup?id_groupe=" + g.getId_group() + "\";'>"
                        + "<td>" + g.getGroup_name() + "</td>"
                        + "<td>" + g.getDescr() + "</td>"
                        + "</tr>";

            }
            table += "</table>";
            //Mise en place de la table 
            mm.addAttribute("groupList", table);
            try {
                if (Project_Management_Presenter.model.isAdmin(token)) {
                    mm.addAttribute("display", "submit");
                } else {
                    mm.addAttribute("display", "hidden");
                }
            } catch (SQLException ex) {
                Logger.getLogger(Project_Management_Presenter.class
                        .getName()).log(Level.SEVERE, null, ex);
                mm.addAttribute("errorMessage", "Erreur Base de donnée.");
                return "error";
            }
            return "listOfGroup";
        } else {
            mm.addAttribute("errorMessage", "Erreur Base de donnée.");
            return "error";
        }
    }

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

    public void createTaskConsult(ModelMap mm, String token, boolean onlyUserTasks) {
        //Récuperation de toutes les taches
        ArrayList<TaskHeader> tasksHeader = this.getAllTasks(token, onlyUserTasks);
        //Creation de la table en html contenant tous les headers de toutes les taches
        String table =
                "<table class=\"table table-hover\">"
                + "<tr id='entete'>"
                + "<th>Nom du projet</th>"
                + "<th>Nom de la tâche</th>"
                + "<th>Statut</th>"
                + "<th>Date début</th>"
                + "<th>Date limite</th>"
                + "</tr>";
        for (TaskHeader th : tasksHeader) {
            if (th.getStatus().toString().equalsIgnoreCase("URGENT")) {
                table += "<tr class=\"error\" ";

            }
            if (th.getStatus().toString().equalsIgnoreCase("OPEN")) {
                if (((th.getDueDate().getTimeInMillis() - (new java.util.Date().getTime())) / (1000 * 60 * 60 * 24)) <= 20) {
                    table += "<tr class=\"warning\" ";
                } else {

                    table += "<tr class=\"info\" ";
                }
            }
            if (th.getStatus().toString().equalsIgnoreCase("CLOSED")) {
                table += "<tr class=\"success\" ";
            }
            table += "onclick='window.location.href = \"checkTask?idTask=" + th.getId() + "\";'>" + "<td>" + th.getProject_topic() + "</td>"
                    + "<td>" + th.getTitle() + "</td>"
                    + "<td>" + th.getStatus().toString() + "</td>"
                    + "<td>" + th.getStringCreationDate() + "</td>"
                    + "<td>" + th.getStringDueDate() + "</td>"
                    + "</tr>";
        }
        table += "</table>";
        //Mise en place de la table 
        mm.addAttribute("table", table);
        try {
            if (Project_Management_Presenter.model.isAdmin(token)) {
                mm.addAttribute("display", "submit");
            } else {
                mm.addAttribute("display", "hidden");






            }
        } catch (SQLException ex) {
            Logger.getLogger(Project_Management_Presenter.class
                    .getName()).log(Level.SEVERE, null, ex);
            mm.addAttribute(
                    "errorMessage", "Erreur Base de donnée.");
        }
    }

    /**
     * Fonction qui load la liste des taches dans un jolie tableau pour l'admin
     *
     * @param request
     * @param mm
     */
    @RequestMapping(value = {"listOfTasks"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String interceptPageListOfTasks(HttpServletRequest request, ModelMap mm) {
        //Récupération du token de la session
        String token = this.getTokenSession(request.getSession(), mm);
        if (Project_Management_Presenter.model.isValidToken(token) != null) {
            this.createTaskConsult(mm, token, false);
            mm.addAttribute("typeTask", "Toutes les tâches");
            return null;
        } else {
            return "connection";
        }
    }

    /**
     * Fonction qui load la liste des taches dans un jolie tableau pour l'admin
     *
     * @param request
     * @param mm
     */
    @RequestMapping(value = {"myTasks"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String interceptMyTasks(HttpServletRequest request, ModelMap mm) {
        //Récupération du token de la session
        String token = this.getTokenSession(request.getSession(), mm);
        if (Project_Management_Presenter.model.isValidToken(token) != null) {
            this.createTaskConsult(mm, token, true);
            mm.addAttribute("typeTask", "Mes tâches");
            return "listOfTasks";
        } else {
            return "connection";
        }
    }

    /**
     * Create the token (as a cookie) for the session
     *
     * @param session The session on the user
     * @param token The token of the session
     */
    private void setTokenSession(HttpSession session, String token) {
        session.setAttribute("token", token);
    }

    /**
     * Gets the token associate with the current session
     *
     * @param session The session on the user
     * @return
     */
    private String getTokenSession(HttpSession session, ModelMap m) {
        this.addAttribute(m);
        return ((String) session.getAttribute("token"));
    }

    public String fillFormUpTask(HttpServletRequest request, ModelMap modelMap, String token) {
        //Récuperation de l'id de la tâche
        String id = request.getParameter("idTask");
        int id_task = Integer.parseInt(id);
        //Recuperation de la tâche à modifier
        Task taskToUpdate = this.getInfosTask(id_task, token);
        //Remplissage des champs avec appel à la base de données
        //Dans un premier temps, j'ai une map avec le nom de tous les champs mais la valeur de ces champs est vide
        //Il faut donc remplir ces champs grâce aux attributs de la base de données
        //Il faut faire une autre requete à la base de données pour recuperer les utilisateurs associé une tâche
        //String[] recipients = Arrays.copyOf(taskToUpdate.getRecipients().toArray(), taskToUpdate.getRecipients().toArray().length, String[].class);
        String projectTopic = taskToUpdate.getProjectTopic();
        String utilsM = "";
        String utilsG = "";
        String title = taskToUpdate.getTitle();
        String content = taskToUpdate.getContent();
        String creationDate = taskToUpdate.getStringCreationDate();
        String dueDate = taskToUpdate.getStringDueDate();
        String budget = Float.toString(taskToUpdate.getBudget());
        String credate = creationDate.replaceAll("/", "-").split(" ")[0];
        String ddate = dueDate.replaceAll("/", "-").split(" ")[0];
        System.err.println("****  **** *** *** " + credate + "  --  " + ddate);
        //Maintenant je remplis la map
        //modelMap.addAttribute("utilisateursTache", recipients);
        modelMap.addAttribute("projetTache", projectTopic.trim());
        modelMap.addAttribute("Titre", title.trim());
        modelMap.addAttribute("descriptionTache", content.trim());
        modelMap.addAttribute("dateDebut", credate);
        modelMap.addAttribute("dateFin", ddate);
        modelMap.addAttribute("budget", budget);
        modelMap.addAttribute("rae", Float.toString(taskToUpdate.getRae()));
        modelMap.addAttribute("consumed", Float.toString(taskToUpdate.getConsumed()));
        modelMap.addAttribute("rae", Float.toString(taskToUpdate.getRae()));

        for (Recipient taskString : taskToUpdate.getRecipients()) {
            if (taskString.getType().equals(RecipientType.USER)) {
                utilsM += taskString.getId() + ", ";
            } else {
                utilsG += taskString.getId() + ", ";
            }
        }
        modelMap.addAttribute("utilsM", utilsM);
        modelMap.addAttribute("utilsG", utilsG);
        modelMap.addAttribute("idTask", request.getParameter("idTask"));
        modelMap.addAttribute("statut", taskToUpdate.getStatus());
        modelMap.addAttribute("chief", taskToUpdate.getChief());

        return null;
    }

    public String fillFormUpUser(HttpServletRequest request, ModelMap modelMap, String token) {
        //Récuperation de l'id de la tâche
        String id = request.getParameter("idUser");

        //Recuperation de la tâche à modifier
        Member userToUpdate = this.getInfoUser(id, token);
        //Remplissage des champs avec appel à la base de données
        //Dans un premier temps, j'ai une map avec le nom de tous les champs mais la valeur de ces champs est vide
        //Il faut donc remplir ces champs grâce aux attributs de la base de données
        //Il faut faire une autre requete à la base de données pour recuperer les utilisateurs associé une tâche
        //String[] recipients = Arrays.copyOf(taskToUpdate.getRecipients().toArray(), taskToUpdate.getRecipients().toArray().length, String[].class);
        String id_member = userToUpdate.getId_member();
        String name = userToUpdate.getName();
        String firstname = userToUpdate.getFirst_name();
        String email = userToUpdate.getEmail();
        //Maintenant je remplis la map
        //modelMap.addAttribute("utilisateursTache", recipients);
        modelMap.addAttribute("id", id_member);
        modelMap.addAttribute("nom", name);
        modelMap.addAttribute("prenom", firstname);
        modelMap.addAttribute("mail", email);


        return null;
    }

    @RequestMapping(value = {"importXML"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String intercpetImportXML(HttpServletRequest request, ModelMap mm) {
        try {
            String token = this.getTokenSession(request.getSession(), mm);
            if (Project_Management_Presenter.model.isValidToken(token) != null && Project_Management_Presenter.model.isAdmin(token)) {
                return null;
            } else {
                mm.addAttribute("errorMessage", "Vous n'êtes pas administrateur.");
                return "error";
            }
        } catch (SQLException ex) {
            Logger.getLogger(Project_Management_Presenter.class
                    .getName()).log(Level.SEVERE, null, ex);
            mm.addAttribute(
                    "errorMessage", "Erreur base de donnée.");
            return "error";
        }

    }

    @RequestMapping(value = {"groupUpdated"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String intercpetGroupUpdated(HttpSession session, ModelMap mm, HttpServletRequest request) {
        String token = this.getTokenSession(session, mm);
        String id = Project_Management_Presenter.model.isValidToken(token);
        String alertMess = "<div class=\"alert alert-error\">"
                + "<a class=\"close\" data-dismiss=\"alert\">×</a>"
                + "<strong>\"Vous n'avez pas les droits suffisants.\"</strong></div>";
        try {
            if (id != null && Project_Management_Presenter.model.isAdmin(token)) {
                String idG = request.getParameter("idGroup").trim();
                String nom = request.getParameter("nom").trim();
                String descr = request.getParameter("descr").trim();
                String[] members = request.getParameter("membres").trim().replaceAll(" ,", ",").split(",");
                String chef = request.getParameter("chef").split(",")[0].trim();
                System.err.println("--------- **** //// ---- "+idG+" - "+nom+" - "+descr+" - "+ members+" "+chef);
                if (this.updateGroup(idG, nom, descr, members, chef)) {
                    alertMess = "<div class=\"alert alert-success\">"
                            + "<a class=\"close\" data-dismiss=\"alert\">×</a>"
                            + "<strong>\"Mise à jour du groupe réussi.\"</strong></div>";
                } else {
                    alertMess = "<div class=\"alert alert-error\">"
                            + "<a class=\"close\" data-dismiss=\"alert\">×</a>"
                            + "<strong>\"Echec de la mise à jour du groupe.\"</strong></div>";
                }
            }

        } catch (SQLException ex) {
            alertMess = "<div class=\"alert alert-error\">"
                    + "<a class=\"close\" data-dismiss=\"alert\">×</a>"
                    + "<strong>\"Erreur base de données.\"</strong></div>";
            Logger.getLogger(Project_Management_Presenter.class.getName()).log(Level.SEVERE, null, ex);
        }
        mm.addAttribute("alert", alertMess);
        return this.interceptPageListOfGroups(request, mm);
    }

    @RequestMapping(value = {"deconnection"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String intercpetDeco(HttpSession session, ModelMap m) {
        this.disconnectUser((String) session.getAttribute("token"));
        session.setAttribute("isAdmin", null);
        session.setAttribute("token", null);
        return "redirect:connection";
    }

    /**
     * Fonction qui execute l'import XML
     *
     * @param request
     * @param mm
     */
    @RequestMapping(value = {"miam"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String interceptXMLImported(HttpServletRequest request, ModelMap mm) {
        try {
            //Récupération du token de la session
            String token = this.getTokenSession(request.getSession(), mm);
            if (Project_Management_Presenter.model.isValidToken(token) != null && Project_Management_Presenter.model.isAdmin(token)) {

                List<FileItem> items;
                try {
                    items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);


                    for (FileItem item : items) {
                        if (!item.isFormField()) {
                            // Process form file field (input type="file").
                            String filename = item.getName();
                            InputStream filecontent;
                            try {
                                filecontent = item.getInputStream();
                                int nbRes;
                                nbRes = Project_Management_Presenter_Intern_Methods.getInstance().importXML(token, filecontent);
                                if (nbRes == 0) {
                                    LogErrors.getInstance().appendLogMessage("Parsing of {0} gave no result.", Level.WARNING, filename);
                                }
                            } catch (IOException ex) {
                                mm.addAttribute("resultImport", "Echec de l'import");
                                Logger
                                        .getLogger(Project_Management_Presenter.class
                                        .getName()).log(Level.SEVERE, null, ex);



                                return "importXML";
                            }
                        }
                    }
                } catch (FileUploadException ex) {
                    mm.addAttribute("resultImport", "Echec de l'import");
                    Logger
                            .getLogger(Project_Management_Presenter.class
                            .getName()).log(Level.SEVERE, null, ex);



                    return "importXML";
                } catch (Exception ex) {
                    mm.addAttribute("resultImport", "Echec de l'import");
                    Logger
                            .getLogger(Project_Management_Presenter.class
                            .getName()).log(Level.SEVERE, null, ex);



                    return "importXML";
                }
                mm.addAttribute("resultImport", "Succès de l'import");
                return "importXML";
            } else {
                return "connection";






            }
        } catch (SQLException ex) {
            Logger.getLogger(Project_Management_Presenter.class
                    .getName()).log(Level.SEVERE, null, ex);
            mm.addAttribute(
                    "errorMessage", "Erreur Base de donnée.");



            return "error";
        }
    }

    /**
     * Remove the Members of list part present in list all
     *
     * @param part The list to substract
     * @param all The list to substract in
     * @return Result list
     *
     */
    public ArrayList<Member> substractUserList(ArrayList<Member> part, ArrayList<Member> all) {
        boolean copy;
        ArrayList<Member> result = new ArrayList<Member>();

        for (int i = 0; i < all.size(); i++) {
            copy = true;
            for (int j = 0; j < part.size() && copy; j++) {
                if (all.get(i).getId_member().equals(part.get(j).getId_member())) {
                    copy = false;
                }
            }
            if (copy) {
                result.add(all.get(i));
            }
        }
        return result;
    }

    /**
     * Upload the attachements on the disk using the HttpServletRequest and the
     * built item.
     *
     * @param it
     * @param request
     * @return true if operation ended successfully, false otherwise.
     */
    public boolean uploadAttachments(Item it, HttpServletRequest request) {
        List<FileItem> items;
        try {
            items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);

            for (FileItem item : items) {
                if (!item.isFormField()) {
                    // Process form file field (input type="file").
                    String filename = item.getName();
                    InputStream filecontent = null;
                    try {
                        filecontent = item.getInputStream();
                    } catch (IOException ex) {
                        LogErrors.getInstance().appendLogMessage(ex.getMessage(), Level.SEVERE);
                    }
                    ManageAttachements.createAndSaveAttachments(it, filename, (FileInputStream) filecontent);
                }
            }
            return true;
        } catch (FileUploadException ex) {
            LogErrors.getInstance().appendLogMessage(ex.getMessage(), Level.SEVERE);
            return false;
        }

    }

    /**
     * Send the given attachment file of the item.
     *
     * @param it Item from which to get the file.
     * @param filename name of the attachement.
     * @param response Servlet response.
     * @return true if operation ended successfully, false otherwise.
     */
    public boolean sendAttachment(Item it, String filename, HttpServletResponse response) {
        File fileToSend = ManageAttachements.getAttachmentFile(it, filename);
        if (fileToSend != null) {
            return this.sendFile(fileToSend, response);
        }
        return false;
    }

    /**
     * Send all the attachments as a zip file.
     *
     * @param it Item from which to get the attachments.
     * @param response Servlet response.
     * @return true if operation ended successfully, false otherwise.
     */
    public boolean sendAttachmentsAsZip(Item it, HttpServletResponse response) {
        File fileToSend = ManageAttachements.getAttachementsFilesInZip(it);
        if (fileToSend != null) {
            return this.sendFile(fileToSend, response);
        }
        return false;
    }

    private boolean sendFile(File fileToSend, HttpServletResponse response) {
        ServletOutputStream sos;
        try {
            sos = response.getOutputStream();

            response.setContentLength((int) fileToSend.length());
            byte[] buffer = new byte[1024];
            FileInputStream fis;
            try {
                fis = new FileInputStream(fileToSend);
            } catch (FileNotFoundException ex) {
                LogErrors.getInstance().appendLogMessage(ex.getMessage(), Level.SEVERE);
                return false;
            }
            int nbRead;
            try {
                while ((nbRead = fis.read(buffer)) != -1) {
                    sos.write(buffer, 0, nbRead);
                }
                fis.close();
            } catch (IOException ex) {
                LogErrors.getInstance().appendLogMessage(ex.getMessage(), Level.SEVERE);
                return false;
            }
        } catch (IOException ex) {
            LogErrors.getInstance().appendLogMessage(ex.getMessage(), Level.SEVERE);
            return false;
        }
        return true;
    }

    private void fillAccordionMenu(String token, ModelMap m) {
        if (token != null) {
            ArrayList<TaskHeader> tHeaders = this.getAllTasks(token, true);
            ArrayList<MessageHeader> mHeaders = this.getHeaderMessages(token, true);
            ArrayList<Member> userss = this.getUsers();
            ArrayList<GroupHeader> groups = this.getGroups();

            Project_Management_Presenter.tasks = "";
            Project_Management_Presenter.messages = "";
            Project_Management_Presenter.users = "";
            Project_Management_Presenter.groupes = "";

            while (!tHeaders.isEmpty() || !mHeaders.isEmpty() || !userss.isEmpty() || !groups.isEmpty()) {
                if (!tHeaders.isEmpty()) {
                    Project_Management_Presenter.tasks += "<li><a href=\"checkTask?idTask=" + tHeaders.get(0).getId().trim() + "\">" + tHeaders.get(0).getTitle() + "</a></li>";
                    tHeaders.remove(0);
                }
                if (!mHeaders.isEmpty()) {
                    Project_Management_Presenter.messages += "<li><a href=\"checkMessage?idMessage=" + mHeaders.get(0).getId().trim() + "&fromInbox=yes\">" + mHeaders.get(0).getTitle() + "</a></li>";
                    mHeaders.remove(0);
                }
                if (!userss.isEmpty()) {
                    Project_Management_Presenter.users += "<li><a href=\"checkUser?idUser=" + userss.get(0).getId_member().trim() + "\">" + userss.get(0).getId_member() + "</a></li>";
                    userss.remove(0);
                }
                if (!groups.isEmpty()) {
                    Project_Management_Presenter.groupes += "<li><a href=\"checkGroup?id_groupe=" + groups.get(0).getId_group().trim() + "\">" + groups.get(0).getId_group() + "</a></li>";
                    groups.remove(0);
                }
            }

            System.err.println("********   - " + Project_Management_Presenter.tasks + "\n" + Project_Management_Presenter.users + "\n" + Project_Management_Presenter.groupes + "\n" + Project_Management_Presenter.messages);

        }
    }

    private void addAttribute(ModelMap m) {
        System.err.println("-----  " + Project_Management_Presenter.tasks + "\n" + Project_Management_Presenter.users + "\n" + Project_Management_Presenter.groupes + "\n" + Project_Management_Presenter.messages);
        m.addAttribute("myTasks", Project_Management_Presenter.tasks);
        m.addAttribute("myMessages", Project_Management_Presenter.messages);
        m.addAttribute("users", Project_Management_Presenter.users);
        m.addAttribute("groups", Project_Management_Presenter.groupes);
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
                        if (mst.get(0).equals(MessageStatus.IMPORTANT)) {
                            table[0] += "<tr class=\"warning\" id='groupe' ";
                            hasMsgs[0]++;
                            hasLine[0]++;
                        } else if (mst.get(0).equals(MessageStatus.HAVE_TO_ANSWER)) {
                            table[1] += "<tr class=\"info\" id='groupe' ";
                            hasMsgs[1]++;
                            hasLine[1]++;
                        } else if (mst.get(0).equals(MessageStatus.URGENT)) {
                            table[2] += "<tr class=\"error\" id='groupe' ";
                            hasMsgs[2]++;
                            hasLine[2]++;
                        } else if (mst.get(0).equals(MessageStatus.FORWARDED)) {
                            table[3] += "<tr class=\"success\" id='groupe' ";
                            hasMsgs[3]++;
                            hasLine[3]++;
                        } else {
                            table[4] += "<tr id='groupe' ";
                            hasMsgs[4]++;
                            hasLine[4]++;
                        }
                    } else {
                        table[5] += "<tr id='groupe' class=\"default\" ";
                        hasMsgs[5]++;
                        hasLine[5]++;
                    }
                    for (int i = 0; i < 6; i++) {
                        table[i] += hasLine[i] > 0 ? "onclick='window.location.href = \"checkMessage?idMessage=" + m.getId() + "&fromInbox=yes\";'>" + "<td>" + m.getTitle() + "</td>"
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
        String messRead = (messInbox == null ? null : messInbox[4]);
        String messUnRead = (messInbox == null ? null : messInbox[5]);
        String messUrg = (messInbox == null ? null : messInbox[2]);
        String messImp = (messInbox == null ? null : messInbox[0]);
        String messToAnsw = (messInbox == null ? null : messInbox[1]);
        String messFwd = (messInbox == null ? null : messInbox[3]);
        //Mise en place de la table 
        mm.addAttribute("messRead", messRead == null ? "Pas de messages." : messRead);
        mm.addAttribute("messUnRead", messRead == null ? "Pas de messages." : messUnRead);
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
                    table += "<tr id='groupe' onclick='window.location.href = \"checkMessage?idMessage=" + m.getId() + "&fromInbox=no\";'>" + "<td>" + m.getTitle() + "</td>"
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
