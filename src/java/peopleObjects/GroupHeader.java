package peopleObjects;

public class GroupHeader {

    private String id_group;		//ID of the group
    private String group_name;	//Name of the group
    private String descr;

    /**
     * Constructor.
     *
     * @param id_group The ID of the group.
     * @param group_name The name of the group.
     */
    public GroupHeader(String id_group, String group_name, String descr) {
        this.id_group = id_group;
        this.group_name = group_name;
        this.descr = descr;

    }

    /**
     * Sets the id of the group.
     *
     * @param id ID to set.
     */
    public void setIdGroup(String id) {
        this.id_group = id;
    }

    /**
     * Gives the ID of the group.
     *
     * @return the ID of the group.
     */
    public String getId_group() {
        return id_group;
    }

    /**
     * Gives the name of the group.
     *
     * @return The group's name.
     */
    public String getGroup_name() {
        return group_name;
    }

    /**
     * Sets the name of the group.
     *
     * @param group_name The new group's name.
     */
    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }
}
