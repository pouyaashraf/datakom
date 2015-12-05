package datakom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class node implements Runnable{
	
	public static final int BUFFER_SIZE = 4096;
private int rank = -1;
//private Socket Clientsocket;
private DatagramSocket socket;
private ArrayList<SocketAddress> listOfSocket;
private ArrayList<String> stringListOfSocket;
private EditWindow editWindow;
private DatagramPacket packet;
private byte[] receiveBuffer;
private Semaphore reqLockSem;
private LockList firstLock;

/**
 * initialises a node object
 *
 * @param      port    The port number
 * @param      server  The IP-adress of the server
 */
public node(int port, DatagramSocket server){
	
	
	this.listOfSocket= new ArrayList<SocketAddress>();
	receiveBuffer = new byte[BUFFER_SIZE];
	this.editWindow = new EditWindow(this);
	reqLockSem = new Semaphore(1);
	this.firstLock = null;
	try {
		reqLockSem.acquire();
	} catch (InterruptedException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	editWindow.setVisible(true);
	try {
		this.socket = new DatagramSocket(port, InetAddress.getByName("127.0.0.1"));
		socket.setReuseAddress(true);
	
		if(server != null){
			String msg = "J"+this.socket.getLocalSocketAddress().toString();
			packet = new DatagramPacket(msg.getBytes(), msg.length(), server.getLocalSocketAddress());
			socket.send(packet);
			recieveList();
		}else{ //Om det �r den f�rsta noden
			Main.server = this.socket;
			this.rank = 0;
		}
		editWindow.setTitle("Rank " + rank);
	System.out.println("setup done: " + port);
	} catch (IOException e) {
		e.printStackTrace();
	}
}

public void run(){
	try {
		System.out.println("here "+ rank);
		String[] s;
		if(this.rank == 0){
			while(true){ 
			String input = getPacket();
			switch(input.charAt(0)) {
			case 'J':
		    	System.out.println("Server get: " + input);
		    	SocketAddress newNode = packet.getSocketAddress();
		    	
		    	for(int i = 0; i < listOfSocket.size(); i++ ) {
		    		putPacket("N" + newNode.toString(), listOfSocket.get(i));
		    	}
		    	sendList(newNode);
		    	listOfSocket.add(newNode);
				break;
			case 'L':
				s = input.substring(1).split(":");
				int nodeID = Integer.parseInt(s[0].trim());
				int offset = Integer.parseInt(s[1].trim());
				int length = Integer.parseInt(s[2].trim());
			  	
			  	if(this.firstLock == null){
			  		firstLock = new LockList(offset, length);
			  	}
			  	else{
			  		LockList l = this.firstLock;
			  		while(l.nextLockList != null && (l.getOffset() < offset)){
			  			l = l.getNext();
			  		}
			  		l = l.previousLockList;
			  		if(l.getOffset() + l.getLength() < offset && (l.nextLockList.getOffset() > offset+length)){
			  			new LockList(offset, length, l);
			  			packetsToAll(input);
			  		}
			  	}
			  	break;
			case 'M':
				s = input.substring(1).split(":");
				nodeID = Integer.parseInt(s[0].trim());
				offset = Integer.parseInt(s[1].trim());
				length = Integer.parseInt(s[2].trim());
				String txt = s[3];
				editWindow.updateText(txt, offset, offset + length);
		    	for(int i = 0; i < listOfSocket.size(); i++ ) {
		    		putPacket(input, listOfSocket.get(i));
		    	}
		    	LockList l = this.firstLock;
		  		while(l != null && (l.getOffset() != offset)){
		  			l = l.getNext();
		  		}
		  		int delta = l.getLength() - length;
		  		LockList afterL = l.nextLockList;
		  		
		  		while(afterL != null){
		  			afterL.setOffset(afterL.getOffset()-delta);
		  			afterL = afterL.nextLockList;
		  		}
		  		
		  		if (l.getOffset() == offset){
		  			l.remove();
		  		}
		    	
		    	break;
			}
			}
		}else{
			/*  client mode */
			while(true) {
			String input = getPacket();
			
			System.out.println("" + rank + ": " + input);
			switch(input.charAt(0)) {
			case 'N':
				s = input.substring(1).split(":");
				listOfSocket.add(new InetSocketAddress(s[0], Integer.parseInt(s[1].trim())));
				break;
			case 'L':
				s = input.substring(1).split(":");
				int nodeID = Integer.parseInt(s[0].trim());
				int offset = Integer.parseInt(s[1].trim());
				int length = Integer.parseInt(s[2].trim());
				System.out.println("My rank: " + rank + ", node: " + nodeID);
			  	if(nodeID == rank) {
			  		reqLockSem.release();
			  	}
			  	
			  	if(this.firstLock == null){
			  		firstLock = new LockList(offset, length);
			  	}
			  	else{
			  		LockList l = this.firstLock;
			  		while(l.nextLockList != null && (l.getOffset() < offset)){
			  			l = l.getNext();
			  		}
			  		l = l.previousLockList;
			  		if(l.getOffset() + l.getLength() < offset && (l.nextLockList.getOffset() > offset+length)){
			  			new LockList(offset, length, l);
			  			packetsToAll(input);
			  		}
			  	}
			  	break;
			case 'M':
				s = input.substring(1).split(":");
				nodeID = Integer.parseInt(s[0].trim());
				offset = Integer.parseInt(s[1].trim());
				length = Integer.parseInt(s[2].trim());
				String txt = s[3];
				editWindow.updateText(txt, offset, offset + length);
				LockList l = this.firstLock;
		  		while(l != null && (l.getOffset() != offset)){
		  			l = l.getNext();
		  		}
		  		int delta = l.getLength() - length;
		  		LockList afterL = l.nextLockList;
		  		
		  		while(afterL != null){
		  			afterL.setOffset(afterL.getOffset()-delta);
		  			afterL = afterL.nextLockList;
		  		}
		  		
		  		if (l.getOffset() == offset){
		  			l.remove();
		  		}
		    	
				break;
			}
			}
		}
	} catch (IOException e) {
		e.printStackTrace();
	}
	   
}

public void packetsToAll(String messageString) throws IOException {
	for(int i = 0; i < listOfSocket.size(); i++) {
		SocketAddress s = listOfSocket.get(i);
		putPacket(messageString, s);
	}
}

/**
 * Sends a list of connected nodes, as a string consisting of IP and port number
 *
 * @param      out   The outgoing socket.
 */
public void sendList(SocketAddress sock) throws IOException {
	putPacket("" + (listOfSocket.size() + 1), sock);
	for(int i = 0; i < listOfSocket.size(); i++) {
		SocketAddress s = listOfSocket.get(i);
		putPacket(s.toString(), sock);
	}
}

/**
 * receives a list of connected nodes in the network (IP and port number)
 *
 * @param      in           ingoing socket
 *
 * @throws     IOException  regular IOException
 */
public void recieveList() throws IOException {
	this.listOfSocket = new ArrayList<SocketAddress>();
	int x = Integer.parseInt(getPacket().trim());
	this.rank = x;
	System.out.println("My new rank is :" + this.rank);
	listOfSocket.add(packet.getSocketAddress());
	for (int i = 1; i < x; i++){
		String []s = getPacket().split(":");
		listOfSocket.add(new InetSocketAddress(s[0], Integer.parseInt(s[1].trim())));
	}
	

}

private void putPacket(String str, SocketAddress dest) throws IOException {
	System.out.println(str);
	packet = new DatagramPacket(str.getBytes(), str.length(), dest);
	socket.send(packet);
}

private String getPacket() throws IOException {
	receiveBuffer = new byte[BUFFER_SIZE];
	packet = new DatagramPacket(receiveBuffer, BUFFER_SIZE);
	socket.receive(packet);
	return new String(receiveBuffer);
}

public void requestLock(int offset, int length) throws IOException {
	if(rank == 0) {
		
	} else {
		String msg = "L" + rank + ":" + offset + ":" + length;
		putPacket(msg,listOfSocket.get(0));
		try {
			reqLockSem.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	
}

public void requestChange(String str, int offset, int length) throws IOException {
	if(rank == 0) {
		
	} else {
		String msg = "M" + rank + ":" + offset + ":" + length + ":" + str;
		putPacket(msg,listOfSocket.get(0));
	}

}


private class LockList {
	
	private LockList nextLockList;
	private LockList previousLockList;
	private int offset;
	private int length;
	
	LockList(int offset, int length){
		this.nextLockList = null;
		this.previousLockList = null;
		this.offset = offset;
		this.length = length;	
	}
	
	LockList(int offset, int length, LockList previous){
		this.nextLockList = previous.nextLockList;
		this.previousLockList = previous;
		this.offset = offset;
		this.length = length;
		previous.nextLockList = this;
	}
	
	public void remove(){
		this.previousLockList.nextLockList = this.nextLockList;
		this.nextLockList.previousLockList = this.previousLockList;
	}
	
	public int getOffset(){
		return this.offset;
	}
	
	public int getLength(){
		return this.length;
	}
	
	public LockList getNext(){
		return this.nextLockList;
	}
	
	public LockList getPrevious(){
		return this.previousLockList;
	}
	
	public void setOffset(int offset){
		this.offset = offset;
	}
	
	public void setLength(int length){
		this.length = length;
	}
}

}


