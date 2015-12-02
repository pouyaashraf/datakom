package datakom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class node implements Runnable{
private int rank = -1;
private Socket Clientsocket;
private ServerSocket socket;
private ArrayList<Socket> listOfSocket;
private ArrayList<String> stringListOfSocket;

public node(int port, ServerSocket server){
	
	
	this.listOfSocket= new ArrayList<Socket>();
	try {
		this.socket = new ServerSocket(port);
		socket.setReuseAddress(true);
	
		if(server != null){
		Clientsocket = new Socket(server.getInetAddress(), server.getLocalPort());
		}else{ //Om det �r den f�rsta noden
			Main.server = this.socket;
//			System.out.println("xcaa");
			//this.listOfSocket.add(this.Clientsocket);
			this.rank = 0;
		}
	System.out.println("setup done: " + port);
	} catch (IOException e) {
		e.printStackTrace();
	}
}

public void run(){
	try {
		System.out.println("here "+ rank);
		if(this.rank == 0){
			while(true){ 
			this.Clientsocket = socket.accept();
			this.listOfSocket.add(this.Clientsocket);
			PrintWriter out = new PrintWriter( this.Clientsocket.getOutputStream(), true);
		    BufferedReader in = new BufferedReader(new InputStreamReader(Clientsocket.getInputStream()));
		    
			//System.out.print("test322");
	    	String input;
	  
	    	 input= in.readLine();
			    	System.out.println("Server get: " + input);
	    	 
			 sendList(out);
	    	 Clientsocket.close();
			}
		}else{
			//PrintWriter out = new PrintWriter( Clientsocket.getOutputStream(), true);
			PrintWriter toServer = new PrintWriter( this.Clientsocket.getOutputStream(), true);
		    BufferedReader in = new BufferedReader(new InputStreamReader(Clientsocket.getInputStream()));
		    
		    toServer.println("(setup)"+this.socket.toString());
		    recieveList(in);
		    
		    Clientsocket.close();
		}
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	   
}

public void sendList(PrintWriter out ){
	out.println(listOfSocket.size());
	for(int i = 0; i < listOfSocket.size(); i++) {
		Socket s = listOfSocket.get(i);
		out.println(s.getInetAddress() + "," + s.getPort());
	}
}
public void recieveList(BufferedReader in) throws IOException {
	this.stringListOfSocket = new ArrayList<String>();
	int x = Integer.parseInt(in.readLine());
	this.rank = x;
	System.out.println("My new rank is :" + this.rank);
	for (int i = 0; i < x; i++){
		String s = in.readLine();
		this.stringListOfSocket.add(s);
		//System.out.println(s);
	}
	

}
}
