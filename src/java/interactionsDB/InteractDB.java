/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package interactionsDB;

import dataObjects.*;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import peopleObjects.*;

/**
 *
 * @author gabriel
 */
public class InteractDB implements InteractDB_Interface {

    protected static java.sql.Connection connexion;
    private static Integer nextIDTask;
    private static Integer nextIDMessage;
    private static final String login = "admin";
    private static final String pass = "adminadmin";
    private static final String addr = "jdbc:derby://localhost:1527/BE_A2_2012_2012";
    private static final boolean debug = true;
    private static InteractDB iDB = null;

    /**
     * Singleton constructor
     *
     * @return The singleton InteractDB
     * @throws SQLException
     */
    public static InteractDB getInstance() throws SQLException {
        if (InteractDB.iDB == null) {
            InteractDB.iDB = new InteractDB();
        }

        return InteractDB.iDB;
    }

    /**
     * COnstrcutor
     *
     * @throws SQLException
     */
    private InteractDB() throws SQLException {
        DriverManager.registerDriver(new org.apache.derby.jdbc.EmbeddedDriver());
        InteractDB.connexion = DriverManager.getConnection(addr, login, pass);
        ResultSet donnees;

        String request = "SELECT max(idTache) as curIDTask FROM APP.T_Tache";
        try {
            donnees = this.doRequest(request, java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);
            donnees.first();
            InteractDB.nextIDTask = Integer.parseInt(((donnees.getString("curIDTask") == null) ? "0" : donnees.getString("curIDTask")));
            InteractDB.nextIDTask = (InteractDB.nextIDTask == 0 ? 0 : InteractDB.nextIDTask + 1);
        } catch (SQLException ex) {
            System.out.println("Error when fetching the current idTache from APP.T_Tache.\n" + ex.getMessage() + "\n" + ex.getErrorCode() + "\nDefault : nextIDTask = 1");
            InteractDB.nextIDTask = 0;
        }

        request = "SELECT max(idMessage) as curIDMessage FROM APP.T_Message";
        try {
            donnees = this.doRequest(request, java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);
            donnees.first();
            InteractDB.nextIDMessage = Integer.parseInt(((donnees.getString("curIDMessage") == null) ? "0" : donnees.getString("curIDMessage")));
            InteractDB.nextIDMessage = (InteractDB.nextIDMessage == 0 ? 0 : InteractDB.nextIDMessage + 1);

        } catch (SQLException ex) {
            System.out.println("Error when fetching the current idMessage from APP.T_Message.\n" + ex.getMessage() + "\n" + ex.getErrorCode() + "\nDefault : nextIDMessage = 1");
            InteractDB.nextIDMessage = 0;
        }
    }

    /**
     * Ex�cute un requête sur la BDD
     *
     * @param request The request to execute
     * @param typeScroll
     * @param typeAccess
     * @return resultat de la requête
     * @throws SQLException
     */
    private ResultSet doRequest(String request, int typeScroll, int typeAccess) throws SQLException {
        if (InteractDB.debug) {
            System.out.println("\033[1;35m [DEBUG] Request from \033[1;32m" + Thread.currentThread().getStackTrace()[2].getMethodName() + "\033[1;35m on \033[1;36m" + Thread.currentThread().getStackTrace()[1].getMethodName() + "\033[1;35m : " + request + " \033[0m");
        }
        java.sql.PreparedStatement stmt = InteractDB.connexion.prepareStatement(request, typeScroll, typeAccess);
        return (stmt.executeQuery());
    }

    /**
     * Ajoute/supprime/met à jour une ligne dans la table.
     *
     * @param request
     * @param typeScroll
     * @param typeAccess
     * @return resultat de la requête
     * @throws SQLException
     */
    private int doModif(String request, int typeScroll, int typeAccess) throws SQLException {
        if (InteractDB.debug) {
            System.out.println("\033[1;35m [DEBUG] Request from \033[1;32m" + Thread.currentThread().getStackTrace()[2].getMethodName() + "\033[1;35m on \033[1;36m" + Thread.currentThread().getStackTrace()[1].getMethodName() + "\033[1;35m : " + request + " \033[0m");
        }
        java.sql.PreparedStatement stmt = InteractDB.connexion.prepareStatement(request, typeScroll, typeAccess);
        return (stmt.executeUpdate());
    }

    /**
     * Adds a member
     *
     * @param id Id of the member
     * @param nom Name of the member
     * @param prenom First name of the member
     * @param email E-mail of the member
     * @param hash Password
     * @return 1 : Success, != 1 error
     * @throws SQLException
     */
    @Override
    public int addMember(String id, String nom, String prenom, String email, String hash, boolean isAdmin) throws SQLException {
        String addMemberRequest = "INSERT INTO APP.T_Membre (idMembre, nom, prenom, email, motDePasse,isAdmin) "
                + "VALUES ('" + id.trim() + "', '" + nom.trim() + "', '" + prenom.trim() + "', '" + email.trim() + "', '" + hash.trim() + "', '" + (isAdmin ? 'Y' : 'N') + "' )";
        return doModif(addMemberRequest, java.sql.ResultSet.TYPE_SCROLL_SENSITIVE, java.sql.ResultSet.CONCUR_UPDATABLE);
    }

    /**
     * Inserts a group in th DB
     *
     * @param id ID of the group
     * @param nom Name of the group
     * @param description Description of the group
     * @return 1 : Success, != 1 error
     * @throws SQLException
     */
    @Override
    public int addGroup(String id, String nom, String description) throws SQLException {
        String addGroupRequest = "INSERT INTO APP.T_Groupe (idGroupe, nom, description) "
                + "VALUES ('" + id.trim() + "', '" + nom.trim() + "', '" + description.trim() + "')";

        return doModif(addGroupRequest, java.sql.ResultSet.TYPE_SCROLL_SENSITIVE, java.sql.ResultSet.CONCUR_UPDATABLE);
    }

    /**
     * Affects a member to a group
     *
     * @param idPersonne ID of the affected member
     * @param idGroupe ID of the group affected
     * @param isChefProjet TRUE if the member is the chief, FALSE else
     * @return 1 : Success, != 1 error
     * @throws SQLException
     */
    @Override
    public int addAffectionGroup(String idPersonne, String idGroupe, boolean isChefProjet) throws SQLException {
        String addGroupRequest = "INSERT INTO APP.T_AffectationGroupe (idPersonne,idGroupe,isChefProjet) "
                + "VALUES ('" + idPersonne.trim() + "', '" + idGroupe.trim() + "', '" + ((isChefProjet) ? 'Y' : 'N') + "')";

        return doModif(addGroupRequest, java.sql.ResultSet.TYPE_SCROLL_SENSITIVE, java.sql.ResultSet.CONCUR_UPDATABLE);
    }

    /**
     * Inserts a task in the DB
     *
     * @param titre Title of the task
     * @param description Description of the task
     * @param topicProjet Topic/project of the task
     * @param dateDebut Begin date of the task
     * @param dateFin End date of the task
     * @param statut Status of the task
     * @param budget Budget of the task
     * @param consomme Consumed time for the task
     * @param rae Still to do of the task
     * @return null error, else ID of the task newly inserted in the DB
     * @throws SQLException, Exception
     */
    @Override
    public Integer addTask(String titre, String description, String topicProjet, java.util.Date dateDebut, java.util.Date dateFin, TaskStatus statut, float budget, float consomme, float rae) throws SQLException, Exception {
        if (!dateDebut.after(dateFin)) {
            Integer idT;
            String addTaskRequest;
            String ddebut = this.formatStringAsTimestamp(dateDebut);

            String dfin = this.formatStringAsTimestamp(dateFin);

            String dcreat = this.formatStringAsTimestamp(new java.util.Date(((java.util.Calendar.getInstance()).getTime()).getTime()));
            synchronized (InteractDB.nextIDTask) {
                // traitement du cas dateDebut < dateFin
                addTaskRequest = "INSERT INTO APP.T_Tache (idTache, titre, description, topicProjet, dateCreation, "
                        + "dateDebut, dateFin, statut, budget, consomme, RAE) "
                        + "VALUES (" + InteractDB.nextIDTask + ",  '" + titre.trim() + "' , '" + description.trim() + "', '" + (topicProjet == null ? titre.trim() : topicProjet.trim()) + "', " + "TIMESTAMP('" + dcreat + "')"
                        + ",  TIMESTAMP('" + ddebut + "'),  TIMESTAMP('" + dfin + "'), '" + statut + "',  " + budget + ", " + consomme + ", " + rae + ")";
                idT = InteractDB.nextIDTask++;
            }
            return ((doModif(addTaskRequest, java.sql.ResultSet.TYPE_SCROLL_SENSITIVE, java.sql.ResultSet.CONCUR_UPDATABLE) == 1) ? idT : null);
        } else {
            throw new Exception("Date debut après date fin : debut : " + dateDebut + " fin : " + dateFin);
        }
    }

    /**
     * Generates a string which represents a date in derby timestamp
     *
     * @param date The date to format
     * @return A string which represents a date in derby timestamp
     */
    private String formatStringAsTimestamp(java.util.Date date) {
        return ((date.getYear() + 1900) + "-"
                + ((date.getMonth() + 1) < 10 ? "0" : "")
                + (date.getMonth() + 1) + "-"
                + (date.getDate() < 10 ? "0" : "")
                + date.getDate() + " "
                + (date.getHours() < 10 ? "0" : "")
                + date.getHours() + ":"
                + (date.getMinutes() < 10 ? "0" : "")
                + date.getMinutes() + ":00");
    }

    /**
     * Inserts a task in the DB and associate it with all it recipients
     *
     * @param titre Title of the task
     * @param description Description of the task
     * @param topicProjet Topic/project of the task
     * @param dateDebut Begin date of the task
     * @param dateFin End date of the task
     * @param statut Status of the task
     * @param budget Budget of the task
     * @param consomme Consumed time for the task
     * @param rae Still to do of the task
     * @return null error, else ID of the task newly inserted in the DB
     * @throws SQLException, Exception
     */
    @Override
    public Integer addTaskAndAssociate(Task t) throws SQLException, Exception {
        //ajouter la tache dans la BDD et récupérer le code de retour (si == 1 ok)
        int i = this.addTask(t.getTitle(), t.getContent(),
                t.getProjectTopic(), new java.util.Date(t.getCreationDate().getTimeInMillis()),
                new java.util.Date(t.getDueDate().getTimeInMillis()), TaskStatus.OPEN,
                t.getBudget(), t.getConsumed(), t.getRae());

        //Pour tous les destinataires, les affecter à la tâche
        for (Recipient r : t.getRecipients()) {
            if (r.getType().equals(RecipientType.GROUP)) {
                //Si c'est un groupe, associé ses membres à la tâches
                this.addSendTaskToGroup(t.getSender().trim(), r.getId(), Integer.parseInt(t.getId().trim()));
                ArrayList<String> members = this.getMembersGroup(r.getId());
                for (String member : members) {
                    if (!this.taskIsAssociatedWithMember(Integer.parseInt(t.getId().trim()), member)) {
                        this.addSendTaskToMember(t.getSender().trim(), member, Integer.parseInt(t.getId().trim()), RecipientType.USER_IN_GROUP, (this.getGroupInfos(r.getId().trim()).getChief().getId_member().trim().compareToIgnoreCase(t.getSender()) == 0 ? true : false));
                    }
                }
            } else if (r.getType().equals(RecipientType.ALL)) {
                ArrayList<Member> members = this.getAllMembers();
                for (Member member : members) {
                    this.addSendTaskToMember(t.getSender(), member.getId_member(), Integer.parseInt(t.getId().trim()), RecipientType.USER, false);
                }

            } else {
                this.addSendTaskToMember(t.getSender(), r.getId(), Integer.parseInt(t.getId().trim()), RecipientType.USER, false);
            }

        }

        return i;
    }

    /**
     * Adds an attachment to a task
     *
     * @param att The attachment
     * @param idTache The id of the task
     * @return 1 : Success, != 1 error
     * @throws SQLException
     */
    @Override
    public int addSendAttachmentToTask(Attachment att, int idTache) throws SQLException {
        String rqst = "INSERT INTO APP.T_FichierJointTache (idPJTache, idTache, nomFichier) "
                + "VALUES ( DEFAULT, " + idTache + ", '" + att.getName().trim() + "')";

        return doModif(rqst, java.sql.ResultSet.TYPE_SCROLL_SENSITIVE, java.sql.ResultSet.CONCUR_UPDATABLE);
    }

    /**
     * @param idPersonneSource ID of the dispatcher of the task
     * @param idPersonneDestination ID of the recipient member of the task
     * @param idTache ID of affected task
     * @param typeRept Type of recipient (USER or USER_IN_GROUP)
     * @return 1 : Success, != 1 error
     * @throws SQLException
     */
    @Override
    public int addSendTaskToMember(String idPersonneSource, String idPersonneDestination, int idTache, RecipientType typeRept, boolean isChefProjet) throws SQLException {
        String rqst;
        if (this.taskIsAssociatedWithMember(idTache, idPersonneDestination)) {
            rqst = "UPDATE APP.T_EnvoiTacheMembre SET typeDestinataire = '" + typeRept + "', isChefProjet = '" + (isChefProjet ? 'Y' : 'N') + "' WHERE idPersonneSource = '" + idPersonneSource.trim() + "' AND idPersonneDestination = '" + idPersonneDestination.trim() + "' AND idTache = " + idTache + " ";
        } else {
            rqst = "INSERT INTO APP.T_EnvoiTacheMembre (idPersonneSource, idPersonneDestination, idTache, typeDestinataire, isChefProjet) "
                    + "VALUES ( '" + idPersonneSource.trim() + "', '" + idPersonneDestination.trim() + "', " + idTache + ", '" + typeRept + "', '" + (isChefProjet ? 'Y' : 'N') + "')";
        }

        return doModif(rqst, java.sql.ResultSet.TYPE_SCROLL_SENSITIVE, java.sql.ResultSet.CONCUR_UPDATABLE);
    }

    /**
     * Adds the affections of task to a group DO NOT adds the member in group to
     * the task
     *
     * @param idPersonneSource ID of the dispatcher of the task
     * @param idGroupeDestination ID of the recipient group of the task
     * @param idTache ID of affected task
     * @return 1 : Success, != 1 error, 1991 : group not exists
     * @throws SQLException
     */
    @Override
    public int addSendTaskToGroup(String idPersonneSource, String idGroupeDestination, int idTache) throws SQLException {

        int ret = 1991;

        if (this.checkIDGroupExists(idGroupeDestination)) {
            String addSendTaskToMemberRequest = "INSERT INTO APP.T_EnvoiTacheGroupe (idPersonneSource, idGroupeDestination, idTache) "
                    + "VALUES ( '" + idPersonneSource.trim() + "', '" + idGroupeDestination.trim() + "', " + idTache + ")";

            ret = doModif(addSendTaskToMemberRequest, java.sql.ResultSet.TYPE_SCROLL_SENSITIVE, java.sql.ResultSet.CONCUR_UPDATABLE);

        }
        return ret;
    }

    /**
     * Adds the affections of task to a group and adds the member in group to
     * the task
     *
     * @param idPersonneSource ID of the dispatcher of the task
     * @param idGroupeDestination ID of the recipient group of the task
     * @param idTache ID of affected task
     * @return 1 : Success, != 1 error, 1991 : group not exists
     * @throws SQLException
     */
    @Override
    public int addSendTaskToGroupAndAssociateToMembers(String idPersonneSource, String idGroupeDestination, int idTache) throws SQLException {

        int ret = 1991;

        if (this.checkIDGroupExists(idGroupeDestination)) {
            String addSendTaskToMemberRequest = "INSERT INTO APP.T_EnvoiTacheGroupe (idPersonneSource, idGroupeDestination, idTache) "
                    + "VALUES ( '" + idPersonneSource.trim() + "', '" + idGroupeDestination.trim() + "', " + idTache + ")";
            ArrayList<String> membres = null;

            ret = doModif(addSendTaskToMemberRequest, java.sql.ResultSet.TYPE_SCROLL_SENSITIVE, java.sql.ResultSet.CONCUR_UPDATABLE);
            try {
                membres = this.getMembersGroup(idGroupeDestination);
            } catch (SQLException e) {
            }
            if (membres != null) {
                //Associer les membres du groupes à la tâche
                for (String id_membre : membres) {
                    if (!this.taskIsAssociatedWithMember(idTache, id_membre)) {
                        ret = (this.addSendTaskToMember(idPersonneSource, id_membre, idTache, RecipientType.USER_IN_GROUP, (id_membre.compareToIgnoreCase(this.getGroupInfos(idGroupeDestination).getChief().getId_member()) == 0 ? true : false)) == 1 && ret == 1 ? 1 : 0);
                    }
                }
            }

        }
        return ret;
    }

    /**
     * Save a message in the DB
     *
     * @param objet Subject of the message
     * @param date Date of the message
     * @param contenu The message itself
     * @return null error, else ID of the message newly inserted in the DB
     * @throws SQLException
     */
    @Override
    public Integer addMessage(String objet, java.util.Date date, String contenu) throws SQLException {
        Integer idM;
        String addMemberRequest;
        synchronized (InteractDB.nextIDMessage) {
            // traitement du cas dateDebut < dateFin
            System.err.print(date);
            String dcreat = this.formatStringAsTimestamp(date);
            addMemberRequest = "INSERT INTO APP.T_Message (idMessage, objet, date, contenu) "
                    + "VALUES (" + InteractDB.nextIDMessage + ", '" + objet.trim() + "',  TIMESTAMP('" + dcreat + "'), '" + contenu.trim() + "' )";
            idM = InteractDB.nextIDMessage++;
        }
        return ((doModif(addMemberRequest, java.sql.ResultSet.TYPE_SCROLL_SENSITIVE, java.sql.ResultSet.CONCUR_UPDATABLE) == 1) ? idM : null);
    }

    /**
     * Attach a file with a message
     *
     * @param idMessage ID of the message with the attachment
     * @param att The attachment of the message
     * @return 1 : Success, != 1 error
     * @throws SQLException
     */
    @Override
    public int addAttachedFileToMessage(int idMessage, Attachment att) throws SQLException {
        String addMemberRequest = "INSERT INTO APP.T_FichierJointMessage (idPJMessage, idMessage, nomFichier) "
                + "VALUES ( DEFAULT, " + idMessage + ",  '" + att.getName().trim() + "')";

        return doModif(addMemberRequest, java.sql.ResultSet.TYPE_SCROLL_SENSITIVE, java.sql.ResultSet.CONCUR_UPDATABLE);
    }

    /**
     * Links a save a message in the DB with a member
     *
     * @param idPersonneSource ID of the sender
     * @param idPersonneDestination ID of the recipient member
     * @param idMessage ID of the saved message
     * @param typeDestinataire Type of the recipient ('User', 'User_in_group')
     * @return 1 : Success, != 1 error
     * @throws SQLException
     */
    // typeDestinataire : 'User', 'User_in_group'
    @Override
    public int addSendMessageToMember(String idPersonneSource, String idPersonneDestination, int idMessage, RecipientType typeDestinataire) throws SQLException {
        if (typeDestinataire.equals(RecipientType.USER) || typeDestinataire.equals(RecipientType.USER_IN_GROUP)) {
            if (!this.messageIsAssociatedWithMember(idMessage, idPersonneDestination)) {
                //Envoyer les message
                String addSendMessageToMemberRequest = "INSERT INTO APP.T_EnvoiMessageMembre (idPersonneSource, idPersonneDestination, idMessage, typeDestinataire) "
                        + "VALUES ( '" + idPersonneSource.trim() + "', '" + idPersonneDestination.trim() + "', " + idMessage + ", '" + typeDestinataire + "')";
                return doModif(addSendMessageToMemberRequest, java.sql.ResultSet.TYPE_SCROLL_SENSITIVE, java.sql.ResultSet.CONCUR_UPDATABLE);
            } else {
                return 1;
            }
        } else {
            throw new SQLException("Error : typeDestinataire equals " + typeDestinataire + " but it has to be equals to USER or USER_IN_GROUP. ");
        }
    }

    /**
     * Links a save a message in the DB with a group. THIS METHOD DO NOT MAKE
     * INSERTION INTO THE MEMBER'S TABLE.
     *
     * @param idPersonneSource ID of the sender
     * @param idGroupDestination ID of the recipient group
     * @param idMessage ID of the saved message
     * @return 1 : Success, != 1 error, 1991 : association already exists
     * @throws SQLException
     */
    @Override
    public int addSendMessageToGroup(String idPersonneSource, String idGroupDestination, int idMessage) throws SQLException {
        int ret = 1991;
        if (!this.messageIsAssociatedWithGroup(idMessage, idGroupDestination)) {
            //envoyer le message au groupe
            String addSendMessageToGroupRequest = "INSERT INTO APP.T_EnvoiMessageGroupe (idPersonneSource, idGroupeDestination, idMessage, typeDestinataire) "
                    + "VALUES ( '" + idPersonneSource.trim() + "', '" + idGroupDestination.trim() + "', " + idMessage + ", 'GROUP')";
            ret = doModif(addSendMessageToGroupRequest, java.sql.ResultSet.TYPE_SCROLL_SENSITIVE, java.sql.ResultSet.CONCUR_UPDATABLE);
        }
        return ret;

    }

    /**
     * Links a save a message in the DB with a group, included the association
     * with the message and the members of the group.
     *
     * @param idPersonneSource ID of the sender
     * @param idGroupDestination ID of the recipient group
     * @param idMessage ID of the saved message
     * @return 1 : Success, != 1 error, 1991 : group id not exists
     * @throws SQLException
     */
    @Override
    public int addSendMessageToGroupAndAssociateToMembers(String idPersonneSource, String idGroupDestination, int idMessage) throws SQLException {
        int ret = 1991;

        if (this.checkIDGroupExists(idGroupDestination)) {
            //envoyer le message au groupe
            String addSendMessageToGroupRequest = "INSERT INTO APP.T_EnvoiMessageGroupe (idPersonneSource, idGroupeDestination, idMessage, typeDestinataire) "
                    + "VALUES ( '" + idPersonneSource.trim() + "', '" + idGroupDestination.trim() + "', " + idMessage + ", 'GROUP')";
            ArrayList<String> membres = null;

            ret = doModif(addSendMessageToGroupRequest, java.sql.ResultSet.TYPE_SCROLL_SENSITIVE, java.sql.ResultSet.CONCUR_UPDATABLE);
            try {
                membres = this.getMembersGroup(idGroupDestination);
            } catch (SQLException e) {
            }
            if (membres != null) {
                //Envoyer le messages aux membres du groupe
                for (String id_membre : membres) {
                    ret = (this.addSendMessageToMember(idPersonneSource, id_membre, idMessage, RecipientType.USER_IN_GROUP) == 1 && ret == 1 ? 1 : 0);
                }
            }

        }
        return ret;
    }

    /**
     * Return the members of a group
     *
     * @param id_group The ID of the group to get the members
     * @return The members in the group
     * @throws SQLException
     */
    @Override
    public ArrayList<String> getMembersGroup(String id_group) throws SQLException {
        ArrayList<String> membersInGroup = new ArrayList<String>();
        String request = "SELECT ag.idPersonne as idPersonne "
                + "FROM APP.T_AffectationGroupe ag "
                + "WHERE ag.idGroupe = '" + id_group.trim() + "' ";
        ResultSet donnees = this.doRequest(request, java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);
        //Pour tous les membres du groupes, ajouter leur ID à la liste
        while (donnees.next()) {
            membersInGroup.add(donnees.getString("idPersonne"));
        }
        return membersInGroup;
    }

    /**
     * Gets all the groups headers in the DB
     *
     * @return All the groups headers
     * @throws SQLException
     */
    @Override
    public ArrayList<GroupHeader> getGroupsHeaderData() throws SQLException {
        String request = "SELECT g.nom as nom, g.idGroupe as idGroupe, g.description as description FROM APP.T_Groupe g";
        ResultSet donnees = this.doRequest(request, java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);
        ArrayList<GroupHeader> groupsHeader = new ArrayList<GroupHeader>();

        //Créer les headers des différents groupes
        while (donnees.next()) {
            groupsHeader.add(new GroupHeader(donnees.getString("idGroupe"), donnees.getString("nom"), donnees.getString("description")));
        }
        return (groupsHeader);
    }

    /**
     * Gets a group from the DB
     *
     * @param id_group The ID of the group
     * @return The group that has the given ID
     * @throws SQLException
     */
    @Override
    public Group getGroupInfos(String id_group) throws SQLException {
        String request = "SELECT DISTINCT af.isChefProjet as isChefProj, g.description as description, g.nom as Gnom, g.idGroupe as idGroupe, m.idMembre as idMembre, m.nom as Mnom, m.prenom as Mprenom, m.email as Memail "
                + "FROM APP.T_Groupe g, APP.T_AffectationGroupe af, APP.T_Membre m "
                + "WHERE g.idGroupe = '" + id_group.trim() + "' "
                + "AND af.idPersonne = m.idMembre "
                + "AND af.idGroupe = g.idGroupe ";
        ResultSet donnees = this.doRequest(request, java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);
        Group group;
        String nom_group = null;
        String description = null;
        Member chef = null;
        ArrayList<Member> membres = new ArrayList<Member>();

        //Récupérer et insérer les informations du groupe dans un objet Group
        while (donnees.next()) {
            if (nom_group == null) {
                nom_group = donnees.getString("gnom");
            }
            if (description == null) {
                description = donnees.getString("description");
            }
            membres.add(new Member(donnees.getString("idMembre"), donnees.getString("Mnom"), donnees.getString("Mprenom"), donnees.getString("Memail")));

            if (donnees.getString("isChefProj").compareTo("Y") == 0) {
                chef = membres.get(membres.size() - 1);
            }
        }

        group = new Group(id_group, nom_group, membres, chef, description);

        return (group);
    }

    /**
     * Get a member from the DB
     *
     * @param id_membre The ID of the member to get
     * @return The member that has the given ID
     * @throws SQLException
     */
    @Override
    public Member getMemberInfos(String id_membre) throws SQLException {
        String request = "SELECT m.idMembre as idMembre, m.nom as Mnom, m.prenom as Mprenom, m.email as Memail "
                + "FROM APP.T_Membre m "
                + "WHERE m.idMembre = '" + id_membre.trim() + "' ";
        ResultSet donnees = this.doRequest(request, java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);
        Member membre;
        //Récupérer les infos du membre et le créer
        donnees.first();
        membre = new Member(donnees.getString("idMembre"), donnees.getString("Mnom"), donnees.getString("Mprenom"), donnees.getString("Memail"));
        return (membre);
    }

    /**
     * Gets all members of the DB
     *
     * @return All the members store in the DB
     * @throws SQLException
     */
    @Override
    public ArrayList<Member> getAllMembers() throws SQLException {
        String request = "SELECT m.idMembre as idMembre, m.nom as Mnom, m.prenom as Mprenom, m.email as Memail "
                + "FROM APP.T_Membre m ";
        ResultSet donnees = this.doRequest(request, java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);
        ArrayList<Member> membres = new ArrayList<Member>();

        while (donnees.next()) {
            membres.add(new Member(donnees.getString("idMembre"), donnees.getString("Mnom"), donnees.getString("Mprenom"), donnees.getString("Memail")));
        }
        return (membres);
    }

    /**
     * Gets all tasks header from the DB
     *
     * @return All tasks headers store in the DB
     * @throws SQLException
     */
    @Override
    public ArrayList<TaskHeader> getTasksHeaders() throws SQLException {
        ArrayList<TaskHeader> tasksHeaders = new ArrayList<TaskHeader>();
        String request = "SELECT DISTINCT e.idPersonneSource as idPersonneSource, t.idTache as idTache, t.titre as titre, t.dateDebut as dateDebut, t.dateFin as dateFin, t.topicProjet as topicProjet, t.statut as statut "
                + "FROM APP.T_Tache t, APP.T_EnvoiTacheMembre e "
                + "WHERE e.idTache = t.idTache "
                + "ORDER BY t.statut DESC";
        ResultSet donnees = this.doRequest(request, java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);

        while (donnees.next()) {
            tasksHeaders.add(this.parseInfoTaskHeader(donnees));
        }

        return tasksHeaders;
    }

    /**
     * Gets all tasks header from the DB for a member
     *
     * @param id_member The ID of the member
     * @return All tasks headers store in the DB for this member
     * @throws SQLException
     */
    @Override
    public ArrayList<TaskHeader> getTasksHeaders(String id_member) throws SQLException {
        ArrayList<TaskHeader> tasksHeaders = new ArrayList<TaskHeader>();
        String request = "SELECT e.idPersonneSource as idPersonneSource, t.idTache as idTache, t.titre as titre, t.dateCreation as dateCreation, t.dateFin as dateFin, t.topicProjet as topicProjet, t.statut as statut "
                + "FROM APP.T_Tache t, APP.T_EnvoiTacheMembre e "
                + "WHERE e.idPersonneDestination = '" + id_member.trim() + "' "
                + "AND e.idTache = t.idTache "
                + "ORDER BY t.statut DESC";
        ResultSet donnees = this.doRequest(request, java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);

        while (donnees.next()) {
            tasksHeaders.add(this.parseInfoTaskHeader(donnees));
        }

        return tasksHeaders;
    }

    /**
     * Parses the informations of a task given by a ResultSet and add them to a
     * TaskHeader
     *
     * @param donnees The ResultSet in which gets the informations
     * @return The TaskHeader of in the current cursor position
     * @throws SQLException
     */
    private TaskHeader parseInfoTaskHeader(ResultSet donnees) throws SQLException {
        TaskStatus status;
        TaskHeader taskHeader;
        String statut = donnees.getString("statut");

        if (statut.compareToIgnoreCase("OPEN") == 0) {
            status = TaskStatus.OPEN;
        } else if (statut.compareToIgnoreCase("CLOSED") == 0) {
            status = TaskStatus.CLOSED;
        } else {
            status = TaskStatus.URGENT;
        }
        //Créer la tâche avec les infos récupérés
        taskHeader = new TaskHeader(donnees.getString("idTache"), donnees.getString("titre"), donnees.getString("idPersonneSource"), donnees.getString("dateCreation"), true, donnees.getString("topicProjet"), donnees.getString("dateFin"), status);
        taskHeader.setHasAttachments(this.taskHasAttachement(Integer.parseInt(taskHeader.getId())));

        return taskHeader;
    }

    /**
     * Gets a task from the DB
     *
     * @param id_tache The ID of the task to get
     * @return The task associate with the given ID
     * @throws SQLException
     */
    @Override
    public Task getTask(int id_tache) throws SQLException {
        //récupérer les infos propres de la taches
        String request = "SELECT DISTINCT t.description as description, t.budget as budget, t.consomme as consomme, t.RAE as rae, e.idPersonneSource as idPersonneSource, "
                + "t.idTache as idTache, t.titre as titre, t.dateCreation as dateCreation, t.dateFin as dateFin, t.topicProjet as topicProjet, t.statut as statut "
                + "FROM APP.T_Tache t, APP.T_EnvoiTacheMembre e "
                + "WHERE t.idTache = " + id_tache + " "
                + "ORDER BY t.titre ASC ";
        ResultSet donnees = this.doRequest(request, java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);
        String statut;
        TaskStatus status;
        Task task = null;
        while (donnees.next()) {
            statut = donnees.getString("statut");
            if (statut.compareToIgnoreCase("OPEN") == 0) {
                status = TaskStatus.OPEN;
            } else if (statut.compareToIgnoreCase("CLOSED") == 0) {
                status = TaskStatus.CLOSED;
            } else {
                status = TaskStatus.URGENT;
            }

            task = new Task("" + id_tache, donnees.getString("idPersonneSource").trim(), donnees.getString("titre").trim(), donnees.getString("dateCreation"),
                    donnees.getString("description").trim(), donnees.getString("dateFin").trim(), donnees.getString("topicProjet").trim(),
                    Float.parseFloat(donnees.getString("budget").trim()), Float.parseFloat(donnees.getString("consomme").trim()), Float.parseFloat(donnees.getString("rae").trim()), status, donnees.getString("idPersonneSource").trim());

            //récupérer les destinataires de la tache
            this.getTasksRecipientsUser(id_tache, task);

            //récupérer les groupe destinataires de la tache
            this.getTasksRecipientsGroup(id_tache, task);

            //recuperation fichiers joints
            this.getTasksAttachments(id_tache, task);
        }

        return task;
    }

    /**
     * Gets the recipients USER of a task
     *
     * @param id_tache The ID of the task
     * @param task The task in which add the recipients USER
     */
    private void getTasksRecipientsUser(int id_tache, Task task) {
        ResultSet donneesRecpt;
        try {
            String request = "SELECT DISTINCT tm.idPersonneDestination as idPersonneDestination FROM APP.T_EnvoiTacheMembre tm  WHERE tm.idTache = " + id_tache + " AND tm.typeDestinataire != 'USER_IN_GROUP' ";

            donneesRecpt = this.doRequest(request, java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);
            while (donneesRecpt.next()) {
                task.addRecipient(new Recipient(RecipientType.USER, donneesRecpt.getString("idPersonneDestination")));
            }

        } catch (SQLException ex) {
            if (!ex.getSQLState().startsWith("02")) {
                Logger.getLogger(InteractDB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Gets the recipients GROUP of a task
     *
     * @param id_tache The ID of the task
     * @param task The task in which add the recipients GROUP
     */
    private void getTasksRecipientsGroup(int id_tache, Task task) {
        ResultSet donneesRecpt;
        try {
            String request = "SELECT DISTINCT tg.idGroupeDestination as idGroupeDestination FROM APP.T_EnvoiTacheGroupe tg , APP.T_Tache t WHERE tg.idTache = " + id_tache + " ";

            donneesRecpt = this.doRequest(request, java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);
            while (donneesRecpt.next()) {
                task.addRecipient(new Recipient(RecipientType.GROUP, donneesRecpt.getString("idGroupeDestination")));
            }
        } catch (SQLException ex) {
            if (!ex.getSQLState().startsWith("02")) {
                Logger.getLogger(InteractDB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Gets the attachments of a task
     *
     * @param id_tache The ID of the task
     * @param task The task in which add the attachments
     */
    private void getTasksAttachments(int id_tache, Task task) {
        try {
            String request = "SELECT DISTINCT pj.nomFichier as nomFichier "
                    + "FROM APP.T_FichierJointTache pj "
                    + "WHERE pj.idTache = " + task.getId() + " ";
            ResultSet donneesAtt = this.doRequest(request, java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);
            while (donneesAtt.next()) {
                task.addAttachment(new Attachment(donneesAtt.getString("nomFichier"), donneesAtt.getString("nomFichier")));
            }
        } catch (SQLException ex) {
            if (!ex.getSQLState().startsWith("02")) {
                Logger.getLogger(InteractDB.class.getName()).log(Level.SEVERE, null, ex);
            }
            task.setHasAttachments(false);
        }
    }

    /**
     * Update a task in the DB with the given task
     *
     * @param t The task to update
     * @return 1 : Success, != 1 error
     * @throws SQLException
     */
    @Override
    public int updateTask(Task t) throws SQLException {
        String ddebut = t.getStringCreationDate().replaceAll("/", "-") + ":00";
        String dfin = t.getStringDueDate().replaceAll("/", "-") + ":00";

        String request = "UPDATE APP.T_Tache "
                + " SET description = '" + t.getContent().trim() + "', budget = " + t.getBudget() + ", consomme = " + t.getConsumed() + ", RAE = " + t.getRae() + ", "
                + "titre = '" + t.getTitle().trim() + "', dateCreation = TIMESTAMP('" + ddebut + "'), "
                + "dateFin = TIMESTAMP('" + dfin + "'), topicProjet = '" + t.getProjectTopic().trim() + "', statut = '" + t.getStatus() + "' "
                + "WHERE idTache = " + t.getId() + " ";

        return (this.doModif(request, java.sql.ResultSet.TYPE_SCROLL_SENSITIVE, java.sql.ResultSet.CONCUR_UPDATABLE));
    }

    /**
     * Update recipients contain in the Map to the DB
     *
     * @param rcpts Map containing the recipient and TRUE if insert, FALSE if in
     * 0 index and TRUE if is chief, FALSE else in the 1 index delete
     * @param id_Task The ID of the task to add the recipients
     * @param id_Sender The ID of the member who affected the task (creator)
     * @throws SQLException
     * @throws NumberFormatException
     */
    @Override
    public int updateRecipientsTask(HashMap<Recipient, Boolean[]> rcpts, String id_Task, String id_Sender) throws NumberFormatException, SQLException {
        int ret = 0;
        String err = "";
        for (Entry<Recipient, Boolean[]> entry : rcpts.entrySet()) {
            Recipient cle = entry.getKey();
            Boolean[] valeur = entry.getValue();
            try {            //si ajout d'un recipient
                if (valeur[0]) {
                    if (cle.getType().compareTo(RecipientType.GROUP) != 0) {
                        ret = this.addSendTaskToMember(id_Sender, cle.getId(), Integer.parseInt(id_Task), cle.getType(), valeur[1]);
                    } else {
                        ret = this.addSendTaskToGroupAndAssociateToMembers(id_Sender, cle.getId(), Integer.parseInt(id_Task));
                    }
                } else {
                    if (cle.getType().compareTo(RecipientType.GROUP) != 0) {
                        String request = "DELETE FROM APP.T_EnvoiTacheMembre WHERE idPersonneDestination = '" + cle.getId().trim() + "' AND idTache = " + id_Task + " ";
                        if (this.doModif(request, java.sql.ResultSet.TYPE_SCROLL_SENSITIVE, java.sql.ResultSet.CONCUR_UPDATABLE) != 1) {
                            throw new SQLException("La requête de suppression a échoué pour le recipient ID : " + cle.getId().trim() + " pour la tache " + id_Task + ".");
                        } else {
                            ret = 1;
                        }
                    } else {
                        String request = "DELETE FROM APP.T_EnvoiTacheGroupe WHERE idGroupeDestination = '" + cle.getId().trim() + "' AND idTache = " + id_Task + " ";
                        if (this.doModif(request, java.sql.ResultSet.TYPE_SCROLL_SENSITIVE, java.sql.ResultSet.CONCUR_UPDATABLE) != 1) {
                            throw new SQLException("La requête de suppression a échoué pour le recipient ID : " + cle.getId().trim() + " pour la tache " + id_Task + ".");
                        } else {
                            ret = 1;
                        }
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(InteractDB.class.getName()).log(Level.SEVERE, null, ex);
                err += ex.getMessage() + " \n";
            }
        }
        if (err.compareTo("") != 0) {
            throw new SQLException(err);
        }
        return ret;
    }

    /**
     * Update attachments contain in the Map to the DB
     *
     * @param atts Map containing the recipient and TRUE if insert, FALSE if
     * delete
     * @param id_Task The ID of the task to add the recipients
     * @return 1 : Success, != 1 error
     * @throws SQLException
     */
    @Override
    public int updateAttachmentsTask(HashMap<Attachment, Boolean> atts, String id_Task) throws NumberFormatException, SQLException {
        for (Entry<Attachment, Boolean> entry : atts.entrySet()) {
            Attachment cle = entry.getKey();
            Boolean valeur = entry.getValue();

            //si ajout d'un fichier joint
            if (valeur) {
                this.addSendAttachmentToTask(cle, Integer.parseInt(id_Task));
            } else {
                String request = "DELETE FROM APP.T_FichierJointTache WHERE nomFichier = '" + cle.getName().trim() + "' AND idTache = " + id_Task + " ";
                if (this.doModif(request, java.sql.ResultSet.TYPE_SCROLL_SENSITIVE, java.sql.ResultSet.CONCUR_UPDATABLE) != 1) {
                    throw new SQLException("La requête de suppression a échoué pour le recipient ID : " + cle.getName().trim() + " pour la tache " + id_Task + ".");
                }
            }
        }
        return 0;
    }

    /**
     * Delete the association task - member in the DB
     *
     * @param idTask The ID of the task to delete
     * @param id_member The ID of the member
     * @return TRUE : Success, FALSE : one (or more) error(s) occur(s)
     * @throws SQLException
     */
    @Override
    public boolean deleteTaskForMember(int idTask, String id_member) throws SQLException {
        String request;
        boolean ok = true;

        if (this.taskIsAssociatedWithMember(idTask, id_member)) {
            //suppression affection des membres de la tache
            request = "DELETE FROM APP.T_EnvoiTacheMembre WHERE idTache = " + idTask + " AND idPersonneDestination = '" + id_member.trim() + "' ";
            ok = ok && (this.doModif(request, java.sql.ResultSet.TYPE_SCROLL_SENSITIVE, java.sql.ResultSet.CONCUR_UPDATABLE) == 1);
            if (!this.taskHasAssociatedMember(idTask)) {
                if (this.taskHasAttachement(idTask)) {
                    //suppresion fichiers joint dans la tache
                    request = "DELETE FROM APP.T_FichierJointTache WHERE idTache = " + idTask + " ";
                    ok = ok && (this.doModif(request, java.sql.ResultSet.TYPE_SCROLL_SENSITIVE, java.sql.ResultSet.CONCUR_UPDATABLE) == 1);
                }

                if (this.taskHasAssociatedGroup(idTask)) {
                    //suppression affection des groupes de la tache
                    request = "DELETE FROM APP.T_EnvoiTacheGroupe WHERE idTache = " + idTask + " ";
                    ok = ok && (this.doModif(request, java.sql.ResultSet.TYPE_SCROLL_SENSITIVE, java.sql.ResultSet.CONCUR_UPDATABLE) == 1);
                }
                //suppression tache
                request = "DELETE FROM APP.T_Tache WHERE idTache = " + idTask + " ";
                ok = ok && (this.doModif(request, java.sql.ResultSet.TYPE_SCROLL_SENSITIVE, java.sql.ResultSet.CONCUR_UPDATABLE) == 1);
            }
        }
        return ok;

    }

    /**
     * Delete the association task -in the DB
     *
     * @param idTask The ID of the task to delete
     * @return TRUE : Success, FALSE : one (or more) error(s) occur(s)
     * @throws SQLException
     */
    @Override
    public boolean deleteTask(int idTask) throws SQLException {
        String request;
        boolean ok = true;

        if (this.taskHasAssociatedMember(idTask)) {
            //suppression affection des membres de la tache
            request = "DELETE FROM APP.T_EnvoiTacheMembre WHERE idTache = " + idTask + " ";
            ok = ok && (this.doModif(request, java.sql.ResultSet.TYPE_SCROLL_SENSITIVE, java.sql.ResultSet.CONCUR_UPDATABLE) == 1);
        }

        if (this.taskHasAttachement(idTask)) {
            //suppresion fichiers joint dans la tache
            request = "DELETE FROM APP.T_FichierJointTache WHERE idTache = " + idTask + " ";
            ok = ok && (this.doModif(request, java.sql.ResultSet.TYPE_SCROLL_SENSITIVE, java.sql.ResultSet.CONCUR_UPDATABLE) == 1);
        }

        if (this.taskHasAssociatedGroup(idTask)) {
            //suppression affection des groupes de la tache
            request = "DELETE FROM APP.T_EnvoiTacheGroupe WHERE idTache = " + idTask + " ";
            ok = ok && (this.doModif(request, java.sql.ResultSet.TYPE_SCROLL_SENSITIVE, java.sql.ResultSet.CONCUR_UPDATABLE) == 1);
        }
        //suppression tache
        request = "DELETE FROM APP.T_Tache WHERE idTache = " + idTask + " ";
        ok = ok && (this.doModif(request, java.sql.ResultSet.TYPE_SCROLL_SENSITIVE, java.sql.ResultSet.CONCUR_UPDATABLE) == 1);


        return ok;

    }

    /**
     * Deletes all the tasks of a member
     *
     * @param id_member The id of the member
     * @return TRUE : all succeed, FALSE : one or more errors occurs
     * @throws SQLException
     */
    @Override
    public boolean deleteAllMemberTasks(String id_member) throws SQLException {
        boolean allOk = true;
        if (this.checkIDMemberExists(id_member)) {
            String request = "SELECT idTache as idTache FROM APP.T_EnvoiTacheMembre WHERE idPersonneDestination = '" + id_member.trim() + "' ";
            ResultSet donnees = this.doRequest(request, java.sql.ResultSet.TYPE_SCROLL_SENSITIVE, java.sql.ResultSet.CONCUR_UPDATABLE);
            while (donnees.next()) {
                allOk = allOk && this.deleteTaskForMember(Integer.parseInt(donnees.getString("idTache")), id_member);
            }
            return allOk;
        } else {
            throw new SQLException("Member ID does not exists : " + id_member);
        }
    }

    /**
     * Authenticate a member
     *
     * @param login The login of the member
     * @param hashPwd The hash password
     * @return TRUE: success, FALSE: fail
     */
    @Override
    public boolean authenticate(String login, String hashPwd) {
        boolean ok = false;
        String rqst = "SELECT m.idMembre as idMembre "
                + "FROM APP.T_Membre m "
                + "WHERE m.idMembre = '" + login.trim() + "' "
                + "AND m.motDePasse = '" + hashPwd.trim() + "' ";
        System.err.println("----------**** iciiiiiiiiiiiii   +++");
        try {
            if (this.doRequest(rqst, java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY).first()) {
                ok = true;
                System.err.println("----------**** iciiiiiiiiiiiii  222222 +++");
            }


        } catch (SQLException ex) {
            Logger.getLogger(InteractDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ok;
    }

    /**
     * Gets a message store in the DB
     *
     * @param id_message The id of the message to get
     * @param id_membre The ID of the recipient
     * @return The message send to the member
     * @throws SQLException
     */
    @Override
    public Message getReceivedMessage(int id_message, String id_membre) throws SQLException {
        Message message = null;
        //recupération informations générales message
        String request = "SELECT DISTINCT m.objet as titre, m.date as dateCreation, e.idPersonneSource as idPersonneSource, m.contenu as contenu, e.typeDestinataire as typeDestinataire "
                + "FROM APP.T_Message m, APP.T_EnvoiMessageMembre e "
                + "WHERE e.idPersonneDestination = '" + id_membre.trim() + "' "
                + "AND m.idMessage = " + id_message + " "
                + "AND e.idMessage = " + id_message + " "
                + "AND e.idMessage = m.idMessage "
                + "ORDER BY date DESC";
        ResultSet donnees = this.doRequest(request, java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);
        MessageRecipient mrcpt;

        while (donnees.next()) {
            message = new Message("" + id_message, donnees.getString("idPersonneSource"), donnees.getString("titre"), donnees.getString("dateCreation"), donnees.getString("contenu"));

            mrcpt = this.getMessageStatusToMember(donnees, id_membre, id_message);
            if (mrcpt != null) {
                message.addRecipient(mrcpt);
            }
            this.addMessageRecipientsUser(message, donnees, id_message, id_membre);

            this.addMessageRecipientsGroup(message, donnees, id_message);

            this.addMessageAttachements(message);
        }

        return message;
    }

    /**
     * Gets the status of a message for a member
     *
     * @param donnees The ResultSet that contains the type of recipient
     * @param id_membre The ID of the member
     * @param id_message The ID of the message
     * @return A MessageRecipient containing the different status of the message
     * to the member
     */
    private MessageRecipient getMessageStatusToMember(ResultSet donnees, String id_membre, int id_message) {
        MessageRecipient mrcpt = null;
        String statut;
        MessageStatus status;
        try {
            mrcpt = new MessageRecipient(donnees.getString("typeDestinataire"), id_membre);
            //Recupération des statuts message pour le membre
            String request = "SELECT sm.statut as statut "
                    + "FROM APP.T_StatusMessageMembre sm "
                    + "WHERE sm.idPersonneDestination = '" + id_membre.trim() + "' "
                    + "AND sm.idMessage = " + id_message + " ";

            ResultSet donneesStatus = this.doRequest(request, java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);
            while (donneesStatus.next()) {
                statut = donneesStatus.getString("statut");
                if (statut.compareToIgnoreCase("FORWARDED") == 0) {
                    status = MessageStatus.FORWARDED;
                } else if (statut.compareToIgnoreCase("HAVE_TO_ANSWER") == 0) {
                    status = MessageStatus.HAVE_TO_ANSWER;
                } else if (statut.compareToIgnoreCase("IMPORTANT") == 0) {
                    status = MessageStatus.IMPORTANT;
                } else if (statut.compareToIgnoreCase("URGENT") == 0) {
                    status = MessageStatus.URGENT;
                } else {
                    status = MessageStatus.READ;
                }
                mrcpt.addStatus(status);
            }

        } catch (SQLException ex) {
            if (!ex.getSQLState().startsWith("02")) {
                Logger.getLogger(InteractDB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return mrcpt;
    }

    /**
     * Add the users recipient to the message
     *
     * @param message The message
     * @param donnees The ResultSet tot get the sender
     * @param id_message The ID of the message
     * @param id_membre The ID of the member (exclude from recipients)
     */
    private void addMessageRecipientsUser(Message message, ResultSet donnees, int id_message, String id_membre) {
        try {
            String request = "SELECT em.idPersonneDestination as idPersonneDestination "
                    + "FROM APP.T_EnvoiMessageMembre em "
                    + "WHERE em.idPersonneSource = '" + donnees.getString("idPersonneSource").trim() + "' "
                    + "AND em.idMessage = " + id_message + " "
                    + "AND em.typeDestinataire = 'USER' "
                    + "AND em.idPersonneDestination != '" + id_membre.trim() + "' ";

            ResultSet donneeRcpt = this.doRequest(request, java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);
            while (donneeRcpt.next()) {
                message.addRecipient(new Recipient(RecipientType.USER, donneeRcpt.getString("idPersonneDestination")));
            }
        } catch (SQLException ex) {
            if (!ex.getSQLState().startsWith("02")) {
                Logger.getLogger(InteractDB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Add the groups recipient to the message
     *
     * @param message The message
     * @param donnees The ResultSet tot get the sender
     * @param id_message The ID of the message
     */
    private void addMessageRecipientsGroup(Message message, ResultSet donnees, int id_message) {
        try {
            //ajout destinataire group
            String request = "SELECT eg.idGroupeDestination as idGroupeDestination "
                    + "FROM APP.T_EnvoiMessageGroupe eg "
                    + "WHERE eg.idPersonneSource = '" + donnees.getString("idPersonneSource").trim() + "' "
                    + "AND eg.idMessage = " + id_message + " "
                    + "AND eg.typeDestinataire = 'GROUP' ";

            ResultSet donneeRcptGroup = this.doRequest(request, java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);
            while (donneeRcptGroup.next()) {
                message.addRecipient(new Recipient(RecipientType.GROUP, donneeRcptGroup.getString("idGroupeDestination")));
            }
        } catch (SQLException ex) {
            if (!ex.getSQLState().startsWith("02")) {
                Logger.getLogger(InteractDB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Add the message's attachments
     *
     * @param message The message to add the attachments
     */
    private void addMessageAttachements(Message message) {
        //recuperation fichiers joints
        try {
            String request = "SELECT pj.nomFichier as nomFichier "
                    + "FROM APP.T_FichierJointMessage pj "
                    + "WHERE pj.idMessage = " + message.getId() + " ";
            ResultSet donneesAtt = this.doRequest(request, java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);
            while (donneesAtt.next()) {
                message.addAttachment(new Attachment(donneesAtt.getString("nomFichier"), donneesAtt.getString("nomFichier")));
            }
        } catch (SQLException ex) {
            if (!ex.getSQLState().startsWith("02")) {
                Logger.getLogger(InteractDB.class.getName()).log(Level.SEVERE, null, ex);
            }
            message.setHasAttachments(false);
        }
    }

    /**
     *
     * Gets a message send by member
     *
     * @param id_membre The member who received the messages
     * @param id_message ID of the message to get
     * @return All the headers of all the send messages of the member
     * @throws SQLException
     */
    @Override
    public Message getSendMessage(int id_message, String id_membre) throws SQLException {
        Message message = null;
        //recupération informations générales message
        String request = "SELECT DISTINCT m.objet as titre, m.date as dateCreation, e.idPersonneSource as idPersonneSource, m.contenu as contenu, e.typeDestinataire as typeDestinataire "
                + "FROM APP.T_Message m, APP.T_EnvoiMessageMembre e "
                + "WHERE e.idPersonneSource = '" + id_membre.trim() + "' "
                + "AND m.idMessage = " + id_message + " "
                + "AND e.idMessage = " + id_message + " "
                + "AND e.idMessage = m.idMessage "
                + "ORDER BY date DESC";
        ResultSet donnees = this.doRequest(request, java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);

        while (donnees.next()) {
            message = new Message("" + id_message, donnees.getString("idPersonneSource"), donnees.getString("titre"), donnees.getString("dateCreation"), donnees.getString("contenu"));

            this.addMessageRecipientsUser(message, donnees, id_message, id_membre);

            this.addMessageRecipientsGroup(message, donnees, id_message);

            this.addMessageAttachements(message);
        }
        return message;
    }

    /**
     * Gets all messages receive for a member
     *
     * @param id_membre The member who received the messages
     * @return All the headers of all the messages of the member
     * @throws SQLException
     */
    @Override
    public ArrayList<MessageHeader> getReceivedMessagesHeader(String id_membre) throws SQLException {
        ArrayList<MessageHeader> messagesHeaders = new ArrayList<MessageHeader>();
        String request = "SELECT m.idMessage as idMessage, m.objet as titre, m.date as dateCreation, e.idPersonneSource as idPersonneSource "
                + "FROM APP.T_Message m, APP.T_EnvoiMessageMembre e "
                + "WHERE e.idPersonneDestination = '" + id_membre.trim() + "' "
                + "AND e.idMessage = m.idMessage "
                + "ORDER BY m.idMessage DESC";
        ResultSet donnees = this.doRequest(request, java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);
        String statut;
        MessageStatus status;
        MessageHeader messageHeader;

        while (donnees.next()) {
            messageHeader = new MessageHeader(donnees.getString("idMessage").trim(), donnees.getString("titre").trim(), donnees.getString("idPersonneSource").trim(), donnees.getString("dateCreation").trim(), true);

            try {
                request = "SELECT sm.statut as statut "
                        + "FROM APP.T_StatusMessageMembre sm "
                        + "WHERE sm.idPersonneDestination = '" + id_membre + "' "
                        + "AND sm.idMessage = " + messageHeader.getId() + " ";

                ResultSet donneesStatus = this.doRequest(request, java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);
                while (donneesStatus.next()) {
                    statut = donneesStatus.getString("statut");
                    if (statut.compareToIgnoreCase("FORWARDED") == 0) {
                        status = MessageStatus.FORWARDED;
                    } else if (statut.compareToIgnoreCase("HAVE_TO_ANSWER") == 0) {
                        status = MessageStatus.HAVE_TO_ANSWER;
                    } else if (statut.compareToIgnoreCase("IMPORTANT") == 0) {
                        status = MessageStatus.IMPORTANT;
                    } else if (statut.compareToIgnoreCase("URGENT") == 0) {
                        status = MessageStatus.URGENT;
                    } else {
                        status = MessageStatus.READ;
                    }
                    messageHeader.addStatus(status);
                }

            } catch (SQLException ex) {
                if (!ex.getSQLState().startsWith("02")) {
                    Logger.getLogger(InteractDB.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            messageHeader.setHasAttachments(this.messageHasAttachment(Integer.parseInt(messageHeader.getId())));
            messagesHeaders.add(messageHeader);
        }
        return messagesHeaders;
    }

    /**
     * Gets all messages send by member
     *
     * @param id_membre The member who received the messages
     * @return All the headers of all the send messages of the member
     * @throws SQLException
     */
    @Override
    public ArrayList<MessageHeader> getSendMessagesHeader(String id_membre) throws SQLException {
        ArrayList<MessageHeader> messagesHeaders = new ArrayList<MessageHeader>();
        String request = "SELECT m.idMessage as idMessage, m.objet as titre, m.date as dateCreation, e.idPersonneSource as idPersonneSource "
                + "FROM APP.T_Message m, APP.T_EnvoiMessageMembre e "
                + "WHERE e.idPersonneSource = '" + id_membre.trim() + "' "
                + "AND e.idMessage = m.idMessage "
                + "ORDER BY m.idMessage DESC";
        ResultSet donnees = this.doRequest(request, java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);
        MessageHeader messageHeader;

        while (donnees.next()) {
            messageHeader = new MessageHeader(donnees.getString("idMessage").trim(), donnees.getString("titre").trim(), donnees.getString("idPersonneSource").trim(), donnees.getString("dateCreation").trim(), true);

            messageHeader.setHasAttachments(this.messageHasAttachment(Integer.parseInt(messageHeader.getId())));
            messagesHeaders.add(messageHeader);
        }
        return messagesHeaders;
    }

    /**
     * Sets the status to a message for a member
     *
     * @param id_message The id of the message to set the status
     * @param status The new status
     * @param id_member The ID of the member who updated the status of the
     * message
     * @return 1 : Success, != 1 error
     * @throws SQLException
     */
    @Override
    public int addMessageStatus(int id_message, MessageStatus status, String id_member) throws SQLException {
        int ret = 1;
        String request;

        if (!this.messageHasStatusAssociatedWithAMember(id_member, id_message, status)) {
            request = "INSERT INTO APP.T_StatusMessageMembre (idPersonneDestination, idMessage, statut) "
                    + "VALUES ('" + id_member.trim() + "', " + id_message + ", '" + status + "') ";

            ret = (this.doModif(request, java.sql.ResultSet.TYPE_SCROLL_SENSITIVE, java.sql.ResultSet.CONCUR_UPDATABLE));
        }

        return ret;
    }

    /**
     * Deletes the status to a message for a member
     *
     * @param id_message The id of the message to set the status
     * @param status The old status
     * @param id_member The ID of the membre who updated the status of the
     * message
     * @return 1 : Success, != 1 error
     * @throws SQLException
     */
    @Override
    public int delMessageStatus(int id_message, MessageStatus status, String id_member) throws SQLException {
        String request;
        int result = 1;

        if (this.messageHasStatusAssociatedWithAMember(id_member, id_message, status)) {
            request = "DELETE FROM APP.T_StatusMessageMembre "
                    + "WHERE idPersonneDestination = '" + id_member.trim() + "' "
                    + "AND  idMessage = " + id_message + " "
                    + "AND statut = '" + status + "' ";
            result = (this.doModif(request, java.sql.ResultSet.TYPE_SCROLL_SENSITIVE, java.sql.ResultSet.CONCUR_UPDATABLE));

        }
        return result;
    }

    /**
     * Deletes all the messages of a member
     *
     * @param id_member The id of the member
     * @return TRUE : all succeed, FALSE : one or more errors occurs
     * @throws SQLException
     */
    @Override
    public boolean deleteAllMemberMessages(String id_member) throws SQLException {
        boolean allOk = true;
        if (this.checkIDMemberExists(id_member)) {
            String request = "SELECT idMessage as idMessage FROM APP.T_EnvoiMessageMembre WHERE idPersonneDestination = '" + id_member.trim() + "' ";
            ResultSet donnees = this.doRequest(request, java.sql.ResultSet.TYPE_SCROLL_SENSITIVE, java.sql.ResultSet.CONCUR_UPDATABLE);
            while (donnees.next()) {
                allOk = allOk && this.delMessage(Integer.parseInt(donnees.getString("idMessage").trim()), id_member.trim());
            }
            return allOk;
        } else {
            throw new SQLException("Member ID does not exists : " + id_member.trim());
        }
    }

    /**
     * Deletes a message receive by a member to him If there is no more member
     * associated with, it deletes all the attachements, status, associate group
     * and the message itself from the DB
     *
     * @param id_message THe ID of the message to delete
     * @param id_membre The ID of the member who wants delete the message
     * @return TRUE: All related delete succeed, FALSE: one or more delete
     * failed
     * @throws SQLException
     */
    @Override
    public boolean delMessage(int id_message, String id_membre) throws SQLException {
        boolean allOk = true;
        String request;

        if (this.messageHasStatusAssociatedWithAMember(id_membre, id_message)) {
            //Delete the status of the message for this member
            request = "DELETE FROM APP.T_StatusMessageMembre "
                    + "WHERE idPersonneDestination = '" + id_membre.trim() + "' "
                    + "AND  idMessage = " + id_message + " ";
            allOk = allOk && (this.doModif(request, java.sql.ResultSet.TYPE_SCROLL_SENSITIVE, java.sql.ResultSet.CONCUR_UPDATABLE) == 1);
        }

        //Delete the association membre - message
        request = "DELETE FROM APP.T_EnvoiMessageMembre "
                + "WHERE idPersonneDestination = '" + id_membre.trim() + "' "
                + "AND  idMessage = " + id_message + " ";
        allOk = allOk && (this.doModif(request, java.sql.ResultSet.TYPE_SCROLL_SENSITIVE, java.sql.ResultSet.CONCUR_UPDATABLE) == 1);

        //Test si reste des membre associé au message
        if (!this.messageHasAssociatedMembers(id_message)) {
            this.cleanAndDeleteMessage(allOk, id_message);
        }

        return (allOk);
    }

    /**
     * Cleans all the informations related to the message and delete the message
     *
     * @param allOk Boolean to check if everything was OK
     * @param id_message The ID of the message
     * @return TRUE : ok, FALSE : error
     */
    private boolean cleanAndDeleteMessage(boolean allOk, int id_message) {
        String request;
        try {
            //Suppression fichiers joints
            request = "DELETE FROM APP.T_FichierJointMessage "
                    + "WHERE  idMessage = " + id_message + " ";
            allOk = allOk && (this.doModif(request, java.sql.ResultSet.TYPE_SCROLL_SENSITIVE, java.sql.ResultSet.CONCUR_UPDATABLE) == 1);
        } catch (SQLException ex) {
            if (!ex.getSQLState().startsWith("02")) {
                Logger.getLogger(InteractDB.class.getName()).log(Level.SEVERE, null, ex);
                allOk = false;
            }
        }

        try {
            //suppression de l'association entre un message et un groupe car plus de membre associé
            request = "DELETE FROM APP.T_EnvoiMessageGroupe "
                    + "WHERE  idMessage = " + id_message + " ";
            allOk = allOk && (this.doModif(request, java.sql.ResultSet.TYPE_SCROLL_SENSITIVE, java.sql.ResultSet.CONCUR_UPDATABLE) == 1);
        } catch (SQLException ex) {
            if (!ex.getSQLState().startsWith("02")) {
                Logger.getLogger(InteractDB.class.getName()).log(Level.SEVERE, null, ex);
                allOk = false;
            }
        }
        try {
            //Suppression message pur et dur
            request = "DELETE FROM APP.T_Message "
                    + "WHERE  idMessage = " + id_message + " ";
            allOk = allOk && (this.doModif(request, java.sql.ResultSet.TYPE_SCROLL_SENSITIVE, java.sql.ResultSet.CONCUR_UPDATABLE) == 1);
        } catch (SQLException ex) {
            if (!ex.getSQLState().startsWith("02")) {
                Logger.getLogger(InteractDB.class.getName()).log(Level.SEVERE, null, ex);
                allOk = false;
            }
        }
        return allOk;
    }

    /**
     * Checks if the message has at least one status associated with the given
     * member
     *
     * @param id_membre The member to test if he has at least one status
     * associated with the given message
     * @param id_message The message to test
     * @return TRUE : the message has at least one status associated with the
     * given member, FALSE : the message has no status associated with the given
     * member
     * @throws SQLException
     */
    @Override
    public boolean messageHasStatusAssociatedWithAMember(String id_membre, int id_message) throws SQLException {
        //Verification qu'il y ai des statuts pour le message avant de les supprimer
        String request = "SELECT count(*) as nbStatuts FROM APP.T_StatusMessageMembre "
                + "WHERE idPersonneDestination = '" + id_membre.trim() + "' "
                + "AND  idMessage = " + id_message + " ";
        ResultSet donnees = this.doRequest(request, java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);

        donnees.first();
        return (donnees.getString("nbStatuts") != null && Integer.parseInt(donnees.getString("nbStatuts").trim()) >= 1);
    }

    /**
     * Checks if the message has a particular status associated with the given
     * member
     *
     * @param id_membre The member to test if he has a status associated with
     * the given message
     * @param id_message The message to test
     * @param status The status to test
     * @return TRUE : the message has a particular status associated with the
     * given member, FALSE : the message has no status associated with the given
     * member
     * @throws SQLException
     */
    @Override
    public boolean messageHasStatusAssociatedWithAMember(String id_membre, int id_message, MessageStatus status) throws SQLException {
        //Verification qu'il y ai des statuts pour le message avant de les supprimer
        String request = "SELECT count(*) as nbStatuts FROM APP.T_StatusMessageMembre "
                + "WHERE idPersonneDestination = '" + id_membre.trim() + "' "
                + "AND  idMessage = " + id_message + " "
                + "AND statut = '" + status + "' ";
        ResultSet donnees = this.doRequest(request, java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);

        donnees.first();
        return (donnees.getString("nbStatuts") != null && Integer.parseInt(donnees.getString("nbStatuts").trim()) >= 1);
    }

    /**
     * Checks if the message has at least one member associated with
     *
     * @param id_message The message to test
     * @return TRUE : the message has at leat one member associated with, FALSE
     * : the message has no member associated with
     * @throws SQLException
     */
    @Override
    public boolean messageHasAssociatedMembers(int id_message) throws SQLException {
        //Verify if the member was the last member in the association
        String request = "SELECT count(*) as nbRecipients FROM APP.T_EnvoiMessageMembre "
                + "WHERE idMessage = " + id_message + " ";
        ResultSet donnees = this.doRequest(request, java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);
        donnees.first();

        return (donnees.getString("nbRecipients") != null && Integer.parseInt(donnees.getString("nbRecipients").trim()) >= 1);
    }

    /**
     * Checks if the message has at least one group associated with
     *
     * @param id_message The message to test
     * @return TRUE : the message has at leat one group associated with, FALSE :
     * the message has no group associated with
     * @throws SQLException
     */
    @Override
    public boolean messageHasAssociatedGroups(int id_message) throws SQLException {
        //Verify if the member was the last member in the association
        String request = "SELECT count(*) as nbRecipients FROM APP.T_EnvoiMessageGroupe "
                + "WHERE idMessage = " + id_message + " ";
        ResultSet donnees = this.doRequest(request, java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);
        donnees.first();

        return (donnees.getString("nbRecipients") != null && Integer.parseInt(donnees.getString("nbRecipients").trim()) >= 1);
    }

    /**
     * Checks if the message has at least, one attachment
     *
     * @param id_message The message to test
     * @return TRUE : the message has at leat one attachment, FALSE : the
     * message has no attachment
     * @throws SQLException
     */
    @Override
    public boolean messageHasAttachment(int id_message) throws SQLException {
        //Verify if the member was the last member in the association
        String request = "SELECT count(*) as pjs FROM APP.T_FichierJointMessage "
                + "WHERE idMessage = " + id_message + " ";

        ResultSet donnees = this.doRequest(request, java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);
        donnees.first();

        return (donnees.getString("pjs") != null && Integer.parseInt(donnees.getString("pjs").trim()) > 1);
    }

    /**
     * Checks if the ID exists in the table APP.T_Membre
     *
     * @param id_member The ID to test if exists
     * @return TRUE : exists, FALSE : not exists
     */
    @Override
    public boolean checkIDMemberExists(String id_member) {
        boolean exists;
        String request = "SELECT count(*) as nb "
                + "FROM APP.T_Membre m "
                + "WHERE m.idMembre = '" + id_member.trim() + "' ";

        try {
            ResultSet donnees = this.doRequest(request, java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);
            donnees.first();
            exists = (donnees.getString("nb") != null && Integer.parseInt(donnees.getString("nb").trim()) >= 1);
        } catch (SQLException e) {
            exists = false;
        }
        return exists;
    }

    /**
     * Checks if the ID exists in the table APP.T_Tache
     *
     * @param id_Task The ID to test if exists
     * @return TRUE : exists, FALSE : not exists
     */
    @Override
    public boolean checkIDTaskExists(int id_Task) {
        boolean exists;
        String request = "SELECT count(*) as nb "
                + "FROM APP.T_Tache t "
                + "WHERE t.idTache = " + id_Task + " ";

        try {
            ResultSet donnees = this.doRequest(request, java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);
            donnees.first();
            exists = (donnees.getString("nb") != null && Integer.parseInt(donnees.getString("nb").trim()) >= 1);
        } catch (SQLException e) {
            exists = false;
        }
        return exists;
    }

    /**
     * Checks if the ID exists in the table T_Group
     *
     * @param id_Group The ID to test if exists
     * @return TRUE : exists, FALSE : not exists
     */
    @Override
    public boolean checkIDGroupExists(String id_Group) {
        boolean exists;
        String request = "SELECT count(*) as nb "
                + "FROM APP.T_Groupe g "
                + "WHERE g.idGroupe = '" + id_Group.trim() + "' ";

        try {
            ResultSet donnees = this.doRequest(request, java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);
            donnees.first();
            exists = (donnees.getString("nb") != null && Integer.parseInt(donnees.getString("nb").trim()) >= 1);
        } catch (SQLException e) {
            exists = false;
        }
        return exists;
    }

    /**
     * Deletes a member in the DB and all his relations.
     *
     * @param id_member The ID of the member to delete
     * @return 1 == sucess, != 1 at least one error
     * @throws SQLException
     */
    @Override
    public int deleteMember(String id_member) throws SQLException {
        int ret = 1991;
        if (this.checkIDMemberExists(id_member)) {
            if (this.memberHasAssociatedMessaged(id_member)) {
                ret = (this.deleteAllMemberMessages(id_member) ? 1 : 0);
            }
            if (this.memberHasAssociatedTask(id_member)) {
                ret = (this.deleteAllMemberTasks(id_member) ? 1 : 0);
            }
            String request = "DELETE FROM APP.T_Membre WHERE idMembre = '" + id_member.trim() + "' ";
            ret = ((this.doModif(request, java.sql.ResultSet.TYPE_SCROLL_SENSITIVE, java.sql.ResultSet.CONCUR_UPDATABLE) == 1 && ret == 1) ? 1 : 0);
        } else {
            throw new SQLException("No member is associated with this ID : " + id_member.trim());
        }
        return ret;
    }

    /**
     * Deletes a member from a group.
     *
     * @param id_member The ID of the member to delete
     * @return 1 == sucess, != 1 at least one error
     * @throws SQLException
     */
    @Override
    public int deleteMemberFromGroup(String id_member, String id_Group) throws SQLException {
        int ret = 1991;
        if (this.checkIDMemberExists(id_member)) {
            ResultSet donnees;
            //delete affectations
            String request = "DELETE FROM APP.T_AffectationGroupe WHERE idPersonne  = '" + id_member.trim() + "' AND idGroupe  = '" + id_Group.trim() + "' ";
            ret = ((this.doModif(request, java.sql.ResultSet.TYPE_SCROLL_SENSITIVE, java.sql.ResultSet.CONCUR_UPDATABLE) == 1 && ret == 1) ? 1 : 0);
            //T_EnvoiTacheGroupe  typeDestinataire 
            request = "SELECT idTache as idTache FROM APP.T_EnvoiTacheGroupe WHERE idGroupeDestination = '" + id_Group.trim() + "' ";
            try {
                donnees = this.doRequest(request, java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);
                while (donnees.next()) {
                    //mettre à jour le type de destinataire du membre pour les taches relié au groupe
                    request = "UPDATE APP.T_EnvoiTacheMembre SET typeDestinataire = 'USER' WHERE idPersonneDestination  = '" + id_member.trim() + "' AND idTache   = " + donnees.getString("idTache").trim() + " ";
                    this.doModif(request, java.sql.ResultSet.TYPE_SCROLL_SENSITIVE, java.sql.ResultSet.CONCUR_UPDATABLE);
                }
            } catch (SQLException ex) {
                if (!ex.getSQLState().startsWith("02")) {
                    throw ex;
                }
            }
            try {
                //mettre à jour le type de destinataire du membre pour les messages relié au groupe
                request = "SELECT idMessage as idMessage  FROM APP.T_EnvoiMessageGroupe WHERE idGroupeDestination = '" + id_Group.trim() + "' ";
                donnees = this.doRequest(request, java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);
                while (donnees.next()) {
                    request = "UPDATE APP.T_EnvoiMessageMembre SET typeDestinataire = 'USER' WHERE idPersonneDestination  = '" + id_member.trim() + "' AND idMessage = " + donnees.getString("idMessage").trim() + " ";
                    this.doModif(request, java.sql.ResultSet.TYPE_SCROLL_SENSITIVE, java.sql.ResultSet.CONCUR_UPDATABLE);
                }
            } catch (SQLException ex) {
                if (!ex.getSQLState().startsWith("02")) {
                    throw ex;
                }
            }
        } else {
            throw new SQLException("No member is associated with this ID : " + id_member.trim());
        }
        return ret;
    }

    /**
     * Deletes a group in the DB and all his relations
     *
     * @param id_Group The ID of the group to delete
     * @return 1 == sucess, != 1 at least one error
     * @throws SQLException
     */
    @Override
    public int deleteGroup(String id_Group) throws SQLException {
        int ret = 1;
        if (this.checkIDGroupExists(id_Group)) {
            ArrayList<String> membres = null;
            //suppression des associations avec les membres
            try {
                membres = this.getMembersGroup(id_Group);
            } catch (SQLException e) {
            }
            if (membres != null) {
                String request = "DELETE FROM APP.T_AffectationGroupe "
                        + "WHERE  idGroupe = '" + id_Group.trim() + "' ";
                ret = (this.doModif(request, java.sql.ResultSet.TYPE_SCROLL_SENSITIVE, java.sql.ResultSet.CONCUR_UPDATABLE) == 1 && ret == 1 ? 1 : 0);
            }

            this.deleteGroupMessages(id_Group, membres);
            this.deleteGroupTasks(id_Group, membres);

            String request = "DELETE FROM APP.T_Groupe "
                    + "WHERE  idGroupe = '" + id_Group.trim() + "' ";
            ret = (this.doModif(request, java.sql.ResultSet.TYPE_SCROLL_SENSITIVE, java.sql.ResultSet.CONCUR_UPDATABLE) == 1 && ret == 1 ? 1 : 0);

            return ret;

        } else {
            throw new SQLException("No group is associated with this ID : " + id_Group.trim());
        }
    }

    private int deleteGroupMessages(String id_Group, ArrayList<String> membres) {
        int ret = 1991;
        try {
            //suppression de précaution de l'association des messages - groupe
            if (groupHasAssociatedMessaged(id_Group)) {
                String request = "SELECT emg.idMessage as idMessage "
                        + "FROM APP.T_EnvoiMessageGroupe emg "
                        + "WHERE emg.idGroupeDestination = '" + id_Group.trim() + "' ";
                ResultSet donnees = this.doRequest(request, java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);

                while (donnees.next()) {
                    for (String m : membres) {
                        ret = (this.alterTypeDestinataireMessageMembre(m, Integer.parseInt(donnees.getString("idMessage").trim()), RecipientType.USER) == 1 && ret == 1 ? 1 : 0);
                    }
                }
                request = "DELETE FROM APP.T_EnvoiMessageGroupe "
                        + "WHERE  idGroupeDestination = '" + id_Group.trim() + "' ";
                ret = (this.doModif(request, java.sql.ResultSet.TYPE_SCROLL_SENSITIVE, java.sql.ResultSet.CONCUR_UPDATABLE) == 1 && ret == 1 ? 1 : 0);
            }
        } catch (SQLException ex) {
            Logger.getLogger(InteractDB.class.getName()).log(Level.SEVERE, null, ex);
            ret = -1;
        }
        return ret;
    }

    private int deleteGroupTasks(String id_Group, ArrayList<String> membres) {
        int ret = 1991;
        try {
            //suppression de précaution de l'association des task - groupe
            if (groupHasAssociatedTask(id_Group)) {
                String request = "SELECT etg.idTache as idTache "
                        + "FROM APP.T_EnvoiTacheGroupe etg "
                        + "WHERE etg.idGroupeDestination = '" + id_Group.trim() + "' ";
                ResultSet donnees = this.doRequest(request, java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);
                while (donnees.next()) {
                    for (String m : membres) {
                        ret = (this.alterTypeDestinataireTacheMembre(m, Integer.parseInt(donnees.getString("idTache").trim()), RecipientType.USER) == 1 && ret == 1 ? 1 : 0);
                    }
                }
                request = "DELETE FROM APP.T_EnvoiTacheGroupe "
                        + "WHERE  idGroupeDestination = '" + id_Group.trim() + "' ";
                ret = (this.doModif(request, java.sql.ResultSet.TYPE_SCROLL_SENSITIVE, java.sql.ResultSet.CONCUR_UPDATABLE) == 1 && ret == 1 ? 1 : 0);
            }
        } catch (SQLException ex) {
            Logger.getLogger(InteractDB.class.getName()).log(Level.SEVERE, null, ex);
            ret = -1;
        }
        return ret;
    }

    /**
     * Checks if group has at least one association with a message
     *
     * @param id_Group The ID of the group to test
     * @return TRUE : there is at least one association with a message, FALSE :
     * no association
     * @throws SQLException
     */
    @Override
    public boolean groupHasAssociatedMessaged(String id_Group) throws SQLException {
        //Verify if the member was the last member in the association
        String request = "SELECT count(*) as nbGMess FROM APP.T_EnvoiMessageGroupe "
                + "WHERE idGroupeDestination = '" + id_Group.trim() + "' ";
        ResultSet donnees = this.doRequest(request, java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);
        donnees.first();

        return (donnees.getString("nbGMess") != null && Integer.parseInt(donnees.getString("nbGMess").trim()) >= 1);
    }

    /**
     * Checks if member has at least one association with a message
     *
     * @param id_member The ID of the member to test
     * @return TRUE : there is at least one association with a message, FALSE :
     * no association
     * @throws SQLException
     */
    @Override
    public boolean memberHasAssociatedMessaged(String id_member) throws SQLException {
        //Verify if the member was the last member in the association
        String request = "SELECT count(*) as nbMMess FROM APP.T_EnvoiMessageMembre "
                + "WHERE idPersonneDestination = '" + id_member.trim() + "' ";
        ResultSet donnees = this.doRequest(request, java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);
        donnees.first();

        return (donnees.getString("nbMMess") != null && Integer.parseInt(donnees.getString("nbMMess").trim()) >= 1);
    }

    /**
     * Checks if group has at least one association with a task
     *
     * @param id_Group The ID of the group to test
     * @return TRUE : there is at least one association with a task, FALSE : no
     * association
     * @throws SQLException
     */
    @Override
    public boolean groupHasAssociatedTask(String id_Group) throws SQLException {
        //Verify if the member was the last member in the association
        String request = "SELECT count(*) as nbGTask FROM APP.T_EnvoiTacheGroupe "
                + "WHERE idGroupeDestination = '" + id_Group.trim() + "' ";
        ResultSet donnees = this.doRequest(request, java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);
        donnees.first();

        return (donnees.getString("nbGTask") != null && Integer.parseInt(donnees.getString("nbGTask").trim()) >= 1);
    }

    /**
     * Checks if member has at least one association with a task
     *
     * @param id_member The ID of the member to test
     * @return TRUE : there is at least one association with a task, FALSE : no
     * association
     * @throws SQLException
     */
    @Override
    public boolean memberHasAssociatedTask(String id_member) throws SQLException {
        //Verify if the member was the last member in the association
        String request = "SELECT count(*) as nbMTask FROM APP.T_EnvoiTacheMembre "
                + "WHERE idPersonneDestination = '" + id_member.trim() + "' ";
        ResultSet donnees = this.doRequest(request, java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);
        donnees.first();

        return (donnees.getString("nbMTask") != null && Integer.parseInt(donnees.getString("nbMTask").trim()) >= 1);
    }

    /**
     * Checks if a message is associated with a given group
     *
     * @param idMessage The id of the message
     * @param id_group The id of the group
     * @return TRUE : it is associated, FALSE : not associate
     * @throws SQLException
     */
    @Override
    public boolean messageIsAssociatedWithGroup(int idMessage, String id_group) throws SQLException {
        // T_EnvoiMessageMembre 
        //Verify if the member was the last member in the association
        String request = "SELECT count(*) as nbGMess FROM APP.T_EnvoiMessageGroupe  "
                + "WHERE idGroupeDestination = '" + id_group.trim() + "' "
                + "AND idMessage  = " + idMessage + " ";
        ResultSet donnees = this.doRequest(request, java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);
        donnees.first();

        return (donnees.getString("nbGMess") != null && Integer.parseInt(donnees.getString("nbGMess").trim()) >= 1);
    }

    /**
     * Checks if a message is associated with a given member
     *
     * @param idMessage The id of the message
     * @param id_member The id of the member
     * @return TRUE : it is associated, FALSE : not associate
     * @throws SQLException
     */
    @Override
    public boolean messageIsAssociatedWithMember(int idMessage, String id_member) throws SQLException {
        // T_EnvoiMessageMembre 
        //Verify if the member was the last member in the association
        String request = "SELECT count(*) as nbGMess FROM APP.T_EnvoiMessageMembre   "
                + "WHERE idPersonneDestination = '" + id_member.trim() + "' "
                + "AND idMessage  = " + idMessage + " ";
        ResultSet donnees = this.doRequest(request, java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);
        donnees.first();

        return (donnees.getString("nbGMess") != null && Integer.parseInt(donnees.getString("nbGMess").trim()) >= 1);
    }

    /**
     * Checks if the task has association with a member
     *
     * @param id_task The ID of the task
     * @return TRUE : there is at least one association with a member, FALSE :
     * no association
     * @throws SQLException
     */
    @Override
    public boolean taskHasAssociatedMember(int id_task) throws SQLException {
        //Verify if the member was the last member in the association
        String request = "SELECT count(*) as nbMTask FROM APP.T_EnvoiTacheMembre "
                + "WHERE idTache = " + id_task + " ";
        ResultSet donnees = this.doRequest(request, java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);
        donnees.first();

        return (donnees.getString("nbMTask") != null && Integer.parseInt(donnees.getString("nbMTask").trim()) >= 1);
    }

    /**
     * Checks if the task is association with the given member
     *
     * @param id_task The ID of the task
     * @param id_member The ID of the member
     * @return TRUE : the task is associated with the member, FALSE : not
     * associated
     * @throws SQLException
     */
    @Override
    public boolean taskIsAssociatedWithMember(int id_task, String id_member) throws SQLException {
        if (this.checkIDMemberExists(id_member)) {
            //Verify if the member was the last member in the association
            String request = "SELECT count(*) as nbMTask FROM APP.T_EnvoiTacheMembre "
                    + "WHERE idTache = " + id_task + " "
                    + "AND idPersonneDestination = '" + id_member.trim() + "' ";
            ResultSet donnees = this.doRequest(request, java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);
            donnees.first();

            return (donnees.getString("nbMTask") != null && Integer.parseInt(donnees.getString("nbMTask").trim()) >= 1);
        } else {
            throw new SQLException("The member or the task do not exists ! : task : " + id_task + " -- member : " + id_member.trim());
        }
    }

    /**
     * Gets the tasks of a member
     *
     * @param id_member The ID of the member
     * @return The list of the tasks of the member separated by a comma
     * @throws SQLException
     */
    @Override
    public String getTasksMember(String id_member) throws SQLException {
        if (this.memberHasAssociatedTask(id_member)) {
            //Verify if the member was the last member in the association
            String tasks = "";
            String request = "SELECT t.titre as titre, t.idTache as idTache FROM APP.T_EnvoiTacheMembre etm, APP.T_Tache t "
                    + "WHERE etm.idPersonneDestination = '" + id_member.trim() + "' "
                    + "AND t.idTache = etm.idTache";
            ResultSet donnees = this.doRequest(request, java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);
            while (donnees.next()) {
                tasks += donnees.getString("idTache").trim() + ", ";
            }
            return tasks;

        } else {
            return "No tasks.";
        }
    }

    /**
     * Checks if the task is association with the given group
     *
     * @param id_task The ID of the task
     * @param id_Group The ID of the group
     * @return TRUE : the task is associated with the group, FALSE : not
     * associated
     * @throws SQLException
     */
    @Override
    public boolean taskIsAssociatedWithGroup(int id_task, String id_Group) throws SQLException {
        if (this.checkIDGroupExists(id_Group)) {
            //Verify if the member was the last member in the association
            String request = "SELECT count(*) as nbMTask FROM APP.T_EnvoiTacheGroupe "
                    + "WHERE idTache = " + id_task + " "
                    + "AND idGroupeDestination = '" + id_Group.trim() + "' ";
            ResultSet donnees = this.doRequest(request, java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);
            donnees.first();

            return (donnees.getString("nbMTask") != null && Integer.parseInt(donnees.getString("nbMTask").trim()) >= 1);
        } else {
            throw new SQLException("The group or the task do not exists ! : task : " + id_task + " -- group : " + id_Group.trim());
        }
    }

    /**
     * Checks if the task has group association
     *
     * @param id_task The ID of the task
     * @return TRUE : there is at least one association with a group, FALSE : no
     * association
     * @throws SQLException
     */
    @Override
    public boolean taskHasAssociatedGroup(int id_task) throws SQLException {
        //Verify if the member was the last member in the association
        String request = "SELECT count(*) as nbMTask FROM APP.T_EnvoiTacheGroupe "
                + "WHERE idTache = " + id_task + " ";
        ResultSet donnees = this.doRequest(request, java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);
        donnees.first();

        return (donnees.getString("nbMTask") != null && Integer.parseInt(donnees.getString("nbMTask").trim()) >= 1);
    }

    /**
     * Checks if the task has attachement
     *
     * @param id_task The ID of the task
     * @return TRUE : there is at least one attachment, FALSE : no attachment
     * @throws SQLException
     */
    @Override
    public boolean taskHasAttachement(int id_task) throws SQLException {
        //Verify if the member was the last member in the association
        String request = "SELECT count(*) as nbMTask FROM APP.T_FichierJointTache "
                + "WHERE idTache = " + id_task + " ";
        ResultSet donnees = this.doRequest(request, java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);
        donnees.first();

        return (donnees.getString("nbMTask") != null && Integer.parseInt(donnees.getString("nbMTask").trim()) >= 1);
    }

    /**
     * Alter the type of the recipient of a message
     *
     * @param id_member The ID of the member to change
     * @param id_message The ID of the message to change
     * @param typeDest The new type of recipient
     * @return 1 == succes, != 1 error
     * @throws SQLException
     */
    private int alterTypeDestinataireMessageMembre(String id_member, int id_message, RecipientType typeDest) throws SQLException {
        String request = "UPDATE APP.T_EnvoiMessageMembre "
                + " SET typeDestinataire = '" + typeDest + "' "
                + "WHERE idPersonneDestination = '" + id_member.trim() + "' "
                + "AND idMessage = " + id_message + " ";

        return (this.doModif(request, java.sql.ResultSet.TYPE_SCROLL_SENSITIVE, java.sql.ResultSet.CONCUR_UPDATABLE));
    }

    /**
     * Alter the type of the recipient of a task
     *
     * @param id_member The ID of the member to change
     * @param id_task The ID of the message to change
     * @param typeDest The new type of recipient
     * @return 1 == succes, != 1 error
     * @throws SQLException
     */
    private int alterTypeDestinataireTacheMembre(String id_member, int id_task, RecipientType typeDest) throws SQLException {
        String request = "UPDATE APP.T_EnvoiTacheMembre "
                + " SET typeDestinataire = '" + typeDest + "' "
                + "WHERE idPersonneDestination = '" + id_member.trim() + "' "
                + "AND idTache = " + id_task + " ";

        return (this.doModif(request, java.sql.ResultSet.TYPE_SCROLL_SENSITIVE, java.sql.ResultSet.CONCUR_UPDATABLE));
    }

    /**
     * Checks if the member is admin
     *
     * @param id_member The ID of the member
     * @return TRUE : is admin, FALSE : is not admin
     * @throws SQLException
     */
    @Override
    public boolean isAdmin(String id_member) throws SQLException {
        String request = "SELECT count(idMembre) as nbMembre FROM APP.T_Membre WHERE isAdmin = 'Y' AND idMembre = '" + id_member.trim() + "' ";
        ResultSet donnees = this.doRequest(request, java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);

        donnees.first();
        return ((donnees.getString("nbMembre") != null) && Integer.parseInt(donnees.getString("nbMembre").trim()) > 0);
    }

    /**
     * Checks if the member is chief of the task DO NOT USE
     *
     * @param id_member The ID of the member
     * @param id_task The ID of the task
     * @return TRUE : is chief, FALSE : is not chief
     * @throws SQLException
     */
    @Override
    public boolean isChiefTask(String id_member, int id_task) throws SQLException {

        String request = "SELECT count(etm.idPersonneDestination) as idPersonne FROM T_EnvoiTacheMembre etm WHERE etm.isChefProjet = 'Y' AND etm.idTache = " + id_task + " AND etm.idPersonneDestination = '" + id_member.trim() + "' ";
        ResultSet donnees = this.doRequest(request, java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);

        donnees.first();
        return ((donnees.getString("idPersonne") != null) && Integer.parseInt(donnees.getString("idPersonne").trim()) > 0);
    }

    /**
     * Updates a member with the new informations
     *
     * @param m The member with the new informations
     * @return 1 : ok, != 1 error
     */
    @Override
    public int updateMember(Member m) {
        int ret = 1991;
        if (this.checkIDMemberExists(m.getId_member())) {
            String req = "UPDATE APP.T_Membre SET nom = '" + m.getName().trim() + "', prenom = '" + m.getFirst_name().trim() + "', email = '" + m.getEmail().trim() + "' WHERE idMembre = '" + m.getId_member().trim() + "'";
            try {
                ret = this.doModif(req, java.sql.ResultSet.TYPE_SCROLL_SENSITIVE, java.sql.ResultSet.CONCUR_UPDATABLE);
            } catch (SQLException ex) {
                Logger.getLogger(InteractDB.class.getName()).log(Level.SEVERE, null, ex);
                ret = -1;
            }
        }


        return ret;
    }

    /**
     * Updates the group with the new informations
     *
     * @param g The group containing the new informations
     * @param newRecipients The new recipients : <ID of the recipient, (TRUE :
     * add the recipient, FALSE : delete the recpient)>
     * @param idChef The new chief
     * @return 1 : ok, != 1 error
     */
    @Override
    public int updateGroup(Group g, HashMap<String, Boolean> newRecipients, String idChef) {
        int ret = 1991;
        String idGroup = g.getId_group().trim();

        if (this.checkIDGroupExists(g.getId_group())) {

            String req = "UPDATE APP.T_Groupe SET nom = '" + g.getGroup_name().trim() + "', description = '" + g.getDescr().trim() + "' WHERE idGroupe ='" + idGroup + "'";
            try {
                ret = this.doModif(req, java.sql.ResultSet.TYPE_SCROLL_SENSITIVE, java.sql.ResultSet.CONCUR_UPDATABLE);
            } catch (SQLException ex) {
                Logger.getLogger(InteractDB.class.getName()).log(Level.SEVERE, null, ex);
                ret = -1;
            }
            if (idChef != null) {
                try {
                    if (!this.groupIsAssociatedWithMember(idGroup, idChef)) {
                        this.addAffectionGroup(idChef, idGroup, true);
                    } else {
                        req = "UPDATE APP.T_AffectationGroupe SET isChefProjet = 'Y' WHERE idGroupe ='" + idGroup + "' AND idPersonne ='" + idChef + "' ";

                        try {
                            ret = this.doModif(req, java.sql.ResultSet.TYPE_SCROLL_SENSITIVE, java.sql.ResultSet.CONCUR_UPDATABLE);
                        } catch (SQLException ex) {
                            Logger.getLogger(InteractDB.class.getName()).log(Level.SEVERE, null, ex);
                            ret = -1;
                        }
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(InteractDB.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
            this.updateGroupRecipients(newRecipients, idChef, idGroup);

        }

        return ret;
    }

    /**
     * Updates the recipients of a group
     *
     * @param newRecipients The new recipients : <ID of the recipient, (TRUE :
     * add the recipient, FALSE : delete the recpient)>
     * @param idChef The new chief
     * @param idGroup The ID of the group to update the recipients
     * @return
     */
    private int updateGroupRecipients(HashMap<String, Boolean> newRecipients, String idChef, String idGroup) {

        int ret = 1991;
        if (newRecipients != null && !newRecipients.isEmpty()) {
            for (Entry<String, Boolean> entry : newRecipients.entrySet()) {
                String cle = entry.getKey();
                Boolean valeur = entry.getValue();
                Boolean chef = false;
                if (valeur) {
                    if (cle.equals(idChef)) {
                        chef = true;
                    }
                    try {
                        this.addAffectionGroup(cle, idGroup, chef);
                    } catch (SQLException ex) {
                        Logger.getLogger(InteractDB.class.getName()).log(Level.SEVERE, null, ex);
                        ret = -1;
                    }
                } else {
                    try {
                        this.deleteMemberFromGroup(cle, idGroup);
                    } catch (SQLException ex) {
                        Logger.getLogger(InteractDB.class.getName()).log(Level.SEVERE, null, ex);
                        ret = -1;
                    }
                }
            }
        }
        return ret;
    }

    /**
     * Checks if the group is associated with the given member
     *
     * @param idGroup The ID of the group
     * @param id_member The ID of the given member
     * @return TRUE : yes, FALSE : no
     * @throws SQLException
     */
    @Override
    public boolean groupIsAssociatedWithMember(String idGroup, String id_member) throws SQLException {
        // T_EnvoiMessageMembre 
        //Verify if the member was the last member in the association
        String request = "SELECT count(*) as nbGMemb FROM APP.T_AffectationGroupe   "
                + "WHERE idPersonne = '" + id_member.trim() + "' "
                + "AND idGroupe  = '" + idGroup.trim() + "' ";
        ResultSet donnees = this.doRequest(request, java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);
        donnees.first();

        return (donnees.getString("nbGMemb") != null && Integer.parseInt(donnees.getString("nbGMemb").trim()) >= 1);
    }

    /**
     * Checks if the member is associated with at least one group
     *
     * @param id_member The ID of the member
     * @return TRUE : yes, FALSE : no
     * @throws SQLException
     */
    @Override
    public boolean memberHasAssociatedGroup(String id_member) throws SQLException {
        // T_EnvoiMessageMembre 
        //Verify if the member was the last member in the association
        String request = "SELECT count(*) as nbGMemb FROM APP.T_AffectationGroupe   "
                + "WHERE idPersonne = '" + id_member.trim() + "' ";
        ResultSet donnees = this.doRequest(request, java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);
        donnees.first();

        return (donnees.getString("nbGMemb") != null && Integer.parseInt(donnees.getString("nbGMemb").trim()) >= 1);
    }

    /**
     * Gets the groups in which the member is in
     *
     * @param id_member The ID of the member
     * @return The list of the groups of the member separated by a comma
     * @throws SQLException
     */
    @Override
    public String getMemberGroups(String id_member) throws SQLException {
        if (this.memberHasAssociatedGroup(id_member)) {
            String groups = "";
            //Verify if the member was the last member in the association
            String request = "SELECT g.idGroupe as idGroupe, g.nom as nom FROM APP.T_AffectationGroupe ag, APP.T_Groupe g  "
                    + "WHERE ag.idPersonne = '" + id_member.trim() + "' "
                    + "AND g.idGroupe = ag.idGroupe";

            ResultSet donnees = this.doRequest(request, java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);
            while (donnees.next()) {
                groups += donnees.getString("idGroupe") + ", ";
            }

            return (groups);
        } else {
            return "No groups.";
        }
    }

    /**
     * Gets the number of messages for a status for a member
     *
     * @param id_membre The ID of the member who has the status
     * @param mst The message status, if null, search a non read message
     * @return The number of messages with the mst status
     */
    @Override
    public int getNbMessagesForStatus(String id_membre, MessageStatus mst) {
        int nbMess = 0;
        String req;
        if (mst != null) {
            req = "SELECT count(*) as nbMst FROM APP.T_StatusMessageMembre WHERE idPersonneDestination ='" + id_membre + "' AND statut ='" + mst.toString() + "'";
            try {
                ResultSet donnees = this.doRequest(req, java.sql.ResultSet.TYPE_SCROLL_SENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);
                donnees.first();
                nbMess = donnees.getInt("nbMst");
            } catch (SQLException ex) {
                Logger.getLogger(InteractDB.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            req = "SELECT count(*) as nbMst FROM  APP.T_EnvoiMessageMembre emm WHERE emm.idPersonneDestination ='" + id_membre + "' AND  NOT EXISTS (Select * FROM APP.T_StatusMessageMembre smm WHERE smm.IDPERSONNEDESTINATION='" + id_membre + "' AND emm.IDMESSAGE = smm.IDMESSAGE)";
            try {
                ResultSet donnees = this.doRequest(req, java.sql.ResultSet.TYPE_SCROLL_SENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);
                donnees.first();
                nbMess = donnees.getInt("nbMst");
            } catch (SQLException ex) {
                Logger.getLogger(InteractDB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return nbMess;
    }

    /**
     * Updates the password of a member
     *
     * @param idMember The ID of the member to change the password
     * @param hashMdp The new password as a hash
     * @return
     */
    @Override
    public int updateMemberPswd(String idMember, String hashMdp) {
        int ret = 1991;
        if (this.checkIDMemberExists(idMember.trim())) {
            String req = "UPDATE APP.T_Membre SET motDePasse = '" + hashMdp + "' WHERE idMembre = '" + idMember.trim() + "'";
            try {
                ret = this.doModif(req, java.sql.ResultSet.TYPE_SCROLL_SENSITIVE, java.sql.ResultSet.CONCUR_UPDATABLE);
            } catch (SQLException ex) {
                Logger.getLogger(InteractDB.class.getName()).log(Level.SEVERE, null, ex);
                ret = -1;
            }
        }
        return ret;
    }

    /**
     * Search an expression in a table
     *
     * @param table The table in which perform the search
     * @param colName The column in which perform the search
     * @param expr The expression to search
     * @param id The ID of the table (name of the column)
     * @return A list of string in the format : colNameToSearch (id)
     */
    @Override
    public ArrayList<String> searchExprInTable(String table, String colName, String expr, String id) {
        try {
            ArrayList<String> result = new ArrayList<String>();
            String req = "SELECT DISTINCT " + colName + " as " + colName + ", " + id + " as " + id + " FROM APP." + table + " WHERE upper(" + colName + ") LIKE upper('" + expr + "%')";
            ResultSet donnees = this.doRequest(req, java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);

            while (donnees.next()) {
                result.add(donnees.getString(colName) + " (" + donnees.getString(id) + ")");
            }
            return result;
        } catch (SQLException ex) {
            Logger.getLogger(InteractDB.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * Search an expression in a table
     *
     * @param table The table in which perform the search
     * @param colNameToSearch The column in which perform the search
     * @param colNameDetail Column for additional informations
     * @param expr The expression to search
     * @param id The ID of the table (name of the column)
     * @return A list of string in the format : colNameToSearch colNameDetail
     * (id)
     */
    @Override
    public ArrayList<String> searchExprInTable(String table, String colNameToSearch, String colNameDetail, String expr, String id) {
        try {
            ArrayList<String> result = new ArrayList<String>();
            String req = "SELECT DISTINCT " + colNameToSearch + " as " + colNameToSearch + ", " + id + " as " + id + ", " + colNameDetail + " as " + colNameDetail + " FROM APP." + table + " WHERE upper(" + colNameToSearch + ") LIKE upper('" + expr + "%')";
            ResultSet donnees = this.doRequest(req, java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);

            while (donnees.next()) {
                result.add(donnees.getString(colNameToSearch) + " " + donnees.getString(colNameDetail) + " (" + donnees.getString(id) + ")");
            }
            return result;
        } catch (SQLException ex) {
            Logger.getLogger(InteractDB.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
