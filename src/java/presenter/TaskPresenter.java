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
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import peopleObjects.Group;
import peopleObjects.Member;

/**
 *
 * @author gabriel
 */
@Controller
@RequestMapping("task")
public class TaskPresenter extends Project_Management_Presenter {

    public final static String urlDomain = "task/";

    /**
     * Fonction qui load la liste des taches dans un jolie tableau pour l'admin
     *
     * @param request
     * @param mm
     */
    @RequestMapping(value = {"listOfTasks"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String interceptPageListOfTasks(HttpServletRequest request, ModelMap mm) {
        try {
            //Récupération du token de la session
            String token = this.getTokenSession(request.getSession(), mm);
            if (Project_Management_Presenter.model.isValidToken(token) != null) {
                this.createTaskConsult(mm, token, false, request);
                mm.addAttribute("typeTask", "Toutes les tâches");
                return null;
            } else {
                return "connection";
            }
        } catch (Exception ex) {
            Logger.getLogger(Project_Management_Presenter.class.getName()).log(Level.SEVERE, null, ex);
            mm.addAttribute("errorMessage", "Erreur lors de la suppression.");
            return "error";
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
        try {
            //Récupération du token de la session
            String token = this.getTokenSession(request.getSession(), mm);
            if (Project_Management_Presenter.model.isValidToken(token) != null) {
                this.createTaskConsult(mm, token, true, request);
                mm.addAttribute("typeTask", "Mes tâches");
                return TaskPresenter.urlDomain + "listOfTasks";
            } else {
                return "connection";
            }
        } catch (Exception ex) {
            Logger.getLogger(Project_Management_Presenter.class.getName()).log(Level.SEVERE, null, ex);
            mm.addAttribute("errorMessage", "Erreur lors de la suppression.");
            return "error";
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
        try {
            //Récupération du token de la session
            String token = this.getTokenSession(request.getSession(), mm);
            if (token != null) {
                this.fillFormUpTask(request, mm, token);
                return null;
            } else {
                mm.addAttribute("errorMessage", "Vous n'êtes pas identifié");
                return "error";
            }
        } catch (Exception ex) {
            Logger.getLogger(Project_Management_Presenter.class.getName()).log(Level.SEVERE, null, ex);
            mm.addAttribute("errorMessage", "Erreur lors de la suppression.");
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
        try {
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
        } catch (Exception ex) {
            Logger.getLogger(Project_Management_Presenter.class.getName()).log(Level.SEVERE, null, ex);
            m.addAttribute("errorMessage", "Erreur lors de la suppression.");
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
        try {
            String alertMess = "<div class=\"alert alert-success\">"
                    + "<a class=\"close\" data-dismiss=\"alert\">×</a>"
                    + "<strong>Création de la tâche \"" + request.getParameter("titreTache").trim() + " réussie ! </strong></div>";
            try {
                //Récupération du token de la session
                String token = this.getTokenSession(request.getSession(), map);
                //Appel méthode interne correspondante
                if (token != null && TaskPresenter.model.isAdmin(token)) {
                    //Récupération des utilisateurs et des groupes
                    ArrayList<String> members = new ArrayList<String>();
                    ArrayList<String> groups = new ArrayList<String>();
                    this.parseMembersAndGroups(request, members, groups);

                    //On créé la tache
                    String result = this.createNewTask((String) request.getParameter("descriptionTache"), (String) request.getParameter("titreTache"), (String) request.getParameter("projetTache"), (String) request.getParameter("dateDebut") + " 00:00:00", (String) request.getParameter("dateFin") + " 00:00:00", (String) request.getParameter("statutTache"), Float.parseFloat((String) request.getParameter("budget")), Float.parseFloat((String) request.getParameter("consumed")), Float.parseFloat((String) request.getParameter("rae")), members, groups, token);
                    //Result == null si il y a eu une erreur, sinon un message de succès est donné avec (s'il y en a) les membres/groupes qui n'ont pas été trouvé dans la BDD
                    if (result != null) {
                        this.fillAccordionMenu(token, map);
                    } else {
                        alertMess = "<div class=\"alert alert-error\">"
                                + "<a class=\"close\" data-dismiss=\"alert\">×</a>"
                                + "<strong>Echec lors de la création de la tâche \"" + request.getParameter("titreTache").trim() + " ! </strong></div>";
                    }
                } else {
                    map.addAttribute("errorMessage", "Vous n'êtes pas identifier ou vous ne possèdez pas les droits suffisants.");
                    return "error";
                }

            } catch (SQLException ex) {
                Logger.getLogger(TaskPresenter.class.getName()).log(Level.SEVERE, null, ex);
                map.addAttribute("errorMessage", "Erreur base de données.");
                return "error";
            }
            map.addAttribute("alert", alertMess);
            map.addAttribute("typeTask", "Mes tâches");
            return TaskPresenter.urlDomain + "listOfTasks";
        } catch (Exception ex) {
            Logger.getLogger(Project_Management_Presenter.class.getName()).log(Level.SEVERE, null, ex);
            map.addAttribute("errorMessage", "Erreur lors de la suppression.");
            return "error";
        }
    }

    /**
     * Parse les membres/groupes de la tâche
     * @param request
     * @param members
     * @param groups 
     */
    private void parseMembersAndGroups(HttpServletRequest request, ArrayList<String> members, ArrayList<String> groups) {
        String[] memberss = request.getParameterValues("choixUtilsMChk");
        String[] groupss = request.getParameterValues("choixUtilsGChk");

        if (memberss != null) {
            for (String s : memberss) {
                members.add((s.split("[(]")[1].split("[)]")[0]).trim());
            }
        } else {
            members.add("");
        }
        if (groupss != null) {
            for (String s : groupss) {
                groups.add((s.split("[(]")[1].split("[)]")[0]).trim());
            }
        } else {
            groups.add("");
        }
    }

    /**
     * Intercepte la demande de chargement de la page "taskUpdated". Mets à jour la tâche.
     * @param request
     * @param map
     * @return 
     */
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
                        ArrayList<String> groups = new ArrayList<String>();

                        this.parseMembersAndGroups(request, members, groups);

                        //On créé la tache
                        Task newTask = new Task(request.getParameter("idTask").trim(), id, (String) request.getParameter("titreTache"), (String) request.getParameter("dateDebut") + " 00:00:00",
                                (String) request.getParameter("descriptionTache"), (String) request.getParameter("dateFin") + " 00:00:00", (String) request.getParameter("projetTache"),
                                Float.parseFloat((String) request.getParameter("budget")), Float.parseFloat((String) request.getParameter("consumed")), Float.parseFloat((String) request.getParameter("rae")),
                                (String) request.getParameter("statutTache"), (String) request.getParameter("chef").split(",")[0]);

                        result = this.updateTask(newTask, Integer.parseInt(request.getParameter("idTask").trim()), members, groups, token);

                        //Result == null si il y a eu une erreur, sinon un message de succès est donné avec (s'il y en a) les membres/groupes qui n'ont pas été trouvé dans la BDD
                        this.createTaskConsult(map, token, true, request);
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
                        return TaskPresenter.urlDomain + "listOfTasks";
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
            return "redirect:" + TaskPresenter.urlDomain + "checkTask?idTask=" + request.getParameter("idTask").trim() + "";
        }

    }

    /**
     * Intercepte la demande de chargement de la page "deleteTask".
     * @param request
     * @param m
     * @return 
     */
    @RequestMapping(value = {"deleteTask"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String interceptDeleteTask(HttpServletRequest request, ModelMap m) {
        try {
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
        } catch (Exception ex) {
            Logger.getLogger(Project_Management_Presenter.class.getName()).log(Level.SEVERE, null, ex);
            m.addAttribute("errorMessage", "Erreur lors du chargement/traitement  de la page.");
            return "error";
        }
    }

    /**
     * Replis les champs relatif aux tâches avec les bonnes infos si possible.
     * @param request
     * @param modelMap
     * @param token
     * @return 
     */
    public String fillFormUpTask(HttpServletRequest request, ModelMap modelMap, String token) {
        //Récuperation de l'id de la tâche
        try {
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
            String title = taskToUpdate.getTitle();
            String content = taskToUpdate.getContent();
            String creationDate = taskToUpdate.getStringCreationDate();
            String dueDate = taskToUpdate.getStringDueDate();
            String budget = Float.toString(taskToUpdate.getBudget());
            String credate = creationDate.replaceAll("/", "-").split(" ")[0];
            String ddate = dueDate.replaceAll("/", "-").split(" ")[0];
            ArrayList<Member> members = new ArrayList<Member>();
            String libMemb = "";
            String libGps = "";
            ArrayList<Group> groups = new ArrayList<Group>();
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
                    members.add(TaskPresenter.model.getInfosMember(taskString.getId()));
                } else {
                    groups.add(TaskPresenter.model.getGroupInfos(taskString.getId()));
                }
            }

            for (Member m : members) {
                libMemb += "<span class=\"label label-info\" id=\"" + m.getName() + " (" + m.getId_member() + ")\" onclick=\"decoche('" + m.getName() + " (" + m.getId_member() + ")');\">" + m.getName() + " (" + m.getId_member() + ")"
                        + "  <input type=\"checkbox\"  name=\"choixUtilsMChk\" id=\"" + m.getName() + " (" + m.getId_member() + ")_chk\" value=\"" + m.getName() + " (" + m.getId_member() + ")\" checked=true hidden></span>   ";
            }

            for (Group g : groups) {
                libGps += "<span class=\"label label-info\" id=\"" + g.getGroup_name() + " (" + g.getId_group() + ")\" onclick=\"decoche('" + g.getGroup_name() + " (" + g.getId_group() + ")');\">" + g.getGroup_name() + " (" + g.getId_group() + ")"
                        + "  <input type=\"checkbox\"  name=\"choixUtilsGChk\" id=\"" + g.getGroup_name() + " (" + g.getId_group() + ")_chk\" value=\"" + g.getGroup_name() + " (" + g.getId_group() + ")\" checked=true  hidden></span>   ";
            }
            request.setAttribute("usersTable", members);
            request.setAttribute("groupsTable", groups);
            modelMap.addAttribute("utilsG", libGps);
            modelMap.addAttribute("utilsM", libMemb);
            modelMap.addAttribute("idTask", request.getParameter("idTask"));
            modelMap.addAttribute("statut", taskToUpdate.getStatus());
            modelMap.addAttribute("chief", taskToUpdate.getChief());
        } catch (Exception ex) {
            Logger.getLogger(Project_Management_Presenter.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    /**
     * Créer la mise en page pour consulter les tâches.
     * @param mm
     * @param token
     * @param onlyUserTasks
     * @param request 
     */
    public void createTaskConsult(ModelMap mm, String token, boolean onlyUserTasks, HttpServletRequest request) {
        try {
            //Récuperation de toutes les taches
            ArrayList<TaskHeader> tasksHeader = this.getAllTasks(token, onlyUserTasks);
            request.setAttribute("tasks", tasksHeader);

            if (Project_Management_Presenter.model.isAdmin(token)) {
                mm.addAttribute("display", "submit");
            } else {
                mm.addAttribute("display", "hidden");
            }
        } catch (SQLException ex) {
            Logger.getLogger(Project_Management_Presenter.class.getName()).log(Level.SEVERE, null, ex);
            mm.addAttribute(
                    "errorMessage", "Erreur Base de donnée.");
        }
    }
}
