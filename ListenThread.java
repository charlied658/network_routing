import java.util.*;
import java.io.*;


public class ListenThread extends Thread{
  int connectionSlot;
  Packet myPacket;
  boolean exit=false;

  public ListenThread (int whatConnectionSlot){
    connectionSlot = whatConnectionSlot;
  }//end constructor

  public void run(){
    while(!exit){
      try{
        Thread.sleep(100);
        System.out.println("connectionSlot: "+connectionSlot);

        //get the packet
        myPacket = (Packet) NetworkNode.nextNodeInput[connectionSlot].readObject();

        if(myPacket.destNodeID==NetworkNode.nodeInstance){
          System.out.println("Received DATA: "+myPacket.seg.toString());
          Server.segmentsReceived+=1;
          Server.lengthOfData=myPacket.seg.length;
          for(int i=0;i<5;i++){
            //Copy the data from the segment into recvBuf
            Server.recvBuf[myPacket.seg.seq_num*5 +i]=myPacket.seg.data[i];
          }
        }
        else{
          //forward packet based on nextHopTable

          int nodeToForwardTo = NetworkNode.nodeNextHopTable[myPacket.destNodeID];
          NetworkNode.prevNodeOutput[NetworkNode.nodeList[nodeToForwardTo]].writeObject(myPacket);
          System.out.println("Received Packet, sent to node "+nodeToForwardTo);
        }
      }
      catch(Exception e){
        e.printStackTrace();
      }
    }
  }//end run

  public void exit(){
    exit=true;
  }

}//end class ListenThread
