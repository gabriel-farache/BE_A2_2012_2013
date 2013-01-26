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
public class TaskHeaderDecorator extends TableDecorator{ 

    public String getId() {
        TaskHeader th = (TaskHeader)getCurrentRowObject();
        return th.getId();
    }

    public String getTitle() {
        TaskHeader th = (TaskHeader)getCurrentRowObject();
        return th.getTitle();
    }

    public String getSender() {
        TaskHeader th = (TaskHeader)getCurrentRowObject();
        return th.getSender();
    }

    public String getCreationDate() {
        TaskHeader th = (TaskHeader)getCurrentRowObject();
        return th.getStringCreationDate();
    }

    public boolean isHasAttachments() {
        TaskHeader th = (TaskHeader)getCurrentRowObject();
        return th.hasAttachments();
    }

    public String getDueDate() {
        TaskHeader th = (TaskHeader)getCurrentRowObject();
        return th.getStringDueDate();
    }

    public String getProject_topic() {
        TaskHeader th = (TaskHeader)getCurrentRowObject();
        return th.getProject_topic();
    }

    public TaskStatus getStatus() {
        TaskHeader th = (TaskHeader)getCurrentRowObject();
        return th.getStatus();
    }

    
}
