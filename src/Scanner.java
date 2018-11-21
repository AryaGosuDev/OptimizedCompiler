//Afshin Mahini 89245265

import java.io.*;
import java.lang.String;
import java.util.Hashtable;
import java.lang.Character;

// encapsulates streams of tokens
public class Scanner {
  public int sym; // the current token on the input,
                  // 0=error token, 255=end-of-file token
  public int val; // value of the last number encountered
  public int id;  // id of the last identifier encountered

  public  FileReaderFile fr;
  private Hashtable keywords; // keywords -> values
  private int nextid;         // next id number to use
  public  Hashtable idents;   // ident -> id
  private Hashtable ids;      // id -> ident
  
  
  // advance to the next token
  public void Next () { 
    while ((fr.sym != 0x00) && 
           (fr.sym != 0xff) && 
           (Character.isWhitespace((byte)fr.sym) ) ) {
     fr.Next(); 
    }
    //if (fr.sym == FileReaderFile.EOF) {
    if ( fr.sym == -1) {
      sym = eofToken;
    } else if (fr.sym == FileReaderFile.ERROR) {
      sym = errorToken; 
    } else if (fr.sym == '*') { sym = timesToken; fr.Next(); 
    } else if (fr.sym == '/') { sym = divToken; fr.Next(); 
    } else if (fr.sym == '+') { sym = plusToken; fr.Next(); 
    } else if (fr.sym == '-') { sym = minusToken; fr.Next(); 
    } else if (fr.sym == '=') {
      fr.Next();
      if (fr.sym == '=') {
        sym = eqlToken; fr.Next();
      } else {
        sym = errorToken; 
      }
    } else if (fr.sym == '!') {
      fr.Next();
      if (fr.sym == '=') { 
        sym = neqToken; fr.Next(); 
      } else { 
        sym = errorToken;
      }
    } else if (fr.sym == '<') {
      fr.Next();
      if (fr.sym == '=') {
        sym = leqToken; fr.Next(); 
      } else if (fr.sym == '-') { 
        sym = becomesToken; fr.Next(); 
      } else { 
        sym = lssToken; 
      }
    } else if (fr.sym == '>') {
      fr.Next();
      if (fr.sym == '=') { 
        sym = geqToken; fr.Next();
      } else { 
        sym = gtrToken; 
      }
    } else if (fr.sym == '.') { sym = periodToken; fr.Next();
    } else if (fr.sym == ',') { sym = commaToken; fr.Next(); 
    } else if (fr.sym == ')') { sym = closeparenToken; fr.Next(); 
    } else if (fr.sym == '(') { sym = openparenToken; fr.Next();
    } else if (fr.sym == ';') { sym = semiToken; fr.Next(); 
    } else if (fr.sym == '}') { sym = endToken; fr.Next(); 
    } else if (fr.sym == '{') { sym = beginToken; fr.Next();
    } else if (fr.sym == '[') { sym = openbracToken; fr.Next();
    } else if (fr.sym == ']') { sym = closebracToken; fr.Next();
    } else if (Character.isDigit((char) fr.sym)) {
      String n = "";
	  //Lei fr to FileReader
      while (fr.sym != FileReaderFile.EOF && fr.sym != FileReaderFile.ERROR 
             && Character.isDigit((char) fr.sym)) {
        n += ((char) fr.sym); fr.Next(); 
      }
      val =  Integer.parseInt(n);
      sym = number;
    } else if (Character.isLetter((char) fr.sym)) {
      String s = "";
	  //Lei fr to FileReader
	  while (fr.sym != FileReaderFile.EOF && fr.sym != FileReaderFile.ERROR 
             && Character.isLetterOrDigit((char) fr.sym)) {
        s += ((char) fr.sym); fr.Next(); 
      }
      if (keywords.containsKey(s)) { 
        sym = (Integer.parseInt( keywords.get(s).toString()));
      } else {
        sym = ident;
        if (idents.containsKey(s)) { 
          id = (Integer.parseInt(idents.get(s).toString()));
        } else {
          id = nextid++;
          
          idents.put(s, id);
          
          ids.put(id, s);
          
        }
      }
    } else {
      System.out.println("Scanner.Next() encountered unknown character"); 
      System.exit(1);
    }
  }
  
  // signal an error message (calls FileReader.Error in turn)
  public void Error (String errorMsg) { 
    fr.Error(errorMsg);
  }

  // constructor: open file and scan the first token into 'sym'
  public Scanner (String fileName) { 
    fr = new FileReaderFile(fileName);

    idents = new Hashtable();
    ids = new Hashtable();
    keywords = new Hashtable();
    
    
    
    keywords.put("then", thenToken);
    keywords.put("do", doToken);
    keywords.put("od", odToken);
    keywords.put("fi", fiToken);
    keywords.put("else", elseToken);
    keywords.put("elseif", elseifToken);
    keywords.put("let", letToken);
    keywords.put("call", callToken);
    keywords.put("if", ifToken);
    keywords.put("while", whileToken);
    keywords.put("return", returnToken);
    keywords.put("var", varToken);
    keywords.put("function", funcToken);
    keywords.put("procedure", procToken);
    keywords.put("main", mainToken);
    keywords.put("array", arrToken);
    
    id = nextid++;
    idents.put("OutputNum", id);
    ids.put(id, "OutputNum");
    
    id = nextid++;
    idents.put("InputNum", id);
    ids.put(id, "InputNum");
    
    id = nextid++;
    idents.put("OutputNewLine", id);
    ids.put(id, "OutputNewLine");
    
    

    Next();
  }

  /* identifier table methods */

  public String Id2String (int id) {
    if (ids.containsKey(id)) {
      return (ids.get(id).toString()); //// CHECK
    } else {
      return "***NOT FOUND, id=" + id + "***";
    }
  }

  public int String2Id (String name) {
    if (idents.containsKey(name)) { 
      return (Integer.parseInt(idents.get(name).toString())); //// CHECK 
    } else {
      return -1;
    }
  }
  
  public void purgeAllIds( IdentObjectComp[] identArray )
  {
	  for ( int i = 0; i < identArray.length; ++i)
	  {
		  if ( identArray[i].funcNumber == 0 && identArray[i].nameOfIdent != "" )
		  {  
			  ids.remove(Integer.parseInt(idents.get(identArray[i].nameOfIdent).toString()));
			  idents.remove(identArray[i].nameOfIdent); 
		  } 
	  }
  }
  
  /* 
   * *** DO NOT MODIFY ***
   * Token values 
   */
  public static final int errorToken      = 0;
  public static final int timesToken      = 1;
  public static final int divToken        = 2;
  public static final int plusToken       = 11;
  public static final int minusToken      = 12;
  public static final int eqlToken        = 20;
  public static final int neqToken        = 21;
  public static final int lssToken        = 22;
  public static final int geqToken        = 23;
  public static final int leqToken        = 24;
  public static final int gtrToken        = 25;
  public static final int commaToken      = 31;
  public static final int openbracToken   = 32;
  public static final int closebracToken  = 34;
  public static final int closeparenToken = 35;
  public static final int becomesToken    = 40;
  public static final int thenToken       = 41;
  public static final int doToken         = 42;
  public static final int periodToken     = 46;
  public static final int openparenToken  = 50;
  public static final int number          = 60;
  public static final int ident           = 61;
  public static final int semiToken       = 70;
  public static final int endToken        = 80;
  public static final int odToken         = 81;
  public static final int fiToken         = 82;
  public static final int elseToken       = 90;
  public static final int elseifToken     = 95;
  public static final int letToken        = 100;
  public static final int callToken       = 101;
  public static final int ifToken         = 102;
  public static final int whileToken      = 103;
  public static final int returnToken     = 104;
  public static final int varToken        = 110;
  public static final int arrToken        = 111;
  public static final int funcToken       = 112;
  public static final int procToken       = 113;
  public static final int beginToken      = 150;
  public static final int mainToken      = 200;
  public static final int eofToken        = 255;

  /**
   * *** DO NOT MODIFY ***
   * The symbols enumerated in a String array. This is to allow easy
   * lookup of the value of a given symbol: reference to symbols[token]
   * will return the string representation of the token.
   */
  public static String[] symbols = {
    "ERROR", "TIMES", "DIV", "", "", "", "", "", "", "",        // 0
    "", "PLUS", "MINUS", "", "", "", "", "", "", "",            // 10
    "==", "!=", "<", ">=", "<=", ">", "", "", "", "",           // 20
    "", ",", "OPENBRAC", "", "CLOSEBRAC", ")", "", "", "", "",  // 30
    "<-", "THEN", "DO", "", "", "", ".", "", "", "",            // 40
    "(", "", "", "", "", "", "", "", "", "",                    // 50
    "NUMBER", "IDENT", "", "", "", "", "", "", "", "",          // 60
    ";", "", "", "", "", "", "", "", "", "",                    // 70
    "}", "OD", "FI", "", "", "", "", "", "", "",                // 80
    "ELSE", "", "", "", "", "ELSEIF", "", "", "", "",           // 90
    "LET", "CALL", "IF", "WHILE", "RETURN", "", "", "", "", "", // 100
    "VAR", "ARRAY", "FUNCTION", "PROCEDURE", "", "", "", "", "", "", // 110
    "", "", "", "", "", "", "", "", "", "",                     // 120
    "", "", "", "", "", "", "", "", "", "",                     // 130
    "", "", "", "", "", "", "", "", "", "",                     // 140
    "{", "", "", "", "", "", "", "", "", "",                    // 150
    "", "", "", "", "", "", "", "", "", "",                     // 160
    "", "", "", "", "", "", "", "", "", "",                     // 170
    "", "", "", "", "", "", "", "", "", "",                     // 180
    "", "", "", "", "", "", "", "", "", "",                     // 190
    "MAIN", "", "", "", "", "", "", "", "", "",                 // 200
    "", "", "", "", "", "", "", "", "", "",                     // 210
    "", "", "", "", "", "", "", "", "", "",                     // 220
    "", "", "", "", "", "", "", "", "", "",                     // 230
    "", "", "", "", "", "", "", "", "", "",                     // 240
    "", "", "", "", "", "EOF", "", "", "", "",                  // 250
  };
}

