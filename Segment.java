public class Segment implements java.io.Serializable{
  int src_port;
  int dest_port;
  public int seq_num;
  public int length;
  public int type; //SYN 0, SYNACK 1, FIN 2, FINACK 3, DATA 4, DATAACK 5
  int rcv_win;
  int checksum;
  public char[] data;

  final static int SYN = 0;
  final static int SYNACK = 1;
  final static int FIN = 2;
  final static int FINACK = 3;
  final static int DATA = 4;
  final static int DATAACK = 5;

  public Segment(int whatType, int whatSeqNum){
    if(whatType < 0 || whatType > 5){
      System.out.println("Please enter a value between 0 and 5");
    }//end if
    else{
      type = whatType;
    }//end else

    seq_num = whatSeqNum;
  }//end constructor


public Segment(int index, int lengthOfData, char[] array){

  type = Segment.DATA;
  seq_num = index;
  length = lengthOfData;
  data = array;
}//end second constructor

public String toString(){
    String toReturn="[";
    for(int j=0;j<5;j++){
      toReturn+="'";
      toReturn+=data[j];
      toReturn+="'";
      if(j<4){
        toReturn+=",";
      }
    }
    toReturn+="]";
    return toReturn;
  }

}//end class segment
