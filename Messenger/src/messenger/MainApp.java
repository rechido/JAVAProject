package messenger;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXMLLoader;

import messenger.model.LoginMember;
import messenger.util.Client;
import messenger.util.MybatisQuery;
import messenger.util.MybatisQueryChatRoom;
import messenger.util.MybatisQueryLoginMember;
import messenger.util.ParticipantUpdateThread;
import messenger.util.ServerThread;
import messenger.view.LoginController;
import messenger.view.MessengerClientController;
import messenger.view.RoomCreateController;
import messenger.view.SignInController;
import messenger.view.SignOutController;
import messenger.view.ChatRoomBrowserController;

public class MainApp extends Application {

	// JDBC 연동용 Mybatis
	private MybatisQuery query = new MybatisQuery("messenger/mybatis-config.xml");
	private MybatisQueryChatRoom queryChatroom = new MybatisQueryChatRoom("messenger/mybatis-config-chatroom.xml");
	private MybatisQueryLoginMember queryLogin = new MybatisQueryLoginMember(
			"messenger/mybatis-config-loginMember.xml");
	private Logger log = Logger.getLogger(this.getClass());

	private Stage primaryStage = null;
	private String id = null;
	private String ipAddress = null;
	private int localPort = 0;
	private ParticipantUpdateThread update = null;
	private ServerThread server = null;
	private Client client = null;

	// 대화방 참여 인원 리스트
	private ObservableList<LoginMember> participantData = FXCollections.observableArrayList();

	public ObservableList<LoginMember> getParticipantData() {
		return participantData;
	}

	public void updateParticipantData() {
		participantData.clear();
		int roomNum = queryLogin.selectRoomNum(this.id);
		List<LoginMember> memberlist = queryLogin.selectAllinChatRoom(roomNum);
		
		for (int cnt = 0; cnt < memberlist.size(); cnt++) {
			if(memberlist.get(cnt).getId().equals(this.id))
				memberlist.get(cnt).setId(this.id+" (You)");
			participantData.add(memberlist.get(cnt));
		}
			
	}

	// log를 사용하기 위한 메소드
	public void printlog(String msg) {

		log.debug(msg);

	}

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.getIcons().add(new Image("file:resource/images/messengerIcon.png"));
		log.info("Start");
		showLogin();

	}

	@Override
	public void stop() throws Exception {
		super.stop();
		// 로그인 기록을 삭제
		if (this.getID() != null) {
			if (queryLogin.selectStatus(this.getID()).equals("RoomMaster"))
				queryChatroom.deleteChatRoom(this.getID());
			queryLogin.delete(this.getID());
		}
		// 서버 오픈시 닫아줌
		closeServer();
		log.info("정상 종료.");

	}

	// 로그인 Scene을 보여준다
	public void showLogin() {
		try {
			// fxml 파일에서 상위 레이아웃을 가져온다.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/Login.fxml"));
			AnchorPane Layout = (AnchorPane) loader.load();

			// 상위 레이아웃을 포함하는 scene을 보여준다.
			Scene scene = new Scene(Layout);
			primaryStage.setScene(scene);
			primaryStage.setTitle("Login - Dev. by D. E. Lee");
			primaryStage.setResizable(false);
			primaryStage.show();

			// 메인 애플리케이션이 컨트롤러를 이용할 수 있게 한다.
			LoginController controller = loader.getController();
			controller.setMainApp(this);

			log.info("Login");
		} catch (IOException e) {
			e.printStackTrace();

		}

	}

	// 회원가입 Scene을 보여준다
	public void showSignIn() {
		try {
			// fxml 파일에서 상위 레이아웃을 가져온다.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/SignIn.fxml"));
			AnchorPane layout = (AnchorPane) loader.load();

			// 상위 레이아웃을 포함하는 scene을 보여준다.
			Scene scene = new Scene(layout);
			primaryStage.setScene(scene);
			primaryStage.setTitle("Sign In - Dev. by D. E. Lee");
			primaryStage.setResizable(false);
			primaryStage.show();

			// 메인 애플리케이션이 컨트롤러를 이용할 수 있게 한다.
			SignInController controller = loader.getController();
			controller.setMainApp(this);

			log.info("Sign In");
		} catch (IOException e) {
			e.printStackTrace();

		}

	}

	// 회원탈퇴 Scene을 보여준다
	public void showSignOut() {
		try {
			// fxml 파일에서 상위 레이아웃을 가져온다.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/SignOut.fxml"));
			AnchorPane layout = (AnchorPane) loader.load();

			// 상위 레이아웃을 포함하는 scene을 보여준다.
			Scene scene = new Scene(layout);
			primaryStage.setScene(scene);
			primaryStage.setTitle("Sign Out - Dev. by D. E. Lee");
			primaryStage.setResizable(false);
			primaryStage.show();

			// 메인 애플리케이션이 컨트롤러를 이용할 수 있게 한다.
			SignOutController controller = loader.getController();
			controller.setMainApp(this);

			log.info("Sign Out");
		} catch (IOException e) {
			e.printStackTrace();

		}

	}

	// 대화방 선택 Scene을 보여준다
	public void showchatRoomBrowser() {
		try {
			// fxml 파일에서 상위 레이아웃을 가져온다.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/chatRoomBrowser.fxml"));
			AnchorPane layout = (AnchorPane) loader.load();

			// 상위 레이아웃을 포함하는 scene을 보여준다.
			Scene scene = new Scene(layout);
			primaryStage.setScene(scene);
			primaryStage.setTitle("Room Browser - Dev. by D. E. Lee");
			primaryStage.setResizable(false);
			primaryStage.show();

			// 메인 애플리케이션이 컨트롤러를 이용할 수 있게 한다.
			ChatRoomBrowserController controller = loader.getController();
			controller.setMainApp(this);
			controller.refresh();

			log.info("Room Browser");
		} catch (IOException e) {
			e.printStackTrace();

		}

	}

	// 대화방 생성 Scene을 보여준다
	public void showRoomCreate() {
		try {
			// fxml 파일에서 상위 레이아웃을 가져온다.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/RoomCreate.fxml"));
			AnchorPane layout = (AnchorPane) loader.load();

			// 상위 레이아웃을 포함하는 scene을 보여준다.
			Scene scene = new Scene(layout);
			primaryStage.setScene(scene);
			primaryStage.setTitle("New Room - Dev. by D. E. Lee");
			primaryStage.setResizable(false);
			primaryStage.show();

			// 메인 애플리케이션이 컨트롤러를 이용할 수 있게 한다.
			RoomCreateController controller = loader.getController();
			controller.setMainApp(this);

			log.info("New Room");
		} catch (IOException e) {
			e.printStackTrace();

		}

	}

	// MessengerClient를 보여준다
	public void showMessengerClient(String roomTitle) {
		try {
			// fxml 파일에서 상위 레이아웃을 가져온다.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/messengerClient.fxml"));
			AnchorPane layout = (AnchorPane) loader.load();

			// 상위 레이아웃을 포함하는 scene을 보여준다.
			Scene scene = new Scene(layout);
			primaryStage.setScene(scene);
			primaryStage.setTitle(roomTitle + " - Dev. by D. E. Lee");
			primaryStage.setResizable(false);
			primaryStage.show();

			// 메인 애플리케이션이 컨트롤러를 이용할 수 있게 한다.
			MessengerClientController controller = loader.getController();
			controller.setMainApp(this);
			openClient(controller);

			// 참여자 실시간 갱신 스레드 시작
			update = new ParticipantUpdateThread(controller.getParticipantTable());
			update.setMainApp(this);
			update.setMessengerClientController(controller);
			update.setDaemon(true);
			update.start();

			log.info("messengerClient");
		} catch (IOException e) {
			e.printStackTrace();

		}

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

	// 접속한 계정 보관
	public void setID(String id) {
		this.id = id;
	}

	public String getID() {
		return this.id;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
		// log.info("ip주소: " +this.ipAddress);
	}

	public int getLocalPort() {
		return localPort;
	}

	public void setLocalPort(int localPort) {
		this.localPort = localPort;
		// log.info("포트넘버: " + this.localPort);
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public MybatisQuery getQuery() {
		return query;
	}

	public MybatisQueryChatRoom getQueryChatroom() {
		return queryChatroom;
	}

	public MybatisQueryLoginMember getQueryLogin() {
		return queryLogin;
	}

	public ParticipantUpdateThread getParticipantUpdateThread() {
		return update;
	}

	// 서버 오픈 메소드
	public void openServer() {
		server = new ServerThread();
		server.setMainApp(this);
		server.setDaemon(true);
		server.start();
	}

	// 서버 종료 메소드
	public void closeServer() {
		try {
			server.getServerSocket().close();
			log.info("서버 소켓 닫음.");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			log.info("서버가 열려있지 않음.");
		}

	}

	// 클라이언트 오픈 메소드
	public void openClient(MessengerClientController controller) {
		// 클라이언트 오픈
		client = new Client(controller, this.getID(), ipAddress, localPort);
	}

	// 클라이언트 종료 메소드
	public void closeClient() {
		try {
			client.getSocket().close();
			log.info("클라이언트 종료.");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			log.info("클라이언트가 열려있지 않음.");
		}

	}

	public static void main(String[] args) {
		// Server server = new Server();
		launch(args);

	}
}
