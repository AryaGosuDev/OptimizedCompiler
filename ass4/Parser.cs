// Afshin Mahini   89245265



using System;
using System.IO;
using System.Collections;

public class Parser {

  public string[] program; 
  
  
  private bool debugOn;
  private Scanner s;
  
  private int pc = 0 ;
  private int lastOpUsed ;
  private Wrapper w = new Wrapper();
  private int LastLetId ;
  private int label = 1 ;
  private int varCount = 0 ;
  private int varArrayCount = 0 ;
  string nameOfProc ;
  
  //ALL VARS TO RESET FOR FUNCTIONS PROCS
  private int funcVarCount = 0 ;
  private int funcVarArrayCount = 0;
  private int pcfunc = 0 ;
  private bool returnsVal = false ;
  private int pcf = 0 ;
  public string[] codetemp ;
  public string[] code ;
  private int funcVarLoop = 0 ;
  //private bool codeArray = false ;
  
  
  
  private IdentObject[] identArray = new IdentObject[1000];
  private IdentObject temp ;
  
  private IdentObject[] funcObjects = new IdentObject[1000];
  private IdentObject funcArgTemp ;
  
  private IdentObject[] argObjects = new IdentObject[20];
  
  /* constructor */
    public Parser (String filename) {
      s = new Scanner(filename);
  }

  public void computation(){
    int i = 0 ;
    for ( i = 0 ; i < identArray.Length ; ++i )
    {
    	identArray[i] = new IdentObject();
    }
    
    program = new string[1000] ;
    codetemp = new string[1000];
    //code = new 
    int oneArrayCount = 0 ;
    
    eat(Scanner.minusToken); 
    eat(Scanner.minusToken); 
    eat(Scanner.simplToken);
    while (  (s.sym != Scanner.beginToken) && (s.sym != Scanner.procToken) && (s.sym != Scanner.funcToken)  )
    {
    	varDecl();
    	//Console.WriteLine ( s.sym);
    	
    }
    //codeArray = true; 
    while ( (s.sym == Scanner.procToken) || (s.sym == Scanner.funcToken) )
    {
    	funcVarCount = 0 ;
        funcVarArrayCount = 0 ;
        returnsVal = false  ;
        pcfunc = 0 ;
        pcf = 0 ;
        funcVarLoop = 0;
        int countArgs = 0 ;
        
        
    	funcDecl();
    	
    	i = 0 ;
    	
    	//for ( i = 0 ; i < codetemp.Length ; ++i )
    	//{
    		
    	for ( i = 0 ; i < argObjects.Length ; ++i )
    	{
    		if ( argObjects[i].isArg == true )
    		{
    			++countArgs ;
    		}
    		
    			
    	}
    	
    	string[] code = new string[pcf];
    	
    	for ( i = 0 ; i < pcf ; ++i )
    	{
    		code[i] = codetemp[i] ;
    	}
    	
    	i = 0 ;
	int[] funcOneDimensionArray = new int[funcVarArrayCount] ;
	    
	oneArrayCount = 0;
	//Console.WriteLine ( varCount );
	    
	for ( i = 0 ; i < funcObjects.Length ; ++i )
	{
		if ( funcObjects[i].isArray )
	    	{
	    		funcOneDimensionArray[oneArrayCount++] = funcObjects[i].numOfElements ;
	    		//oneArrayCount++;
	    	}
    	}
    	
    	
    	
    	for ( i = 0 ; i < code.Length ; ++i )
    	{
    		Console.WriteLine ( code[i] );
    	}
    	
    	for ( i = 0 ; i < funcOneDimensionArray.Length ; i++ )
    	{
    		Console.WriteLine ( funcOneDimensionArray[i] + "_---------------");
    	}
    		
    	
    	//Console.WriteLine ( funcVarCount + "_---------------");		
    	w.insertProcedure ( nameOfProc , returnsVal ,  countArgs , code , funcVarCount , funcOneDimensionArray , 50 ) ;
    	
    }
    //codeArray = false ;
    //Console.WriteLine ( "sfdf");
    eat(Scanner.beginToken );
    
    statSequence();
    eat ( Scanner.endToken );
    
    eat ( Scanner.periodToken ) ;
    
    program[pc++] = "ret" ;
    
    i = 0 ;
    int[] oneDimensionArray = new int[varArrayCount] ;
    
    oneArrayCount = 0;
    Console.WriteLine ( varCount );
    
    for ( i = 0 ; i < identArray.Length ; ++i )
    {
    	if ( identArray[i].isArray )
    	{
    		oneDimensionArray[oneArrayCount++] = identArray[i].numOfElements ;
    		//oneArrayCount++;
    	}
    }
    
    //for ( i = 0 ; i < oneDimensionArray.Length ; ++i )
    //{
    	//Console.WriteLine ( oneDimensionArray[i] + "sdddddddddddddd" );
    //}
    
    //Console.WriteLine ( varCount + "hehehehehe" ) ;
    
    //int[] oneDimensionArray = new int[oneArrayCount] ;
    
    //i = 0 ;
    
    //for ( i = 0 ; i < oneDimensionArray.Length ; ++ i )
    //{
    	//oneDimensionArray[i] = 
    
    
    	
    
    
    w.insertMain ( program, varCount , oneDimensionArray , 50  );
    w.WriteFile( "1.asm");
    
  }
  
  public void funcDecl()
  {
  	if ( s.sym == Scanner.procToken ) eat ( Scanner.procToken ) ;
  	else if ( s.sym == Scanner.funcToken ) eat ( Scanner.funcToken ) ;
  	
  	nameOfProc = s.Id2String( s.id )  ;
  	
  	int loop = 0 ;
  	
  	eat ( Scanner.ident );
  	
  	eat ( Scanner.openparenToken ) ;
  	
  	int i ;
  	for ( i = 0 ; i < funcObjects.Length ; ++i )
	{
	    	funcObjects[i] = new IdentObject();
    	}
    	
    	i = 0 ;
    	
    	for ( i = 0 ; i < argObjects.Length ; ++i )
    	{
    		argObjects[i] = new IdentObject() ;
    	}
  	
  	while ( s.sym != Scanner.closeparenToken )
  	{
  		
  		  if ( s.sym == Scanner.ident )
  		  {
  			eat(Scanner.ident);
			temp = argObjects[loop++] ;
    			temp.nameOfIdent = s.Id2String( s.id ) ;
    			temp.isArg = true ;
    			if ( s.sym == Scanner.commaToken ) eat ( Scanner.commaToken ) ;
    		  }
    	}
    	eat ( Scanner.closeparenToken );
    	eat ( Scanner.semiToken );
    	
    	while ( s.sym != Scanner.beginToken )
    	{
    		funcVarDecl() ;
    		//Console.WriteLine ( "FDgfdgfdgfdg");
    	}
    	eat ( Scanner.beginToken );
    	
    	funcStatSeq();
    	
    	eat ( Scanner.endToken );
    	eat ( Scanner.semiToken ) ;
    }
    
  public void funcVarDecl()
  {
  	if ( s.sym == Scanner.varToken )
	{
	      
	    eat(Scanner.varToken);
	    eat(Scanner.ident);
	    temp = funcObjects[funcVarLoop++] ;
	    //Console.WriteLine ( s.Id2String( s.id ) + "sdddddddddd");
	    temp.nameOfIdent = s.Id2String( s.id ) ;
	    funcVarCount ++ ;
	    
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
	    		temp = funcObjects[funcVarLoop++] ;
	    		temp.nameOfIdent = s.Id2String( s.id ) ;
	    		funcVarCount ++ ;
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
	    	tempDimen[dimenLoop++] = s.val ;
	    	eat ( Scanner.number );
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
		    	temp = funcObjects[funcVarLoop++] ;
		    	temp.nameOfIdent = s.Id2String( s.id ) ;
		    	temp.isArray = true ;
		    	temp.numOfElements = dimenCount ;
		    	if ( dimenLoop > 1 ) temp.dimArrayAccess = Dimen ;
		    }
		    else error ( Scanner.commaToken , s.sym ) ;
		
	    	
	    }
	    eat ( Scanner.semiToken );
	}
		
  }
    	
  public void funcStatSeq()
  {
  	funcStatement();
	while ( s.sym == Scanner.semiToken  )
	{
		if ( s.sym == Scanner.semiToken )
	    	{
	    		eat ( Scanner.semiToken );
	    		if ( s.sym == Scanner.endToken ) emptyStatementError() ;
	    		funcStatement();
	    	}
  	}
  }
  
  public int funcStatement()
  {
  
  	if (s.sym == Scanner.letToken )
  	{
  		bool cond = false ;
  		int holdPos = 0 ;
  		IdentObject temp2;
  		int index = 0 ;
  		
  		eat ( Scanner.letToken );
  		eat ( Scanner.ident ) ;
  		int i = 0 ;
  		for ( i= 0 ; i < funcObjects.Length; ++i )
  		{
  			temp = funcObjects [i];
  			if ( temp.nameOfIdent == s.Id2String ( s.id ) )
  			{
  				cond = true ;
  				holdPos = i ;
  			}
    		}
    				
  		if ( cond )
  		{
  			temp2 = temp = funcObjects [holdPos] ;
  			if ( temp2.isArray)
  			{
  				//Console.WriteLine ( "11111");
  				eat ( Scanner.openbracToken );
  				LastLetId = holdPos ;
  				codetemp[pcf++] = "ldloc ";
				codetemp[pcf - 1] = string.Concat ( codetemp[pcf- 1 ] , (holdPos).ToString());
				
  				index = fexp() ;
  				
  				eat ( Scanner.closebracToken );
  				
  				eat ( Scanner.becomesToken );
				
				
				  			
				temp2.isDeclared = true ;
				holdPos ++ ;
				int addToLoc = pc ;
				
				
				
				//program[pc++] = "ldc.i4 " ;
				//program[pc - 1] = string.Concat ( program[pc-1], index.ToString());
				
				temp2.valOfIdent = fexp() ;
				
				//program[pc++] = "ldc.i4 ";
				//program[pc - 1] = string.Concat ( program [pc - 1] , (temp2.valOfIdent).ToString());
				  			
				codetemp[pcf++] = "stelem.i4" ;
				  			
				//program[pc - 1 ] = string.Concat ( program [ pc -1 ] , (holdPos).ToString() );
				  			
  				return 0 ;
  			}
  			else
  			{
  				
  				eat ( Scanner.becomesToken );
  				LastLetId = holdPos ;
  				temp2.valOfIdent = fexp() ;
  				
  				temp2.isDeclared = true ;
  				//holdPos ++ ;
  				int addToLoc = pc ;
  				
  				codetemp[pcf++] = "stloc " ;
  				
  				codetemp[pcf - 1 ] = string.Concat ( codetemp [ pcf -1 ] , (holdPos).ToString() );
  				
  				return 0 ;
  			}
  		}
  		else undeclaredIdentifierError (  s.Id2String ( s.id )) ;
  		return 0;
  	}
  	else if ( s.sym == Scanner.callToken )
	{
	  		eat ( Scanner.callToken ) ;
	  		eat ( Scanner.ident ) ;
	  		
	  		if ( s.Id2String (s.id ) == "inputnum" )
	  		{
	  			if ( s.sym == Scanner.openparenToken )
	  			{
	  				eat ( Scanner.openparenToken ) ;
	  				eat ( Scanner.closeparenToken ) ;
	  			}
	  			
	  			codetemp [ pcf++ ] = w.getProcedureCall("Read",true,0);
	  			
	  			return (funcObjects[LastLetId]).valOfIdent;
	  			
	  		}
	  		else if ( s.Id2String (s.id) == "outputnum" )
	  		{
	  			
	  			eat ( Scanner.openparenToken ) ;
	  			
	  			int outPut = fexp() ;
	  			
	  			codetemp[pcf++] = w.getProcedureCall ( "Write" , false , 1 );
	  			
	  			eat ( Scanner.closeparenToken ) ;
	  			return 0;
	  		}
	  		else if ( s.Id2String (s.id) == "outputnewline" )
	  		{
	  			if ( s.sym == Scanner.openparenToken )
				{
				 	eat ( Scanner.openparenToken ) ;
				  	eat ( Scanner.closeparenToken ) ;
	  			}
	  			
	  			codetemp[pcf++] = w.getProcedureCall("WriteLn",false,0);
	  			
	  			return 0;
	  		}
	  		else funcCallError ( s.Id2String (s.id)) ;
	  		return 0;
	  	
  	}
  	else if ( s.sym == Scanner.ifToken )
  	{
  		eat ( Scanner.ifToken  );
  		frelation() ;
  		int elseLabel = label ;
  		label++;
  		eat( Scanner.thenToken );
  		funcStatSeq ();
  		codetemp[pcf++] = "br " + "Label" + label.ToString() ;
  		int endLabel = label ;
  		label ++ ;
  		codetemp[pcf++] = "Label" + elseLabel.ToString() + ":" ;
  		if ( s.sym == Scanner.elseToken )
  		{
  			eat ( Scanner.elseToken ) ;
  			funcStatSeq() ;
  		}
  		codetemp[pcf++] = "Label" + endLabel.ToString() + ":" ;
  		eat(Scanner.fiToken ) ;
  	}
  	else if ( s.sym == Scanner.whileToken )
  	{
  		eat ( Scanner.whileToken );
  		codetemp[pcf++] = "Label" + label.ToString() + ":" ;
  		int startWhile = label ;
  		++label;
  		frelation ();
  		int endWhile = label ;
  		eat ( Scanner.doToken ) ;
  		++label;
  		funcStatSeq();
  		eat( Scanner.odToken );
  		codetemp[pcf++] = "br " + "Label" + startWhile.ToString() ;
  		codetemp[pcf++] = "Label" + endWhile.ToString() + ":" ;
  	}
  	else if ( s.sym == Scanner.returnToken )
  	{
  		returnsVal = true ;
  		eat ( Scanner.returnToken ) ;
  		fexp();
  		codetemp[pcf++] = "ret" ;
  	}
  		
  	else if ( s.sym == Scanner.endToken ){ return 0 ;}
  	
  	else error ( 0 , 0 );
  	return 0;
  }
  	
  	
  	
  public void varDecl()
  { 
    if ( s.sym == Scanner.varToken )
    {
      
    	eat(Scanner.varToken);
    	eat(Scanner.ident);
    	//Console.WriteLine ( s.id + "omg" );
    	temp = identArray[s.id] ;
    	temp.nameOfIdent = s.Id2String( s.id ) ;
    	++varCount ;
    	
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
    			++varCount ;
    			//Console.WriteLine ( "dsssssssssssdd111111");
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
	    		Console.WriteLine ( s.id + "omg" );
	    		temp.nameOfIdent = s.Id2String( s.id ) ;
	    		temp.isArray = true ;
	    		temp.numOfElements = dimenCount ; 
	    		if ( dimenLoop > 1 ) temp.dimArrayAccess = Dimen ;
	    		//++varCount ;
	    		++varArrayCount ;
	    	}
	    	else error ( Scanner.commaToken , s.sym ) ;
	}
    	eat ( Scanner.semiToken );
    }
	
  }
  
  public void statSequence()
  {
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
  	
  	
  public int statement()
  {
  	if (s.sym == Scanner.letToken )
  	{
  		bool cond = false ;
  		int holdPos = 0 ;
  		IdentObject temp2,temp3;
  		int index = 0 ;
  		
  		eat ( Scanner.letToken );
  		eat ( Scanner.ident ) ;
  		int i = 0 ;
  		for ( i= 0 ; i < identArray.Length; ++i )
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
  				//Console.WriteLine ( "11111");
  				eat ( Scanner.openbracToken );
  				LastLetId = holdPos ;
  				int loop = 0 , loop1 = 1 , holder = 1 ;
  				
  				program[pc++] = "ldloc ";
				program[pc - 1] = string.Concat ( program[pc- 1 ] , (holdPos).ToString());
				
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
							
						
  				index = exp() ;
  				
  				eat ( Scanner.closebracToken );
  				
  				eat ( Scanner.becomesToken );
				
				
				  			
				temp2.isDeclared = true ;
				holdPos ++ ;
				int addToLoc = pc ;
				
				
				
				//program[pc++] = "ldc.i4 " ;
				//program[pc - 1] = string.Concat ( program[pc-1], index.ToString());
				
				temp2.valOfIdent = exp() ;
				
				//program[pc++] = "ldc.i4 ";
				//program[pc - 1] = string.Concat ( program [pc - 1] , (temp2.valOfIdent).ToString());
				  			
				program[pc++] = "stelem.i4" ;
				  			
				//program[pc - 1 ] = string.Concat ( program [ pc -1 ] , (holdPos).ToString() );
				  			
  				return 0 ;
  			}
  			else
  			{
  				
  				eat ( Scanner.becomesToken );
  				LastLetId = holdPos ;
  				temp2.valOfIdent = exp() ;
  				
  				temp2.isDeclared = true ;
  				//holdPos ++ ;
  				int addToLoc = pc ;
  				
  				program[pc++] = "stloc " ;
  				
  				program[pc - 1 ] = string.Concat ( program [ pc -1 ] , (holdPos).ToString() );
  				
  				return 0 ;
  			}
  		}
  		else undeclaredIdentifierError (  s.Id2String ( s.id )) ;
  		return 0;
  	}
  	else if ( s.sym == Scanner.callToken )
	{
	  		eat ( Scanner.callToken ) ;
	  		eat ( Scanner.ident ) ;
	  		
	  		if ( s.Id2String (s.id ) == "inputnum" )
	  		{
	  			if ( s.sym == Scanner.openparenToken )
	  			{
	  				eat ( Scanner.openparenToken ) ;
	  				eat ( Scanner.closeparenToken ) ;
	  			}
	  			
	  			program [ pc++ ] = w.getProcedureCall("Read",true,0);
	  			
	  			return (identArray[LastLetId]).valOfIdent;
	  			
	  		}
	  		else if ( s.Id2String (s.id) == "outputnum" )
	  		{
	  			
	  			eat ( Scanner.openparenToken ) ;
	  			
	  			int outPut = exp() ;
	  			
	  			program[pc++] = w.getProcedureCall ( "Write" , false , 1 );
	  			
	  			eat ( Scanner.closeparenToken ) ;
	  			return 0;
	  		}
	  		else if ( s.Id2String (s.id) == "outputnewline" )
	  		{
	  			if ( s.sym == Scanner.openparenToken )
				{
				 	eat ( Scanner.openparenToken ) ;
				  	eat ( Scanner.closeparenToken ) ;
	  			}
	  			
	  			program[pc++] = w.getProcedureCall("WriteLn",false,0);
	  			
	  			return 0;
	  		}
	  		else
	  		{
	  			int countNumOfArgs = 0 ;
	  			
	  			string tempProcName = s.Id2String (s.id) ;
	  			
	  			eat ( Scanner.openparenToken ) ;
	  			
	  			while ( s.sym != Scanner.closeparenToken )
	  			{
	  				exp();
	  				if ( s.sym == Scanner.commaToken )
	  					eat ( Scanner.commaToken );
	  				++countNumOfArgs ;
	  			}
	  			eat (Scanner.closeparenToken ); 
	  			
	  			program[pc++] = w.getProcedureCall ( s.Id2String (s.id) , true , countNumOfArgs ) ;
	  		}
	  		//else funcCallError ( s.Id2String (s.id)) ;
	  		return 0;
	  	
  	}
  	else if ( s.sym == Scanner.ifToken )
  	{
  		eat ( Scanner.ifToken  );
  		relation() ;
  		int elseLabel = label ;
  		label++;
  		eat( Scanner.thenToken );
  		statSequence ();
  		program[pc++] = "br " + "Label" + label.ToString() ;
  		int endLabel = label ;
  		label ++ ;
  		program[pc++] = "Label" + elseLabel.ToString() + ":" ;
  		if ( s.sym == Scanner.elseToken )
  		{
  			eat ( Scanner.elseToken ) ;
  			statSequence() ;
  		}
  		program[pc++] = "Label" + endLabel.ToString() + ":" ;
  		eat(Scanner.fiToken ) ;
  	}
  	else if ( s.sym == Scanner.whileToken )
  	{
  		eat ( Scanner.whileToken );
  		program[pc++] = "Label" + label.ToString() + ":" ;
  		int startWhile = label ;
  		++label;
  		relation ();
  		int endWhile = label ;
  		eat ( Scanner.doToken ) ;
  		++label;
  		statSequence();
  		eat( Scanner.odToken );
  		program[pc++] = "br " + "Label" + startWhile.ToString() ;
  		program[pc++] = "Label" + endWhile.ToString() + ":" ;
  	}
  	else if ( s.sym == Scanner.returnToken )
  	{
  		eat ( Scanner.returnToken ) ;
  		exp();
  		program[pc++] = "ret" ;
  	}
  		
  	else if ( s.sym == Scanner.endToken ){ return 0 ;}
  	
  	else error ( 0 , 0 );
  	return 0;
  }
  
  public int exp()
  {
    	int val ;
    	
    	val = term () ;
    	
    	while ( s.sym == Scanner.plusToken ||  s.sym == Scanner.minusToken)
    	{
    		if ( s.sym == Scanner.plusToken )
    		{
    			lastOpUsed = Scanner.plusToken ;
    			eat ( Scanner.plusToken );
    		
    			val += term();
    			program[pc++] = "add" ;
  
    		}
    		else if ( s.sym == Scanner.minusToken )
    		{
    			lastOpUsed = Scanner.minusToken ;
    			eat (  Scanner.minusToken );
    			val -= term () ;
    			program[pc++] = "sub" ;
    		}
    		else error ( 0 , 0 ) ;
    	}
    	return val ;
  }
  
  public int term()
    {
    	int val ;
    	val = factor () ;
    	while ( s.sym == Scanner.timesToken  || s.sym == Scanner.divToken )
    	{
    		if ( s.sym == Scanner.timesToken )
    		{	
    			lastOpUsed = Scanner.timesToken ;
    			eat (Scanner.timesToken);
    		
    			//s.Next ();
    			val *= factor () ;
    			program[pc++] = "mul" ;
    		}
    		else if ( s.sym == Scanner.divToken )
    		{
    			lastOpUsed = Scanner.divToken ;
    			eat ( Scanner.divToken );
    			//s.Next ();
    			val /= factor() ;
    			program[pc++] = "div" ;
    		}
    		else error ( 0 , 0 ) ;
    	}
    	return val ;
    }
    
    public int factor ()
    {
    	int val = 0;
    	
    	if ( s.sym == Scanner.number )
    	{
    		val = s.val ;
    		int addToLoc = pc ;
    		
    		program[pc++] = "ldc.i4 ";
    		
    		program[pc - 1 ] = string.Concat ( program[pc - 1 ] , val.ToString() );
    		
    		eat ( Scanner.number ) ;
    		
    	}
    	else if ( s.sym == Scanner.openparenToken )
    	{
    		eat ( Scanner.openparenToken );
    		val = exp () ;
    		
    		if ( s.sym == Scanner.closeparenToken )
    		{
    			eat ( Scanner.closeparenToken ) ;
    			
    		}
    		else error ( 0 , 0 ) ;
    		
    	}
    	else if ( s.sym == Scanner.ident )
    	{
    		bool cond = false ;
  		int holdPos = 0 ;
  		  		
  		int i = 0 ;
  		for ( i =0 ; i < identArray.Length ; ++i )
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
  				
  				program[pc++] = "ldloc " ;
  				//holdPos ++ ;
  				program[pc - 1] = string.Concat ( program [ pc -1 ] , holdPos.ToString() );
  				eat(Scanner.ident );
  				eat(Scanner.openbracToken );
  				exp();
  				eat(Scanner.closebracToken);
  				
  				program[pc++] = "ldelem.i4";
  			}
  			else
  			{
  				temp = identArray [holdPos] ;
  		        
  		  		val = temp.valOfIdent  ;
  		  		program[pc++] = "ldloc " ;
  		  		//holdPos ++ ;
  		  		program[pc - 1] = string.Concat ( program [ pc -1 ] , holdPos.ToString() );
  		  		eat(Scanner.ident );
  		  	}
  		  	
  		  	
  		}
  		else undeclaredIdentifierError ( s.Id2String ( s.id ) );
  		
  		
  		return val ;
  	}
  	else if ( s.sym == Scanner.callToken ) return statement () ;
  	else emptyFactorError() ;
    		
    	return val ;
  }
  
  public int fexp()
  {
      	int val ;
      	
      	val = fterm () ;
      	
      	while ( s.sym == Scanner.plusToken ||  s.sym == Scanner.minusToken)
      	{
      		if ( s.sym == Scanner.plusToken )
      		{
      			lastOpUsed = Scanner.plusToken ;
      			eat ( Scanner.plusToken );
      		
      			val += fterm();
      			codetemp[pcf++] = "add" ;
    
      		}
      		else if ( s.sym == Scanner.minusToken )
      		{
      			lastOpUsed = Scanner.minusToken ;
      			eat (  Scanner.minusToken );
      			val -= fterm () ;
      			codetemp[pcf++] = "sub" ;
      		}
      		else error ( 0 , 0 ) ;
      	}
      	return val ;
    }
    
    public int fterm()
      {
      	int val ;
      	val = ffactor () ;
      	while ( s.sym == Scanner.timesToken  || s.sym == Scanner.divToken )
      	{
      		if ( s.sym == Scanner.timesToken )
      		{	
      			lastOpUsed = Scanner.timesToken ;
      			eat ( Scanner.timesToken );
      		
      			//s.Next ();
      			val *= ffactor () ;
      			codetemp[pcf++] = "mul" ;
      		}
      		else if ( s.sym == Scanner.divToken )
      		{
      			lastOpUsed = Scanner.divToken ;
      			eat ( Scanner.divToken );
      			//s.Next ();
      			val /= ffactor() ;
      			codetemp[pcf++] = "div" ;
      		}
      		else error ( 0 , 0 ) ;
      	}
      	return val ;
      }
      
      public int ffactor ()
      {
      	int val = 0;
      	
      	if ( s.sym == Scanner.number )
      	{
      		val = s.val ;
      		int addToLoc = pc ;
      		
      		codetemp[pcf++] = "ldc.i4 ";
      		
      		codetemp[pcf - 1 ] = string.Concat ( codetemp[pcf - 1 ] , val.ToString() );
      		
      		eat ( Scanner.number ) ;
      		
      	}
      	else if ( s.sym == Scanner.openparenToken )
      	{
      		eat ( Scanner.openparenToken );
      		val = fexp () ;
      		
      		if ( s.sym == Scanner.closeparenToken )
      		{
      			eat ( Scanner.closeparenToken ) ;
      			
      		}
      		else error ( 0 , 0 ) ;
      		
      	}
      	else if ( s.sym == Scanner.ident )
      	{
      		bool cond = false ;
    		int holdPos = 0 ;
    		  		
    		int i = 0 ;
    		for ( i =0 ; i < funcObjects.Length ; ++i )
    		{
    			temp = funcObjects [i];
    		  	if ( temp.nameOfIdent == s.Id2String ( s.id ) )
    		  	{
    		  		cond = true ;
    		  		holdPos = i ;
    		  	}
    		}
    		
    		if ( cond == false )
    		{
    			i = 0;
    			for ( i = 0 ; i < argObjects.Length ; ++i )
    			{
    				temp = argObjects [i];
    				//Console.WriteLine ( temp.nameOfIdent ); 	
    				if ( temp.nameOfIdent == s.Id2String ( s.id ) )
				{
					holdPos = i ;
    		  		
    		  		}	
    		  	}
    		  	
			 
			    	temp = argObjects [holdPos] ;
			    	//Console.WriteLine ( "22222222222222");
			    	val = temp.valOfIdent ;
			    	codetemp[pcf++] = "ldarg " ;
			    	codetemp[pcf - 1] = string.Concat ( codetemp [ pcf -1 ] , holdPos.ToString() );
			    	eat(Scanner.ident );
			 
		}
    		  	    				
    		else if (  ( cond && ((funcObjects[holdPos]).isDeclared == true) ))		
    		{
    			if ( (funcObjects[holdPos]).isArray )
    			{
    				temp = funcObjects [holdPos] ;
    				val = temp.valOfIdent  ;
    				
    				codetemp[pcf++] = "ldloc " ;
    				//holdPos ++ ;
    				codetemp[pcf - 1] = string.Concat ( codetemp [ pcf -1 ] , holdPos.ToString() );
    				eat(Scanner.ident );
    				eat(Scanner.openbracToken );
    				fexp();
    				eat(Scanner.closebracToken);
    				
    				codetemp[pcf++] = "ldelem.i4";
    			}
    			else
    			{
    				
    				    					
    					temp = funcObjects [holdPos] ;
    		        
    		  			val = temp.valOfIdent  ;
    		  			codetemp[pcf++] = "ldloc " ;
    		  			//holdPos ++ ;
    		  			codetemp[pcf - 1] = string.Concat ( codetemp [ pcf -1 ] , holdPos.ToString() );
    		  			eat(Scanner.ident );
    		  		
    		  	}
    		      		  	
    		}
    		else undeclaredIdentifierError ( s.Id2String ( s.id ) );
    		
    		
    		return val ;
    	}
    	else if ( s.sym == Scanner.callToken ) return funcStatement () ;
    	else emptyFactorError() ;
      		
      	return val ;
  }
  
  private void relation ()
  {
  	exp();
  	
  	int relOp = s.sym ;
  	//Console.WriteLine ( relOp + "dsssssss" );
  	if ( !isRelop ( relOp ))
  	{ 
  		nonRelOpError (relOp) ;
  	}
  	
  	if ( relOp == Scanner.lssToken )
  	{
  		eat ( Scanner.lssToken );
  		exp();
  		program[pc++] = "bge " + "Label" + label.ToString() ;
  	}
  	else if ( relOp == Scanner.gtrToken )
	{
		eat ( Scanner.gtrToken );
	  	exp();
	  	program[pc++] = "ble " + "Label" + label.ToString() ;
  	}
  	else if ( relOp == Scanner.leqToken )
	{
	  	eat ( Scanner.leqToken );
	  	exp();
	  	program[pc++] = "bgt " + "Label" + label.ToString() ;
  	}
  	else if ( relOp == Scanner.geqToken )
	{
	  	eat ( Scanner.geqToken );
	  	exp();
	  	program[pc++] = "blt " + "Label" + label.ToString() ;
  	}
  	else if ( relOp == Scanner.eqlToken )
	{
	  	eat ( Scanner.eqlToken );
	  	exp();
	  	program[pc++] = "ceq" ;
	  	program[pc++] = "brfalse " + "Label" + label.ToString();
  	}
  	else if ( relOp == Scanner.neqToken )
  	{
  		eat ( Scanner.neqToken ) ;
  		exp ();
  		program[pc++] = "beq " + "Label" + label.ToString() ;
  	}
  }
  
  private void frelation ()
    {
    	fexp();
    	
    	int relOp = s.sym ;
    	//Console.WriteLine ( relOp + "dsssssss" );
    	if ( !isRelop ( relOp ))
    	{ 
    		nonRelOpError (relOp) ;
    	}
    	
    	if ( relOp == Scanner.lssToken )
    	{
    		eat ( Scanner.lssToken );
    		fexp();
    		codetemp[pcf++] = "bge " + "Label" + label.ToString() ;
    	}
    	else if ( relOp == Scanner.gtrToken )
  	{
  		eat ( Scanner.gtrToken );
  	  	fexp();
  	  	codetemp[pcf++] = "ble " + "Label" + label.ToString() ;
    	}
    	else if ( relOp == Scanner.leqToken )
  	{
  	  	eat ( Scanner.leqToken );
  	  	fexp();
  	  	codetemp[pcf++] = "bgt " + "Label" + label.ToString() ;
    	}
    	else if ( relOp == Scanner.geqToken )
  	{
  	  	eat ( Scanner.geqToken );
  	  	fexp();
  	  	codetemp[pcf++] = "blt " + "Label" + label.ToString() ;
    	}
    	else if ( relOp == Scanner.eqlToken )
  	{
  	  	eat ( Scanner.eqlToken );
  	  	fexp();
  	  	codetemp[pcf++] = "ceq" ;
  	  	codetemp[pcf++] = "brfalse " + "Label" + label.ToString();
    	}
    	else if ( relOp == Scanner.neqToken )
    	{
    		eat ( Scanner.neqToken ) ;
    		fexp ();
    		codetemp[pcf++] = "beq " + "Label" + label.ToString() ;
    	}
  }
  	  	
  private bool isRelop ( int op )
  {
  	if (( op <= 25 ) && ( op >= 20 )) return true ;
  	else return false ;
  }
  

  // Checks to see if the current token is the same as "expected".
  // If so, advance to the next token.  Else, throw an exception.
  private void eat(int expected)
  {
    Console.WriteLine ( s.sym );
    //Console.WriteLine ( "\n");
    if (s.sym != expected) {
      error(expected, s.sym);
    }
    s.Next();
  }

  // Returns true if the current token is the same as "expected".
    private bool currentIs (int expected) {
      return s.sym == expected;
    }
  
    // ***************** Error Handling Functions *******************
    // DON'T add new Error methods.
    // Used to signal an error while parsing
    private void error (int expected, int got) {
      throw new ParserException(expected, got);
    }
  
    // Used to signal that "ident" is an undeclared identifier
    private void undeclaredIdentifierError (String ident) {
      throw new ParserException("Undeclared Identifier: " + ident);
    }
    
    // Used in statement, when none of the three possibilities are present
    // (i.e. assignment, funcCall, ifStatement)
    private void emptyStatementError () {
      throw new ParserException("Empty Statment");
    }
  
    // Used in factor, when none of the four possibilities are present
    // (i.e. ident, number, "(" expression ")", funcCall
    private void emptyFactorError () {
      throw new ParserException("Empty Factor");
    }
    
    // Used in factor
    private void factorError (String message) {
      throw new ParserException("Factor parsing error: " + message);
    }
  
    // Used when program expects a relationship operator (e.g. <, ==, >=),
    // but got a different token
    private void nonRelOpError (int sym) {
      throw new ParserException("Expected a relationship operator, got: " 
                                + Scanner.symbols[sym]);
    }
  
    // Used to signal that "ident" is an undeclared identifier
    private void funcCallError (String name) {
      throw new ParserException("Function call error: " + name);
    }
    
    //Used in varDecl
    private void varDeclError(String name) {
      throw new ParserException("Variable declaration error: " + name);
    }
  
    //Used in assignment
    private void assignmentError(String name) {
      throw new ParserException("Assignment error: " + name);
    }
    
    //Used in code generation
    private void CGError(String message) {
      throw new ParserException("Code Generation Error: " + message);
    }
}

public class IdentObject
{
	public string nameOfIdent ;
	public int valOfIdent ;
	public bool isDeclared ;
	public bool isArray ;
	public int numOfElements ;
	public bool isArg ;
	public int[] dimArrayAccess;
	
	
	public IdentObject ()
	{
		nameOfIdent = "";
		valOfIdent = 0 ;
		isDeclared = false ;
		isArray = false ;
		numOfElements = 0 ;
		isArg = false ;
		
	}
}

