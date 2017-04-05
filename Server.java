import java.net.*;
import java.io.*;
/*
	This file creates the new serversocket, accepts clients continuously and passes them	
	to the run() method in ServerThread.java. 
*/

public class Server {
	public static void main(String[] args) throws IOException {
        boolean acceptingClients = true;
		if (args.length != 1) {
       		System.out.println("How to use: java Server <the port number>");
        	System.exit(1);
    	}
    	int port = Integer.parseInt(args[0]);
    	try(ServerSocket server = new ServerSocket(port)){
    		while(acceptingClients){
    			new ServerThread(server.accept()).start();
    		}
    	} catch (IOException e) {
            System.out.println("A client has disconnected.");
        }  
	}
}