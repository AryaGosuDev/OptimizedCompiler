
import java.util.Stack;
import java.util.LinkedList;

public class PhiHandler {
	
	static boolean  handlePhiWhile ( int holdPos, int bbLevel, LinkedList<LinkedList<SSAAssigns>> SSA_LL, IdentObjectComp[] identArray,  int pc, Stack<controlStackItem> nestStack )
	{
		LinkedList<SSAAssigns> llsub;
		//llsub = SSA_LL.get(bbLevel-1);
		llsub = nestStack.peek().bbJoinPointer;
		int i = 1;//starting block is node info
		while ( i < llsub.size() && llsub.get(i).operation == DLX.PHI ) //check if any phis are already assigned for this value
		{ 
			if ( llsub.get(i).phi_assign.address == holdPos)
			{
				//llsub.get(i).y.value = llidentArray[holdPos].identIteration;
				llsub.get(i).y.value = SSA_LL.get(bbLevel).get(0).getIteration(identArray[holdPos].nameOfIdent);
				return false;
			}
			++i;
			
		
		}
		
		SSAAssigns tempSSANode = new SSAAssigns();
		tempSSANode.pc = pc;
		tempSSANode.operation = DLX.PHI;
		
		Result tempRes = new Result();
		tempRes.kind = DLX.VarInt;
		
		//tempRes.value = SSA_LL.get(bbLevel-1).get(0).BBValues.get(i).identIterinBB;
		identArray[holdPos].identIteration++;
		tempRes.value = identArray[holdPos].identIteration;
		tempRes.address = holdPos;
		tempSSANode.phi_assign = tempRes;
		
		Result tempx = new Result();
		tempx.kind = DLX.VarInt;
		tempx.value = SSA_LL.get(nestStack.peek().bbParent).get(0).getIteration(identArray[holdPos].nameOfIdent);
		tempx.address = holdPos;
		tempSSANode.x = tempx;
		
		Result tempy = new Result();
		tempy.kind = DLX.VarInt;
		tempy.value = SSA_LL.get(bbLevel).get(0).getIteration(identArray[holdPos].nameOfIdent);
		tempy.address = holdPos;
		tempSSANode.y = tempy;
		
		llsub.add(1,tempSSANode); //add phi
		
		distributePhis ( tempRes, nestStack,SSA_LL, bbLevel);
		
		return true;
	
	}
	
	static boolean handlePhiIf ( int holdPos, int bbLevel, LinkedList<LinkedList<SSAAssigns>> SSA_LL, IdentObjectComp[] identArray,  int pc, Stack<controlStackItem> nestStack, char dir )
	{
		
		LinkedList<SSAAssigns> llsub = nestStack.peek().bbJoinPointer;
		int i = 1;//starting block is node info
		while ( i < llsub.size() && llsub.get(i).operation == DLX.PHI ) //check if any phis are already assigned for this value
		{ 
			if ( llsub.get(i).phi_assign.address == holdPos)
			{
				if ( dir == 'l') llsub.get(i).x.value = SSA_LL.get(bbLevel).get(0).getIteration(identArray[holdPos].nameOfIdent);
				else llsub.get(i).y.value = SSA_LL.get(bbLevel).get(0).getIteration(identArray[holdPos].nameOfIdent);
				return false;
			}
			++i;
			
		
		}
		
		SSAAssigns tempSSANode = new SSAAssigns();
		tempSSANode.pc = pc;
		tempSSANode.operation = DLX.PHI;
		
		Result tempRes = new Result();
		tempRes.kind = DLX.VarInt;
		
		//tempRes.value = SSA_LL.get(bbLevel-1).get(0).BBValues.get(i).identIterinBB;
		identArray[holdPos].identIteration++;
		tempRes.value = identArray[holdPos].identIteration;
		tempRes.address = holdPos;
		tempSSANode.phi_assign = tempRes;
		
		Result tempx = new Result();
		tempx.kind = DLX.VarInt;
		if ( dir == 'l') tempx.value = SSA_LL.get(bbLevel).get(0).getIteration(identArray[holdPos].nameOfIdent);
		else tempx.value = SSA_LL.get(nestStack.peek().bbParent).get(0).getIteration(identArray[holdPos].nameOfIdent);
		tempx.address = holdPos;
		tempSSANode.x = tempx;
		
		Result tempy = new Result();
		tempy.kind = DLX.VarInt;
		if ( dir == 'l' ) tempy.value = SSA_LL.get(nestStack.peek().bbParent).get(0).getIteration(identArray[holdPos].nameOfIdent);
		else tempy.value = SSA_LL.get(bbLevel).get(0).getIteration(identArray[holdPos].nameOfIdent);
		tempy.address = holdPos;
		tempSSANode.y = tempy;
		
		llsub.add(1,tempSSANode); //add phi
		
		//distributePhis ( tempRes, nestStack,SSA_LL, bbLevel);
		
		
		return true;
	}
	
	static boolean distributePhis (Result res, Stack<controlStackItem> nestStack , LinkedList<LinkedList<SSAAssigns>> SSA_LL, int bbLevel  )
	{
		LinkedList<SSAAssigns> llsub = SSA_LL.get(bbLevel);  //bblevel, current block after join block or while
		
		for ( int i = 0 ; i < llsub.size() ; ++i)
		{
			
			if ( llsub.get(i).operation != DLX.MOV )
			{
				if ( llsub.get(i).x != null && llsub.get(i).x.address == res.address ) llsub.get(i).x.value = res.value; //update its values with the values of the phis
				if ( llsub.get(i).y != null && llsub.get(i).y.address == res.address ) llsub.get(i).y.value = res.value;
				
			}
			
		}
		
		llsub = SSA_LL.get(bbLevel-1); //this is the actual while join block
		
		for ( int i = 0 ; i < llsub.size() ; ++i )
		{
			if ( llsub.get(i).operation == DLX.CMP && llsub.get(i).x.address == res.address)
			{
				llsub.get(i).x.value = res.value ;
			}
		}
		return true;
	}
	
	static boolean updatePhis ( IdentObjectComp[] identArray, LinkedList<LinkedList<SSAAssigns>> SSA_LL, int bbLevel, Stack<controlStackItem> nestStack, LinkedList<SSAAssigns> joinNode, int joinNodeFollow   ) 
	{
		
		//LinkedList<SSAAssigns> llsub = SSA_LL.get(nestStack.peek().bbjoinNode);
		//LinkedList<SSAAssigns> llsub = SSA_LL.get(bbJoinNode);
		//LinkedList<SSAAssigns> llsub = SSA_LL.get(bbLevel); 
		
		int i = 0;
		
		while ( i < SSA_LL.get(nestStack.peek().bbParent).get(0).BBValues.size() )
		{
			SSA_LL.get(bbLevel).get(0).updateBB(SSA_LL.get(nestStack.peek().bbParent).get(0).BBValues.get(i).identNameinBB, 
					SSA_LL.get(nestStack.peek().bbParent).get(0).BBValues.get(i).identAddressinBB,
					SSA_LL.get(nestStack.peek().bbParent).get(0).BBValues.get(i).identIterinBB);
			++i;
			
		}
		
		i = 1;
		
		while ( i < joinNode.size() )
		{
			if ( joinNode.get(i).phi_assign != null)
			{
				SSA_LL.get(bbLevel).get(0).updateBB(identArray[joinNode.get(i).phi_assign.address].nameOfIdent, 
						joinNode.get(i).phi_assign.address,
						joinNode.get(i).phi_assign.value);
				//System.out.println(bbLevel + " " + identArray[joinNode.get(i).phi_assign.address].nameOfIdent + " " + joinNode.get(i).phi_assign.address + " " + joinNode.get(i).phi_assign.value );
				
				if ( nestStack.peek().control == 1 || (nestStack.peek().control == 0 && SSA_LL.get(joinNodeFollow).get(0).whilejoin != null )   )
				{
				
					for ( int j = 1 ; j < SSA_LL.get(joinNodeFollow).size() ; ++j ) //update the instructions for the block that follows the join on the while
					{
						if ( SSA_LL.get(joinNodeFollow).get(j).operation != DLX.MOV )
						{
							if ( SSA_LL.get(joinNodeFollow).get(j).x != null && SSA_LL.get(joinNodeFollow).get(j).x.address == joinNode.get(i).phi_assign.address )
							{
								SSA_LL.get(joinNodeFollow).get(j).x.value = joinNode.get(i).phi_assign.value; //update its values with the values of the phis
							}
							if ( SSA_LL.get(joinNodeFollow).get(j).y != null && SSA_LL.get(joinNodeFollow).get(j).y.address == joinNode.get(i).phi_assign.address )
							{
								SSA_LL.get(joinNodeFollow).get(j).y.value = joinNode.get(i).phi_assign.value; //update its values with the values of the phis
							}
							/********* added oct 7 2014 *******/
							if ( SSA_LL.get(joinNodeFollow).get(j).operation == DLX.WRD && SSA_LL.get(joinNodeFollow).get(j).r.kind == DLX.VarInt && SSA_LL.get(joinNodeFollow).get(j).r.address == joinNode.get(i).phi_assign.address )
							{
								SSA_LL.get(joinNodeFollow).get(j).r.value = joinNode.get(i).phi_assign.value;
							}
						}
						else
						{
							if ( SSA_LL.get(joinNodeFollow).get(j).r != null && SSA_LL.get(joinNodeFollow).get(j).r.address == joinNode.get(i).phi_assign.address )
							{
								SSA_LL.get(joinNodeFollow).get(j).r.value = joinNode.get(i).phi_assign.value; //update its values with the values of the phis
							}
						}
					}
				}
			}
			++i;
		}
		
		//updateAllWhileNested ( identArray, SSA_LL, nestStack); // this will check if we have a while within a while or a if within a while and update all their phis.......
		
		return true;
	}
	
	static int updateNestedPhis ( IdentObjectComp[] identArray, LinkedList<LinkedList<SSAAssigns>> SSA_LL, int bbLevel, Stack<controlStackItem> nestStack, LinkedList<SSAAssigns> priorJoinNode, int pc, Stack<Character> dirStack    ) 
	{
		boolean updatedPhi = false;
		for ( int i = 1; i < priorJoinNode.size() ; ++i ) //iterate through phis of the prior join node
		{
			if ( priorJoinNode.get(i).phi_assign != null) //take all the phis of the prior join node
			{
				LinkedList<SSAAssigns> llsub = nestStack.peek().bbJoinPointer ;
				int j = 1; //starting block is node info
				while ( j < llsub.size() && llsub.get(j).operation == DLX.PHI ) //check if any phis are already assigned for this value
				{ 
					
					if ( llsub.get(j).phi_assign.address == priorJoinNode.get(i).phi_assign.address)
					{
						if ( dirStack.isEmpty()) llsub.get(j).y.value = priorJoinNode.get(i).phi_assign.value;
						else if ( dirStack.peek() == 'r' ) llsub.get(j).y.value = priorJoinNode.get(i).phi_assign.value; //asign the right enter of the top phi to the value of the dominated phi block
						else if ( dirStack.peek() == 'l')  llsub.get(j).x.value = priorJoinNode.get(i).phi_assign.value;
						else System.out.println("NESTED IF ERROR, NO ITEMS ON DIR STACK") ;
						updatedPhi = true;
					}
					++j;
				}
				if (updatedPhi == false ) //if the phi doesnt exist in the outer phi block, assign it
				{
					int holdPos = priorJoinNode.get(i).phi_assign.address;
					
					SSAAssigns tempSSANode = new SSAAssigns();
					tempSSANode.pc = pc++;
					tempSSANode.operation = DLX.PHI;
					
					Result tempRes = new Result();
					tempRes.kind = DLX.VarInt;
					
					//tempRes.value = SSA_LL.get(bbLevel-1).get(0).BBValues.get(i).identIterinBB;
					identArray[holdPos].identIteration++;
					tempRes.value = identArray[holdPos].identIteration;
					tempRes.address = holdPos;
					tempSSANode.phi_assign = tempRes;
					
					Result tempx = new Result();
					tempx.kind = DLX.VarInt;
					if ( dirStack.isEmpty()) tempx.value = SSA_LL.get(nestStack.peek().bbParent).get(0).getIteration(identArray[holdPos].nameOfIdent);
					else if ( dirStack.peek() == 'l' ) tempx.value = SSA_LL.get(bbLevel).get(0).getIteration(identArray[holdPos].nameOfIdent);
					else tempx.value = SSA_LL.get(nestStack.peek().bbParent).get(0).getIteration(identArray[holdPos].nameOfIdent);
					tempx.address = holdPos;
					tempSSANode.x = tempx;
					
					Result tempy = new Result();
					tempy.kind = DLX.VarInt;
					if ( dirStack.isEmpty()) tempy.value = SSA_LL.get(bbLevel).get(0).getIteration(identArray[holdPos].nameOfIdent);
					else if ( dirStack.peek() == 'r' ) tempy.value = SSA_LL.get(bbLevel).get(0).getIteration(identArray[holdPos].nameOfIdent);
					else tempy.value = SSA_LL.get(nestStack.peek().bbParent).get(0).getIteration(identArray[holdPos].nameOfIdent);
					tempy.address = holdPos;
					tempSSANode.y = tempy;
					
					llsub.add(1,tempSSANode); //add phi
					
					
				
				}
				
				updatedPhi = false;
			}
			
		}
		
		
		if ( nestStack.peek().control == 0 && priorJoinNode.get(0).inedge != null) //nested while within while
		{
			for ( int enclosingLoopHeader = 1; enclosingLoopHeader < nestStack.peek().bbJoinPointer.size() ; ++enclosingLoopHeader)
			{
				if ( nestStack.peek().bbJoinPointer.get(enclosingLoopHeader).phi_assign != null)
				{
					for ( int priorJoinParent = 1; priorJoinParent < priorJoinNode.get(0).parent.size(); ++ priorJoinParent )
					{
						if (priorJoinNode.get(0).parent.get(priorJoinParent).x != null &&
								priorJoinNode.get(0).parent.get(priorJoinParent).x.kind == DLX.VarInt &&
								priorJoinNode.get(0).parent.get(priorJoinParent).x.address == nestStack.peek().bbJoinPointer.get(enclosingLoopHeader).phi_assign.address	)
						{
							priorJoinNode.get(0).parent.get(priorJoinParent).x.value = nestStack.peek().bbJoinPointer.get(enclosingLoopHeader).phi_assign.value ;
							
						}
						
						if (priorJoinNode.get(0).parent.get(priorJoinParent).y != null &&
								priorJoinNode.get(0).parent.get(priorJoinParent).y.kind == DLX.VarInt &&
								priorJoinNode.get(0).parent.get(priorJoinParent).y.address == nestStack.peek().bbJoinPointer.get(enclosingLoopHeader).phi_assign.address	)
						{
							priorJoinNode.get(0).parent.get(priorJoinParent).y.value = nestStack.peek().bbJoinPointer.get(enclosingLoopHeader).phi_assign.value ;
							
						}
						
					}
					
				}
				
			}
			
			for ( int priorJoinNodeIter = 1; priorJoinNodeIter < priorJoinNode.size() ; priorJoinNodeIter++  )
			{
				if ( priorJoinNode.get(priorJoinNodeIter).phi_assign != null) // if our prior join has a left side phi that is 0, assign it to the phi of the outer loop
				{
					if ( priorJoinNode.get(priorJoinNodeIter).x.value == 0)
					{
						if ( priorJoinNode.get(0).parent.get(0).getIteration(identArray[priorJoinNode.get(priorJoinNodeIter).x.address].nameOfIdent) == 0 )
						{
							for ( int enclosingLoopIter = 1 ; enclosingLoopIter < nestStack.peek().bbJoinPointer.size() ; ++enclosingLoopIter )
							{
								if ( nestStack.peek().bbJoinPointer.get(enclosingLoopIter).phi_assign != null &&
										nestStack.peek().bbJoinPointer.get(enclosingLoopIter).phi_assign.address == priorJoinNode.get(priorJoinNodeIter).x.address)
								{
									priorJoinNode.get(priorJoinNodeIter).x.value = nestStack.peek().bbJoinPointer.get(enclosingLoopIter).phi_assign.value;
								}
							}	
						}
					}	
				}
			}
			
			for ( int i = 1 ; i < nestStack.peek().bbJoinPointer.size() ; ++i ) // update the values of the compare in the outer while join
			{
				if ( nestStack.peek().bbJoinPointer.get(i).operation == DLX.CMP)
				{
					Result tempRes = nestStack.peek().bbJoinPointer.get(i).x ;
					for ( int phiIter = 1; phiIter < nestStack.peek().bbJoinPointer.size() && tempRes.kind == DLX.VarInt ; ++phiIter )
					{
						if ( nestStack.peek().bbJoinPointer.get(phiIter).phi_assign != null && 
								nestStack.peek().bbJoinPointer.get(phiIter).phi_assign.address == tempRes.address)
						{
							
							tempRes.value = nestStack.peek().bbJoinPointer.get(phiIter).phi_assign.value;
						}
						
					}
					 tempRes = nestStack.peek().bbJoinPointer.get(i).y ;
					for ( int phiIter = 1; phiIter < nestStack.peek().bbJoinPointer.size() && tempRes.kind == DLX.VarInt ; ++phiIter )
					{
						if ( nestStack.peek().bbJoinPointer.get(phiIter).phi_assign != null && 
								nestStack.peek().bbJoinPointer.get(phiIter).phi_assign.address == tempRes.address)
						{
							
							tempRes.value = nestStack.peek().bbJoinPointer.get(phiIter).phi_assign.value;
						}
					}	
				}
			}
		}
		return pc;
	}
	
	static void updateAllWhileNested ( IdentObjectComp[] identArray, LinkedList<LinkedList<SSAAssigns>> SSA_LL,  Stack<controlStackItem> nestStack   ) 
	{
		if ( SSA_LL.get(nestStack.peek().bbjoinNode).get(0).inedge != null &&  //if the control after the while join is a while block
				SSA_LL.get(nestStack.peek().bbjoinNode).get(0).inedge.get(0).whilejoin == null && 
				SSA_LL.get(nestStack.peek().bbjoinNode).get(0).inedge.get(0).inedge.get(0).follow != null)
		{
			//LinkedList<SSAAssigns> llsub = SSA_LL.get(nestStack.peek().bbjoinNode).get(0).inedge; 
			
			//int i = 0;
			/*
			while ( i < SSA_LL.get(nestStack.peek().bbParent).get(0).BBValues.size() )
			{
				SSA_LL.get(bbLevel).get(0).updateBB(SSA_LL.get(nestStack.peek().bbParent).get(0).BBValues.get(i).identNameinBB, 
						SSA_LL.get(nestStack.peek().bbParent).get(0).BBValues.get(i).identAddressinBB,
						SSA_LL.get(nestStack.peek().bbParent).get(0).BBValues.get(i).identIterinBB);
				++i;
				
			}
			*/
			
			
		}
		
	}
	
	static void cycleThroughNestedWhile ( LinkedList<SSAAssigns> whileJoinNode )
	{
		for ( int phiInWhileJoinIter = 1 ; phiInWhileJoinIter < whileJoinNode.size() ; ++phiInWhileJoinIter )
		{
			if ( whileJoinNode.get(phiInWhileJoinIter).phi_assign != null)
			{
				
				
				for ( int i = 1 ; i < whileJoinNode.size() ; ++i ) // update the values of the compare in the outer while join
				{
					if ( whileJoinNode.get(i).operation == DLX.CMP)
					{
						Result tempRes = whileJoinNode.get(i).x ;
						for ( int phiIter = 1; phiIter < whileJoinNode.size() && tempRes.kind == DLX.VarInt ; ++phiIter )
						{
							if ( whileJoinNode.get(phiIter).phi_assign != null && 
									whileJoinNode.get(phiIter).phi_assign.address == tempRes.address)
							{
								
								tempRes.value = whileJoinNode.get(phiIter).phi_assign.value;
							}
							
						}
						
						 tempRes = whileJoinNode.get(i).y ;
						for ( int phiIter = 1; phiIter < whileJoinNode.size() && tempRes.kind == DLX.VarInt ; ++phiIter )
						{
							if ( whileJoinNode.get(phiIter).phi_assign != null && 
									whileJoinNode.get(phiIter).phi_assign.address == tempRes.address)
							{
								
								tempRes.value = whileJoinNode.get(phiIter).phi_assign.value;
							}
							
						}
						
						
						
					}
				}
				
				
				
				
				LinkedList<SSAAssigns> currPoint = whileJoinNode.get(0).inedge;
				
				while (currPoint != whileJoinNode) //while we have not cycled
				{
					System.out.println("Cycle : " +currPoint.get(0).label);
					if ( currPoint.get(0).inedge == null) //not at end
					{
						for ( int currBlockIter = 1; currBlockIter < currPoint.size(); currBlockIter ++) // do all instructions within the while block but outside the inner while blocks
						{
							if ( currPoint.get(currBlockIter).x != null &&
									currPoint.get(currBlockIter).x.kind == DLX.VarInt &&
									currPoint.get(currBlockIter).x.address == whileJoinNode.get(phiInWhileJoinIter).phi_assign.address)
							{
								currPoint.get(currBlockIter).x.value = whileJoinNode.get(phiInWhileJoinIter).phi_assign.value;
							}
							if ( currPoint.get(currBlockIter).y != null &&
									currPoint.get(currBlockIter).y.kind == DLX.VarInt &&
									currPoint.get(currBlockIter).y.address == whileJoinNode.get(phiInWhileJoinIter).phi_assign.address)
							{
								currPoint.get(currBlockIter).y.value = whileJoinNode.get(phiInWhileJoinIter).phi_assign.value;
							}
							
						}
						
						currPoint = currPoint.get(0).follow;
						if ( currPoint == null ) currPoint = whileJoinNode ; //we are not within nested whiles
					}
					else 
					{
						boolean found = false;
						for ( int whileJoinPhis = 1; whileJoinPhis < currPoint.size() ; ++whileJoinPhis  )
						{
							if ( currPoint.get(whileJoinPhis).phi_assign != null && 
									currPoint.get(whileJoinPhis).phi_assign.address == whileJoinNode.get(phiInWhileJoinIter).phi_assign.address)
							{
								found = true;
							}
						}
						if ( found == false)
						{
							//for ( int joinBody = 1 ; joinBody < currPoint.size(); joinBody ++  ) //changed sept 6 2014 to make sure it works
							for ( int joinBody = 1 ; joinBody < currPoint.get(0).inedge.size(); joinBody ++  )
							{
								
									if ( currPoint.get(0).inedge.get(joinBody).x != null &&
											currPoint.get(0).inedge.get(joinBody).x.kind == DLX.VarInt &&
													currPoint.get(0).inedge.get(joinBody).x.address == whileJoinNode.get(phiInWhileJoinIter).phi_assign.address)
									{
										currPoint.get(0).inedge.get(joinBody).x.value = whileJoinNode.get(phiInWhileJoinIter).phi_assign.value;
									}
									if ( currPoint.get(0).inedge.get(joinBody).y != null &&
											currPoint.get(0).inedge.get(joinBody).y.kind == DLX.VarInt &&
													currPoint.get(0).inedge.get(joinBody).y.address == whileJoinNode.get(phiInWhileJoinIter).phi_assign.address)
									{
										currPoint.get(0).inedge.get(joinBody).y.value = whileJoinNode.get(phiInWhileJoinIter).phi_assign.value;
									}
									
									if ( currPoint.get(0).inedge.get(joinBody).r != null &&
											currPoint.get(0).inedge.get(joinBody).r.kind == DLX.VarInt &&
													currPoint.get(0).inedge.get(joinBody).r.address == whileJoinNode.get(phiInWhileJoinIter).phi_assign.address)
									{
										currPoint.get(0).inedge.get(joinBody).r.value = whileJoinNode.get(phiInWhileJoinIter).phi_assign.value;
									}
									
									
									
								
								
							}
							
						}
						
						if (found == true )
						{
							currPoint = currPoint.get(0).outedge;
							if ( currPoint == null ) currPoint = whileJoinNode ; //we are not within nested whiles
							
						}
						if ( found == false)
						{
							if ( currPoint.get(0).inedge.get(0).whilejoin == null)
							{
								
								if ( currPoint.get(0).whileCPVisited == 1)
								{
									currPoint.get(0).whileCPVisited = 0;
									System.out.println("state : " + currPoint.get(0).label + " to " + currPoint.get(0).whileCPVisited);
									currPoint = currPoint.get(0).outedge;
									
									
								}
								else
								{
									
									currPoint.get(0).whileCPVisited++;
									//System.out.println("increment : " + currPoint.get(0).label + " to " + currPoint.get(0).whileCPVisited);
									currPoint = currPoint.get(0).inedge.get(0).follow;
									
								}
								
							}
							else
							{
								currPoint = currPoint.get(0).outedge;
								if ( currPoint == null ) currPoint = whileJoinNode ; //we are not within nested whiles
							}
								
							
						}
						
						//currPoint = currPoint.get(0).outedge;
						//if ( currPoint == null ) currPoint = whileJoinNode ; //we are not within nested whiles
					}
					
					
				}
				
			}
			
		}
		
	}
	
}
