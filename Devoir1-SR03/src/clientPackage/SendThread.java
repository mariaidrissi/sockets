package clientPackage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class SendThread extends Thread {

	protected Socket commSocket;
	
	@Override
	public synchronized void start() {
		// TODO Auto-generated method stub
		super.start();
	}

	public SendThread(Socket s) {
		commSocket=s;
	}
	
	public void run() {
		DataOutputStream output=null;
		DataInputStream input=null;
		try {
			output = new DataOutputStream(commSocket.getOutputStream());
			input = new DataInputStream(commSocket.getInputStream());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
		Scanner sc = new Scanner(System.in);
		String message = "";
		
		 while(!commSocket.isClosed()) {
			 
			 try {
	         	message = sc.nextLine();

	         	if(message != null) {
	         		
		         	if(message.equals("exit"))
		         		break;
		         	
	         		output.writeUTF(message);
	         	}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Connection problem in sending !");
				Thread.currentThread().interrupt();
			}            	
         }	
		
	}

}
