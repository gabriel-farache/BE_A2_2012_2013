package dataObjects;

/**
 * This class represents an attachment of an item.
 *
 * @author durban
 */
public class Attachment {

    private String name;        //Filename of the attachment.
    private String content;     //Base64 content of the attachment.

    public Attachment(String name, String content) {
        this.setName(name);
        this.setContent(content);
    }

    /**
     * Gives the name of the Attachment (name of the file).
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the file name of the attachment.
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gives the base64 content of the attachment.
     *
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the base64 content of the attachment.
     *
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    /**
     * Gives a stringified version of the object.
     */
    public String toString() {
        return "Name=" + this.getName();
    }
}
