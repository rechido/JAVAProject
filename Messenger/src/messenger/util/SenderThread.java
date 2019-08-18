package messenger.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SenderThread extends Thread {
	Socket socket;
	InputStream input;

	public SenderThread(Socket socket, InputStream input) {
		super();
		this.socket = socket;
		this.input = input;
	}

	@Override
	public void run() {
		try {

			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			PrintWriter writer = new PrintWriter(socket.getOutputStream());
			// writer.println(name);
			// writer.flush();

			// while(true) {

			String str = reader.readLine();
			// if(str.equals("bye"))
			// break;
			writer.println(str);
			writer.flush();
			// if(!(str.equals("")))
			// System.out.println("입력: " + str);
			// }

		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				// socket.close();
				this.interrupt();
			} catch (Exception ignored) {
			}
		}

	}

}
