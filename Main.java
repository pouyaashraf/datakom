package datacom21;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Main {
	public static void main(String[] args) {
		int port = 1637;
		ceratNode(port, null);
		try {
			ceratNode(port+1, new Socket("127.0.0.1", port));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void ceratNode(int port, Socket s) {
	
		   Thread loop = new Thread()
		   {
		      public void run()
		      {
		      System.out.println("h:" + port);
		        	node b = new node(port, s);
		       
		      }
		   };
		   loop.start();
		}
		
	
}
