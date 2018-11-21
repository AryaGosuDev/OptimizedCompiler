import java.util.LinkedList;

public class BasicBlock {
	
	public LinkedList<LinkedList<SSAAssigns>> SSA_LL;
	
	public BasicBlock () 
	{
		SSA_LL = new LinkedList<LinkedList<SSAAssigns>>() ;
		
		
	}
}