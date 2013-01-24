/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import dataObjects.Recipient;
import dataObjects.Task;
import dataObjects.TaskHeader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author gabriel
 */
public interface Task_Model_Interface {

    /**
     * Create a new task
     *
     * @param task The task to create
     * @param sender The sender
     * @param members The IDs of the members recipients of the task
     * @param groups The IDs of the groups recipients of the task
     * @return The ID of the created task
     */
    public String createNewTaskAndNotify(Task task, String sender, ArrayList<String> members, ArrayList<String> groups);

    /**
     * Deletes a task and notify the members affected to this task
     *
     * @param id_Task The task to delete
     * @return TRUE : ok, FALSE : error
     */
    public Boolean deleteTaskAndNotify(int id_Task) throws SQLException;

    /**
     * Checks if the user is identified and has rights to update tasks DO
     * NOTHING
     *
     * @return
     */
    public Boolean generateFormUpTask();

    /**
     * Checks if the user is identified and has rights to create tasks DO
     * NOTHING
     *
     * @return
     */
    public Boolean getCreateTaskForm();

    /**
     * Gets the informations of a task
     *
     * @param id_Task The task to gets the infos
     * @return The task with it informations
     */
    public Task getInfosTask(int id_task);

    /**
     * Gets the headers of all tasks
     *
     * @return The headers of all tasks
     */
    public ArrayList<TaskHeader> getTasksHeader();

    /**
     * Gets the header of all tasks for a given member
     *
     * @param id_member The ID of the member to get the tasks
     * @return The headers of all tasks for a given member
     */
    public ArrayList<TaskHeader> getTasksHeader(String id_member);

    /**
     * Updates a task and notify the affected members
     *
     * @param rcpts The recipients to Add if value = true, to delete if value =
     * false
     * @param taskUpdate The new task description
     * @return
     */
    public Boolean updateTaskAndNotify(Task taskUpdate, HashMap<Recipient, Boolean[]> rcpts);
}
