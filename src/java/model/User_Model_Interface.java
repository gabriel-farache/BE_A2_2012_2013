/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.sql.SQLException;
import java.util.ArrayList;
import peopleObjects.Member;

/**
 *
 * @author gabriel
 */
public interface User_Model_Interface {

    /**
     * Authenticates an user in the DB
     *
     * @param login The login of the user
     * @param hash The MD5 of the password of the user
     * @return The token of the session associated with the ID of the member
     */
    public String authenticate(String login, String hash) throws SQLException;

    /**
     * Create a new user
     *
     * @param user The new user to create
     * @param pswd The password of the new user
     * @return TRUE : create, FALSE : error
     */
    public Boolean createNewUser(Member user, String pswd) throws SQLException;

    /**
     * Gets all the members
     *
     * @return The list of all the members
     */
    public ArrayList<Member> getAvailableMembers();

    /**
     * Gets the informations of a member
     *
     * @param id_member The member to gets the infos
     * @return The member with all the infos
     */
    public Member getInfosMember(String id_member);

    /**
     * Load the form page and check if the user is connected DO NOTHING
     *
     * @return
     */
    public Boolean getNewUserForm();

    /**
     * Gets all the users in the DB and add them to the page to load
     *
     * @return The list of all the members in the DB
     * @throws SQLException
     */
    public ArrayList<Member> getUsers() throws SQLException;

    /**
     * Generates an ID for a member
     *
     * @param nom The name of the member
     * @param prenom The first name of the member
     * @return The ID generates for the member
     */
    public String generateIdMember(String nom, String prenom);

    /**
     * Disconnects the user
     *
     * @param token The token of the session
     */
    public void disconnectUser(String token);

    /**
     * Updates a member
     *
     * @param m The new member description
     * @return TRUE : ok, FALSE : error
     */
    public boolean updateUser(Member m);

    /**
     * Update the password of a member
     *
     * @param m The member who wants to update his password
     * @param mdpClair The password in clear (to send the mail)
     * @param hash The password as a MD5 hash
     * @return TRUE : ok, FALSE : error
     */
    public boolean updateUserPswd(Member m, String mdpClair, String hash);

    /**
     * Gets the tasks in which the member is affected
     *
     * @param id_member The ID of the member to get the tasks
     * @return All the tasks in which the user is affected, separated with a
     * comma
     */
    public String getMemberTasks(String id_member);

    /**
     * Gets the groups in which the member is in
     *
     * @param id_member The ID of the member to get the groups
     * @return All the groups in which the user is, separated with a comma
     */
    public String getMemberGroups(String id_member);

    /**
     * Créer un passowrd
     *
     * @param longueur la longueur du passwor
     * @return le password
     */
    public String generatePswd(int longueur);
}
