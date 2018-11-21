using System;
using System.IO;

// encapsulates streams of characters
public class FileReader { 
  public byte sym; // the current character on the input, 0x00=error, 0xff=EOF

  public const int EOF = 0xff;
  public const int ERROR = 0x00;

  private StreamReader sr;
  public int row, col;

  // advance to the next character
  public void Next() {
    if (sym == ERROR || sym == EOF) {
      return;
    }

    if (sym == '\n'){
       row += 1;
       col = 1;
    } else {
       col += 1;
    }
 
    int i = 0;
    try {
      i = sr.Read();
    } catch (Exception e) {
      Console.WriteLine("Exception in FileReader.Next while reading stream");
      Environment.Exit(0);     
    }

    if (i == -1) {
      sym = EOF;
    } else {
      sym = (byte) i;
    }
  }

  // signal an error with current file position
  public void Error (string errorMsg) {
    Console.WriteLine("ERROR: line {0} at position {1}",row,col);
    Console.WriteLine("MSG: {0}",errorMsg);
    Environment.Exit(0);     
  }

  // constructor: open file and read the first character into 'sym'
  public FileReader(string fileName) {
    try{
      sr = new StreamReader(fileName);
    } catch (Exception e) {
      Console.WriteLine("File {0} not found.",fileName);
      Environment.Exit(0);     
    }

    row = col = 1;
    int i = 0;
    try {
      i = sr.Read();
    } catch (Exception e) {
      Console.WriteLine("Exception in FileReader constr. while reading stream");
      Environment.Exit(0);     
    }

    if (i == -1) {
      sym = EOF;
    } else {
      sym = (byte) i;
    }
  }
}

