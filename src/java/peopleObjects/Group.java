package peopleObjects;

import java.util.ArrayList;

public class Group extends GroupHeader {

    private ArrayList<Member> members;	//Members in the group
    private Member chief;                   //Chief of the group

    /**
     * Constructor of the group from the given list of members.
     *
     * @param id_group The ID on the group.
     * @param group_name The name of the group.
     * @param members The members in the group.
     * @param chief Chief of the group.
     */
    public Group(String id_group, String group_name, ArrayList<Member> members, Member chief) {
        super(id_group, group_name, "");
        this.members = members;
        this.chief = chief;
    }

    /**
     * Constructor of the group from the given list of members.
     *
     * @param id_group The ID on the group.
     * @param group_name The name of the group.
     * @param members The members in the group.
     * @param chief Chief of the group.
     */
    public Group(String id_group, String group_name, ArrayList<Member> members, Member chief, String descr) {
        super(id_group, group_name, descr);
        this.members = members;
        this.chief = chief;
    }

    /**
     * Constructor of the group with an empty list of members.
     *
     * @param id_group The ID on the group.
     * @param group_name The name of the group.
     * @param chief Chief of the group.
     */
    public Group(String id_group, String group_name, Member chief) {
        super(id_group, group_name, "");
        this.setMembers(new ArrayList<Member>());
        this.chief = chief;
    }

    /**
     * Gives all the members in the group.
     *
     * @return The group's members.
     */
    public ArrayList<Member> getMembers() {
        return members;
    }

    /**
     * Sets all the member of the group.
     *
     * @param members The members in the group.
     */
    public void setMembers(ArrayList<Member> members) {
        this.members = members;
    }

    /**
     * Adds a member in the group.
     *
     * @param member The member to add.
     */
    public void addMember(Member member) {
        if (this.members.contains(member)) {
            this.members.add(member);
        }
    }

    /**
     * Removes a member from the group.
     *
     * @param member member to remove.
     */
    public void removeMember(Member member) {
        this.members.remove(member);
    }

    /**
     * Gives the chief of the group.
     *
     * @return the chief
     */
    public Member getChief() {
        return chief;
    }

    /**
     * Sets the chief of the group.
     *
     * @param chief the chief to set
     */
    public void setChief(Member chief) {
        this.chief = chief;
    }

    @Override
    public String getDescr() {
        return super.getDescr();
    }

    @Override
    public void setDescr(String descr) {
        super.setDescr(descr);
    }
}
