/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Controler;

import dataObjects.Attachment;
import dataObjects.Item;
import dataObjects.Message;
import dataObjects.MessageHeader;
import dataObjects.MessageStatus;
import dataObjects.Recipient;
import dataObjects.Task;
import dataObjects.TaskHeader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import peopleObjects.Group;
import peopleObjects.GroupHeader;
import peopleObjects.Member;

/**
 *
 * @author gabriel
 */
@WebService(name = "Presenter_Intern_Methods_Interface", targetNamespace =
"http://Project_Management_Presenter_Intern_Methods/")
public interface Presenter_Intern_Methods_Interface {

    /**
     * Adds members to a group
     *
     * @param token The token of the session
     * @param membersID IDs of the members to add in the group
     * @param id_group The ID of the group
     * @return
     */
    @WebMethod(operationName = "addMembersGroup")
    public Boolean addMembersGroup(@WebParam(name = "token") String token, @WebParam(name = "membersID") java.util.List<String> membersID, @WebParam(name = "id_group") String id_group);

    /**
     * Deletes members from a group
     *
     * @param token The token of the session
     * @param membersID IDs of the members to delete from the group
     * @param id_group The ID of the group
     * @return
     */
    @WebMethod(operationName = "deleteMembersfromGroup")
    public Boolean deleteMembersfromGroup(@WebParam(name = "token") String token, @WebParam(name = "membersID") List<String> membersID, @WebParam(name = "id_group") String id_group);

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
    @WebMethod(operationName = "createGroup")
    public boolean createGroup(@WebParam(name = "membres") java.util.ArrayList<Member> membres, @WebParam(name = "token") String token, @WebParam(name = "nom_groupe") String nom_groupe, @WebParam(name = "desc") String desc);

    /**
     * Creates a new task
     *
     * @param request
     * @param token
     * @return
     */
    @WebMethod(operationName = "createNewTask")
    public String createNewTask(String content, @WebParam(name = "title") String title, @WebParam(name = "projectTopic") String projectTopic, @WebParam(name = "creationDate") String creationDate, @WebParam(name = "dueDate") String dueDate, @WebParam(name = "statutString") String statutString, @WebParam(name = "budget") Float budget, @WebParam(name = "consumed") Float consumed, @WebParam(name = "rae") Float rae, @WebParam(name = "members") ArrayList<String> members, @WebParam(name = "groups") ArrayList<String> groups, @WebParam(name = "token") String token);

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
    @WebMethod(operationName = "createNewUser")
    public String createNewUser(@WebParam(name = "user") Member user, @WebParam(name = "pswd") String pswd);

    /**
     * Deletes a task
     *
     * @param token The token of the session
     * @param id_Task The task to delete
     * @return
     */
    @WebMethod(operationName = "deleteTask")
    public Boolean deleteTask(@WebParam(name = "token") String token, @WebParam(name = "id_Task") int id_Task);

    /**
     * Disconnects the user
     *
     * @param token The token of the session
     * @return
     */
    @WebMethod(operationName = "disconnectUser")
    public void disconnectUser(@WebParam(name = "token") String token);

    /**
     *
     * @return
     */
    @WebMethod(operationName = "extractionDonnees")
    public String extractionDonnees();

    /**
     * Get all the Tasks (headers)
     *
     * @param token The token of the session
     * @return
     */
    @WebMethod(operationName = "getAllTasks")
    public ArrayList<TaskHeader> getAllTasks(@WebParam(name = "token") String token, @WebParam(name = "onlyUserTasks") boolean onlyUserTasks);

    /**
     * Gets all the members
     *
     * @param token The token of the session
     * @return
     */
    /**
     * Web service operation
     */
    @WebMethod(operationName = "getAvailableMembers")
    public java.util.ArrayList<Member> getAvailableMembers();

    /**
     * Load the form to create a task and check if the user is connected and if
     * he has rights
     *
     * @param token The token of the session
     * @return
     */
    @WebMethod(operationName = "getCreateTaskForm")
    public Boolean getCreateTaskForm(@WebParam(name = "token") String token);

    /**
     * Load the form to update a task and check if the user is connected
     *
     * @param token The token of the session
     * @return
     */
    @WebMethod(operationName = "getFormUpTask")
    public Boolean getFormUpTask(@WebParam(name = "token") String token);

    /**
     * Gets the informations of a group
     *
     * @param token The token of the session
     * @param id_group The ID of the group to gets the infos
     * @return
     */
    @WebMethod(operationName = "getGroupInfos")
    public Group getGroupInfos(@WebParam(name = "token") String token, @WebParam(name = "id_group") String id_group);

    /**
     * Gets the headers of the messages
     *
     * @param token The token of the session
     * @return
     */
    @WebMethod(operationName = "getHeaderMessages")
    public ArrayList<MessageHeader> getHeaderMessages(@WebParam(name = "token") String token);

    /**
     * Gets the informations of a task
     *
     * @param id_Task The task to gets the infos
     * @param token The token of the session
     * @return
     */
    @WebMethod(operationName = "getInfosTask")
    public Task getInfosTask(@WebParam(name = "id_Task") int id_Task, @WebParam(name = "token") String token);

    /**
     * Gets the body of a message
     *
     * @param token The token of the session
     * @param idMess The ID of the message to get in the DB
     * @return
     */
    @WebMethod(operationName = "getMessageBody")
    public Message getMessageBody(@WebParam(name = "idMessage") String idMessage, @WebParam(name = "token") String token);

    /**
     * Load the form page and check if the user is connected
     *
     * @param token The token of the session
     * @return
     */
    @WebMethod(operationName = "getNewUserForm")
    public Boolean getNewUserForm(@WebParam(name = "token") String token);

    /**
     * Gets all the users in the DB and add them to the page to load
     *
     * @param token The token of the session
     * @return
     */
    @WebMethod(operationName = "getUsers")
    public ArrayList<Member> getUsers();

    /**
     * Launch the parsing and save the results in the database.
     *
     * @param token
     * @param is input stream of the file to parse
     * @return the number of item inserted
     */
    @WebMethod(operationName = "importXML")
    public int importXML(@WebParam(name = "token") String token, @WebParam(name = "inputStream") InputStream is);

    /**
     * Create the String ID list from a recipients list.
     *
     * @param recipients the recipients list.
     * @return the String ID list
     */
    @WebMethod(operationName = "createMemberIDList")
    public ArrayList<String> createMemberIDList(@WebParam(name = "recipients") ArrayList<Recipient> recipients);

    /**
     * Log-in the user
     *
     * @param login The login of the user
     * @param password The password of the user
     * @return The token created for this user
     */
    @WebMethod(operationName = "login")
    public String login(@WebParam(name = "login") String login, @WebParam(name = "password") String password);

    /**
     * Generate the MD5 String From another String
     */
    @WebMethod(operationName = "generateMD5FromString")
    public String generateMD5FromString(@WebParam(name = "str") String str);

    /**
     * Save the message for given members in the DB
     *
     * @param idSender The sender
     * @param members Members who receive the message
     * @param title The title of the message
     * @param messageBody The message body
     * @param ms The status of the message
     * @param attachments The attachments
     * @param token The token of the session
     * @return
     */
    @WebMethod(operationName = "saveMessageToMembers")
    public boolean saveMessageToMembers(@WebParam(name = "idSender") String idSender, @WebParam(name = "members") ArrayList<String> members, @WebParam(name = "title") String title, @WebParam(name = "messageBody") String messageBody, @WebParam(name = "ms") MessageStatus ms, @WebParam(name = "attachments") ArrayList<Attachment> attachments, @WebParam(name = "token") String token);

    /**
     * Save the message in the DB
     *
     * @param token The token of the session
     * @param members The members who receive the message
     * @param message The message
     * @return
     */
    @WebMethod(operationName = "saveMessageGivenMessage")
    public boolean saveMessageGivenMessage(@WebParam(name = "token") String token, @WebParam(name = "message") Message message);

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
    @WebMethod(operationName = "updateMessageStatus")
    public boolean updateMessageStatus(@WebParam(name = "token") String token, @WebParam(name = "idMessage") String idMessage, @WebParam(name = "ms") MessageStatus ms, @WebParam(name = "addStatus") boolean addStatus);

    /**
     * Update a task.
     *
     * @param token The token of the session
     * @param taskUpdate The task to update with its new values
     * @return
     */
    @WebMethod(operationName = "updateTask")
    public Boolean updateTask(@WebParam(name = "token") String token, @WebParam(name = "taskUpdate") Task taskUpdate);

    /**
     * Checks if the member associates with the token is an Admin
     *
     * @param token The token of the session
     * @return
     */
    @WebMethod(operationName = "checkIsAdmin")
    public boolean checkIsAdmin(@WebParam(name = "token") String token);

    @WebMethod(operationName = "deleteMessage")
    public boolean deleteMessage(@WebParam(name = "token") String token, @WebParam(name = "idMessage") String idMessage);

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getGroups")
    public java.util.ArrayList<GroupHeader> getGroups();

    /**
     * Save the message for given groups in the DB
     *
     * @param idSender The sender
     * @param groups Groups who receive the message
     * @param title The title of the message
     * @param messageBody The message body
     * @param ms The status of the message
     * @param attachments The attachments
     * @param token The token of the session
     * @return
     */
    @WebMethod(operationName = "saveMessageToGroups")
    public boolean saveMessageToGroups(String idSender, ArrayList<String> groups, ArrayList<String> members, String title, String messageBody, MessageStatus ms, ArrayList<Attachment> attachments, String token);

    @WebMethod(operationName = "updateUser")
    public boolean updateUser(Member m);

}

