package peopleObjects;

public class Member{
	private String id_member;		//The ID of the member
	private String name;		//The name of the member
	private String first_name;	//The first name of the member
	private String email;		//The e-mail of the member
	
	/**
	 * Constructor.
	 * @param id_member The ID of the member.
	 * @param name The name of the member.
	 * @param first_name The first name of the member.
	 * @param email The e-mail of the member.
	 */
	public Member(String id_member, String name, String first_name, String email) {
		this.id_member = id_member;
		this.name = name;
		this.first_name = first_name;
		this.email = email;
	}

        /**
         * Sets the id of the member.
         * @param id id to set.
         */
        public void setId_member(String id) {
            this.id_member = id;
        }
	
	/**
	 * Gives the ID of the member.
	 * @return The ID of the member.
	 */
	public String getId_member() {
		return id_member;
	}
	
	/**
	 * Gives the name of the member.
	 * @return The name of the member.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the name of the member.
	 * @param name The name of the member.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Gives the first name of the member.
	 * @return The first name of the member.
	 */
	public String getFirst_name() {
		return first_name;
	}
	
	/**
	 * Sets the first name of the member.
	 * @param first_name The first name of the member.
	 */
	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}
	
	/**
	 * Gives the e-mail of the member.
	 * @return The e-mail of the member.
	 */
	public String getEmail() {
		return email;
	}
	
	/**
	 * Sets the e-mail of the member.
	 * @param email The e-mail of the member.
	 */
	public void setEmail(String email) {
		this.email = email;
	}
}
