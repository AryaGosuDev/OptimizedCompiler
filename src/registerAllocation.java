import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.LinkedList;
import java.io.*;
import java.util.Stack;
import java.util.Vector;
import java.util.ListIterator;


public class registerAllocation {
	
	LinkedList<LinkedList<SSAAssigns>> SSA_LL;
	
	
	
	LinkedList<liveNodes> IGNodes ;
	
	Vector<Integer> loopHeaders;
	
	private int currLoopParent = 0, numLoopHeaders = 0;
	
	
	
	
	
	public registerAllocation(LinkedList<LinkedList<SSAAssigns>> inSSA_LL)
	{
		SSA_LL = inSSA_LL;
		LinkedList<Integer> live;
		IGNodes = new LinkedList<liveNodes>();
		loopHeaders = new Vector<Integer>();
		
		System.out.println (" " );
		System.out.println ("Begin RA" );
		
		LinkedList<SSAAssigns> currBB = SSA_LL.get(0) ;
		
		live = ComputeLive (currBB, 1, 1 );
		resetVisits(); //added oct 6 2014
		live = ComputeLive (currBB, 1, 2 );
		
		Coalesce ();
		//IGNodes.removeFirst();
		if ( IGNodes.size() != 0 ) ColorGraph (27, 0);
		
		printIG();
		
		
	}
	
	public LinkedList<Integer> ComputeLive(LinkedList<SSAAssigns> block , int branch, int pass)
	{
		LinkedList<Integer> live = new LinkedList<Integer>(); 
		if ( block == null)
		{
			
		}
		else
		{
			if ( pass == 1 && block.get(0).inedge != null)
			{
				buildLoopHeaders (block);
			}
			
			if ( block.get(0).liveRangeVisited >= pass)
			{
				addToLivefromBlock ( block.get(0).liveEdgeNodes, live);	
			}
			else
			{
				block.get(0).liveRangeVisited++;
				if ( block.get(0).liveRangeVisited == 2)
				{
					
					for ( int loopHeadersIter = 0 ; loopHeadersIter < block.get(0).loopHeaders.size() ; ++ loopHeadersIter)
					{
						System.out.println ( "Adding header block " + block.get(0).loopHeaders.get(loopHeadersIter) + " to "  + block.get(0).label );
						
						block.get(0).assignLiveValues(SSA_LL.get(block.get(0).loopHeaders.get(loopHeadersIter)).get(0).liveEdgeNodes);
					}
				}
				
				if ( block.get(0).left != null)
				{
					/********** added ********* oct 6 2014*/
				
					addToLivefromLive( ComputeLive ( block.get(0).left, 1, pass ), live);
					addToLivefromLive( ComputeLive ( block.get(0).right, 2, pass ), live);
						
					
					/********** original ********* oct 6 2014*/
					/*addToLivefromLive( ComputeLive ( block.get(0).left, 1, pass ), live);
					if ( block.get(0).right != null) 
					{
						
						addToLivefromLive(ComputeLive ( block.get(0).right, 2, pass ), live);
					}*/
					
				}
				else if ( block.get(0).ifJoin != null )
				{
					addToLivefromLive ( ComputeLive ( block.get(0).ifJoin, branch, pass ), live );
					
				}
				else if ( block.get(0).follow != null )
				{
					addToLivefromLive ( ComputeLive ( block.get(0).follow, branch, pass ), live );
					
					int dfdfd = 4;
					
				}
				else if ( block.get(0).inedge != null ) //we are currently at a loop header
				{
					
					addToLivefromLive ( ComputeLive ( block.get(0).inedge, 1, pass), live );
					addToLivefromLive ( ComputeLive ( block.get(0).outedge, 2, pass ), live);
					
					int fdgdf = 4;
				}
				else if ( block.get(0).whilejoin != null)
				{ 
					
					addToLivefromLive ( ComputeLive ( block.get(0).whilejoin, 2, pass), live );
					
					int dffdfd = 4;
				}
				
				for ( int nonPhiInstructions = block.size() -1;nonPhiInstructions >= 1; nonPhiInstructions --  )
				{
					if ( block.get(nonPhiInstructions).operation != DLX.PHI && block.get(nonPhiInstructions).kill == false)
					{
						removeFromLive(block.get(nonPhiInstructions).pc, live) ;
						
						if (block.get(nonPhiInstructions).operation < DLX.BEQ || 
								(block.get(nonPhiInstructions).operation > DLX.RET &&  block.get(nonPhiInstructions).operation != DLX.WRD) )
						{
							createLiveNodesandEdgesInIG ( block.get(nonPhiInstructions).pc, live);
						}
						
						
						
						if ( block.get(nonPhiInstructions).x != null && block.get(nonPhiInstructions).x.kind == DLX.RegInt)
						{
							System.out.println("Adding " + block.get(nonPhiInstructions).x.regno + " to live");
							
							addToLive(block.get(nonPhiInstructions).x.regno, live);
							
						}
						if ( block.get(nonPhiInstructions).y != null && block.get(nonPhiInstructions).y.kind == DLX.RegInt)
						{
							System.out.println("Adding " + block.get(nonPhiInstructions).y.regno + " to live");
							
							addToLive(block.get(nonPhiInstructions).y.regno, live);
							
						}
						
						if ( block.get(nonPhiInstructions).r != null && block.get(nonPhiInstructions).r.regno != -1  )
						{
							System.out.println("Adding " + block.get(nonPhiInstructions).r.regno + " to live");
							
							addToLive(block.get(nonPhiInstructions).r.regno, live);
								
						}
						
						//createLiveNodesandEdgesInIG ( live);
						
					}
				}
				
				block.get(0).assignLiveValues(live);
				
			}
			
			for ( int phiInstructions = 1;phiInstructions < block.size(); phiInstructions ++  )
			{
				if ( block.get(phiInstructions).operation == DLX.PHI )
				{
					
					removeFromLive(block.get(phiInstructions).pc, live) ;
					
					createLiveNodesandEdgesInIG (block.get(phiInstructions).pc,live);	
			
					if ( block.get(phiInstructions).x.kind == DLX.RegInt && branch == 1)
					{
						System.out.println("Adding " + block.get(phiInstructions).x.regno + " to live phi");
						
						addToLive(block.get(phiInstructions).x.regno, live);
						
					}
					if ( block.get(phiInstructions).y.kind == DLX.RegInt && branch == 2)
					{
						System.out.println("Adding " + block.get(phiInstructions).y.regno + " to live phi");
						
						addToLive(block.get(phiInstructions).y.regno, live);
						
					}
					
					
				}
			}
			
			System.out.println("At block # " + block.get(0).label);
			printLiveRange (live);
			System.out.println("Done printing live");
			
			printIGRange ();
			System.out.println("Done printing IG");
			
		}
		return live;
	}
	
	public void addToLive ( int instruction, LinkedList<Integer> live)
	{
		boolean found = false;
		for ( int liveValues = 0 ; liveValues < live.size() ; ++liveValues )
		{
			if ( live.get(liveValues) == instruction)
			{
				found = true;
			}
			
		}
		
		if ( found == false)
		{
			live.add(instruction);
			
		}
	}
	
	public void createLiveNodesandEdgesInIG(int mainInstruction, LinkedList<Integer> live ) //created edges from live list
	{
		if ( getLiveNode (mainInstruction) == null ){ //add the instruction to the IG
			System.out.println ( "Adding " + mainInstruction + " IG Nodes");
			IGNodes.add(new liveNodes(mainInstruction));
		}
		
		liveNodes mainNodeInIG = getLiveNode(mainInstruction); //get the IG node
		liveNodes nodeInIG = null;
		
		for ( int liveValues = 0; liveValues < live.size(); ++liveValues)
		{
			
			if (!isInIG(live.get(liveValues)) ) //add to IG if not there already
			{
				System.out.println("Adding " + live.get(liveValues) + " to IG Nodes");
				liveNodes liveNode = new liveNodes(live.get(liveValues));
				IGNodes.add(liveNode);	
			}
			
			nodeInIG = getLiveNode(live.get(liveValues));
			
			if (!mainNodeInIG.isInEdgeList(nodeInIG) )
			{
				System.out.println("Adding edge " + mainInstruction + " to edge " + live.get(liveValues) );
				mainNodeInIG.edges.add(nodeInIG);
				nodeInIG.edges.add(mainNodeInIG);
			}
			else
			{
				System.out.println("NOT Adding edge " + mainInstruction + " to edge " + live.get(liveValues) );
				
			}
			
			
			
			
			
			
			
			
			
			
			
			
			
			/********** ORIGINAL ********* oct 9 2014*/
			/*  
			liveNodes mainNode = getLiveNode(live.get(liveValues)); //get the IG node
			
			for ( int restOfLiveValues = 0; restOfLiveValues < live.size() && mainNode != null; ++restOfLiveValues)
			{//now add it to the edge list of this node to the other nodes in the live list
				if (restOfLiveValues != liveValues )
				{
					if (!isInIG(live.get(restOfLiveValues)) ) //add to IG if not there already for rest of live values
					{
						System.out.println("Adding " + live.get(restOfLiveValues) + " to live");
						liveNodes liveNode = new liveNodes(live.get(restOfLiveValues));
						IGNodes.add(liveNode);	
					}
					
					liveNodes nonMainNode = getLiveNode(live.get(restOfLiveValues)); //get the other IG node
					if ( nonMainNode != null && !mainNode.isInEdgeList(nonMainNode) )
					{
						System.out.println("Adding edge from " + mainNode.instruction + " to " + nonMainNode.instruction );
						mainNode.edges.add(nonMainNode);
					}
				}
			}*/	
		}
	}
	
	public void addToLivefromBlock (LinkedList<Integer> blockLive, LinkedList<Integer> live )
	{
		boolean found = false;
		
		for ( int inblockLive = 0; inblockLive < blockLive.size() ; ++inblockLive)
		{
			int valueInBlockLive = blockLive.get(inblockLive);
			
			for ( int inLive = 0; inLive < live.size(); ++inLive)
			{
				if ( valueInBlockLive == live.get(inLive))
				{
					found = true;
				}
			}
			
			if ( found != true)
			{
				live.add(valueInBlockLive);
			}
			
			found = false;
			
		}
	}
	
	public void addToLivefromLive (LinkedList<Integer> blockLive, LinkedList<Integer> live )
	{
		boolean found = false;
		
		for ( int inblockLive = 0; inblockLive < blockLive.size() ; ++inblockLive)
		{
			int valueInBlockLive = blockLive.get(inblockLive);
			
			for ( int inLive = 0; inLive < live.size(); ++inLive)
			{
				if ( valueInBlockLive == live.get(inLive))
				{
					found = true;
				}
			}
			
			if ( found != true)
			{
				live.add(valueInBlockLive);
			}
			
			found = false;
			
		}
	}
	
	public void addToLiveFromSource ( LinkedList<Integer> source, LinkedList<Integer> live)
	{
	    boolean found = false;
		
		for ( int inblockLive = 0; inblockLive < source.size() ; ++inblockLive)
		{
			int valueInBlockLive = source.get(inblockLive);
			
			for ( int inLive = 0; inLive < live.size(); ++inLive)
			{
				if ( valueInBlockLive == live.get(inLive))
				{
					found = true;
				}
			}
			
			if ( found != true)
			{
				live.add(valueInBlockLive);
			}
			
			found = false;
			
		}
		
	}
	
	
	
	public boolean isInIG (int instNumber)
	{
		for ( int igNodeNumber = 0; igNodeNumber < IGNodes.size() ; ++ igNodeNumber)
		{
			if ( IGNodes.get(igNodeNumber).instruction == instNumber ) return true;  
		}
		return false;
	}
	
	public liveNodes getLiveNode ( int pc)
	{
		for ( int inIGNodes = 0 ; inIGNodes < IGNodes.size() ; ++ inIGNodes)
		{
			if ( IGNodes.get(inIGNodes).instruction == pc) return IGNodes.get(inIGNodes);
			
		}
		
		return null;
	}
	
	
	
	public void removeFromLive ( int pc, LinkedList<Integer> live )
	{
		for ( int inLive = 0; inLive < live.size(); ++inLive)
		{
			if ( live.get(inLive) == pc)
			{
				live.remove(inLive);
			}
		}
	}
	
	private void resetVisits (){
		for ( int i = 0; i < SSA_LL.size(); ++ i) SSA_LL.get(i).get(0).liveRangeVisited = 0;
	}
	
	public void printLiveRange ( LinkedList<Integer> live)
	{
		System.out.println("Printing live" );
		for ( int printLive = 0 ; printLive < live.size() ; ++printLive  )
		{
			
			System.out.println( " " + live.get(printLive) + " " );
		}
	}
	
	public void printIGRange ( )
	{
		System.out.println("Printing IG" );
		for ( int printIG = 0 ; printIG < IGNodes.size() ; ++printIG  )
		{
			
			System.out.println( " " + IGNodes.get(printIG).instruction  + " " );
		}
	}
	
	public void buildLoopHeaders ( LinkedList<SSAAssigns> block)
	{
		
				if ( currLoopParent == 0 ) //have not set a parent yet
				{
					currLoopParent = block.get(0).label;
					loopHeaders.add( currLoopParent);
				}
				else if ( currLoopParent == block.get(0).label ) //the parent is the node we are at
				{
					loopHeaders.remove(loopHeaders.size() - 1);
					numLoopHeaders--;
					
					if ( loopHeaders.size() == 0)
					{
						currLoopParent = 0;
						numLoopHeaders = 0;
					}
					else currLoopParent = loopHeaders.get(loopHeaders.size() - 1);
						
				}
				else // we have come to a nested while, an enclosed loop header
				{
					currLoopParent = block.get(0).label;
					loopHeaders.add(block.get(0).label); // add the new loop header to the vector
					numLoopHeaders ++ ;
					if ( numLoopHeaders >= 1)
					{
						for ( int loopHeaderIteration = 0 ;loopHeaderIteration < numLoopHeaders ; loopHeaderIteration ++  )
						{
							SSA_LL.get(loopHeaders.get(loopHeaderIteration)).get(0).loopHeaders.add(loopHeaders.lastElement());
						}
					}	
				}
	}
	
	public void printIG ()
	{
		
		try
		{
		  	  FileWriter fstream = new FileWriter("outIG.dot");
		  	  BufferedWriter out = new BufferedWriter(fstream);
		 
		  	  String outString = "digraph G { \n";
		  	  outString += "node [shape = record];\n";
		  	  
		  	  for ( int valuesInIG = 0 ;valuesInIG < IGNodes.size() ; valuesInIG++ )
		  	  {
		  		  
		  		  if ( IGNodes.get(valuesInIG).coalesce.size() > 0)
		  		  {
		  			  //ORIGINAL//outString += "node" + IGNodes.get(valuesInIG).instruction  + "[label=\"" + IGNodes.get(valuesInIG).instruction + "|" + IGNodes.get(valuesInIG).allocatedReg +  "}\"];";
		  			  outString += "node" + IGNodes.get(valuesInIG).instruction  + "[label=\"" + IGNodes.get(valuesInIG).instruction ;
		  			  for ( int coalVals = 0 ; coalVals < IGNodes.get(valuesInIG).coalesce.size() ; ++ coalVals)
		  			  {
		  				outString += "-" + IGNodes.get(valuesInIG).coalesce.get(coalVals).instruction; 
		  				  
		  			  }
		  			  outString += "|" + IGNodes.get(valuesInIG).allocatedReg +  "}\"];";
		  		  }
		  		  else{
		  			  outString += "node" + IGNodes.get(valuesInIG).instruction  + "[label=\"" + IGNodes.get(valuesInIG).instruction + "|" + IGNodes.get(valuesInIG).allocatedReg +  "}\"];";
		  		  }
		  	  }
	  	  
		  	  for (  int valuesInIG = 0 ;valuesInIG < IGNodes.size() ; valuesInIG++ )
		  	  {
		  		  liveNodes ln = IGNodes.get(valuesInIG);
		  		  System.out.println(valuesInIG);
		  		  for (int edges1 = 0 ; edges1 < ln.edges.size() ; ++edges1 )
		  		  {
		  			//System.out.println("e" + edges1);
		  			
		  			if ( edges1 == 8 )
		  			{
		  				int thht = 7;
		  			}
		  			  //outString += "node" + valuesInIG  + " -> node"  + ln.edges.get(edges1).instruction + ";\n" ;
		  			outString += "node" + IGNodes.get(valuesInIG).instruction  + " -> node"  + ln.edges.get(edges1).instruction + ";\n" ;
		  		  }
		  	  }
		  	
		  	outString += "}\n";
		  	
		  	out.write(outString);
	    	  //Close the output stream
	    	  out.close();
		}
		catch (Exception e)
  	  	{	//Catch exception if any
  		  System.err.println("Error: " + e.getMessage());
  	  	}
	}
	
	private void Coalesce ()
	{
		System.out.println("COALESCING");
		
		for ( int blocks = 0; blocks < SSA_LL.size() ; blocks ++)
		{
			LinkedList<SSAAssigns> llsub = SSA_LL.get(blocks);
			
			for ( int instructions = 1 ; instructions < llsub.size() ; instructions ++)
			{
				if ( llsub.get(instructions).operation == DLX.PHI)
				{
					
					boolean found = false; //iterate until we find the ig node with the phi instruction
					liveNodes livePointer = null;
					System.out.println("Block : " + blocks + " instruction : " + instructions);
					
					if ( blocks == 5 )
					{
						int dfgfd = 4;
					}
					
					if ( isInIG(llsub.get(instructions).pc))
					{
						found = true; //we found a fi instruction in the ig
						livePointer = getLiveNode (llsub.get(instructions).pc );
						livePointer.phiInstructuion = true;
						
						
					}
						
						
					boolean foundPhiOperand = false;
					liveNodes phiOperandCoalNode = null ;
					liveNodes edgePointer = null ;
						
						
					if ( found &&  llsub.get(instructions).x.kind == DLX.RegInt) //find out if the first operand conflicts with the phi instructions. if it doesnt add, coalesce the instructions
					{
						for ( int phiEdgeNodes = 0; phiEdgeNodes < livePointer.edges.size() && foundPhiOperand == false ; ++phiEdgeNodes  )
						{
							edgePointer = livePointer.edges.get(phiEdgeNodes);
							if ( edgePointer.instruction == llsub.get(instructions).x.regno  )
							{
								foundPhiOperand = true;
							}	
						}
						
						if ( !foundPhiOperand && isInIG (llsub.get(instructions).x.regno ) )
						{
							phiOperandCoalNode = getLiveNode (llsub.get(instructions).x.regno );
							livePointer.coalesce.add(new liveNodes(phiOperandCoalNode.instruction));
							
							System.out.println ( "Coalescing : " + phiOperandCoalNode.instruction + " with : " + livePointer.instruction  );
							for ( int oldCoalescedEdge = 0 ; oldCoalescedEdge < phiOperandCoalNode.edges.size() ; ++oldCoalescedEdge )
							{
								phiOperandCoalNode.edges.get(oldCoalescedEdge).removeFromEdgeList(phiOperandCoalNode);
								
								if (!livePointer.isInEdgeList(phiOperandCoalNode.edges.get(oldCoalescedEdge)))
								{
									livePointer.edges.add(phiOperandCoalNode.edges.get(oldCoalescedEdge));
									phiOperandCoalNode.edges.get(oldCoalescedEdge).edges.add(livePointer);
								}
								
							}
							
							IGNodes.remove(phiOperandCoalNode);	
						}
					}
							
					foundPhiOperand = false;
						
					if ( found &&  llsub.get(instructions).y.kind == DLX.RegInt) //find out if the first operand conflicts with the phi instructions. if it doesnt add, coalesce the instructions
					{
						for ( int phiEdgeNodes = 0; phiEdgeNodes < livePointer.edges.size() && foundPhiOperand == false ; ++phiEdgeNodes  )
						{
							edgePointer = livePointer.edges.get(phiEdgeNodes);
							if ( edgePointer.instruction == llsub.get(instructions).y.regno  )
							{
								foundPhiOperand = true;
							}	
						}
						
						if ( !foundPhiOperand && isInIG ( llsub.get(instructions).y.regno) )
						{
							phiOperandCoalNode = getLiveNode (llsub.get(instructions).y.regno );
							livePointer.coalesce.add(new liveNodes(phiOperandCoalNode.instruction));
							System.out.println ( "Coalescing : " + phiOperandCoalNode.instruction + " with : " + livePointer.instruction  );
							for ( int oldCoalescedEdge = 0 ; oldCoalescedEdge < phiOperandCoalNode.edges.size() ; ++oldCoalescedEdge )
							{
								phiOperandCoalNode.edges.get(oldCoalescedEdge).removeFromEdgeList(phiOperandCoalNode);
								
								if (!livePointer.isInEdgeList(phiOperandCoalNode.edges.get(oldCoalescedEdge)))
								{
									livePointer.edges.add(phiOperandCoalNode.edges.get(oldCoalescedEdge));
									phiOperandCoalNode.edges.get(oldCoalescedEdge).edges.add(livePointer);
								}
								
							}
							
							IGNodes.remove(phiOperandCoalNode);	
						}
					}		
				}
			}
		}
	}
	
	
	private void ColorGraph (int maxRegisters, int node) //graph coloring algorithm
	{
		boolean found = false;
		int nodeToPass = 0;
		
		liveNodes x = IGNodes.get(node);
		
		if ( x.edges.size() < maxRegisters ) //fewer than max registers
		{
			x.removed = true; //remove it
			System.out.println("Remove node " + x.instruction + " from IG graph" );
			
			
			
		}
		else
		{
			System.out.println("MAX REGISTERS EXCEEDED");
		}
		
		for ( int nodesInIG = 0 ; nodesInIG < IGNodes.size() ; ++nodesInIG) 
		{
			if (IGNodes.get(nodesInIG).removed == false ) //find a node still not removed and pass it
			{
				found = true;
				nodeToPass = nodesInIG;
				
			}
			
		}

		
		if ( found )
		{
			ColorGraph (maxRegisters , nodeToPass );
		}
		
		x.removed = false;
		
		System.out.println("Adding node " + x.instruction + " to IG graph" );
		
		if ( x.instruction == 21 )
			
		{
			int dfgfdg = 4;
		}
		
		if ( x.edges.size() == 0)
		{
			x.allocatedReg = 0;
		}
		else
		{
			boolean foundConflict = true;
			int iteration = 0;
			x.allocatedReg = iteration ;
			while  ( foundConflict) //not passed yet, passed only when pass = true
			{
				foundConflict = false;
				for ( int xNeighbors = 0; xNeighbors < x.edges.size() ; xNeighbors ++)
				{
					if ( foundConflict == false && x.edges.get(xNeighbors).removed == false )
					{
						if ( x.edges.get(xNeighbors).allocatedReg == iteration) //is our iteration conflicting with another nodes iteration
						{
							foundConflict = true;
							iteration++;	
						}
					}
				}
			}
			x.allocatedReg = iteration ;
		}	
		System.out.println("Assigning value " + x.allocatedReg + " to node " + x.instruction  );
	}
}
