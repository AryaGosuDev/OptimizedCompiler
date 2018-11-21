import java.util.LinkedList;


public class CSEHandler {
	
	LinkedList<LinkedList<SSAAssigns>> SSA_LL;
	LinkedList<LinkedList<SSAAssigns>> SSA_LL_CSE ;
	
	
	public CSEHandler ( LinkedList<LinkedList<SSAAssigns>> thisSSA)
	{
		SSA_LL = thisSSA ;
		
		printLevels();
		
		PerformGlobalCSE ( );
		
	}
	
	void PerformGlobalCSE ( )
	{
		int bbLevel = 0;
		boolean found = false;
		LinkedList<Integer> domLL = SSA_LL.get(bbLevel).get(0).dominators;
		LinkedList<SSAAssigns> llsub = SSA_LL.get(bbLevel);
		
		SSA_LL_CSE = new LinkedList<LinkedList<SSAAssigns>>();
		LinkedList<SSAAssigns> tempLLsub = new LinkedList<SSAAssigns>() ;
		
		SSA_LL_CSE.add (tempLLsub);
		
		for ( int global = 0 ; global < SSA_LL.size() ; ++ global) //for all bb's
		{
			if ( global == 19)
			{
				int dfgfd = 2 ;
				
			}
			llsub = SSA_LL.get(global);
			for ( int i = 1 ; i < llsub.size() ; ++i  ) //for all instructions in a bb
			{
				
				if ( i ==1)
				{
					int dfgfd = 2 ;
					
				}
				
				replaceInstructions (llsub, i);
				
				
				
				domLL = SSA_LL.get(global).get(0).dominators ;
				for ( int j = 0; j < domLL.size() ; ++j) //for all the dominator bb's
				{
					System.out.println("bb : " + global +  " instr: " + i + " doms: " + domLL.get(j));
					if (global == 14 && i == 2 && j ==8)
					{
						int dfgfd = 2 ;
						
					}
					if ( llsub.get(i).operation != DLX.MOV && llsub.get(i).operation != DLX.PHI && (llsub.get(i).operation < DLX.BEQ || llsub.get(i).operation > DLX.BSR) && llsub.get(i).operation != DLX.WRD  && llsub.get(i).kill != true  )
					{
						int k;
						for (  k = 0 ; k < SSA_LL_CSE.get(domLL.get(j)).size() && found == false ; ++k ) //for all the stored instructions in the auxillary data structure
						{
							
							if ( SSA_LL_CSE.get(domLL.get(j)).get(k).operation == llsub.get(i).operation &&
									llsub.get(i).x.kind == DLX.RegInt && llsub.get(i).y.kind == DLX.RegInt &&
									SSA_LL_CSE.get(domLL.get(j)).get(k).x.kind == DLX.RegInt && SSA_LL_CSE.get(domLL.get(j)).get(k).y.kind == DLX.RegInt &&
									((SSA_LL_CSE.get(domLL.get(j)).get(k).x.regno == llsub.get(i).x.regno && SSA_LL_CSE.get(domLL.get(j)).get(k).y.regno == llsub.get(i).y.regno) ||
									(SSA_LL_CSE.get(domLL.get(j)).get(k).y.regno == llsub.get(i).x.regno && SSA_LL_CSE.get(domLL.get(j)).get(k).x.regno == llsub.get(i).y.regno)))
							{
								System.out.println( " enter 1");
								found = true;
							}
							else if ( SSA_LL_CSE.get(domLL.get(j)).get(k).operation == llsub.get(i).operation &&
									llsub.get(i).x.kind == DLX.VarInt && llsub.get(i).y.kind == DLX.VarInt &&
									SSA_LL_CSE.get(domLL.get(j)).get(k).x.kind == DLX.VarInt && SSA_LL_CSE.get(domLL.get(j)).get(k).y.kind == DLX.VarInt &&
									((SSA_LL_CSE.get(domLL.get(j)).get(k).x.address == llsub.get(i).x.address && SSA_LL_CSE.get(domLL.get(j)).get(k).y.address == llsub.get(i).y.address) ||
									(SSA_LL_CSE.get(domLL.get(j)).get(k).y.address == llsub.get(i).x.address && SSA_LL_CSE.get(domLL.get(j)).get(k).x.address == llsub.get(i).y.address)) && 
									((SSA_LL_CSE.get(domLL.get(j)).get(k).x.value == llsub.get(i).x.value && SSA_LL_CSE.get(domLL.get(j)).get(k).y.value == llsub.get(i).y.value) ||
									(SSA_LL_CSE.get(domLL.get(j)).get(k).y.value == llsub.get(i).x.value && SSA_LL_CSE.get(domLL.get(j)).get(k).x.value == llsub.get(i).y.value)))
							{
								System.out.println( " enter 2");
								found = true;
							}
							else if ( SSA_LL_CSE.get(domLL.get(j)).get(k).operation == llsub.get(i).operation &&
									llsub.get(i).x.kind == DLX.AddrsInt && llsub.get(i).y.kind == DLX.AddrsInt &&
									SSA_LL_CSE.get(domLL.get(j)).get(k).x.kind == DLX.AddrsInt && SSA_LL_CSE.get(domLL.get(j)).get(k).y.kind == DLX.AddrsInt &&
									((SSA_LL_CSE.get(domLL.get(j)).get(k).x.address == llsub.get(i).x.address && SSA_LL_CSE.get(domLL.get(j)).get(k).y.address == llsub.get(i).y.address) ||
									(SSA_LL_CSE.get(domLL.get(j)).get(k).y.address == llsub.get(i).x.address && SSA_LL_CSE.get(domLL.get(j)).get(k).x.address == llsub.get(i).y.address)))
							{
								System.out.println( " enter 3");
								found = true;
							}
							else if ( SSA_LL_CSE.get(domLL.get(j)).get(k).operation == llsub.get(i).operation &&
									llsub.get(i).x.kind == DLX.ConstInt && llsub.get(i).y.kind == DLX.ConstInt &&
									SSA_LL_CSE.get(domLL.get(j)).get(k).x.kind == DLX.ConstInt && SSA_LL_CSE.get(domLL.get(j)).get(k).y.kind == DLX.ConstInt &&
									((SSA_LL_CSE.get(domLL.get(j)).get(k).x.value == llsub.get(i).x.value && SSA_LL_CSE.get(domLL.get(j)).get(k).y.value == llsub.get(i).y.value) ||
									(SSA_LL_CSE.get(domLL.get(j)).get(k).y.value == llsub.get(i).x.value && SSA_LL_CSE.get(domLL.get(j)).get(k).x.value == llsub.get(i).y.value)))
							{
								System.out.println( " enter 4");
								found = true;
							}
							else if ( SSA_LL_CSE.get(domLL.get(j)).get(k).operation == llsub.get(i).operation &&
									llsub.get(i).x.kind == DLX.RegInt && llsub.get(i).y.kind == DLX.VarInt &&
									SSA_LL_CSE.get(domLL.get(j)).get(k).x.kind == DLX.RegInt && SSA_LL_CSE.get(domLL.get(j)).get(k).y.kind == DLX.VarInt &&
									SSA_LL_CSE.get(domLL.get(j)).get(k).x.regno == llsub.get(i).x.regno &&
									SSA_LL_CSE.get(domLL.get(j)).get(k).y.address == llsub.get(i).y.address &&
									SSA_LL_CSE.get(domLL.get(j)).get(k).y.value == llsub.get(i).y.value)
									
							{
								System.out.println( " enter 5 " + SSA_LL_CSE.get(domLL.get(j)).get(k).x.address + " " + llsub.get(i).x.address);
								found = true;
							}
							else if ( SSA_LL_CSE.get(domLL.get(j)).get(k).operation == llsub.get(i).operation &&
									llsub.get(i).x.kind == DLX.VarInt && llsub.get(i).y.kind == DLX.RegInt &&
									SSA_LL_CSE.get(domLL.get(j)).get(k).x.kind == DLX.VarInt && SSA_LL_CSE.get(domLL.get(j)).get(k).y.kind == DLX.RegInt &&
									SSA_LL_CSE.get(domLL.get(j)).get(k).y.regno == llsub.get(i).y.regno &&
									SSA_LL_CSE.get(domLL.get(j)).get(k).x.address == llsub.get(i).x.address &&
									SSA_LL_CSE.get(domLL.get(j)).get(k).x.value == llsub.get(i).x.value)
									
							{
								System.out.println( " enter 6");
								found = true;
							}
							else if ( SSA_LL_CSE.get(domLL.get(j)).get(k).operation == llsub.get(i).operation &&
									llsub.get(i).x.kind == DLX.RegInt && llsub.get(i).y.kind == DLX.VarInt &&
									SSA_LL_CSE.get(domLL.get(j)).get(k).x.kind == DLX.VarInt && SSA_LL_CSE.get(domLL.get(j)).get(k).y.kind == DLX.RegInt &&
									SSA_LL_CSE.get(domLL.get(j)).get(k).y.regno == llsub.get(i).x.regno &&
									SSA_LL_CSE.get(domLL.get(j)).get(k).x.address == llsub.get(i).y.address &&
									SSA_LL_CSE.get(domLL.get(j)).get(k).x.value == llsub.get(i).y.value)
									
							{
								System.out.println( " enter 7");
								found = true;
							}
							else if ( SSA_LL_CSE.get(domLL.get(j)).get(k).operation == llsub.get(i).operation &&
									llsub.get(i).x.kind == DLX.VarInt && llsub.get(i).y.kind == DLX.RegInt &&
									SSA_LL_CSE.get(domLL.get(j)).get(k).x.kind == DLX.RegInt && SSA_LL_CSE.get(domLL.get(j)).get(k).y.kind == DLX.VarInt &&
									SSA_LL_CSE.get(domLL.get(j)).get(k).x.regno == llsub.get(i).y.regno &&
									SSA_LL_CSE.get(domLL.get(j)).get(k).y.address == llsub.get(i).x.address &&
									SSA_LL_CSE.get(domLL.get(j)).get(k).y.value == llsub.get(i).x.value)
									
							{
								System.out.println( " enter 8");
								found = true;
							}
							//////////consts
							else if ( SSA_LL_CSE.get(domLL.get(j)).get(k).operation == llsub.get(i).operation &&
									llsub.get(i).x.kind == DLX.ConstInt && llsub.get(i).y.kind == DLX.VarInt &&
									SSA_LL_CSE.get(domLL.get(j)).get(k).x.kind == DLX.ConstInt && SSA_LL_CSE.get(domLL.get(j)).get(k).y.kind == DLX.VarInt &&
									SSA_LL_CSE.get(domLL.get(j)).get(k).x.value == llsub.get(i).x.value &&
									SSA_LL_CSE.get(domLL.get(j)).get(k).y.address == llsub.get(i).y.address &&
									SSA_LL_CSE.get(domLL.get(j)).get(k).y.value == llsub.get(i).y.value)
									
							{
								System.out.println( " enter 9 " + SSA_LL_CSE.get(domLL.get(j)).get(k).x.address + " " + llsub.get(i).x.address);
								found = true;
							}
							else if ( SSA_LL_CSE.get(domLL.get(j)).get(k).operation == llsub.get(i).operation &&
									llsub.get(i).x.kind == DLX.VarInt && llsub.get(i).y.kind == DLX.ConstInt &&
									SSA_LL_CSE.get(domLL.get(j)).get(k).x.kind == DLX.VarInt && SSA_LL_CSE.get(domLL.get(j)).get(k).y.kind == DLX.ConstInt &&
									SSA_LL_CSE.get(domLL.get(j)).get(k).y.value == llsub.get(i).y.value &&
									SSA_LL_CSE.get(domLL.get(j)).get(k).x.address == llsub.get(i).x.address &&
									SSA_LL_CSE.get(domLL.get(j)).get(k).x.value == llsub.get(i).x.value)
									
							{
								System.out.println( " enter 10");
								found = true;
							}
							else if ( SSA_LL_CSE.get(domLL.get(j)).get(k).operation == llsub.get(i).operation &&
									llsub.get(i).x.kind == DLX.VarInt && llsub.get(i).y.kind == DLX.ConstInt &&
									SSA_LL_CSE.get(domLL.get(j)).get(k).x.kind == DLX.ConstInt && SSA_LL_CSE.get(domLL.get(j)).get(k).y.kind == DLX.VarInt &&
									SSA_LL_CSE.get(domLL.get(j)).get(k).x.value == llsub.get(i).y.value &&
									SSA_LL_CSE.get(domLL.get(j)).get(k).y.address == llsub.get(i).x.address &&
									SSA_LL_CSE.get(domLL.get(j)).get(k).y.value == llsub.get(i).x.value)
									
							{
								System.out.println( " enter 11");
								found = true;
							}
							else if ( SSA_LL_CSE.get(domLL.get(j)).get(k).operation == llsub.get(i).operation &&
									llsub.get(i).x.kind == DLX.ConstInt && llsub.get(i).y.kind == DLX.VarInt &&
									SSA_LL_CSE.get(domLL.get(j)).get(k).x.kind == DLX.VarInt && SSA_LL_CSE.get(domLL.get(j)).get(k).y.kind == DLX.ConstInt &&
									SSA_LL_CSE.get(domLL.get(j)).get(k).y.value == llsub.get(i).x.value &&
									SSA_LL_CSE.get(domLL.get(j)).get(k).x.address == llsub.get(i).y.address &&
									SSA_LL_CSE.get(domLL.get(j)).get(k).x.value == llsub.get(i).y.value)
									
							{
								System.out.println( " enter 12");
								found = true;
							}
							//////////const vs reg
							else if ( SSA_LL_CSE.get(domLL.get(j)).get(k).operation == llsub.get(i).operation &&
									llsub.get(i).x.kind == DLX.ConstInt && llsub.get(i).y.kind == DLX.RegInt &&
									SSA_LL_CSE.get(domLL.get(j)).get(k).x.kind == DLX.ConstInt && SSA_LL_CSE.get(domLL.get(j)).get(k).y.kind == DLX.RegInt &&
									SSA_LL_CSE.get(domLL.get(j)).get(k).x.value == llsub.get(i).x.value &&
									SSA_LL_CSE.get(domLL.get(j)).get(k).y.regno == llsub.get(i).y.regno)
									
							{
								System.out.println( " enter 13 " + SSA_LL_CSE.get(domLL.get(j)).get(k).x.address + " " + llsub.get(i).x.address);
								found = true;
							}
							else if ( SSA_LL_CSE.get(domLL.get(j)).get(k).operation == llsub.get(i).operation &&
									llsub.get(i).x.kind == DLX.RegInt && llsub.get(i).y.kind == DLX.ConstInt &&
									SSA_LL_CSE.get(domLL.get(j)).get(k).x.kind == DLX.RegInt && SSA_LL_CSE.get(domLL.get(j)).get(k).y.kind == DLX.ConstInt &&
									SSA_LL_CSE.get(domLL.get(j)).get(k).x.value == llsub.get(i).x.value &&
									SSA_LL_CSE.get(domLL.get(j)).get(k).y.regno == llsub.get(i).y.regno )
									
							{
								System.out.println( " enter 14");
								found = true;
							}
							else if ( SSA_LL_CSE.get(domLL.get(j)).get(k).operation == llsub.get(i).operation &&
									llsub.get(i).x.kind == DLX.RegInt && llsub.get(i).y.kind == DLX.ConstInt &&
									SSA_LL_CSE.get(domLL.get(j)).get(k).x.kind == DLX.ConstInt && SSA_LL_CSE.get(domLL.get(j)).get(k).y.kind == DLX.RegInt &&
									SSA_LL_CSE.get(domLL.get(j)).get(k).x.value == llsub.get(i).y.value &&
									SSA_LL_CSE.get(domLL.get(j)).get(k).y.regno == llsub.get(i).x.regno )
									
									
							{
								System.out.println( " enter 15");
								found = true;
							}
							else if ( SSA_LL_CSE.get(domLL.get(j)).get(k).operation == llsub.get(i).operation &&
									llsub.get(i).x.kind == DLX.ConstInt && llsub.get(i).y.kind == DLX.RegInt &&
									SSA_LL_CSE.get(domLL.get(j)).get(k).x.kind == DLX.RegInt && SSA_LL_CSE.get(domLL.get(j)).get(k).y.kind == DLX.ConstInt &&
									SSA_LL_CSE.get(domLL.get(j)).get(k).x.regno == llsub.get(i).y.regno &&
									SSA_LL_CSE.get(domLL.get(j)).get(k).y.value == llsub.get(i).x.value)
									
									
							{
								System.out.println( " enter 16");
								found = true;
							}
						}
						
						if ( found == false && global == domLL.get(j)) //only add if we are inside the bb where instructions are being killed
						{
							System.out.println("Adding to block : " + domLL.get(j) );
							SSAAssigns tempAssigns = new SSAAssigns();
							tempAssigns.operation = llsub.get(i).operation;
							//tempAssigns.x = llsub.get(i).x;
							Result tempResx = new Result();
							copyResult (tempResx, llsub.get(i).x );
							Result tempResy = new Result();
							copyResult (tempResy, llsub.get(i).y );
							
							tempAssigns.x = tempResx;
							tempAssigns.y = tempResy;
							
							//tempAssigns.y = llsub.get(i).y;
							tempAssigns.pc = llsub.get(i).pc;
							SSA_LL_CSE.get(domLL.get(j)).add(tempAssigns);
						}
						else if ( found == true)
						{
							System.out.println("Adding to block : " + domLL.get(j) );
							llsub.get(i).kill = true;
							llsub.get(i).substituteKill = SSA_LL_CSE.get(domLL.get(j)).get(k-1).pc ;
						}
						found = false;
					}
				}
				
			}
			tempLLsub = new LinkedList<SSAAssigns>() ;
			SSA_LL_CSE.add (tempLLsub);
		}
		
		
	}
	
	void copyResult (Result copyTo , Result copyFrom )
	{
		copyTo.address = copyFrom.address;
		copyTo.fixuplocation = copyFrom.fixuplocation;
		copyTo.kind = copyFrom.kind;
		copyTo.regno = copyFrom.regno;
		copyTo.value = copyFrom.value;
		copyTo.cond = copyFrom.cond;
		
	}
	
	void replaceInstructions (LinkedList<SSAAssigns> llsub, int i  )
	{
		if ( llsub.get(i).operation == DLX.MOV && llsub.get(i).r != null && 
				(llsub.get(i).r.kind == DLX.RegInt || llsub.get(i).r.kind == DLX.CondInt)  && 
				findInstruction( llsub.get(i).r.regno ) != null &&  
				findInstruction( llsub.get(i).r.regno ).kill == true) // substitute an instruction
		{
			llsub.get(i).r.regno = replace ( llsub.get(i).r);
			//llsub.get(i).r.regno = llsub.get(llsub.get(i).r.regno+1).substituteKill ;
		}
		
		if ( llsub.get(i).operation >=  DLX.ADD && llsub.get(i).operation <=  DLX.DIV )
		{
			if ( llsub.get(i).x.kind == DLX.RegInt && 
					findInstruction( llsub.get(i).x.regno ).kill == true && 
					findInstruction( llsub.get(i).x.regno) != null)
				llsub.get(i).x.regno = replace ( llsub.get(i).x);
			
			if ( llsub.get(i).y.kind == DLX.RegInt && 
					findInstruction( llsub.get(i).y.regno ).kill == true && 
					findInstruction( llsub.get(i).y.regno) != null)
				llsub.get(i).y.regno = replace ( llsub.get(i).y);
		}
		
		if ( llsub.get(i).operation == DLX.ADDA )
		{
			if ( llsub.get(i).x.kind == DLX.RegInt && 
					findInstruction( llsub.get(i).x.regno ).kill == true && 
					findInstruction( llsub.get(i).x.regno) != null)
			{
				llsub.get(i).x.regno = replace ( llsub.get(i).x);
			}
			
			if ( llsub.get(i).y.kind == DLX.RegInt && 
					findInstruction( llsub.get(i).y.regno ).kill == true && 
					findInstruction( llsub.get(i).y.regno) != null)
			{
				llsub.get(i).y.regno = replace ( llsub.get(i).y);
			}
			
		}
	}
	
	void printLevels()
	{
		for ( int i = 0 ; i < SSA_LL.size() ; ++i )
		{
			String outDoms = i + ":  ";
			LinkedList domLL = SSA_LL.get(i).get(0).dominators;
			for ( int j = 0; j < domLL.size() ; ++ j)
			{
				outDoms += domLL.get(j) + " " ;
			}
			
			//System.out.println ( outDoms );
			
		}
	}
	
	SSAAssigns findInstruction ( int pc)
	{
		for ( int global = 0 ; global < SSA_LL.size() ; ++global)
		{
			for ( int i = 1 ; i < SSA_LL.get(global).size() ; ++i)
			{
				if (SSA_LL.get(global).get(i).pc == pc ) return SSA_LL.get(global).get(i);
			
			}
		}
		return null ;
		
	}
	
	int replace ( Result r )
	{
		for ( int global = 0 ; global < SSA_LL.size() ; ++global)
		{
			for ( int i = 1 ; i < SSA_LL.get(global).size() ; ++i)
			{
				if (SSA_LL.get(global).get(i).pc ==  r.regno ) return SSA_LL.get(global).get(i).substituteKill;
			
			}
		}
		return 0;
		
	}
}
