import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.scene.control.ListView;
/*
 * Clicker: A: I really get it    B: No idea what you are talking about
 * C: kind of following
 */

public class Server{

	int count = 1;	
	ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
	TheServer server;
	private Consumer<Serializable> callback;
	
	
	Server(Consumer<Serializable> call){
	
		callback = call;
		server = new TheServer();
		server.start();
	}
	
	
	public class TheServer extends Thread{
		
		public void run() {
		
			try(ServerSocket mysocket = new ServerSocket(5555);){
		    System.out.println("Server is waiting for a client!");
		  
			
		    while(true) {
		
				ClientThread c = new ClientThread(mysocket.accept(), count);
				callback.accept("client has connected to server: " + "client #" + count);
				clients.add(c);
				c.start();
				
				count++;
				
			    }
			}//end of try
				catch(Exception e) {
					callback.accept("Server socket did not launch");
				}
			}//end of while
		}
	

		class ClientThread extends Thread{
			
		
			Socket connection;
			int count;
			ObjectInputStream in;
			ObjectOutputStream out;
			
			ClientThread(Socket s, int count){
				this.connection = s;
				this.count = count;	
			}
			
			public void updateClients(String message) {
				StorageInfo x = new StorageInfo();
				
				if (message.contains("new client") || message.contains("left the server")) {
					x.isUpdate = true;
					for (int i =0; i < clients.size();i++) {
						synchronized(x) {
						ClientThread s = clients.get(i);
						x.names.add("client #" + s.count);
					}
					}
				} else {
					x.isUpdate = false;
					x.message = message;
				}
				

				for(int i = 0; i < clients.size(); i++) {
					ClientThread t = clients.get(i);
					synchronized(x) {
					try {
					 x.clientName = "client #" + t.count;
					 t.out.writeObject(x);
					}
					catch(Exception e) {}
					}
				}
			}
			
			public void updateMessage(StorageInfo data) {

				if (data.msgRecipients.contains(0)) {
					for (int i =0; i < clients.size(); i++) {
						ClientThread t = clients.get(i);
						try {
							t.out.writeObject(data);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} 
				} else {
					for (int i =0; i < clients.size(); i++) {
						ClientThread t = clients.get(i);
						
						if (data.msgRecipients.contains(t.count)) {
							try {
								t.out.writeObject(data);
								t.out.reset();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						
					}
				}
				
				
			}
			
			public void run(){
					
				try {
					in = new ObjectInputStream(connection.getInputStream());
					out = new ObjectOutputStream(connection.getOutputStream());
					connection.setTcpNoDelay(true);	
				}
				catch(Exception e) {
					System.out.println("Streams not open");
				}
				
				callback.accept("new client on server: client #"+count);
				updateClients("new client on server: client #"+count);
					
				 while(true) {
					    try {
					    	StorageInfo data = (StorageInfo) in.readObject();
					    	synchronized(data) {
					    	callback.accept("client: " + count + " sent: " + data.message);
					    	
					    	updateMessage(data);
					    	}
					    }
					    catch(Exception e) {
					    	callback.accept("OOOOPPs...Something wrong with the socket from client: " + count + "....closing down!");
					    	callback.accept("dropped: client #"+count);
					    	clients.remove(this);
					    	updateClients("Client #"+count+" has left the server!");
					    	
					    	break;
					    }
					}
				}//end of run
			
			
		}//end of client thread
}


	
	

	
