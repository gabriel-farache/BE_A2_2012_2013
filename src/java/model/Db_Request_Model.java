/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

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
import presenter.Project_Management_Presenter_Intern_Methods;

/**
 *
 * @author gabriel
 */
public class Db_Request_Model implements User_Model_Interface, Group_Model_Interface, Message_Model_Interface, Task_Model_Interface {

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
     * @return The token of the session associated with the ID of the member
     */
    @Override
    public String authenticate(String login, String hash) throws SQLException {
        String token = null;
        Db_Request_Model.idb = InteractDB.getInstance();

        boolean userExists;
        //Select motDePasse From T_Membre Where login = @login;
        userExists = Db_Request_Model.idb.authenticate(login, hash);
        if (userExists) {
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
    @Override
    public ArrayList<String> addMembersGroup(List<String> membersID, String id_group) {
        ArrayList<String> affectationFailed = new ArrayList<String>();
        Group g = this.getGroupInfos(id_group);
        for (String id_member : membersID) {
            try {
                Db_Request_Model.idb.addAffectionGroup(id_member, "" + id_group, false);
            } catch (SQLException ex) {
                Logger.getLogger(Db_Request_Model.class.getName()).log(Level.SEVERE, null, ex);
                affectationFailed.add(id_member);
            }
        }
        for (String id_Member : membersID) {
            if (!affectationFailed.contains(id_Member)) {
                try {
                    Member user = this.getInfosMember(id_Member);
                    SendEmail.sendEmail(user, "Affectation à un groupe", "Bonjour " + user.getFirst_name() + " " + user.getName()
                            + ".\nVous venez d'être affecté au groupe de travail " + g.getGroup_name() + " (" + g.getId_group() + ")\n");
                } catch (Exception ex) {
                    Logger.getLogger(Project_Management_Presenter_Intern_Methods.class.getName()).log(Level.SEVERE, null, ex);
                }
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
    @Override
    public ArrayList<String> deleteMembersGroup(List<String> membersID, String id_group) {
        ArrayList<String> affectationFailed = new ArrayList<String>();
        Group g = this.getGroupInfos(id_group);
        for (String id_member : membersID) {
            try {
                Db_Request_Model.idb.deleteMemberFromGroup(id_member, id_group);
            } catch (SQLException ex) {
                Logger.getLogger(Db_Request_Model.class.getName()).log(Level.SEVERE, null, ex);
                affectationFailed.add(id_member);
            }
        }
        for (String id_Member : membersID) {
            if (!affectationFailed.contains(id_Member)) {
                try {
                    Member user = this.getInfosMember(id_Member);
                    SendEmail.sendEmail(user, "Retrait d'un groupe", "Bonjour " + user.getFirst_name() + " " + user.getName()
                            + ".\nVous venez d'être retirer du groupe de travail " + g.getGroup_name() + " (" + g.getId_group() + ")\n");
                } catch (Exception ex) {
                    Logger.getLogger(Project_Management_Presenter_Intern_Methods.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return (affectationFailed);
    }

    /**
     * Create a new group
     *
     * @param group The group to create
     * @return TRUE : created, FALSE : error
     */
    @Override
    public Boolean createGroup(Group group) throws SQLException {
        Db_Request_Model.idb = InteractDB.getInstance();
        ArrayList<Member> membersInGroup = group.getMembers();
        Db_Request_Model.idb.addGroup(group.getId_group(), group.getGroup_name(), group.getDescr());

        //ajoute le chef
        Db_Request_Model.idb.addAffectionGroup(group.getChief().getId_member(), group.getId_group(), true);

        //ajoute les membres
        for (int i = 0; i < membersInGroup.size(); i++) {
            Db_Request_Model.idb.addAffectionGroup(membersInGroup.get(i).getId_member(), group.getId_group(), false);
            Member user = this.getInfosMember(membersInGroup.get(i).getId_member());

            this.sendEmailToMember(user, "Affectation à un groupe", "Bonjour " + user.getFirst_name() + " " + user.getName()
                    + ".\nVous venez d'être affecté au groupe de travail " + group.getGroup_name() + " (" + group.getId_group() + ")\n");
        }

        return (true);
    }

    /**
     * Create a new task
     *
     * @param task The task to create
     * @param sender The sender
     * @param members The IDs of the members recipients of the task
     * @param groups The IDs of the groups recipients of the task
     * @return The ID of the created task
     */
    @Override
    public String createNewTaskAndNotify(Task task, String sender, ArrayList<String> members, ArrayList<String> groups) {
        Integer sucess = null;
        //Gestion des dates
        java.util.Date start, end;
        start = new java.util.Date(Timestamp.valueOf((task.getStringCreationDate().replaceAll("/", "-") + ":00")).getTime());
        end = new java.util.Date(Timestamp.valueOf((task.getStringDueDate().replaceAll("/", "-") + ":00")).getTime());
        //Gestion des membres et groupes
        String unfoundMembers = "";
        String unfoundGroups = "";
        //Ré
        try {
            sucess = Db_Request_Model.idb.addTask(task.getTitle(), task.getContent(), task.getProjectTopic(), start, end, task.getStatus(), task.getBudget(), task.getConsumed(), task.getRae());
        } catch (SQLException ex) {
            Logger.getLogger(Db_Request_Model.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Db_Request_Model.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (sucess != null) {

            task.setId(String.valueOf(sucess));
            //Adding task's members if they exist
            unfoundMembers += this.associateMembersWithTask(sucess, task, sender, members);
            //Adding task's groups if they exist
            unfoundGroups += this.associateGroupsWithTask(sucess, sender, groups);
            for (String m : members) {
                task.addRecipient(new Recipient(RecipientType.USER, m));
            }
            for (String g : groups) {
                task.addRecipient(new Recipient(RecipientType.GROUP, g));
            }
            //Sending a message to all task's members
            this.notifyMembersNewTask(task);
            if (unfoundMembers.compareTo("") == 0 && unfoundGroups.compareTo("") == 0) {
                return "Task created";
            } else {
                return "Task created <br>"
                        + "WARNING : <br>"
                        + "The following members couldn't be added as they do not exist : <br>" + unfoundMembers + "<br>"
                        + "The following groups couldn't be added as they do not exist : <br>" + unfoundGroups + "<br>";
            }
        } else {
            return null;
        }
    }

    /**
     * Associate members with a task
     *
     * @param idTask The ID of the task with which associate the members
     * @param task The task
     * @param sender The sender of the task
     * @param members The list of the IDs of the members to add in the task
     * @return
     */
    private String associateMembersWithTask(Integer idTask, Task task, String sender, ArrayList<String> members) {
        String unfoundMembers = "";
        if (members != null) {
            for (String name : members) {
                if (Db_Request_Model.idb.checkIDMemberExists(name)) {
                    try {
                        Db_Request_Model.idb.addSendTaskToMember(sender, name, idTask, RecipientType.USER, (name.compareToIgnoreCase(task.getSender()) == 0 ? true : false));
                    } catch (SQLException ex) {
                        Logger.getLogger(Db_Request_Model.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    unfoundMembers += name + "\n";
                }
            }
        }
        return unfoundMembers;
    }

    /**
     * Associate groups with a task
     *
     * @param idTask The ID of the task with which associate the groups
     * @param sender The sender of the task
     * @param groups The list of the IDs of the groups to add in the task
     * @return The unfounded groups
     */
    private String associateGroupsWithTask(Integer sucess, String sender, ArrayList<String> groups) {
        String unfoundGroups = "";
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
        return unfoundGroups;
    }

    /**
     * Notify the members of the task that they are affected to it.
     *
     * @param task
     */
    private void notifyMembersNewTask(Task task) {
        Member m;
        for (Recipient r : task.getRecipients()) {
            try {
                if (!r.getType().equals(RecipientType.GROUP)) {
                    m = idb.getMemberInfos(r.getId());
                    this.sendEmailToMember(r.getId(), "Nouvelle tâche.", "Bonjour " + m.getFirst_name() + " " + m.getName()
                            + "La tâche " + task.getTitle() + " a été rajouté au projet.\n");

                } else {
                    ArrayList<Member> members = idb.getGroupInfos(r.getId()).getMembers();
                    for (Member mm : members) {
                        this.sendEmailToMember(mm, "Nouvelle tâche.", "Bonjour " + mm.getFirst_name() + " " + mm.getName()
                                + "La tâche " + task.getTitle() + " a été rajouté au projet.\n");
                    }
                }

            } catch (SQLException ex) {
                Logger.getLogger(Db_Request_Model.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    /**
     * Create a new user
     *
     * @param user The new user to create
     * @param pswd The password of the new user
     * @return TRUE : create, FALSE : error
     */
    @Override
    public Boolean createNewUser(Member user, String pswd) throws SQLException {
        int res;
        res = Db_Request_Model.idb.addMember(user.getId_member(), user.getName(), user.getFirst_name(), user.getEmail(), pswd, false);
        return (res == 1);
    }

    /**
     * Deletes a task and notify the members affected to this task
     *
     * @param id_Task The task to delete
     * @return TRUE : ok, FALSE : error
     */
    @Override
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
                    this.sendEmailToMember(m, "Tâche supprimé.", "Bonjour " + m.getFirst_name() + " " + m.getName()
                            + "La tâche " + id_Task + " a été supprimé du projet.\n");
                }
            }

            return true;
        } else {
            return false;
        }

    }

    /**
     * Checks if the user is identified and has rights to update tasks DO
     * NOTHING
     *
     * @return
     */
    @Override
    public Boolean generateFormUpTask() {
        return true;
    }

    /**
     * Gets all the members
     *
     * @return The list of all the members
     */
    @Override
    public ArrayList<Member> getAvailableMembers() {
        try {
            return Db_Request_Model.idb.getAllMembers();
        } catch (SQLException ex) {
            Logger.getLogger(Db_Request_Model.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

    /**
     * Checks if the user is identified and has rights to create tasks DO
     * NOTHING
     *
     * @return
     */
    @Override
    public Boolean getCreateTaskForm() {
        return true;
    }

    /**
     * Gets the informations of a group
     *
     * @param id_group The ID of the group to gets the infos
     * @return The group with all it informations
     */
    @Override
    public Group getGroupInfos(String id_group) {
        try {
            return Db_Request_Model.idb.getGroupInfos("" + id_group);
        } catch (SQLException ex) {
            Logger.getLogger(Db_Request_Model.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * Gets the headers of the messages
     *
     * @param token The token of the session
     * @param received TRUE : get the received messages header, FALSE : get the
     * send messages header
     * @return The headers of the messages
     * @throws SQLException
     */
    @Override
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
     * @param idMess The ID of the message to get in the DB
     * @param token The token of the session
     * @param received TRUE : get the body of a received message, FALSE : get
     * the body of a send message
     * @return The message with all it informations
     * @throws SQLException
     */
    @Override
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
     * @return The task with it informations
     */
    @Override
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
     * @return The member with all the infos
     */
    @Override
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
     * Load the form page and check if the user is connected DO NOTHING
     *
     * @return
     */
    @Override
    public Boolean getNewUserForm() {
        return true;
    }

    /**
     * Gets the headers of all tasks
     *
     * @return The headers of all tasks
     */
    @Override
    public ArrayList<TaskHeader> getTasksHeader() {
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
     * Gets the header of all tasks for a given member
     *
     * @param id_member The ID of the member to get the tasks
     * @return The headers of all tasks for a given member
     */
    @Override
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
     * @return The list of all the members in the DB
     * @throws SQLException
     */
    @Override
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
     * @param token The token to check
     * @return TRUE : is admin, FALSE : is not admin
     * @throws SQLException
     */
    public Boolean isAdmin(String token) throws SQLException {
        String id_member = this.isValidToken(token);

        return (id_member != null && idb.isAdmin(id_member));
    }

    /**
     * Checks if the ID associate with token is one of the chief of the task
     *
     * @param token The token to check
     * @param id_tache The ID of the task to check
     * @return TRUE : is admin for the task, FALSE : is not admin for the task
     * @throws SQLException
     */
    public Boolean isAdmin(String token, String id_tache) throws SQLException {
        String id_member = this.isValidToken(token);

        return id_member != null && idb.isAdmin(id_member);
    }

    /**
     * Checks if the token is valid
     *
     * @param token The token of the session
     * @return The ID of the member if valid, null else
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
    @Override
    public void saveMessage(Message m) throws SQLException {
        ArrayList<Recipient> recipients = m.getRecipients();
        ArrayList<String> gpMemb;
        Integer idMess = Db_Request_Model.idb.addMessage(m.getTitle(), m.getCreationDate().getTime(), m.getContent());

        if (idMess != null) {
            for (Recipient recipient : recipients) {
                if (recipient.getId() != null && recipient.getId().trim().compareToIgnoreCase("") != 0) {
                    if (recipient.getType().equals(RecipientType.GROUP)) {
                        Db_Request_Model.idb.addSendMessageToGroupAndAssociateToMembers(m.getSender().trim(), recipient.getId(), idMess);
                        gpMemb = Db_Request_Model.idb.getMembersGroup(recipient.getId());
                        for (String mem : gpMemb) {
                            notifyNewMessage(mem, m);
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
    @Override
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
     * @param rcpts The recipients to Add if value = true, to delete if value =
     * false
     * @param taskUpdate The new task description
     * @return
     */
    @Override
    public Boolean updateTaskAndNotify(Task taskUpdate, HashMap<Recipient, Boolean[]> rcpts) {
        try {
            if (Db_Request_Model.idb.updateTask(taskUpdate) == 1) {
                if (rcpts != null && !rcpts.isEmpty()) {
                    if (Db_Request_Model.idb.updateRecipientsTask(rcpts, taskUpdate.getId().trim(), taskUpdate.getSender().trim()) == 1) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return true;
                }
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

    /**
     * Create a new message (do not save in the DB)
     *
     * @param idSender The sender of the messages
     * @param members The members/groups recipients of the message
     * @param title The title of the messgae
     * @param message The body of the message
     * @param ms The status of the messages
     * @return The created message
     */
    @Override
    public Message createMessage(String idSender, ArrayList<Recipient> members, String title, String message, MessageStatus ms) {
        MessageHeader mH = new MessageHeader("");
        Message m = new Message(mH);
        m.setCreationDate(Calendar.getInstance().getTime().toString());
        m.setTitle(title);
        m.setSender(idSender);
        for (Recipient member : members) {
            m.addRecipient(new MessageRecipient(member.getType(), member.getId().trim(), ms));
        }
        m.setContent(message);
        return (m);
    }

    /**
     * Deletes a message. ONLY for RECEIVED messages
     *
     * @param token The token of the session
     * @param idMessage The ID of the message to delete
     * @throws SQLException
     */
    @Override
    public void deleteMessage(String token, String idMessage) throws SQLException {
        String id = isValidToken(token);
        Db_Request_Model.idb.delMessage(Integer.parseInt(idMessage), id);
    }

    /**
     * Notify a new message by e-mail
     *
     * @param id The ID of the member to mail
     * @param m The message that he receives in his company inbox
     * @throws SQLException
     */
    @Override
    public void notifyNewMessage(String id, Message m) throws SQLException {
        Member user = Db_Request_Model.idb.getMemberInfos(id);
        try {
            SendEmail.sendEmail(user, "Nouveau message sur votre messagerie interne.", "Bonjour " + user.getFirst_name() + " " + user.getName()
                    + ".\nVous avez reçu un nouveau message de " + m.getSender() + " ayant pour sujet : " + m.getTitle() + " dans votre messagerie interne.\n");
        } catch (Exception ex) {
            Logger.getLogger(Project_Management_Presenter_Intern_Methods.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Generates an ID for a member
     *
     * @param nom The name of the member
     * @param prenom The first name of the member
     * @return The ID generates for the member
     */
    @Override
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

    /**
     * Generates an ID for a group
     *
     * @param nom_group The name of the group
     * @return The ID for the group
     */
    @Override
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

    /**
     * Gets all the groups headers which exists in the DB
     *
     * @return All the groups header stored in the DB
     * @throws SQLException
     */
    @Override
    public ArrayList<GroupHeader> getExistingGroups() throws SQLException {
        return Db_Request_Model.idb.getGroupsHeaderData();
    }

    /**
     * Disconnects the user
     *
     * @param token The token of the session
     */
    @Override
    public void disconnectUser(String token) {
        if (token != null) {
            Db_Request_Model.tokenID.remove(token);
        }
    }

    /**
     * Updates a member
     *
     * @param m The new member description
     * @return TRUE : ok, FALSE : error
     */
    @Override
    public boolean updateUser(Member m) {
        return (Db_Request_Model.idb.updateMember(m) == 1);
    }

    /**
     * Update the password of a member
     *
     * @param m The member who wants to update his password
     * @param mdpClair The password in clear (to send the mail)
     * @param hash The password as a MD5 hash
     * @return TRUE : ok, FALSE : error
     */
    @Override
    public boolean updateUserPswd(Member m, String mdpClair, String hash) {
        if (Db_Request_Model.idb.updateMemberPswd(m.getId_member(), hash) == 1) {
            try {
                SendEmail.sendEmail(m, "Changement de votre mot de passe résussi.", "Bonjour " + m.getFirst_name() + " " + m.getName() + ", \nVotre mot de passe a bien été mit à jour.\n\nIdentifiant : " + m.getId_member() + "\nMot de passe : " + mdpClair + "\n");
            } catch (Exception ex) {
                Logger.getLogger(Db_Request_Model.class.getName()).log(Level.SEVERE, null, ex);
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Gets the tasks in which the member is affected
     *
     * @param id_member The ID of the member to get the tasks
     * @return All the tasks in which the user is affected, separated with a
     * comma
     */
    @Override
    public String getMemberTasks(String id_member) {
        try {
            return Db_Request_Model.idb.getTasksMember(id_member);
        } catch (SQLException ex) {
            Logger.getLogger(Db_Request_Model.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }

    /**
     * Gets the groups in which the member is in
     *
     * @param id_member The ID of the member to get the groups
     * @return All the groups in which the user is, separated with a comma
     */
    @Override
    public String getMemberGroups(String id_member) {
        try {
            return Db_Request_Model.idb.getMemberGroups(id_member);
        } catch (SQLException ex) {
            Logger.getLogger(Db_Request_Model.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }

    /**
     * Gets the number of message for a given status
     *
     * @param id_membre The ID of the member to check the messages
     * @param mst The status to count
     * @return The number of member's messages for the status
     */
    @Override
    public int getNbMessagesForStatus(String id_membre, MessageStatus mst) {
        return Db_Request_Model.idb.getNbMessagesForStatus(id_membre, mst);
    }

    /**
     * Checks if the message has a status associated with a given member
     *
     * @param id_membre The ID of member
     * @param id_message The ID of the message
     * @param status The status to check
     * @return TRUE : yes, FALSE : no
     */
    @Override
    public boolean messageHasStatusAssociatedWithAMember(String id_membre, int id_message, MessageStatus status) {
        try {
            return Db_Request_Model.idb.messageHasStatusAssociatedWithAMember(id_membre, id_message, status);
        } catch (SQLException ex) {
            Logger.getLogger(Db_Request_Model.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    /**
     * Créer un passowrd
     *
     * @param longueur la longueur du passwor
     * @return le password
     */
    @Override
    public String generatePswd(int longueur) {
        SecureRandom random = new SecureRandom();
        //créer le token
        String pwd = new BigInteger(130, random).toString(32).substring(0, longueur);
        //associé le token avec l'id
        return pwd;
    }

    /**
     * Updates a group
     *
     * @param idGroup The ID of the group to update
     * @param nomG The name of th group
     * @param descrG The description of the group
     * @param membersG The IDs of the members in the group
     * @param chefG The chief, if null, do not change the chief
     * @return TRUE : ok, FALSE : error
     */
    @Override
    public boolean updateGroup(String idGroup, String nomG, String descrG, String[] membersG, String chefG) {
        ArrayList<String> members = new ArrayList<String>();
        HashMap<String, Boolean> memToAddDel = new HashMap<String, Boolean>();

        boolean ok;
        try {
            Group oldG = Db_Request_Model.idb.getGroupInfos(idGroup);
            ArrayList<Member> oldMembers = oldG.getMembers();
            ArrayList<String> oldMembersString = new ArrayList<String>();
            for (Member mm : oldMembers) {
                oldMembersString.add(mm.getId_member().trim());
            }
            for (String mmmm : membersG) {
                members.add(mmmm.trim());
            }
            for (String newMs : members) {
                if (!oldMembersString.contains(newMs.trim())) {
                    memToAddDel.put(newMs.trim(), true);
                }
            }
            for (String oldMs : oldMembersString) {
                if (!members.contains(oldMs.trim())) {
                    memToAddDel.put(oldMs.trim(), false);
                }
            }

            Group g = new Group(idGroup, nomG, null, null, descrG);

            ok = Db_Request_Model.idb.updateGroup(g, memToAddDel, chefG) != -1;
        } catch (SQLException ex) {
            Logger.getLogger(Db_Request_Model.class.getName()).log(Level.SEVERE, null, ex);
            ok = false;
        }
        return ok;

    }

    public ArrayList<String> searchExprInTable(String table, String colName, String expr, String id) {
        return Db_Request_Model.idb.searchExprInTable(table, colName, expr, id);
    }

    public ArrayList<String> searchExprInTable(String table, String colNameToSearch, String colNameDetail, String expr, String id) {
        return Db_Request_Model.idb.searchExprInTable(table, colNameToSearch, colNameDetail, expr, id);
    }

    public void sendEmailToMember(String idMember, String messageBody, String messageSubject) {
        try {
            Member user = this.getInfosMember(idMember);
            SendEmail.sendEmail(user, messageSubject, messageBody);
        } catch (Exception ex) {
            Logger.getLogger(Project_Management_Presenter_Intern_Methods.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendEmailToMember(Member member, String messageBody, String messageSubject) {
        try {
            Member user = member;
            SendEmail.sendEmail(user, messageSubject, messageBody);
        } catch (Exception ex) {
            Logger.getLogger(Project_Management_Presenter_Intern_Methods.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
