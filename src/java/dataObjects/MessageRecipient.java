package dataObjects;

import java.util.ArrayList;

public class MessageRecipient extends Recipient{

	private ArrayList<MessageStatus> status;
	
	
	/**
	 * Constructor.
	 * @param type Type of the recipient
	 * @param id ID of the recipient
	 */
	public MessageRecipient(String type, String id) {
		super(type, id);
		this.status = new ArrayList<MessageStatus>();
	}
	/**
	 * Constructor with multiples status
	 * @param type Type of the recipient
	 * @param id ID of the recipient
	 * @param status All the status of the message for this recipient
	 */
	public MessageRecipient(RecipientType type, String id, ArrayList<MessageStatus> status) {
		super(type, id);
		this.setStatus(status);
	}
	
	/**
	 * Constructor with only one status
	 * @param type Type of the recipient
	 * @param id ID of the recipient
	 * @param status The status of the message for this recipient
	 */
	public MessageRecipient(RecipientType type, String id, MessageStatus status) {
		super(type, id);
		this.status = new ArrayList<MessageStatus>();
		this.addStatus(status);
	}
	
	/**
	 * Constructor with multiples status
	 * @param type Type of the recipient
	 * @param id ID of the recipient
	 * @param status All the status of the message for this recipient
	 */
	public MessageRecipient(String type, String id, ArrayList<MessageStatus> status) {
		super(type, id);
		this.setStatus(status);
	}
	
	/**
	 * Constructor with only one status
	 * @param type Type of the recipient
	 * @param id ID of the recipient
	 * @param status The status of the message for this recipient
	 */
	public MessageRecipient(String type, String id, MessageStatus status) {
		super(type, id);
		this.status = new ArrayList<MessageStatus>();
		this.addStatus(status);
	}


	/**
	 * Indicates if the message is new for this recipient
	 * @return TRUE if new, FALSE else
	 */
	public boolean isNew() {
		return this.status.isEmpty();
	}

	/**
	 * Remove all status of the message for this recipient
	 * @param isNew
	 */
	public void setNew(boolean isNew)
	{
		if(isNew)
		{
			this.status = new ArrayList<MessageStatus>();
		}
	}


	/**
	 * Gives the status of the message for this recipient
	 * @return The status of the message for this recipient
	 */
	public ArrayList<MessageStatus> getStatus() {
		return status;
	}

	
	/**
	 * Changes all the status of the message for this recipient
	 * @param status the new status for this recipient
	 */
	public void setStatus(ArrayList<MessageStatus> status) {
		this.status = status;
	}
	
	/**
     * Adds the status to the list if it isn't already there.
     * @param ms status to add.
     */
    public void addStatus(MessageStatus ms) {
        if(!this.status.contains(ms)) {
            this.status.add(ms);
        }
    }
	
	 /**
     * Removes the status from the list.
     * @param ms the status to remove.
     */
    public void removeStatus(MessageStatus ms) {
        this.removeStatus(ms);
    }
}
