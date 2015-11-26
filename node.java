package datacom21;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class node {
private int rank = -1;
private Socket Clientsocket;
private ServerSocket socket;
private ArrayList<Socket> listOfSocket;

public node(int port, Socket server){
	this.listOfSocket = new ArrayList<Socket>();
	try {
		this.socket = new ServerSocket(port);
		
		if(server != null){
		this.listOfSocket.add(server);
	
		}else{ //Om det är den första noden
			this.Clientsocket = this.socket.accept();
			this.listOfSocket.add(this.Clientsocket);
			this.rank = 0;
		}
		eventloop();
	} catch (IOException e) {
		e.printStackTrace();
	}
}

private void eventloop(){
	try {
		this.Clientsocket = socket.accept();
			PrintWriter out = new PrintWriter( Clientsocket.getOutputStream(), true);
			PrintWriter toServer = new PrintWriter( this.listOfSocket.get(0).getOutputStream(), true);
		    BufferedReader in = new BufferedReader(new InputStreamReader(Clientsocket.getInputStream()));
		    if(this.rank == 0){
		    	System.out.print("test");
		    	 while (in.readLine() != null){
				    	System.out.print("Server get: " + in.readLine());
				    	break;
				    }
		    	 socket.close();
		    }
		    toServer.println("(setup)"+this.socket.toString());
		    socket.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	   
}
}
