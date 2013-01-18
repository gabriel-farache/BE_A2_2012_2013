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
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.ws.Endpoint;
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
    
    public static Project_Management_Presenter getInstance() {
        if (Project_Management_Presenter.me == null) {
            Project_Management_Presenter.me = new Project_Management_Presenter();
        }
        return Project_Management_Presenter.me;
    }
    
    public Project_Management_Presenter() {
        System.out.println("Lancement du serveur web");
        
        try {
            Endpoint.publish(
                    "http://localhost:8081/BE_A2_2012_2013/Project_Management_Presenter_Intern_Methods",
                    Project_Management_Presenter_Intern_Methods.getInstance());
            Project_Management_Presenter_Intern_Methods.getInstance();
        } catch (Exception ex) {
            Logger.getLogger(Project_Management_Presenter_Intern_Methods.class.getName()).log(Level.SEVERE, null, ex);
            
        }
    }
    
    @RequestMapping(value = {"*"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String defaultPage(ModelMap m) {
        m.addAttribute("errorMessage", "<h1>404 Page not found</h1>");
        return "error";
    }
    
    @RequestMapping(value = {"error"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String errorPage(HttpServletRequest request) {
        String token = this.getTokenSession(request.getSession());
        if (token != null) {
            if ((Project_Management_Presenter_Intern_Methods.model.isValidToken(token)) != null) {
                return null;
            }
        }
        return "connection";
    }
    
    @RequestMapping(value = {"createMessage"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String createMessage(HttpServletRequest request, ModelMap m) {
        String token = this.getTokenSession(request.getSession());
        try {
            String[] users = request.getParameterValues("selectUsersForm");
            String uss = "";
            for (String u : users) {
                uss += u + ", ";
            }
            m.addAttribute("user", uss);
        } catch (Exception e) {
        }
        try {
            String user = request.getParameter("idSender");
            m.addAttribute("user", user);
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
    public String displayConnexionPage() {
        return "connection";
    }

    /**
     * Affiche la tâche à modifier avec les champs pré-remplis
     *
     * @param request
     * @param mm
     */
    @RequestMapping(value = {"updateTask", "checkTask"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String interceptPageToUpdate(HttpServletRequest request, ModelMap mm) {

        //Récupération du token de la session
        String token = this.getTokenSession(request.getSession());
        try {
            if (Project_Management_Presenter.model.isAdmin(token)) {
                this.fillFormUpTask(request, mm, token);
                return "updateTask";
                
            } else {
                this.fillFormUpTask(request, mm, token);
                return "checkTask";
                
            }
        } catch (SQLException ex) {
            Logger.getLogger(Project_Management_Presenter.class.getName()).log(Level.SEVERE, null, ex);
            mm.addAttribute("errorMessage", "Erreur Base de donnée.");
            return "error";
        }
        
    }

    /**
     * Affiche la tâche à modifier avec les champs pré-remplis
     *
     * @param request
     * @param mm
     */
    @RequestMapping(value = {"updateUser", "checkUser"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String interceptPageToupdateUser(HttpServletRequest request, ModelMap mm) {

        //Récupération du token de la session
        String token = this.getTokenSession(request.getSession());
        try {
            if (Project_Management_Presenter.model.isAdmin(token)) {
                this.fillFormUpUser(request, mm, token);
                return "updateUser";
                
            } else {
                this.fillFormUpUser(request, mm, token);
                return "checkUser";
                
            }
        } catch (SQLException ex) {
            Logger.getLogger(Project_Management_Presenter.class.getName()).log(Level.SEVERE, null, ex);
            mm.addAttribute("errorMessage", "Erreur Base de donnée.");
            return "error";
        }
        
    }

    /**
     * Intercept the request load the create task page
     *
     * @param request
     * @return
     */
    @RequestMapping(value = {"/createTask"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String interceptCreateTask(HttpServletRequest request) {
        //Récupération du token de la session
        String token = this.getTokenSession(request.getSession());
        //Appel méthode interne correspondante
        if (token != null && Project_Management_Presenter.model.getCreateTaskForm()) {
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
        
        String token = this.getTokenSession(request.getSession());
        System.err.println("-------------------------------------------------------------------");
        //Appel méthode interne correspondante
        if (token != null) {
            //Récupération des utilisateurs et des groupes
            Map<String, String[]> allParams = request.getParameterMap();
            ArrayList<String> members = new ArrayList<String>();
            //Collections.addAll(members, allParams.get("selectUtilisateur"));
            ArrayList<String> groups = new ArrayList<String>();
            //Collections.addAll(groups, allParams.get("selectGroupe"));
            String[] memberss = request.getParameter("choixUtils").trim().split(",");
            Collections.addAll(members, memberss);
            System.err.println(" -- " + request.getParameter("titreTache") + ", " + request.getParameter("projetTache") + ", " + request.getParameter("dateDebut") + ", " + request.getParameter("dateFin") + ", " + request.getParameter("statutTache") + ", " + request.getParameter("budget") + ", " + request.getParameter("consumed") + ", " + request.getParameter("rae") + ", " + memberss + ", " + request.getParameter("descriptionTache"));

            //On créé la tache
            String result = this.createNewTask((String) request.getParameter("descriptionTache"), (String) request.getParameter("titreTache"), (String) request.getParameter("projetTache"), (String) request.getParameter("dateDebut") + " 00:00:00", (String) request.getParameter("dateFin") + " 00:00:00", (String) request.getParameter("statutTache"), Float.parseFloat((String) request.getParameter("budget")), Float.parseFloat((String) request.getParameter("consumed")), Float.parseFloat((String) request.getParameter("rae")), members, groups, token);
            //Result == null si il y a eu une erreur, sinon un message de succès est donné avec (s'il y en a) les membres/groupes qui n'ont pas été trouvé dans la BDD
            if (result != null) {
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
        //Récupération du token de la session
        System.err.println("----------------------546465342534--------------------");
        
        String token = this.getTokenSession(request.getSession());
        System.err.println("-------------------------------------------------------------------");
        //Appel méthode interne correspondante
        if (token != null) {
            try {
                model.deleteTaskAndNotify(Integer.parseInt(request.getParameter("idTask")));
            } catch (SQLException ex) {
                Logger.getLogger(Project_Management_Presenter.class.getName()).log(Level.SEVERE, null, ex);
            }
            //Récupération des utilisateurs et des groupes
            Map<String, String[]> allParams = request.getParameterMap();
            ArrayList<String> members = new ArrayList<String>();
            //Collections.addAll(members, allParams.get("selectUtilisateur"));
            ArrayList<String> groups = new ArrayList<String>();
            //Collections.addAll(groups, allParams.get("selectGroupe"));
            String[] memberss = request.getParameter("choixUtils").trim().split(",");
            Collections.addAll(members, memberss);
            System.err.println(" -- " + request.getParameter("titreTache") + ", " + request.getParameter("projetTache") + ", " + request.getParameter("dateDebut") + ", " + request.getParameter("dateFin") + ", " + request.getParameter("statutTache") + ", " + request.getParameter("budget") + ", " + request.getParameter("consumed") + ", " + request.getParameter("rae") + ", " + memberss + ", " + request.getParameter("descriptionTache"));

            //On créé la tache
            String result = this.createNewTask((String) request.getParameter("descriptionTache"), (String) request.getParameter("titreTache"), (String) request.getParameter("projetTache"), (String) request.getParameter("dateDebut") + " 00:00:00", (String) request.getParameter("dateFin") + " 00:00:00", (String) request.getParameter("statutTache"), Float.parseFloat((String) request.getParameter("budget")), Float.parseFloat((String) request.getParameter("consumed")), Float.parseFloat((String) request.getParameter("rae")), members, groups, token);
            //Result == null si il y a eu une erreur, sinon un message de succès est donné avec (s'il y en a) les membres/groupes qui n'ont pas été trouvé dans la BDD
            if (result != null) {
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
    
    @RequestMapping(value = {"supprMessage"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String interceptDeleteMess(HttpServletRequest request, ModelMap m) {
        //Récupération du token de la session
        String token = this.getTokenSession(request.getSession());

        //Récuperation de l'id de la tâche
        String idMess = request.getParameter("idMess").trim();
        //Appel méthode interne correspondante
        if (this.deleteMessage(token, idMess)) {
            m.addAttribute("errorMessage", "Suppression terminé avec succès.");
        } else {
            m.addAttribute("errorMessage", "Erreur lors de la suppression du message.");
        }
        return "error";
        //Retour
    }
    
    @RequestMapping(value = {"/deleteTask"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String interceptDeleteTask(HttpServletRequest request) {
        //Récupération du token de la session
        String token = this.getTokenSession(request.getSession());
        
        Map<String, String[]> allParams = request.getParameterMap();
        //Récuperation de l'id de la tâche
        String[] id_task_params = allParams.get("id_task");
        int idTask = Integer.parseInt(id_task_params[0]);
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
        String token = this.getTokenSession(request.getSession());
        String mmm = "";
        for (Member m : this.getUsers()) {
            mmm += "<input type=\"checkbox\" name=\"" + m.getId_member() + "\" value=\"" + m.getId_member() + "\"> " + m.getId_member() + " - " + m.getFirst_name() + "- " + m.getName() + "<br>";
        }
        mm.addAttribute("listeDesUtilisateurs", mmm);
        return null;
        //Retour
    }

    /**
     * Page appellée lorsque l'utilisateur appuit sur le bouton "Valider update"
     *
     * @param request
     */
    @RequestMapping(value = {"/confirmupdateTask.html"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String interceptPageToConfirmUpdate(HttpServletRequest request) {
        //Récupération du token de la session
        String token = this.getTokenSession(request.getSession());
        //Creation de la liste des attributs & Creation d'une map contenant la valeur associé à un champ
        Map<String, String[]> allParams = request.getParameterMap();
        //Récuperation de l'id de la tâche
        String[] id_task_params = allParams.get("id_task");
        int id_task = Integer.parseInt(id_task_params[0]);
        //Recuperation de la tâche à modifier
        Task taskToUpdate = this.getInfosTask(id_task, token);
        //Modification de la tâche dans un premier temps 
        taskToUpdate.setProjectTopic(allParams.get("projetTache")[0]);
        taskToUpdate.setTitle(allParams.get("titreTache")[0]);
        taskToUpdate.setContent(allParams.get("descriptionTache")[0]);
        taskToUpdate.setDueDate(allParams.get("dateTache")[0]);
        taskToUpdate.setStatus(allParams.get("statutTache")[0]);
        taskToUpdate.setBudget(Integer.parseInt(allParams.get("budgetTache")[0]));
        //Appel méthode interne correspondante pour modifier la tâche dans la base de données
        Boolean toReturn = updateTask(token, taskToUpdate);
        //Retour
        return (toReturn ? null : "error");
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
            
        } catch (Exception ex) {
            Logger.getLogger(Project_Management_Presenter_Intern_Methods.class.getName()).log(Level.SEVERE, null, ex);
        }
        token = this.getTokenSession(request.getSession());
        //} else {
        System.out.println("ici1");
        // token = this.getTokenSession(request.getSession());
        // }
        if (Project_Management_Presenter.model.isValidToken(token) != null) {
            this.createTaskConsult(model, token);
            return null;
        } else {
            return "connection";
        }
        
    }

    /*
     * PAGE JSP A FAIRE !!!!!!!!!!!!!!!
     */
    @RequestMapping(value = {"messageCreated"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String interceptMessageToCreate(HttpServletRequest request, ModelMap m) {
        String pageToLoad = null;
        //Récupération du token de la session
        String token = this.getTokenSession(request.getSession());
        try {
            //IL FAUT RECUPERER LES GROUPES AUSSI !!!!
            String title = request.getParameter("titreMessage");
            String message = request.getParameter("saisieMessage");
            String[] cheminsPj = request.getParameterValues("choisirPieceJointe");
            String memberss[] = request.getParameter("saisieUtilisateurDestinataire").split(",");
            String groups[] = request.getParameter("saisieGroupeDestinataire").split(",");

//            System.err.println("interceptMessageToCreate       " + memberss[0] + " - " + memberss[1] + " - " + title + " - " + message + " - " + groups[0]);
            ArrayList<String> members = new ArrayList<String>();
            
            Collections.addAll(members, memberss);
            String idSender = Project_Management_Presenter.model.isValidToken(token);
            
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
            String err = "";
            if (!this.saveMessageToMembers(idSender, members, title, message, null, pj, token)) {
                err = "<h1>Pb Membre ?</h1><br>";
                pageToLoad = "error";
            }
            if (groups.length > 0 && groups[0].trim().compareToIgnoreCase("") != 0 && groups[0].trim().compareToIgnoreCase(" ") != 0) {
                if (!this.saveMessageToGroups(idSender, groupsL, members, title, message, null, pj, token)) {
                    err += "<h1>Pb groupe ?</h1>";
                    pageToLoad = "error";
                }
            }
            m.addAttribute("errorMessage", "<h1>Message envoyé.</h1>");
            return "error";
        } catch (Exception ex) {
            Logger.getLogger(Project_Management_Presenter.class.getName()).log(Level.SEVERE, null, ex);
            m.addAttribute("errorMessage", "<h1>Pb</h1>");
            return "error";
        }
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
        String token = this.getTokenSession(request.getSession());
        //Creation de la liste des attributs & Creation d'une map contenant la valeur associé à un champ
        Map<String, String[]> allParams = request.getParameterMap();
        Enumeration allParamsName = request.getParameterNames();
        Message m = this.getMessageBody(request.getParameter("idMessage"), token);
        if (m != null) {
            modelMap.addAttribute("title", m.getTitle());
            modelMap.addAttribute("sender", m.getSender());
            modelMap.addAttribute("content", m.getContent());
            modelMap.addAttribute("idMess", request.getParameter("idMessage"));
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
    @RequestMapping(value = {"createNewUser"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String userCreationRequest(HttpServletRequest request, ModelMap mm) {
        String token;

        // on récupère l'id de la session (token) contenu dans le cookie
        token = getTokenSession(request.getSession());
        try {
            if (Project_Management_Presenter.model.isAdmin(token)) {
                return null;
            } else {
                mm.addAttribute("errorMessage", "Vous ne disposez pas de droits suffisants pour effectuer cette opération.");
                return "error";
            }
        } catch (SQLException ex) {
            Logger.getLogger(Project_Management_Presenter.class.getName()).log(Level.SEVERE, null, ex);
            mm.addAttribute("errorMessage", "Erreur Base de donnée.");
            return "error";
        }
    }

    /*
     * PAGE JSP A FAIRE !!!!!!!!!!!!!!!
     * 
     */
    @RequestMapping(value = {"userCreated"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String userCreationFormSubmitted(HttpServletRequest request, ModelMap mm) {
        try {
            String token, user_id;

            // on récupère l'id de la session (token) contenu dans le cookie
            token = getTokenSession(request.getSession());
            
            if (Project_Management_Presenter.model.isAdmin(token)) {
                Member new_user = new Member(request.getParameter("id"), request.getParameter("nom"), request.getParameter("prenom"), request.getParameter("mail"));
                String password = request.getParameter("pass");
                String id = this.createNewUser(new_user, password);
                System.err.println("-- " + id + " - " + new_user.getName() + " -- " + new_user.getFirst_name() + " -- " + new_user.getEmail());
                //System.out.println
                if (id != null) {
                    mm.addAttribute("nom", "request.getParameter(\"nom\")");
                    mm.addAttribute("prenom", "request.getParameter(\"prenom\")");
                    mm.addAttribute("errorMessage", "Succès de la création de l'utilisateur. ID : " + id);
                    return "error";
                } else {
                    mm.addAttribute("errorMessage", "Échec de la création de l'utilisateur.");
                    return "error";
                }
            } else {
                mm.addAttribute("errorMessage", "Vous ne disposez pas de droits suffisants pour effectuer cette opération.");
                return "error";
            }
        } catch (SQLException ex) {
            Logger.getLogger(Project_Management_Presenter.class.getName()).log(Level.SEVERE, null, ex);
            mm.addAttribute("errorMessage", "Erreur Base de donnée.");
            return "error";
        }
    }
    
    @RequestMapping(value = {"createGroup"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String groupCreationRequest(HttpServletRequest request, ModelMap mm) {
        try {
            String token;
            String html_liste;

            // on récupère l'id de la session (token) contenu dans le cookie
            token = getTokenSession(request.getSession());
            
            if (Project_Management_Presenter.model.isAdmin(token)) {
                ArrayList<Member> liste_membres = getAvailableMembers();
                
                html_liste = "<select id=\"ListeMembres\" name=\"ListeMembres\" multiple=\"multiple\" size=30>";
                for (int i = 0; i < liste_membres.size(); i++) {
                    html_liste += "<option value=" + liste_membres.get(i).getId_member() + ">" + liste_membres.get(i).getName() + " " + liste_membres.get(i).getFirst_name() + "</option>";
                }
                html_liste += "</select>";
                mm.addAttribute("liste_membres_disponibles", html_liste);
                
                return null;
            } else {
                mm.addAttribute("errorMessage", "Vous ne disposez pas de droits suffisants pour effectuer cette opération.");
                return "error";
            }
        } catch (SQLException ex) {
            Logger.getLogger(Project_Management_Presenter.class.getName()).log(Level.SEVERE, null, ex);
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
        try {
            String token;

            // on récupère l'id de la session (token) contenu dans le cookie
            token = getTokenSession(request.getSession());
            
            if (Project_Management_Presenter.model.isAdmin(token)) {
                String[] membres_select = request.getParameterValues("ListeMembres");
                ArrayList<Member> liste_membres = new ArrayList<Member>();
                for (int i = 0; i < membres_select.length; i++) {
                    liste_membres.add(Project_Management_Presenter.model.getInfosMember(membres_select[i]));
                }
                
                if (createGroup(liste_membres, token, request.getParameter("nomGroupe"), request.getParameter("descriptionGroupe"))) {
                    mm.addAttribute("nomGroupe", request.getParameter("nomGroupe"));
                    return null;
                } else {
                    mm.addAttribute("errorMessage", "Échec de la création du groupe.");
                    return "error";
                }
            } else {
                mm.addAttribute("errorMessage", "Vous ne disposez pas de droits suffisants pour effectuer cette opération.");
                return "error";
            }
        } catch (SQLException ex) {
            Logger.getLogger(Project_Management_Presenter.class.getName()).log(Level.SEVERE, null, ex);
            mm.addAttribute("errorMessage", "Erreur Base de donnée.");
            return "error";
        }
    }

    /* Affiche la page qui présente la liste des groupes à sélectionner
     */
    @RequestMapping(value = {"/openGroups"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String groupDisplayRequest(HttpServletRequest request, ModelMap mm) {
        //todo : implementation 
        String token = getTokenSession(request.getSession());
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
        try {
            String token = this.getTokenSession(request.getSession());
            String html_liste, table;
            Group g;
            String id_group;
            if ((Project_Management_Presenter.model.isValidToken(token) != null) && Project_Management_Presenter.model.isAdmin(token)) {
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
                    id_group = request.getParameter("idGroup");
                }
                g = getGroupInfos(token, id_group);
                mm.addAttribute("id_groupe", g.getId_group());
                mm.addAttribute("nom_groupe", g.getGroup_name());
                mm.addAttribute("desc_groupe", g.getDescr());
                //mm.addAttribute("chef_groupe", g.getChief().getName() + " " + g.getChief().getFirst_name());

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
        } catch (SQLException ex) {
            Logger.getLogger(Project_Management_Presenter.class.getName()).log(Level.SEVERE, null, ex);
            mm.addAttribute("errorMessage", "Erreur Base de donnée.");
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
    @RequestMapping(value = {"adminListOfUsers"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String interceptPageAdminListOfUsers(HttpSession session, HttpServletRequest request, ModelMap mm) {
        String pageToLoad = null;

        //Récupération du token de la session
        String token = this.getTokenSession(session);
        System.out.println("Token : " + session);
        if ((Project_Management_Presenter.model.isValidToken(token) != null)) {
            ArrayList<Member> memberList;
            //Récuperation de toutes les taches
            memberList = this.getUsers();
            if (memberList != null) {
                //Creation de la table en html contenant tous les headers de toutes les taches
                String table =
                        "<table class='table-bordered'>"
                        + "<tr id='entete'>"
                        + "<th>Nom</th>"
                        + "<th>Prénom</th>"
                        + "<th>Email</th>"
                        + "</tr>";
                for (Member m : memberList) {
                    table +=
                            "<tr id='utilisateur1' onclick='window.open(this.href=\"checkUser?idUser=" + m.getId_member() + "\"); return false;'>"
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
                Logger.getLogger(Project_Management_Presenter.class.getName()).log(Level.SEVERE, null, ex);
                mm.addAttribute("errorMessage", "Erreur Base de donnée.");
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
    @RequestMapping(value = {"adminListOfGroups"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String interceptPageAdminListOfGroups(HttpServletRequest request, ModelMap mm) {
        //Récupération du token de la session
        String token = this.getTokenSession(request.getSession());
        if (Project_Management_Presenter.model.isValidToken(token) != null) {
            ArrayList<GroupHeader> groupList = this.getGroups();
            //Creation de la table en html contenant tous les headers de toutes les taches
            String table =
                    "<table class='table-bordered'>"
                    + "<tr id='entete'>"
                    + "<th>Nom du groupe</th>"
                    + "<th>Description du groupe</th>"
                    + "</tr>";
            for (GroupHeader g : groupList) {
                table +=
                        "<tr id='groupe' onclick='window.open(this.href=\"checkGroup?id_groupe=" + g.getId_group() + "\"); return false;'>"
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
                Logger.getLogger(Project_Management_Presenter.class.getName()).log(Level.SEVERE, null, ex);
                mm.addAttribute("errorMessage", "Erreur Base de donnée.");
                return "error";
            }
            return null;
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
    @RequestMapping(value = {"adminListOfMessages"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String interceptPageAdminListOfMessages(HttpServletRequest request, ModelMap mm) {
        //Récupération du token de la session
        String token = this.getTokenSession(request.getSession());
        String pageToLoad = null;
        if (Project_Management_Presenter.model.isValidToken(token) != null) {
            
            ArrayList<MessageHeader> messageList = this.getHeaderMessages(token);
            if (messageList != null) {
                //Creation de la table en html contenant tous les headers de toutes les taches
                String table =
                        "<table class='table-bordered'>"
                        + "<tr id='entete'>"
                        + "<th>Titre du message</th>"
                        + "<th>Auteur</th>"
                        + "<th>Date de création</th>"
                        + "</tr>";
                for (MessageHeader m : messageList) {
                    table +=
                            "<tr id='groupe' onclick='window.open(this.href=\"checkMessage?idMessage=" + m.getId() + "\"); return false;'>"
                            + "<td>" + m.getTitle() + "</td>"
                            + "<td>" + m.getSender() + "</td>"
                            + "<td>" + m.getStringCreationDate() + "</td>"
                            + "</tr>";
                }
                table += "</table>";
                //Mise en place de la table 
                mm.addAttribute("messageList", table);
            } else {
                mm.addAttribute("errorMessage", "Pas de messages.");
                pageToLoad = "error";
            }
        } else {
            pageToLoad = "connection";
        }
        return pageToLoad;
    }
    
    public void createTaskConsult(ModelMap mm, String token) {
        //Récuperation de toutes les taches
        ArrayList<TaskHeader> tasksHeader = this.getAllTasks(token);
        //Creation de la table en html contenant tous les headers de toutes les taches
        String table =
                "<table class='table-bordered'>"
                + "<tr id='entete'>"
                + "<th>Nom du projet</th>"
                + "<th>Nom de la tâche</th>"
                + "<th>Statut</th>"
                + "<th>Date début</th>"
                + "<th>Date limite</th>"
                + "</tr>";
        for (TaskHeader th : tasksHeader) {
            if (th.getStatus().toString().equalsIgnoreCase("URGENT")) {
                table += "<tr id='urgent' onclick='window.open(this.href=\"checkTask?idTask=" + th.getId() + "\"); return false;'>";
                
            }
            if (th.getStatus().toString().equalsIgnoreCase("OPEN")) {
                table += "<tr id='open' onclick='window.open(this.href=\"checkTask?idTask=" + th.getId() + "\"); return false;'>";
            }
            if (th.getStatus().toString().equalsIgnoreCase("CLOSED")) {
                table += "<tr id='closed' onclick='window.open(this.href=\"checkTask?idTask=" + th.getId() + "\"); return false;'>";
            }
            table += "<td>" + th.getProject_topic() + "</td>"
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
            Logger.getLogger(Project_Management_Presenter.class.getName()).log(Level.SEVERE, null, ex);
            mm.addAttribute("errorMessage", "Erreur Base de donnée.");
        }
    }

    /**
     * Fonction qui load la liste des taches dans un jolie tableau pour l'admin
     *
     * @param request
     * @param mm
     */
    @RequestMapping(value = {"adminListOfTasks"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String interceptPageAdminListOfTasks(HttpServletRequest request, ModelMap mm) {
        //Récupération du token de la session
        String token = this.getTokenSession(request.getSession());
        if (Project_Management_Presenter.model.isValidToken(token) != null) {
            this.createTaskConsult(mm, token);
            return null;
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
    private String getTokenSession(HttpSession session) {
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
        String utils = "";
        String title = taskToUpdate.getTitle();
        String content = taskToUpdate.getContent();
        String creationDate = taskToUpdate.getStringCreationDate();
        String dueDate = taskToUpdate.getStringDueDate();
        String budget = Float.toString(taskToUpdate.getBudget());
        //Maintenant je remplis la map
        //modelMap.addAttribute("utilisateursTache", recipients);
        modelMap.addAttribute("projetTache", projectTopic);
        modelMap.addAttribute("Titre", title);
        modelMap.addAttribute("descriptionTache", content);
        modelMap.addAttribute("dateDebut", creationDate);
        modelMap.addAttribute("dateFin", dueDate);
        modelMap.addAttribute("budget", budget);
        modelMap.addAttribute("rae", Float.toString(taskToUpdate.getRae()));
        modelMap.addAttribute("consumed", Float.toString(taskToUpdate.getConsumed()));
        for (Recipient taskString : taskToUpdate.getRecipients()) {
            utils += taskString.getId() + ", ";
        }
        modelMap.addAttribute("utils", utils);
        modelMap.addAttribute("idTask", request.getParameter("idTask"));
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
            String token = this.getTokenSession(request.getSession());
            if (Project_Management_Presenter.model.isValidToken(token) != null && Project_Management_Presenter.model.isAdmin(token)) {
                return null;
            } else {
                mm.addAttribute("errorMessage", "Vous n'êtes pas administrateur.");
                return "error";
            }
        } catch (SQLException ex) {
            Logger.getLogger(Project_Management_Presenter.class.getName()).log(Level.SEVERE, null, ex);
            mm.addAttribute("errorMessage", "Erreur base de donnée.");
            return "error";
        }
        
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
            String token = this.getTokenSession(request.getSession());
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
                                Logger.getLogger(Project_Management_Presenter.class.getName()).log(Level.SEVERE, null, ex);
                                return "importXML";
                            }
                        }
                    }
                } catch (FileUploadException ex) {
                    mm.addAttribute("resultImport", "Echec de l'import");
                    Logger.getLogger(Project_Management_Presenter.class.getName()).log(Level.SEVERE, null, ex);
                    return "importXML";
                } catch (Exception ex) {
                    mm.addAttribute("resultImport", "Echec de l'import");
                    Logger.getLogger(Project_Management_Presenter.class.getName()).log(Level.SEVERE, null, ex);
                    return "importXML";
                }
                mm.addAttribute("resultImport", "Succès de l'import");
                return "importXML";
            } else {
                return "connection";
            }
        } catch (SQLException ex) {
            Logger.getLogger(Project_Management_Presenter.class.getName()).log(Level.SEVERE, null, ex);
            mm.addAttribute("errorMessage", "Erreur Base de donnée.");
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
}