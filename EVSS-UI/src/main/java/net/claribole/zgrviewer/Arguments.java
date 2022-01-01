package net.claribole.zgrviewer;

import java.util.Scanner;

public class Arguments {
      public String inFilePath  =  "AIDS40k"; 
      String outFilePath =  "TEDPatternsOnAIDS40k.txt";
      public long minSup        =  1;
      String  swapcondition  = "swap1";  //"swap1", "swap2", "swapalpha"
      Double  swapAlpha    = 0.99;
      public long minNodeNum = 2;
      public long maxNodeNum = 11;

      String strategy  = "topk";
      //String strategy  = "greedy";
      public Integer numberofpatterns = 5;
      public Integer numberofgraphs   = 40000;
      
      Boolean hasPRM = true;
      Boolean isPESIndex  = true;
      Boolean isSimpleIndex = !isPESIndex;
      
      
      Boolean hasDSS = true;
      Boolean hasInitialPatternGenerator  = true;
      
     // double maintainTime = 0;
      
      Boolean isLightVersion = false;
      Integer ReadNumInEachBatch = 100000;
      Double  AvgE = 43.783167;         ///For Pubchem
      Double  SampleEdgeNum = 500000.0; /// For Pubchem
      

      Boolean isSimplified = false;
      
}
