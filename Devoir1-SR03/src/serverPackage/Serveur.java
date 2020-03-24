package serverPackage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import serverPackage.ServerThread;

import java.net.ServerSocket;
import java.net.Socket;

//Classe Serveur principale : receptionne les connexions et reparti les connexions sur des threads
public class Serveur {

    protected static Hashtable<String,ServerThread> clients = new Hashtable<String,ServerThread>();
    protected static int nbClientsConnectes = 0;
    
    public static void main(String[] args) throws IOException {
    	ServerSocket conn = null;
    	
    	try{
    		conn = new ServerSocket(10080) ;
    	} catch(IOException e){
    		e.printStackTrace();
        }
	
        while(true){
      
        	try {
        		Socket comm = conn.accept();
            	nbClientsConnectes++;

            	DataOutputStream out =new DataOutputStream(comm.getOutputStream());
                DataInputStream in =new DataInputStream(comm.getInputStream());
                
                String name = in.readUTF();
                if(clients.containsKey(name)) {
                	System.out.println("Ce nom est deja pris, le client n'a pas ete cree");
                	comm.close();
                }
                
                System.out.println("New client, starting a new thread...");
                
                ServerThread client = new ServerThread(comm, name);
    			Serveur.joinConversation(name, client);
                client.start();
            
        	 } catch (IOException e) {
        		conn.close();
             	e.printStackTrace();
             }
        }
    }
    
    static void joinConversation(String name, ServerThread s) {
    	
    	clients.put(name,s);
    	
    	Set keys = clients.keySet();
    	Iterator itr = keys.iterator();
    	String key="";
    	
    	while (itr.hasNext()) {
    		
    		key=(String) itr.next();
    		try {
				clients.get(key).getOutput().writeUTF("\t"+name + " a rejoint la conversation\n \t--------------------- \n ");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    	}
    }
    
    static void sendMessage(String message, String nom) {
    	
    	Set keys = clients.keySet();
    	Iterator itr = keys.iterator();
    	String key="";
    	
    	while (itr.hasNext()) {
    		
    		key=(String) itr.next();
    		try {
    			clients.get(key).getOutput().writeUTF("\t"+nom + " a dit : " + message);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    	}
    }
    

    static void quitConversation(String nom) {
    	
    	clients.remove(nom);
    	nbClientsConnectes--;

    	Set keys = clients.keySet();
    	Iterator itr = keys.iterator();
    	String key="";
    	
    	while (itr.hasNext()) {
    		
    		key=(String) itr.next();
    		try {
    			clients.get(key).getOutput().writeUTF("\t"+nom + " a quitte la conversation\n \t--------------------- \n ");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    	}
    }
}
