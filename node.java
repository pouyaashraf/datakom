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
    private boolean gotLock;
    
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
	    }

	    else{ //Om det �r den f�rsta noden
		Main.server = this.socket;
		this.rank = 0;
		editWindow.setVisible(false);
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
				/*SERVER MODE*/
				while(true){
					debug("Wait on packet");
				    String input = getPacket();
			    	//System.out.println("Server get: " + input);
				    switch(input.charAt(0)) {
				    case 'J':
				    	SocketAddress newNode = packet.getSocketAddress();
				    	editWindow.addNode(newNode);
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
						
						//System.out.println("Lock request: " + nodeID + "," + offset + "," + length);
						if(this.firstLock == null){
						    firstLock = new LockList(offset, length);
						    editWindow.addLock(firstLock);
					    	packetsToAll(input);
						}
						else{
						    LockList l = LockList.findFirstAfter(this.firstLock, offset);
						    if(l != null){
						    	if(l.getPrevious() == null) {
						    		/* first entry has a higher offset, so insert before it */
						    		if((offset + length) < l.getOffset()) {
						    			/* ... assuming this lock ends before next starts */
						    			firstLock = new LockList(offset, length, firstLock);
						    			editWindow.addLock(firstLock);
						    			packetsToAll(input);
						    		}
						    		else {
						    			putPacket("E", listOfSocket.get(nodeID - 1));
						    		}
						    	} else {
						    		if((l.getPrevious().getOffset() + l.getPrevious().getLength()) < offset &&
						    			((offset + length) < l.getOffset())) {
						    			editWindow.addLock(new LockList(offset, length, l));
						    			packetsToAll(input);
						    		}
						    		else{
						    			putPacket("E", listOfSocket.get(nodeID - 1));
						    		}
						    	}
						    }
						    else{
						    	putPacket("E", listOfSocket.get(nodeID - 1));
						    }
						}
						break;
				    case 'M':
				    	s = input.substring(1).split(":");
				    	nodeID = Integer.parseInt(s[0].trim());
				    	offset = Integer.parseInt(s[1].trim());
				    	length = Integer.parseInt(s[2].trim());
				    	String txt = s[3];
				    	LockList l = LockList.findByOffset(firstLock, offset);
		
				    	if(l != null) {
				    		editWindow.updateText(txt, offset, offset + length);
				        	for(int i = 0; i < listOfSocket.size(); i++ ) {
				        		putPacket(input, listOfSocket.get(i));
				        	}
		
				        	int delta = l.getLength() - length;
				        	LockList afterL = l.getNext();
				  	
				        	while(afterL != null){
				        		afterL.setOffset(afterL.getOffset()-delta);
				        		afterL = afterL.getNext();
				        	}
				  	
				        	if(l == firstLock) {
				        		firstLock = l.getNext();
				        	}
				        	l.remove();
				    	}
				    	else {
				    		debug("Error: no lock found for M");
				    	}
				    	
				    	break;
				    }
				}
		    }else{
				/*  CLIENT MODE */
				while(true) {
					debug("Wait on packet");
					String input = getPacket();
				    
				    //System.out.println("" + rank + ": " + input);
				    switch(input.charAt(0)) {
				    case 'N':
				    	s = input.substring(1).split(":");
				    	SocketAddress sa = new InetSocketAddress(s[0], Integer.parseInt(s[1].trim()));
				    	listOfSocket.add(sa);
				    	editWindow.addNode(sa);
				    	break;
				    case 'L':
				    	s = input.substring(1).split(":");
				    	int nodeID = Integer.parseInt(s[0].trim());
				    	int offset = Integer.parseInt(s[1].trim());
				    	int length = Integer.parseInt(s[2].trim());
				    	//System.out.println("My rank: " + rank + ", node: " + nodeID);
				    	if(nodeID == rank) {
				    		gotLock = true;
						    reqLockSem.release();
						}
						
						if(this.firstLock == null){
						    firstLock = new LockList(offset, length);
						    editWindow.addLock(firstLock);
						}
						else{
						    LockList l = LockList.findFirstAfter(this.firstLock, offset);
						    if(l.getPrevious() == null) {
						    	/* first entry has a higher offset, so insert before it */
						    	if((offset + length) < l.getOffset()) {
						    		/* ... assuming this lock ends before next starts */
						    		firstLock = new LockList(offset, length, firstLock);
						    		editWindow.addLock(firstLock);
						    	}
						    } else {
						    	if((l.getPrevious().getOffset() + l.getPrevious().getLength()) < offset &&
						    		((offset + length) < l.getOffset())) {
						    		editWindow.addLock(new LockList(offset, length, l));
						    	}
						    }
						}
						break;
				    case 'E':
				    	this.editWindow.setStatus("Could not aquire lock for region");
				    	gotLock = false;
				    	reqLockSem.release();
				    	break;
				    case 'M':
				    	s = input.substring(1).split(":");
						nodeID = Integer.parseInt(s[0].trim());
						offset = Integer.parseInt(s[1].trim());
						length = Integer.parseInt(s[2].trim());
						String txt = s[3];
						
						LockList l = LockList.findByOffset(firstLock, offset);
						editWindow.updateText(txt, offset, offset + l.getLength());
		
			        	int delta = l.getLength() - length;
			        	LockList afterL = l.getNext();
			  	
			        	while(afterL != null){
			        		afterL.setOffset(afterL.getOffset()-delta);
			        		afterL = afterL.getNext();
			        	}
			  	
			        	if(l == firstLock) {
			        		firstLock = l.getNext();
			        	}
			        	l.remove();
			        	break;
					}
				    	
					
			    }
			}
		} catch (IOException e) {
		    e.printStackTrace();
		}	
    }
    
    /**
     * sends a list of connected sockets to a specified node
     *
     * @param      sock         node to sent list to
     *
     * @throws     IOException  exceptional exception.
     */
    public void sendList(SocketAddress sock) throws IOException {
		putPacket("" + (listOfSocket.size() + 1), sock);
		for(int i = 0; i < listOfSocket.size(); i++) {
		    SocketAddress s = listOfSocket.get(i);
		    putPacket(s.toString(), sock);
		}
    }
    
    /**
     * recieves a list of sockets and adds them to own list of sockets.
     *
     * @throws     IOException  exceptional exception.
     */
    public void recieveList() throws IOException {
		this.listOfSocket = new ArrayList<SocketAddress>();
		int x = Integer.parseInt(getPacket().trim());
		this.rank = x;
		System.out.println("My new rank is :" + this.rank);
		listOfSocket.add(packet.getSocketAddress());
		editWindow.addNode(packet.getSocketAddress());
		for (int i = 1; i < x; i++){
		    String []s = getPacket().split(":");
	    	SocketAddress sa = new InetSocketAddress(s[0], Integer.parseInt(s[1].trim()));
	    	listOfSocket.add(sa);
	    	editWindow.addNode(sa);
		}
    }

    /**
     * receives a datagram packet
     *
     * @return     the received data represented as a string.
     *
     * @throws     IOException  exceptional exception
     */
    private String getPacket() throws IOException {
    	receiveBuffer = new byte[BUFFER_SIZE];
    	packet = new DatagramPacket(receiveBuffer, BUFFER_SIZE);
    	socket.receive(packet);
    	String s = new String(receiveBuffer).trim();
    	debug(s);
    	return s;
    }
     
    /**
     * sends a packet of information to a specified node in the network
     *
     * @throws     IOException  exceptional exception
     */
    private void putPacket(String str, SocketAddress dest) throws IOException {
    	debug("putPacket(" + str + "," + dest + ")");
    	packet = new DatagramPacket(str.getBytes(), str.length(), dest);
    	socket.send(packet);
    }
    
    /**
     * sends a packet of information to all nodes in the list of connected nodes
     *
     * @param      messageString  message to send
     *
     * @throws     IOException    exceptional exception
     */
    public void packetsToAll(String messageString) throws IOException {
		for(int i = 0; i < listOfSocket.size(); i++) {
		    SocketAddress s = listOfSocket.get(i);
		    putPacket(messageString, s);
		}
	}

	/**
	 * requests a lock to a specified region of the text buffer
	 *
	 * @param      offset       where the lock should begin
	 * @param      length       length of the lock (in characters)
	 *
	 * @throws     IOException  exceptional exception
	 */
    public boolean requestLock(int offset, int length) throws IOException {
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
    	return gotLock;
    }
    
    /**
     * sends a request to the server to change the contents of the buffer being edited
     * (effectively pushing the changes made to the buffer to the server and all connected nodes.)
     *
     * @param      str          message string
     * @param      offset       offset where the string begins
     * @param      length       length of the string
     *
     * @throws     IOException  exceptional exception.
     */
    public void requestChange(String str, int offset, int length) throws IOException {
    	if(rank == 0) {
	    
    	} else {
    		String msg = "M" + rank + ":" + offset + ":" + length + ":" + str;
    		putPacket(msg,listOfSocket.get(0));
    	}
    }
    
    /**
     * prints a text string, prepending the rank of the node
     * 
     *  @param	str			message string
     *  
     */
    public void debug(String msg) {
    	System.out.println(rank + ": " + msg);
    }
    

}