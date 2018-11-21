public class IdentObjectComp
{
	public String nameOfIdent ;
	public int valOfIdent ;
	public boolean isDeclared ;
	public boolean isArray ;
	public int numOfElements ;
	public boolean isArg ;
	public int[] dimArrayAccess;
	public int identIteration;
	public int funcNumber ;
	
	
	public IdentObjectComp ()
	{
		nameOfIdent = "";
		valOfIdent = 0 ;
		isDeclared = false ;
		isArray = false ;
		numOfElements = 0 ;
		isArg = false ;
		identIteration = 0;
		funcNumber = 0;
		
	}
	
	public IdentObjectComp ( int func )
	{
		nameOfIdent = "";
		valOfIdent = 0 ;
		isDeclared = false ;
		isArray = false ;
		numOfElements = 0 ;
		isArg = false ;
		identIteration = 0;
		funcNumber = func;
		
	}
	
	public IdentObjectComp ( int func, boolean isArgIn )
	{
		nameOfIdent = "";
		valOfIdent = 0 ;
		isDeclared = false ;
		isArray = false ;
		numOfElements = 0 ;
		isArg = isArgIn ;
		identIteration = 0;
		funcNumber = func;
		
	} 
}