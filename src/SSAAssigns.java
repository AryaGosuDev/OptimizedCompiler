import java.util.LinkedList;


public class SSAAssigns {
	int label;
	int operation;
	int value;
	String assigned_ident;
	int pc ;
	int reg;
	boolean isRoot;
	int whileCPVisited = 0;
	int liveRangeVisited = 0;
	int whileCHVisited = 0;
	boolean isifJoinBlock = false;
	boolean isWhileJoinBlock = false;
	boolean isFunc;
	int funcNumber;
	boolean kill;
	int substituteKill ;
	int firstRealInstruction = -1;

	Result phi_assign;
	Result r;
	Result x, y;
	SSAAssigns nodeAssign;
	
	IdentObjectComp[] identArray;
	IdentObjectComp[] funcArgumentArray;
	
	LinkedList<SSAAssigns> inedge;
	LinkedList<SSAAssigns> outedge;
	LinkedList<SSAAssigns> parent;
	LinkedList<SSAAssigns> right;
	LinkedList<SSAAssigns> left;
	LinkedList<SSAAssigns> whilejoin;
	LinkedList<SSAAssigns> ifJoin;
	LinkedList<SSAAssigns> follow;
	
	
	LinkedList<identNode> BBValues;
	LinkedList<Integer> dominators ;
	
	LinkedList<Integer> liveEdgeNodes;
	LinkedList<Integer> loopHeaders;
	
	public SSAAssigns( )
	{
		
	}
	
	public SSAAssigns( int label1 )
	{
		label = label1;
		BBValues = new LinkedList<identNode>();
		dominators = new LinkedList<Integer>();
		liveEdgeNodes = new LinkedList<Integer>();
		loopHeaders = new LinkedList<Integer>();
		
	}
	
	public SSAAssigns( int op, int v, String ident )
	{
		operation = op;
		value = v;
		assigned_ident = ident;	
	}
	
	public int  updateBB ( String s, int a, int n) //s = name of ident, a = address, n = iteration
	{ 
		if ( BBValues.size() != 0)
		{
			identNode temp;
			
			for ( int i = 0 ; i < BBValues.size() ; i++ )
			{
				temp = BBValues.get(i);
				if ( s == temp.identNameinBB )
				{
					temp.identIterinBB = n;
					return 1;
				}
			}
			  
			BBValues.add(new identNode ( s, a, n));
		}
		else 
		{
			BBValues.add(new identNode ( s,a, n));
		}
		
		return 1;
	}
	
	public int getIteration ( String s)
	{
		if ( BBValues.size() != 0)
		{
			identNode temp;
			
			for ( int i = 0 ; i < BBValues.size() ; i++ )
			{
				temp = BBValues.get(i);
				if ( s == temp.identNameinBB )
				{
					return temp.identIterinBB ;
					
				}
			}
			  
			
		}
		return 0;
	}
	
	public void updateDoms (int parentbbLevel, int currbbLevel,  LinkedList<LinkedList<SSAAssigns>> SSA_LL)
	{
		if ( parentbbLevel < 0) parentbbLevel = 0;
		LinkedList<Integer> parentDoms = SSA_LL.get(parentbbLevel).get(0).dominators ;
		
		for ( int i = 0; i < parentDoms.size() ; ++i) SSA_LL.get(currbbLevel).get(0).dominators.add( parentDoms.get(i));
		
		SSA_LL.get(currbbLevel).get(0).dominators.add(currbbLevel);
	}
	
	public void propogateBBValues ( int bbLevel, int bbLevelParent, LinkedList<LinkedList<SSAAssigns>> SSA_LL)
	{
		for (int i = 0 ; i < SSA_LL.get(bbLevelParent).get(0).BBValues.size() ; i++)
		{
			SSA_LL.get(bbLevel).get(0).updateBB(SSA_LL.get(bbLevelParent).get(0).BBValues.get(i).identNameinBB, 
					SSA_LL.get(bbLevelParent).get(0).BBValues.get(i).identAddressinBB, 
					SSA_LL.get(bbLevelParent).get(0).BBValues.get(i).identIterinBB);	
		}
		
	}
	
	public void assignLiveValues ( LinkedList<Integer> live)
	{
		boolean found = false;
		for ( int valuesInLive = 0 ; valuesInLive < live.size() ; ++ valuesInLive)
		{
			int liveValue = live.get(valuesInLive);
			
			
			for ( int valuesInBBLive = 0 ; valuesInBBLive < this.liveEdgeNodes.size() ;  ++ valuesInBBLive )
			{
				if ( this.liveEdgeNodes.get(valuesInBBLive) == liveValue)
				{
					found = true;
					
				}
				
			}
			
			if ( found == false)
			{
				this.liveEdgeNodes.addLast(liveValue);
			}
			found = false;
			
		}
	}
}
