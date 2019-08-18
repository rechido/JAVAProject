package messenger.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ChatRoom {

	private final IntegerProperty roomNum;
	private final StringProperty roomMasterId;
	private final StringProperty roomTitle;
	private final IntegerProperty participantCount;
	private final StringProperty serverIP;
	private final IntegerProperty serverLocalPort;

	public ChatRoom() {
		this(0, null, null, 0, null, 0);
	}

	public ChatRoom(int roomNum, String roomMasterId, String roomTitle, int participantCount, String serverIP,
			int serverLocalPort) {
		super();
		this.roomNum = new SimpleIntegerProperty(roomNum);
		this.roomMasterId = new SimpleStringProperty(roomMasterId);
		this.roomTitle = new SimpleStringProperty(roomTitle);
		this.participantCount = new SimpleIntegerProperty(participantCount);
		this.serverIP = new SimpleStringProperty(serverIP);
		this.serverLocalPort = new SimpleIntegerProperty(serverLocalPort);
	}

	public int getRoomNum() {
		return roomNum.get();
	}

	public IntegerProperty getRoomNumProperty() {
		return roomNum;
	}

	public void setRoomNum(int roomNum) {
		this.roomNum.set(roomNum);
	}

	public String getRoomMasterId() {
		return roomMasterId.get();
	}

	public StringProperty getRoomMasterIdProperty() {
		return roomMasterId;
	}

	public void setRoomMasterId(String roomMasterId) {
		this.roomMasterId.set(roomMasterId);
	}

	public String getRoomTitle() {
		return roomTitle.get();
	}

	public StringProperty getRoomTitleProperty() {
		return roomTitle;
	}

	public void setRoomTitle(String roomTitle) {
		this.roomTitle.set(roomTitle);
	}

	public int getParticipantCount() {
		return participantCount.get();
	}

	public IntegerProperty getParticipantCountProperty() {
		return participantCount;
	}

	public void setParticipantCount(int participantCount) {
		this.participantCount.set(participantCount);
	}

	public StringProperty getServerIPProperty() {
		return serverIP;
	}

	public String getServerIP() {
		return serverIP.get();
	}

	public void setServerIP(String serverIP) {
		this.serverIP.set(serverIP);
	}

	public IntegerProperty getServerLocalPortProperty() {
		return serverLocalPort;
	}

	public int getServerLocalPort() {
		return serverLocalPort.get();
	}

	public void setServerLocalPort(int serverLocalPort) {
		this.serverLocalPort.set(serverLocalPort);
	}

}
