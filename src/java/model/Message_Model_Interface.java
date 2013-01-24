/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import dataObjects.Message;
import dataObjects.MessageHeader;
import dataObjects.MessageStatus;
import dataObjects.Recipient;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author gabriel
 */
public interface Message_Model_Interface {

    /**
     * Gets the headers of the messages
     *
     * @param token The token of the session
     * @param received TRUE : get the received messages header, FALSE : get the
     * send messages header
     * @return The headers of the messages
     * @throws SQLException
     */
    public ArrayList<MessageHeader> getHeaderMessages(String token, boolean received) throws SQLException;

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
    public Message getMessageBody(String idMessage, String token, boolean received) throws SQLException;

    /**
     *
     * @brief sauvegarde un message dans la BDD sa piece jointe et link les
     * destinataires
     * @param m message
     * @throws SQLException
     */
    public void saveMessage(Message m) throws SQLException;

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
    public void updateMessageStatus(String token, String idMessage, MessageStatus ms, boolean addStatus) throws SQLException;

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
    public Message createMessage(String idSender, ArrayList<Recipient> members, String title, String message, MessageStatus ms);

    /**
     * Deletes a message. ONLY for RECEIVED messages
     *
     * @param token The token of the session
     * @param idMessage The ID of the message to delete
     * @throws SQLException
     */
    public void deleteMessage(String token, String idMessage) throws SQLException;

    /**
     * Notify a new message by e-mail
     *
     * @param id The ID of the member to mail
     * @param m The message that he receives in his company inbox
     * @throws SQLException
     */
    public void notifyNewMessage(String id, Message m) throws SQLException;

    /**
     * Gets the number of message for a given status
     *
     * @param id_membre The ID of the member to check the messages
     * @param mst The status to count
     * @return The number of member's messages for the status
     */
    public int getNbMessagesForStatus(String id_membre, MessageStatus mst);

    /**
     * Checks if the message has a status associated with a given member
     *
     * @param id_membre The ID of member
     * @param id_message The ID of the message
     * @param status The status to check
     * @return TRUE : yes, FALSE : no
     */
    public boolean messageHasStatusAssociatedWithAMember(String id_membre, int id_message, MessageStatus status);
}
