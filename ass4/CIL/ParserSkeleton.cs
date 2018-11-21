/*
 * ParserSkeleton.cs (for Assignments 3&4)
 * ICS 142 - Winter 2005
 * 
 * This skeleton parser is provided by courtesy as a starting point of 
 * Assignment 3. You may build your parser from it, or you may borrow 
 * some ideas from it and build on your own.
 *
 * Be aware this is just a skeleton, so you need to add your own class
 * members, data structures, or new classes in order to get a working
 * version. 
 *
 * DON'T add new Error methods. Try to fit the errors you detect in to
 * the provided Error types. Otherwise, your output will not match the 
 * output files in the test cases.
 * 
 */

using System; 
using System.IO;
using System.Collections;

public class Parser {

  /******************************************************* 
   *if you need to declare new classes, declare them here 
   ********************************************************/

  
  /***********************************************************
   * Parser's field declarations go here 
   ************************************************************/
  private Scanner s;
  private Wrapper w;
  private string outputFile;
  public string[] program;     // your program assembly strings go here

  // Add your field declarations here */
  
  /**************************************************************
   * method declarations start here 
   * Important: 
   *     1. You need to modify most of the methods to detect parsing errors
   *        and to create CIL assembly strings.
   *     2. You may need to declare more methods;
   **************************************************************/
     
  /* constructor */
  public Parser (String filename) {
    s = new Scanner("tests/" + filename + ".spl");
    w = new Wrapper();
    outputFile = filename + ".asm";
  }

  public void computation () {
    eat(Scanner.minusToken);
    eat(Scanner.minusToken);
    eat(Scanner.simplToken);
    varDecl(); 
    eat(Scanner.beginToken);
    statSequence();
    eat(Scanner.endToken);
    eat(Scanner.periodToken);	
    eat(Scanner.eofToken);
  
    w.writeFile(outputFile);
  }

  private void varDecl () {
    if (!currentIs(Scanner.varToken)) return;
    eat(Scanner.varToken);
    eat(Scanner.ident);
    while(currentIs(Scanner.commaToken)) {
      eat(Scanner.commaToken);
      eat(Scanner.ident);
    }
    eat(Scanner.semiToken);
  }

  private void statement() {
    if ( currentIs(Scanner.letToken) )
      assignment();
    else if ( currentIs(Scanner.callToken) )
      funcCall();
    else if ( currentIs(Scanner.ifToken) )
      ifStatement();
    else if ( currentIs(Scanner.whileToken) )
      whileStatement();
    else 
      emptyStatementError();
  }
  
  private void statSequence() {
    statement();    
    while ( currentIs(Scanner.semiToken) ) {
      eat(Scanner.semiToken);
      statement();
    }
  }
  
  private void ifStatement() {
    eat(Scanner.ifToken);
    relation();
    eat(Scanner.thenToken);
    statSequence();
    if ( currentIs(Scanner.elseToken) ) {
      eat(Scanner.elseToken);
      statSequence();
    }
    eat(Scanner.fiToken);
  }

  private void whileStatement() {
    eat(Scanner.whileToken);
    relation();
    eat(Scanner.doToken);
    statSequence();
    eat(Scanner.odToken);
  }

  private void assignment() {
    eat(Scanner.letToken);
    eat(Scanner.ident);
    eat(Scanner.becomesToken);
    expression();
  }

  private void funcCall() {
    eat(Scanner.callToken);
    eat(Scanner.ident);
    if ( currentIs(Scanner.openparenToken) ) {
      eat(Scanner.openparenToken);
   //   if ( currentIsExpressionToken() ){ 
        while ( currentIs(Scanner.commaToken) ) {
          eat(Scanner.commaToken);
        }
  //    }
      eat(Scanner.closeparenToken);
    } 
  }
  
  private void factor() {
    if ( currentIs(Scanner.ident) ) {
      eat(Scanner.ident);
    }
    else if ( currentIs(Scanner.number) ) {
      eat(Scanner.number);
    }
    else if ( currentIs(Scanner.openparenToken) ) {
      eat(Scanner.openparenToken);
      expression();
      eat(Scanner.closeparenToken);
    }
    else if ( currentIs(Scanner.callToken) ) 
      funcCall();
    else
      emptyFactorError();
  }
  
  private void term() {
    factor();
    bool times;
    while ( (times = currentIs(Scanner.timesToken))  
            || currentIs(Scanner.divToken) ) {
      if ( times ) {
        eat(Scanner.timesToken);
      }
      else {
        eat(Scanner.divToken);
      }
      factor();
    }
  }
  
  private void expression() {
    bool plus;
    while ( (plus = currentIs(Scanner.plusToken))  
            || currentIs(Scanner.minusToken) ) {
      if ( plus ) {
        eat(Scanner.plusToken);
      }
      else {
        eat(Scanner.minusToken);
      }
      term();
    }
  }
  
  private void relation() {
    expression();

    if ( currentIs(Scanner.eqlToken) ) ;
    else if ( currentIs(Scanner.neqToken) );
    else if ( currentIs(Scanner.lssToken) );
    else if ( currentIs(Scanner.geqToken) );
    else if ( currentIs(Scanner.leqToken) );
    else if ( currentIs(Scanner.gtrToken) );
    else nonRelOpError(s.id);
    
    s.Next();
    expression();
  }

 
  // Checks to see if the current token is the same as "expected".
  // If so, advance to the next token.  Else, throw an exception.
  private void eat (int expected) {
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
