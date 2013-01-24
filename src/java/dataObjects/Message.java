package dataObjects;


/**
 * This class represents a Message in the system.
 * @author durban
 */
public class Message extends Item{
    
    /**
     * Basic constructor of a Message with only its ID.
     * @param messHeader Header of the message.
     */
    public Message(MessageHeader messHeader) {
        super(messHeader);
    }

    public Message(String id, String sender, String title, String creationDate, String content) {
        super(new MessageHeader(id,title,sender,creationDate,false));
        super.setContent(content);
    }

    @Override
    /**
     * Gives a stringified version of the object.
     */
    public String toString() {
        return super.toString()+"\n*** TYPE=MESSAGE";
    }

    
    
   
}
