/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package presenter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import peopleObjects.Group;
import peopleObjects.GroupHeader;
import peopleObjects.Member;

/**
 *
 * @author gabriel
 */
@Controller
@RequestMapping("group")
public class GroupPresenter extends Project_Management_Presenter {

    public final static String urlDomain = "group/";

    /**
     * Fonction qui load la liste des groupes dans un jolie tableau pour l'admin
     *
     * @param request
     * @param mm
     */
    @RequestMapping(value = {"listOfGroup"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String interceptPageListOfGroups(HttpServletRequest request, ModelMap mm) {
        try {
            //Récupération du token de la session
            String token = this.getTokenSession(request.getSession(), mm);
            if (Project_Management_Presenter.model.isValidToken(token) != null) {
                ArrayList<GroupHeader> groupList = this.getGroups(token);
                request.setAttribute("groupsTable", groupList);

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
                return GroupPresenter.urlDomain + "listOfGroup";
            } else {
                mm.addAttribute("errorMessage", "Erreur Base de donnée.");
                return "error";
            }
        } catch (Exception ex) {
            Logger.getLogger(Project_Management_Presenter.class.getName()).log(Level.SEVERE, null, ex);
            mm.addAttribute("errorMessage", "Erreur lors du chargement/traitement  de la page.");
            return "error";
        }
    }

    /**
     * Intercepte la demande de chargement des pages "createGroup" et
     * "updateGroup"
     *
     * @param request
     * @param mm
     * @return
     */
    @RequestMapping(value = {"createGroup", "updateGroup"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String groupCreationRequest(HttpServletRequest request, ModelMap mm) {
        try {
            String token;
            String html_liste;

            // on récupère l'id de la session (token) contenu dans le cookie
            token = getTokenSession(request.getSession(), mm);

            if (Project_Management_Presenter.model.isAdmin(token)) {
                ArrayList<Member> liste_membres = getAvailableMembers(token);

                html_liste = "<select id=\"ListeMembres\" name=\"ListeMembres\" multiple=\"multiple\" size=30>";
                for (int i = 0; i < liste_membres.size(); i++) {
                    html_liste += "<option value=" + liste_membres.get(i).getId_member() + ">" + liste_membres.get(i).getName() + " " + liste_membres.get(i).getFirst_name() + "</option>";
                }
                html_liste += "</select>";
                mm.addAttribute("liste_membres_disponibles", html_liste);
                try {
                    Group gp = this.getGroupInfos(token, request.getParameter("idGroup"));
                    ArrayList<Member> members = gp.getMembers();
                    String libMemb = "";

                    for (Member memb : members) {
                        libMemb += "<span class=\"label label-info\" id=\"" + memb.getName() + " (" + memb.getId_member() + ")\" onclick=\"decoche('" + memb.getName() + " (" + memb.getId_member() + ")');\">" + memb.getName() + " (" + memb.getId_member() + ")"
                                + "  <input type=\"checkbox\"  name=\"choixUtilsMChk\" id=\"" + memb.getName() + " (" + memb.getId_member() + ")_chk\" value=\"" + memb.getName() + " (" + memb.getId_member() + ")\" checked=true hidden></span>   ";
                    }
                    mm.addAttribute("nom_groupe", gp.getGroup_name());
                    mm.addAttribute("desc_groupe", gp.getDescr());
                    mm.addAttribute("liste_membres_groupe", libMemb);
                    mm.addAttribute("idGroup", request.getParameter("idGroup"));
                    mm.addAttribute("chef_groupe", gp.getChief() != null ? gp.getChief().getId_member() : "");


                } catch (Exception ex) {
                    Logger.getLogger(Project_Management_Presenter.class.getName()).log(Level.SEVERE, null, ex);
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
     * Intercepte la demande de chargement de la page "groupCreated". Crée le
     * groupe à jour
     *
     * @param request
     * @param mm
     * @return
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
                String[] memberss = request.getParameterValues("choixUtilsMChk");
                ArrayList<Member> liste_membres = new ArrayList<Member>();

                for (String s : memberss) {
                    liste_membres.add(Project_Management_Presenter.model.getInfosMember((s.split("[(]")[1].split("[)]")[0]).trim()));
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

        return GroupPresenter.urlDomain + "listOfGroup";
    }

    /**
     * Interception de la page qui affiche les infos du groupe et la liste des
     * utilisateurs ajoutables checkGroup
     */
    @RequestMapping(value = {"checkGroup"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String interceptPageAdminCheckGroup(HttpServletRequest request, ModelMap mm) {
        try {
            String token = this.getTokenSession(request.getSession(), mm);
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
                request.setAttribute("usersTable", liste_membres);

                return null;

                //deleteMemberInGroup(String idMember, String idGroup)
            } else {
                mm.addAttribute("errorMessage", "Vous ne disposez pas de droits suffisants pour effectuer cette opération.");
                return "error";
            }
        } catch (Exception ex) {
            Logger.getLogger(Project_Management_Presenter.class.getName()).log(Level.SEVERE, null, ex);
            mm.addAttribute("errorMessage", "Erreur lors du chargement/traitement  de la page.");
            return "error";
        }
    }

    /**
     * Intercepte le demande de chargement de la page "goupeUpdated". Mets à
     * jour le groupe
     *
     * @param session
     * @param mm
     * @param request
     * @return
     */
    @RequestMapping(value = {"groupUpdated"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String intercpetGroupUpdated(HttpSession session, ModelMap mm, HttpServletRequest request) {
        try {
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
                    String[] members = request.getParameterValues("choixUtilsMChk");

                    if (members != null) {
                        for (int i = 0; i < members.length; i++) {
                            System.err.println(" MMM-*- " + members[i]);
                            members[i] = (members[i].split("[(]")[1].split("[)]")[0]).trim();
                        }
                    } else {
                        members = new String[]{""};
                    }

                    String chef = request.getParameter("chef").split(",")[0].trim();
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
        } catch (Exception ex) {
            Logger.getLogger(Project_Management_Presenter.class.getName()).log(Level.SEVERE, null, ex);
            mm.addAttribute("errorMessage", "Erreur lors du chargement/traitement  de la page.");
            return "error";
        }
    }
}
