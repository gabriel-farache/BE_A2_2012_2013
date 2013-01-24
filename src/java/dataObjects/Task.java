package dataObjects;

import java.util.GregorianCalendar;

/**
 * This class represents a Task in the system.
 * @author durban
 */
public class Task extends Item {

    private float budget;     //Budget of the task.
    private float consumed;   //Budget consumed for the task.
    private float rae;        //Budget remaining.
    private String chief;

    /**
     * Basic constructor fof a Task with only its ID.
     * @param taskheader header of the Task.
     */
    public Task(TaskHeader taskheader) {
        super(taskheader);
        this.chief = "";
    }
    
    /**
     * Complete constructor with all its attributes.
     * @param id id of the task
     * @param sender sender of the task
     * @param title title of the task
     * @param creationDate creation data of the task
     * @param content content of the task
     * @param budget budget of the task
     * @param consumed budget consumed for the task
     * @param rae budget remaining for the task
     * @param status status of the task
     */
    public Task(String id, String sender, String title, String creationDate, String content, String dueDate, String projectTopic, float budget, float consumed, float rae, TaskStatus status, String chief) {
        super(new TaskHeader(id,title,sender,creationDate,false,projectTopic,dueDate,status));
        super.setContent(content);
        this.setBudget(budget);
        this.setConsumed(consumed);
        this.setRae(rae);
        this.setChief(chief);
    }
    
    /**
     * Complete constructor with all its attributes.
     * @param id id of the task
     * @param sender sender of the task
     * @param title title of the task
     * @param creationDate creation data of the task
     * @param content content of the task
     * @param budget budget of the task
     * @param consumed budget consumed for the task
     * @param rae budget remaining for the task
     * @param status status of the task
     */
    public Task(String id, String sender, String title, String creationDate, String content, String dueDate, String projectTopic, int budget, int consumed, int rae, TaskStatus status, String chief) {
        super(new TaskHeader(id,title,sender,creationDate,false,projectTopic,dueDate,status));
        super.setContent(content);
        this.setBudget(budget);
        this.setConsumed(consumed);
        this.setRae(rae);
        this.setChief(chief);
    }
    
    public Task(String id, String sender, String title, String creationDate, String content, String dueDate, String projectTopic, float budget, float consumed, float rae, String status, String chief) {
        super(new TaskHeader(id,title,sender,creationDate,false,projectTopic,dueDate,status));
        super.setContent(content);
        this.setBudget(budget);
        this.setConsumed(consumed);
        this.setRae(rae);
        this.setChief(chief);
    }
    
    /**
     * Complete constructor with all its attributes.
     * @param id id of the task
     * @param sender sender of the task
     * @param title title of the task
     * @param creationDate creation data of the task
     * @param content content of the task
     * @param budget budget of the task
     * @param consumed budget consumed for the task
     * @param rae budget remaining for the task
     * @param status status of the task
     */
    public Task(String id, String sender, String title, String creationDate, String content, String dueDate, String projectTopic, int budget, int consumed, int rae, String status) {
        super(new TaskHeader(id,title,sender,creationDate,false,projectTopic,dueDate,status));
        super.setContent(content);
        this.setBudget(budget);
        this.setConsumed(consumed);
        this.setRae(rae);
        this.setChief(chief);
    }

    /**
     * Gives the budget of the task.
     * @return the budget
     */
    public float getBudget() {
        return budget;
    }

    /**
     * Sets the budget of the task.
     * @param budget the budget to set
     */
    public void setBudget(float budget) {
        this.budget = budget;
    }

    /**
     * Gives the budget consumed of the task.
     * @return the consumed
     */
    public float getConsumed() {
        return consumed;
    }

    /**
     * Sets the budget consumed of the task.
     * @param consumed the consumed to set
     */
    public void setConsumed(float consumed) {
        this.consumed = consumed;
    }

    /**
     * Gives the budget remaining for the task.
     * @return the budget remaining.
     */
    public float getRae() {
        return rae;
    }

    /**
     * Sets the budget remaining for the task.
     * @param rae the budget remaining to set
     */
    public void setRae(float rae) {
        this.rae = rae;
    }

  

    /**
     * Sets the status of the task
     * @param status the status of the task
     */
	public void setStatus(TaskStatus status) {
		((TaskHeader)this.getHeader()).setStatus(status);
	}
	
	/**
     * Sets the status of the task
     * @param status the status of the task
     */
	public void setStatus(String status) {
		((TaskHeader)this.getHeader()).setStatus(status);
	}
	
    /**
     * Gives the status of the task
     * @return the status
     */
    public TaskStatus getStatus() {
		return ((TaskHeader)this.getHeader()).getStatus();
	}
	

    @Override
    /**
     * Gives a stringified version of the object.
     */
    public String toString() {
    	TaskHeader th = (TaskHeader)this.getHeader();
        return super.toString()+"\nDue to:"+th.getStringDueDate()+
                "\nBudget= "+this.getBudget()+" consumed="+this.getConsumed()+
                " RAE="+this.getRae()+"\n*** TYPE=TASK";
    }
    
    /**
     * Gives a stringified version of the due date.
     * @return stringified version of the due date.
     */
    public String getStringDueDate() {
        return ((TaskHeader)(this.getHeader())).getStringDueDate();
    }
    
    /**
     * Gives a  the due date.
     * @return  the due date.
     */
    public GregorianCalendar getDueDate() {
        return ((TaskHeader)(this.getHeader())).getDueDate();
    }

    /**
     * Set the due date of the item given the several time parameters.
     * @param day   day in the week of creation of the item. 1 to 31.
     * @param month month of crreation of the item, 0 to 11.
     * @param year year of creation of the item.
     * @param hour hour of creation of the item.
     * @param minutes minutes of creation of the item.
     */
    public void setDueDate(int day, int month, int year, int hour, int minutes) {
        ((TaskHeader)(this.getHeader())).setDueDate(day, month, year, hour, minutes);
    }
    
    /**
     * Set the due date of the item given the stringified version with format
     * DD/MM/YYYY HH:MM.
     * @param date Stringified version of date.
     */
    public void setDueDate(String date) {
        ((TaskHeader)(this.getHeader())).setDueDate(date);
    }

    /**
     * Gives the project topic of the task.
     * @return the project topic.
     */
    public String getProjectTopic() {
        return ((TaskHeader)(this.getHeader())).getProject_topic();
    }
    
    /**
     * Sets the project topic.
     * @param pT project topic to set.
     */
    public void setProjectTopic(String pT) {
        ((TaskHeader)(this.getHeader())).setProject_topic(pT);
    }
    
    public void setChief (String chief)
    {
        this.chief = chief;
    }
    
    public String getChief ()
    {
        return this.chief;
    }
}
