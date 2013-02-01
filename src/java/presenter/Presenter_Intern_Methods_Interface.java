/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package presenter;

import dataObjects.Attachment;
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
"http://presenter/")
public interface Presenter_Intern_Methods_Interface {

    /**
     * Adds members to a group
     *
     * @param token The token of the session
     * @param membersID IDs of the members to add in the group
     * @param id_group The ID of the group
     * @return TRUE : ok, FALSE : error
     */
    @WebMethod(operationName = "addMembersGroupArr")
    public Boolean addMembersGroup(@WebParam(name = "token") String token, @WebParam(name = "membersID") java.util.List<String> membersID, @WebParam(name = "id_group") String id_group);

    /**
     * Adds members to a group
     *
     * @param token The token of the session
     * @param membersID IDs of the members to add in the group separated by a comma
     * @param id_group The ID of the group
     * @return TRUE : ok, FALSE : error
     */
    @WebMethod(operationName = "addMembersGroup")
    public Boolean addMembersGroup(@WebParam(name = "token") String token, @WebParam(name = "membersID") String membersID, @WebParam(name = "id_group") String id_group);

    /**
     * Deletes members from a group
     *
     * @param token The token of the session
     * @param membersID IDs of the members to delete from the group
     * @param id_group The ID of the group
     * @return TRUE : ok, FALSE : error
     */
    @WebMethod(operationName = "deleteMembersfromGroup")
    public Boolean deleteMembersfromGroup(@WebParam(name = "token") String token, @WebParam(name = "membersID") List<String> membersID, @WebParam(name = "id_group") String id_group);

    /**
     * Create a new group
     *
     * @param membres The members in the group
     * @param token The token session
     * @param nom_groupe The name of the group
     * @param desc The description of the group
     * @return TRUE : ok, FALSE : error
     */
    @WebMethod(operationName = "createGroup")
    public boolean createGroup(@WebParam(name = "membres") java.util.ArrayList<Member> membres, @WebParam(name = "token") String token, @WebParam(name = "nom_groupe") String nom_groupe, @WebParam(name = "desc") String desc);

    /**
     * Creates a new task
     *
     * @param content The content of the task
     * @param title he title of the taks
     * @param projectTopic The project/topic of the task
     * @param creationDate The date of the creation of the task
     * @param dueDate The due date of the task
     * @param statutString The status of the task as a String
     * @param budget The budget of the task
     * @param consumed The consumed value of the task
     * @param rae The value that the task still get to be completed
     * @param members The IDs of the members affected to the task
     * @param groups The IDs of the groups affected to the task
     * @param token The token of the session
     * @return The ID of the new task
     */
    @WebMethod(operationName = "createNewTaskArray")
    public String createNewTask(@WebParam(name = "content") String content, @WebParam(name = "title") String title, @WebParam(name = "projectTopic") String projectTopic, @WebParam(name = "creationDate") String creationDate, @WebParam(name = "dueDate") String dueDate, @WebParam(name = "statutString") String statutString, @WebParam(name = "budget") Float budget, @WebParam(name = "consumed") Float consumed, @WebParam(name = "rae") Float rae, @WebParam(name = "members") ArrayList<String> members, @WebParam(name = "groups") ArrayList<String> groups, @WebParam(name = "token") String token);

    /**
     * Creates a new task with members and groups separated by a comma
     *
     * @param content The content of the task
     * @param title he title of the taks
     * @param projectTopic The project/topic of the task
     * @param creationDate The date of the creation of the task
     * @param dueDate The due date of the task
     * @param statutString The status of the task as a String
     * @param budget The budget of the task
     * @param consumed The consumed value of the task
     * @param rae The value that the task still get to be completed
     * @param members The IDs of the members affected to the task separated by a
     * comma
     * @param groups The IDs of the groups affected to the task separated by a
     * comma
     * @param token The token of the session
     * @return The ID of the new task
     */
    @WebMethod(operationName = "createNewTask")
    public String createNewTask(@WebParam(name = "content") String content, @WebParam(name = "title") String title, @WebParam(name = "projectTopic") String projectTopic, @WebParam(name = "creationDate") String creationDate, @WebParam(name = "dueDate") String dueDate, @WebParam(name = "statutString") String statutString, @WebParam(name = "budget") Float budget, @WebParam(name = "consumed") Float consumed, @WebParam(name = "rae") Float rae, @WebParam(name = "members") String members, @WebParam(name = "groups") String groups, @WebParam(name = "token") String token);

    /**
     * Create a new user
     *
     * @param user The new user to create
     * @return The ID of the member created
     */
    @WebMethod(operationName = "createNewUserNoCheck")
    public String createNewUser(@WebParam(name = "user") Member user);

    /**
     * Create a new user and check if there are enough right
     *
     * @param user The new user to create
     * @param token The token to check the rights
     * @return The ID of the member created
     */
    @WebMethod(operationName = "createNewUser")
    public String createNewUserAndCheck(@WebParam(name = "user") Member user, @WebParam(name = "token") String token);

    /**
     * Deletes a task
     *
     * @param token The token of the session
     * @param id_Task The task to delete
     * @return TRUE : ok, FALSE : error
     */
    @WebMethod(operationName = "deleteTask")
    public Boolean deleteTask(@WebParam(name = "token") String token, @WebParam(name = "id_Task") int id_Task);

    /**
     * Disconnects the user
     *
     * @param token The token of the session to disconnect
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
     * @param onlyUserTasks TRUE : gets only all the headers' tasks of the user,
     * FALSE : get all tasks header
     * @return The list of all the TaskHeader of the member
     */
    @WebMethod(operationName = "getAllTasks")
    public ArrayList<TaskHeader> getAllTasks(@WebParam(name = "token") String token, @WebParam(name = "onlyUserTasks") boolean onlyUserTasks);

    /**
     * Gets all the members
     *
     * @param token The token of the session
     * @return The list of the available members
     */
    @WebMethod(operationName = "getAvailableMembers")
    public java.util.ArrayList<Member> getAvailableMembers(@WebParam(name = "token") String token);

    /**
     * Load the form to create a task and check if the user is connected and if
     * he has rights
     *
     * @param token The token of the session
     * @return TRUE : ok, FALSE : error
     */
    @WebMethod(operationName = "getCreateTaskForm")
    public Boolean getCreateTaskForm(@WebParam(name = "token") String token);

    /**
     * Load the form to update a task and check if the user is connected
     *
     * @param token The token of the session
     * @return TRUE : ok, FALSE : error
     */
    @WebMethod(operationName = "getFormUpTask")
    public Boolean getFormUpTask(@WebParam(name = "token") String token);

    /**
     * Gets the informations of a group
     *
     * @param token The token of the session
     * @param id_group The ID of the group to gets the infos
     * @return The group with all it informations
     */
    @WebMethod(operationName = "getGroupInfos")
    public Group getGroupInfos(@WebParam(name = "token") String token, @WebParam(name = "id_group") String id_group);

    /**
     * Gets the headers of the messages
     *
     * @param token The token of the session
     * @param received TRUE : gets the received messages, FALSE : gets the send
     * messages
     * @return The list of the MessagesHeader which received the member
     */
    @WebMethod(operationName = "getHeaderMessages")
    public ArrayList<MessageHeader> getHeaderMessages(@WebParam(name = "token") String token, @WebParam(name = "received") boolean received);

    /**
     * Gets the informations of a task
     *
     * @param id_Task The task to gets the infos
     * @param token The token of the session
     * @return The task with all it informations
     */
    @WebMethod(operationName = "getInfosTask")
    public Task getInfosTask(@WebParam(name = "id_Task") int id_Task, @WebParam(name = "token") String token);

    /**
     * Gets the body of a message
     *
     * @param token The token of the session
     * @param idMess The ID of the message to get in the DB
     * @return The message with all it informations
     */
    @WebMethod(operationName = "getMessageBody")
    public Message getMessageBody(@WebParam(name = "idMessage") String idMessage, @WebParam(name = "token") String token, @WebParam(name = "received") boolean received);

    /**
     * Load the form page and check if the user is connected
     *
     * @param token The token of the session
     * @return TRUE : ok, FALSE : error
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
     *
     * @param str The base string
     * @return The string hashed with MD5
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
     * @return TRUE : ok, FALSE : error
     */
    @WebMethod(operationName = "saveMessageToMembers")
    public Message saveMessageToMembers(@WebParam(name = "idSender") String idSender, @WebParam(name = "members") ArrayList<Recipient> rcpts, @WebParam(name = "title") String title, @WebParam(name = "messageBody") String messageBody, @WebParam(name = "ms") MessageStatus ms, @WebParam(name = "attachments") ArrayList<Attachment> attachments, @WebParam(name = "token") String token);

    /**
     * Save the message in the DB
     *
     * @param token The token of the session
     * @param members The members who receive the message
     * @param message The message
     * @return TRUE : ok, FALSE : error
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
     * @return TRUE : ok, FALSE : error
     */
    @WebMethod(operationName = "updateMessageStatus")
    public boolean updateMessageStatus(@WebParam(name = "token") String token, @WebParam(name = "idMessage") String idMessage, @WebParam(name = "ms") MessageStatus ms, @WebParam(name = "addStatus") boolean addStatus);

    /**
     * Update a task.
     *
     * @param token The token of the session
     * @param taskUpdate The task to update with its new values
     * @return TRUE : ok, FALSE : error
     */
    @WebMethod(operationName = "updateTask")
    public boolean updateTask(@WebParam(name = "newTask") Task newTask, @WebParam(name = "id_task") int id_task, @WebParam(name = "idsNewMembers") ArrayList<String> idsNewMembers, @WebParam(name = "idsNewGroups") ArrayList<String> idsNewGroups, @WebParam(name = "chief") String chief);

    /**
     * Checks if the member associates with the token is an Admin
     *
     * @param token The token of the session
     * @return TRUE : ok, FALSE : error
     */
    @WebMethod(operationName = "checkIsAdmin")
    public boolean checkIsAdmin(@WebParam(name = "token") String token);

    /**
     * Deletes a message for the member associated with the given token
     *
     * @param token The token associate with the ID of the member
     * @param idMessage The ID of the message to delete
     * @return TRUE : ok, FALSE : error
     */
    @WebMethod(operationName = "deleteMessage")
    public boolean deleteMessage(@WebParam(name = "token") String token, @WebParam(name = "idMessage") String idMessage);

    /**
     * Gets all groups store in the DB.
     *
     * @return All groups store in the DB.
     */
    @WebMethod(operationName = "getGroups")
    public java.util.ArrayList<GroupHeader> getGroups(@WebParam(name = "token") String token);

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
    @WebMethod(operationName = "saveMessage")
    public Message saveMessage(@WebParam(name = "idSender") String idSender, @WebParam(name = "groups") ArrayList<String> groups, @WebParam(name = "members") ArrayList<String> members, @WebParam(name = "title") String title, @WebParam(name = "messageBody") String messageBody, @WebParam(name = "ms") MessageStatus ms, @WebParam(name = "attachments") ArrayList<Attachment> attachments, @WebParam(name = "token") String token);

    /**
     * Updates an user
     *
     * @param m The new user definition
     * @return TRUE id succeed, FALSE else
     */
    @WebMethod(operationName = "updateUser")
    public boolean updateUser(Member m);

    /**
     * Gets all the tasks of a member as a String. Each task is separated with a
     * comma.
     *
     * @param id_member The ID of the member to get the tasks
     * @return The tasks of a member as a String. Each task is separated with a
     * comma.
     */
    @WebMethod(operationName = "getMemberTasks")
    public String getMemberTasks(String id_member);

    /**
     * Gets all the groups of a member as a String. Each group is separated with
     * a comma.
     *
     * @param id_member The ID of the member to get the tasks
     * @return The groups of a member as a String. Each group is separated with
     * a comma.
     */
    @WebMethod(operationName = "getMemberGroups")
    public String getMemberGroups(String id_member);

    /**
     * Gets the number of messages for a status
     *
     * @param id_membre The ID of the member who ants to consult his inbox
     * @param mst The status to count
     * @return The number of messages with the specified status
     */
    @WebMethod(operationName = "getNbMessagesForStatus")
    public int getNbMessagesForStatus(String id_membre, MessageStatus mst);

    /**
     * Update the profile of a member
     *
     * @param m The new member definition
     * @param mdp The new password, if null, do not change.
     * @return TRUE : ok, FALSE : error
     */
    @WebMethod(operationName = "updateUserProfile")
    public boolean updateUserProfile(Member m, String mdp);

    /**
     * Updates a group
     *
     * @param idGroup The ID of the group to update
     * @param nomG The new group name
     * @param descrG The new description of the group
     * @param membersG The new members IDs of the group
     * @param chefG The new chief. If null, do not change.
     * @return
     */
    @WebMethod(operationName = "updateGroup")
    public boolean updateGroup(String idGroup, String nomG, String descrG, String[] membersG, String chefG);
    
    /**
     * Checks if the token is valid
     * @param token The token
     * @return the ID of the member if valid, null else
     */
    @WebMethod(operationName = "isValidToken")
    public String isValidToken(@WebParam(name = "token") String token);
    
}
