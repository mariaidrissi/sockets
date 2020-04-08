package clientPackage;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/**
 * Thread écrivain du client : attend une entrée de l'utilisateur et utilise le flux de sortie de la socket pour envoyer des messages.
 * 
 * @author lise
 */
public class SendThread extends Thread {

	/**
	 * Socket de communication avec le serveur créé par la classe principale.
	 */
	private Socket commSocket;
	
	/**
	 * Flux d'écriture de la socket de communication.
	 */
	private DataOutputStream output;
	
	@Override
	public synchronized void start() {
		super.start();
	}

	/**
	 * Récupère le flux d'écriture output de la socket.
	 * @param s
	 * 			Socket de communication créé par le client
	 */
	public SendThread(Socket s) {
		commSocket=s;
		try {
			output = new DataOutputStream(commSocket.getOutputStream()); //ouvrir le flux d'écriture
		} catch (IOException e1) {
			System.out.println("Erreur dans la récupération du flux d'écriture de la socket.");
		}
	}
	
	@Override
	public void run() {
		
		String message = "";
        
		Scanner sc = new Scanner(System.in);
		message = sc.nextLine(); //récupération du premier message
		
		try {
			while(message != null) { //tant que la connexion est ouverte
		 		output.writeUTF(message);
		 		if(message.equals("exit")) { //on se déconnecte sur le message "exit"
		 			Client.isConnected=false; 
		 			break;
		 		}	
		 		message = sc.nextLine();
			}
		} catch (Exception e) {
			if(Client.isConnected==true) { //si on était connecté alors cela veut dire que le serveur s'est deconnecté de manière anormale
				System.out.println("Erreur de connexion avec le serveur dans l'écriture.");
				Client.isConnected=false; //on se déconnecte 
			}	
			//sinon, le thread lecteur a fini en premier (à cause d'une erreur du serveur) et a fermé la connexion
		}          
		sc.close();
		try {
			output.close();
		} catch (IOException e1) {
			System.out.println("Erreur dans le fermeture du flux d'écriture.");
		}
	}

}
