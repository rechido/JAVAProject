package application;

import java.io.IOException;

import org.apache.log4j.Logger;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.fxml.FXMLLoader;

public class Main extends Application {

	// 디버깅용 log
	private Logger log = Logger.getLogger(this.getClass());
	private Stage primaryStage = null;
	private int bombCount = 0;
	private int width = 0, height = 0;
	private long startTime = 0, endTime = 0;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		showMain();

	}

	public void showMain() {
		try {
			// fxml 파일에서 상위 레이아웃을 가져온다.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("Main.fxml"));
			AnchorPane layout = (AnchorPane) loader.load();

			// 상위 레이아웃을 포함하는 scene을 보여준다.
			Scene scene = new Scene(layout);
			primaryStage.setScene(scene);
			primaryStage.setTitle("MineSweeper Ver 2.0 - Dev. by D. E. Lee");
			primaryStage.getIcons().add(new Image("file:resources/icon.png"));
			primaryStage.setResizable(false);
			primaryStage.show();

			// 메인 애플리케이션이 컨트롤러를 이용할 수 있게 한다.
			MainController controller = loader.getController();
			controller.setMain(this);

			log.info("Display - Main");
		} catch (IOException e) {
			e.printStackTrace();

		}
	}

	public void showMineSweeper() {
		try {
			// fxml 파일에서 상위 레이아웃을 가져온다.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("MineSweeper.fxml"));
			AnchorPane layout = (AnchorPane) loader.load();
			layout.setPrefWidth(30 * width);
			layout.setPrefHeight(30 + 30 * height);

			// 메인 애플리케이션이 컨트롤러를 이용할 수 있게 한다.
			MineSweeperController controller = loader.getController();
			controller.setMain(this);			
			controller.initBtnsArray(width * height);
			controller.initializeTexts(width * height);
			controller.getGamePane().getChildren().add(controller.getGrid());
			controller.installBomb();
			controller.installNumbers();
			controller.installButtons();
			// controller.printTexts();
			// controller.showTexts();

			// 상위 레이아웃을 포함하는 scene을 보여준다.
			Scene scene = new Scene(layout);
			primaryStage.setScene(scene);
			primaryStage.setTitle("MineSweeper - Dev. by D. E. Lee");
			primaryStage.setResizable(false);
			primaryStage.show();

			log.info("Display - MineSweeper");
		} catch (IOException e) {
			e.printStackTrace();

		}
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	// 에러 메시지 띄워주기
	public void alertMessage(String title, String str) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(title);

		// Header Text: null
		alert.setHeaderText(null);
		alert.setContentText(str);

		alert.showAndWait();
	}

	public int getBombCount() {
		return bombCount;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void setBombCount(int bombCount) {
		this.bombCount = bombCount;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
