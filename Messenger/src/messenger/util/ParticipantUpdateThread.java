package messenger.util;

import java.util.List;

import org.apache.log4j.Logger;

import javafx.application.Platform;
import javafx.scene.control.TableView;
import messenger.MainApp;
import messenger.model.LoginMember;
import messenger.view.MessengerClientController;

public class ParticipantUpdateThread extends Thread {

	private Logger log = Logger.getLogger(this.getClass());

	private TableView<LoginMember> participantTable;

	int participantCount = 0;

	// 메인 앱 참조
	MainApp mainApp;

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	public ParticipantUpdateThread(TableView<LoginMember> participantTable) {
		super();
		this.participantTable = participantTable;
	}

	@Override
	public void run() {
		super.run();
		log.info("참여자 업데이트 스레드 시작");

		try {
			while (true) {
				int roomNum = mainApp.getQueryLogin().selectRoomNum(mainApp.getID());
				List<LoginMember> member = mainApp.getQueryLogin().selectAllinChatRoom(roomNum);

				// 방장 없을 시 자동 강퇴
				boolean roomMasterIsNoWhere = true;
				for (int cnt = 0; cnt < member.size(); cnt++) {
					if (member.get(cnt).getStatus().equals("RoomMaster"))
						roomMasterIsNoWhere = false;
				}
				if (roomMasterIsNoWhere) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							mainApp.alertMessage("대화방 종료", "방장이 나가 대화방이 닫힙니다.");
							controller.returnToBrowser();
						}
					});

					break;
				}

				int updatedParticipantCount = member.size();
				// log.info("현재 대화방 총 인원: " + updatedParticipantCount);

				if (participantCount != updatedParticipantCount) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							mainApp.updateParticipantData();
							participantTable.setItems(mainApp.getParticipantData());
							participantCount = updatedParticipantCount;
						}
					});
				}
				Thread.sleep(1000); // 1초에 한번 업데이트

			}
		} catch (NullPointerException e) {
			e.printStackTrace();
			log.info("참여자 업데이트 스레드 에러 종료: NullPointerException");
		} catch (Exception e) {
			e.printStackTrace();
			log.info("참여자 업데이트 스레드 에러 종료");
		} finally {
			log.info("참여자 업데이트 스레드 종료");
		}

	}

	MessengerClientController controller;

	public void setMessengerClientController(MessengerClientController controller) {
		this.controller = controller;
	}

}
