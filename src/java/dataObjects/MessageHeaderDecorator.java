/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dataObjects;

import java.util.GregorianCalendar;
import org.displaytag.decorator.TableDecorator;

/**
 *
 * @author gabriel
 */
public class MessageHeaderDecorator extends TableDecorator{
    private String id;                             //Id of the header.
    private String title;                       //Title of the header.
    private String sender;                      //Sender of the header.
    private GregorianCalendar creationDate;     //Date of creation of the header.
    private boolean hasAttachments;	

    public String getId() {
        MessageHeader mh = (MessageHeader)getCurrentRowObject();
        return mh.getId();
    }

    public String getTitle() {
        MessageHeader mh = (MessageHeader)getCurrentRowObject();
        return mh.getTitle();
    }

    public String getSender() {
        MessageHeader mh = (MessageHeader)getCurrentRowObject();
        return mh.getSender();
    }

    public String getCreationDate() {
        MessageHeader mh = (MessageHeader)getCurrentRowObject();
        return mh.getStringCreationDate();
    }

    public boolean isHasAttachments() {
        MessageHeader mh = (MessageHeader)getCurrentRowObject();
        return mh.hasAttachments();
    }

    
}
