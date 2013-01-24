/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package presenter;

import dataObjects.Recipient;
import dataObjects.RecipientType;
import dataObjects.Task;
import dataObjects.TaskHeader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
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
@RequestMapping("task")
public class TaskPresenter extends Project_Management_Presenter{
    public final static String urlDomain = "task/";
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
            return TaskPresenter.urlDomain+"listOfTasks";
        } else {
            return "connection";
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
                        return TaskPresenter.urlDomain+"listOfTasks";
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
            return "redirect:"+TaskPresenter.urlDomain+"checkTask?idTask=" + request.getParameter("idTask").trim() + "";
        }

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
            table += "onclick='window.location.href = \""+Project_Management_Presenter.domain+"/task/checkTask?idTask=" + th.getId() + "\";'>" + "<td>" + th.getProject_topic() + "</td>"
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
}
