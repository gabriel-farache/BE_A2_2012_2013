/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package interactionsDB;

import dataObjects.Attachment;
import dataObjects.Message;
import dataObjects.MessageHeader;
import dataObjects.MessageStatus;
import dataObjects.Recipient;
import dataObjects.RecipientType;
import dataObjects.Task;
import dataObjects.TaskHeader;
import dataObjects.TaskStatus;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import peopleObjects.Group;
import peopleObjects.GroupHeader;
import peopleObjects.Member;

/**
 *
 * @author gabriel
 */
public interface InteractDB_Interface {

    /**
     * Adds a member
     *
     * @param id Id of the member
     * @param nom Name of the member
     * @param prenom First name of the member
     * @param email E-mail of the member
     * @param hash Password
     * @return 1 : Success, != 1 error
     * @throws SQLException
     */
    public int addMember(String id, String nom, String prenom, String email, String hash, boolean isAdmin) throws SQLException;

    /**
     * Inserts a group in th DB
     *
     * @param id ID of the group
     * @param nom Name of the group
     * @param description Description of the group
     * @return 1 : Success, != 1 error
     * @throws SQLException
     */
    public int addGroup(String id, String nom, String description) throws SQLException;

    /**
     * Affects a member to a group
     *
     * @param idPersonne ID of the affected member
     * @param idGroupe ID of the group affected
     * @param isChefProjet TRUE if the member is the chief, FALSE else
     * @return 1 : Success, != 1 error
     * @throws SQLException
     */
    public int addAffectionGroup(String idPersonne, String idGroupe, boolean isChefProjet) throws SQLException;

    /**
     * Inserts a task in the DB
     *
     * @param titre Title of the task
     * @param description Description of the task
     * @param topicProjet Topic/project of the task
     * @param dateDebut Begin date of the task
     * @param dateFin End date of the task
     * @param statut Status of the task
     * @param budget Budget of the task
     * @param consomme Consumed time for the task
     * @param rae Still to do of the task
     * @return null error, else ID of the task newly inserted in the DB
     * @throws SQLException, Exception
     */
    public Integer addTask(String titre, String description, String topicProjet, java.util.Date dateDebut, java.util.Date dateFin, TaskStatus statut, float budget, float consomme, float rae) throws SQLException, Exception;

    /**
     * Inserts a task in the DB and associate it with all it recipients
     *
     * @param titre Title of the task
     * @param description Description of the task
     * @param topicProjet Topic/project of the task
     * @param dateDebut Begin date of the task
     * @param dateFin End date of the task
     * @param statut Status of the task
     * @param budget Budget of the task
     * @param consomme Consumed time for the task
     * @param rae Still to do of the task
     * @return null error, else ID of the task newly inserted in the DB
     * @throws SQLException, Exception
     */
    public Integer addTaskAndAssociate(Task t) throws SQLException, Exception;

    /**
     * Adds an attachment to a task
     *
     * @param att The attachment
     * @param idTache The id of the task
     * @return 1 : Success, != 1 error
     * @throws SQLException
     */
    public int addSendAttachmentToTask(Attachment att, int idTache) throws SQLException;

    /**
     * @param idPersonneSource ID of the dispatcher of the task
     * @param idPersonneDestination ID of the recipient member of the task
     * @param idTache ID of affected task
     * @param typeRept Type of recipient (USER or USER_IN_GROUP)
     * @return 1 : Success, != 1 error
     * @throws SQLException
     */
    public int addSendTaskToMember(String idPersonneSource, String idPersonneDestination, int idTache, RecipientType typeRept, boolean isChefProjet) throws SQLException;

    /**
     * Adds the affections of task to a group DO NOT adds the member in group to
     * the task
     *
     * @param idPersonneSource ID of the dispatcher of the task
     * @param idGroupeDestination ID of the recipient group of the task
     * @param idTache ID of affected task
     * @return 1 : Success, != 1 error, 1991 : group not exists
     * @throws SQLException
     */
    public int addSendTaskToGroup(String idPersonneSource, String idGroupeDestination, int idTache) throws SQLException;

    /**
     * Adds the affections of task to a group and adds the member in group to
     * the task
     *
     * @param idPersonneSource ID of the dispatcher of the task
     * @param idGroupeDestination ID of the recipient group of the task
     * @param idTache ID of affected task
     * @return 1 : Success, != 1 error, 1991 : group not exists
     * @throws SQLException
     */
    public int addSendTaskToGroupAndAssociateToMembers(String idPersonneSource, String idGroupeDestination, int idTache) throws SQLException;

    /**
     * Save a message in the DB
     *
     * @param objet Subject of the message
     * @param date Date of the message
     * @param contenu The message itself
     * @param idSender The ID of the sender
     * @return null error, else ID of the message newly inserted in the DB
     * @throws SQLException
     */
    public Integer addMessage(String objet, java.util.Date date, String contenu, String idSender) throws SQLException;

    /**
     * Attach a file with a message
     *
     * @param idMessage ID of the message with the attachment
     * @param att The attachment of the message
     * @return 1 : Success, != 1 error
     * @throws SQLException
     */
    public int addAttachedFileToMessage(int idMessage, Attachment att) throws SQLException;

    /**
     * Links a save a message in the DB with a member
     *
     * @param idPersonneSource ID of the sender
     * @param idPersonneDestination ID of the recipient member
     * @param idMessage ID of the saved message
     * @param typeDestinataire Type of the recipient ('User', 'User_in_group')
     * @return 1 : Success, != 1 error
     * @throws SQLException
     */
    // typeDestinataire : 'User', 'User_in_group'
    public int addSendMessageToMember(String idPersonneSource, String idPersonneDestination, int idMessage, RecipientType typeDestinataire) throws SQLException;

    /**
     * Links a save a message in the DB with a group. THIS METHOD DO NOT MAKE
     * INSERTION INTO THE MEMBER'S TABLE.
     *
     * @param idPersonneSource ID of the sender
     * @param idGroupDestination ID of the recipient group
     * @param idMessage ID of the saved message
     * @return 1 : Success, != 1 error, 1991 : association already exists
     * @throws SQLException
     */
    public int addSendMessageToGroup(String idPersonneSource, String idGroupDestination, int idMessage) throws SQLException;

    /**
     * Links a save a message in the DB with a group, included the association
     * with the message and the members of the group.
     *
     * @param idPersonneSource ID of the sender
     * @param idGroupDestination ID of the recipient group
     * @param idMessage ID of the saved message
     * @return 1 : Success, != 1 error, 1991 : group id not exists
     * @throws SQLException
     */
    public int addSendMessageToGroupAndAssociateToMembers(String idPersonneSource, String idGroupDestination, int idMessage) throws SQLException;

    /**
     * Return the members of a group
     *
     * @param id_group The ID of the group to get the members
     * @return The members in the group
     * @throws SQLException
     */
    public ArrayList<String> getMembersGroup(String id_group) throws SQLException;

    /**
     * Gets all the groups headers in the DB
     *
     * @return All the groups headers
     * @throws SQLException
     */
    public ArrayList<GroupHeader> getGroupsHeaderData() throws SQLException;

    /**
     * Gets a group from the DB
     *
     * @param id_group The ID of the group
     * @return The group that has the given ID
     * @throws SQLException
     */
    public Group getGroupInfos(String id_group) throws SQLException;

    /**
     * Get a member from the DB
     *
     * @param id_membre The ID of the member to get
     * @return The member that has the given ID
     * @throws SQLException
     */
    public Member getMemberInfos(String id_membre) throws SQLException;

    /**
     * Gets all members of the DB
     *
     * @return All the members store in the DB
     * @throws SQLException
     */
    public ArrayList<Member> getAllMembers() throws SQLException;

    /**
     * Gets all tasks header from the DB
     *
     * @return All tasks headers store in the DB
     * @throws SQLException
     */
    public ArrayList<TaskHeader> getTasksHeaders() throws SQLException;

    /**
     * Gets all tasks header from the DB for a member
     *
     * @param id_member The ID of the member
     * @return All tasks headers store in the DB for this member
     * @throws SQLException
     */
    public ArrayList<TaskHeader> getTasksHeaders(String id_member) throws SQLException;

    /**
     * Gets a task from the DB
     *
     * @param id_tache The ID of the task to get
     * @return The task associate with the given ID
     * @throws SQLException
     */
    public Task getTask(int id_tache) throws SQLException;

    /**
     * Update a task in the DB with the given task
     *
     * @param t The task to update
     * @return 1 : Success, != 1 error
     * @throws SQLException
     */
    public int updateTask(Task t) throws SQLException;

    /**
     * Update recipients contain in the Map to the DB
     *
     * @param rcpts Map containing the recipient and TRUE if insert, FALSE if in
     * 0 index and TRUE if is chief, FALSE else in the 1 index delete
     * @param id_Task The ID of the task to add the recipients
     * @param id_Sender The ID of the member who affected the task (creator)
     * @throws SQLException
     * @throws NumberFormatException
     */
    public int updateRecipientsTask(HashMap<Recipient, Boolean[]> rcpts, String id_Task, String id_Sender) throws NumberFormatException, SQLException;

    /**
     * Update attachments contain in the Map to the DB
     *
     * @param atts Map containing the recipient and TRUE if insert, FALSE if
     * delete
     * @param id_Task The ID of the task to add the recipients
     * @return 1 : Success, != 1 error
     * @throws SQLException
     */
    public int updateAttachmentsTask(HashMap<Attachment, Boolean> atts, String id_Task) throws NumberFormatException, SQLException;

    /**
     * Delete the association task - member in the DB
     *
     * @param idTask The ID of the task to delete
     * @param id_member The ID of the member
     * @return TRUE : Success, FALSE : one (or more) error(s) occur(s)
     * @throws SQLException
     */
    public boolean deleteTaskForMember(int idTask, String id_member) throws SQLException;

    /**
     * Delete the association task -in the DB
     *
     * @param idTask The ID of the task to delete
     * @return TRUE : Success, FALSE : one (or more) error(s) occur(s)
     * @throws SQLException
     */
    public boolean deleteTask(int idTask) throws SQLException;

    /**
     * Deletes all the tasks of a member
     *
     * @param id_member The id of the member
     * @return TRUE : all succeed, FALSE : one or more errors occurs
     * @throws SQLException
     */
    public boolean deleteAllMemberTasks(String id_member) throws SQLException;

    /**
     * Authenticate a member
     *
     * @param login The login of the member
     * @param hashPwd The hash password
     * @return TRUE: success, FALSE: fail
     */
    public boolean authenticate(String login, String hashPwd);

    /**
     * Gets a message store in the DB
     *
     * @param id_message The id of the message to get
     * @param id_membre The ID of the recipient
     * @return The message send to the member
     * @throws SQLException
     */
    public Message getReceivedMessage(int id_message, String id_membre) throws SQLException;

    /**
     * Gets a message send by member
     *
     * @param id_membre The member who received the messages
     * @param id_message ID of the message to get
     * @return All the headers of all the send messages of the member
     * @throws SQLException
     */
    public Message getSendMessage(int id_message, String id_membre) throws SQLException;

    /**
     * Gets all messages receive for a member
     *
     * @param id_membre The member who received the messages
     * @return All the headers of all the received messages of the member
     * @throws SQLException
     */
    public ArrayList<MessageHeader> getReceivedMessagesHeader(String id_membre) throws SQLException;

    /**
     * Gets all messages send by member
     *
     * @param id_membre The member who received the messages
     * @return All the headers of all the send messages of the member
     * @throws SQLException
     */
    public ArrayList<MessageHeader> getSendMessagesHeader(String id_membre) throws SQLException;

    /**
     * Sets the status to a message for a member
     *
     * @param id_message The id of the message to set the status
     * @param status The new status
     * @param id_member The ID of the member who updated the status of the
     * message
     * @return 1 : Success, != 1 error
     * @throws SQLException
     */
    public int addMessageStatus(int id_message, MessageStatus status, String id_member) throws SQLException;

    /**
     * Deletes the status to a message for a member
     *
     * @param id_message The id of the message to set the status
     * @param status The old status
     * @param id_member The ID of the membre who updated the status of the
     * message
     * @return 1 : Success, != 1 error
     * @throws SQLException
     */
    public int delMessageStatus(int id_message, MessageStatus status, String id_member) throws SQLException;

    /**
     * Deletes all the messages of a member
     *
     * @param id_member The id of the member
     * @return TRUE : all succeed, FALSE : one or more errors occurs
     * @throws SQLException
     */
    public boolean deleteAllMemberMessages(String id_member) throws SQLException;

    /**
     * Deletes a message receive by a member to him If there is no more member
     * associated with, it deletes all the attachements, status, associate group
     * and the message itself from the DB
     *
     * @param id_message THe ID of the message to delete
     * @param id_membre The ID of the member who wants delete the message
     * @return TRUE: All related delete succeed, FALSE: one or more delete
     * failed
     * @throws SQLException
     */
    public boolean delMessage(int id_message, String id_membre) throws SQLException;

    /**
     * Checks if the message has at least one status associated with the given
     * member
     *
     * @param id_membre The member to test if he has at least one status
     * associated with the given message
     * @param id_message The message to test
     * @return TRUE : the message has at least one status associated with the
     * given member, FALSE : the message has no status associated with the given
     * member
     * @throws SQLException
     */
    public boolean messageHasStatusAssociatedWithAMember(String id_membre, int id_message) throws SQLException;

    /**
     * Checks if the message has a particular status associated with the given
     * member
     *
     * @param id_membre The member to test if he has a status associated with
     * the given message
     * @param id_message The message to test
     * @param status The status to test
     * @return TRUE : the message has a particular status associated with the
     * given member, FALSE : the message has no status associated with the given
     * member
     * @throws SQLException
     */
    public boolean messageHasStatusAssociatedWithAMember(String id_membre, int id_message, MessageStatus status) throws SQLException;

    /**
     * Checks if the message has at least one member associated with
     *
     * @param id_message The message to test
     * @return TRUE : the message has at leat one member associated with, FALSE
     * : the message has no member associated with
     * @throws SQLException
     */
    public boolean messageHasAssociatedMembers(int id_message) throws SQLException;

    /**
     * Checks if the message has at least one group associated with
     *
     * @param id_message The message to test
     * @return TRUE : the message has at leat one group associated with, FALSE :
     * the message has no group associated with
     * @throws SQLException
     */
    public boolean messageHasAssociatedGroups(int id_message) throws SQLException;

    /**
     * Checks if the message has at least, one attachment
     *
     * @param id_message The message to test
     * @return TRUE : the message has at leat one attachment, FALSE : the
     * message has no attachment
     * @throws SQLException
     */
    public boolean messageHasAttachment(int id_message) throws SQLException;

    /**
     * Checks if the ID exists in the table APP.T_Membre
     *
     * @param id_member The ID to test if exists
     * @return TRUE : exists, FALSE : not exists
     */
    public boolean checkIDMemberExists(String id_member);

    /**
     * Checks if the ID exists in the table APP.T_Tache
     *
     * @param id_Task The ID to test if exists
     * @return TRUE : exists, FALSE : not exists
     */
    public boolean checkIDTaskExists(int id_Task);

    /**
     * Checks if the ID exists in the table T_Group
     *
     * @param id_Group The ID to test if exists
     * @return TRUE : exists, FALSE : not exists
     */
    public boolean checkIDGroupExists(String id_Group);

    /**
     * Deletes a member in the DB and all his relations.
     *
     * @param id_member The ID of the member to delete
     * @return 1 == sucess, != 1 at least one error
     * @throws SQLException
     */
    public int deleteMember(String id_member) throws SQLException;

    /**
     * Deletes a member from a group.
     *
     * @param id_member The ID of the member to delete
     * @return 1 == sucess, != 1 at least one error
     * @throws SQLException
     */
    public int deleteMemberFromGroup(String id_member, String id_Group) throws SQLException;

    /**
     * Deletes a group in the DB and all his relations
     *
     * @param id_Group The ID of the group to delete
     * @return 1 == sucess, != 1 at least one error
     * @throws SQLException
     */
    public int deleteGroup(String id_Group) throws SQLException;

    /**
     * Checks if group has at least one association with a message
     *
     * @param id_Group The ID of the group to test
     * @return TRUE : there is at least one association with a message, FALSE :
     * no association
     * @throws SQLException
     */
    public boolean groupHasAssociatedMessaged(String id_Group) throws SQLException;

    /**
     * Checks if member has at least one association with a message
     *
     * @param id_member The ID of the member to test
     * @return TRUE : there is at least one association with a message, FALSE :
     * no association
     * @throws SQLException
     */
    public boolean memberHasAssociatedMessaged(String id_member) throws SQLException;

    /**
     * Checks if group has at least one association with a task
     *
     * @param id_Group The ID of the group to test
     * @return TRUE : there is at least one association with a task, FALSE : no
     * association
     * @throws SQLException
     */
    public boolean groupHasAssociatedTask(String id_Group) throws SQLException;

    /**
     * Checks if member has at least one association with a task
     *
     * @param id_member The ID of the member to test
     * @return TRUE : there is at least one association with a task, FALSE : no
     * association
     * @throws SQLException
     */
    public boolean memberHasAssociatedTask(String id_member) throws SQLException;

    /**
     * Checks if a message is associated with a given group
     *
     * @param idMessage The id of the message
     * @param id_group The id of the group
     * @return TRUE : it is associated, FALSE : not associate
     * @throws SQLException
     */
    public boolean messageIsAssociatedWithGroup(int idMessage, String id_group) throws SQLException;

    /**
     * Checks if a message is associated with a given member
     *
     * @param idMessage The id of the message
     * @param id_member The id of the member
     * @return TRUE : it is associated, FALSE : not associate
     * @throws SQLException
     */
    public boolean messageIsAssociatedWithMember(int idMessage, String id_member) throws SQLException;

    /**
     * Checks if the task has association with a member
     *
     * @param id_task The ID of the task
     * @return TRUE : there is at least one association with a member, FALSE :
     * no association
     * @throws SQLException
     */
    public boolean taskHasAssociatedMember(int id_task) throws SQLException;

    /**
     * Checks if the task is association with the given member
     *
     * @param id_task The ID of the task
     * @param id_member The ID of the member
     * @return TRUE : the task is associated with the member, FALSE : not
     * associated
     * @throws SQLException
     */
    public boolean taskIsAssociatedWithMember(int id_task, String id_member) throws SQLException;

    /**
     * Gets the tasks of a member
     *
     * @param id_member The ID of the member
     * @return The list of the tasks of the member separated by a comma
     * @throws SQLException
     */
    public String getTasksMember(String id_member) throws SQLException;

    /**
     * Checks if the task is association with the given group
     *
     * @param id_task The ID of the task
     * @param id_Group The ID of the group
     * @return TRUE : the task is associated with the group, FALSE : not
     * associated
     * @throws SQLException
     */
    public boolean taskIsAssociatedWithGroup(int id_task, String id_Group) throws SQLException;

    /**
     * Checks if the task has group association
     *
     * @param id_task The ID of the task
     * @return TRUE : there is at least one association with a group, FALSE : no
     * association
     * @throws SQLException
     */
    public boolean taskHasAssociatedGroup(int id_task) throws SQLException;

    /**
     * Checks if the task has attachement
     *
     * @param id_task The ID of the task
     * @return TRUE : there is at least one attachment, FALSE : no attachment
     * @throws SQLException
     */
    public boolean taskHasAttachement(int id_task) throws SQLException;

    /**
     * Checks if the member is admin
     *
     * @param id_member The ID of the member
     * @return TRUE : is admin, FALSE : is not admin
     * @throws SQLException
     */
    public boolean isAdmin(String id_member) throws SQLException;

    /**
     * Checks if the member is chief of the task DO NOT USE
     *
     * @param id_member The ID of the member
     * @param id_task The ID of the task
     * @return TRUE : is chief, FALSE : is not chief
     * @throws SQLException
     */
    public boolean isChiefTask(String id_member, int id_task) throws SQLException;

    /**
     * Updates a member with the new informations
     *
     * @param m The member with the new informations
     * @return 1 : ok, != 1 error
     */
    public int updateMember(Member m);

    /**
     * Updates the group with the new informations
     *
     * @param g The group containing the new informations
     * @param newRecipients The new recipients : <ID of the recipient, (TRUE :
     * add the recipient, FALSE : delete the recpient)>
     * @param idChef The new chief
     * @return 1 : ok, != 1 error
     */
    public int updateGroup(Group g, HashMap<String, Boolean> newRecipients, String idChef);

    /**
     * Checks if the group is associated with the given member
     *
     * @param idGroup The ID of the group
     * @param id_member The ID of the given member
     * @return TRUE : yes, FALSE : no
     * @throws SQLException
     */
    public boolean groupIsAssociatedWithMember(String idGroup, String id_member) throws SQLException;

    /**
     * Checks if the member is associated with at least one group
     *
     * @param id_member The ID of the member
     * @return TRUE : yes, FALSE : no
     * @throws SQLException
     */
    public boolean memberHasAssociatedGroup(String id_member) throws SQLException;

    /**
     * Gets the groups in which the member is in
     *
     * @param id_member The ID of the member
     * @return The list of the groups of the member separated by a comma
     * @throws SQLException
     */
    public String getMemberGroups(String id_member) throws SQLException;

    /**
     * Gets the number of messages for a status for a member
     *
     * @param id_membre The ID of the member who has the status
     * @param mst The message status, if null, search a non read message
     * @return The number of messages with the mst status
     */
    public int getNbMessagesForStatus(String id_membre, MessageStatus mst);

    /**
     * Updates the password of a member
     *
     * @param idMember The ID of the member to change the password
     * @param hashMdp The new password as a hash
     * @return
     */
    public int updateMemberPswd(String idMember, String hashMdp);

    /**
     * Search an expression in a table
     *
     * @param table The table in which perform the search
     * @param colName The column in which perform the search
     * @param expr The expression to search
     * @param id The ID of the table (name of the column)
     * @return A list of string in the format : colNameToSearch (id)
     */
    public ArrayList<String> searchExprInTable(String table, String colName, String expr, String id);

    /**
     * Search an expression in a table
     *
     * @param table The table in which perform the search
     * @param colNameToSearch The column in which perform the search
     * @param colNameDetail Column for additional informations
     * @param expr The expression to search
     * @param id The ID of the table (name of the column)
     * @return A list of string in the format : colNameToSearch colNameDetail
     * (id)
     */
    public ArrayList<String> searchExprInTable(String table, String colNameToSearch, String colNameDetail, String expr, String id);
}
