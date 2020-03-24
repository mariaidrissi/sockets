package serverPackage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import serverPackage.Serveur;

public class ServerThread extends Thread {

	protected Socket commSocket;
	protected String nom;
	protected DataInputStream input;
	protected DataOutputStream output;
	
	public DataInputStream getInput() {
		return input;
	}

	public void setInput(DataInputStream input) {
		this.input = input;
	}

	public DataOutputStream getOutput() {
		return output;
	}

	public void setOutput(DataOutputStream output) {
		this.output = output;
	}

	public ServerThread(Socket soc,String nom) throws IOException {
		super();
		commSocket = soc;
		this.nom=nom;
		input = new DataInputStream(commSocket.getInputStream());
		output = new DataOutputStream(commSocket.getOutputStream());
	}

	@Override
	public void run() {

		try {

			String message = "";
			
			while(!commSocket.isClosed()) {
				message = input.readUTF();
				if(message != null) {
					if(message.equals("exit")) {
						break; //to change 
					}
					Serveur.sendMessage(message,nom);
				}
			}
			
		} catch (IOException e) {
			System.out.println("Connection has been closed by client.");
		}
		
		try {
			
			input.close();
			output.close();
			this.commSocket.close();
			
			Serveur.quitConversation(nom);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	public Socket getCommSocket() {
		return commSocket;
	}

	public void setCommSocket(Socket commSocket) {
		this.commSocket = commSocket;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	@Override
	public synchronized void start() {
		super.start();
	}

	@Override
	public String toString() {
		return super.toString();
	}

	

}
