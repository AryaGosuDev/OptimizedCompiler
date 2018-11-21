
import java.io.*;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.String;

public class FileReaderFile { 
	  public byte sym; // the current character on the input, 0x00=error, 0xff=EOF

	  public static int EOF = 0xff;
	  public static int ERROR = 0x00;
	  
	  
	  private FileReader fr;

	  //private StreamReader sr;
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
	      i = fr.read();
	    } catch (Exception e) {
	      System.out.println("Exception in FileReader.Next while reading stream");
	      System.exit(1)   ; 
	    }

	    if (i == -1) {
	      sym = (byte)EOF;
	    } else {
	      sym = (byte) i;
	    }
	  }

	  // signal an error with current file position
	  public void Error (String errorMsg) {
		System.out.println("ERROR: line" + row +  "at position" + col);
		System.out.println("MSG:" + errorMsg);
		System.exit(1)   ;     
	  }

	  // constructor: open file and read the first character into 'sym'
	  public FileReaderFile(String fileName) {
	    try{
	    	fr =  new FileReader(fileName);
	      
	    } catch (Exception e) {
	    	System.out.println("File " + fileName + " not found.");
	        System.exit(1);     
	    }
	    
	    row = col = 1;
	    int i = 0;
	    try {
	      i = fr.read();
	    } catch (Exception e) {
	    	System.out.println("Exception in FileReader constr. while reading stream");
	      System.exit(1);     
	    }

	    if (i == -1) {
	      sym = (byte)EOF;
	    } else {
	      sym = (byte) i;
	    }
	  }
	}
