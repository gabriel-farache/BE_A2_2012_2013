package dataObjects;

import java.util.ArrayList;
import java.util.GregorianCalendar;

/**
 * This class represents any items, message or task.
 * Since an item can hahve different form, this class is abstract.
 * @author durban
 */
public abstract class Item{

    private Header header;
    private ArrayList<Recipient> recipients;    //Recipients list of the item.
    private ArrayList<Attachment> attachments;  //Attachments list of the item.
    private String content;                     //Content of the item (text).
    
    /**
     * Basic constructor of an item with only its header.
     * Creates empty recipients and attachments list.
     * @param header the header of the item.
     */
    public Item(Header header) {
        this.setHeader(header);
        this.recipients = new ArrayList<Recipient>();
        this.attachments = new ArrayList<Attachment>();
    }

    /**
     * Basic constructor of an item with nothing.
     * Creates empty recipients and attachments list.
     */
    public Item() {
        this.recipients = new ArrayList<Recipient>();
        this.attachments = new ArrayList<Attachment>();
    }

    /**
     * Gives the header of the item.
     * @return The header of the item.
     */
    public Header getHeader() {
		return header;
	}

    /**
     * Gives the ID of the item.
     * @return the id
     */
    public String getId() {
        return this.header.getId();
    }

    /**
     * Sets the ID of the item.
     * @param id the id to set
     */
    public void setId(String id) {
        this.header.setId(id);
    }

    /**
     * Gives the title of the item.
     * @return the title
     */
    public String getTitle() {
        return this.header.getTitle();
    }

    /**
     * Sets the title of the item.
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.header.setTitle(title.replaceAll("'", "`"));
    }

    /**
     * Gives the sender of the item.
     * @return the sender
     */
    public String getSender() {
        return this.header.getSender();
    }

    /**
     * Sets the sender of the item.
     * @param sender the sender to set
     */
    public void setSender(String sender) {
        this.header.setSender(sender);
    }

    /**
     * Gives the date of creation of the item.
     * @return the creationDate
     */
    public GregorianCalendar getCreationDate() {
        return this.header.getCreationDate();
    }

    /**
     * Gives a stringified version of the creation date.
     * @return stringified version of the creation date.
     */
    public String getStringCreationDate() {
        return this.header.getStringCreationDate();
    }

    /**
     * Sets the creation date of the item given the several time parameters.
     * @param day   day in the week of creation of the item. 1 to 31.
     * @param month month of crreation of the item, 0 to 11.
     * @param year year of creation of the item.
     * @param hour hour of creation of the item.
     * @param minutes minutes of creation of the item.
     */
    public void setCreationDate(int day, int month, int year, int hour, int minutes) {
        this.header.setCreationDate(year,month,day,hour,minutes);
    }

    /**
     * Sets the creation date of the item given the stringified version with format
     * DD/MM/YYYY HH:MM.
     * @param date Stringified version of date.
     */
    public void setCreationDate(String date) {
        this.header.setCreationDate(date);
    }
    
    /**
     * Sets the header of the item.
     * @param header The header of the item.
     */
	public void setHeader(Header header) {
		this.header = header;
	}


	/**
     * Gives the list of recipients of the item.
     * @return the recipients
     */
    public ArrayList<Recipient> getRecipients() {
        return recipients;
    }

    /**
     * Add a recipient to the list.
     * @param recp recipient to add.
     */
    public void addRecipient(Recipient recp) {
        this.recipients.add(recp);
    }
    
    /**
     * Add a list of recipients to the list.
     * @param recps recipient to add.
     */
    public void addRecipients(ArrayList<Recipient> recps) {
        this.recipients.addAll(recps);
    }

    /**
     * Remove a recipient to the list.
     * @param recp recipient to remove.
     */
    public void removeRecipient(Recipient recp) {
        this.recipients.remove(recp);
    }

    /**
     * Add an attachment to the list.
     * @param att attachment to add.
     */
    public void addAttachment(Attachment att) {
        if(this.getAttachmentsCount() == 0)
            this.header.setHasAttachments(true);
        this.getAttachments().add(att);
    }

    /**
     * Remove an attachment to the list.
     * @param att attachment to remove.
     */
    public void removeAttachment(Attachment att) {
        this.getAttachments().remove(att);
        if(this.getAttachmentsCount() == 0)
            this.header.setHasAttachments(false);
    }

    /**
     * Gives the list of attachements of the item.
     * @return the attachments
     */
    public ArrayList<Attachment> getAttachments() {
        return attachments;
    }
    
    @Override
    /**
     * Gives a stringified version of the object.
     */
    public String toString() {
    	Header h = this.getHeader();
    	
        String str= "ID="+h.getId()+" - Title="+h.getTitle();
        str += "\nFrom: "+h.getSender()+" on "+h.getStringCreationDate();
        str += "\nTo: ("+this.getRecipientsCount()+" recipients)";
        for(Recipient rec : this.getRecipients()) {
            str += "\n\t"+rec;
        }
        str += "\nWith "+this.getAttachmentsCount()+" attachments : ";
        for(Attachment att : this.getAttachments()) {
            str += "\n\t"+att;
        }

        return str;
    }

    /**
     * Indicates if the item has attachments or not.
     * @return true if the item has attachments, false otherwise.
     */
    public boolean hasAttachments() {
        return this.getAttachments().size() > 0;
    }

    /**
     * Gives the number of attachments of the item.
     * @return the number of attachments of the item.
     */
    public int getAttachmentsCount() {
        return this.getAttachments().size();
    }

    /**
     * Gives the number of recipients of the item.
     * @return the number of recipients of the item.
     */
    public int getRecipientsCount() {
        return this.getRecipients().size();
    }
    
    /**
     * Gives the content of the item.
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the content of the item.
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content.replaceAll("'", "`");
    }

    /**
     * Sets the recipients list.
     * @param recipients the recipients to set
     */
    protected void setRecipients(ArrayList<Recipient> recipients) {
        this.recipients = recipients;
    }

    /**
     * Sets the attachments list.
     * @param attachments the attachments to set
     */
    protected void setAttachments(ArrayList<Attachment> attachments) {
    	this.header.setHasAttachments(true);
        this.attachments = attachments;
    }
    
    /**
	 * Sets the possibility of an attachment. 
	 * @param hasAttachments TRUE if have attachment, FALSE else
	 */
	public void setHasAttachments(boolean hasAttachments) {
		this.header.setHasAttachments(hasAttachments);
	}
}

    
