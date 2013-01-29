/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package presenter;

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
import java.util.List;
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
    private static String groupes = "";
    private static String users = "";
    private static String messages = "";
    private static String tasks = "";
    public final static String domain = "/BE_A2_2012_2013";

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
                    "http://localhost:8081/BE_A2_2012_2013/presenterService",
                    Project_Management_Presenter_Intern_Methods.getInstance());
            Project_Management_Presenter_Intern_Methods.getInstance();
        } catch (Exception ex) {
            Logger.getLogger(Project_Management_Presenter_Intern_Methods.class.getName()).log(Level.SEVERE, null, ex);

        }
    }

    @RequestMapping(value = {"*"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String defaultPage(ModelMap m) {
        this.addAttribute(m);
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

    @RequestMapping(value = {"connection", "index", "/"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String displayConnexionPage(HttpSession session, ModelMap m, HttpServletRequest request) {
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
            Logger.getLogger(Project_Management_Presenter.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(Project_Management_Presenter.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (Project_Management_Presenter.model.isValidToken(token) != null) {
            return "welcome";
        } else {
            model.addAttribute("connectionError", "<div class=\"alert alert-error\"> <a class=\"close\" data-dismiss=\"alert\">×</a> Erreur : identifiant et/ou mot de passe invalide !</div> ");

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
    protected String getTokenSession(HttpSession session, ModelMap m) {
        this.addAttribute(m);
        return ((String) session.getAttribute("token"));
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
            Logger.getLogger(Project_Management_Presenter.class
                    .getName()).log(Level.SEVERE, null, ex);
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

    protected void fillAccordionMenu(String token, ModelMap m) {
        if (token != null) {
            ArrayList<TaskHeader> tHeaders = this.getAllTasks(token, true);
            ArrayList<MessageHeader> mHeaders = this.getHeaderMessages(token, true);
            ArrayList<Member> userss = this.getUsers();
            ArrayList<GroupHeader> groups = this.getGroups(token);

            Project_Management_Presenter.tasks = "";
            Project_Management_Presenter.messages = "";
            Project_Management_Presenter.users = "";
            Project_Management_Presenter.groupes = "";

            while (!tHeaders.isEmpty() || !mHeaders.isEmpty() || !userss.isEmpty() || !groups.isEmpty()) {
                if (!tHeaders.isEmpty()) {
                    Project_Management_Presenter.tasks += "<li><a href=\"" + Project_Management_Presenter.domain + "/task/checkTask?idTask=" + tHeaders.get(0).getId().trim() + "\">" + tHeaders.get(0).getTitle() + "</a></li>";
                    tHeaders.remove(0);
                }
                if (!mHeaders.isEmpty()) {
                    Project_Management_Presenter.messages += "<li><a href=\"" + Project_Management_Presenter.domain + "/message/checkMessage?idMessage=" + mHeaders.get(0).getId().trim() + "&fromInbox=yes\">" + mHeaders.get(0).getTitle() + "</a></li>";
                    mHeaders.remove(0);
                }
                if (!userss.isEmpty()) {
                    Project_Management_Presenter.users += "<li><a href=\"" + Project_Management_Presenter.domain + "/user/checkUser?idUser=" + userss.get(0).getId_member().trim() + "\">" + userss.get(0).getId_member() + "</a></li>";
                    userss.remove(0);
                }
                if (!groups.isEmpty()) {
                    Project_Management_Presenter.groupes += "<li><a href=\"" + Project_Management_Presenter.domain + "/group/checkGroup?id_groupe=" + groups.get(0).getId_group().trim() + "\">" + groups.get(0).getId_group() + "</a></li>";
                    groups.remove(0);
                }
            }
        }
    }

    private void addAttribute(ModelMap m) {
        m.addAttribute("myTasks", Project_Management_Presenter.tasks);
        m.addAttribute("myMessages", Project_Management_Presenter.messages);
        m.addAttribute("users", Project_Management_Presenter.users);
        m.addAttribute("groups", Project_Management_Presenter.groupes);
    }
}
