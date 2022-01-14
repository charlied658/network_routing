import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import java.net.*;
import java.io.*;
import java.util.*;

public class Server {




	public static boolean ServerType = true;
	private static ServerSocket ss;
	public static Thread segHandler;
	public static ListenThread lt;
	private static int socksr;
	private static int sockfd;
	private static int data_size;
	public static ObjectOutputStream output;
	public static ObjectInputStream input;
	public static int segmentsReceived = 0;
	public static int lengthOfData=100;
	public static char[] recvBuf;


		//private static int expect_seqNum;

		public static void main(String[] args) throws Exception {
			System.out.println("Server Ready");

			//call the startOverlay method, throw error if it returns -1
			//call the initSRTServer() method, throw error if it returns -1 //create a srt server sock at port 88 using the createSockSRTServer(88)
			//and assign to socksr, throw error if it returns -1
			//connect to srt client using acceptSRTServer(socksr), throw error if it
			//returns -1
			//for now, just use a Thread.sleep(10000) here
			//disconnect using closeSRTServer(sockfd), throw error if it returns -1 //finally, call stopOverlay(), throw error if it returns -1


			if (startOverlay() == -1) {
				throw new Exception();
			}
			System.out.println("startOverlay");

			/*
			if (initSRTServer() == -1) {
				throw new Exception();
			}
			System.out.println("initSRTServer");

			int socksr = createSRTServer(88);
			if (socksr == -1) {
				throw new Exception();
			}
			System.out.println("createSRTServer");
			*/
			rcvSRTServer();
			/*
			if (acceptSRTServer(socksr) == -1) {
				throw new Exception();
			}
			System.out.println("acceptSRTServer");

			//placeholder
			Thread.sleep(10000);


			if (closeSRTServer(sockfd) == -1) {
				throw new Exception();
			}
			System.out.println("closeSRTServer");

			if (stopOverlay() == -1) {
				throw new Exception();
			}
			System.out.println("stopOverlay");
			System.exit(0);
			*/

		}

		private static int startOverlay() throws IOException {

			//try {

			//create a server socket using ss = new ServerSocket(59090); and accept the client connection using s = ss.accept();

			recvBuf = new char[10000];
			/*
			ss = new ServerSocket(59090);
			Socket s = ss.accept();

			output = new ObjectOutputStream(s.getOutputStream());
			input = new ObjectInputStream(s.getInputStream());


			System.out.println("Connected");

			}
			catch(Exception e) {
				System.out.println("Exception: "+e);
				return -1;
			}
			return 0;
			*/

			NetworkNode.nodeInstance=4;

      //Connects to the node -1 at port 5555. Node -1 essentially acts as the master or supervisor node which shares the nexthop tables to all the nodes
      NetworkNode.createRegSocket(-1,7474);

			try{
	      //Get the port table, which allows each node to see the ports of the other nodes
	      NetworkNode.nodePortTable = (int[]) NetworkNode.prevNodeInput[0].readObject();
	      System.out.println("got portTable");

	      //Get the next hop table, which shows the current node which node to forward a packet to
	      NetworkNode.nodeNextHopTable = (int[]) NetworkNode.prevNodeInput[0].readObject();
	      System.out.println("got nextHopTable");
			}
			catch(Exception e){
				e.printStackTrace();
			}
      /*
      for(int i=0;i<5;i++){
        System.out.println("nextHopTable at index "+i+" is "+nextHopTable[i]);
      }
      */

      //Get the port from the port table
      NetworkNode.nodePort = NetworkNode.nodePortTable[NetworkNode.nodeInstance];

      System.out.println("Node: "+NetworkNode.nodeInstance);
      System.out.println("Port: "+NetworkNode.nodePort);
      System.out.println("Next hop: "+NetworkNode.nodeNextHopTable[4]);

      //Create a server socket
      NetworkNode.createServerSocket(NetworkNode.nodePort);

			NetworkNode.serverSocketAccept();


			return 1;
		}

		final private static int initSRTServer() {

			try {

			//This method initializes a TCB table containing TCBServer objects. Finally, the method starts the ListenThread to handle the incoming segments. There is only one Listen- Thread object for the server side which handles call connections for the client.
			//TCBtable = new TCB_Server[5]; //keep the name

			}

			catch (Exception e) {
			 return -1; }

			return 0;
			}
			/*
			private static int createSRTServer(final int sockfd) throws IOException {
				try {
					for(int i=0;i<TCBtable.length;i++){
			      if(TCBtable[i]==null){
			        //Create a new TCB_Client object and return the index of the object in the table
			        TCBtable[i]= new TCB_Server(sockfd);
			        return i;
			      }
			    }
					return -1;
				}
				catch (Exception e) {
					return -1;
				}
			}

		private static int acceptSRTServer(int sockfd) {

		try {
			lt = new ListenThread(1, sockfd);
			segHandler = new Thread(lt);
			segHandler.start();
			//This method gets the TCBServer entry using sockfd and changes the state of the connection to LISTENING. It then starts a timer to “busy wait” until the TCB entry’s state changes to CONNECTED (the ListenThread object does this when a SYN is received). It waits in an infinite loop for the state transition before proceeding and to return 1 when the state change happens, dropping out of the busy wait loop.
			TCBtable[sockfd].stateServer = TCB_Server.LISTENING; }
		catch (Exception e) {
			return -1;
		}
		return 0;}


		private static int closeSRTServer(int socksr) {
			//This method removes the TCB entry, obtained using socksr. It returns 1 if succeeded (i.e., was in the right state to complete a close) and -1 if fails (i.e., in the wrong state).
			while(TCBtable[socksr].stateServer != 1){
				try{
					Thread.sleep(1000);
				}
				catch(Exception e){}
				System.out.println("State: "+TCBtable[socksr].stateServer);
			}

			int getState = 0;

			if (TCBtable[socksr].stateServer == 1) {
				getState = 1;
			}

			else {
				getState = -1;
			}


			TCBtable[socksr] = null;
			return getState;

		}

		private static int stopOverlay() throws IOException {
			try {
				ss.close();
				input.close();
				output.close();
				//lt.exit();
				segHandler.interrupt();
				return 0; }

			catch (Exception e) {
				return -1;
			}
		}
		*/
		public static void rcvSRTServer() {

			//lt = new ListenThread(4);
			//lt.run();

			while(segmentsReceived<lengthOfData){
			//System.out.println("State: "+TCBtable[socksr].stateServer);
			try{
				Thread.sleep(100);
			}
			catch(Exception e){}
		}

		//Start reconstructing the data from recvBuf
		String reconstructedData="";
		for(int i=0;i<recvBuf.length;i++){
			if(recvBuf[i]==0){
				break;
			}
			reconstructedData+=recvBuf[i];
		}

		//Print out the received data
		System.out.println("Reconstructed Data: '"+reconstructedData+"'");

		}
		/*
		public static void sendDATAACK(int expect_seqNum) throws IOException {

			Segment s = new Segment(Segment.DATAACK, 0);
			output.writeObject(s);

		}
		*/

		public static void reconstructData(Segment s) throws ClassNotFoundException, IOException {
			//Segment s = (Segment) input.readObject();
			System.out.println("Received Segment " + s);

			System.out.println("Buffer: ");

			//Shows the Buffer
			for (int i = 0; i < recvBuf.length; i++) {
				System.out.print(recvBuf[i] + " ");
			}


		}

public Segment extractSegment(Packet p) {
	return p.getSegment();
}
/*
public int getNextNodeID(NetworkNode n) {

	return Main.g.getNextHopTables()[n.position][n.position + 1];
}


public NetworkNode getNextNode(NetworkNode n) {

	int id = getNextNodeID(n);

	for (int i = 0; i < Main.g.nodeTable.length; i++) {

		if (Main.g.nodeTable[i].nodeID == id) {
			return Main.g.nodeTable[i];
		}

	}

	throw new NullPointerException("Cannot find Node.");
}



public Segment CheckNodeGetData(NetworkNode n) {

	if (getNextNodeID(n) == Main.g.nodeTable[Main.g.nodeTable.length - 1].nodeID) {
		return extractSegment(getNextNode(n).packetToContain);
	}
	else {
		return null;
	}

}*/

}
