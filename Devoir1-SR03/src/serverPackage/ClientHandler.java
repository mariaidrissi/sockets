package serverPackage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import serverPackage.Serveur;


/** 
 * Elle étend la classe Thread et implémente les fonctions start() et run().
 * Cette classe est instanciée pour chaque client : elle gère les messages reçus par la socket.
 * On a a alors un thread par client (1 instance de ClientHandler par client).
 *
 * @author lise
 */
public class ClientHandler extends Thread {

	/**
	 * Socket de communication du client créé par le serveur
	 */
	private Socket commSocket;
	
	/**
	 * Nom du client permettant de l'identifier (unique)
	 */
	private String nom;
	
	/**
	 * Flux de lecture pour la socket de communication
	 */
	private DataInputStream input;
	
	/**
	 * Flux d'écriture pour la socket de communication
	 */
	private DataOutputStream output;

	/**
	 * Constructeur ClientHandler.
	 * @param soc
	 * 			Socket client créé par le serveur
	 * @param nom
	 * 			Nom du client pour identification
	 * @throws IOException
	 */
	public ClientHandler(Socket soc,String nom) throws IOException {
		super();
		commSocket = soc;
		this.nom=nom;
		input = new DataInputStream(commSocket.getInputStream());
		output = new DataOutputStream(commSocket.getOutputStream());
	}

	@Override
	public void run() {

		String message = "";
		
		try {
			while(true) {
				message = input.readUTF();
				if(message != null) { //tant que la connexion est ouverte
					if(message.equals("exit")) //si le client ferme la connexion, on sort de la boucle
						break; 
					Serveur.sendMessage(message,nom);
				}
			}
		} catch (IOException e) {
			System.out.println("La connexion avec "+ nom +"  a été perdue."); //fermeture inattendue du client
		}
		
		try {
			//on ferme la connexion avec le client
			input.close();
			output.close();
			commSocket.close();
			
			//le client quitte la conversation 
			Serveur.quitConversation(nom);
			
		} catch (IOException e) {
			System.out.println("Erreur dans la fermeture des streams de donnees.");
		}
	}

	public Socket getCommSocket() {
		return commSocket;
	}
	
	public DataInputStream getInput() {
		return input;
	}

	public DataOutputStream getOutput() {
		return output;
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
