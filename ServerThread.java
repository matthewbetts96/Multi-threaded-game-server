import java.io.*;
import java.net.*;
/*
	This file creates in BufferedReader and out PrintWriter which are
	needed to collect what the user inputs and display the correct outputs
	on the screen. Every time a client connects it creates a new instance of 
	tokenGame and then constantly listens for what the client has to say. It 
	also prints out any output that it recieves.
*/
public class ServerThread extends Thread{

	private Socket client = null;

	public void run(){
		String inputText;
		String outputText;
		try(
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter out = new PrintWriter(client.getOutputStream(), true);
            ){
				System.out.println("A client has connected to the server.");
				Game tokenGame = new Game();
            	outputText = tokenGame.computeGame(null);
            	out.println(outputText);
	            while ((inputText = in.readLine()) != null) {
	                outputText = tokenGame.computeGame(inputText);
	                out.println(outputText);
	            } 
	            client.close();
        } catch (IOException e) {
            System.out.println("Player has disconnected from the server.");
        }  
	}

	public ServerThread(Socket client) {
	    super("ServerThread");
		this.client = client;
    }

}