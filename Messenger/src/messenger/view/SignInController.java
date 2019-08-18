package messenger.view;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import messenger.MainApp;
import messenger.util.MybatisQuery;

public class SignInController implements Initializable {

	@FXML
	private TextField idField;
	@FXML
	private PasswordField pswdField;
	@FXML
	private PasswordField pswdcheckField;
	@FXML
	private Button cancelBtn;
	@FXML
	private Button signInBtn;

	// 디버깅용 log
	private Logger log = Logger.getLogger(this.getClass());
	// 메인 앱 참조
	private MainApp mainApp;

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		signInBtn.setOnAction(e -> signInBtnAction(e));
		cancelBtn.setOnAction(e -> cancelBtnAction(e));
		// 엔터키로도 액션 작동하게 만들기
		idField.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					signIn();
					event.consume(); // 기존 엔터키 이벤트 덮어쓰기
				}

			}
		});
		pswdField.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					signIn();
					event.consume(); // 기존 엔터키 이벤트 덮어쓰기
				}

			}
		});
		pswdcheckField.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					signIn();
					event.consume(); // 기존 엔터키 이벤트 덮어쓰기
				}

			}
		});

	}

	// 회원가입버튼액션
	public void signInBtnAction(ActionEvent event) {
		signIn();
	}

	// 취소버튼액션
	public void cancelBtnAction(ActionEvent event) {
		mainApp.showLogin();
	}
	
	public void signIn() {
		log.info("회원가입 창");
		// ID, Password 빈칸 검사
		if (idField.getText().isEmpty() || pswdField.getText().isEmpty() || pswdcheckField.getText().isEmpty()) {
			mainApp.alertMessage("Sign In Fail", "Enter Correct ID or Password.");
			return;
		}

		// ID, Password 길이 검사
		if (idField.getText().length() > 20 || pswdField.getText().length() > 20) {
			mainApp.alertMessage("Sign In Fail", "Maximum length of ID/Password is 20 characters.");
			return;
		}

		// 패스워드/체크란 동일 여부 체크
		if (!pswdField.getText().equals(pswdcheckField.getText())) {
			mainApp.alertMessage("Sign In Fail", "Two passwords are not same.");
			return;
		}

		// 아이디 중복 검사
		if (mainApp.getQuery().checkExistingID(idField.getText())) {
			mainApp.alertMessage("Sign In Fail", "ID exists already.");
			return;
		} else {
			mainApp.alertMessage("Sign In Success", "Welcome to the Messenger Dev. by D. E. Lee!");
			mainApp.getQuery().insertMember(idField.getText(), pswdField.getText());
			mainApp.showLogin();
		}
	}

}
