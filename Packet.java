public class Packet implements java.io.Serializable{
int srcNodeID; // src node ID
int destNodeID; // dest node ID
Segment seg; //a Segment object

  public Packet(){

  }

  public Packet(int whatSrcNodeID, int whatDestNodeID, Segment whatSeg){
    srcNodeID = whatSrcNodeID;
    destNodeID = whatDestNodeID;
    seg = whatSeg;
  }//end constructor

 public Segment getSegment(){
   return seg;
 }//end method getSegment

}//end class packet
