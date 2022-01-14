import java.io.*;
public class Graph{
  /*
  77 4223
  521 4534
  14 5542
  96 5146
  382 4675
  77 521 4
  521 14 2
  14 96 3
  521 96 7
  382 96 2
  */

  public static int[] portTable;
  public static int node;

  public static NetworkNode[] nodeTable;
  public static int[][] edges;
  public static int[][] nextHopTables;

  public static void main(String args[]){

    readNetwork();

    shortestPath(0,4);


    //while(true){

    //}

    /*
    while(true){
      try{
        Thread.sleep(1000);
      }
      catch(Exception e){
        e.printStackTrace();
      }
      for(int i=0;i<5;i++){
        //System.out.println("port of "+i+" ="+nodeTable[i].port);
      }
    }
    */

    /*
    for(int i=0;i<5;i++){
      nodeTable[i].createSocket(nodeTable[i].port);
    }
    */

    //}
    //else
    //}

    //TODO: This doesn't work. Need to use separate terminals/threads

    //nodeTable[1].acceptSocket();
    //nodeTable[0].connectToSocket(nodeTable[1].port);

  }

  //Establish the values of the nodes in the Graph and read network.dat
  public static void readNetwork(){
    nodeTable = new NetworkNode[5];
    edges = new int[5][5];
    //portTable (to be forwarded to all nodes in graph)
    portTable = new int[]{4223,4534,5542,5146,4675};
    //if (args.length==0){

    //Data is currently hardcoded in
    nodeTable[0]=new NetworkNode(77,4223);
    nodeTable[1]=new NetworkNode(521,4534);
    nodeTable[2]=new NetworkNode(14,5542);
    nodeTable[3]=new NetworkNode(96,5146);
    nodeTable[4]=new NetworkNode(382,4675);

    //Set each edge to -1 to make it clear that there is no connection
    for(int i=0;i<5;i++){
      for(int j=0;j<5;j++){
        edges[i][j]=-1;
      }
    }

    //System.out.println("Edge at 4,4 is "+edges[4][4]);

    //Data is hardcoded in

    edges[0][1]=4;
    edges[1][0]=4;

    edges[1][2]=2;
    edges[2][1]=2;

    edges[2][3]=3;
    edges[3][2]=3;

    edges[1][3]=7;
    edges[3][1]=7;

    edges[4][3]=2;
    edges[3][4]=2;
  }

  //Determines the shortest path from a source node to a destination node in according with Djikstra's algorithm
  public static void shortestPath(int src, int dest){

    int[] N = new int[5];
    int[] distance = new int[5];
    int[] prev = new int[5];
    int checked=0;
    nextHopTables = new int[5][5];

    for(int i=0;i<5;i++){
      distance[i]=-1;
    }

    //Initialize the values for distance and prev
    N[dest]=1;
    checked+=1;
    for(int i=0;i<5;i++){
      if(edges[dest][i]!=-1){
        distance[i]=edges[dest][i];
        prev[i]=dest;
      }
    }

    //Repeat until all nodes are in N
    while(checked<5){

      /*
      System.out.println("Iteration "+checked+":");
      for(int i=0;i<5;i++){
        System.out.println("Entry "+i+": NodeID: "+nodeTable[i].nodeID);
        System.out.println("distance "+distance[i]);
        System.out.println("previous "+prev[i]+": NodeID: "+nodeTable[prev[i]].nodeID);
        System.out.println("");
      }
      */

      int min=1000;
      int nextNode=-1;
      //Find node with minimum value of distance
      for(int i=0;i<5;i++){
        if (N[i]==0 && distance[i]!=-1 && distance[i]<min){
          min=distance[i];
          nextNode=i;

        }
      }
      //System.out.println("Next node = "+nextNode);

      //Add that node to N
      N[nextNode]=1;
      checked+=1;

      //Check all neighbors of that node, see if distance can be reduced
      for(int i=0;i<5;i++){
        if(edges[nextNode][i]!=-1 && i!=dest){
          if(distance[i]==-1 || distance[nextNode]+edges[nextNode][i]<distance[i]){
            distance[i]=distance[nextNode]+edges[nextNode][i];
            prev[i]=nextNode;
          }
        }
      }

    }

    for(int i=0;i<5;i++){
      System.out.println("Entry "+i+": NodeID: "+nodeTable[i].nodeID);
      System.out.println("port: "+portTable[i]);
      System.out.println("distance "+distance[i]);
      System.out.println("previous "+prev[i]+": NodeID: "+nodeTable[prev[i]].nodeID);
      System.out.println("");
    }

    //Update nextHopTables
    for(int i=0;i<5;i++){
      nextHopTables[i][dest]=prev[i];
      //System.out.println("dest = "+dest);
      System.out.println("Set next hop for "+i+" to "+nextHopTables[i][dest]);
    }

    //nodeTable[3].nextHopTable[4]=2;
    /*
    for(int i=0;i<5;i++){
      System.out.println("Accessing node "+i);
      for(int j=0;j<5;j++){
        System.out.println("nextHopTable at index "+j+" is "+nextHopTables[i][j]);
      }
    }
    */

  }

  public static void printGraph() {
  	for (int i =0; i < edges.length; i++) {
  		for (int j =0; j < edges[i].length; j++) {
        if(edges[i][j]>=0){
          System.out.print(" ");
        }
  			System.out.print(edges[i][j] + " ");

  		}
  		System.out.println();

  	}
  }

  public static String Read(String file){
      int ch;
      String s =  "";
      try{
        // check if File exists or not
        FileReader fr=null;
        try
        {
          fr = new FileReader(file);
        }
        catch (FileNotFoundException fe)
        {
          System.out.println("File " + file + " not found");
          System.exit(1);
        }

        // read from FileReader till the end of file
        s =  "";
        while ((ch=fr.read())!=-1){
          s += (char)ch;
        }

        // close the file
        fr.close();

      }
      catch(Exception e){
        e.printStackTrace();
      }
      return s.trim();
    }

    public static void ParseWeights(String data) {
    	String[] totalLines = data.split("\n");
      edges = new int[5][5];
    	String[] lines = new String[5];
      int[] connections = new int[5];

    	for(int i=0;i<totalLines.length/2;i++){
    		lines[i]=totalLines[totalLines.length/2+i];
        //System.out.println(lines[i]);
    	}

    	//System.out.println("length of lines: "+lines.length);


    	//String[][] Stringy = new String[lines.length][3];
    	//int[][] returning = new int[lines.length][3];

    	for (int i = 0; i < edges.length; i++) {
    		for (int j = 0; j < edges[i].length; j++) {
    			edges[i][j] = -1;
    		}
    	}

    	for(int q=0;q<lines.length;q++) {
    		//System.out.println("q: "+q);
    		connections[q] = Integer.parseInt(lines[q].split(" ")[2]);

    	}

      edges[0][1]=connections[0];
      edges[1][0]=connections[0];

      edges[1][2]=connections[1];
      edges[2][1]=connections[1];

      edges[2][3]=connections[2];
      edges[3][2]=connections[2];

      edges[1][3]=connections[3];
      edges[3][1]=connections[3];

      edges[4][3]=connections[4];
      edges[3][4]=connections[4];

    }

    public static void ParseNodeVals(String data) {

      String[] totalLines = data.split("\n");
      portTable = new int[5];
      int[] nodeIDTable = new int[5];
      nodeTable = new NetworkNode[5];


    	String[] lines = data.split("\n");

      for(int i=0;i<totalLines.length/2;i++){
    		lines[i]=totalLines[i];
        //System.out.println(lines[i]);
    	}

    	//System.out.println("length of lines: "+lines.length);


    	for(int q=0;q<lines.length/2;q++) {
    		//System.out.println("q: "+q);
    		nodeIDTable[q] = Integer.parseInt(lines[q].split(" ")[0]);
        portTable[q] = Integer.parseInt(lines[q].split(" ")[1]);
        nodeTable[q] = new NetworkNode(nodeIDTable[q],portTable[q]);
        System.out.println("Created new node with node id = "+nodeIDTable[q]+" and port = "+portTable[q]);

    	}
    }
}
