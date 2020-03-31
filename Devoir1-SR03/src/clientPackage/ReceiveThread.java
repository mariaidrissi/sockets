package clientPackage;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Thread lecture du client : attend un message sur le flux d'entrée de la socket de communication et l'affiche sur la console.
 * 
 * @author lise
 */
public class ReceiveThread extends Thread {

	/**
	 * Socket de communication avec le serveur créé par la classe principale.
	 */
	private Socket commSocket;

	/**
	 * Flux de lecture de la socket de communication.
	 */
	private DataInputStream input;
	
	/**
	 * Récupère le flux de lecture input de la socket
	 * @param s
	 * 			Socket de communication créé par le client
	 */
	public ReceiveThread(Socket s) {
		commSocket = s;
		try {
			 input = new DataInputStream(commSocket.getInputStream());
		} catch (IOException e) {
			System.out.println("Erreur dans la création du flux de lecture de la socket.");
			Thread.currentThread().interrupt();
		}
	}
	
	public void run(){

		String message = "";
		try {
			message = input.readUTF();
			while(message != null) { //tant que la connexion est ouverte
				System.out.println(message);
				message = input.readUTF();
			}
		}catch (Exception e) {
			if(Client.isConnected==true) { //si on était connecté alors cela veut dire que le serveur s'est deconnecté de manière anormale
				System.out.println("Erreur de connexion avec le serveur dans la lecture.");
				Client.isConnected=false; //on se deconnecte
			}
			//else : interrompu par le thread écrivain fermant la connexion parce que l'utilisateur a envoyé "exit" 
		}
		try {
			input.close();
		} catch (IOException e1) {
			System.out.println("Erreur dans la fermeture du flux de lecture.");
		}
	}

	@Override
	public synchronized void start() {
		// TODO Auto-generated method stub
		super.start();
	}
}
