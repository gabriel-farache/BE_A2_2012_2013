package dataObjects;

import java.util.ArrayList;

public class MessageHeader extends Header {

	private ArrayList<MessageStatus> status;

	/**
	 * Constructor of MessageHeader.
	 * @param id ID of the header.
	 */
	public MessageHeader(String id) {
		super(id);
	}

	/**
	 * Constructor of MessageHeader with full informations. Date as String : DD/MM/YYYY HH:MM.
	 * @param id ID of the message.
	 * @param title title of the message.
	 * @param sender sender of the message.
	 * @param creationDate creation date of the message.
	 * @param haveAttachment Indicates if there is attachment.
	 */
	public MessageHeader(String id, String title, String sender,
			String creationDate, boolean haveAttachment) {
		super(id, title, sender, creationDate, haveAttachment);
		this.setStatus(new ArrayList<MessageStatus>());
	}

	/**
	 * 
	 * Constructor of MessageHeader with full informations. Details date.
	 * @param id ID of the message.
	 * @param title title of the message.
	 * @param sender sender of the message.
	 * @param creationDay creation Day of the message.
	 * @param creationMonth creation Month of the message.
	 * @param creationYear creation Year of the message.
	 * @param creationHour creation Hour of the message.
	 * @param creationMinutes creation Minutes of the message.
	 * @param haveAttachment Indicates if there is attachment.
	 * @param status The status of the message.
	 */
	public MessageHeader(String id, String title, String sender,
			int creationDay, int creationMonth, int creationYear, int creationHour, int creationMinutes, boolean haveAttachment, ArrayList<MessageStatus> status) {
		super(id, title, sender, creationDay, creationMonth, creationYear, creationHour, creationMinutes, haveAttachment);
		this.setTitle(title);
		this.setSender(sender);
		this.setCreationDate(creationDay, creationMonth, creationYear, creationHour, creationMinutes);
		this.setStatus(new ArrayList<MessageStatus>());
	}

	/**
	 * Gives the status of the message
	 * @return The status of the message.
	 */
	public ArrayList<MessageStatus> getStatus() {
		return status;
	}

	/**
	 * Removes the status from the list.
	 * @param ms the status to remove.
	 */
	public void removeStatus(MessageStatus ms) {
		this.removeStatus(ms);
	}

	/**
	 * Sets the status of the message
	 * @param status The status of the message
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
}
