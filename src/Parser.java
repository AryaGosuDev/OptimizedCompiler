// Afshin Mahini   89245265

import java.io.*;
import java.lang.String;
import java.util.Hashtable;
import java.lang.Character;
import java.util.LinkedList;
import java.util.Stack;


public class Parser {

	//public String[] program;
  public int[] program;

  private Scanner s;
  public FuncHandler f;
  
  private int pc = 0 ;
  private int sp = 0 ;
  private int lastOpUsed ;
  private int LastLetId ;
  
  private boolean mainInit = false;
  
  private int varArrayCount = 0 ;
  String nameOfProc ;
  
  //ALL VARS TO RESET FOR FUNCTIONS PROCS
  private int numOfFunctions = 0;
  private int funcVarCount = 0 ;
  private int funcVarArrayCount = 0;
  
  private boolean returnsVal = false ;
  private int pcf = 0 ;
  private boolean inFunction = false;
  public String[] codetemp ;
  public String[] code ;
  private int funcVarLoop = 0 ;
  public boolean[] Registers;
  public LinkedList<LinkedList<SSAAssigns>> SSA_LL;
  public int op;
  public int bbLevel = 0;
  
  private char direction;
  
  public Stack<controlStackItem> nestStack;
  public Stack<Character> dirStack ;
  
  //private bool codeArray = false ;
  
  public IdentObjectComp[] identArray = new IdentObjectComp[1000];
  private IdentObjectComp temp ;
  
  public IdentObjectComp[] funcIdentObjects = new IdentObjectComp[1000];
  private IdentObjectComp funcArgTemp ;
  
  public IdentObjectComp[] argObjects = new IdentObjectComp[1000];
  
  /* constructor */
    public Parser (String filename) 
    {
      s = new Scanner(filename);
      
      
      Registers = new boolean[32];
      
      
      SSA_LL = new LinkedList<LinkedList<SSAAssigns>>() ;
      
      nestStack = new Stack();
      dirStack = new Stack();
      f = new FuncHandler (SSA_LL, s, identArray  );
       
  }
    
  public int[] computation() throws Exception {
    int i = 0 ;
    for ( i = 0 ; i < identArray.length ; ++i )
    {
    	identArray[i] = new IdentObjectComp();
    }
    
    program = new int[1000] ;
    codetemp = new String[1000];
    
    int oneArrayCount = 0 ;
    
    eat(Scanner.mainToken);
    while (  (s.sym != Scanner.beginToken) && (s.sym != Scanner.procToken) && (s.sym != Scanner.funcToken)  )
    {
    	varDecl();
    	
    }
    
    while ( s.sym == Scanner.procToken || s.sym == Scanner.funcToken )
    {
    	numOfFunctions++;
    	
        returnsVal = false  ;
        
        f.setFunctionNumber(numOfFunctions, bbLevel );
        bbLevel = f.handleFunction();
        bbLevel ++;
        s.purgeAllIds(identArray);
        
        
	    
    }
    
    eat(Scanner.beginToken );
    //bbLevel = 0;
    statSequence();
    eat (Scanner.endToken );
    
    eat ( Scanner.periodToken ) ;
    
    
    program[pc++] = DLX.assemble(DLX.RET, 0);
    
    i = 0 ;
    int[] oneDimensionArray = new int[varArrayCount] ;
    
    oneArrayCount = 0;
    
    System.out.println( "END");
    
    for ( i = 0 ; i < identArray.length ; ++i )
    {
    	if ( identArray[i].isArray )
    	{
    		oneDimensionArray[oneArrayCount++] = identArray[i].numOfElements ;
    		
    	}
    }
    
    return program;
    
  }
  
  /* MOVED TO CLASS FUNCHANDLER
  public void funcDecl() throws Exception
  {
  	if ( s.sym == Scanner.procToken ) eat ( Scanner.procToken ) ;
  	else if ( s.sym == Scanner.funcToken ) eat ( Scanner.funcToken ) ;
  	
  	nameOfProc = s.Id2String( s.id )  ;
  	
  	int loop = 0 ;
  	
  	eat ( Scanner.ident );
  	
  	eat ( Scanner.openparenToken ) ;
  	
  	int i ;
  	for ( i = 0 ; i < funcIdentObjects.length ; ++i )
	{
	    funcIdentObjects[i] = new IdentObjectComp(numOfFunctions);
    }
    	
	for ( i = 0 ; i < argObjects.length ; ++i )
	{
		argObjects[i] = new IdentObjectComp( numOfFunctions , true) ;
	}
  	
  	while ( s.sym != Scanner.closeparenToken )
  	{
  		
  		  if ( s.sym == Scanner.ident )
  		  {
				eat(Scanner.ident);
				temp = argObjects[loop++] ;
				temp.nameOfIdent = s.Id2String( s.id ) ;
				
				if ( s.sym == Scanner.commaToken ) eat ( Scanner.commaToken ) ;
    	  }
    }
    	eat ( Scanner.closeparenToken );
    	eat ( Scanner.semiToken );
    	
    	while ( s.sym != Scanner.beginToken )
    	{
    		varDecl() ;
    		
    	}
    	eat ( Scanner.beginToken );
    	
    	
    	
    	eat ( Scanner.endToken );
    	eat ( Scanner.semiToken ) ;
    } */
    
  	
  public void varDecl() throws Exception
  { 
    if ( s.sym == Scanner.varToken )
    {
      
    	eat(Scanner.varToken);
    	eat(Scanner.ident);
    	
    	temp = identArray[s.id] ;
    	temp.nameOfIdent = s.Id2String( s.id ) ;
    	
    	if ( temp.isDeclared == true ) varDeclError ( "Already declared");
    	
    	temp.isDeclared = true;
    	while ( s.sym != Scanner.semiToken )
    	{
    		if ( s.sym == Scanner.commaToken )
    		{
    			eat( Scanner.commaToken);
    			if ( s.sym != Scanner.ident )
    				error ( Scanner.ident , s.sym );
    		}
    		else if ( s.sym == Scanner.ident )
    		{
    			eat(Scanner.ident);
    			temp = identArray[s.id] ;
    			temp.nameOfIdent = s.Id2String( s.id ) ;
    			temp.isDeclared = true;
    		}
    		
    		else error ( Scanner.commaToken , s.sym ) ;
    	}
    	eat ( Scanner.semiToken );
    }
    else if ( s.sym == Scanner.arrToken )
    {
    	eat ( Scanner.arrToken ) ;
        eat ( Scanner.openbracToken );
        int dimenCount = 1 ;
        
		int dimenLoop = 0 ;
		int[] tempDimen = new int[100] ;
        
        while ( s.sym != Scanner.ident )
        {
        	dimenCount *= s.val ;
        	eat ( Scanner.number );
        	tempDimen[dimenLoop++] = s.val ;
        	eat ( Scanner.closebracToken );
        	if ( s.sym == Scanner.openbracToken )
        	{
        		eat ( Scanner.openbracToken );
        	}
        }
        
        int[] Dimen = new int[dimenLoop] ;
		    
	int i = 0 ;
	for ( i = 0 ; i < dimenLoop ; ++i )
	{
		Dimen[i] = tempDimen[i] ;
	}
        
    while ( s.sym != Scanner.semiToken )
	{
		if ( s.sym == Scanner.commaToken )
	    	{
	    		eat( Scanner.commaToken);
	    		if ( s.sym != Scanner.ident )
	    			error ( Scanner.ident , s.sym );
	    	}
	    	else if ( s.sym == Scanner.ident )
	    	{
	    		eat(Scanner.ident);
	    		temp = identArray[s.id] ;
	    		if ( temp.isDeclared == true ) varDeclError ( "Already declared");
	    		//System.out.println ( s.id + "omg" );
	    		temp.nameOfIdent = s.Id2String( s.id ) ;
	    		temp.isDeclared = true;
	    		temp.isArray = true ;
	    		temp.numOfElements = dimenCount ; 
	    		if ( dimenLoop > 1 ) temp.dimArrayAccess = Dimen ;
	    		
	    		++varArrayCount ;
	    	}
	    	else error ( Scanner.commaToken , s.sym ) ;
	}
    	eat ( Scanner.semiToken );
    }
  }
  
  public void statSequence() throws Exception
  {
	  
	   if ( SSA_LL.size() == 0 ) // beginning of the program
	   {
			
			LinkedList<SSAAssigns> llsub = new LinkedList<SSAAssigns>(); 
			llsub.addFirst(new SSAAssigns(bbLevel) );
			if ( inFunction == false ) llsub.get(bbLevel).isRoot = true;
			
			mainInit = true;
			llsub.get(0).identArray = identArray ;
			SSA_LL.add(llsub);
			llsub.get(0).updateDoms(bbLevel-1, bbLevel, SSA_LL);
			
	  }
	  else if ( mainInit == false)
	  {
		   LinkedList<SSAAssigns> llsub = new LinkedList<SSAAssigns>(); 
			llsub.add(new SSAAssigns(bbLevel) );
			if ( inFunction == false ) llsub.get(0).isRoot = true;
			
			mainInit = true;
			llsub.get(0).identArray = identArray ;
			SSA_LL.add(llsub);
			llsub.get(0).updateDoms(bbLevel-1, bbLevel, SSA_LL);
	  }
	  
	  
  	  statement();
  	  
  	  
  	  
  	  
      while ( s.sym == Scanner.semiToken  )
      {
    		if ( s.sym == Scanner.semiToken )
    		{
    			eat ( Scanner.semiToken );
    			if ( s.sym == Scanner.endToken ) emptyStatementError() ;
    			
    			statement();
    		}
      }
  }
  	
  	
  public int statement() throws Exception
  {
  	if (s.sym == Scanner.letToken )
  	{
  		
  		boolean cond = false ;
  		int holdPos = 0 ;
  		IdentObjectComp temp2,temp3;
  		int index = 0 ;
  		Result x ,y ;
  		
  		eat ( Scanner.letToken );
  		eat ( Scanner.ident ) ;
  		temp = identArray[s.id] ;
    	temp.nameOfIdent = s.Id2String( s.id ) ;
    	
  		
  		for ( int i= 0 ; i < identArray.length; ++i )
  		{
  			temp = identArray [i];
  			if ( temp.nameOfIdent == s.Id2String ( s.id ) )
  			{
  				cond = true ;
  				holdPos = i ;
  			}
    	}
  		if ( cond )
  		{
  			temp2 = temp = identArray [holdPos] ;
  			if ( temp2.isArray)
  			{
  				
  				eat ( Scanner.openbracToken );
  				LastLetId = holdPos ;
  				int loop = 0 , loop1 = 1 , holder = 1 ;
  				
  				
				
				/*if ( (temp2.dimArrayAccess).Length > 1 )
				{
					while ( s.sym != Scanner.becomesToken )
					{
						for ( loop = (temp2.dimArrayAccess).Length -1  ; loop > loop1 ; --loop )
						{
							temp3 = temp2.dimArrayAccess ;
							holder *= temp3[loop] ;
						}
						++loop1 ;
						program[pc++] = "ldc.i4 ";
						program[pc - 1] = string.Concat ( program[pc-1], holder.ToString());
						
						exp
					}
				}*/
								
  				int loc1;
  				int loc2;
  				x = exp();
  				eat ( Scanner.closebracToken );
  				eat ( Scanner.becomesToken );			
				temp2.isDeclared = true ;
				
				y = exp();
				
				SSAAssigns temp  ;
				
				Result tempResult = new Result();
				
				LinkedList<SSAAssigns>  llsub = SSA_LL.get(bbLevel); //make multiply factor
				llsub.add(new SSAAssigns());
				temp = llsub.getLast();
				temp.operation = DLX.MUL;
				temp.pc = pc++;
				temp.x = x;
				loc1 = temp.pc;
				
				
				tempResult.kind = DLX.ConstInt;
				tempResult.value = 4;
				temp.y = tempResult;
				
				llsub.add(new SSAAssigns()); //make address pointer
				temp = llsub.getLast();
				temp.pc = pc++;
				loc2 = temp.pc;
				temp.operation = DLX.ADD;
				Result tempResultx = new Result();
				Result tempResulty = new Result();
				
				tempResultx.kind = DLX.AddrsInt;
				tempResultx.address = 0;
				temp.x = tempResultx;
				
				tempResulty.kind = DLX.AddrsInt;
				tempResulty.address = holdPos ;
				temp.y = tempResulty;
				
				llsub.add(new SSAAssigns()); //array add instruction
				temp = llsub.getLast();
				temp.pc = pc++;
				temp.operation = DLX.ADDA;
				tempResultx = new Result();
				tempResulty = new Result();
				
				tempResultx.kind = DLX.RegInt ;
				tempResultx.regno = loc1;
				temp.x = tempResultx;
				
				tempResulty.kind = DLX.RegInt;
				tempResulty.regno = loc2;
				temp.y = tempResulty;
				
				llsub.add(new SSAAssigns()); //array store
				temp = llsub.getLast();
				temp.pc = pc++;
				temp.operation = DLX.STW ;
				tempResultx = new Result();
				tempResulty = new Result();
				
				temp.x = y;
				tempResulty.kind = DLX.RegInt;
				tempResulty.regno = temp.pc - 1;
				temp.y = tempResulty;
				
				temp2.valOfIdent = y.value ;
				
  				return 0 ;
  			}
  			else
  			{
  				
  				eat ( Scanner.becomesToken );
  				LastLetId = holdPos ;
  				
  				y = exp(); //evaluate expression right of becomes
  				temp2.valOfIdent = y.value;
  				temp2.isDeclared = true ;
  				
				SSAAssigns temp  ;
				
				LinkedList<SSAAssigns>  llsub = SSA_LL.get(bbLevel);
				llsub.add(new SSAAssigns());
				temp = llsub.getLast();
				temp.operation = DLX.MOV;
				temp.r = y;  
				//temp.value = y.value;
				temp2.identIteration++;
				temp.value = temp2.identIteration;
				temp.assigned_ident = temp2.nameOfIdent + "_" + temp2.identIteration;
				
				temp.pc = pc ++;
				llsub.get(0).updateBB(temp2.nameOfIdent, holdPos, temp2.identIteration);
				

				
				if ( nestStack.size() > 0 )
				{
					if ( nestStack.peek().control == 0 )
					{
						if ( PhiHandler.handlePhiWhile(holdPos, bbLevel, SSA_LL, identArray, pc, nestStack) ) pc++;
					}
					else if ( PhiHandler.handlePhiIf(holdPos, bbLevel, SSA_LL, identArray, pc, nestStack, direction) ) pc++;
				}
  					
  				return 0 ;
  			}
  		}
  		else undeclaredIdentifierError (  s.Id2String ( s.id )) ;
  		return 0;
  	}
  	else if ( s.sym == Scanner.callToken )
	{
  			Result x ,y ;
	  		eat ( Scanner.callToken ) ;
	  		eat ( Scanner.ident ) ;
	  		
	  		if ( s.Id2String (s.id ) == "InputNum" )
	  		{
	  			if ( s.sym == Scanner.openparenToken )
	  			{
	  				eat ( Scanner.openparenToken ) ;
	  				eat ( Scanner.closeparenToken ) ;
	  			}
	  			
	  			//program [ pc++ ] = w.getProcedureCall("Read",true,0); TO RUN
	  			
	  			return (identArray[LastLetId]).valOfIdent;
	  			
	  		}
	  		else if ( s.Id2String (s.id) == "OutputNum" )
	  		{
	  			
	  			eat ( Scanner.openparenToken ) ;
	  			
	  			x = exp();
	  			
	  			LinkedList<SSAAssigns> llsub = SSA_LL.get(bbLevel);
				SSAAssigns tempSSANode = new SSAAssigns();
				tempSSANode.pc = pc++;
				tempSSANode.operation = DLX.WRD;
				tempSSANode.r = x ;
				llsub.add(tempSSANode);
	  			
	  			
	  			eat ( Scanner.closeparenToken ) ;
	  			return 0;
	  		}
	  		else if ( s.Id2String (s.id) == "OutputNewLine" )
	  		{
	  			if ( s.sym == Scanner.openparenToken )
				{
				 	eat ( Scanner.openparenToken ) ;
				  	eat ( Scanner.closeparenToken ) ;
	  			}
	  			
	  			
	  			
	  			return 0;
	  		}
	  		else
	  		{
	  			int countNumOfArgs = 0 ;
	  			
	  			String tempProcName = s.Id2String (s.id) ;
	  			
	  			eat ( Scanner.openparenToken ) ;
	  			
	  			while ( s.sym != Scanner.closeparenToken )
	  			{
	  				exp();
	  				if ( s.sym == Scanner.commaToken )
	  					eat ( Scanner.commaToken );
	  				++countNumOfArgs ;
	  			}
	  			eat (Scanner.closeparenToken ); 
	  			
	  			
	  		}
	  		
	  		return 0;
	  	
  	}
  	else if ( s.sym == Scanner.ifToken )
  	{
  		Result x , y, condRes;
  		eat ( Scanner.ifToken  );
  		Result follow = new Result();
  		follow.fixuplocation = 0;
  		
  		
  		//x = relation() ;
  		//CondNegBraFwd(x);
  		
  		condRes = relation ();
  		CondNegBraFwd(condRes);
  		
  		
  		
  		LinkedList<SSAAssigns> llsub2 = new LinkedList<SSAAssigns>(); //make else block in case we use it
  		LinkedList<SSAAssigns> ifJoinNode = new LinkedList<SSAAssigns>(); //make if join
  		ifJoinNode.addFirst(new SSAAssigns(-1));
  		ifJoinNode.get(0).isifJoinBlock = true;
  		
  		nestStack.push(new controlStackItem(1, bbLevel, -1, ifJoinNode ));
  		++bbLevel;
  		 LinkedList<SSAAssigns> llsub = new LinkedList<SSAAssigns>(); //left of control, if block
		  llsub.addFirst(new SSAAssigns(bbLevel) );
		  
		  
		  
		  
		  SSA_LL.get(bbLevel-1).get(0).left = llsub;
		  SSA_LL.get(bbLevel-1).get(0).right = null;
		  
		  llsub.get(0).parent = SSA_LL.get(bbLevel-1);
		  llsub.get(0).ifJoin = ifJoinNode;
		  
		  SSA_LL.add(llsub); //add if block
		  
		  llsub.get(0).propogateBBValues(bbLevel, bbLevel-1, SSA_LL);
		  
		  llsub.get(0).updateDoms(bbLevel-1, bbLevel, SSA_LL);
		  
		  
		direction = 'l';
		dirStack.push(direction);
  		eat( Scanner.thenToken );
  		
  		
  		statSequence ();
  		
  		dirStack.pop();
  		
  		
  		if ( s.sym == Scanner.elseToken )
  		{
  			bbLevel++;
  			direction = 'r'; //coming from the right
  			dirStack.push(direction);
  			
  			llsub2.addFirst(new SSAAssigns(bbLevel) ); //add label
  			
  			SSA_LL.get(nestStack.peek().bbParent).get(0).right = llsub2; //parent points to this
  			SSA_LL.get(nestStack.peek().bbParent).getLast().x.fixuplocation = bbLevel;
  			SSA_LL.add(llsub2); //add else block
  			llsub2.get(0).propogateBBValues(bbLevel, nestStack.peek().bbParent, SSA_LL);
  			
  			llsub.get(0).updateDoms(nestStack.peek().bbParent, bbLevel, SSA_LL);
  			
  			llsub2.get(0).ifJoin = ifJoinNode;
  			
  			
  			eat ( Scanner.elseToken ) ;
  			//UnCondBraFwd(follow);
  			//Fixup(x.fixuplocation);
  			
  			statSequence() ;
  			
  			dirStack.pop();
  		}
  		else
  		{
  			SSA_LL.get(nestStack.peek().bbParent).get(0).right = ifJoinNode;
  			SSA_LL.get(nestStack.peek().bbParent).getLast().x.fixuplocation = bbLevel+1; //where the parent of the if points to if there is no else
  		}
  		
  		
  		eat(Scanner.fiToken ) ;
  		SSA_LL.add(ifJoinNode);
  		
  		++bbLevel;
  		ifJoinNode.get(0).label = bbLevel; //add the ifJoin
  		llsub.get(0).updateDoms(nestStack.peek().bbParent, bbLevel, SSA_LL);
  		
  		//now make the following node after the if join node
  		++bbLevel;
  		LinkedList<SSAAssigns> llsub3 = new LinkedList<SSAAssigns>(); 
  		llsub3.addFirst(new SSAAssigns(bbLevel) );
  		llsub3.get(0).parent = ifJoinNode;
		ifJoinNode.get(0).follow = llsub3 ;
		
		SSA_LL.add(llsub3);
		llsub3.get(0).updateDoms(bbLevel-1, bbLevel, SSA_LL);
		
		LinkedList<SSAAssigns> priorJoinllsub = nestStack.peek().bbJoinPointer ;
		PhiHandler.updatePhis(identArray, SSA_LL, bbLevel, nestStack, ifJoinNode, bbLevel );//update phis after if and else is done, only bbvalues of level not identiteration
  		nestStack.pop();
  		
  		if ( !nestStack.isEmpty())
  		{
  			llsub3.get(0).follow = nestStack.peek().bbJoinPointer ; //the follow node after the phi in the if block now points to the join of the nested control join block
  			SSA_LL.get(nestStack.peek().bbjoinNode+1).get(0).whilejoin = null;
  			
  			if ( SSA_LL.get(nestStack.peek().bbParent+1).get(0).right != null ) // the prior control follow block now points to the right join instead of the old join
  			{
  				SSA_LL.get(nestStack.peek().bbParent+1).get(0).ifJoin = null; //if there is an else, dont follow the prior join
  				
  			}
  			else if ( SSA_LL.get(nestStack.peek().bbParent+1).get(0).ifJoin != null )//if we set this value to null, before, then we did that for a reason
  				SSA_LL.get(nestStack.peek().bbParent+1).get(0).ifJoin = priorJoinllsub; //if there is no else, point to the priorjoin
  			
  			if ( SSA_LL.get(nestStack.peek().bbParent).get(0).right != null && SSA_LL.get(nestStack.peek().bbParent).get(0).right.get(0).right != null  ) // the prior control else block now points to the right join instead of the old join
  			{
  				SSA_LL.get(nestStack.peek().bbParent).get(0).right.get(0).ifJoin = null; //if there is an else, dont follow the prior join
  				
  			}
  			else
  			{
  				//SSA_LL.get(nestStack.peek().bbParent+1).get(0).ifJoin = priorJoinllsub;
  			}
  			/*
  			if ( llsub.get(0).ifJoin != null)
  	  		{
  	  			SSAAssigns tempSSA ;
  	  			llsub.add(new SSAAssigns());
  	  			
  	  			tempSSA = llsub.getLast();
  	  			
  	  			tempSSA.operation = DLX.BSR;
  	  			
  	  			Result newResr = new Result();
  	  			
  	  			newResr.fixuplocation = ifJoinNode.get(0).label;
  	  			
  	  			tempSSA.r = newResr;
  	  		}*/
  			
  			
  			

  			pc = PhiHandler.updateNestedPhis(identArray, SSA_LL, bbLevel, nestStack, priorJoinllsub, pc, dirStack);
  		}
  		/*
  		else if ( llsub.get(0).ifJoin != null)
  		{
  			SSAAssigns tempSSA ;
  			llsub.add(new SSAAssigns());
  			
  			tempSSA = llsub.getLast();
  			
  			tempSSA.operation = DLX.BSR;
  			
  			Result newResr = new Result();
  			
  			newResr.fixuplocation = ifJoinNode.get(0).label;
  			
  			tempSSA.r = newResr;
  			
  			
  			
  		}*/
  		
  	}
  	else if ( s.sym == Scanner.whileToken )
  	{
  		int inEdgebbLevel;
  		Result x , y, condRes;
  		eat ( Scanner.whileToken );
  		
	  		bbLevel ++;
	  	
	  		LinkedList<SSAAssigns> temp = new LinkedList<SSAAssigns>(); //while outedge
	  	
	  		LinkedList<SSAAssigns> llsub = new LinkedList<SSAAssigns>(); //make join BB
			llsub.addFirst(new SSAAssigns(bbLevel) );
			
			llsub.get(0).parent = SSA_LL.get(bbLevel-1);//point up
			llsub.get(0).outedge = temp; //make outedge for while join bb
			llsub.get(0).isWhileJoinBlock = true;
			
			nestStack.push(new controlStackItem(0, bbLevel-1, bbLevel, llsub));
			
			SSA_LL.get(nestStack.peek().bbParent).get(0).follow = llsub ; //the block before the while join block is linked 
			
			SSA_LL.add(llsub); //add while bb join to master list
			llsub.get(0).propogateBBValues(bbLevel, bbLevel-1, SSA_LL); //ADDED oct 2 2014, some of the values are not corect because the ident iterations were changed before this block so it uses wrong ident iterations
			llsub.get(0).updateDoms(bbLevel-1, bbLevel, SSA_LL);
	  		
			condRes = relation ();
	  		CondNegBraFwd(condRes);
	  		
	  		bbLevel ++ ;
	  		
	  		LinkedList<SSAAssigns> llsub2 = new LinkedList<SSAAssigns>();  //make inedge for while bb
	  		llsub2.addFirst(new SSAAssigns(bbLevel) );
	  		llsub2.get(0).whilejoin = SSA_LL.get(bbLevel-1); //point back to join block for while
	  		llsub.get(0).inedge = llsub2; //join block now has a pointer to while block
	  		SSA_LL.add(llsub2); //add while block
	  		
	  		llsub2.get(0).propogateBBValues(bbLevel, bbLevel-1, SSA_LL); //ADDED oct 2 2014
	  		llsub.get(0).updateDoms(bbLevel-1, bbLevel, SSA_LL);
	  		
	  		eat ( Scanner.doToken ) ;
	  	
	  		statSequence();
	  	
	  		//Fixup(x.fixuplocation);
	  		
	  		SSAAssigns tempAssigns = new SSAAssigns (); //add instruction for branch at end of while block
	  		tempAssigns.operation = DLX.BSR ;
	  		tempAssigns.pc = pc++ ;
	  		Result tempRes = new Result();
	  		tempRes.fixuplocation = nestStack.peek().bbjoinNode;
	  		tempAssigns.r = tempRes;
	  		SSA_LL.get(bbLevel).add(tempAssigns);
	  		if ( SSA_LL.get(bbLevel).get(0).whilejoin == null)  
  			{
	  			//SSA_LL.get(bbLevel).get(0).whilejoin = nestStack.peek().bbJoinPointer;
  			}
	  		
	  		eat( Scanner.odToken );
	  		
	  		
	  		inEdgebbLevel = bbLevel; // save the bbLevel of the while block after the while join to feed into the update phi function
	  		
	  		++bbLevel;
	  		SSA_LL.add(temp); //add outedge to master list
	  		temp.addFirst(new SSAAssigns (bbLevel));
	  		llsub.get(0).updateDoms(nestStack.peek().bbjoinNode, bbLevel, SSA_LL);
	  		
	  		
	  		//SSA_LL.get(nestStack.peek().bbjoinNode).getLast().x.fixuplocation = bbLevel;
	  		
	  		LinkedList<SSAAssigns> priorJoinllsub = nestStack.peek().bbJoinPointer ;
	  		nestStack.peek().bbJoinPointer.getLast().x.fixuplocation = bbLevel;
	  		PhiHandler.updatePhis(identArray, SSA_LL, bbLevel, nestStack, llsub,inEdgebbLevel   );
	  		nestStack.pop();
	  		
	  		if ( !nestStack.isEmpty())
	  		{
	  			
	  			temp.get(0).follow = nestStack.peek().bbJoinPointer;
	  			
	  			SSA_LL.get(nestStack.peek().bbjoinNode+1).get(0).whilejoin = null;
	  			
	  			if ( SSA_LL.get(nestStack.peek().bbParent).get(0).left != null )
	  			{
	  				SSA_LL.get(nestStack.peek().bbParent).get(0).left.get(0).ifJoin = null ;
	  			}
	  			/*
	  			if ( temp.get(0).follow.get(0).isifJoinBlock == true || temp.get(0).ifJoin != null)
	  			{
	  				SSAAssigns tempSSA ;
	  				temp.add(new SSAAssigns());
	  	  			
	  	  			tempSSA = temp.getLast();
	  	  			
	  	  			tempSSA.operation = DLX.BSR;
	  	  			
	  	  			Result newResr = new Result();
	  	  			
	  	  			newResr.fixuplocation = temp.get(0).follow.get(0).label;
	  	  			
	  	  			tempSSA.r = newResr;
	  				
	  			}*/
	  			
	  			
	  			
	  			pc = PhiHandler.updateNestedPhis(identArray, SSA_LL, bbLevel, nestStack, priorJoinllsub, pc, dirStack);
	  		}
	  		
	  		if ( llsub.get(0).inedge.get(0).whilejoin == null ) //cycle through nested loop to update variables if move was made at end of the loop
	  		{
	  			PhiHandler.cycleThroughNestedWhile ( llsub );
	  		}
  		
  	}
  	else if ( s.sym == Scanner.returnToken )
  	{
  		eat ( Scanner.returnToken ) ;
  		exp();
  		
  	}
  		
  	else if ( s.sym == Scanner.endToken ){ return 0 ;}
  	
  	else error ( 0 , 0 );
  	return 0;
  }
  
  public Result exp() throws Exception
  {
	  	Result x, y;
	  	int op;
    	int val ;
    	
    	x = term () ;
    	
    	while ( s.sym == Scanner.plusToken ||  s.sym == Scanner.minusToken)
    	{
    		if ( s.sym == Scanner.plusToken )
    		{
    			lastOpUsed = Scanner.plusToken ;
    			eat ( Scanner.plusToken );
    		
    			y = term();
    			
    			
    			x = Compute( DLX.PlusInt , x, y);
  
    		}
    		else if ( s.sym == Scanner.minusToken )
    		{
    			lastOpUsed = Scanner.minusToken ;
    			eat (  Scanner.minusToken );
    			
    			y = term () ;
    			
    			x =  Compute (DLX.MinusInt, x, y  );
    		}
    		else error ( 0 , 0 ) ;
    	}
    	return x ;
  }
  
  public Result term() throws Exception
    {
	  Result x , y;
	  int op;
    	int val ;
    	x = factor () ;
    	while ( s.sym == Scanner.timesToken  || s.sym == Scanner.divToken )
    	{
    		if ( s.sym == Scanner.timesToken )
    		{	
    			lastOpUsed = Scanner.timesToken ;
    			eat (Scanner.timesToken);
    		

    			y = factor () ;
    			
    			x = Compute (DLX.TimesInt, x, y );
    		}
    		else if ( s.sym == Scanner.divToken )
    		{
    			lastOpUsed = Scanner.divToken ;
    			eat ( Scanner.divToken );
    			//s.Next ();
    			y = factor() ;
    			
    			x =  Compute (DLX.DivInt, x, y );
    		}
    		else error ( 0 , 0 ) ;
    	}
    	return x ;
    }
    
    public Result factor () throws Exception
    {
    	Result x = new Result();
    	int val = 0;
    	
    	if ( s.sym == Scanner.number )
    	{
    		val = s.val ;
    		int addToLoc = pc ;
    		
    		x.kind = DLX.ConstInt;
    		x.value = val;
    		
    		
    		eat ( Scanner.number ) ;
    		
    	}
    	else if ( s.sym == Scanner.openparenToken )
    	{
    		eat ( Scanner.openparenToken );
    		//val = exp () ;
    		x = exp();
    		
    		if ( s.sym == Scanner.closeparenToken )
    		{
    			eat ( Scanner.closeparenToken ) ;
    			
    		}
    		else error ( 0 , 0 ) ;
    		
    	}
    	else if ( s.sym == Scanner.ident )
    	{
	    		boolean cond = false ;
	  		int holdPos = 0 ;
	  		  		
	  		int i = 0 ;
	  		for ( i =0 ; i < identArray.length ; ++i )
	  		{
	  			temp = identArray [i];
	  		  	if ( temp.nameOfIdent == s.Id2String ( s.id ) )
	  		  	{
	  		  		cond = true ;
	  		  		holdPos = i ;
	  		  	}
	  		}
	  		if ( cond && ((identArray[holdPos]).isDeclared == true) )
	  		{
	  			if ( (identArray[holdPos]).isArray )
	  			{
	  				temp = identArray [holdPos] ;
	  				val = temp.valOfIdent  ;
	  				
	  				eat(Scanner.ident );
	  				eat(Scanner.openbracToken );
	  				x = exp();
	  				eat(Scanner.closebracToken);
	  				
	  				int loc1;
	  				int loc2;
	  				SSAAssigns temp  ;
	  				
	  				Result tempResult = new Result();
	  				
	  				LinkedList<SSAAssigns>  llsub = SSA_LL.get(bbLevel); //make multiply factor
					llsub.add(new SSAAssigns());
					temp = llsub.getLast();
					temp.operation = DLX.MUL;
					temp.pc = pc++;
					temp.x = x;
					loc1 = temp.pc;
						
					tempResult.kind = DLX.ConstInt;
					tempResult.value = 4;
					temp.y = tempResult;
					
					llsub.add(new SSAAssigns()); //make address framepointer
					temp = llsub.getLast();
					temp.pc = pc++;
					loc2 = temp.pc;
					temp.operation = DLX.ADD;
					Result tempResultx = new Result();
					Result tempResulty = new Result();
					
					tempResultx.kind = DLX.AddrsInt;
					tempResultx.address = 0;
					temp.x = tempResultx;
					
					tempResulty.kind = DLX.AddrsInt;
					tempResulty.address = holdPos ;
					temp.y = tempResulty;
					
					llsub.add(new SSAAssigns()); //array add instruction
					temp = llsub.getLast();
					temp.pc = pc++;
					temp.operation = DLX.ADDA;
					tempResultx = new Result();
					tempResulty = new Result();
					
					tempResultx.kind = DLX.RegInt ;
					tempResultx.regno = loc1;
					temp.x = tempResultx;
					
					tempResulty.kind = DLX.RegInt;
					tempResulty.regno = loc2;
					temp.y = tempResulty;
					
					llsub.add(new SSAAssigns()); //array load
					temp = llsub.getLast();
					temp.pc = pc++;
					temp.operation = DLX.LDW ;
					tempResultx = new Result();
					tempResulty = new Result();
					
					
					tempResulty.kind = DLX.RegInt;
					tempResulty.regno = temp.pc - 1;
					temp.r = tempResulty;
					
					tempResultx.kind = DLX.RegInt;
					tempResultx.regno = pc - 1;
					
					x = tempResultx;
					
	  			}
	  			else
	  			{
	  				temp = identArray [holdPos] ;
	  	
	  		  		val = temp.valOfIdent  ;
	  		  		x.kind = DLX.VarInt;
	  		  		x.address = holdPos;
	  		  		if ( SSA_LL.get(bbLevel).get(0).getIteration(identArray[holdPos].nameOfIdent) != 0 ) x.value = SSA_LL.get(bbLevel).get(0).getIteration(identArray[holdPos].nameOfIdent) ;
	  		  		else x.value = identArray[x.address].identIteration;
	  		  		//NEED TO FIX
	  		  		eat(Scanner.ident );
	  		  	}
	  		  	
	  		  	
	  		}
	  		else undeclaredIdentifierError ( s.Id2String ( s.id ) );
	  		
	  		
	  		return x ;
  	}
  	else if ( s.sym == Scanner.callToken )
  	{
  		eat ( Scanner.callToken);
  		
  		eat ( Scanner.ident);
  		
  		int holdPos = 0 ;
  		boolean cond = false ;
	  		
  		int i = 0 ;
  		for ( i =0 ; i < identArray.length ; ++i )
  		{
  			temp = identArray [i];
  		  	if ( temp.nameOfIdent == s.Id2String ( s.id ) )
  		  	{
  		  		cond = true ;
  		  		holdPos = i ;
  		  	}
  		}
  		
  		if ( cond == true)
  		{
  			Result tempResult = new Result();	
  		}
  		else error ( 0, 0);
  		
  	}
  	else emptyFactorError() ;
    		
    	//return val ;
    	return x;
  }
  
  public Result relation () throws Exception
  {
	  Result x, y, condRes;
	  condRes = new Result();
	  int op;
  	  //exp();
	  x = exp();
  	
  	int relOp = s.sym ;
  	
  	if ( !isRelop ( relOp ))
  	{ 
  		nonRelOpError (relOp) ;
  	}
  	
  	if ( relOp == Scanner.lssToken )
  	{
  		op = relOp;
  		
  		eat ( Scanner.lssToken );
  		
  		y = exp();
  		Compute  (DLX.CMP, x, y);
  		//.kind = DLX.CondInt;
  		//x.cond = op;
  		//x.fixuplocation = 0;
  		
  		condRes.kind = DLX.CondInt;
  		condRes.cond = op;
  		condRes.fixuplocation = 0;
  		
  		
  		
  	}
  	else if ( relOp == Scanner.gtrToken )
	{
  		op = relOp;
  		
		eat ( Scanner.gtrToken );
		
		y = exp();
  		Compute  (DLX.CMP, x, y);
  		//x.kind = DLX.CondInt;
  		//x.cond = op;
  		//x.fixuplocation = 0;
  		condRes.kind = DLX.CondInt;
  		condRes.cond = op;
  		condRes.fixuplocation = 0;
  	}
  	else if ( relOp == Scanner.leqToken )
	{
  		op = relOp;
  		
	  	eat ( Scanner.leqToken );
	  	
	  	y = exp();
  		Compute  (DLX.CMP, x, y);
  		//x.kind = DLX.CondInt;
  		//x.cond = op;
  		//x.fixuplocation = 0;
  		condRes.kind = DLX.CondInt;
  		condRes.cond = op;
  		condRes.fixuplocation = 0;
  	}
  	else if ( relOp == Scanner.geqToken )
	{
  		op = relOp;
  		
	  	eat ( Scanner.geqToken );
	  	
	  	y = exp();
  		Compute  (DLX.CMP, x, y);
  		//x.kind = DLX.CondInt;
  		//x.cond = op;
  		//x.fixuplocation = 0;
  		condRes.kind = DLX.CondInt;
  		condRes.cond = op;
  		condRes.fixuplocation = 0;
  	}
  	else if ( relOp == Scanner.eqlToken )
	{
  		op = relOp ;
  		
	  	eat ( Scanner.eqlToken );
	  	
	  	y = exp();
  		Compute  (DLX.CMP, x, y);
  		//x.kind = DLX.CondInt;
  		//x.cond = op;
  		//x.fixuplocation = 0;
  		condRes.kind = DLX.CondInt;
  		condRes.cond = op;
  		condRes.fixuplocation = 0;
  	}
  	else if ( relOp == Scanner.neqToken )
  	{
  		op = relOp;
  		
  		eat ( Scanner.neqToken ) ;
  		
  		y = exp();
  		Compute  (DLX.CMP, x, y);
  		//x.kind = DLX.CondInt;
  		//x.cond = op;
  		//x.fixuplocation = 0;
  		condRes.kind = DLX.CondInt;
  		condRes.cond = op;
  		condRes.fixuplocation = 0;
  	}
  	
  	//return x;
  	return condRes;
  }
  
  
  
  public Result Compute(int op, Result x, Result y)
  {
	  	Result returnResult = new Result();
		if ( (DLX.kindValues[x.kind] == "const") && (DLX.kindValues[y.kind] == "const"))
		{
			if ( DLX.opValues[op] == "plus")
			{
				x.value += y.value;
			}
			else if (DLX.opValues[op] == "minus")
			{
				x.value -= y.value;
			}
			else if (DLX.opValues[op] == "times")
			{
				x.value *= y.value;
			}
			else if (DLX.opValues[op] == "div")
			{
				x.value /= y.value ;
			}
			returnResult = x;
		}
		else if ( op == DLX.CMP )
		{
			load(x);
			if (x.regno == 0)
			{
				x.regno = AllocateReg();
				program[pc++] = DLX.assemble(DLX.ADD, x.regno, 0 , 0);
				
			}
			if ( DLX.kindValues[y.kind] == "const")
			{
				
				
				SSAAssigns temp  ;
				LinkedList<SSAAssigns> llsub ;
				llsub = SSA_LL.get(bbLevel);
				llsub.add(new SSAAssigns());
				temp = llsub.getLast();
				temp.operation =  DLX.CMP; 
				temp.x = x;
				temp.y = y;
				//temp.value = y.value;
				//temp.assigned_ident = temp2.nameOfIdent + "_" + temp2.identIteration;
				temp.pc = pc++;
				
				returnResult = new Result();
				returnResult.regno = temp.pc;
				returnResult.kind = DLX.RegInt;

				return returnResult;
			}
			else
			{
				SSAAssigns temp  ;
				LinkedList<SSAAssigns> llsub ;
				llsub = SSA_LL.get(bbLevel);
				llsub.add(new SSAAssigns());
				temp = llsub.getLast();
				temp.operation =  DLX.CMP;
				//temp.r = y; 
				temp.x = x;
				temp.y = y;
				//temp.value = y.value;
				//temp.assigned_ident = temp2.nameOfIdent + "_" + temp2.identIteration;
				temp.pc = pc++;
				returnResult = new Result();
				returnResult.regno = temp.pc;
				returnResult.kind = DLX.RegInt;

				return returnResult;
			} 
		}
		else
		{
			load(x);
			if (x.regno == 0)
			{
				x.regno = AllocateReg();
				program[pc++] = DLX.assemble(DLX.ADD, x.regno, 0 , 0);
			}
			if ( DLX.kindValues[y.kind] == "const")
			{
				//DLX.assemble(op)
				
				
				SSAAssigns temp  ;
				LinkedList<SSAAssigns> llsub ;
				llsub = SSA_LL.get(bbLevel);
				llsub.add(new SSAAssigns());
				temp = llsub.getLast();
				temp.operation =  DLX.opCodeImm[op + 4 ];
				//temp.r = y; 
				temp.x = x;
				temp.y = y;
				//temp.value = y.value;
				//temp.assigned_ident = temp2.nameOfIdent + "_" + temp2.identIteration;
				temp.pc = pc++;
				
				returnResult = new Result();
				returnResult.regno = temp.pc;
				returnResult.kind = DLX.RegInt;

				return returnResult;
			}
			else
			{
				//load(y);
				//OFFSET 4 for ADD, SUB, MUL, DIV
				
				//deAllocateReg(y);
				SSAAssigns temp  ;
					LinkedList<SSAAssigns> llsub ;
					llsub = SSA_LL.get(bbLevel);
					llsub.add(new SSAAssigns());
					temp = llsub.getLast();
					temp.operation =  DLX.opCodeImm[op + 4 ];
					//temp.r = y; 
					temp.x = x;
					temp.y = y;
					//temp.value = y.value;
					//temp.assigned_ident = temp2.nameOfIdent + "_" + temp2.identIteration;
					temp.pc = pc++;
					returnResult = new Result();
					returnResult.regno = temp.pc;
					returnResult.kind = DLX.RegInt;
	
					return returnResult;
				
			}
		}
		return returnResult;
	}
	
	public void load (Result x) //does nothing
	{
		if ( DLX.kindValues[x.kind] == "var")
		{
			//x.regno = AllocateReg();
			//ll.add(e)
			
			//x.kind = DLX.RegInt;
		}
		else if ( DLX.kindValues[x.kind] == "const")
		{/*
			if ( x.value == 0 ) x.regno = 0;
			else
			{
				x.regno = AllocateReg();
				program[pc++] = DLX.assemble(DLX.ADDI, x.regno, 0 , x.value);
				
			}
			x.kind = DLX.RegInt;
			*/
		}
	}
	
	private int AllocateReg()
	{
		for ( int i = 1 ; i < 27 ; ++i )
		{
			if ( Registers[i] == false )
			{
				Registers[i] = true;
				return i;
			}
		}
		return 0;
	}
	
	private void deAllocateReg( Result i)
	{
		Registers[i.regno] = false;
	}
  
  void CondNegBraFwd(Result x)
  {
	  
	  x.regno = pc-1; //point to instruction before value of condition
	  
	  SSAAssigns temp  ;
		LinkedList<SSAAssigns> llsub ;
		llsub = SSA_LL.get(bbLevel);
		llsub.add(new SSAAssigns());
		temp = llsub.getLast();
		temp.operation =  DLX.negatedBranchOp[x.cond]; 
		temp.x = x;
		temp.r = x;
		
		temp.pc = pc++;
		
  }
  
  void UnCondBraFwd( Result x) 
  {
	  program[pc++] = DLX.assemble( DLX.BEQ, 0, x.fixuplocation);
	  x.fixuplocation = pc - 1;
  }
  
  void Fixup ( int loc)
  {
	  //program[loc] = program[loc] & 0xffff0000 + (pc - loc);
	  //buf[loc] = buf[loc] & 0xffff0000 + (pc - loc);
  }
  
  void FixAll ( int loc)
  {
	  int next;
	  while ( loc != 0)
	  {
		  next = program[loc] & 0x0000ffff;
		  Fixup(loc);
		  loc = next;
	  }
  }
  
  
  private boolean isRelop ( int op )
  {
  	if (( op <= 25 ) && ( op >= 20 )) return true ;
  	else return false ;
  }
  
  // Checks to see if the current token is the same as "expected".
  // If so, advance to the next token.  Else, throw an exception.
  private void eat(int expected) throws Exception
  {
		    System.out.println ( s.sym + "  " + s.symbols[s.sym]);
		    //Console.WriteLine ( "\n");
		    if (s.sym != expected) {
		      error(expected, s.sym);
		      //throw new ParserExceptionCompile(expected, s.sym);
		    }
		    s.Next();
  }

  // Returns true if the current token is the same as "expected".
    private boolean currentIs (int expected) {
      return s.sym == expected;
    }
  
    // ***************** Error Handling Functions *******************
    // DON'T add new Error methods.
    // Used to signal an error while parsing
    private void error (int expected, int got) throws Exception   {
      throw new ParserExceptionCompile(expected, got);
     
    }	
  
    // Used to signal that "ident" is an undeclared identifier
    private void undeclaredIdentifierError (String ident) throws Exception {
      throw new ParserExceptionCompile("Undeclared Identifier: " + ident);
    }
    
    // Used in statement, when none of the three possibilities are present
    // (i.e. assignment, funcCall, ifStatement)
    private void emptyStatementError () throws Exception {
      throw new ParserExceptionCompile("Empty Statment");
    }
  
    // Used in factor, when none of the four possibilities are present
    // (i.e. ident, number, "(" expression ")", funcCall
    private void emptyFactorError () throws Exception {
      throw new ParserExceptionCompile("Empty Factor");
    }
    
    // Used in factor
    private void factorError (String message) throws Exception {
      throw new ParserExceptionCompile("Factor parsing error: " + message);
    }
  
    // Used when program expects a relationship operator (e.g. <, ==, >=),
    // but got a different token
    private void nonRelOpError (int sym) throws Exception {
      throw new ParserExceptionCompile("Expected a relationship operator, got: " 
                                + Scanner.symbols[sym]);
    }
  
    // Used to signal that "ident" is an undeclared identifier
    private void funcCallError (String name) throws Exception {
      throw new ParserExceptionCompile("Function call error: " + name);
    }
    
    //Used in varDecl
    private void varDeclError(String name) throws Exception {
      throw new ParserExceptionCompile("Variable declaration error: " + name);
    }
  
    //Used in assignment
    private void assignmentError(String name) throws Exception {
      throw new ParserExceptionCompile("Assignment error: " + name);
    }
    
    //Used in code generation
    private void CGError(String message) throws Exception {
      throw new ParserExceptionCompile("Code Generation Error: " + message);
    }
    
    void outGraph (  )
    {
    	  try
    	  {
	    	  // Create file 
	    	  FileWriter fstream = new FileWriter("out.dot");
	    	  BufferedWriter out = new BufferedWriter(fstream);
	    	  String variable;
	    	  String variable2;
	    	  String outString = "digraph G { \n";
	    	  outString += "node [shape = record];\n";
	    	  int llCount = SSA_LL.size();
	    	  int llCountSub;
	    	  for ( int i = 0 ; i < llCount ; ++i)
	    	  {
	    		  
	    		  llCountSub = SSA_LL.get(i).size();
	    		  
	    		  if ( SSA_LL.get(i).get(0).BBValues.size() > 0) //print bbvalues of a node if there are instructions in that node or not
    			  {
	    			  outString += "node" + i + " [label=\"";
    				  outString += "{";
    				  
    				  for ( int bbvalueIter = 0; bbvalueIter < SSA_LL.get(i).get(0).BBValues.size() ; ++ bbvalueIter)
					  {
						  outString += "bbval : " + SSA_LL.get(i).get(0).BBValues.get(bbvalueIter).identNameinBB + " " + 
								  						SSA_LL.get(i).get(0).BBValues.get(bbvalueIter).identIterinBB + "|";
						  
					  }
    				  
    			  }
	    		  
	    		  if (llCountSub > 1)
	    		  {
	    			  if ( SSA_LL.get(i).get(0).BBValues.size() == 0 ) outString += "node" + i + " [label=\"";
	    			  
	    			  
	    			  
	    			  for ( int j = 1; j < llCountSub ; ++ j ) // for all instructions in a block	
	    			  {
	    				  System.out.println ( i + " : " + j);
	    				  
	    				  
	    				  
	    				  if ( j == 1 &&  SSA_LL.get(i).get(0).BBValues.size() == 0)
	    				  {
	    					  outString += "{";
	    					  
	    				  }
	    				  
	    				  if (SSA_LL.get(i).get(j).operation == DLX.MOV )
	    				  {
	    					  switch ( SSA_LL.get(i).get(j).r.kind )
	    					  {
	    					  	case DLX.ConstInt :
	    					  		outString += SSA_LL.get(i).get(j).pc + " : " + DLX.mnemo[SSA_LL.get(i).get(j).operation] + " " + SSA_LL.get(i).get(j).r.value + " " + SSA_LL.get(i).get(j).assigned_ident ;
	    					  		break;
	    					  	case DLX.VarInt :
	    					  		//variable = getDirectingIdent ( i , SSA_LL.get(i).get(j).r.address);
	    					  		variable = s.Id2String(SSA_LL.get(i).get(j).r.address) + "_" + SSA_LL.get(i).get(j).r.value;
	    					  		outString += SSA_LL.get(i).get(j).pc + " : " + DLX.mnemo[SSA_LL.get(i).get(j).operation] + " " + variable + " " + SSA_LL.get(i).get(j).assigned_ident ;
	    					  		break;
	    					  	case DLX.RegInt :
	    					  		outString += SSA_LL.get(i).get(j).pc + " : " + DLX.mnemo[SSA_LL.get(i).get(j).operation] + " " + "("+ SSA_LL.get(i).get(j).r.regno+")" + " " + SSA_LL.get(i).get(j).assigned_ident ;
	    					  		break;
	    					  	default:
	    					  		break;
	    					  }	   
	    				  }
	    				  if (SSA_LL.get(i).get(j).operation == DLX.MOVEREG )
	    				  {
	    					  switch ( SSA_LL.get(i).get(j).x.kind )
	    					  {
	    					  	
	    					  	case DLX.RegInt :
	    					  		switch (SSA_LL.get(i).get(j).y.kind )
	    					  		{
	    					  		  case DLX.ConstInt :
		    							  outString += SSA_LL.get(i).get(j).pc + " : " + DLX.mnemo[SSA_LL.get(i).get(j).operation] + " " + "("+ SSA_LL.get(i).get(j).x.regno + ")" + " " +  SSA_LL.get(i).get(j).y.value;
			    						  break;
		    						  case DLX.VarInt :
		    							  variable = s.Id2String(SSA_LL.get(i).get(j).y.address) + "_" + SSA_LL.get(i).get(j).y.value;
		    							  outString += SSA_LL.get(i).get(j).pc + " : " + DLX.mnemo[SSA_LL.get(i).get(j).operation] + " " + SSA_LL.get(i).get(j).x.value + " " + variable;
		    							  break;
		    						  case DLX.RegInt :
		    							  outString += SSA_LL.get(i).get(j).pc + " : " + DLX.mnemo[SSA_LL.get(i).get(j).operation] + " " + "("+ SSA_LL.get(i).get(j).x.regno + ")" + " " + "(" +  SSA_LL.get(i).get(j).y.regno + ")" ;
			    						  break;
			    						  
			    					  default:
			    						  break;
	    					  		
	    					  		}
	    					  		
	    					  		break;
	    					  	default:
	    					  		break;
	    					  }	   
	    				  }
	    				  if (SSA_LL.get(i).get(j).operation >=  DLX.ADD && SSA_LL.get(i).get(j).operation <=  DLX.DIV  )
	    				  {
	    					  switch ( SSA_LL.get(i).get(j).x.kind )
	    					  {
		    					  case DLX.ConstInt :
		    						  switch (  SSA_LL.get(i).get(j).y.kind )
		    						  {
			    						  case DLX.ConstInt :
			    							  outString += SSA_LL.get(i).get(j).pc + " : " + DLX.mnemo[SSA_LL.get(i).get(j).operation] + " " + SSA_LL.get(i).get(j).x.value + " " +  SSA_LL.get(i).get(j).y.value;
				    						  break;
			    						  case DLX.VarInt :
			    							  variable = s.Id2String(SSA_LL.get(i).get(j).y.address) + "_" + SSA_LL.get(i).get(j).y.value;
			    							  outString += SSA_LL.get(i).get(j).pc + " : " + DLX.mnemo[SSA_LL.get(i).get(j).operation] + " " + SSA_LL.get(i).get(j).x.value + " " + variable;
			    							  break;
			    						  case DLX.RegInt :
			    							  outString += SSA_LL.get(i).get(j).pc + " : " + DLX.mnemo[SSA_LL.get(i).get(j).operation] + " " + SSA_LL.get(i).get(j).x.value + " " + "(" +  SSA_LL.get(i).get(j).y.regno + ")" ;
				    						  break;
				    						  
				    					  default:
				    						  break;
		    						  }
		    						  break;
		    						  
		    					  case DLX.VarInt :
		    						  switch (  SSA_LL.get(i).get(j).y.kind )
		    						  {
			    						  case DLX.ConstInt :
			    							  
				    						  variable = s.Id2String(SSA_LL.get(i).get(j).x.address) + "_" + SSA_LL.get(i).get(j).x.value;
				    						  outString += SSA_LL.get(i).get(j).pc + " : " + DLX.mnemo[SSA_LL.get(i).get(j).operation] + " " +  variable + " " + SSA_LL.get(i).get(j).y.value;
				    						  break;
			    						  case DLX.VarInt :
			    							  
				    						  variable = s.Id2String(SSA_LL.get(i).get(j).x.address) + "_" + SSA_LL.get(i).get(j).x.value;
				    						  variable2 = s.Id2String(SSA_LL.get(i).get(j).y.address) + "_" + SSA_LL.get(i).get(j).y.value;
				    						  outString += SSA_LL.get(i).get(j).pc + " : " + DLX.mnemo[SSA_LL.get(i).get(j).operation] + " " +  variable + " " + variable2;
			    							  break;
			    						  case DLX.RegInt :
			    							  
				    						  variable = s.Id2String(SSA_LL.get(i).get(j).x.address) + "_" + SSA_LL.get(i).get(j).x.value;
				    						  outString += SSA_LL.get(i).get(j).pc + " : " + DLX.mnemo[SSA_LL.get(i).get(j).operation] + " " +  variable + " (" + SSA_LL.get(i).get(j).y.regno + ")";
				    						  break;
				    					  default:
				    						  break;
		    						  }
		    						  break;
		    						  
		    					  case DLX.RegInt :
		    						  switch (  SSA_LL.get(i).get(j).y.kind )
		    						  {
			    						  case DLX.ConstInt :
			    							  outString += SSA_LL.get(i).get(j).pc + " : " + DLX.mnemo[SSA_LL.get(i).get(j).operation] + " " + "("+  SSA_LL.get(i).get(j).x.regno + ")" + " " + SSA_LL.get(i).get(j).y.value;
				    						  break;
			    						  case DLX.VarInt :
			    							  variable2 = s.Id2String(SSA_LL.get(i).get(j).y.address) + "_" + SSA_LL.get(i).get(j).y.value;
			    							  outString += SSA_LL.get(i).get(j).pc + " : " + DLX.mnemo[SSA_LL.get(i).get(j).operation] + " " + "("+  SSA_LL.get(i).get(j).x.regno + ")" + " " + variable2;
			    							  break;
			    						  case DLX.RegInt :
			    							  outString += SSA_LL.get(i).get(j).pc + " : " + DLX.mnemo[SSA_LL.get(i).get(j).operation] + " (" + SSA_LL.get(i).get(j).x.regno + ") " + "(" +  SSA_LL.get(i).get(j).y.regno + ")" ;
				    						  break;
				    					  default:
				    						  break;
		    						  }
		    						  break;
		    					  case DLX.AddrsInt :
		    						  switch (  SSA_LL.get(i).get(j).y.kind )
		    						  {
			    						  case DLX.ConstInt :
			    							  outString += SSA_LL.get(i).get(j).pc + " : " + DLX.mnemo[SSA_LL.get(i).get(j).operation] + " " + "("+  SSA_LL.get(i).get(j).x.regno + ")" + " " + SSA_LL.get(i).get(j).y.value;
				    						  break;
			    						  case DLX.VarInt :
			    							  variable2 = s.Id2String(SSA_LL.get(i).get(j).y.address) + "_" + SSA_LL.get(i).get(j).y.value;
			    							  outString += SSA_LL.get(i).get(j).pc + " : " + DLX.mnemo[SSA_LL.get(i).get(j).operation] + " " + "("+  SSA_LL.get(i).get(j).x.regno + ")" + " " + variable2;
			    							  break;
			    						  case DLX.RegInt :
			    							  outString += SSA_LL.get(i).get(j).pc + " : " + DLX.mnemo[SSA_LL.get(i).get(j).operation] + " (" + SSA_LL.get(i).get(j).x.regno + ") " + "(" +  SSA_LL.get(i).get(j).y.regno + ")" ;
				    						  break;
			    						  case DLX.AddrsInt :
			    							  outString += SSA_LL.get(i).get(j).pc + " : " + DLX.mnemo[SSA_LL.get(i).get(j).operation] + " FP" + " " + identArray[SSA_LL.get(i).get(j).y.address].nameOfIdent + "baseaddress";
				    					  default:
				    						  break;
		    						  }
		    						  break;
		    						  
		    						  
		    					  default :
		    						  break;
	    					  }
	    				  }
	    				  if ( SSA_LL.get(i).get(j).operation == DLX.ADDA)
	    				  {
	    					  outString += SSA_LL.get(i).get(j).pc + " : " + DLX.mnemo[SSA_LL.get(i).get(j).operation] + " (" + SSA_LL.get(i).get(j).x.regno + ")" + " (" + SSA_LL.get(i).get(j).y.regno + ")";
	    					  
	    				  }
	    				  if (SSA_LL.get(i).get(j).operation == DLX.PHI  )
	    				  {
	    					  //String variable_assign = s.Id2String(SSA_LL.get(i).get(j).phi_assign.address) + "_" + SSA_LL.get(i).get(j).phi_assign.value ;
	    					  //String variable_parent = s.Id2String(SSA_LL.get(i).get(j).x.address) + "_" + SSA_LL.get(i).get(j).x.value  ;
	    					  //String variable_right = s.Id2String(SSA_LL.get(i).get(j).y.address) + "_" + SSA_LL.get(i).get(j).y.value  ;
	    					  String variable_assign = identArray[SSA_LL.get(i).get(j).phi_assign.address].nameOfIdent + "_" + SSA_LL.get(i).get(j).phi_assign.value ;
	    					  String variable_parent = "";
	    					  String variable_right = "";
	    					  switch ( SSA_LL.get(i).get(j).x.kind)
	    					  {
	    					  case DLX.ConstInt :
	    						  variable_parent = Integer.toString(SSA_LL.get(i).get(j).x.value)  ;
	    						  break;
	    					  case DLX.VarInt :
	    						  variable_parent = identArray[SSA_LL.get(i).get(j).x.address].nameOfIdent + "_" + SSA_LL.get(i).get(j).x.value  ;
	    						  break;
	    					  case DLX.RegInt :
	    						  variable_parent = "(" + SSA_LL.get(i).get(j).x.regno + ")"  ;
	    						  break;
	    						  default: break;
	    					  
	    					  }
	    					  
	    					  switch ( SSA_LL.get(i).get(j).y.kind)
	    					  {
	    					  case DLX.ConstInt :
	    						  variable_right = Integer.toString(SSA_LL.get(i).get(j).y.value)  ;
	    						  break;
	    					  case DLX.VarInt :
	    						  variable_right =  identArray[SSA_LL.get(i).get(j).y.address].nameOfIdent + "_" + SSA_LL.get(i).get(j).y.value  ;
	    						  break;
	    					  case DLX.RegInt :
	    						  variable_right = "(" + SSA_LL.get(i).get(j).y.regno + ")"  ;
	    						  break;
	    						  default: break;
	    					  
	    					  }
	    					  //variable_parent = identArray[SSA_LL.get(i).get(j).x.address].nameOfIdent + "_" + SSA_LL.get(i).get(j).x.value  ;
	    					  //variable_right =  identArray[SSA_LL.get(i).get(j).y.address].nameOfIdent + "_" + SSA_LL.get(i).get(j).y.value  ;
	    					  outString += SSA_LL.get(i).get(j).pc + " : " + DLX.mnemo[SSA_LL.get(i).get(j).operation] + " "+ variable_assign + " " + variable_parent + " " + variable_right;
	    				  }
	    				  
	    				  if (SSA_LL.get(i).get(j).operation == DLX.CMP  )
	    				  {
	    					  //String variable_assign = s.Id2String(SSA_LL.get(i).get(j).x.address) + "_" + SSA_LL.get(i).get(j).x.value ;
	    					  
	    					  //outString += SSA_LL.get(i).get(j).pc + " : " + DLX.mnemo[SSA_LL.get(i).get(j).operation] + " "+ variable_assign + " " + SSA_LL.get(i).get(j).y.value ;
	    					  
	    					  switch ( SSA_LL.get(i).get(j).x.kind )
	    					  {
		    					  case DLX.ConstInt :
		    						  switch (  SSA_LL.get(i).get(j).y.kind )
		    						  {
			    						  case DLX.ConstInt :
			    							  outString += SSA_LL.get(i).get(j).pc + " : " + DLX.mnemo[SSA_LL.get(i).get(j).operation] + " " + SSA_LL.get(i).get(j).x.value + " " + SSA_LL.get(i).get(j).y.value ;
				    						  break;
			    						  case DLX.VarInt :
			    							  variable = s.Id2String(SSA_LL.get(i).get(j).y.address) + "_" + SSA_LL.get(i).get(j).y.value;
			    							  outString += SSA_LL.get(i).get(j).pc + " : " + DLX.mnemo[SSA_LL.get(i).get(j).operation] + " " + SSA_LL.get(i).get(j).x.value + " " + variable;
			    							  break;
			    						  case DLX.RegInt :
			    							  outString += SSA_LL.get(i).get(j).pc + " : " + DLX.mnemo[SSA_LL.get(i).get(j).operation] + " " + SSA_LL.get(i).get(j).x.value + " " + "(" +  SSA_LL.get(i).get(j).y.regno + ")" ;
				    						  break;
				    						  
				    					  default:
				    						  break;
		    						  }
		    						  break;
		    						  
		    					  case DLX.VarInt :
		    						  switch (  SSA_LL.get(i).get(j).y.kind )
		    						  {
			    						  case DLX.ConstInt :
			    							  
				    						  variable = s.Id2String(SSA_LL.get(i).get(j).x.address) + "_" + SSA_LL.get(i).get(j).x.value;
				    						  outString += SSA_LL.get(i).get(j).pc + " : " + DLX.mnemo[SSA_LL.get(i).get(j).operation] + " " +  variable + " " + SSA_LL.get(i).get(j).y.value;
				    						  break;
			    						  case DLX.VarInt :
			    							  
				    						  variable = s.Id2String(SSA_LL.get(i).get(j).x.address) + "_" + SSA_LL.get(i).get(j).x.value;
				    						  variable2 = s.Id2String(SSA_LL.get(i).get(j).y.address) + "_" + SSA_LL.get(i).get(j).y.value;
				    						  outString += SSA_LL.get(i).get(j).pc + " : " + DLX.mnemo[SSA_LL.get(i).get(j).operation] + " " +  variable + " " + variable2;
			    							  break;
			    						  case DLX.RegInt :
			    							  
				    						  variable = s.Id2String(SSA_LL.get(i).get(j).x.address) + "_" + SSA_LL.get(i).get(j).x.value;
				    						  outString += SSA_LL.get(i).get(j).pc + " : " + DLX.mnemo[SSA_LL.get(i).get(j).operation] + " " +  variable + " " + SSA_LL.get(i).get(j).y.value;
				    						  break;
				    					  default:
				    						  break;
		    						  }
		    						  break;
		    						  
		    					  case DLX.RegInt :
		    						  switch (  SSA_LL.get(i).get(j).y.kind )
		    						  {
			    						  case DLX.ConstInt :
			    							  outString += SSA_LL.get(i).get(j).pc + " : " + DLX.mnemo[SSA_LL.get(i).get(j).operation] + " " + "("+  SSA_LL.get(i).get(j).x.regno + ")" + " " + SSA_LL.get(i).get(j).y.value;
				    						  break;
			    						  case DLX.VarInt :
			    							  variable2 = s.Id2String(SSA_LL.get(i).get(j).y.address) + "_" + SSA_LL.get(i).get(j).y.value;
			    							  outString += SSA_LL.get(i).get(j).pc + " : " + DLX.mnemo[SSA_LL.get(i).get(j).operation] + " " + "("+  SSA_LL.get(i).get(j).x.regno + ")" + " " + variable2;
			    							  break;
			    						  case DLX.RegInt :
			    							  outString += SSA_LL.get(i).get(j).pc + " : " + DLX.mnemo[SSA_LL.get(i).get(j).operation] + " " + "("+  SSA_LL.get(i).get(j).x.regno + ")" + " " + "("+  SSA_LL.get(i).get(j).y.regno + ")";
				    						  break;
				    					  default:
				    						  break;
		    						  }
		    						  break;
		    						  
		    					  default :
		    						  break;
	    					  }
	    				  }
	    				  if (SSA_LL.get(i).get(j).operation >= DLX.BEQ && SSA_LL.get(i).get(j).operation <= DLX.BGT  )
	    				  {
	    					  //outString += SSA_LL.get(i).get(j).pc + " : " + DLX.mnemo[SSA_LL.get(i).get(j).operation] + " ("+ SSA_LL.get(i).get(j).x.regno + ") " + " [" + SSA_LL.get(i).get(j).x.fixuplocation + "]" ;
	    					  outString += SSA_LL.get(i).get(j).pc + " : " + DLX.mnemo[SSA_LL.get(i).get(j).operation] + " ("+ SSA_LL.get(i).get(j).r.regno + ") " + " [" + SSA_LL.get(i).get(j).r.fixuplocation + "]" ;
	    				  }
	    				  if ( SSA_LL.get(i).get(j).operation == DLX.BSR )
	    				  {
	    					  outString += SSA_LL.get(i).get(j).pc + " : " + DLX.mnemo[SSA_LL.get(i).get(j).operation] + " ["+ SSA_LL.get(i).get(j).r.fixuplocation + "]" ;
	    				  }
	    				  if ( SSA_LL.get(i).get(j).operation == DLX.WRD )
	    				  {
	    					  //outString += SSA_LL.get(i).get(j).pc + " : " + DLX.mnemo[SSA_LL.get(i).get(j).operation] + " ["+ SSA_LL.get(i).get(j).reg + "]" ;
	    			
	    					  switch ( SSA_LL.get(i).get(j).r.kind )
	    					  {
	    					  	case DLX.ConstInt :
	    					  		outString += SSA_LL.get(i).get(j).pc + " : " + DLX.mnemo[SSA_LL.get(i).get(j).operation] + " " + SSA_LL.get(i).get(j).r.value ;
	    					  		break;
	    					  	case DLX.VarInt :
	    					  		variable = s.Id2String(SSA_LL.get(i).get(j).r.address) + "_" + SSA_LL.get(i).get(j).r.value;
	    					  		outString += SSA_LL.get(i).get(j).pc + " : " + DLX.mnemo[SSA_LL.get(i).get(j).operation] + " " + variable ;
	    					  		break;
	    					  	case DLX.RegInt :
	    					  		outString += SSA_LL.get(i).get(j).pc + " : " + DLX.mnemo[SSA_LL.get(i).get(j).operation] + " (" + SSA_LL.get(i).get(j).r.regno + ")" ;
	    					  	default:
	    					  		break;
	    					  }	
	    				  }
	    				  if ( SSA_LL.get(i).get(j).operation == DLX.STW )
	    				  {
	    					  //outString += SSA_LL.get(i).get(j).pc + " : " + DLX.mnemo[SSA_LL.get(i).get(j).operation] + " ["+ SSA_LL.get(i).get(j).reg + "]" ;
	    			
	    					  switch ( SSA_LL.get(i).get(j).x.kind )
	    					  {
	    					  	case DLX.ConstInt :
	    					  		outString += SSA_LL.get(i).get(j).pc + " : " + DLX.mnemo[SSA_LL.get(i).get(j).operation] + " " + SSA_LL.get(i).get(j).x.value + " (" + SSA_LL.get(i).get(j).y.regno + ")" ;
	    					  		break;
	    					  	case DLX.VarInt :
	    					  		variable = s.Id2String(SSA_LL.get(i).get(j).x.address) + "_" + SSA_LL.get(i).get(j).x.value;
	    					  		outString += SSA_LL.get(i).get(j).pc + " : " + DLX.mnemo[SSA_LL.get(i).get(j).operation] + " " + variable + " (" + SSA_LL.get(i).get(j).y.regno + ")" ;
	    					  		break;
	    					  	case DLX.RegInt :
	    					  		outString += SSA_LL.get(i).get(j).pc + " : " + DLX.mnemo[SSA_LL.get(i).get(j).operation] + " (" + SSA_LL.get(i).get(j).y.regno + ")" + " ( " + SSA_LL.get(i).get(j).y.regno + ")"  ;
	    					  	default:
	    					  		break;
	    					  }	
	    				  }
	    				  if ( SSA_LL.get(i).get(j).operation == DLX.LDW)
	    				  {
	    					  outString += SSA_LL.get(i).get(j).pc + " : " + DLX.mnemo[SSA_LL.get(i).get(j).operation] + " (" + SSA_LL.get(i).get(j).r.regno + ")"  ;
	    					  
	    					  
	    					  
	    				  }
	    				  
	    				  if ( SSA_LL.get(i).get(j).kill == true) outString += " KILLED" + " " + SSA_LL.get(i).get(j).substituteKill ;
	    				  if ( j + 1 < llCountSub ) outString += "|";
	    				  else outString += "}";
	    			  }
	    			  //System.out.println(outString);
	    			  outString += "\"];\n";
	    		  }
	    		 
	    		  if ( llCountSub <= 1 && SSA_LL.get(i).get(0).BBValues.size() > 0)  outString += "}\"];\n";
	    	  }
	    	  outString += "\n";
	    	  
	    	  
	    	  
	    	  for ( int i = 0 ; i < bbLevel ; ++i)
	    	  {
	    		  if ( SSA_LL.get(i).get(0).isRoot )
	    		  {
	    			  //outString += "node" + i + " -> node" + (i + 1) + ";\n" ;
	    			  
	    		  }
	    		  if (  SSA_LL.get(i).get(0).outedge != null)
	    		  {
	    			  
	    			  outString += "node" + i + " -> node" + SSA_LL.get(i).get(0).outedge.get(0).label + ";\n" ;
	    			  
	    		  }
	    		  if ( SSA_LL.get(i).get(0).inedge != null )
	    		  {
	    			  outString += "node" + i + " -> node" + SSA_LL.get(i).get(0).inedge.get(0).label + ";\n" ;
	    		  }
	    		  if (SSA_LL.get(i).get(0).left != null && SSA_LL.get(i).get(0).right != null )
	    		  {
	    			  outString += "node" + i + " -> node" + SSA_LL.get(i).get(0).right.get(0).label + ";\n" ;
	    			  outString += "node" + i + " -> node" + SSA_LL.get(i).get(0).left.get(0).label + ";\n" ;
	    			  
	    		  }
	    		  if ( SSA_LL.get(i).get(0).whilejoin != null )
	    		  {
	    			  System.out.println( " WHILEJOIMN");
	    			  outString += "node" + i + " -> node" + SSA_LL.get(i).get(0).whilejoin.get(0).label + ";\n" ;
	    		  }
	    		  if ( SSA_LL.get(i).get(0).ifJoin != null )
	    		  {
	    			  outString += "node" + i + " -> node" + SSA_LL.get(i).get(0).ifJoin.get(0).label + ";\n" ;
	    		  }
	    		  if ( SSA_LL.get(i).get(0).follow != null )
	    		  {
	    			  outString += "node" + i + " -> node" + SSA_LL.get(i).get(0).follow.get(0).label + ";\n" ;
	    		  }
	    		  if ( SSA_LL.get(i).get(0).parent != null )
	    		  {
	    			  //outString += "node" + i + " -> node" + SSA_LL.get(i).get(0).parent.get(0).label + ";\n" ;
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
    
    public String getDirectingIdent ( int BBNode, int a)
    {
    	LinkedList<identNode> BBValues = SSA_LL.get(BBNode).get(0).BBValues;
    	if ( BBValues.size() != 0)
		{
			identNode temp;
			
			for ( int i = 0 ; i < BBValues.size() ; i++ )
			{
				temp = BBValues.get(i);
				if ( a == temp.identAddressinBB )
				{
					return s.Id2String(a) + "_" + temp.identIterinBB;
						
				}
			}
		}
		else 
		{
			
		}
    	return "";
    } 
}