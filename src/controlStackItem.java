import java.util.LinkedList;


public class controlStackItem {
	
	public int control;
	public int bbParent;
	public int bbjoinNode;
	LinkedList<SSAAssigns> bbJoinPointer;
	
	public controlStackItem ( int inControl , int inbbParent , int inbbjoinNode, LinkedList<SSAAssigns> inJoinPointer )
	{
		control = inControl ;
		bbParent = inbbParent;
		bbjoinNode = inbbjoinNode ;
		bbJoinPointer = inJoinPointer ;
		
	}
	
	

}
