package messenger.model;

// DAO (Data Access Object)
public class Member {
	private String id;
	private String password;

	public Member() {
		// 기본생성자
	}

	public Member(String id, String password) {
		super();
		this.id = id;
		this.password = password;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
