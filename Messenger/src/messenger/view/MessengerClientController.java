package messenger.view;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import messenger.MainApp;
import messenger.model.LoginMember;
import messenger.util.SenderThread;

public class MessengerClientController implements Initializable {

	@FXML
	private TextArea chatEnterArea;
	@FXML
	private TextArea chatDisplayArea;
	@FXML
	private Button chatEnterBtn;
	@FXML
	private TableView<LoginMember> participantTable;
	@FXML
	private TableColumn<LoginMember, String> participantID;
	@FXML
	private TableColumn<LoginMember, String> participantStatus;
	@FXML
	private MenuItem returnMenu;
	@FXML
	private MenuItem exitMenu;
	@FXML
	private MenuItem aboutMenu;

	// 디버깅용 log
	private Logger log = Logger.getLogger(this.getClass());
	Socket socket;
	// 메인 앱 참조
	private MainApp mainApp;

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		log.info("대화방 창");
		// 테이블 초기화
		// participantTable.refresh();
		participantID.setCellValueFactory(cellData -> cellData.getValue().getIDProperty());
		participantStatus.setCellValueFactory(cellData -> cellData.getValue().getStatusProperty());
		// TextArea 자동 줄바꿈
		chatDisplayArea.setWrapText(true);
		chatEnterArea.setWrapText(true);
		// 버튼, 메뉴 등 액션 초기화
		returnMenu.setOnAction(e -> returnMenuAction(e));
		exitMenu.setOnAction(e -> exitMenuAction(e));
		aboutMenu.setOnAction(e -> aboutMenuAction(e));
		chatEnterBtn.setOnAction(e -> chatEnterBtnAction(e));
		chatEnterArea.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					sendFunction();
					event.consume(); // 기존 엔터키 이벤트 덮어쓰기
				}

			}
		});

	}

	public void returnMenuAction(ActionEvent event) {
		returnToBrowser();
	}

	public void exitMenuAction(ActionEvent event) {
		Platform.exit();
	}

	public void aboutMenuAction(ActionEvent event) {
		mainApp.alertMessage("About Messenger Application",
				"Developed by Da Eun Lee from Korea.\n\nSubmitted to Kosea mini project.\n\n2018 July.");
	}

	public void chatEnterBtnAction(ActionEvent event) {
		sendFunction();
	}

	// define send function
	public void sendFunction() {
		if (chatEnterArea.getText() != null) {
			try {
				InputStream input = new ByteArrayInputStream(chatEnterArea.getText().getBytes("UTF-8"));
				Thread thread1 = new SenderThread(socket, input);
				thread1.setDaemon(true);
				thread1.start();
				chatEnterArea.clear();

			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
		}
	}

	public void returnToBrowser() {
		// 방장이 방에서 나갈 시 방 삭제
		if (mainApp.getQueryLogin().selectStatus(mainApp.getID()).equals("RoomMaster"))
			mainApp.getQueryChatroom().deleteChatRoom(mainApp.getID());
		// 대기 상태로 전환
		mainApp.getQueryLogin().updateRoomNum(mainApp.getID(), 0);
		mainApp.getQueryLogin().updateStatus(mainApp.getID(), "WaitingRoom");
		// 참여자 업뎃 스레드 종료
		mainApp.getParticipantUpdateThread().interrupt();
		// 클라이언트 제거
		mainApp.closeClient();
		// 서버 오픈시 종료
		mainApp.closeServer();

		// 룸 브라우저로 복귀
		mainApp.showchatRoomBrowser();
	}

	// 소켓 참조
	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public TextArea getChatDisplayArea() {
		return chatDisplayArea;
	}

	public TextArea getChatEnterArea() {
		return chatEnterArea;
	}

	public TableView<LoginMember> getParticipantTable() {
		return participantTable;
	}

	public MessengerClientController getMessengerClientController() {
		return this;
	}

}
