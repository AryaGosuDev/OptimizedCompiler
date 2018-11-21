import java.util.LinkedList;
import java.util.Stack;
import java.util.Hashtable;


public class CodeHandler 
{
	Parser thisParser ;
	
	int[] program = new int[2500] ; 
	int pc;
	private int GlobalRegister = 30;
	
	private LinkedList<LinkedList<SSAAssigns>> SSA_LL;
	private LinkedList<SSAAssigns> subll;
	private LinkedList<SSAAssigns> subllPrior;
	
	private Stack<Integer> controlStack;
	private Stack<Character> flowStack;
	private Stack<Integer> firstInstr; 
	
	LinkedList<liveNodes> IGNodes ;
	
	Hashtable<Integer, Integer> registerHash ;
	
	public CodeHandler ( Parser inP, LinkedList<liveNodes> inIGNodes )
	{
		thisParser = inP ;
		SSA_LL = thisParser.SSA_LL ;
		controlStack = new Stack<Integer>();
		flowStack = new Stack<Character>();
		firstInstr = new Stack<Integer>();
		registerHash = new Hashtable<Integer, Integer>(); 
		IGNodes = inIGNodes;
		
		loadRegisterHash();
		
		eliminateNonRegisterInstructions();
	}
	
	public void convertOutOfSSAForm() throws Exception
	{
		System.out.println("Converting out of ssa form");
		for ( int blocks = 0 ; blocks < SSA_LL.size() ; ++ blocks )
		{
			System.out.println(blocks);
			subll = SSA_LL.get(blocks);
			if ( subll.get(0).follow == null )
			{
				if ( subll.get(0).left != null ) //has an if statement
				{
					
					if ( subll.get(0).right.get(0).isifJoinBlock == true) // there is no else
					{
						for ( int phiInstructions = 1 ; phiInstructions < subll.get(0).right.size() ; ++phiInstructions )
						{
							SSAAssigns temp  ;
							subll.add(subll.size()-2 ,new SSAAssigns());
							
							temp = subll.get(subll.size()-3);
							temp.operation = DLX.MOVEREG;
							
							Result tempx = new Result();
							tempx.kind = DLX.RegInt;
							tempx.regno = subll.get(0).right.get(phiInstructions).pc;
							
							temp.x = tempx;
							temp.y = subll.get(0).right.get(phiInstructions).y;
						}	
					}
					else flowStack.add('r'); //there is an else
					flowStack.add('l'); //add the if to the stack, left direction
				}
				else if ( subll.get(0).inedge != null ) // add the phis of the loop header to the previous block
				{
					subllPrior = SSA_LL.get(blocks -1);
					for ( int phiInstructionsInWhile = 1 ; phiInstructionsInWhile < subll.size() ; ++phiInstructionsInWhile )
					{
						if ( subll.get(phiInstructionsInWhile).phi_assign != null)
						{	
							SSAAssigns temp  ;
							subllPrior.add(new SSAAssigns());
							temp = subllPrior.getLast();
							temp.operation = DLX.MOVEREG;
							
							Result tempResult = new Result ();
							tempResult.kind = DLX.RegInt ;
							tempResult.regno = subll.get(phiInstructionsInWhile).pc;
							
							temp.x = tempResult;
						    temp.y = subll.get(phiInstructionsInWhile).x;	
						}
					}
					//subll.get(0).whileCHVisited  ++;
					controlStack.push(subll.get(0).label);
				}
				else if ( subll.get(0).whilejoin != null ) //add the phis of the loop header to the last block in the loop before pointing back to the loop header 
				{
					subllPrior = SSA_LL.get(controlStack.peek());
					for ( int phiInstructionsInWhile = 1 ; phiInstructionsInWhile < subllPrior.size() ; ++phiInstructionsInWhile )
					{
						if ( subllPrior.get(phiInstructionsInWhile).phi_assign != null)
						{
							SSAAssigns temp  ;
							
							subll.add(subll.size() - 1, new SSAAssigns());
							temp = subll.get(subll.size()-2);
							temp.operation = DLX.MOVEREG;
							
							Result tempResult = new Result ();
							tempResult.kind = DLX.RegInt ;
							tempResult.regno = subllPrior.get(phiInstructionsInWhile).pc;
					
							temp.x = tempResult;
						    temp.y = subllPrior.get(phiInstructionsInWhile).y;	
						}
					}
					controlStack.pop();
				}
				else if ( subll.get(0).ifJoin != null ) //we have come to an if join
				{
					
					char dir = flowStack.pop(); //which side are we approaching
					for ( int phiInstructions = 1 ; phiInstructions < subll.get(0).ifJoin.size() ; ++phiInstructions )
					{
						if ( subll.get(0).ifJoin.get(phiInstructions).phi_assign != null)
						{
							SSAAssigns temp;
							
							subll.add(new SSAAssigns());
							
							temp = subll.getLast();
							temp.operation = DLX.MOVEREG;
							
							Result tempx = new Result();
							
							tempx.kind = DLX.RegInt;
							tempx.regno = subll.get(0).ifJoin.get(phiInstructions).pc;
							
							temp.x = tempx;
							
							if ( dir == 'l') temp.y = subll.get(0).ifJoin.get(phiInstructions).x;
							else if ( dir == 'r' ) temp.y = subll.get(0).ifJoin.get(phiInstructions).y;
							else throw new Exception ( "Not the right value on the flow stack in convert ssa");
							
						}
					}
					if ( dir == 'l') //add a bsr to the end of an if block
					{
						
						SSAAssigns temp;
						subll.add(new SSAAssigns());
						
						temp = subll.getLast();
						temp.operation = DLX.BSR;
						
						Result tempr = new Result();
						
						tempr.fixuplocation = subll.get(0).ifJoin.get(0).label;
						
						temp.r = tempr;
							
					}
					
				}
			}
			else if ( subll.get(0).follow != null && subll.get(0).follow.get(0).isifJoinBlock == true  ) //some of the links to the if join is not an ifjoin
			{
				char dir = flowStack.pop(); //which side are we approaching
				for ( int phiInstructions = 1 ; phiInstructions < subll.get(0).follow.size() ; ++phiInstructions )
				{
					if ( subll.get(0).follow.get(phiInstructions).phi_assign != null)
					{
						SSAAssigns temp;
						
						subll.add(new SSAAssigns());
						
						temp = subll.getLast();
						temp.operation = DLX.MOVEREG;
						
						Result tempx = new Result();
						
						tempx.kind = DLX.RegInt;
						tempx.regno = subll.get(0).follow.get(phiInstructions).pc;
						
						temp.x = tempx;
						
						if ( dir == 'l') temp.y = subll.get(0).follow.get(phiInstructions).x;
						else if ( dir == 'r' ) temp.y = subll.get(0).follow.get(phiInstructions).y;
						else throw new Exception ( "Not the right value on the flow stack in convert ssa");	
					}
				}
				if ( dir == 'l') //add a bse to the end of an if block
				{
					SSAAssigns temp;
					subll.add(new SSAAssigns());
					
					temp = subll.getLast();
					temp.operation = DLX.BSR;
					
					Result tempr = new Result();
					
					tempr.fixuplocation = subll.get(0).follow.get(0).label;
					
					temp.r = tempr;
						
				}
			}
			else if ( subll.get(0).follow != null && !controlStack.isEmpty() && subll.get(0).follow.get(0).label == controlStack.peek() )
			{ //add the phis of the loop header to the last block in the loop before pointing back to the loop header
				for ( int phiInstructions = 1 ; phiInstructions < subll.get(0).follow.size() ; ++phiInstructions )
				{
					if ( subll.get(0).follow.get(phiInstructions).phi_assign != null)
					{
						SSAAssigns temp;
						
						subll.add(subll.size() -1,new SSAAssigns());
						
						temp = subll.get(subll.size() -2);
						temp.operation = DLX.MOVEREG;
						
						Result tempx = new Result();
						
						tempx.kind = DLX.RegInt;
						tempx.regno = subll.get(0).follow.get(phiInstructions).pc;
						
						temp.x = tempx;
						
						temp.y = subll.get(0).follow.get(phiInstructions).y;	
					}
				}
				controlStack.pop();
			}
		}
	}
	
	public int[] returnCode () throws Exception
	{
		pc = 0;
		System.out.println("Loading Code");
		for ( int blocks = 0 ; blocks < SSA_LL.size() ; ++ blocks )
		{
			subll = SSA_LL.get(blocks);
			System.out.println(blocks);
			firstInstr.push(blocks);

			for ( int instructionsInBlock = 1 ; instructionsInBlock < subll.size() ; ++ instructionsInBlock )
			{
				if (subll.get(instructionsInBlock).kill != true && subll.get(instructionsInBlock).phi_assign == null )
				{
					
					switch ( subll.get(instructionsInBlock).operation)
					{
						case DLX.CMP:
							if ( subll.get(instructionsInBlock).x.kind == DLX.ConstInt && subll.get(instructionsInBlock).y.kind == DLX.ConstInt )
							{
								assignFirstInstruction(pc);
								program[pc++] = DLX.ADDI << 26 | GlobalRegister << 21 | subll.get(instructionsInBlock).x.value ;
								
								program[pc++] = DLX.CMPI << 26 | registerHash.get(subll.get(instructionsInBlock).pc) << 21 | GlobalRegister << 16 | subll.get(instructionsInBlock).y.value;  
							}
							else if ( subll.get(instructionsInBlock).x.kind == DLX.RegInt && subll.get(instructionsInBlock).y.kind == DLX.RegInt )
							{
								assignFirstInstruction(pc);
								program[pc++] = DLX.CMP << 26 | registerHash.get(subll.get(instructionsInBlock).pc) << 21 | subll.get(instructionsInBlock).x.regno << 16 | subll.get(instructionsInBlock).y.regno;  
							}
							else
							{
								assignFirstInstruction(pc);
								if ( subll.get(instructionsInBlock).x.kind == DLX.ConstInt ) 
									program[pc++] = DLX.CMPI << 26 | registerHash.get(subll.get(instructionsInBlock).pc) << 21 | registerHash.get(subll.get(instructionsInBlock).y.regno) << 16 | subll.get(instructionsInBlock).x.value;
								else program[pc++] = DLX.CMPI << 26 | registerHash.get(subll.get(instructionsInBlock).pc) << 21 | registerHash.get(subll.get(instructionsInBlock).x.regno) << 16 | subll.get(instructionsInBlock).y.value;
												
							}
							
						break;
				
						case DLX.WRD:
							switch (  subll.get(instructionsInBlock).r.kind)
							{
								case DLX.ConstInt :
									assignFirstInstruction(pc);
									program[pc++] = DLX.ADDI << 26 | GlobalRegister << 21 | subll.get(instructionsInBlock).r.value ;  
									program[pc++] = subll.get(instructionsInBlock).operation << 26 | 
											GlobalRegister << 16 ;
									
									break;
								case DLX.RegInt :
									assignFirstInstruction(pc);
									program[pc++] = DLX.WRD << 26 | registerHash.get(subll.get(instructionsInBlock).r.regno) << 16;
										
									break;
								case DLX.VarInt :
									break;
							}
									
							break;
							
						case DLX.MOVEREG :
							
							if ( subll.get(instructionsInBlock).y.kind == DLX.VarInt && subll.get(instructionsInBlock).y.value == 0 )
							{
								throw new Exception ( "YOU HAVE AN ERROR IN YOUR CODE. \nYOU ARE USING AN UN UNITIALIZED VARIABLE:\n" + thisParser.identArray[subll.get(instructionsInBlock).y.address].nameOfIdent + "\nAT INSTRUCTION " + pc  );
							}
							
							if ( subll.get(instructionsInBlock).y.kind == DLX.ConstInt && registerHash.get(subll.get(instructionsInBlock).x.regno) != null)
							{
								assignFirstInstruction(pc);
								program[pc++] = DLX.ADDI << 26 | registerHash.get(subll.get(instructionsInBlock).x.regno) << 21 | subll.get(instructionsInBlock).y.value  ;
							}
							else if ( subll.get(instructionsInBlock).y.kind != DLX.VarInt && registerHash.get(subll.get(instructionsInBlock).x.regno) != null && registerHash.get(subll.get(instructionsInBlock).y.regno) != null)
							{
								assignFirstInstruction(pc);
								program[pc++] = DLX.ADD << 26 | registerHash.get(subll.get(instructionsInBlock).x.regno) << 21 | registerHash.get(subll.get(instructionsInBlock).y.regno) << 16 ;
							}
							
							break;
							
						default:
							
							break;
						
					}
					
					if (subll.get(instructionsInBlock).operation >= DLX.BEQ && subll.get(instructionsInBlock).operation <= DLX.BGT  )
					{
						assignFirstInstruction(pc);
						program[pc++] = subll.get(instructionsInBlock).operation << 26 | registerHash.get(subll.get(instructionsInBlock).r.regno) << 21 | subll.get(instructionsInBlock).r.fixuplocation ;  
						
					}
					else if ( subll.get(instructionsInBlock).operation == DLX.BSR )
					{
						assignFirstInstruction(pc);
						program[pc++] = subll.get(instructionsInBlock).operation << 26 | subll.get(instructionsInBlock).r.fixuplocation;	
					}
					else if ( subll.get(instructionsInBlock).operation >= DLX.ADD && subll.get(instructionsInBlock).operation <= DLX.DIV)
					{
						assignFirstInstruction(pc);
						
						if ( subll.get(instructionsInBlock).x.kind == DLX.VarInt && subll.get(instructionsInBlock).x.value == 0) 
							throw new Exception ( "YOU HAVE AN ERROR IN YOUR CODE. \nYOU ARE USING AN UN UNITIALIZED VARIABLE:\n" + thisParser.identArray[subll.get(instructionsInBlock).x.address].nameOfIdent + "\nAT INSTRUCTION " + pc  ); 
						
						if ( subll.get(instructionsInBlock).y.kind == DLX.VarInt && subll.get(instructionsInBlock).y.value == 0)
							throw new Exception ( "YOU HAVE AN ERROR IN YOUR CODE. \nYOU ARE USING AN UN UNITIALIZED VARIABLE:\n" + thisParser.identArray[subll.get(instructionsInBlock).y.address].nameOfIdent + "\nAT INSTRUCTION " + pc  ); 
							
						
						
						if (subll.get(instructionsInBlock).x.kind == DLX.RegInt && subll.get(instructionsInBlock).y.kind == DLX.RegInt
								&& registerHash.get(subll.get(instructionsInBlock).x.regno) != null && registerHash.get(subll.get(instructionsInBlock).y.regno) != null )
						{
							program[pc++] = subll.get(instructionsInBlock).operation << 26 | 
									registerHash.get(subll.get(instructionsInBlock).pc) << 21 |
									registerHash.get(subll.get(instructionsInBlock).x.regno) << 16 |
									registerHash.get(subll.get(instructionsInBlock).y.regno);
									
									
						}
						else if (subll.get(instructionsInBlock).x.kind == DLX.RegInt && registerHash.get(subll.get(instructionsInBlock).x.regno) != null)
						{
							program[pc++] = (subll.get(instructionsInBlock).operation + 16) << 26 | 
									registerHash.get(subll.get(instructionsInBlock).pc) << 21 |
									registerHash.get(subll.get(instructionsInBlock).x.regno) << 16 |
									subll.get(instructionsInBlock).y.value;
							
						}
						else if (subll.get(instructionsInBlock).y.kind == DLX.RegInt && registerHash.get(subll.get(instructionsInBlock).y.regno) != null)
						{
							program[pc++] = (subll.get(instructionsInBlock).operation + 16) << 26 | 
									registerHash.get(subll.get(instructionsInBlock).pc) << 21 |
									registerHash.get(subll.get(instructionsInBlock).y.regno) << 16 |
									subll.get(instructionsInBlock).x.value;
						}			
					}	
				}
			}
		}
		
		assignFirstInstruction(pc);
		program[pc] = DLX.RET << 26;
		
		if ( !firstInstr.isEmpty()) throw new Exception ( "Error, The First instruction stack is not empty!");
		
		for ( int instructions = 0 ; instructions < program.length ; ++ instructions )
		{
			if ( (program[instructions] >>> 26) >= DLX.BEQ && (program[instructions] >>> 26) <= DLX.BSR   ) //now change all the branches to real instructions by introducing the first instruction of a particular block
			{
				int fixuplocation = program[instructions] << 16;
				fixuplocation = fixuplocation  >> 16;
				program[instructions] = program[instructions] & 0xFFFF0000;
				if ( (SSA_LL.get(fixuplocation).get(0).firstRealInstruction - instructions) < 0 )
				{//jump an offset meant to go back ward by introducing a jump instead of a branch 
					program[instructions] = (DLX.JSR << 26) | ( SSA_LL.get(fixuplocation).get(0).firstRealInstruction * 4 );	
				}
				else program[instructions] = program[instructions] | (SSA_LL.get(fixuplocation).get(0).firstRealInstruction - instructions);
			}	
		}
		
		
		
		int[] realProgram = new int[pc+1];
		
		for ( int i = 0 ; i < realProgram.length ; ++ i)
		{
			realProgram[i] = program[i]; 
		}
		
		return realProgram;
	}
	
	private void loadRegisterHash ( ) //load all the register in the ig nodes to a hash table to easily access register values
	{
		System.out.println("Loading Register Hash");
		for ( int iterateIGNodes = 0; iterateIGNodes < IGNodes.size() ; ++ iterateIGNodes )
		{
			
			registerHash.put(IGNodes.get(iterateIGNodes).instruction, IGNodes.get(iterateIGNodes).allocatedReg+1);
			System.out.println ( IGNodes.get(iterateIGNodes).instruction);
			if ( IGNodes.get(iterateIGNodes).coalesce.size() > 0 )
			{
				for ( int iterateCoal = 0; iterateCoal <  IGNodes.get(iterateIGNodes).coalesce.size() ; ++iterateCoal )
				{
					registerHash.put(IGNodes.get(iterateIGNodes).coalesce.get(iterateCoal).instruction, IGNodes.get(iterateIGNodes).allocatedReg +1);
					System.out.println ( IGNodes.get(iterateIGNodes).coalesce.get(iterateCoal).instruction);
				}
			}	
		}
	}
	
	private void eliminateNonRegisterInstructions() //if there are instructions that dont have any registers, kill them
	{
		for ( int blocks = 0 ; blocks < SSA_LL.size() ; ++blocks )
		{
			subll = SSA_LL.get(blocks);
			for ( int instr = 1 ; instr < subll.size() ; ++instr )
			{
				if ( subll.get(instr).kill != true && ( subll.get(instr).operation >= DLX.ADD && subll.get(instr).operation <= DLX.DIV ) && registerHash.get(subll.get(instr).pc) == null )
				{
					subll.get(instr).kill = true;
				}
				if ( subll.get(instr).kill != true && ( subll.get(instr).operation >= DLX.ADDI && subll.get(instr).operation <= DLX.DIVI ) && registerHash.get(subll.get(instr).pc) == null  )
				{
					subll.get(instr).kill = true;
				}
			}
		}
	}
	
	private void assignFirstInstruction(int inPC)
	{ //assign the first instruction of a block to its block and if there are any blocks before it
		while (!firstInstr.isEmpty() )
		{
			SSA_LL.get(firstInstr.pop()).get(0).firstRealInstruction = inPC;	
		}
	}
}
