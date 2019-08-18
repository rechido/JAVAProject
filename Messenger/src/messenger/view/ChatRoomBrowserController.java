package messenger.view;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import messenger.MainApp;
import messenger.model.ChatRoom;

public class ChatRoomBrowserController implements Initializable {

	@FXML
	private Button getInBtn;
	@FXML
	private Button createBtn;
	@FXML
	private Button refreshBtn;
	@FXML
	private TableView<ChatRoom> chatRoomTable;
	@FXML
	private TableColumn<ChatRoom, String> chatRoomCreatorName;
	@FXML
	private TableColumn<ChatRoom, String> chatRoomTitle;
	@FXML
	private MenuItem logOutMenu;
	@FXML
	private MenuItem exitMenu;
	@FXML
	private MenuItem aboutMenu;

	// 디버깅용 log
	private Logger log = Logger.getLogger(this.getClass());
	// 채팅방 리스트 보관 데이터
	private ObservableList<ChatRoom> chatRoomListData = FXCollections.observableArrayList();
	// 메인 앱 참조
	private MainApp mainApp;

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// 테이블 초기화
		chatRoomCreatorName.setCellValueFactory(cellData -> cellData.getValue().getRoomMasterIdProperty());
		chatRoomTitle.setCellValueFactory(cellData -> cellData.getValue().getRoomTitleProperty());
		// 버튼 초기화
		getInBtn.setOnAction(e -> getInBtnAction(e));
		createBtn.setOnAction(e -> createBtnAction(e));
		refreshBtn.setOnAction(e -> refreshBtnAction(e));
		// 메뉴아이템 초기화
		logOutMenu.setOnAction(e -> logOutMenuAction(e));
		exitMenu.setOnAction(e -> exitMenuAction(e));
		aboutMenu.setOnAction(e -> aboutMenuAction(e));

	}

	public void logOutMenuAction(ActionEvent event) {
		// 로그인 기록을 삭제
		if (mainApp.getQueryLogin().selectStatus(mainApp.getID()).equals("RoomMaster"))
			mainApp.getQueryChatroom().deleteChatRoom(mainApp.getID());
		mainApp.getQueryLogin().delete(mainApp.getID());
		mainApp.setID(null);
		mainApp.showLogin();
	}

	public void exitMenuAction(ActionEvent event) {
		Platform.exit();
	}

	public void aboutMenuAction(ActionEvent event) {
		mainApp.alertMessage("About Messenger Application",
				"Developed by Da Eun Lee from Korea.\n\nSubmitted to Kosea mini project.\n\n2018 July.");
	}

	public void getInBtnAction(ActionEvent event) {
		log.info("룸브라우저 창");
		ChatRoom chatroom;
		try {
			chatroom = chatRoomTable.getSelectionModel().getSelectedItem(); // 리스트에서 선택한 방 받아옴
			chatroom = mainApp.getQueryChatroom().selectOne(chatroom.getRoomNum());
		} catch (NullPointerException e) {
			mainApp.alertMessage("Fail to get in the room", "Please select a proper room."); // 방 선택 안된 경우 처리
			return;
		}
		try {
			mainApp.getQueryLogin().updateRoomNum(mainApp.getID(), chatroom.getRoomNum());
			mainApp.getQueryLogin().updateStatus(mainApp.getID(), "Participant");
			mainApp.setIpAddress(chatroom.getServerIP());
			mainApp.setLocalPort(chatroom.getServerLocalPort());
			mainApp.showMessengerClient(chatroom.getRoomTitle());
			log.info("방 입장 성공.");
		} catch (NullPointerException e) {
			mainApp.alertMessage("Fail to get in the room", "No such room exists.");
		}

	}

	public void createBtnAction(ActionEvent event) {
		mainApp.showRoomCreate();
	}

	public void refreshBtnAction(ActionEvent event) {
		refresh();
	}

	public void refresh() {
		chatRoomListData.clear();
		List<ChatRoom> chatRoomList = mainApp.getQueryChatroom().selectAll();
		for (int cnt = 0; cnt < chatRoomList.size(); cnt++)
			chatRoomListData.add(chatRoomList.get(cnt));
		chatRoomTable.setItems(chatRoomListData);
	}

}
