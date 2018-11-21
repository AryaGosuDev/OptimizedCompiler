/*
 * Example.cs
 * ICS 142 - Winter 2005
 * A example file for assignment 4, .NET CIL output version
 *
 * Example code for program (the output is: "1011"):
 * 
 * --simpl var a, b[30], c, d, e, f[4][10], e[5];
 * function Pow2[num];
 * {
 *   return num * num;
 * }
 *
 * {
 *   let a <- Pow2(3); 
 *   let b[3] <- 10;
 *   Write(b[a-6]);
 *   f[2][a] <- 11;
 *   Write(f[2][9]);
 *   WriteLn;
 * } 
 */
using System;
using System.IO;


public class Driver {
  private static bool debugOn = false;
  
  public static void Main (string[] args) {    
    Wrapper w = new Wrapper();

    string[] proc = new string[4];
    int idx = 0;    
    proc[idx++] = "ldarg 0";
    proc[idx++] = "dup";
    proc[idx++] = "mul";
    proc[idx++] = "ret";
    w.insertProcedure("Pow2",true,1,proc,0,null,4);


    int[] oneDimArray = new int[3]; // 3 array variables
    oneDimArray[0] = 30; // for b, unidimensional array of 30 elements
    oneDimArray[1] = 40; // for f, two dimensional array of 4*10 elements
    oneDimArray[2] = 5;  // for e, unidimensional array of 5 elements
    string[] code = new string[32];
    idx = 0;
    code[idx++] = "ldc.i4 3";
    code[idx++] = w.getProcedureCall("Pow2",true,1);
    code[idx++] = "stloc 0";
    code[idx++] = "ldloc 4"; // <- array variables are placed after the scalars
    code[idx++] = "ldc.i4 3";
    code[idx++] = "ldc.i4 10";
    code[idx++] = "stelem.i4";
    code[idx++] = "ldloc 4";
    code[idx++] = "ldloc 0";
    code[idx++] = "ldc.i4 6";
    code[idx++] = "sub";
    code[idx++] = "ldelem.i4";
    code[idx++] = w.getProcedureCall("Write",false,1);
    code[idx++] = "ldloc 5";
    code[idx++] = "ldc.i4 2";
    code[idx++] = "ldc.i4 10";
    code[idx++] = "mul";
    code[idx++] = "ldloc 0";
    code[idx++] = "add";
    code[idx++] = "ldc.i4 11";
    code[idx++] = "stelem.i4";
    code[idx++] = "ldloc 5";
    code[idx++] = "ldc.i4 2";
    code[idx++] = "ldc.i4 10";
    code[idx++] = "mul";
    code[idx++] = "ldc.i4 9";
    code[idx++] = "add";
    code[idx++] = "ldelem.i4";
    code[idx++] = w.getProcedureCall("Write",false,1);
    code[idx++] = w.getProcedureCall("WriteLn",false,0);
    code[idx++] = "ret";

    w.insertMain(code,4,oneDimArray,16);
    w.WriteFile(args[0] + ".asm");
  }
  
  private static void output (String s) {
    Console.WriteLine(s);
  }
}
