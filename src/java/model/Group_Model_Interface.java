/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import peopleObjects.Group;
import peopleObjects.GroupHeader;

/**
 *
 * @author gabriel
 */
public interface Group_Model_Interface {

    /**
     * Adds members to a group By default, is not chief of the project
     *
     * @param membersID IDs of the members to add in the group
     * @param id_group The ID of the group
     * @return The list of the failed insert, null if everything was OK
     */
    public ArrayList<String> addMembersGroup(List<String> membersID, String id_group);
/**
     * Adds members to a group By default, is not chief of the project
     *
     * @param membersID IDs of the members to add in the group
     * @param id_group The ID of the group
     * @return The list of the failed insert, null if everything was OK
     */
    public ArrayList<String> deleteMembersGroup(List<String> membersID, String id_group);
/**
     * Create a new group
     * @param group The group to create
     * @return TRUE : created, FALSE : error
     */
    public Boolean createGroup(Group group) throws SQLException;
/**
     * Gets the informations of a group
     *
     * @param id_group The ID of the group to gets the infos
     * @return The group with all it informations
     */
    public Group getGroupInfos(String id_group) throws SQLException;

    /**
     * Generates an ID for a group
     *
     * @param nom_group The name of the group
     * @return The ID for the group
     */
    public String generateIdGroup(String nom_group);

    /**
     * Gets all the groups headers which exists in the DB
     *
     * @return All the groups header stored in the DB
     * @throws SQLException
     */
    public ArrayList<GroupHeader> getExistingGroups() throws SQLException;

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
    public boolean updateGroup(String idGroup, String nomG, String descrG, String[] membersG, String chefG);
}
