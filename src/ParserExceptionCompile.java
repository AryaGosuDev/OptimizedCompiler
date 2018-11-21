
public class ParserExceptionCompile extends Exception 
{
  public ParserExceptionCompile (int expected, int got, int line, int col){
	  
    super("expected: " + Scanner.symbols[expected] +
         ", got: " + Scanner.symbols[got] + ":line(" +
         line + "), column(" + col + ")") ;
  }
  
  public ParserExceptionCompile (int expected, int got){
	  
    super("expected: " + Scanner.symbols[expected] +
         ", got: " + Scanner.symbols[got]);  
         } 
  
  public ParserExceptionCompile (String msg) { super(msg);  }
}
