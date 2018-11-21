using System;
using System.IO;
using System.Collections;

// encapsulates streams of tokens
public class Scanner {
  public int sym; // the current token on the input,
                  // 0=error token, 255=end-of-file token
  public int val; // value of the last number encountered
  public int id;  // id of the last identifier encountered

  public  FileReader fr;
  private Hashtable keywords; // keywords -> values
  private int nextid;         // next id number to use
  public  Hashtable idents;   // ident -> id
  private Hashtable ids;      // id -> ident
  
  // advance to the next token
  public void Next () { 
    while ((fr.sym != 0x00) && 
           (fr.sym != 0xff) && 
           (Char.IsWhiteSpace((char) fr.sym))) {
     fr.Next(); 
    }
    if (fr.sym == FileReader.EOF) {
      sym = eofToken;
    } else if (fr.sym == FileReader.ERROR) {
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
    } else if (Char.IsDigit((char) fr.sym)) {
      string n = "";
	  //Lei fr to FileReader
      while (fr.sym != FileReader.EOF && fr.sym != FileReader.ERROR 
             && Char.IsDigit((char) fr.sym)) {
        n += ((char) fr.sym); fr.Next(); 
      }
      val = Int32.Parse(n);
      sym = number;
    } else if (Char.IsLetter((char) fr.sym)) {
      string s = "";
	  //Lei fr to FileReader
	  while (fr.sym != FileReader.EOF && fr.sym != FileReader.ERROR 
             && Char.IsLetterOrDigit((char) fr.sym)) {
        s += ((char) fr.sym); fr.Next(); 
      }
      if (keywords.ContainsKey(s)) { 
        sym = ((int) keywords[s]);
      } else {
        sym = ident;
        if (idents.ContainsKey(s)) { 
          id = ((int) idents[s]);
        } else {
          id = nextid++;
          idents.Add(s, id);
          ids.Add(id, s);
        }
      }
    } else {
      Console.WriteLine("Scanner.Next() encountered unknown character"); 
      Environment.Exit(0);
    }
  }
  
  // signal an error message (calls FileReader.Error in turn)
  public void Error (string errorMsg) { 
    fr.Error(errorMsg);
  }

  // constructor: open file and scan the first token into 'sym'
  public Scanner (string fileName) { 
    fr = new FileReader(fileName);

    idents = new Hashtable();
    ids = new Hashtable();
    keywords = new Hashtable();

    keywords.Add("then", thenToken);
    keywords.Add("do", doToken);
    keywords.Add("od", odToken);
    keywords.Add("fi", fiToken);
    keywords.Add("else", elseToken);
    keywords.Add("let", letToken);
    keywords.Add("call", callToken);
    keywords.Add("if", ifToken);
    keywords.Add("while", whileToken);
    keywords.Add("return", returnToken);
    keywords.Add("var", varToken);
    keywords.Add("function", funcToken);
    keywords.Add("procedure", procToken);
    keywords.Add("simpl", simplToken);

    Next();
  }

  /* identifier table methods */

  public string Id2String (int id) {
    if (ids.ContainsKey(id)) {
      return (string) ids[id]; //// CHECK
    } else {
      return "***NOT FOUND, id=" + id + "***";
    }
  }

//  public static void Main(string[] Args){
//    Scanner s = new Scanner(Args[0]);
//    while (s.sym != Scanner.eofToken){
//      Console.WriteLine(Scanner.symbols[s.sym]);
//      s.Next();	
//    }
//  }

  public int String2Id (string name) {
    if (idents.ContainsKey(name)) { 
      return ((int) idents[name]); //// CHECK 
    } else {
      return -1;
    }
  }

  /* 
   * *** DO NOT MODIFY ***
   * Token values 
   */
  public const int errorToken      = 0;
  public const int timesToken      = 1;
  public const int divToken        = 2;
  public const int plusToken       = 11;
  public const int minusToken      = 12;
  public const int eqlToken        = 20;
  public const int neqToken        = 21;
  public const int lssToken        = 22;
  public const int geqToken        = 23;
  public const int leqToken        = 24;
  public const int gtrToken        = 25;
  public const int periodToken     = 30;
  public const int commaToken      = 31;
  public const int closeparenToken = 35;
  public const int becomesToken    = 40;
  public const int thenToken       = 41;
  public const int doToken         = 42;
  public const int openparenToken  = 50;
  public const int number          = 60;
  public const int ident           = 61;
  public const int semiToken       = 70;
  public const int endToken        = 80;
  public const int odToken         = 81;
  public const int fiToken         = 82;
  public const int elseToken       = 90;
  public const int letToken        = 100;
  public const int callToken       = 101;
  public const int ifToken         = 102;
  public const int whileToken      = 103;
  public const int returnToken     = 104;
  public const int varToken        = 110;
  public const int funcToken       = 111;
  public const int procToken       = 112;
  public const int beginToken      = 150;
  public const int simplToken      = 200;
  public const int eofToken        = 255;

  /**
   * *** DO NOT MODIFY ***
   * The symbols enumerated in a String array. This is to allow easy
   * lookup of the value of a given symbol: reference to symbols[token]
   * will return the string representation of the token.
   */
  public static string[] symbols = {
    "ERROR", "TIMES", "DIV", "", "", "", "", "", "", "",        // 0
    "", "PLUS", "MINUS", "", "", "", "", "", "", "",            // 10
    "==", "!=", "<", ">=", "<=", ">", "", "", "", "",           // 20
    ".", ",", "", "", "", ")", "", "", "", "",                  // 30
    "<-", "THEN", "DO", "", "", "", "", "", "", "",             // 40
    "(", "", "", "", "", "", "", "", "", "",                    // 50
    "NUMBER", "IDENT", "", "", "", "", "", "", "", "",          // 60
    ";", "", "", "", "", "", "", "", "", "",                    // 70
    "}", "OD", "FI", "", "", "", "", "", "", "",                // 80
    "ELSE", "", "", "", "", "", "", "", "", "",                 // 90
    "LET", "CALL", "IF", "WHILE", "RETURN", "", "", "", "", "", // 100
    "VAR", "FUNCTION", "PROCEDURE", "", "", "", "", "", "", "", // 110
    "", "", "", "", "", "", "", "", "", "",                     // 120
    "", "", "", "", "", "", "", "", "", "",                     // 130
    "", "", "", "", "", "", "", "", "", "",                     // 140
    "{", "", "", "", "", "", "", "", "", "",                    // 150
    "", "", "", "", "", "", "", "", "", "",                     // 160
    "", "", "", "", "", "", "", "", "", "",                     // 170
    "", "", "", "", "", "", "", "", "", "",                     // 180
    "", "", "", "", "", "", "", "", "", "",                     // 190
    "SIMPL", "", "", "", "", "", "", "", "", "",                // 200
    "", "", "", "", "", "", "", "", "", "",                     // 210
    "", "", "", "", "", "", "", "", "", "",                     // 220
    "", "", "", "", "", "", "", "", "", "",                     // 230
    "", "", "", "", "", "", "", "", "", "",                     // 240
    "", "", "", "", "", "EOF", "", "", "", "",                  // 250
  };
}
