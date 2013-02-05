package dataObjects;

import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TaskHeader extends Header {

    private GregorianCalendar dueDate;     	//Due date of the task.
    private String project_topic;
    private TaskStatus status;

    /**
     * Constructor of Task.
     *
     * @param id ID of the task.
     */
    public TaskHeader(String id) {
        super(id);
    }

    /**
     * Constructor of task with full informations. Details date.
     *
     * @param id ID of the task.
     * @param title title of the task.
     * @param sender sender of the task.
     * @param creationDay creation Day of the task.
     * @param creationMonth creation Month of the task.
     * @param creationYear creation Year of the task.
     * @param creationHour creation Hour of the task.
     * @param creationMinutes creation Minutes of the task.
     * @param dueDay Due Day of the task.
     * @param dueMonth Due Month of the task.
     * @param dueYear Due Year of the task.
     * @param dueHour Due Hour of the task.
     * @param dueMinutes Due Minutes of the task.
     * @param haveAttachment Indicates if the task has attachment.
     * @param project_topic The project/topic of the task.
     * @param status The status of the task.
     */
    public TaskHeader(int creationDay, int creationMonth, int creationYear, int creationHour, int creationMinutes, String id, String title, String sender, int dueDay,
            int dueMonth, int dueYear, int dueHour, int dueMinutes, boolean haveAttachment, String project_topic, TaskStatus status) {
        super(id, title, sender, creationDay, creationMonth, creationYear,
                creationHour, creationMinutes, haveAttachment);
        this.setProject_topic(project_topic);
        this.setDueDate(dueDay, dueMonth, dueYear, dueHour, dueMinutes);
        this.setStatus(status);
    }

    /**
     * Constructor of task with full informations. Date as String : DD/MM/YYYY
     * HH:MM.
     *
     * @param id ID of the task.
     * @param title title of the task.
     * @param sender sender of the task.
     * @param creationDate creation date of the header.
     * @param haveAttachment Indicates if there is attachment.
     * @param dueDate Due date of the header.
     * @param status The status of the task.
     */
    public TaskHeader(String id, String title, String sender,
            String creationDate, boolean haveAttachment, String project_topic, String dueDate, TaskStatus status) {
        super(id, title, sender, creationDate, haveAttachment);
        this.setProject_topic(project_topic);
        this.setDueDate(dueDate);
        this.setStatus(status);
    }

    /**
     * Constructor of task with full informations. Details date.
     *
     * @param id ID of the task.
     * @param title title of the task.
     * @param sender sender of the task.
     * @param creationDay creation Day of the task.
     * @param creationMonth creation Month of the task.
     * @param creationYear creation Year of the task.
     * @param creationHour creation Hour of the task.
     * @param creationMinutes creation Minutes of the task.
     * @param dueDay Due Day of the task.
     * @param dueMonth Due Month of the task.
     * @param dueYear Due Year of the task.
     * @param dueHour Due Hour of the task.
     * @param dueMinutes Due Minutes of the task.
     * @param haveAttachment Indicates if the task has attachment.
     * @param project_topic The project/topic of the task.
     * @param status The status of the task.
     */
    public TaskHeader(int creationDay, int creationMonth, int creationYear, int creationHour, int creationMinutes, String id, String title, String sender, int dueDay,
            int dueMonth, int dueYear, int dueHour, int dueMinutes, boolean haveAttachment, String project_topic, String status) {
        super(id, title, sender, creationDay, creationMonth, creationYear,
                creationHour, creationMinutes, haveAttachment);
        this.setProject_topic(project_topic);
        this.setDueDate(dueDay, dueMonth, dueYear, dueHour, dueMinutes);
        this.setStatus(status);
    }

    /**
     * Constructor of task with full informations. Date as String : DD/MM/YYYY
     * HH:MM.
     *
     * @param id ID of the task.
     * @param title title of the task.
     * @param sender sender of the task.
     * @param creationDate creation date of the header.
     * @param haveAttachment Indicates if there is attachment.
     * @param dueDate Due date of the header.
     * @param status The status of the task.
     */
    public TaskHeader(String id, String title, String sender,
            String creationDate, boolean haveAttachment, String project_topic, String dueDate, String status) {
        super(id, title, sender, creationDate, haveAttachment);
        this.setProject_topic(project_topic);
        this.setDueDate(dueDate);
        this.setStatus(status);
    }

    /**
     * Sets the due date of the task given the several time parameters.
     *
     * @param day day in the week of due date of the task. 1 to 31.
     * @param month month of due of the task, 0 to 11.
     * @param year year of due of the task.
     * @param hour hour of due of the task.
     * @param minutes minutes of due of the task.
     */
    public void setDueDate(int day, int month, int year, int hour, int minutes) {
        this.setDueDate(new GregorianCalendar(year, month, day, hour, minutes));
    }

    /**
     * Sets the due date of the task given the stringified version with format
     * DD/MM/YYYY HH:MM.
     *
     * @param date Stringified version of date.
     */
    public void setDueDate(String date) {
        int day, month, year, hours, mins;
        try {
            if (date.contains("-")) {
                try {
                    date = date.split("T")[0] + " " + date.split("T")[1];
                } catch (Exception ex) {
                    Logger.getLogger(TaskHeader.class.getName()).log(Level.SEVERE, null, ex);
                }
                year = Integer.parseInt(date.split("-")[0]);
                month = Integer.parseInt(date.split("-")[1]);
                day = Integer.parseInt(date.split("-")[2].split(" ")[0]);
                hours = Integer.parseInt(date.split(" ")[1].split(":")[0]);
                mins = 0;

            } else {
                day = Integer.parseInt(date.split("/")[0]);
                month = Integer.parseInt(date.split("/")[1]);
                year = Integer.parseInt(date.split("/")[2].split(" ")[0]);
                hours = Integer.parseInt(date.split(" ")[1].split(":")[0]);
                mins = Integer.parseInt(date.split(" ")[1].split(":")[1]);
            }
            this.setDueDate(day, month - 1, year, hours, mins);
        } catch (Exception ex) {
            Logger.getLogger(TaskHeader.class.getName()).log(Level.SEVERE, null, ex);
            this.setDueDate(new GregorianCalendar());
        }
    }

    /**
     * Gives the projext/topic of the task.
     *
     * @return the projext/topic of the task.
     */
    public String getProject_topic() {
        return project_topic;
    }

    /**
     * Sets the projext/topic of the task.
     *
     * @param project_topic The projext/topic of the task.
     */
    public void setProject_topic(String project_topic) {
        this.project_topic = project_topic;
    }

    /**
     * Gives the date of due of the task.
     *
     * @return the creationDate.
     */
    public GregorianCalendar getDueDate() {
        return this.dueDate;
    }

    /**
     * Gives a stringified version of the due date.
     *
     * @return stringified version of the due date.
     */
    public String getStringDueDate() {
        return (this.getDateAsString(this.dueDate));
    }

    /**
     * Gives the status of the task.
     *
     * @return The status of the task.
     */
    public TaskStatus getStatus() {
        return status;
    }

    /**
     * Sets the status of the task.
     *
     * @param status The status of the task.
     */
    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    /**
     * Sets the status of the task from String.
     *
     * @param status the status to set [open|closed|urgent|]
     */
    public void setStatus(String status) {
        if (status.toLowerCase().equals("open")) {
            this.setStatus(TaskStatus.OPEN);
        } else if (status.toLowerCase().equals("closed")) {
            this.setStatus(TaskStatus.CLOSED);
        } else if (status.toLowerCase().equals("urgent")) {
            this.setStatus(TaskStatus.URGENT);
        } else {
            this.setStatus(TaskStatus.OPEN);
        }
    }

    /**
     * Sets the due date
     *
     * @param dueDate the dueDate to set
     */
    public void setDueDate(GregorianCalendar dueDate) {
        this.dueDate = dueDate;
    }
}
