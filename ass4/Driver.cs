/*
 * Driver.cs
 * ICS 142 - Winter 2005
 * A driver file for running the assignment 4 compiler files
 *
 */
using System;
using System.IO;

public class Driver {
  private static bool debugOn = false;
  
  public static void Main (string[] args) {    
    if (args.Length != 1) { 
      output("Usage: mono Driver.exe <test number>");
      Environment.Exit(2);
    }else{
      Parser p = new Parser(args[0]);
      p.computation();
    }
  }
  
  private static void output (String s) {
    Console.WriteLine(s);
  }
}
