/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Controler;

import Model.Db_Request_Model;
import Model.SendEmail;
import dataObjects.Attachment;
import dataObjects.Item;
import dataObjects.Message;
import dataObjects.MessageHeader;
import dataObjects.MessageStatus;
import dataObjects.Recipient;
import dataObjects.RecipientType;
import dataObjects.Task;
import dataObjects.TaskHeader;
import dataObjects.TaskStatus;
import errorsLogging.LogErrors;
import importXML.ItemParser;
import interactionsDB.InteractDB;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jws.WebService;
import peopleObjects.Group;
import peopleObjects.GroupHeader;
import peopleObjects.Member;

/**
 *
 * @author gabriel
 */
@WebService(portName = "Presenter_Intern_Methods_Interface_Port", serviceName = "Presenter_Intern_Methods_Interface_Service",
targetNamespace = "http://Project_Management_Presenter_Intern_Methods/",
endpointInterface = "Controler.Presenter_Intern_Methods_Interface")
public class Project_Management_Presenter_Intern_Methods implements Presenter_Intern_Methods_Interface {

    protected static Db_Request_Model model;
    private static Project_Management_Presenter_Intern_Methods me = null;

    public static Project_Management_Presenter_Intern_Methods getInstance() {
        if (Project_Management_Presenter_Intern_Methods.me == null) {
            Project_Management_Presenter_Intern_Methods.me = new Project_Management_Presenter_Intern_Methods();
            Project_Management_Presenter_Intern_Methods.model = Db_Request_Model.getInstance(Project_Management_Presenter_Intern_Methods.me);
        }
        return Project_Management_Presenter_Intern_Methods.me;
    }

    protected Project_Management_Presenter_Intern_Methods() {
    }

    /**
     * Adds members to a group
     *
     * @param token The token of the session
     * @param membersID IDs of the members to add in the group
     * @param id_group The ID of the group
     * @return
     */
    @Override
    public Boolean addMembersGroup(String token, java.util.List<String> membersID, String id_group) {
        boolean ok = false;
        try {
            String id = Project_Management_Presenter_Intern_Methods.model.isValidToken(token);

            if (id != null && Project_Management_Presenter_Intern_Methods.model.isAdmin(token)) {
                ArrayList<String> failedInserts = Project_Management_Presenter_Intern_Methods.model.addMembersGroup(membersID, id_group);
                if (failedInserts != null) {
                    System.err.println("Error to the affections of : ");
                    for (String err : failedInserts) {
                        System.err.println("  * id of the member : " + err);
                    }
                } else {
                    ok = true;
                }
            }
            // implement here...
        } catch (SQLException ex) {
            Logger.getLogger(Project_Management_Presenter_Intern_Methods.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return ok;

        }
    }

    /**
     * Deletes members from a group
     *
     * @param token The token of the session
     * @param membersID IDs of the members to delete from the group
     * @param id_group The ID of the group
     * @return
     */
    @Override
    public Boolean deleteMembersfromGroup(String token, List<String> membersID, String id_group) {
        boolean ok = false;
        try {
            String id = Project_Management_Presenter_Intern_Methods.model.isValidToken(token);

            if (id != null && Project_Management_Presenter_Intern_Methods.model.isAdmin(token)) {
                ArrayList<String> failedInserts = Project_Management_Presenter_Intern_Methods.model.deleteMembersGroup(membersID, id_group);
                if (failedInserts != null) {
                    System.err.println("Error to the affections of : ");
                    for (String err : failedInserts) {
                        System.err.println("  * id of the member : " + err);
                    }
                } else {
                    ok = true;
                }
            }
            // implement here...
        } catch (SQLException ex) {
            Logger.getLogger(Project_Management_Presenter_Intern_Methods.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return ok;

        }
    }

    /**
     * Create a new group
     *
     * @param token The token of the session
     * @param group The group to create
     * @return
     */
    /**
     * Web service operation
     */
    @Override
    public boolean createGroup(java.util.ArrayList<Member> membres, String token, String nom_groupe, String desc) {
        Member chief = Project_Management_Presenter_Intern_Methods.model.getInfosMember(Project_Management_Presenter_Intern_Methods.model.isValidToken(token));
        Group new_group = new Group(Project_Management_Presenter_Intern_Methods.model.generateIdGroup(nom_groupe), nom_groupe, membres, chief, desc);
        try {
            return Project_Management_Presenter_Intern_Methods.model.createGroup(new_group);
        } catch (SQLException ex) {
            return false;
        }
    }

    /**
     * Creates a new task
     *
     * @param request
     * @param token
     * @return
     */
    @Override
    public String createNewTask(String content, String title, String projectTopic, String creationDate, String dueDate, String statutString, Float budget, Float consumed, Float rae, ArrayList<String> members, ArrayList<String> groups, String token) {
        try {
            String id;
            Task task;
            String sender;
            TaskStatus statut;
            if (Project_Management_Presenter_Intern_Methods.model.isAdmin(token)) {
                id = Project_Management_Presenter_Intern_Methods.model.isValidToken(token);
                if (id != null) {
                    sender = (String) id;

                    if (statutString.compareToIgnoreCase("OPEN") == 0) {
                        statut = TaskStatus.OPEN;
                    } else if (statutString.compareToIgnoreCase("CLOSED") == 0) {
                        statut = TaskStatus.CLOSED;
                    } else if (statutString.compareToIgnoreCase("URGENT") == 0) {
                        statut = TaskStatus.URGENT;
                    } else {
                        System.out.println("Error, unexpected status given, should be : OPEN, CLOSED or URGENT");
                        return null;
                    }

                    //public Task(String id, String sender, String title, String creationDate, String content, String dueDate, String projectTopic, int budget, int consumed, int rae, TaskStatus status) {

                    task = new Task(null, sender, title, creationDate, content, dueDate, projectTopic, budget, consumed, rae, statut, sender);
                    System.err.println("Error,      " + task.getStringDueDate());
                    if (!members.contains(sender)) {
                        members.add(sender);
                    }
                    return model.createNewTaskAndNotify(task, sender, members, groups);
                }
                return null;
            }
            return null;
        } catch (SQLException ex) {
            Logger.getLogger(Project_Management_Presenter_Intern_Methods.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public boolean updateTask(Task newTask, int id_task, ArrayList<String> idsNewMembers, ArrayList<String> idsNewGroups, String chief) {
        HashMap<Recipient, Boolean[]> rcpts = new HashMap<Recipient, Boolean[]>();
        Task oldTask = Project_Management_Presenter_Intern_Methods.model.getInfosTask(id_task);
        Recipient rpt;
        ArrayList<String> toAvoid = new ArrayList<String>();
        int i = 0;
        for (Recipient r : oldTask.getRecipients()) {
            //membre ou groupe a supprimer
            System.err.println("''''''''''''    " + r.getId());
            if (idsNewMembers.contains(r.getId().trim()) || idsNewGroups.contains(r.getId().trim())) {
                toAvoid.add(r.getId());
            } else {
                Boolean[] suppr = {false, false};
                rcpts.put(r, suppr);
            }
        }
        for (String s : toAvoid) {
            System.err.println("avoid : " + s);
        }
        while (i < idsNewGroups.size()) {

            if (!toAvoid.contains(idsNewGroups.get(i).trim())) {
                Boolean[] add = {true, false};
                rpt = new Recipient(RecipientType.GROUP, idsNewGroups.get(i).trim());
                rcpts.put(rpt, add);
                System.err.println("------------    " + idsNewGroups.get(i).trim());

            }
            i++;
        }
        i = 0;
        while (i < idsNewMembers.size()) {

            if (!toAvoid.contains(idsNewMembers.get(i).trim())) {
                Boolean[] add = {true, idsNewMembers.get(i).compareToIgnoreCase(chief) == 0 ? true : false};
                rpt = new Recipient(RecipientType.USER, idsNewMembers.get(i).trim());
                rcpts.put(rpt, add);
                System.err.println("************    " + idsNewMembers.get(i));
            }

            i++;
        }
        if (!rcpts.isEmpty()) {
            return Project_Management_Presenter_Intern_Methods.model.updateTaskAndNotify(newTask, rcpts);
        } else {
            return true;
        }
    }

    /**
     * Create a new user
     *
     * @param token The token of the session
     * @param user The new user to create
     * @return
     */
    /**
     * Web service operation méthode qui crée un utilisateur à partir de son
     * nom, prénom, email, et mot de passe en clair elle renvoie un booleen à
     * true si tout s'est bien passé
     */
    @Override
    public String createNewUser(Member user, String pswd) {
        String id = null;
        if (user.getId_member() == null || user.getId_member().equals("")) {
            id = Project_Management_Presenter_Intern_Methods.model.generateIdMember(user.getName(), user.getFirst_name());
            user.setId_member(id);
        }
        String hash = generateMD5FromString(pswd);
        try {
            boolean ok = Project_Management_Presenter_Intern_Methods.model.createNewUser(user, hash);
            if (ok) {
                try {
                    SendEmail.sendEmail(user, "Votre compte a été crée.", "Bonjour " + user.getFirst_name() + " " + user.getName()
                            + ".\nUn nouveau compte vient de vous être créé.\nIdentifiant : " + user.getId_member() + "\nMot de passe : " + pswd + "\n");
                } catch (Exception ex) {
                    Logger.getLogger(Project_Management_Presenter_Intern_Methods.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return id;
        } catch (SQLException ex) {
            return null;
        }
    }

    /**
     * Deletes a task
     *
     * @param token The token of the session
     * @param id_Task The task to delete
     * @return
     */
    @Override
    public Boolean deleteTask(String token, int id_Task) {
        try {
            String id = Project_Management_Presenter_Intern_Methods.model.isValidToken(token);

            if (id != null && Project_Management_Presenter_Intern_Methods.model.isAdmin(token)) {
                return Project_Management_Presenter_Intern_Methods.model.deleteTaskAndNotify(id_Task);
            } else {
                return (false);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Project_Management_Presenter_Intern_Methods.class.getName()).log(Level.SEVERE, null, ex);
            return (false);
        }
    }

    /**
     * Disconnects the user
     *
     * @param token The token of the session
     * @return
     */
    @Override
    public void disconnectUser(String token) {
        Project_Management_Presenter_Intern_Methods.model.disconnectUser(token);
    }

    /**
     *
     * @return
     */
    @Override
    public String extractionDonnees() {
        // implement here...
        return (null);
    }

    /**
     * Get all the Tasks (headers)
     *
     * @param token The token of the session
     * @return
     */
    @Override
    public ArrayList<TaskHeader> getAllTasks(String token, boolean onlyUserTasks) {
        String id = Project_Management_Presenter_Intern_Methods.model.isValidToken(token);
        if (id != null) {
            return Project_Management_Presenter_Intern_Methods.model.getTasksHeader((onlyUserTasks ? id : null));
        } else {
            //Authentication failure
            return null;
        }
    }

    /**
     * Gets all the members
     *
     * @param token The token of the session
     * @return
     */
    /**
     * Web service operation
     */
    @Override
    public java.util.ArrayList<Member> getAvailableMembers() {
        return Project_Management_Presenter_Intern_Methods.model.getAvailableMembers();
    }

    /**
     * Load the form to create a task and check if the user is connected and if
     * he has rights
     *
     * @param token The token of the session
     * @return
     */
    @Override
    public Boolean getCreateTaskForm(String token) {
        // implement here...
        return (true);
    }

    /**
     * Load the form to update a task and check if the user is connected
     *
     * @param token The token of the session
     * @return
     */
    @Override
    public Boolean getFormUpTask(String token) {
        if (model.isValidToken(token) != null) {
            //Load the form to update a task
        } else {
            //Authentification failure
        }
        return (true);
    }

    /**
     * Gets the informations of a group
     *
     * @param token The token of the session
     * @param id_group The ID of the group to gets the infos
     * @return
     */
    @Override
    public Group getGroupInfos(String token, String id_group) {
        String id = Project_Management_Presenter_Intern_Methods.model.isValidToken(token);
        Group group = null;
        if (id != null) {
            try {
                group = Project_Management_Presenter_Intern_Methods.model.getGroupInfos(id_group);
            } catch (SQLException ex) {
                Logger.getLogger(Project_Management_Presenter_Intern_Methods.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return group;
    }

    /**
     * Gets the headers of the messages
     *
     * @param token The token of the session
     * @return
     */
    @Override
    public ArrayList<MessageHeader> getHeaderMessages(String token, boolean received) {
        ArrayList<MessageHeader> messageHeader = null;
        try {
            messageHeader = (Project_Management_Presenter_Intern_Methods.model.getHeaderMessages(token, received));
        } catch (SQLException ex) {
            Logger.getLogger(Project_Management_Presenter_Intern_Methods.class.getName()).log(Level.SEVERE, null, ex);
        }
        return messageHeader;
    }

    /**
     * Gets the informations of a task
     *
     * @param id_Task The task to gets the infos
     * @param token The token of the session
     * @return
     */
    @Override
    public Task getInfosTask(int id_Task, String token) {
        //if (model.isValidToken(token) != null) {
        return Project_Management_Presenter_Intern_Methods.model.getInfosTask(id_Task);
        //} else {
        //Authentification failure
        //return null;
        //}
    }

    public Member getInfoUser(String id_User, String token) {
        //if (model.isValidToken(token) != null) {
        return Project_Management_Presenter_Intern_Methods.model.getInfosMember(id_User);
        //} else {
        //Authentification failure
        //return null;
        //}
    }

    /**
     * Gets the body of a message
     *
     * @param token The token of the session
     * @param idMess The ID of the message to get in the DB
     * @return
     */
    @Override
    public Message getMessageBody(String idMessage, String token, boolean received) {
        Message message = null;
        try {
            message = (Project_Management_Presenter_Intern_Methods.model.getMessageBody(idMessage, token, received));
        } catch (SQLException ex) {
            Logger.getLogger(Project_Management_Presenter_Intern_Methods.class.getName()).log(Level.SEVERE, null, ex);
        }
        return message;
    }

    /**
     * Load the form page and check if the user is connected
     *
     * @param token The token of the session
     * @return
     */
    @Override
    public Boolean getNewUserForm(String token) {
        // implement here...
        return (null);
    }

    /**
     * Gets all the users in the DB and add them to the page to load
     *
     * @param token The token of the session
     * @return
     */
    @Override
    public ArrayList<Member> getUsers() {
        ArrayList<Member> member = null;
        try {
            member = (Project_Management_Presenter_Intern_Methods.model.getUsers());
        } catch (SQLException ex) {
            Logger.getLogger(Project_Management_Presenter_Intern_Methods.class.getName()).log(Level.SEVERE, null, ex);
        }
        return member;
    }

    /**
     *
     * @return
     */
    @Override
    public int importXML(String token, InputStream is) {
        ArrayList<Item> iList;
        iList = ItemParser.parse(is);
        int nbInsert = 0;

        if (iList == null) {
            LogErrors.getInstance().appendLogMessage("Failed to parse the XML file.", Level.SEVERE);
        } else {
            for (Item it : iList) {
                if (it instanceof Message) {
                    Message m = (Message) it;
                    if (!this.saveMessageGivenMessage(token, m)) {
                        Logger.getLogger(Project_Management_Presenter_Intern_Methods.class.getName()).log(Level.SEVERE, null, "saveMessageGiven : fail to insert the message into the DB.");
                    } else {
                        nbInsert++;
                    }
                } else {
                    try {
                        Task t = (Task) it;
                        InteractDB.getInstance().addTaskAndAssociate(t);
                        nbInsert++;
                    } catch (SQLException ex) {
                        Logger.getLogger(Project_Management_Presenter_Intern_Methods.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (Exception ex) {
                        Logger.getLogger(Project_Management_Presenter_Intern_Methods.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        return nbInsert;
    }

    /**
     * Create the String ID list from a recipients list.
     *
     * @param recipients the recipients list.
     * @return the String ID list
     */
    @Override
    public ArrayList<String> createMemberIDList(ArrayList<Recipient> recipients) {
        ArrayList<String> mList = new ArrayList<String>();

        for (Recipient rcpt : recipients) {
            mList.add(rcpt.getId());
        }

        return mList;
    }

    /**
     * Log-in the user
     *
     * @param login The login of the user
     * @param password The password of the user
     * @return The token created for this user
     */
    @Override
    public String login(String login, String password) {
        Project_Management_Presenter_Intern_Methods.model = Db_Request_Model.getInstance(Project_Management_Presenter_Intern_Methods.me);
        String token = null;
        try {
            token = (Project_Management_Presenter_Intern_Methods.model.authenticate(login, generateMD5FromString(password)));
        } catch (SQLException ex) {
            Logger.getLogger(Project_Management_Presenter_Intern_Methods.class.getName()).log(Level.SEVERE, null, ex);
        }

        return token;
    }

    /**
     * Generate the MD5 String From another String
     */
    @Override
    public String generateMD5FromString(String str) {
        byte[] strBytes = str.getBytes();
        byte[] hash = null;
        try {
            hash = MessageDigest.getInstance("MD5").digest(strBytes);
        } catch (NoSuchAlgorithmException ex) {
            System.err.println("Le MD5 n'est pas supporté sur cette machine.\n" + ex.getMessage());
        }
        //On reforme une chaîne de caractères représentant le hash
        StringBuilder hashString = new StringBuilder();
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(hash[i]);
            if (hex.length() == 1) {
                hashString.append('0');
                hashString.append(hex.charAt(hex.length() - 1));
            } else {
                hashString.append(hex.substring(hex.length() - 2));
            }
        }
        return hashString.toString();
    }

    /**
     * Save the message in the DB
     *
     * @param idSender The ID of the sender
     * @param members The members to send the message
     * @param title The title of the message
     * @param messageBody The body of the message
     * @param ms The status of the message
     * @param attachments The attachments
     * @param token The token
     * @return
     */
    @Override
    public boolean saveMessageToMembers(String idSender, ArrayList<String> members, String title, String messageBody, MessageStatus ms, ArrayList<Attachment> attachments, String token) {
        String id = Project_Management_Presenter_Intern_Methods.model.isValidToken(token);
        boolean ok = false;
        System.err.println("saveMessageToMembers");
        if (id != null) {
            System.err.println("!!!!!!! saveMessageToMembers  " + idSender);

            Message m = Project_Management_Presenter_Intern_Methods.model.createMessage(idSender, members, title, messageBody, ms);
            for (Attachment attachment : attachments) {
                m.addAttachment(attachment);
            }
            m.setContent(messageBody);
            try {
                System.err.println("........ saveMessageToMembers  " + m.getSender());

                Project_Management_Presenter_Intern_Methods.model.saveMessage(m);
                ok = true;
            } catch (SQLException ex) {
                Logger.getLogger(Project_Management_Presenter_Intern_Methods.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.err.println("FIN saveMessageToMembers");

        return (ok);
    }

    @Override
    public boolean saveMessageToGroups(String idSender, ArrayList<String> groups, ArrayList<String> members, String title, String messageBody, MessageStatus ms, ArrayList<Attachment> attachments, String token) {
        String id = Project_Management_Presenter_Intern_Methods.model.isValidToken(token);
        boolean ok = false;
        if (id != null) {
            for (String group : groups) {
                ArrayList<Member> membersL;
                try {
                    membersL = model.getGroupInfos(group).getMembers();
                    for (Member member : membersL) {
                        if (!members.contains(member.getId_member())) {
                            members.add(member.getId_member());
                        }
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(Project_Management_Presenter_Intern_Methods.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
            saveMessageToMembers(idSender, members, title, messageBody, ms, attachments, token);
            return (ok);
        }
        return (false);
    }

    /**
     * Save the message in the DB
     *
     * @param token The token of the session
     * @param message The message, include the Recipients
     * @return
     */
    @Override
    public boolean saveMessageGivenMessage(String token, Message message) {
        String id = Project_Management_Presenter_Intern_Methods.model.isValidToken(token);
        boolean ok = false;
        if (id != null) {
            try {

                Project_Management_Presenter_Intern_Methods.model.saveMessage(message);
                ok = true;
            } catch (SQLException ex) {
                Logger.getLogger(Project_Management_Presenter_Intern_Methods.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return (ok);
    }

    /**
     * Update the status of a message
     *
     * @param token The token of the session
     * @param idMess The ID of the message to update
     * @param status The new status of the message (add/remove to/from the
     * others status)
     * @param addStatus Indicates if the status is to add or to remove of the
     * status
     * @return
     */
    @Override
    public boolean updateMessageStatus(String token, String idMessage, MessageStatus ms, boolean addStatus) {
        boolean ok = true;
        try {
            Project_Management_Presenter_Intern_Methods.model.updateMessageStatus(token, idMessage, ms, addStatus);
        } catch (SQLException ex) {
            Logger.getLogger(Project_Management_Presenter_Intern_Methods.class.getName()).log(Level.SEVERE, null, ex);
            ok = false;
        }

        return ok;
    }

    public boolean updateMessageStatusString(String token, String idMessage, String mst, boolean addStatus) {
        System.err.println("............+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++" + token + " -- " + idMessage + " -- " + addStatus + " -- " + mst);

        return this.updateMessageStatus(token, idMessage, this.parseMessageStatus(mst), addStatus);
    }

    /**
     * Checks if the member associates with the token is an Admin
     *
     * @param token The token of the session
     * @return
     */
    @Override
    public boolean checkIsAdmin(String token) {
        boolean ok;
        try {
            ok = (Project_Management_Presenter_Intern_Methods.model.isValidToken(token) != null && Project_Management_Presenter_Intern_Methods.model.isAdmin(token));
        } catch (SQLException ex) {
            Logger.getLogger(Project_Management_Presenter_Intern_Methods.class.getName()).log(Level.SEVERE, null, ex);
            ok = false;
        }
        return ok;
    }

    @Override
    public boolean deleteMessage(String token, String idMessage) {
        boolean ok = true;
        try {
            Project_Management_Presenter_Intern_Methods.model.deleteMessage(token, idMessage);
        } catch (SQLException ex) {
            Logger.getLogger(Project_Management_Presenter_Intern_Methods.class.getName()).log(Level.SEVERE, null, ex);
            ok = false;
        }
        return ok;
    }

    /**
     * Web service operation
     */
    @Override
    public java.util.ArrayList<GroupHeader> getGroups() {
        try {
            return Project_Management_Presenter_Intern_Methods.model.getExistingGroups();
        } catch (SQLException ex) {
            return null;
        }
    }

    @Override
    public boolean updateUser(Member m) {
        return Project_Management_Presenter.model.updateUser(m);
    }

    @Override
    public String getMemberTasks(String id_member) {
        String tks = "Error when fetching tasks.";
        try {
            tks = Project_Management_Presenter_Intern_Methods.model.getMemberTasks(id_member);
        } catch (Exception ex) {
            Logger.getLogger(Project_Management_Presenter_Intern_Methods.class.getName()).log(Level.SEVERE, null, ex);

        }
        return tks;
    }

    @Override
    public String getMemberGroups(String id_member) {
        String gps = "Error when fetching groups.";
        try {
            gps = Project_Management_Presenter_Intern_Methods.model.getMemberGroups(id_member);
        } catch (Exception ex) {
            Logger.getLogger(Project_Management_Presenter_Intern_Methods.class.getName()).log(Level.SEVERE, null, ex);

        }
        return gps;
    }

    @Override
    public int getNbMessagesForStatus(String token, MessageStatus mst) {
        String id = Project_Management_Presenter_Intern_Methods.model.isValidToken(token);
        if (id != null) {
            return Project_Management_Presenter_Intern_Methods.model.getNbMessagesForStatus(id, mst);
        } else {
            return 0;
        }
    }
    
    public int getNbMessagesForStatus(String token, String mst) {
        String id = Project_Management_Presenter_Intern_Methods.model.isValidToken(token);
        if (id != null) {
            return Project_Management_Presenter_Intern_Methods.model.getNbMessagesForStatus(id, this.parseMessageStatus(mst));
        } else {
            return 0;
        }
    }

    public MessageStatus parseMessageStatus(String mst) {
        MessageStatus msts;
        if (mst.equals("IMPORTANT")) {
            msts = MessageStatus.IMPORTANT;
        } else if (mst.equals("HAVE_TO_ANSWER")) {
            msts = MessageStatus.HAVE_TO_ANSWER;
        } else if (mst.equals("URGENT")) {
            msts = MessageStatus.URGENT;
        } else if (mst.equals("FORWARDED")) {
            msts = MessageStatus.FORWARDED;
        } else if (mst.equals("READ")) {
            msts = MessageStatus.READ;
        } else {
            msts = null;
        }
        return msts;
    }

    public boolean messageHasStatusAssociatedWithAMember(String token, int id_message, String status) {
        String id = Project_Management_Presenter_Intern_Methods.model.isValidToken(token);
        return (id != null && Project_Management_Presenter_Intern_Methods.model.messageHasStatusAssociatedWithAMember(id, id_message, this.parseMessageStatus(status)));
    }
}
