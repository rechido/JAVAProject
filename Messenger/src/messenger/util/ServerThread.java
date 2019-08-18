package messenger.util;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import messenger.MainApp;

public class ServerThread extends Thread {

	private Logger log = Logger.getLogger(this.getClass());
	private ServerSocket serverSocket = null;
	private String ipAddress = null;
	private int localPort = 0;

	// 메인 앱 참조
	MainApp mainApp;

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	@Override
	public void run() {
		super.run();

		try {
			for (int cnt = 9000; cnt < 10000; cnt++) {
				if (isTcpPortAvailable(cnt)) {
					serverSocket = new ServerSocket(cnt);
					localPort = serverSocket.getLocalPort();
					break;
				} else if (cnt <= 10000) {
					log.info("port: " + cnt + "가 사용불가");
				} else {
					log.info("사용 가능한 포트가 없습니다.");
					return;
				}
			}
			// log.info("포트넘버: " + localPort);
			mainApp.setLocalPort(localPort);
			try {
				InetAddress local = InetAddress.getLocalHost();
				ipAddress = local.getHostAddress();
				// log.info("Server IP Address: " + ipAddress);
				mainApp.setIpAddress(ipAddress);

			} catch (UnknownHostException e) {
				System.out.println(e.getMessage());
			}

			log.info("Server Start");

			while (true) {
				Socket socket = serverSocket.accept();
				PerClientThread thread = new PerClientThread(socket);
				thread.setDaemon(true);
				thread.start();
				if (serverSocket.isClosed()) {
					break;
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			log.info("서버 종료.");
		}
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	// 포트 가용성 테스트 메소드
	public static boolean isTcpPortAvailable(int port) {
		try (ServerSocket serverSocket = new ServerSocket()) {
			// setReuseAddress(false) is required only on OSX,
			// otherwise the code will not work correctly on that platform
			serverSocket.setReuseAddress(false);
			serverSocket.bind(new InetSocketAddress(InetAddress.getByName("localhost"), port), 1);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	public void setServerSocket(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

}
