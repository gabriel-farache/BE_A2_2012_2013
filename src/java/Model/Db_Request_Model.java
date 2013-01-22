/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import Controler.Project_Management_Presenter_Intern_Methods;
import dataObjects.*;
import interactionsDB.InteractDB;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import peopleObjects.*;

/**
 *
 * @author gabriel
 */
public class Db_Request_Model {
    
    private static HashMap<String, String> tokenID;
    private static Project_Management_Presenter_Intern_Methods presenter;
    private static InteractDB idb;
    private static Db_Request_Model me = null;
    
    public static Db_Request_Model getInstance(Project_Management_Presenter_Intern_Methods p) {
        if (Db_Request_Model.me == null) {
            Db_Request_Model.me = new Db_Request_Model(p);
        }
        return Db_Request_Model.me;
    }
    
    private Db_Request_Model(Project_Management_Presenter_Intern_Methods p) {
        Db_Request_Model.presenter = p;
        Db_Request_Model.tokenID = new HashMap<String, String>();
    }

    /**
     * Authenticates an user in the DB
     *
     * @param login The login of the user
     * @param hash The MD5 of the password of the user
     * @return
     */
    public String authenticate(String login, String hash) throws SQLException {
        String token = null;
        Db_Request_Model.idb = InteractDB.getInstance();
        
        boolean userExists;
        //Select motDePasse From T_Membre Where login = @login;
        userExists = Db_Request_Model.idb.authenticate(login, hash);
        if (userExists) {
            System.out.println("login   :    " + login);
            token = this.createToken(login);
        }
        return token;
    }

    /**
     * Adds members to a group By default, is not chief of the project
     *
     * @param membersID IDs of the members to add in the group
     * @param id_group The ID of the group
     * @return The list of the failed insert, null if everything was OK
     */
    public ArrayList<String> addMembersGroup(List<String> membersID, String id_group) {
        ArrayList<String> affectationFailed = new ArrayList<String>();
        for (String id_member : membersID) {
            try {
                Db_Request_Model.idb.addAffectionGroup(id_member, "" + id_group, false);
            } catch (SQLException ex) {
                Logger.getLogger(Db_Request_Model.class.getName()).log(Level.SEVERE, null, ex);
                affectationFailed.add(id_member);
            }
        }
        return (affectationFailed);
    }

    /**
     * Adds members to a group By default, is not chief of the project
     *
     * @param membersID IDs of the members to add in the group
     * @param id_group The ID of the group
     * @return The list of the failed insert, null if everything was OK
     */
    public ArrayList<String> deleteMembersGroup(List<String> membersID, String id_group) {
        ArrayList<String> affectationFailed = new ArrayList<String>();
        for (String id_member : membersID) {
            try {
                Db_Request_Model.idb.deleteMemberFromGroup(id_member, id_group);
            } catch (SQLException ex) {
                Logger.getLogger(Db_Request_Model.class.getName()).log(Level.SEVERE, null, ex);
                affectationFailed.add(id_member);
            }
        }
        return (affectationFailed);
    }

    /**
     * Create a new group
     *
     *
     * @param group The group to create
     * @return
     */
    public Boolean createGroup(Group group) throws SQLException {
        Db_Request_Model.idb = InteractDB.getInstance();
        int res;
        Db_Request_Model.idb.addGroup(group.getId_group(), group.getGroup_name(), group.getDescr());

        //ajoute le chef
        Db_Request_Model.idb.addAffectionGroup(group.getChief().getId_member(), group.getId_group(), true);

        //ajoute les membres
        for (int i = 0; i < group.getMembers().size(); i++) {
            Db_Request_Model.idb.addAffectionGroup(group.getMembers().get(i).getId_member(), group.getId_group(), false);
        }
        
        return (true);
    }

    /**
     * Create a new task
     *
     * @param task The task to add
     * @return
     */
    public String createNewTaskAndNotify(Task task, String sender, ArrayList<String> members, ArrayList<String> groups) {
        Integer sucess = null;
        //Gestion des dates
        java.util.Date start, end;
        start = new java.util.Date(Timestamp.valueOf((task.getStringCreationDate().replaceAll("/", "-") + ":00")).getTime());
        end = new java.util.Date(Timestamp.valueOf((task.getStringDueDate().replaceAll("/", "-") + ":00")).getTime());
        System.err.println("---*****************   " + (task.getStringDueDate().replaceAll("/", "-") + ":00") + " --- " + (task.getStringCreationDate().replaceAll("/", "-") + ":00"));
        //Gestion des membres et groupes
        String unfoundMembers = "";
        String unfoundGroups = "";
        //Ré
        try {
            sucess = Db_Request_Model.idb.addTask(task.getTitle(),
                    task.getContent(),
                    task.getProjectTopic(),
                    start,
                    end,
                    task.getStatus(),
                    task.getBudget(),
                    task.getConsumed(),
                    task.getRae());
        } catch (SQLException ex) {
            Logger.getLogger(Db_Request_Model.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Db_Request_Model.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (sucess != null) {
            task.setId(String.valueOf(sucess));
            //Adding task's members if they exist
            if (members != null) {
                for (String name : members) {
                    if (Db_Request_Model.idb.checkIDMemberExists(name)) {
                        try {
                            Db_Request_Model.idb.addSendTaskToMember(sender, name, sucess, RecipientType.USER, (name.compareToIgnoreCase(task.getSender()) == 0 ? true : false));
                        } catch (SQLException ex) {
                            Logger.getLogger(Db_Request_Model.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        unfoundMembers += name + "\n";
                    }
                }
            }
            //Adding task's groups if they exist
            if (groups != null) {
                for (String name : groups) {
                    if (Db_Request_Model.idb.checkIDGroupExists(name)) {
                        try {
                            Db_Request_Model.idb.addSendTaskToGroupAndAssociateToMembers(sender, name, sucess);
                        } catch (SQLException ex) {
                            Logger.getLogger(Db_Request_Model.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        unfoundGroups += name + "\n";
                    }
                }
            }
            //Sending a message to all task's members
            Member m = null;
            for (Recipient r : task.getRecipients()) {
                try {
                    m = idb.getMemberInfos(r.getId());
                } catch (SQLException ex) {
                    Logger.getLogger(Db_Request_Model.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                    SendEmail.sendEmail(m, "Nouvelle tâche.", "Bonjour " + m.getFirst_name() + " " + m.getName()
                            + "La tâche " + task.getTitle() + " a été rajouté au projet.\n");
                } catch (Exception ex) {
                    Logger.getLogger(Project_Management_Presenter_Intern_Methods.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
            if (unfoundMembers.compareTo("") == 0 && unfoundGroups.compareTo("") == 0) {
                return "Task created";
            } else {
                return "Task created <br>"
                        + "WARNING : <br>"
                        + "The following members couldn't be added as they do not exist : <br>" + unfoundMembers + "<br>"
                        + "The following groups couldn't be added as they do not exist : <br>" + unfoundGroups + "<br>";
            }
        } else {
            //System.out.println("Task could not be created, database error : " + sucess);
            //return "Task could not be created, database error : " + sucess;
            return null;
        }
    }

    /**
     * Create a new user
     *
     *
     * @param user The new user to create
     * @return
     */
    public Boolean createNewUser(Member user, String pswd) throws SQLException {
        int res;
        res = Db_Request_Model.idb.addMember(user.getId_member(), user.getName(), user.getFirst_name(), user.getEmail(), pswd, false);
        return (res == 1);
    }

    /**
     * Deletes a task and notify the members affected to this task
     *
     *
     * @param id_Task The task to delete
     * @return
     */
    public Boolean deleteTaskAndNotify(int id_Task) throws SQLException {
        Task task = Db_Request_Model.idb.getTask(id_Task);
        
        if (Db_Request_Model.idb.deleteTask(id_Task)) {
            Message message = new Message("", "", "Task " + id_Task + " deleted.", null, "The Task " + id_Task + " has been deleted from the project.");
            ArrayList<Recipient> rcpts = task.getRecipients();
            Member m;
            for (Recipient r : rcpts) {
                if (r.getType().compareTo(RecipientType.GROUP) != 0 && r.getType().compareTo(RecipientType.ALL) != 0) {
                    m = Db_Request_Model.idb.getMemberInfos(r.getId());
                    //ENVOYER UN MAIL 
                    try {
                        SendEmail.sendEmail(m, "Tâche supprimé.", "Bonjour " + m.getFirst_name() + " " + m.getName()
                                + "La tâche " + id_Task + " a été supprimé du projet.\n");
                    } catch (Exception ex) {
                        Logger.getLogger(Project_Management_Presenter_Intern_Methods.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            
            return true;
        } else {
            return false;
        }
        
    }

    /**
     * Checks if the user is identified and has rights to update tasks
     *
     * @return
     */
    public Boolean generateFormUpTask() {
        return true;
    }

    /**
     * Gets all the members
     *
     *
     * @return
     */
    public ArrayList<Member> getAvailableMembers() {
        try {
            return Db_Request_Model.idb.getAllMembers();
        } catch (SQLException ex) {
            Logger.getLogger(Db_Request_Model.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        
    }

    /**
     * Checks if the user is identified and has rights to create tasks
     *
     *
     * @return
     */
    public Boolean getCreateTaskForm() {
        return true;
    }

    /**
     * Gets the informations of a group
     *
     *
     * @param id_group The ID of the group to gets the infos
     * @return
     */
    public Group getGroupInfos(String id_group) throws SQLException {
        return Db_Request_Model.idb.getGroupInfos("" + id_group);
    }

    /**
     * Gets the headers of the messages
     *
     *
     * @return
     */
    public ArrayList<MessageHeader> getHeaderMessages(String token, boolean received) throws SQLException {
        String id = isValidToken(token);
        if (received) {
            return (Db_Request_Model.idb.getReceivedMessagesHeader(id));
        } else {
            return (Db_Request_Model.idb.getSendMessagesHeader(id));
        }
        
        
    }

    /**
     * Gets the body of a message
     *
     *
     * @param idMess The ID of the message to get in the DB
     * @return
     */
    public Message getMessageBody(String idMessage, String token, boolean received) throws SQLException {
        String id = isValidToken(token);
        if (id != null) {
            if (received) {
                return (Db_Request_Model.idb.getReceivedMessage(Integer.parseInt(idMessage), id));
            } else {
                return (Db_Request_Model.idb.getSendMessage(Integer.parseInt(idMessage), id));
            }
            
        }
        return (null);
    }

    /**
     * Gets the informations of a task
     *
     * @param id_Task The task to gets the infos
     * @return
     */
    public Task getInfosTask(int id_task) {
        try {
            Db_Request_Model.idb = InteractDB.getInstance();
            return Db_Request_Model.idb.getTask(id_task);
        } catch (SQLException ex) {
            Logger.getLogger(Db_Request_Model.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * Gets the informations of a member
     *
     * @param id_member The member to gets the infos
     * @return
     */
    public Member getInfosMember(String id_member) {
        try {
            Db_Request_Model.idb = InteractDB.getInstance();
            
            return Db_Request_Model.idb.getMemberInfos(id_member);
        } catch (SQLException ex) {
            Logger.getLogger(Db_Request_Model.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }

    /**
     * Load the form page and check if the user is connected
     *
     *
     * @return
     */
    public Boolean getNewUserForm() {
        return true;
    }

    /**
     * Gets the header of all tasks
     *
     * @return
     */
    private ArrayList<TaskHeader> getTasksHeader() {
        try {
            Db_Request_Model.idb = InteractDB.getInstance();
            return Db_Request_Model.idb.getTasksHeaders();
        } catch (SQLException ex) {
            System.err.append(ex.getLocalizedMessage() + "  " + ex.getMessage());
            Logger.getLogger(Db_Request_Model.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * Gets the header of all tasks
     *
     * @return
     */
    public ArrayList<TaskHeader> getTasksHeader(String id_member) {
        try {
            Db_Request_Model.idb = InteractDB.getInstance();
            return (id_member == null ? this.getTasksHeader() : Db_Request_Model.idb.getTasksHeaders(id_member));
        } catch (SQLException ex) {
            System.err.append(ex.getLocalizedMessage() + "  " + ex.getMessage());
            Logger.getLogger(Db_Request_Model.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * Gets all the users in the DB and add them to the page to load
     *
     *
     * @return
     */
    public ArrayList<Member> getUsers() throws SQLException {
        return Db_Request_Model.idb.getAllMembers();
    }

    /**
     *
     */
    public Boolean insertionDonnees() {
        return null;
    }

    /**
     * Checks if the ID associate with token is admin
     *
     *
     * @return
     */
    public Boolean isAdmin(String token) throws SQLException {
        String id_member = this.isValidToken(token);
        
        return (id_member != null && idb.isAdmin(id_member));
    }

    /**
     * Checks if the ID associate with token is one of the chief of the task
     *
     *
     * @return
     */
    public Boolean isAdmin(String token, String id_tache) throws SQLException {
        String id_member = this.isValidToken(token);
        
        return id_member != null && idb.isAdmin(id_member);
    }

    /**
     * Checks if the token is valid
     *
     * @param token The token of the session
     * @return
     */
    public String isValidToken(String token) {
        return (Db_Request_Model.tokenID.get(token));
    }

    /**
     *
     * @brief sauvegarde un message dans la BDD sa piece jointe et link les
     * destinataires
     * @param m message
     * @throws SQLException
     */
    public void saveMessage(Message m) throws SQLException {
        ArrayList<Recipient> recipients = m.getRecipients();
        System.err.println("-----------------------------------------------------------------------------------------------   " + m.getCreationDate().getTime());
        
        Integer idMess = Db_Request_Model.idb.addMessage(m.getTitle(), m.getCreationDate().getTime(), m.getContent());
        if (idMess != null) {
            for (Recipient recipient : recipients) {
                if (recipient.getId() != null && recipient.getId().trim().compareToIgnoreCase("") != 0) {
                    if (recipient.getType().equals(RecipientType.GROUP)) {
                        Db_Request_Model.idb.addSendMessageToGroup(m.getSender().trim(), recipient.getId(), idMess);
                        ArrayList<String> members = Db_Request_Model.idb.getMembersGroup(recipient.getId());
                        for (String member : members) {
                            if (!Db_Request_Model.idb.messageIsAssociatedWithMember(idMess, member)) {
                                Db_Request_Model.idb.addSendMessageToMember(m.getSender().trim(), member, idMess, RecipientType.USER_IN_GROUP);
                            }
                        }
                    } else if (recipient.getType().equals(RecipientType.ALL)) {
                        ArrayList<Member> members = Db_Request_Model.idb.getAllMembers();
                        for (Member member : members) {
                            Db_Request_Model.idb.addSendMessageToMember(m.getSender().trim(), member.getId_member(), idMess, RecipientType.USER);
                        }
                        
                    } else {
                        Db_Request_Model.idb.addSendMessageToMember(m.getSender().trim(), recipient.getId(), idMess, RecipientType.USER);
                        notifyNewMessage(recipient.getId(), m);
                    }
                }
            }
            if (m.hasAttachments()) {
                for (Attachment att : m.getAttachments()) {
                    Db_Request_Model.idb.addAttachedFileToMessage(Integer.parseInt(m.getId()), att);
                }
            }
        } else {
            throw new SQLException("Erreur sauvegarde message.");
        }
    }

    /**
     * Update the status of a message
     *
     *
     * @param idMess The ID of the message to update
     * @param status The new status of the message (add/remove to/from the
     * others status)
     * @param addStatus Indicates if the status is to add or to remove of the
     * status
     * @return
     */
    public void updateMessageStatus(String token, String idMessage, MessageStatus ms, boolean addStatus) throws SQLException {
        String id = isValidToken(token);
        if (id != null && ms != null) {
            if (addStatus) {
                Db_Request_Model.idb.addMessageStatus(Integer.parseInt(idMessage.trim()), ms, id);
            } else {
                Db_Request_Model.idb.delMessageStatus(Integer.parseInt(idMessage.trim()), ms, id);
            }
        }
    }

    /**
     * Updates a task and notify the affected members
     *
     * @param token
     * @param taskUpdate
     * @return
     */
    public Boolean updateTaskAndNotify(Task taskUpdate, HashMap<Recipient, Boolean[]> rcpts) {
        try {
            if (Db_Request_Model.idb.updateTask(taskUpdate) == 1 && Db_Request_Model.idb.updateRecipientsTask(rcpts, taskUpdate.getId().trim(), taskUpdate.getSender().trim()) == 1) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Db_Request_Model.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    /**
     * Créer un token
     *
     * @param id
     * @return le token associé à l'identifiant
     */
    public String createToken(String id) {
        SecureRandom random = new SecureRandom();
        //créer le token
        String token = new BigInteger(130, random).toString(32);
        //associé le token avec l'id
        Db_Request_Model.tokenID.put(token, id);
        return token;
    }
    
    public Message createMessage(String idSender, ArrayList<String> members, String title, String message, MessageStatus ms) {
        MessageHeader mH = new MessageHeader("");
        Message m = new Message(mH);
        m.setCreationDate(Calendar.getInstance().getTime().toString());
        m.setTitle(title);
        m.setSender(idSender);
        for (String member : members) {
            m.addRecipient(new MessageRecipient(RecipientType.USER, member.trim(), ms));
        }
        System.err.println("createMessage   " + idSender + " --- " + mH.getSender() + " --- " + m.getSender());
        m.setContent(message);
        return (m);
    }
    
    public void deleteMessage(String token, String idMessage) throws SQLException {
        String id = isValidToken(token);
        Db_Request_Model.idb.delMessage(Integer.parseInt(idMessage), id);
    }
    
    private void notifyNewMessage(String id, Message m) throws SQLException {
        Member user = Db_Request_Model.idb.getMemberInfos(id);
        try {
            SendEmail.sendEmail(user, "Nouveau message sur votre messagerie interne.", "Bonjour " + user.getFirst_name() + " " + user.getName()
                    + ".\nVous avez reçu un nouveau message de " + m.getSender() + " ayant pour sujet : " + m.getTitle() + " dans votre messagerie interne.\n");
        } catch (Exception ex) {
            Logger.getLogger(Project_Management_Presenter_Intern_Methods.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String generateIdMember(String nom, String prenom) {
        try {
            String nom_ok = nom.toLowerCase().replaceAll(" ", "");
            String prenom_ok = prenom.toLowerCase().replaceAll(" ", "");
            String id = nom_ok;
            int index = 0, cpt = 0;
            
            while (Db_Request_Model.idb.checkIDMemberExists(id) && index < prenom_ok.length()) {
                id = prenom_ok.substring(0, index) + nom_ok;
                index++;
            }
            if (index == prenom.length()) {
                
                while (Db_Request_Model.idb.checkIDMemberExists(id)) {
                    id = prenom_ok + nom_ok + cpt;
                    cpt++;
                }
            }
            
            return id;
        } catch (Exception ex) {
            Logger.getLogger(Db_Request_Model.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
    public String generateIdGroup(String nom_group) {
        
        try {
            String nom_ok = nom_group.toLowerCase().replaceAll(" ", "");
            String id = nom_ok.substring(0, (nom_ok.length() > 9 ? 9 : nom_ok.length()));
            int cpt = 2;
            
            while (idb.checkIDGroupExists(id)) {
                id = nom_ok + cpt;
                cpt++;
            }
            
            return id;
        } catch (Exception ex) {
            Logger.getLogger(Db_Request_Model.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public ArrayList<GroupHeader> getExistingGroups() throws SQLException {
        return Db_Request_Model.idb.getGroupsHeaderData();
    }

    /**
     * Disconnects the user
     *
     * @param token The token of the session
     */
    public void disconnectUser(String token) {
        if (token != null) {
            Db_Request_Model.tokenID.remove(token);
        }
    }
    
    public boolean updateUser(Member m) {
        return (Db_Request_Model.idb.updateMember(m) == 1);
    }
    
    public String getMemberTasks(String id_member) {
        try {
            return Db_Request_Model.idb.getTasksMember(id_member);
        } catch (SQLException ex) {
            Logger.getLogger(Db_Request_Model.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }
    
    public String getMemberGroups(String id_member) {
        try {
            return Db_Request_Model.idb.getMemberGroups(id_member);
        } catch (SQLException ex) {
            Logger.getLogger(Db_Request_Model.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }
    
    public int getNbMessagesForStatus(String id_membre, MessageStatus mst) {
        return Db_Request_Model.idb.getNbMessagesForStatus(id_membre, mst);
    }
    
    public boolean messageHasStatusAssociatedWithAMember(String id_membre, int id_message, MessageStatus status) 
    {
        try {
            return Db_Request_Model.idb.messageHasStatusAssociatedWithAMember(id_membre, id_message, status);
        } catch (SQLException ex) {
            Logger.getLogger(Db_Request_Model.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
}
