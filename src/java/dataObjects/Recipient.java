package dataObjects;

/**
 * This class represents a recipient of an item.
 *
 * @author durban
 */
public class Recipient {

    private RecipientType type;    //Type of the recepient (user or group).
    private String id;      //ID of the recipient.

    /**
     * Basic constructor of a recipient with the type and its ID.
     *
     * @param type type of the recipient (user or group).
     * @param id ID of the recipient.
     */
    public Recipient(RecipientType type, String id) {
        this.setType(type);
        this.setId(id);
    }

    /**
     * Basic constructor of a recipient with the type and its ID.
     *
     * @param type type of the recipient (user or group).
     * @param id ID of the recipient.
     */
    public Recipient(String type, String id) {
        this.setType(type);
        this.setId(id);
    }

    public Recipient(String type, int id) {
        this.setType(type);
        this.setId("" + id + "");
    }

    /**
     * Gives the type of the recipient.
     *
     * @return the type (user or group).
     */
    public RecipientType getType() {
        return type;
    }

    /**
     * Sets the type of the recipient.
     *
     * @param type the type to set
     */
    public void setType(RecipientType type) {
        this.type = type;
    }

    /**
     * Sets the type of the recipient from a string parameter.
     *
     * @param type the type to set [user|group|userInGroup|all]
     */
    public void setType(String type) {
        if (type.equals("user")) {
            this.setType(RecipientType.USER);
        } else if (type.equals("group")) {
            this.setType(RecipientType.GROUP);
        } else if (type.equals("userInGroup")) {
            this.setType(RecipientType.USER_IN_GROUP);
        } else if (type.equals("all")) {
            this.setType(RecipientType.ALL);
        } else {
            this.setType(RecipientType.USER);
        }
    }

    /**
     * Gives the ID of the recipient.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the ID of the recipient.
     *
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    @Override
    /**
     * Gives a stringified version of the object.
     */
    public String toString() {
        return "Id=" + this.getId() + " Type=" + this.getType();
    }
}
