package messenger.view;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import messenger.MainApp;

public class LoginController implements Initializable {

	@FXML
	private TextField idField;
	@FXML
	private PasswordField pswdField;
	@FXML
	private Button loginBtn;
	@FXML
	private Button cancelBtn;
	@FXML
	private Button signInBtn;
	@FXML
	private Button signOutBtn;

	// 디버깅용 log
	private Logger log = Logger.getLogger(this.getClass());
	// 메인 앱 참조
	private MainApp mainApp;

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		log.info("로그인 창");
		loginBtn.setOnAction(e -> loginBtnAction(e));
		cancelBtn.setOnAction(e -> cancelBtnAction(e));
		signInBtn.setOnAction(e -> signInBtnAction(e));
		signOutBtn.setOnAction(e -> signOutBtnAction(e));

		// 엔터키로도 로그인 가능하게 하기
		idField.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					login();
					event.consume(); // 기존 엔터키 이벤트 덮어쓰기
				}

			}
		});
		pswdField.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					login();
					event.consume(); // 기존 엔터키 이벤트 덮어쓰기
				}

			}
		});
	}

	// 로그인버튼액션
	public void loginBtnAction(ActionEvent event) {
		login();
	}

	// 취소버튼액션
	public void cancelBtnAction(ActionEvent event) {
		Platform.exit();
	}

	// 회원가입버튼액션
	public void signInBtnAction(ActionEvent event) {
		mainApp.showSignIn();

	}

	// 취소버튼액션
	public void signOutBtnAction(ActionEvent event) {
		mainApp.showSignOut();
	}

	public void login() {
		// 빈칸 체크
		if (idField.getText().isEmpty() || pswdField.getText().isEmpty()) {
			mainApp.alertMessage("Login Fail", "Please Fill both Fields");
			return;
		}
		// 아이디/패스워드 길이 제한
		if (idField.getText().length() > 20) {
			mainApp.alertMessage("Login Fail", "ID/Password must be shorter than 20 characters.");
			return;
		}
		// 아이디 존재 검사
		if (!mainApp.getQuery().checkExistingID(idField.getText())) {
			mainApp.alertMessage("Login Fail", "Incorrect ID!");
			return;
		}
		// 비밀번호 일치 검사
		else if (!mainApp.getQuery().checkPassword(idField.getText(), pswdField.getText())) {
			mainApp.alertMessage("Login Fail", "Incorrect password!");
			return;
		}
		// 중복 로그인 검사
		else if (mainApp.getQueryLogin().checkExistingID(idField.getText())) {
			log.info(mainApp.getQuery().selectOne(idField.getText()).getId());
			mainApp.alertMessage("Login Fail", "The User Is Already Being Logged In!");
			return;
		}
		// 로그인 성공
		else {
			mainApp.alertMessage("Login Success", "Welcome " + idField.getText() + "!");
			mainApp.setID(idField.getText()); // 이후 메인 앱에서 참조하기 위해 ID 넘겨주기
			mainApp.getQueryLogin().insertMember(idField.getText(), 0, "WaitingRoom");

			// 로그인 성공 했으면 클라이언트 띄워주기
			mainApp.showchatRoomBrowser();
		}
	}

}
