/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dataObjects;

import org.displaytag.decorator.TableDecorator;

/**
 *
 * @author gabriel
 */
public class TaskDecorator extends TableDecorator {

    public String getId() {
        Task t = (Task) getCurrentRowObject();
        return t.getId();
    }

    public String getTitle() {
        Task t = (Task) getCurrentRowObject();
        return t.getTitle();
    }

    public String getSender() {
        Task t = (Task) getCurrentRowObject();
        return t.getSender();
    }

    public String getCreationDate() {
        Task t = (Task) getCurrentRowObject();
        return t.getStringCreationDate();
    }

    public boolean isHasAttachments() {
        Task t = (Task) getCurrentRowObject();
        return t.hasAttachments();
    }

    public String getDueDate() {
        Task t = (Task) getCurrentRowObject();
        return t.getStringDueDate();
    }

    public String getProject_topic() {
        Task t = (Task) getCurrentRowObject();
        return t.getProjectTopic();
    }

    public TaskStatus getStatus() {
        Task t = (Task) getCurrentRowObject();
        return t.getStatus();
    }
}
