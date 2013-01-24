package dataObjects;

import java.util.GregorianCalendar;

public abstract class Header {

    private String id;                             //Id of the header.
    private String title;                       //Title of the header.
    private String sender;                      //Sender of the header.
    private GregorianCalendar creationDate;     //Date of creation of the header.
    private boolean hasAttachments;				//The item has attachment(s)

    /**
     * Constructor of Header.
     *
     * @param id ID of the header.
     */
    public Header(String id) {
        this.setId(id);
    }

    /**
     * Constructor of Header with full informations. Date as String : DD/MM/YYYY
     * HH:MM.
     *
     * @param id ID of the header.
     * @param title title of the header.
     * @param sender sender of the header.
     * @param creationDate creation date of the header.
     * @param hasAttachments Indicates if there is attachment.
     */
    public Header(String id, String title, String sender,
            String creationDate, boolean hasAttachments) {
        this.setId(id);
        this.setTitle(title);
        this.setSender(sender);
        this.setCreationDate(creationDate);
        this.setHasAttachments(hasAttachments);
    }

    /**
     *
     * Constructor of Header with full informations. Details date.
     *
     * @param id ID of the header.
     * @param title title of the header.
     * @param sender sender of the header.
     * @param creationDay creation Day of the header.
     * @param creationMonth creation Month of the header.
     * @param creationYear creation Year of the header.
     * @param creationHour creation Hour of the header.
     * @param creationMinutes creation Minutes of the header.
     * @param hasAttachments Indicates if there is attachment.
     */
    public Header(String id, String title, String sender,
            int creationDay, int creationMonth, int creationYear, int creationHour, int creationMinutes, boolean hasAttachments) {
        this.setId(id);
        this.setTitle(title);
        this.setSender(sender);
        this.setCreationDate(creationDay, creationMonth, creationYear, creationHour, creationMinutes);
        this.setHasAttachments(hasAttachments);
    }

    /**
     * Indicates if there is, at least, one attachment.
     *
     * @return TRUE if have attachment, FALSE else
     */
    public boolean hasAttachments() {
        return hasAttachments;
    }

    /**
     * Sets the possibility of an attachment.
     *
     * @param hasAttachments TRUE if have attachment, FALSE else
     */
    public void setHasAttachments(boolean hasAttachments) {
        this.hasAttachments = hasAttachments;
    }

    /**
     * Gives the ID of the header.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the ID of the header.
     *
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gives the title of the header.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the header.
     *
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gives the sender of the header.
     *
     * @return the sender
     */
    public String getSender() {
        return sender;
    }

    /**
     * Sets the sender of the header.
     *
     * @param sender the sender to set
     */
    public void setSender(String sender) {
        this.sender = sender;
    }

    /**
     * Gives the date of creation of the header.
     *
     * @return the creationDate
     */
    public GregorianCalendar getCreationDate() {
        return this.creationDate;
    }

    /**
     * Gives a stringified version of the creation date.
     *
     * @return stringified version of the creation date.
     */
    public String getStringCreationDate() {
        return (this.getDateAsString(this.getCreationDate()));
    }

    /**
     * Sets the creation date of the header given the several time parameters.
     *
     * @param day day in the week of creation of the header. 1 to 31.
     * @param month month of crreation of the header, 0 to 11.
     * @param year year of creation of the header.
     * @param hour hour of creation of the header.
     * @param minutes minutes of creation of the header.
     */
    public void setCreationDate(int day, int month, int year, int hour, int minutes) {
        this.setCreationDate(new GregorianCalendar(year, month, day, hour, minutes));
    }
    

    /**
     * Sets the creation date of the header given the stringified version with
     * format DD/MM/YYYY HH:MM.
     *
     * @param date Stringified version of date.
     */
    public void setCreationDate(String date) {
        int day, month, year, hours, mins;
        System.err.println("------------***       "+date);
        try {
            if (date.contains("-")) {
                year = Integer.parseInt(date.split("-")[0]);
                month = Integer.parseInt(date.split("-")[1]);
                day = Integer.parseInt(date.split("-")[2].split(" ")[0]);
                hours = Integer.parseInt(date.split(" ")[1].split(":")[0]);
                mins = Integer.parseInt(date.split(" ")[1].split(":")[1]);
            } else {
                day = Integer.parseInt(date.split("/")[0]);
                month = Integer.parseInt(date.split("/")[1]);
                year = Integer.parseInt(date.split("/")[2].split(" ")[0]);
                hours = Integer.parseInt(date.split(" ")[1].split(":")[0]);
                mins = Integer.parseInt(date.split(" ")[1].split(":")[1]);
            }
            this.setCreationDate(day, month - 1, year, hours, mins);
        } catch (Exception ex) {
            this.setCreationDate(new GregorianCalendar());
            System.err.println("Erreur : " + ex.getMessage());
        }
    }

    /**
     * Transforms a date into String
     *
     * @param date The date to transform
     * @return The date as String
     */
    protected String getDateAsString(GregorianCalendar date) {
        return (date.getTime().getYear() + 1900) + "/"
                + ((date.getTime().getMonth() + 1) < 10 ? "0" : "")
                + (date.getTime().getMonth() + 1) + "/"
                + (date.getTime().getDate() < 10 ? "0" : "")
                + date.getTime().getDate() + " "
                + (date.getTime().getHours() < 10 ? "0" : "")
                + date.getTime().getHours() + ":"
                + (date.getTime().getMinutes() < 10 ? "0" : "")
                + date.getTime().getMinutes();
    }

    /**
     * Sets the creation date
     *
     * @param creationDate the creationDate to set
     */
    protected void setCreationDate(GregorianCalendar creationDate) {
        this.creationDate = creationDate;
    }
}
