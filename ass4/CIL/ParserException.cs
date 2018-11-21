using System;

public class ParserException: Exception {
  public ParserException (int expected, int got, int line, int col):
    base("expected: " + Scanner.symbols[expected] +
         ", got: " + Scanner.symbols[got] + ":line(" +
         line + "), column(" + col + ")"){  } 
  public ParserException (int expected, int got):
    base("expected: " + Scanner.symbols[expected] +
         ", got: " + Scanner.symbols[got]){  } 
  public ParserException (string msg): base(msg) {  }
}
