/*
 * Copyright 2010, Center for Advanced Information Systems, Nanyang Technological University
 * 
 * File name: DotGenerator.java
 * 
 * Abstract: Create the DOT format file for the selected result graph
 * 
 * Current Version: 0.1 Author: Jin Changjiu Modified Date: Dec.08, 2009
 */
package prague.result;

/**
 *
 * @author cjjin
 */
import static com.google.common.base.Preconditions.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.BitSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import gblend.db.DatabaseInfo;
import java.util.ArrayList;
import java.util.List;
import prague.graph.Graph;
import prague.graph.ImmutableGraph;
import prague.graph.Vertex;
import prague.graph.lg.LGVertex;
import prague.graph.lg.LinkGraph;

public class DotGenerator {

    private static String getRealLabels(int l, int i) {
        String[] realLabels = DatabaseInfo.getLabels();
        String label = realLabels[l] + "_" + i;
        return label;
    }

    private static String getRealLabels(int l) {
        String[] realLabels = DatabaseInfo.getLabels();
        String label = realLabels[l];
        return label;
    }
 @SuppressWarnings("SpellCheckingInspection")
    private static void printEdges2(PrintStream p, Graph<? extends Vertex> graph,
            String[] realLabels, @Nullable MatchedChecker checker, List<Integer> missEdges,List<String> theMissedEdgeLabel) {

        int n = graph.getNodeNum();
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (graph.getEdge(i, j) == 1) {
                    printEdge(p, realLabels, checker, i, j);
                }
            }
        }
        if (checker != null) {
            BitSet unUsedEdges = checker.getUnUsedEdges();
            if (!unUsedEdges.isEmpty()) {
                p.println(
                        "{ Legend [shape=none, margin=0, label=<\n"
                        + "  <TABLE BORDER=\"0\" CELLBORDER=\"1\" CELLSPACING=\"0\" CELLPADDING=\"4\">\n"
                        + "   <TR>\n"
                        + "    <TD COLSPAN=\"2\"><B>Similar</B></TD>\n"
                        + "   </TR>\n"
                        + "   <TR>\n"
                        + "    <TD>Number of Missed Edges:</TD>\n"
                        + "    <TD><FONT COLOR=\"red\">" + unUsedEdges.cardinality() + "</FONT></TD>\n"
                        + "   </TR>\n"
                        + "   <TR>\n"
                        + "    <TD>Missed Edges:</TD>\n"
                        + "    <TD><FONT COLOR=\"red\">" + unUsedEdges + "</FONT></TD>\n"
                        + "   </TR>\n"
                        + "  </TABLE>\n"
                        + " >];\n"
                        + "}\n"
                );
                if (missEdges != null) {
                    missEdges.add(unUsedEdges.cardinality());
                    theMissedEdgeLabel.add(unUsedEdges.toString());
                }
            }
        }
    }
 
    @SuppressWarnings("SpellCheckingInspection")
    private static void printEdges(PrintStream p, Graph<? extends Vertex> graph,
            String[] realLabels, @Nullable MatchedChecker checker, List<Integer> missEdges) {

        int n = graph.getNodeNum();
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (graph.getEdge(i, j) == 1) {
                    printEdge(p, realLabels, checker, i, j);
                }
            }
        }
        if (checker != null) {
            BitSet unUsedEdges = checker.getUnUsedEdges();
            if (!unUsedEdges.isEmpty()) {
                p.println(
                        "{ Legend [shape=none, margin=0, label=<\n"
                        + "  <TABLE BORDER=\"0\" CELLBORDER=\"1\" CELLSPACING=\"0\" CELLPADDING=\"4\">\n"
                        + "   <TR>\n"
                        + "    <TD COLSPAN=\"2\"><B>Similar</B></TD>\n"
                        + "   </TR>\n"
                        + "   <TR>\n"
                        + "    <TD>Number of Missed Edges:</TD>\n"
                        + "    <TD><FONT COLOR=\"red\">" + unUsedEdges.cardinality() + "</FONT></TD>\n"
                        + "   </TR>\n"
                        + "   <TR>\n"
                        + "    <TD>Missed Edges:</TD>\n"
                        + "    <TD><FONT COLOR=\"red\">" + unUsedEdges + "</FONT></TD>\n"
                        + "   </TR>\n"
                        + "  </TABLE>\n"
                        + " >];\n"
                        + "}\n"
                );
                if (missEdges != null) {
                    missEdges.add(unUsedEdges.cardinality());
                }
            }
        }
    }
 
    @SuppressWarnings("SpellCheckingInspection")
    private static void printEdge(PrintStream p, String[] realLabels,
            @Nullable MatchedChecker checker, int i, int j) {
        p.print(realLabels[i]);
        p.print(" -- ");
        p.print(realLabels[j]);
        if (checker != null) {
            int el = checker.checkEdge(i, j);
            if (el > 0) {
                p.print(String.format(" [label=\"%d\", ", el));
                p.print("fontcolor=navy, color=red, style=bold, penwidth=5]");
            }
        }
        p.println(";");
    }
 @SuppressWarnings("SpellCheckingInspection")
    public static File formatMatchedGraph(@Nonnull Graph<? extends Vertex> graph,
            @Nullable MatchedChecker checker, List<Integer> missEdges) {
        checkNotNull(graph);
        String output = "graph.dot";
        File fOut = new File(output);

        try {
            // Connect print stream to the output stream
            try (PrintStream p = new PrintStream(new FileOutputStream(fOut))) {

                p.println("graph \"result\" {");
                p.println("graph [ fontname=\"Helvetica-Oblique\", fontsize=20,");
                int n = graph.getNodeNum();
                p.printf("label=\"\\n\\nGraph %d (%d,%d)\", ", graph.getGraphId(), n, graph.getEdgeNum());
                p.println("size=\"4,4\" ];");
                p.println("node [ label=\"\\N\", shape=box, sides=4, color=cadetblue1,");
                p.println("style=filled, fontname=\"Helvetica-Outline\" ];");

                String[] realLabels = new String[n];
                for (int i = 0; i < n; i++) {
                    realLabels[i] = getRealLabels(graph.getNode(i).getLabel(), i);
                    p.print(realLabels[i]);
                    if (checker != null && checker.checkNode(i)) {
                        p.print(" [color=orange]");
                    }
                    p.println(";");
                }

                printEdges(p, graph, realLabels, checker, missEdges);

                p.println("}");
            }
        } catch (IOException e) {
            throw new RuntimeException("formatDotFile error", e);
        }

        return fOut;
    }
   
    
    // formulate the dot file
    @SuppressWarnings("SpellCheckingInspection")
    public static File formatMatchedGraph2(@Nonnull Graph<? extends Vertex> graph,
            @Nullable MatchedChecker checker, List<Integer> missEdges, List<String> theMissedEdgeLabel) {
        checkNotNull(graph);
        String output = "graph.dot";
        File fOut = new File(output);

        try {
            // Connect print stream to the output stream
            try (PrintStream p = new PrintStream(new FileOutputStream(fOut))) {

                p.println("graph \"result\" {");
                p.println("graph [ fontname=\"Helvetica-Oblique\", fontsize=20,");
                int n = graph.getNodeNum();
                p.printf("label=\"\\n\\nGraph %d (%d,%d)\", ", graph.getGraphId(), n, graph.getEdgeNum());
                p.println("size=\"4,4\" ];");
                p.println("node [ label=\"\\N\", shape=box, sides=4, color=cadetblue1,");
                p.println("style=filled, fontname=\"Helvetica-Outline\" ];");

                String[] realLabels = new String[n];
                for (int i = 0; i < n; i++) {
                    realLabels[i] = getRealLabels(graph.getNode(i).getLabel(), i);
                    p.print(realLabels[i]);
                    if (checker != null && checker.checkNode(i)) {
                        p.print(" [color=orange]");
                    }
                    p.println(";");
                }

                printEdges2(p, graph, realLabels, checker, missEdges,theMissedEdgeLabel);

                p.println("}");
            }
        } catch (IOException e) {
            throw new RuntimeException("formatDotFile error", e);
        }

        return fOut;
    }
   
    // formulate the dot file

    @SuppressWarnings("SpellCheckingInspection")
    public static File formatMatchedGraphWithName(@Nonnull Graph<? extends Vertex> graph,
            @Nullable MatchedChecker checker, List<Integer> missEdges, String pathname) {
        checkNotNull(graph);
        String output = pathname;
        File fOut = new File(output);

        try {
            // Connect print stream to the output stream
            try (PrintStream p = new PrintStream(new FileOutputStream(fOut))) {

                p.println("graph \"result\" {");
                p.println("graph [ fontname=\"Helvetica-Oblique\", fontsize=20,");
                int n = graph.getNodeNum();
                p.printf("label=\"\\n\\nGraph %d (%d,%d)\", ", graph.getGraphId(), n, graph.getEdgeNum());
                p.println("size=\"4,4\" ];");
                p.println("node [ label=\"\\N\", shape=box, sides=4, color=cadetblue1,");
                p.println("style=filled, fontname=\"Helvetica-Outline\" ];");

                String[] realLabels = new String[n];
                for (int i = 0; i < n; i++) {
                    realLabels[i] = getRealLabels(graph.getNode(i).getLabel(), i);
                    p.print(realLabels[i]);
                    if (checker != null && checker.checkNode(i)) {
                        p.print(" [color=orange]");
                    }
                    p.println(";");
                }

                printEdges(p, graph, realLabels, checker, missEdges);

                p.println("}");
            }
        } catch (IOException e) {
            throw new RuntimeException("formatDotFile error", e);
        }

        return fOut;
    }

    @SuppressWarnings("SpellCheckingInspection")
    public static File formatPattern(@Nonnull LinkGraph<LGVertex> graph, int patternId) {
        checkNotNull(graph);
        String output = "temp_pattern.dot";
        File fOut = new File(output);

        try {
            try (PrintStream p = new PrintStream(new FileOutputStream(fOut))) {
                p.println("graph \"result\" {");
                p.println("graph [fontname=\"Helvetica-Oblique\", fontsize=20, dpi=100,");
                p.println("ratio=\"compress\",");
                int n = graph.getNodeNum();
                p.printf("label=\"Pattern %d (%d,%d)\", ", patternId, n, graph.getEdgeNum());
                p.printf("size=\"3,%f\" ];\n", 3 + 0.4 * n);
                p.println("node [shape=box, sides=4, color=cadetblue1, "
                        + "style=filled, fontname=\"Helvetica-Outline\" ];");

                String[] realLabels = new String[n];
                for (int i = 0; i < n; i++) {
                    String displayLabel = getRealLabels(graph.getNode(i).getLabel());
                    realLabels[i] = String.valueOf(i);
                    p.print(realLabels[i]);
                    p.printf("[ label=\"%s\" ];\n", displayLabel);
                }

                printEdges(p, graph, realLabels, null, null);

                p.println("}");
                p.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("formatPattern error", e);
        }

        return fOut;
    }

}
