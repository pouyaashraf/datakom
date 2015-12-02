package datakom;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
	//public static ArrayList<RunNode> nodes;
	public static ServerSocket server = null;
	
	public static void main(String[] args) {
		//nodes = new ArrayList<RunNode>();
		int port = 1549;
		ExecutorService executor = Executors.newFixedThreadPool(5);
		for (int i = 0; i < 3; i++) {
			creatNode(port + i, executor);
		}
		
		//executor.execute(worker);
	  	
	      executor.shutdown();
	      while (!executor.isTerminated()) {
	      }
	      System.out.println("Finished all threads");

	}

	private static void creatNode(int port, ExecutorService exec) {
		        
		       // for (int i = 0; i < 2; i++) {
		        	//System.out.println("i="+i);
		        	
		        		  Runnable worker = new node(port, server);
		        		  exec.execute(worker);
		        		  
		          		  
		
		//}
	}
		
	  
	
}
