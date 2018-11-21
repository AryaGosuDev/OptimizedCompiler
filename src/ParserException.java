
public class ParserException extends Exception 
{
  public ParserException (int expected, int got, int line, int col){
	  
    super("expected: " + Scanner.symbols[expected] +
         ", got: " + Scanner.symbols[got] + ":line(" +
         line + "), column(" + col + ")") ;
  }
  
  public ParserException (int expected, int got){
	  
    super("expected: " + Scanner.symbols[expected] +
         ", got: " + Scanner.symbols[got]);  
         } 
  
  public ParserException (String msg) { super(msg);  }
}
