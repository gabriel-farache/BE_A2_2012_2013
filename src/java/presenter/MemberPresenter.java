/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package presenter;

import dataObjects.Task;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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
@RequestMapping("user")
public class MemberPresenter extends Project_Management_Presenter {

    public final static String urlDomain = "user/";

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
        try {
            //Récupération du token de la session
            String token = this.getTokenSession(session, mm);
            System.out.println("Token : " + session);
            if ((Project_Management_Presenter.model.isValidToken(token) != null)) {
                ArrayList<Member> memberList = this.getUsers();
                //Récuperation de toutes les taches
                request.setAttribute("usersTable", memberList);
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
                mm.addAttribute("errorMessage", "Erreur Base de donnée.");
                return "error";
            }
        } catch (Exception ex) {
            Logger.getLogger(Project_Management_Presenter.class.getName()).log(Level.SEVERE, null, ex);
            mm.addAttribute("errorMessage", "Erreur lors du chargement/traitement  de la page.");
            return "error";
        }
        return pageToLoad;
    }

    /**
     * Affiche la tâche à modifier avec les champs pré-remplis
     *
     * @param request
     * @param mm
     */
    @RequestMapping(value = {"checkUser"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String interceptPageToCheckUser(HttpServletRequest request, ModelMap mm) {
        try {
            //Récupération du token de la session
            String token = this.getTokenSession(request.getSession(), mm);
            String id = Project_Management_Presenter.model.isValidToken(token);
            if (id != null) {
                ArrayList<Group> groups = new ArrayList<Group>();
                ArrayList<Task> tasks = new ArrayList<Task>();
                String[] gps = this.getMemberGroups(request.getParameter("idUser")).replaceAll(" ", "").split(",");
                String[] tsks = this.getMemberTasks(request.getParameter("idUser")).replaceAll(" ", "").split(",");
                try {
                    for (String g : gps) {
                        groups.add(MemberPresenter.model.getGroupInfos(g));
                    }

                    for (String t : tsks) {
                        tasks.add(MemberPresenter.model.getInfosTask(Integer.parseInt(t.trim())));
                    }
                } catch (Exception ex) {
                    Logger.getLogger(Project_Management_Presenter.class.getName()).log(Level.SEVERE, null, ex);
                }

                Member memb = this.getInfoUser(request.getParameter("idUser"), token);
                mm.addAttribute("id", memb.getId_member());
                mm.addAttribute("nom", memb.getName());
                mm.addAttribute("prenom", memb.getFirst_name());
                mm.addAttribute("mail", memb.getEmail());
                request.setAttribute("groupsTable", groups);
                request.setAttribute("tasksTable", tasks);
                this.fillAccordionMenu(token, mm);
                return MemberPresenter.urlDomain + "checkUser";
            } else {
                mm.addAttribute("errorMessage", "Vous n'êtes pas identifé.");
                return "error";
            }
        } catch (Exception ex) {
            Logger.getLogger(Project_Management_Presenter.class.getName()).log(Level.SEVERE, null, ex);
            mm.addAttribute("errorMessage", "Erreur lors du chargement/traitement  de la page.");
            return "error";
        }

    }

    /**
     * Intercepte la demande de chargement de la page "updateProfileInfos".
     *
     * @param request
     * @param mm
     * @return
     */
    @RequestMapping(value = {"updateProfileInfos"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String interceptPageToUpdateProfileInfos(HttpServletRequest request, ModelMap mm) {

        try {
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
                return MemberPresenter.urlDomain + "updateProfileInfos";
            } else {
                mm.addAttribute("errorMessage", "Vous n'êtes pas identifé.");
                return "error";
            }
        } catch (Exception ex) {
            Logger.getLogger(Project_Management_Presenter.class.getName()).log(Level.SEVERE, null, ex);
            mm.addAttribute("errorMessage", "Erreur lors du chargement/traitement  de la page.");
            return "error";
        }

    }

    /**
     * Intercpete la demande de chargement des pages "createNewUser" et "updateUser".
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
            Logger.getLogger(Project_Management_Presenter.class.getName()).log(Level.SEVERE, null, ex);
            mm.addAttribute("errorMessage", "Erreur Base de donnée.");
            return "error";
        }
    }

    /**
     * Intercepte la demande de chargement de la page "userCreated". Crée
     * l'utilisateur.
     *
     * @param request
     * @param mm
     * @return
     */
    @RequestMapping(value = {"userCreated"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String userCreationFormSubmitted(HttpServletRequest request, ModelMap mm) {
        try {
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
            return MemberPresenter.urlDomain + "listOfUser";
        } catch (Exception ex) {
            Logger.getLogger(Project_Management_Presenter.class.getName()).log(Level.SEVERE, null, ex);
            mm.addAttribute("errorMessage", "Erreur lors du chargement/traitement  de la page.");
            return "error";
        }
    }

    /**
     * Intercepte la demande de chargement de la page "userUpdated". Mets à jour
     * l'utilisateur.
     *
     * @param request
     * @param mm
     * @return
     */
    @RequestMapping(value = {"userUpdated"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String userUpdateFormSubmitted(HttpServletRequest request, ModelMap mm) {
        try {
            String alertMess;
            try {
                String token, user_id;
                // on récupère l'id de la session (token) contenu dans le cookie
                token = getTokenSession(request.getSession(), mm);
                user_id = Project_Management_Presenter.model.isValidToken(token);
                if (user_id != null && Project_Management_Presenter.model.isAdmin(token)) {
                    Member new_user = new Member(request.getParameter("idUser"), request.getParameter("nom"), request.getParameter("prenom"), request.getParameter("mail"));
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
            return MemberPresenter.urlDomain + "listOfUser";
        } catch (Exception ex) {
            Logger.getLogger(Project_Management_Presenter.class.getName()).log(Level.SEVERE, null, ex);
            mm.addAttribute("errorMessage", "Erreur lors du chargement/traitement  de la page.");
            return "error";
        }
    }

    /**
     * Intercpete la demande de chargement de la page "profileInfosUpdated".
     * Mets à jour le profile.
     *
     * @param request
     * @param mm
     * @return
     */
    @RequestMapping(value = {"profileInfosUpdated"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String updatedProfileInfosIntercept(HttpServletRequest request, ModelMap mm) {
        try {
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
            return MemberPresenter.urlDomain + "listOfUser";
        } catch (Exception ex) {
            Logger.getLogger(Project_Management_Presenter.class.getName()).log(Level.SEVERE, null, ex);
            mm.addAttribute("errorMessage", "Erreur lors du chargement/traitement  de la page.");
            return "error";
        }
    }

    /**
     * Remplis les champs relatifs aux utilisateurs avec les information de ces
     * derniers.
     *
     * @param request
     * @param modelMap
     * @param token
     * @return
     */
    public String fillFormUpUser(HttpServletRequest request, ModelMap modelMap, String token) {
        //Récuperation de l'id de la tâche
        String id = request.getParameter("idUser");

        //Recuperation de la tâche à modifier
        Member userToUpdate = this.getInfoUser(id, token);
        String id_member = userToUpdate.getId_member();
        String name = userToUpdate.getName();
        String firstname = userToUpdate.getFirst_name();
        String email = userToUpdate.getEmail();
        //transmettre les infos à charger dans la page
        modelMap.addAttribute("id", id_member);
        modelMap.addAttribute("nom", name);
        modelMap.addAttribute("prenom", firstname);
        modelMap.addAttribute("mail", email);

        return null;
    }
}
