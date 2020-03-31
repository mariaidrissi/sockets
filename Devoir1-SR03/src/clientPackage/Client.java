package clientPackage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Scanner;
import java.net.Socket;

/**
 * Cette classe est la classe principale du client : récupère le nom et lance les threads écrivain et lecteur.
 * Elle ouvre et ferme la socket de communication pour les threads.
 *
 * @author lise
 */
public class Client {

	/**
	 * Variable partagée permettant de gérer la communication entre les threads
	 * écrivains et lecteurs. isConnected est true quand la connexion est établie et fonctionne ; et
	 * devient faux si le client se déconnecte ou un problème est survenu sur le canal.
	 */
	public volatile static boolean isConnected=false;
	
	public Client() {
	}

	public static void main(String[] args) {
		
		try {
			Socket client = new Socket ("localhost", 10080); //création de la socket de communication sur le port 10080
             
			String name = recupererNomClient(client); //récupérer le nom du client
			if(name.equals("")) { //s'il y a eu un problème lors de la réception du nom, on annule cette connexion
        		client.close();
        		return;
        	}
            
            isConnected=true; //client identifié et ayant des flux fonctionnels : connecté
            SendThread send = new SendThread(client); 
            ReceiveThread receive = new ReceiveThread(client); 
            send.start(); //démarrer le thread ecrivain
            receive.start(); //démarrer le thread lecteur
            
            try {
            	//attendre la fin des threads
				send.join(); 
				receive.join();
				//fermer la connexion
				client.close();
			} catch (Exception e) {
				System.out.println("Erreur dans la fermeture du client.");
			}
		}catch (IOException e) {
			System.out.println("Erreur dans la création de la socket");
		}
	}
	
	/**
     * 
     * Récupère le nom du client en utilisant la socket de communication et gère les erreurs de communication et 
     * le fait que le nom doit être unique.
     * 
     * @param client
     * 		La connexion par laquelle communiquer avec le client
     * @return
     * 		Le nom du client ; le nom est vide s'il y a eu un problème de connexion.
     */
	public static String recupererNomClient(Socket client) {
		
		String name="";
		Scanner sc=null;
		
		try {
			//récupération des flux d'entrée/sortie pour fixer le nom du client
	        DataOutputStream out = new DataOutputStream(client.getOutputStream());
	        DataInputStream in = new DataInputStream(client.getInputStream());
	
	        System.out.println("Entrez votre pseudo:");
	        sc = new Scanner(System.in); 
	        name = sc.nextLine(); //récupération du nom 
        
            out.writeUTF(name);
            while(in.readBoolean()==false) { //tant que le nom entré n'est pas correct (déjà utilisé par quelqu'un d'autre)
        	   System.out.println("Pseudo déjà utilisé, entrez en un autre:");
        	   name=sc.nextLine();
        	   out.writeUTF(name);
            }
        }catch(IOException e) {
        	System.out.println("Erreur dans la communication avec le serveur, connexion abandonée."); 
        	sc.close();
        	name="";
        }
        return name;
	}

}
