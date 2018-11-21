import java.util.LinkedList;
import java.util.Hashtable;
import java.lang.String;
import java.util.Queue;

public class CPHandler {
	
	LinkedList<LinkedList<SSAAssigns>> SSA_LL;
	LinkedList<LinkedList<SSAAssigns>> SSA_LL_CP ;
	
	LinkedList<SSAAssigns> pointer ;
	LinkedList<SSAAssigns> pointerRight ;
	
	Result tempRes ;
	
	IdentObjectComp[] identArray;
	
	Hashtable valuesHash = new Hashtable ();
	Hashtable registerHash = new Hashtable();
	
	Queue<LinkedList<SSAAssigns>> bbOrder = new LinkedList<LinkedList<SSAAssigns>>() ;
	
	public CPHandler (  LinkedList<LinkedList<SSAAssigns>> SSA_LL_in ) throws Exception
	{
		SSA_LL = SSA_LL_in ;
		
		PerformGlobalCP();
	}
	
	public void PerformGlobalCP ( ) throws Exception
	{
		resetAllCPVisited ();
		pointer = SSA_LL.get(0);
		bbOrder.add (pointer);
		identArray = pointer.get(0).identArray ;
		
		
		while ( pointer != null  )
		{
			System.out.println(pointer.get(0).label);
			
			for ( int instructionsInBlock = 1; instructionsInBlock < pointer.size() ; ++ instructionsInBlock)
			{
				System.out.println("pc : " + instructionsInBlock);
				 
				if ( pointer.get(instructionsInBlock).kill != true)
				{
					if ( pointer.get(instructionsInBlock).operation == DLX.MOV )	
					{
							if ( getValueConst ( pointer.get(instructionsInBlock).r.regno ) != null ) 
							{
								pointer.get(instructionsInBlock).r = getValueConst ( pointer.get(instructionsInBlock).r.regno );
							}
							
							/**************** NEW ****************** CHANGE OCT 5 2014*/
							Result tempResult = pointer.get(instructionsInBlock).r;
							
							if ( tempResult.kind == DLX.VarInt )//keep iterating through the hash until the assigned ident can return a constant or register
							{
								
								while ( tempResult != null && tempResult.kind != DLX.ConstInt && tempResult.kind != DLX.RegInt )
									tempResult = getValue ( identArray[tempResult.address].nameOfIdent + "_"+ tempResult.value  );
								
							}
							
							if (  tempResult == null) throw new Exception ( "CPHandler, no constant value for variable");
							
							storeValue(tempResult , pointer.get(instructionsInBlock).assigned_ident);
							pointer.get(instructionsInBlock).kill = true; 
							
							/**************** ORIGINAL ****************** CHANGE OCT 5 2014*/
							//storeValue(pointer.get(instructionsInBlock).r , pointer.get(instructionsInBlock).assigned_ident);
							//pointer.get(instructionsInBlock).kill = true;
			
					}
					else if ( pointer.get(instructionsInBlock).operation == DLX.WRD)
					{
						if ( pointer.get(instructionsInBlock).r.kind == DLX.VarInt)
						{
							tempRes = getValue (  identArray[pointer.get(instructionsInBlock).r.address].nameOfIdent + "_" + pointer.get(instructionsInBlock).r.value);
							pointer.get(instructionsInBlock).r = tempRes;
						}
						else if ( pointer.get(instructionsInBlock).r.kind == DLX.RegInt && getValueConst ( pointer.get(instructionsInBlock).r.regno ) != null)
						{
							pointer.get(instructionsInBlock).r = getValueConst ( pointer.get(instructionsInBlock).r.regno );
							
						}
						
					}
					else if (pointer.get(instructionsInBlock).operation == DLX.PHI )
					{
						tempRes = getValue (  identArray[pointer.get(instructionsInBlock).x.address].nameOfIdent + "_" + pointer.get(instructionsInBlock).x.value);
						if (tempRes != null )
						{
							pointer.get(instructionsInBlock).x = tempRes ;
						}	
						
						tempRes = getValue (  identArray[pointer.get(instructionsInBlock).y.address].nameOfIdent + "_" + pointer.get(instructionsInBlock).y.value);
						if (tempRes != null )
						{
							pointer.get(instructionsInBlock).y = tempRes ;
						}
						
						tempRes = new Result ();
						tempRes.kind = DLX.RegInt;
						tempRes.regno = pointer.get(instructionsInBlock).pc;
						storeValue ( tempRes , identArray[pointer.get(instructionsInBlock).phi_assign.address].nameOfIdent + "_" + pointer.get(instructionsInBlock).phi_assign.value );
						
						if ( pointer.get(instructionsInBlock).x.kind == DLX.RegInt && getValueConst ( pointer.get(instructionsInBlock).x.regno ) != null )
						{
							pointer.get(instructionsInBlock).x = getValueConst ( pointer.get(instructionsInBlock).x.regno );
						}
						
						if ( pointer.get(instructionsInBlock).y.kind == DLX.RegInt && getValueConst ( pointer.get(instructionsInBlock).y.regno ) != null )
						{
							pointer.get(instructionsInBlock).y = getValueConst ( pointer.get(instructionsInBlock).y.regno );
						}
						
					}
					else if (  pointer.get(instructionsInBlock).operation < DLX.BEQ || pointer.get(instructionsInBlock).operation > DLX.BSR)
					{
						
						if ( pointer.get(instructionsInBlock).x.kind == DLX.VarInt )
						{
							tempRes = getValue (  identArray[pointer.get(instructionsInBlock).x.address].nameOfIdent + "_" + pointer.get(instructionsInBlock).x.value);
							if (tempRes != null )
							{
								pointer.get(instructionsInBlock).x = tempRes ;
								
							}		
								
						}
						if ( pointer.get(instructionsInBlock).y.kind == DLX.VarInt )
						{
							tempRes = getValue (  identArray[pointer.get(instructionsInBlock).y.address].nameOfIdent + "_" + pointer.get(instructionsInBlock).y.value);
							if (tempRes != null )
							{
								pointer.get(instructionsInBlock).y = tempRes ;
									
							}
						}
						
						if (pointer.get(instructionsInBlock).operation == DLX.CMP )
						{
							if ( pointer.get(instructionsInBlock).x.kind == DLX.RegInt && getValueConst ( pointer.get(instructionsInBlock).x.regno ) != null )
							{
								pointer.get(instructionsInBlock).x = getValueConst ( pointer.get(instructionsInBlock).x.regno );
							}
							
							if ( pointer.get(instructionsInBlock).y.kind == DLX.RegInt && getValueConst ( pointer.get(instructionsInBlock).y.regno ) != null )
							{
								pointer.get(instructionsInBlock).y = getValueConst ( pointer.get(instructionsInBlock).y.regno );
							}
						}
						
						if (pointer.get(instructionsInBlock).operation >= DLX.ADD && pointer.get(instructionsInBlock).operation <= DLX.DIV   )
						{
							if ( pointer.get(instructionsInBlock).x.kind == DLX.RegInt && getValueConst ( pointer.get(instructionsInBlock).x.regno ) != null )
							{
								pointer.get(instructionsInBlock).x = getValueConst ( pointer.get(instructionsInBlock).x.regno );
							}
							
							if ( pointer.get(instructionsInBlock).y.kind == DLX.RegInt && getValueConst ( pointer.get(instructionsInBlock).y.regno ) != null )
							{
								pointer.get(instructionsInBlock).y = getValueConst ( pointer.get(instructionsInBlock).y.regno );
							}
						}
						
						
						
						
						
						if ( pointer.get(instructionsInBlock).operation == DLX.ADD )
						{
							if ( pointer.get(instructionsInBlock).x.kind == DLX.ConstInt && pointer.get(instructionsInBlock).y.kind == DLX.ConstInt )
							{
								tempRes = new Result();
								tempRes.kind = DLX.ConstInt;
								tempRes.value = pointer.get(instructionsInBlock).x.value + pointer.get(instructionsInBlock).y.value;
								storeValueConst ( tempRes , pointer.get(instructionsInBlock).pc );
								pointer.get(instructionsInBlock).kill = true;
								
								
								
							}
							
						}
						if ( pointer.get(instructionsInBlock).operation == DLX.SUB )
						{
							if ( pointer.get(instructionsInBlock).x.kind == DLX.ConstInt && pointer.get(instructionsInBlock).y.kind == DLX.ConstInt )
							{
								tempRes = new Result();
								tempRes.kind = DLX.ConstInt;
								tempRes.value = pointer.get(instructionsInBlock).x.value - pointer.get(instructionsInBlock).y.value;
								storeValueConst ( tempRes , pointer.get(instructionsInBlock).pc );
								pointer.get(instructionsInBlock).kill = true;
								
								
							}
							
						}
						if ( pointer.get(instructionsInBlock).operation == DLX.MUL )
						{
							if ( pointer.get(instructionsInBlock).x.kind == DLX.ConstInt && pointer.get(instructionsInBlock).y.kind == DLX.ConstInt )
							{
								tempRes = new Result();
								tempRes.kind = DLX.ConstInt;
								tempRes.value = pointer.get(instructionsInBlock).x.value * pointer.get(instructionsInBlock).y.value;
								storeValueConst ( tempRes , pointer.get(instructionsInBlock).pc );
								pointer.get(instructionsInBlock).kill = true;
								
								
							}
							
						}
						if ( pointer.get(instructionsInBlock).operation == DLX.DIV )
						{
							if ( pointer.get(instructionsInBlock).x.kind == DLX.ConstInt && pointer.get(instructionsInBlock).y.kind == DLX.ConstInt )
							{
								tempRes = new Result();
								tempRes.kind = DLX.ConstInt;
								tempRes.value = pointer.get(instructionsInBlock).x.value / pointer.get(instructionsInBlock).y.value;
								storeValueConst ( tempRes , pointer.get(instructionsInBlock).pc );
								pointer.get(instructionsInBlock).kill = true;
								
								
							}
							
						}
						
					}
				}
				
			}
			if ( bbOrder.peek().get(0).left != null)
			{
				bbOrder.add(bbOrder.peek().get(0).left);
				if (bbOrder.peek().get(0).right != null )
					bbOrder.add(bbOrder.peek().get(0).right);
				else
					bbOrder.add(bbOrder.peek().get(0).ifJoin);
				
			}
			else if ( bbOrder.peek().get(0).ifJoin != null )
			{
				bbOrder.add(bbOrder.peek().get(0).ifJoin);
			}
			else if ( bbOrder.peek().get(0).follow != null )
			{
				bbOrder.add(bbOrder.peek().get(0).follow);
			}
			else if ( bbOrder.peek().get(0).inedge != null )
			{
				if ( bbOrder.peek().get(0).whileCPVisited >= 2 )
					bbOrder.add(bbOrder.peek().get(0).outedge);
				else
				{
					bbOrder.add(bbOrder.peek().get(0).inedge);
					pointer.get(0).whileCPVisited ++;
				}
			}
			else if ( bbOrder.peek().get(0).whilejoin != null)
			{
				bbOrder.add(bbOrder.peek().get(0).whilejoin);
				bbOrder.peek().get(0).whilejoin.get(0).whileCPVisited++;
			}
			
			
			bbOrder.poll();
		
			if ( bbOrder.peek() != null)
				pointer = bbOrder.peek();
			else pointer = null;
			
		}
		
	}
	
	private void resetAllCPVisited ()
	{
		for ( int i = 0 ; i < SSA_LL.size() ; ++ i)
		{
			SSA_LL.get(i).get(0).whileCPVisited = 0 ;
			
		}
	}
	
	Result getValue(  String value)
	{
		return (Result)valuesHash.get(value);
	}
	
	boolean storeValue ( Result assigned, String value )
	{
		valuesHash.put(value, assigned);
		return true;
	}
	
	boolean storeValueConst ( Result assigned, int value)
	{
		registerHash.put ( value, assigned);
		return true;
		
	}
	
	Result getValueConst ( int value)
	{
		return (Result)registerHash.get(value);
	}
	
	
}
