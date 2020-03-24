package clientPackage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.net.ServerSocket;
import java.net.Socket;

public class Client {

	public Client() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		
		try {
			
			Socket client = new Socket ("localhost", 10080);
             
            DataOutputStream out = new DataOutputStream(client.getOutputStream());
            DataInputStream in = new DataInputStream(client.getInputStream());

            System.out.println("Entrez votre pseudo:");
            Scanner sc = new Scanner(System.in);
            String name = sc.nextLine();
            out.writeUTF(name);
            
            SendThread send = new SendThread(client);
            ReceiveThread receive = new ReceiveThread(client);
            send.start();
            receive.start();
            
            try {
				send.join();
				out.close();
				in.close();
				client.close();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
           
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

}
