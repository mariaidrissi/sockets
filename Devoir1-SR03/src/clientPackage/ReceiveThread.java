package clientPackage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ReceiveThread extends Thread {

	protected Socket commSocket;
	
	public ReceiveThread(Socket s) {
		commSocket = s;
	}
	
	public void run() {
		
		DataOutputStream output= null;
		DataInputStream input = null;
		try {
			 output = new DataOutputStream(commSocket.getOutputStream());
			 input = new DataInputStream(commSocket.getInputStream());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
       
		String message = "";
		
		while(!commSocket.isClosed()) {
			
			try {
				message = input.readUTF();
				if(message != null)
					System.out.println(message);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Connection problem in receiving!");
				Thread.currentThread().interrupt();
			}
		}
	}

	@Override
	public synchronized void start() {
		// TODO Auto-generated method stub
		super.start();
	}
}
