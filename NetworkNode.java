import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.UnknownHostException;

public class NetworkNode {
  public static int nodeID;
  public static int nodePort;
  public static int nodeInstance;
  public static Packet packetToContain;

  public static ObjectOutputStream[] prevNodeOutput = new ObjectOutputStream[5];
  public static ObjectInputStream[] prevNodeInput = new ObjectInputStream[5];

  public static ObjectOutputStream[] nextNodeOutput = new ObjectOutputStream[5];
  public static ObjectInputStream[] nextNodeInput = new ObjectInputStream[5];

  public static Socket nodeSocket;
  public static ServerSocket nodeServerSocket;

  public static int[][] myNextHopTable;
  public static int[] myPortTable;

  public static int[] nodeNextHopTable;
  public static int[] nodePortTable;

  public static Thread[] threads = new Thread[5];
  public static ListenThread aListeningThread;

  public static int[] nodeList = new int[5];

  public static int[][] myConnections = new int [2][5]; //two rows, five columns
  //myConnections[0][] is prevNode connections, myConnections[1][] is nextNode connections
  public static int prevNodeConnectionsCounter = 0; //5 is the limit
  public static int nextNodeConnectionsCounter = 0; //5 is the limit


  public NetworkNode(int whatID, int whatNodePort){
    nodeID = whatID;
    nodePort = whatNodePort;

  }//end constructor

/////////////////////////////////Methods////////////////////////////////////////

  //create a Server socket
  public static void createServerSocket(int whatPortNum){
    try{
      nodeServerSocket = new ServerSocket(whatPortNum);
    }//end try

    catch(Exception e){
      e.printStackTrace();
    }//end catch

  }//end method createSocket



  public static void serverSocketAccept(){
    try{
      //the server socket listens for a connection and accepts a socket connection at the same port
      Socket tempSocket = nodeServerSocket.accept();

      if (nextNodeConnectionsCounter < 5){
        nextNodeOutput[nextNodeConnectionsCounter] = new ObjectOutputStream(tempSocket.getOutputStream());
        nextNodeInput[nextNodeConnectionsCounter] = new ObjectInputStream(tempSocket.getInputStream());
        if(nodeInstance!=-1){
          aListeningThread = new ListenThread(nextNodeConnectionsCounter);
          threads[nextNodeConnectionsCounter] = new Thread(aListeningThread);
          threads[nextNodeConnectionsCounter].start();
        }
        nextNodeConnectionsCounter++;
      }//end if

    }//end try

    catch(Exception e){
      e.printStackTrace();
    }//end catch

  }//end method acceptSocket


  public static void createRegSocket(int nodeNum, int whatPortNum){
    System.out.println("Trying to connect to port "+whatPortNum);
    try{
      InetAddress ip = InetAddress.getByName("localhost");
      nodeSocket = new Socket(ip, whatPortNum);
      //when it's created, it will want to connect to a server socket

      if (prevNodeConnectionsCounter < 5){
        prevNodeOutput[prevNodeConnectionsCounter] = new ObjectOutputStream(nodeSocket.getOutputStream());
        prevNodeInput[prevNodeConnectionsCounter] = new ObjectInputStream(nodeSocket.getInputStream());

        if(nodeNum!=-1){
          nodeList[nodeNum] = prevNodeConnectionsCounter;
        }
        prevNodeConnectionsCounter++;
      }//end for loop

    }//end try

    catch(Exception e){
      e.printStackTrace();
    }//end catch

  }//end method connectToSocket


///////////////////////////////End Methods//////////////////////////////////////

///////////////////////////////Main/////////////////////////////////////////////

  public static void main(String args[]){
    if (args.length == 1){
      //user provides node number, -1 is node supervisor, 0-4 is every other node
      nodeInstance = Integer.parseInt(args[0]);
    }

    if (nodeInstance == -1){
      //supervisor node

      //create and/or fill nextHopTable + portTable
      Graph myGraph = new Graph();
      String fileContents = myGraph.Read("network.dat");
      myGraph.ParseWeights(fileContents);
      myGraph.ParseNodeVals(fileContents);
      myGraph.shortestPath(0,4);
      myGraph.readNetwork();
      myPortTable = myGraph.portTable;
      myNextHopTable = myGraph.nextHopTables;

      createServerSocket(7474);

      for (int i = 0; i < 5; i++){
        serverSocketAccept();

        try{
          //supervisor node gives all nodes the nextHopTable and the portTable
          nextNodeOutput[i].writeObject(myPortTable);
          System.out.println("Sent myPortTable");
          nextNodeOutput[i].writeObject(myNextHopTable[4 - i]);
          System.out.println("Sent myNextHopTable");
        }//end try

        catch(Exception e){
          e.printStackTrace();
        }//end catch

      }//end for loop


    }//end if nodeInstance = -1

    if (nodeInstance > -1 && nodeInstance < 5){
      //all nodes that are not the supervisor

      //connect all nodes to supervisor node
      createRegSocket(-1, 7474);
      try{
        //recieve next hop table and port table
        nodePortTable = (int[]) prevNodeInput[0].readObject();
        nodeNextHopTable = (int[]) prevNodeInput[0].readObject();
      }//end try

      catch(Exception e){
        e.printStackTrace();
      }//end catch

      createServerSocket(nodePortTable[nodeInstance]); //this will allow nodes to connect to each other

    }//end if node instance 0-4


    //connect nodes to each other
    if (nodeInstance == 0){
      createRegSocket(1, nodePortTable[1]);
    }//end if

    if (nodeInstance == 1){
      createRegSocket(2, nodePortTable[2]);
      createRegSocket(3, nodePortTable[3]);
      serverSocketAccept();
    }//end if

    if (nodeInstance == 2){
      createRegSocket(3, nodePortTable[3]);
      serverSocketAccept();
    }//end if

    if (nodeInstance == 3){
      createRegSocket(4, nodePortTable[4]);
      serverSocketAccept();
      serverSocketAccept();
    }//end if

    if (nodeInstance == 4){
      serverSocketAccept();
    }//end if

  }///////////////////////////////End Main//////////////////////////////////////

}/////////////////////////////////End Class NetworkNode/////////////////////////
