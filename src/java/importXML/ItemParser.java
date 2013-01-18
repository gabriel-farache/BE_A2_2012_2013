package importXML;

import dataObjects.Task;
import dataObjects.Recipient;
import dataObjects.Message;
import dataObjects.Item;
import dataObjects.Attachment;
import dataObjects.MessageHeader;
import dataObjects.MessageRecipient;
import dataObjects.TaskHeader;
import errorsLogging.LogErrors;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This class process a XML. It validates it thanks to a XSD file and parses it
 * and build in memory the corresponding objects.
 * @author durban
 */
public class ItemParser {

    /**
     * Validates with the given XSD file (with absolute path)
     * and parses the given file (with absolute path) and build the list of
     * the Item found.
     * @param filePath absolute path of the XML file to parse.
     * @return the list of Item found in the XML file.
     */
    public static ArrayList<Item> parse(String filePath){
        try {
            return parse(new FileInputStream(new File(filePath)));
        } catch (FileNotFoundException ex) {
            LogErrors.getInstance().appendLogMessage(ex.getMessage(), Level.SEVERE);
            return new ArrayList<Item>();
        }
    }

    /**
     * Validates with the given XSD file (with absolute path)
     * and parses the given file (with absolute path) and build the list of
     * the Item found.
     * @param fis input stream of the XML file to parse.
     * @return the list of Item found in the XML file.
     */
    public static ArrayList<Item> parse(InputStream fis) {
        ArrayList<Item> result = new ArrayList<Item>();

        try {
            // parse an XML document into a DOM tree
            DocumentBuilder parser;
            parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = parser.parse(fis);
            // create a SchemaFactory capable of understanding WXS schemas
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            // load a WXS schema, represented by a Schema instance
            Source schemaFile = new StreamSource(ItemParser.class.getResourceAsStream("insa.xsd"));
            Schema schema = factory.newSchema(schemaFile);
            // create a Validator instance, which can be used to validate an instance document
            Validator validator = schema.newValidator();
            // validate the DOM tree
            validator.validate(new DOMSource((Node) (document)));
            //parse the file
            Element racine = document.getDocumentElement();
            NodeList nL = racine.getChildNodes();
            //Call of the recursive method.
            parseFile(result,nL,null);

        } catch (SAXException e) {
            e.printStackTrace();
            System.out.println("Invalid file");
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ItemParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ItemParser.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }

    /**
     * Parses the given nodes list and build the Items list.
     * This method is recurcive.
     * @param item_list Items list to be built.
     * @param nL node list to browses.
     * @param it Current Item extracted (recurcive call).
     */
    private static void parseFile(ArrayList<Item> item_list, NodeList nL, Item it) {
        for(int i = 0 ; i < nL.getLength() ; i++) {
            Node n = nL.item(i);
            //Escape of the "#text" node.
            if(!n.getNodeName().equals("#text")) {
                //If the tag is messages.
                if(n.getNodeName().equals("messages")) {
                    parseFile(item_list,n.getChildNodes(),it);
                } else if(n.getNodeName().equals("tasks")) {
                    parseFile(item_list,n.getChildNodes(),it);
                } else if(n.getNodeName().equals("message")) {
                    Item item = null;
                    NamedNodeMap nnm = n.getAttributes();
                    String id="";
                    if(nnm != null) {
                        //Browses the attributes
                        for( int j = 0 ; j < nnm.getLength() ; j++) {
                              Node nn = nnm.item(j);
                              if(nn.getNodeName().equals("id")) {
                                  id = nn.getNodeValue();
                              }
                        }
                     }
                    item = new Message(new MessageHeader(id));
                    item_list.add(item);
                    parseFile(item_list,n.getChildNodes(),item);
                } else if(n.getNodeName().equals("task")) {
                    Item item = null;
                    NamedNodeMap nnm = n.getAttributes();
                    String id="";
                    if(nnm != null) {
                        //Browses the attributes
                        for( int j = 0 ; j < nnm.getLength() ; j++) {
                              Node nn = nnm.item(j);
                              if(nn.getNodeName().equals("id")) {
                                  id = nn.getNodeValue();
                              }
                        }
                     }
                    item = new Task(new TaskHeader(id));
                    item_list.add(item);
                    parseFile(item_list,n.getChildNodes(),item);
                } else if(n.getNodeName().equals("title")) {
                    it.setTitle(n.getTextContent());
                    parseFile(item_list,n.getChildNodes(),it);
                } else if(n.getNodeName().equals("sender")) {
                    it.setSender(n.getTextContent());
                    parseFile(item_list,n.getChildNodes(),it);
                } else if(n.getNodeName().equals("content")) {
                    it.setContent(n.getTextContent());
                    parseFile(item_list,n.getChildNodes(),it);
                } else if(n.getNodeName().equals("recipients")) {
                    parseFile(item_list,n.getChildNodes(),it);
                } else if(n.getNodeName().equals("recipient")) {
                    String type="";
                    String id="";
                    NamedNodeMap nnm = n.getAttributes();
                    if(nnm != null) {
                        //Browses the attributes
                        for( int j = 0 ; j < nnm.getLength() ; j++) {
                              Node nn = nnm.item(j);
                              if(nn.getNodeName().equals("id")) {
                                  id = nn.getNodeValue();
                              } else if(nn.getNodeName().equals("type")) {
                                  type = nn.getNodeValue();
                              }
                        }
                     }
                    if(it instanceof Message)
                        it.addRecipient(new MessageRecipient(type,id));
                    else
                        it.addRecipient(new Recipient(type,id));
                    parseFile(item_list,n.getChildNodes(),it);
                } else if(n.getNodeName().equals("attachments")) {
                    parseFile(item_list,n.getChildNodes(),it);
                } else if(n.getNodeName().equals("attachment")) {
                    String name="";
                    String content = "";
                    NamedNodeMap nnm = n.getAttributes();
                    if(nnm != null) {
                        //Browses the attributes
                        for( int j = 0 ; j < nnm.getLength() ; j++) {
                              Node nn = nnm.item(j);
                              if(nn.getNodeName().equals("name")) {
                                  name = nn.getNodeValue();
                              } 
                        }
                     }
                    content = n.getTextContent();
                    it.addAttachment(new Attachment(name,content));
                    parseFile(item_list,n.getChildNodes(),it);
                } else if(n.getNodeName().equals("creationDate")) {
                    it.setCreationDate(n.getTextContent());
                    parseFile(item_list,n.getChildNodes(),it);
                } else if(n.getNodeName().equals("dueDate")) {
                    ((Task)(it)).setDueDate(n.getTextContent());
                    parseFile(item_list,n.getChildNodes(),it);
                } else if(n.getNodeName().equals("budget")) {
                    ((Task)(it)).setBudget(Integer.parseInt(n.getTextContent()));
                    parseFile(item_list,n.getChildNodes(),it);
                } else if(n.getNodeName().equals("consumed")) {
                    ((Task)(it)).setConsumed(Integer.parseInt(n.getTextContent()));
                    parseFile(item_list,n.getChildNodes(),it);
                } else if(n.getNodeName().equals("RAE")) {
                    ((Task)(it)).setRae(Integer.parseInt(n.getTextContent()));
                    parseFile(item_list,n.getChildNodes(),it);
                } else {
                    parseFile(item_list,n.getChildNodes(),it);
                }
            }
        }
    }
}
