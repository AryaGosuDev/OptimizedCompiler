import java.util.LinkedList;


public class liveNodes { //NODES IN THE IG GRAPH
	
	LinkedList<liveNodes> edges;
	LinkedList<liveNodes> coalesce;
	
	boolean removed = false;
	boolean phiInstructuion = false;
	
	int instruction ;
	int allocatedReg = -1 ;
	
	public liveNodes (int pc)
	{
		edges = new LinkedList<liveNodes>(); //each node has an edge list
		coalesce =  new LinkedList<liveNodes>(); //coalesce phi operands with its phi operation
		
		instruction = pc ;	
	}
	
	public boolean isInEdgeList ( liveNodes thisNode )
	{
		for ( int edgeEntries = 0; edgeEntries < this.edges.size() ; ++edgeEntries  )
		{
			//if ( this.equals(this.edges.get(edgeEntries))) return true ;
			if ( this.edges.get(edgeEntries).equals(thisNode)) return true;
			
		}
		return false;	
	}
	
	public void removeFromEdgeList ( liveNodes thisNode)
	{
		for ( int edgeEntries = 0; edgeEntries < this.edges.size() ; ++edgeEntries  )
		{
			//if ( this.equals(this.edges.get(edgeEntries))) return true ;
			if ( this.edges.get(edgeEntries).equals(thisNode)) this.edges.remove(thisNode);
			
		}	
	}
}
