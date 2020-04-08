package serverPackage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import serverPackage.ClientHandler;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Elle reçoit les nouvelles connexions et créé un nouveau thread pour chaque client.
 * Elle stocke les informations sur les connexions dans une HashTable : les pseudonymes (nom) sont uniques donc clé de la table.
 * Cette classe définit des methodes statiques pour utilisation par les threads ClientHandler.
 * 
 * @author lise
 */
public class Serveur {

	/**
	 * Liste des clients connectés au serveur.
	 * Associe le nom unique du client avec le thread ClientHandler créé pour recevoir les messages
	 */
    private static Hashtable<String,ClientHandler> clients = new Hashtable<String,ClientHandler>();
    
    /**
     * Nombre de clients connectes sur le serveur.
     */
    private static int nbClientsConnectes = 0;
    
    /**
     * 
     * Attend les nouvelles connexions, créé la socket de communication et récupère le nom pour chaque nouvelle connexion.
     * 
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
    	ServerSocket conn = null;
    	ClientHandler client = null;
    	Socket comm = null;
    	
    	try{
    		conn = new ServerSocket(10080) ;
    	} catch(IOException e){
    		System.out.println("La socket n'a pas pu etre créé.");
        }
	
    	try {
    		while(true){
    			System.out.println("En attente d'un client...");
	    		comm = conn.accept();
	    	
	        	String nom = recupererNomClient(comm);
	        	if(nom.equals("")) { //s'il y a eu un problème lors de la réception du nom, on annule cette connexion
	        		comm.close();
	        		continue;
	        	}
	            
	            System.out.println("Nouveau client : "+nom+", un nouveau thread est créé...");
	            
	            try {
	            	client = new ClientHandler(comm, nom); //créé le nouveau thread client
	            	client.start();
	            	Serveur.joinConversation(nom, client);
	            	
	            } catch(IOException e){
	        		System.out.println("Erreur dans la creation du thread client.");
	            }
    		}
    	} catch (IOException e) {
    		conn.close();
    		System.out.println("Erreur dans la creation de la socket.");
        }
    }
    
    /**
     * 
     * Récupère le nom du client en utilisant la socket de communication
     * et vérifie que le nom donné n'est pas déjà pris puisqu'il est unique.
     * 
     * @param comm
     * 		La connexion par laquelle communiquer avec le client
     * @return
     * 		Le nom du client ; le nom retourné est vide s'il y a eu un problème de connexion.
     */
    static String recupererNomClient(Socket comm) {
    	
    	String name="";
		try {
			//récupération des flux d'entrée/sortie de la socket
			DataInputStream in = new DataInputStream(comm.getInputStream());
	        DataOutputStream out = new DataOutputStream(comm.getOutputStream());
	        
	        name = in.readUTF();
	        while(clients.containsKey(name)) { //le nom est déjà pris par un client connecté
	        	out.writeBoolean(false);
	        	name=in.readUTF(); 
	        }
	        out.writeBoolean(true);
	        
		} catch (IOException e) {
			System.out.println("Problème dans la récupération du nom. Connexion annulée.");
			name="";
		}
		return name;
    }
    
    /**
     * 
     * Écrit la chaine passée en paramètre aux clients connectés.
     * Utilise une structure Iterator pour parcourir les clients.                                                                                                                                                                                                                                                    
     * 
     * @param chaine
     * 			Chaine à écrire dans les sockets des clients connectés. 
     */
    static void ecrireClients(String chaine) {
    	
    	Set<String> keys = clients.keySet();
    	Iterator<String> itr = keys.iterator();
    	String key="";
    	
    	while (itr.hasNext()) { //tant qu'il reste un client
    		key=(String) itr.next();
    		try {
    			clients.get(key).getOutput().writeUTF(chaine);
			} catch (IOException e) {
				System.out.println("Le serveur n'a pas pu communiquer avec " + key);
			}
    	}
    }
    
    /**
     * 
     * Méthode pour ajouter un nouveau client dans la conversation et 
     * diffuser l'information à tous les clients connectés.
     * 
     * @param name
     * 			Nom du client rejoignant la conversation qui l'identifie de maniere unique
     * @param threadClient
     * 			Thread du serveur créé pour ce client
     */
    static void joinConversation(String name, ClientHandler threadClient) {
    	
    	nbClientsConnectes++;
    	clients.put(name,threadClient); //ajouter le client dans la liste des clients connectés
    	ecrireClients("\t"+name + " a rejoint la conversation\n \t--------------------- \n ");
    	System.out.println(nbClientsConnectes+" clients connectés au serveur.");
    }
    
    /**
     * 
     * Méthode permettant d'envoyer un message à tous les clients
     * connectés sur le chat.
     * 
     * @param message
     * 				Message à transmettre aux autres clients connectés.
     * @param nom
     * 				Nom de l'émetteur du message pour affichage.
     */
    static void sendMessage(String message, String nom) {
    	ecrireClients("\t"+nom + " a dit : " + message);
    }
    
    /**
     * 
     * Retire le client 'nom' de la liste des clients connectés et informe
     * les autres de son départ.
     * 
     * @param nom
     * 			Nom du client quittant la conversation qui l'identifie de manière unique.
     * 
     * @see removeClient
     */
    static void quitConversation(String nom) {
    	
    	removeClient(nom);
    	ecrireClients("\t"+nom + " a quitté la conversation\n \t--------------------- \n ");
    }

    /**
     * 
     * Retire le client de la liste des clients
     * et décremente le nombre total de clients.
     * 
     * @param nom
     * 			Nom du client à retirer de la liste des clients.
     */
    static void removeClient(String nom)
    {
    	try {
    		//les flux et la socket ont été fermés dans le thread
			clients.remove(nom);
	    	nbClientsConnectes--;
	    	
	    	System.out.println(nom+" est parti de la conversation.");
	    	System.out.println(nbClientsConnectes + " clients connectés au serveur.");
		} catch (Exception e) {
			System.out.println("Erreur dans la fermeture de la connexion du client.");
		}
    }
    
}
