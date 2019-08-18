package application;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

public class MainController implements Initializable {
		// 디버깅용 log
		private Logger log = Logger.getLogger(this.getClass());
		// 메인 앱 참조
		private Main main;

		public void setMain(Main main) {
			this.main = main;
		}
		
		@FXML
		private Button start;
		@FXML
		private TextField width;
		@FXML
		private TextField height;
		@FXML
		private TextField bombCount;

		@Override
		public void initialize(URL location, ResourceBundle resources) {
			start.setOnAction(e -> startAction(e));
		}
		
		public void startAction(ActionEvent event) {
			try {
				int widthSize = Integer.parseInt(width.getText());
				int heightSize = Integer.parseInt(height.getText());
				int areaSize = widthSize * heightSize;
				int bombcount = Integer.parseInt(bombCount.getText());
				if( bombcount>=areaSize )
					main.alertMessage("맵 조성 에러", "지뢰의 수가 필드보다 많습니다!");
				else {
					main.setWidth(widthSize);
					main.setHeight(heightSize);
					main.setBombCount(bombcount);
					main.showMineSweeper();
					main.setStartTime(System.nanoTime()); 
				}
				
			}catch (NumberFormatException nfe) {
				main.alertMessage("입력 에러", "숫자를 바르게 입력해주세요.");
			}
			
		}
		
}
