import java.util.LinkedList;
import java.util.Stack;


public class FuncHandler {
	
	public LinkedList<LinkedList<SSAAssigns>> SSA_LL;
	
	private IdentObjectComp temp ;
	  
	//public IdentObjectComp[] funcIdentObjects = new IdentObjectComp[1000]; //re written as a linked list
	//private IdentObjectComp funcArgTemp ;
	  
	//public IdentObjectComp[] argObjects = new IdentObjectComp[1000]; //re written as a linked list
	
	private Scanner s;
	
	private int pc;
	private int LastLetId ;
	private int varArrayCount = 0 ;
	String nameOfProc ;
	
	private int numofFunction;
	public int op;
	public int bbLevel = 0;
	
	private char direction;
	  
	private Stack<controlStackItem> nestStack;
	private Stack<Character> dirStack ;
	
	public IdentObjectComp[] baseIdentObject;
	
	public LinkedList<IdentObjectComp[]> funcIdentObjectsHolder ;
	private IdentObjectComp[] funcIdentObjects ;
	private IdentObjectComp funcArgTemp ;
	
	public LinkedList<IdentObjectComp[]> argObjectsHolder ; 
	private IdentObjectComp[] argObjects ;
	
	public boolean[] Registers;
	
	
	public FuncHandler ( LinkedList<LinkedList<SSAAssigns>> inSSA, Scanner inS , IdentObjectComp[] inbaseIdentObject )
	{
		Registers = new boolean[32];
		SSA_LL = inSSA ;
		s = inS;
		nestStack = new Stack();
	    dirStack = new Stack();
	    funcIdentObjectsHolder = new LinkedList<IdentObjectComp[]>();
	    argObjectsHolder = new LinkedList<IdentObjectComp[]>() ;
	    baseIdentObject = inbaseIdentObject;
		
	}
	
	public void setFunctionNumber ( int inFunNum, int inbbLevel )
	{
		numofFunction = inFunNum;
		bbLevel = inbbLevel;
		
		
	}
	
	public int handleFunction () throws Exception
	{
		funcDecl();
		return bbLevel;
	}
	
	public void funcDecl() throws Exception
	{
	  	if ( s.sym == Scanner.procToken ) eat ( Scanner.procToken ) ;
	  	else if ( s.sym == Scanner.funcToken ) eat ( Scanner.funcToken ) ;
	  	
	  	nameOfProc = s.Id2String( s.id ) ;
	  	
	  	temp = baseIdentObject[s.id] ; //save the func name in the main object array
    	temp.nameOfIdent = s.Id2String( s.id ) ;
    	if ( temp.isDeclared == true ) varDeclError ( "Already declared");
    	temp.isDeclared = true;
    	temp.funcNumber = numofFunction;
	  	
	  	funcIdentObjectsHolder.add(new IdentObjectComp[1000] );
	  	funcIdentObjects = funcIdentObjectsHolder.get(numofFunction -1 );
	  	
	  	argObjectsHolder.add(new IdentObjectComp[1000]);
	  	argObjects = argObjectsHolder.get(numofFunction -1) ;
	  	
	  	int argNumber = 0 ;
	  	
	  	eat ( Scanner.ident );
	  	
	  	eat ( Scanner.openparenToken ) ;
	  	
	  	int i ;
	  	for ( i = 0 ; i < funcIdentObjects.length ; ++i )
		{
		    funcIdentObjects[i] = new IdentObjectComp(numofFunction);
	  		
	    }
	    	
		for ( i = 0 ; i < argObjects.length ; ++i )
		{
			argObjects[i] = new IdentObjectComp( numofFunction , true) ;
		}
	  	
	  	while ( s.sym != Scanner.closeparenToken )
	  	{
	  		
	  		  if ( s.sym == Scanner.ident )
	  		  {
					eat(Scanner.ident);
					temp = argObjects[argNumber++] ;
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
	    	
	    	bbLevel = 0;
	    	
	    	statSequence();
	        eat (Scanner.endToken );
	        
	        
	    	eat ( Scanner.semiToken ) ;
	  }
	
	public void varDecl() throws Exception
	{ 
	    if ( s.sym == Scanner.varToken )
	    {
	      
	    	eat(Scanner.varToken);
	    	eat(Scanner.ident);
	    	
	    	temp = funcIdentObjects[s.id] ;
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
	    			temp = funcIdentObjects[s.id] ;
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
		    		temp = funcIdentObjects[s.id] ;
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
		  
		   //if ( SSA_LL.size() == 0 ) // beginning of the program
		   if ( bbLevel == 0)
		   {
				
				LinkedList<SSAAssigns> llsub = new LinkedList<SSAAssigns>(); 
				if (SSA_LL.size() == 0  ) llsub.addFirst(new SSAAssigns(bbLevel) );
				else llsub.add(new SSAAssigns(bbLevel) );
				
				llsub.get(bbLevel).isRoot = false;
				llsub.get(bbLevel).funcNumber = numofFunction;
				llsub.get(bbLevel).isFunc = true;
				
				llsub.get(0).identArray = funcIdentObjects ;
				llsub.get(0).funcArgumentArray = argObjects ;
				
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
	  		temp = funcIdentObjects[s.id] ;
	    	temp.nameOfIdent = s.Id2String( s.id ) ;
	    	
	  		
	  		for ( int i= 0 ; i < funcIdentObjects.length; ++i )
	  		{
	  			temp = funcIdentObjects [i];
	  			if ( temp.nameOfIdent == s.Id2String ( s.id ) )
	  			{
	  				cond = true ;
	  				holdPos = i ;
	  			}
	    	}
	  		if ( cond )
	  		{
	  			temp2 = temp = funcIdentObjects [holdPos] ;
	  			if ( temp2.isArray)
	  			{
	  				
	  				eat ( Scanner.openbracToken );
	  				LastLetId = holdPos ;
	  				int loop = 0 , loop1 = 1 , holder = 1 ;	
									
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
							if ( PhiHandler.handlePhiWhile(holdPos, bbLevel, SSA_LL, funcIdentObjects, pc, nestStack) ) pc++;
						}
						else if ( PhiHandler.handlePhiIf(holdPos, bbLevel, SSA_LL, funcIdentObjects, pc, nestStack, direction) ) pc++;
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
		  			
		  			return (funcIdentObjects[LastLetId]).valOfIdent;
		  			
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
		  		//else funcCallError ( s.Id2String (s.id)) ;
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
			PhiHandler.updatePhis(funcIdentObjects, SSA_LL, bbLevel, nestStack, ifJoinNode, bbLevel );//update phis after if and else is done, only bbvalues of level not identiteration
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
	  			

	  			pc = PhiHandler.updateNestedPhis(funcIdentObjects, SSA_LL, bbLevel, nestStack, priorJoinllsub, pc, dirStack);
	  		}
	  		
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
				
				nestStack.push(new controlStackItem(0, bbLevel-1, bbLevel, llsub));
				
				SSA_LL.get(nestStack.peek().bbParent).get(0).follow = llsub ; //the block before the while join block is linked 
				
				SSA_LL.add(llsub); //add while bb join to master list
				llsub.get(0).updateDoms(bbLevel-1, bbLevel, SSA_LL);
		  		
				
				
				condRes = relation ();
		  		CondNegBraFwd(condRes);
		  		
		  		bbLevel ++ ;
		  		
		  		LinkedList<SSAAssigns> llsub2 = new LinkedList<SSAAssigns>();  //make inedge for while bb
		  		llsub2.addFirst(new SSAAssigns(bbLevel) );
		  		llsub2.get(0).whilejoin = SSA_LL.get(bbLevel-1); //point back to join block for while
		  		llsub.get(0).inedge = llsub2; //join block now has a pointer to while block
		  		SSA_LL.add(llsub2); //add while block
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
		  		PhiHandler.updatePhis(funcIdentObjects, SSA_LL, bbLevel, nestStack, llsub,inEdgebbLevel   );
		  		nestStack.pop();
		  		
		  		if ( !nestStack.isEmpty())
		  		{
		  			
		  			temp.get(0).follow = nestStack.peek().bbJoinPointer;
		  			
		  			SSA_LL.get(nestStack.peek().bbjoinNode+1).get(0).whilejoin = null;
		  			
		  			if ( SSA_LL.get(nestStack.peek().bbParent).get(0).left != null )
		  			{
		  				SSA_LL.get(nestStack.peek().bbParent).get(0).left.get(0).ifJoin = null ;
		  			}
		  			
		  			
		  			
		  			pc = PhiHandler.updateNestedPhis(funcIdentObjects, SSA_LL, bbLevel, nestStack, priorJoinllsub, pc, dirStack);
		  		}
		  		
		  		if ( llsub.get(0).inedge.get(0).whilejoin == null ) //cycle through nested loop top update variables if move was made at end of the loop
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
	    			
	    			eat ( Scanner.plusToken );
	    		
	    			y = term();
	    			
	    			
	    			x = Compute( DLX.PlusInt , x, y);
	  
	    		}
	    		else if ( s.sym == Scanner.minusToken )
	    		{
	    			
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
    			
    			eat (Scanner.timesToken);
    		

    			y = factor () ;
    			
    			x = Compute (DLX.TimesInt, x, y );
    		}
    		else if ( s.sym == Scanner.divToken )
    		{
    			
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
		  		for ( i =0 ; i < funcIdentObjects.length ; ++i )
		  		{
		  			temp = funcIdentObjects [i];
		  		  	if ( temp.nameOfIdent == s.Id2String ( s.id ) )
		  		  	{
		  		  		cond = true ;
		  		  		holdPos = i ;
		  		  	}
		  		}
		  		if ( cond && ((funcIdentObjects[holdPos]).isDeclared == true) )
		  		{
		  			if ( (funcIdentObjects[holdPos]).isArray )
		  			{
		  				temp = funcIdentObjects [holdPos] ;
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
		  				temp = funcIdentObjects [holdPos] ;
		  				
		  				
		  		        
		  		  		val = temp.valOfIdent  ;
		  		  		x.kind = DLX.VarInt;
		  		  		x.address = holdPos;
		  		  		if ( SSA_LL.get(bbLevel).get(0).getIteration(funcIdentObjects[holdPos].nameOfIdent) != 0 ) x.value = SSA_LL.get(bbLevel).get(0).getIteration(funcIdentObjects[holdPos].nameOfIdent) ;
		  		  		else x.value = funcIdentObjects[x.address].identIteration;
		  		  		
		  		  		eat(Scanner.ident );
		  		  	}
		  		  	
		  		  	
		  		}
		  		else undeclaredIdentifierError ( s.Id2String ( s.id ) );
		  		
		  		
		  		return x ;
	  	}
	  	else if ( s.sym == Scanner.callToken )
	  	{
	  		//x.kind = 
	  		
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
				
				if (x.regno == 0)
				{
					x.regno = AllocateReg();
					
					
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
				
				if (x.regno == 0)
				{
					x.regno = AllocateReg();
					
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
	 
	 public void CondNegBraFwd(Result x)
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
}
