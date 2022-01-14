//Author: Charlie Davidson
//CS327 - Networks
//April 28, 2021

import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.*;
public class Client{

  //Initialize final variables
  static final int SYN_TIMEOUT=100;
  static final int SYN_MAX_RETRY=5;
  static final int FIN_TIMEOUT=100;
  static final int FIN_MAX_RETRY=5;

  //Initialize public variables (will be used in multiple methods and/or classes)
  public static ListenThread listenThread;
  public static int retryCount;
  public static Thread segHandler;
  public static Segment segment;
  public static Socket clientSocket;
  public static ObjectOutputStream output;
  public static ObjectInputStream input;
  public static int currentTime;
  public static int segmentsSent=0;
  public static LinkedList<Segment> sendBuffer;

  public static String data = "The quick brown fox jumps over the lazy dog. Random data: 17&gJO34_**$%h38hdd312joooo3oo3218797";
  static final int MAX_DATA_LEN=5;

  public static void main(String[] args){

    //Wrap everything in a try/catch statement
    try{
      //Run each method, testing if it returns -1. If it does, throw an exception

      //Most methods were removed except for startOverlay and sendSRTClient
      if (startOverlay()==-1){
        throw new Exception();
      }
      System.out.println("startOverlay");

      /*
      if (initSRTClient()==-1){
        throw new Exception();
      }
      System.out.println("initSRTClient");
      int socksr = createSockSRTClient(87);
      if (socksr==-1){
        throw new Exception();
      }
      System.out.println("createSockSRTClient");
      if (connectSRTClient(socksr,88)==-1){
        throw new Exception();
      }
      System.out.println("connectSRTClient");
      //Thread.sleep(10000);
      */


      if (sendSRTClient()==-1){
        throw new Exception();
      }

      System.out.println("sendSRTClient");


      /*
      if (disconnSRTClient(socksr)==-1){
        throw new Exception();
      }

      System.out.println("disconnSRTClient");

      if (closeSRTClient(socksr)==-1){
        throw new Exception();
      }
      System.out.println("closeSRTClient");

      if (stopOverlay()==-1){
        throw new Exception();
      }
      System.out.println("stopOverlay");
      */

      /*
      while(segHandler.isAlive()){
        //Debugging - trying to determine why the thread stays alive even after repeated calling of interrupt()
        System.out.println("Client thread alive");
        Thread.sleep(1000);
        segHandler.interrupt();

      }*/
    } catch (Exception e){
      //Exception handling
      //System.out.println("Exception: "+e);
      e.printStackTrace();
    }
  }

  //Modified startOverlay which essentially acts as node 0
  public static int startOverlay(){
    try{
      //Initialize the sendBuffer
      sendBuffer = new LinkedList<Segment>();

      NetworkNode.nodeInstance=0;

      //Connects to the node -1 at port 5555. Node -1 essentially acts as the master or supervisor node which shares the nexthop tables to all the nodes
      NetworkNode.createRegSocket(-1,7474);

      //Get the port table, which allows each node to see the ports of the other nodes
      NetworkNode.nodePortTable = (int[]) NetworkNode.prevNodeInput[0].readObject();
      System.out.println("got portTable");

      //Get the next hop table, which shows the current node which node to forward a packet to
      NetworkNode.nodeNextHopTable = (int[]) NetworkNode.prevNodeInput[0].readObject();
      System.out.println("got nextHopTable");
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

      //Connect to node 1
      NetworkNode.createRegSocket(1,NetworkNode.nodePortTable[1]);

      /*
      listenThread = new ListenThread(2,0);
      segHandler = new Thread(listenThread);
      segHandler.start();
      */

      //Return 1 if successful
      return 1;
    }
    catch(Exception e){
      //If an exception was caught, print the result (for debugging) and return -1
      //System.out.println("Exception: "+e);
      e.printStackTrace();
      return -1;
    }
  }
  /*
  //Initialize the TCB table and start the ListenThread
  public static int initSRTClient(){
    //Create a TCB table of length 5 (arbitrary)
    table = new TCB_Client[5];
    for(int i=0;i<5;i++){
      //Set each entry to null
      table[i]=null;
    }
    //Create and start the ListenThread
    listenThread = new ListenThread(0,0);
    segHandler = new Thread(listenThread);
    segHandler.start();
    return 1;
  }

  //Create the entry in the TCB table
  public static int createSockSRTClient(int client_port){
    //Search through table until a null element is found
    for(int i=0;i<table.length;i++){
      if(table[i]==null){
        //Create a new TCB_Client object and return the index of the object in the table
        table[i]= new TCB_Client(client_port);
        return i;
      }
    }
    //If no null elements were found, return -1
    return -1;
  }

  //Move to SYNSENT phase, begin sending SYN's
  public static int connectSRTClient(int socksr, int server_port){
    try {
      //Assign the server port number
      table[socksr].portNumServer=server_port;
      //Create a new SYN segment
      Segment syn = new Segment(0);
      retryCount=0;

      //Create a new TimerTask which will send the SYN repeatedly
      final Timer timer = new Timer();
      final TimerTask task = new TimerTask(){
        public void run(){
          try{
            //Write the SYN to the OutputStream
            //output = new ObjectOutputStream(clientSocket.getOutputStream());
            output.writeObject(syn);
            System.out.println("Sent SYN");
            //output.flush();
            //System.out.println(retryCount);
            //System.out.println(SYN_MAX_RETRY);
            //Increment the retryCount variable each time
            Client.retryCount+=1;
          }
          catch(Exception e){
            e.printStackTrace();
          }
        };
      };
      //Set the timer to send a SYN once every SYN_TIMEOUT milliseconds
      timer.schedule(task,0,SYN_TIMEOUT);
      //Set the state to 2, SYNSENT
      table[socksr].stateClient=2;

      //Keep sending the SYN until the maximum number of retries is met or exceeded
      while(retryCount<SYN_MAX_RETRY){
        //System.out.println("while");
        //If the thread is dead
        if(!segHandler.isAlive()){
          //and the ListenThread received a segment of type 1, SYNACK
          //System.out.println("segHandler is dead");
          if(segment.type==1){
            //Cancel the timer
            timer.cancel();
            System.out.println("Received SYNACK");
            //Change the state to 3, CONNECTED
            table[socksr].stateClient=3;

            //Restart the ListenThread and return out of the method
            segHandler = new Thread(listenThread);
            segHandler.start();
            return 1;
          }
          else{
            //If ListenThread found a Segment which wasn't a SYNACK, start the Thread up again
            segHandler = new Thread(listenThread);
            segHandler.start();
          }
        }
      }
      //System.out.println("test");
      timer.cancel();
      //If the number of retrys exceedd SYN_MAX_RETRY, change state to 1, CLOSED, and return -1
      table[socksr].stateClient=1;
      return -1;
    }
    catch (Exception e){
      e.printStackTrace();
      //System.out.println("Exception: "+e);
      return -1;
    }
  }
  */
  public static int sendSRTClient(){
    String subdata;
    int subdataLength;
    int subdataIndex=0;
    char[] charArray;
    //Determine the number of segments to be sent
    int dataLength = (int)Math.ceil(data.length()/5);

    //Print the data before dividing up into segments (for debugging)
    System.out.println("Data to send: '"+data+"'");

    //Split the data up into chunks of 5 characters each
    for(int i=0;i<data.length();i+=5){

      charArray = new char[5];
      if(i+5<data.length()){
        //Get the next 5 characters
        System.out.println(data.substring(i,i+5));
        subdata=data.substring(i,i+5);
        subdataLength=5;
        for(int j=0;j<5;j++){
          //Split the 5 characters into the charArray
          charArray[j]=data.charAt(i+j);
        }
      }
      else{
        //If the for loop reaches the end of the data String and cannot form a clean 5 character long segment
        System.out.println(data.substring(i));
        subdata=data.substring(i);
        subdataLength=data.length()-1;
        for(int j=0;i+j<data.length();j++){
          charArray[j]=data.charAt(i+j);
        }
      }

      //Create a new Segment with the charArray data
      Segment subdataSegment = new Segment(subdataIndex,dataLength,charArray);
      System.out.println("subdataIndex: "+subdataIndex+", dataLength: "+dataLength+", charArray: "+charArray);

      //Add the segment to the sendBuffer
      sendBuffer.add(subdataSegment);
      /*
      String toPrint="[";
      for(int j=0;j<5;j++){
        toPrint+="'";
        toPrint+=charArray[j];
        toPrint+="',";
      }
      System.out.println(toPrint);
      */
      subdataIndex+=1;
    }

    //Set the current time (to calculate sentTime in SendBufferNode)
    int currentTime = (int)System.currentTimeMillis();

    //While there are still segments to send
    while(segmentsSent<dataLength){

      try{

        for(int i=0;i<sendBuffer.size();i++){
          Segment tempSeg = sendBuffer.get(i);

          //Wrap each segment in a packet with source 0 and destination 4
          Packet packet = new Packet(0,4,tempSeg);

          //Send the packet to node 1 (happens to be at connection slot 1)
          NetworkNode.prevNodeOutput[1].writeObject(packet);

          //Increment segmentsSent
          segmentsSent+=1;
        }
      }
      catch(Exception e){
        e.printStackTrace();
      }
      /*
      Packet finalPacket = new Packet(0,-1,new Segment());
      NetworkNode.clientOutput[1].writeObject(finalPacket);
      */

      /*
      //System.out.println("State: "+TCBtable[socksr].stateServer);
      if(!segHandler.isAlive()){
        //System.out.println("Segment type: "+segment.type);
        if(segment.type==5){
          //If ListenThread received a segment of type 5, DATAACK
          //timer.cancel();
          System.out.println("Received DATAACK");
          //Debugging
          System.out.println("seq_num: "+segment.seq_num+", segmentsSent: "+segmentsSent);

          //Call the ack() function in sendbuffernode, which changes the state to 2 (see more on state in SendBufferNode)
          table[socksr].sendBuffer.get(segment.seq_num-segmentsSent).ack();
          segmentsSent+=1;

          //Start the listenthread up again
          listenThread = new ListenThread(0,0);
          segHandler = new Thread(listenThread);
          segHandler.start();
        }
        else{
          //If ListenThread did not find a DATAACK, restart ListenThread
          listenThread = new ListenThread(0,0);
          segHandler = new Thread(listenThread);
          segHandler.start();
        }
      }

      int firstUnACKedIndex=-1;

      //Determine the first unACKed element
      for(int i=0;i<TCB_Client.GBN_WINDOW && i<table[socksr].sendBuffer.size();i++){
        if(table[socksr].sendBuffer.get(i).state==2){
          firstUnACKedIndex=i;
        }
        else{
          break;
        }
      }

      //Remove segments which have already been ACKED
      for(int i=0;i<=firstUnACKedIndex;i++){
        table[socksr].sendBuffer.removeFirst();
      }

      //If new segments fall within GBN_WINDOW, send them
      try{
        for(int i=0;i<TCB_Client.GBN_WINDOW;i++){
          if (table[socksr].sendBuffer.get(i).state==0){
            //Call the send function in sendBufferNode, which changes state to 1 then sends the segment
            table[socksr].sendBuffer.get(i).send();
          }
        }

        //Sleep 100 ms (to avoid spamming the console too quickly)
        Thread.sleep(100);


      }
      catch(Exception e){}
        */
    }
    return 1;
  }
  /*
  //Move to FINWAIT, begin sending FIN's
  public static int disconnSRTClient(int socksr){
    try{
      //Create a FIN segment
      Segment fin = new Segment(2);
      retryCount=0;

      //Create a timer to send FIN segments through the OutputStream
      final Timer timer = new Timer();
      final TimerTask task = new TimerTask(){
        public void run(){
          try{
            output.writeObject(fin);
            //output.flush();
            System.out.println("Sent FIN");
            Client.retryCount+=1;
          }
          catch(Exception e){
            e.printStackTrace();
          }
        };
      };

      //segHandler = new Thread(listenThread);
      //segHandler.start();
      //Send a FIN once every FIN_TIMEOUT ms
      timer.schedule(task,0,FIN_TIMEOUT);
      table[socksr].stateClient=4;
      while(retryCount<FIN_MAX_RETRY){
        if(!segHandler.isAlive()){
          //System.out.println("Segment type: "+segment.type);
          if(segment.type==3){
            //If ListenThread received a segment of type 3, FINACK
            timer.cancel();
            System.out.println("Received FINACK");
            //Change state to 1, CLOSED
            table[socksr].stateClient=1;
            //segHandler = new Thread(listenThread);
            //segHandler.start();
            //Return out of the method
            return 1;
          }
          else{
            //If ListenThread did not find a FINACK, restart ListenThread
            listenThread = new ListenThread(0,0);
            segHandler = new Thread(listenThread);
            segHandler.start();
          }
        }
      }
      timer.cancel();
      //If FIN_MAX_RETRY was exceeded, change state to CLOSED and return -1
      table[socksr].stateClient=1;
      return -1;
    }
    catch(Exception e){
      e.printStackTrace();
      return -1;
    }
  }

  //Remove item from TCB table
  public static int closeSRTClient(int socksr){
    if(table[socksr].stateClient==1){
      //Change value in table to null
      table[socksr]=null;
      return 1;
    }
    else{
      return -1;
    }
  }

  //Terminate the socket connection to the server
  public static int stopOverlay(){
    try{
      //Write a custom Segment of type -1 to shut down the thread, which is listening for a segment (not the best solution)
      //output.writeObject(new Segment(-1));
      //Interrupt the thread
      listenThread.stop();
      segHandler.interrupt();

      //Close input/output streams and the socket
      output.close();
      input.close();
      clientSocket.close();
      return 1;
    }
    catch(Exception e){
      return -1;
    }
  }
  */

}
