package messenger.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class LoginMember {
	private final StringProperty id;
	private final IntegerProperty roomNum;
	private final StringProperty status;

	public LoginMember() {
		this(null, 0, null);
	}

	public LoginMember(String id, int roomNum, String status) {
		super();
		this.id = new SimpleStringProperty(id);
		this.roomNum = new SimpleIntegerProperty(roomNum);
		this.status = new SimpleStringProperty(status);
	}

	public String getId() {
		return id.get();
	}

	public void setId(String id) {
		this.id.set(id);
	}

	public StringProperty getIDProperty() {
		return id;
	}

	public Integer getRoomNum() {
		return roomNum.get();
	}

	public void setRoomNum(int roomNum) {
		this.roomNum.set(roomNum);
	}

	public IntegerProperty getRoomNumProperty() {
		return roomNum;
	}

	public String getStatus() {
		return status.get();
	}

	public void setStatus(String status) {
		this.status.set(status);
	}

	public StringProperty getStatusProperty() {
		return status;
	}

}
