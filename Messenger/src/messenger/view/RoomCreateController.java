package messenger.view;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import messenger.MainApp;

public class RoomCreateController implements Initializable {

	@FXML
	private Button createBtn;
	@FXML
	private Button cancelBtn;
	@FXML
	private TextField roomTitleField;

	// 디버깅용 log
	private Logger log = Logger.getLogger(this.getClass());
	// 메인 앱 참조
	private MainApp mainApp;

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		log.info("방 생성 창");
		createBtn.setOnAction(e -> createBtnAction(e));
		cancelBtn.setOnAction(e -> cancelBtnAction(e));
		roomTitleField.setOnAction(e -> roomTitleFieldAction(e));

	}

	public void createBtnAction(ActionEvent event) {
		// 방제 빈칸 체크
		if (roomTitleField.getText().isEmpty()) {
			mainApp.alertMessage("Fail to Make a New Room", "Please enter proper room title.");
			return;
		}
		// 방제 길이 제한 체크
		if (roomTitleField.getText().length() > 100) {
			mainApp.alertMessage("Fail to Make a New Room", "Room title must be shorter than 100 characters.");
			return;
		}
		// 방 오픈 성공
		else {

			// 서버 오픈
			mainApp.openServer();
			// server thread가 ip주소와 port번호를 반환하기까지 대기하고 1초 내로 반환하지 않을시 방 개설 실패 선언
			int cnt = 0;
			while (true) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				if (mainApp.getIpAddress() != null && mainApp.getLocalPort() != 0)
					break;
				else if (cnt > 10) {
					log.info("서버 주소 미반환");
					mainApp.alertMessage("Fail to Make a New Room", "서버 주소를 받아오지 못했습니다.");
					return;
				}
				log.info("서버 주소 반환 대기중");
				cnt++;
			}
			int roomCount = mainApp.getQueryChatroom().countRoomNumber();
			String ipAddress = mainApp.getIpAddress();
			int localPort = mainApp.getLocalPort();
			// log.info("방개수: " + roomCount);
			// log.info("아이피여기까지와라 " + mainApp.getIpAddress());
			// log.info("로컬포트여기까지와라 " + mainApp.getLocalPort());
			mainApp.getQueryChatroom().insertChatRoom(roomCount + 1, mainApp.getID(), roomTitleField.getText(),
					ipAddress, localPort);
			mainApp.getQueryLogin().updateRoomNum(mainApp.getID(), roomCount + 1);
			mainApp.getQueryLogin().updateStatus(mainApp.getID(), "RoomMaster");
			mainApp.alertMessage("Success", "New room has been opened.");
			// 방 오픈 성공했으면 대화방 창으로 이동
			mainApp.showMessengerClient(roomTitleField.getText());

			log.info("방 개설 성공");
		}

	}

	public void cancelBtnAction(ActionEvent event) {
		mainApp.showchatRoomBrowser();

	}

	public void roomTitleFieldAction(ActionEvent event) {
		createBtnAction(event);
	}

}
