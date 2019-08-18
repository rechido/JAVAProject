package messenger.util;

import java.io.PrintWriter;
import java.net.Socket;

import org.apache.log4j.Logger;

import messenger.view.MessengerClientController;

public class Client {

	private Logger log = Logger.getLogger(this.getClass());
	private ReceiverThread receiver = null;
	private Socket socket = null;

	public Client(MessengerClientController controller, String id, String ipAddress, int locaPort) {
		super();

		// 클라이언트 소켓 생성 및 서버에 접속
		try {
			socket = new Socket(ipAddress, locaPort);
			log.info("IP: " + ipAddress + ", Port: " + locaPort + "에 접속합니다.");
			controller.setSocket(socket);
			receiver = new ReceiverThread(socket, controller.getChatDisplayArea(), id);
			receiver.setDaemon(true);
			receiver.start();

			PrintWriter writer = new PrintWriter(socket.getOutputStream());

			writer.println(id);
			writer.flush();
			log.info("Client Start");

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public ReceiverThread getReceiverThread() {
		return receiver;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

}
