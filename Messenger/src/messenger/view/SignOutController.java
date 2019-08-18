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

public class SignOutController implements Initializable {

	@FXML
	private TextField idField;
	@FXML
	private PasswordField pswdField;
	@FXML
	private PasswordField pswdcheckField;
	@FXML
	private Button cancelBtn;
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

		signOutBtn.setOnAction(e -> signOutBtnAction(e));
		cancelBtn.setOnAction(e -> cancelBtnAction(e));
		idField.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					signOut();
					event.consume(); // 기존 엔터키 이벤트 덮어쓰기
				}

			}
		});
		pswdField.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					signOut();
					event.consume(); // 기존 엔터키 이벤트 덮어쓰기
				}

			}
		});
		pswdcheckField.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					signOut();
					event.consume(); // 기존 엔터키 이벤트 덮어쓰기
				}

			}
		});

	}

	// 회원 탈퇴 버튼 액션
	public void signOutBtnAction(ActionEvent event) {
		signOut();
	}

	// 취소버튼액션
	public void cancelBtnAction(ActionEvent event) {
		mainApp.showLogin();
	}

	public void signOut() {
		// ID, Password 빈칸 검사
		if (idField.getText().isEmpty() || pswdField.getText().isEmpty() || pswdcheckField.getText().isEmpty()) {
			mainApp.alertMessage("Sign Out Fail", "Enter Correct ID or Password.");
			return;
		}

		// 패스워드/체크란 동일 여부 체크
		if (!pswdField.getText().equals(pswdcheckField.getText())) {
			mainApp.alertMessage("Sign Out Fail", "Two passwords are not same.");
			return;
		}

		// 아이디 존재 검사
		if (mainApp.getQuery().checkExistingID(idField.getText())) {
			// 패스워드 검사
			if (mainApp.getQuery().checkPassword(idField.getText(), pswdField.getText())) {
				mainApp.alertMessage("Sign Out Success", "Good Bye " + idField.getText() + "!");
				mainApp.getQuery().delete(idField.getText());
				mainApp.showLogin();
			} else {
				mainApp.alertMessage("Sign Out Fail", "Password doesn't match.");
				return;
			}

		} else {
			mainApp.alertMessage("Sign Out Fail", "ID doesn't exist.");
			return;
		}
	}

}
