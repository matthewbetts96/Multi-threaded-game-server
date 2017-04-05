import java.io.*;
import java.net.*;
/*
	This file is what each of the clients run to start playing. If
	a server is running already it will connect (a new thread is created
	for each of the clients, see ServerThread.java) As long as the client
	enters anything that is not null, it will compute what the client says.
	It also prints out any output that it recieves from the variable "theOutput"
	from the Game.java file.
*/
public class Client {
	public static void main(String[] args) throws IOException {
        
        String server;
        String user;

        if (args.length != 2) {
            System.out.println("How to use: java Client <host name> <port number>");
            System.exit(1);
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);

        try (
            Socket clientSocket = new Socket(host, port);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        	) {
            BufferedReader buffIn = new BufferedReader(new InputStreamReader(System.in));
            while ((server = in.readLine()) != null) {
                System.out.println(server);
                user = buffIn.readLine();
                if (user != null) {
                    out.println(user);
                }
            }     
        } catch (UnknownHostException e) {
            System.out.println("I cannot find " + host);
            System.exit(1);
        } catch (IOException e) {
            System.out.println("Lost I/O connection to " + host + " or couldn't the find server.");
            System.exit(1);
        }
    }
}