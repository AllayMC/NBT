/* Generated by: CongoCC Parser Generator. NonTerminalCall.java */
package org.allaymc.snbt;

import java.io.PrintStream;
import java.util.Set;


public class NonTerminalCall {
    final TokenSource lexer;
    final String sourceFile;
    public final String productionName;
    final String parserClassName;
    final int line, column;
    final Set<? extends Node.NodeType> followSet;

    public NonTerminalCall(String parserClassName, TokenSource lexer, String sourceFile, String productionName, int line, int column, Set<? extends Node.NodeType> followSet) {
        this.parserClassName = parserClassName;
        this.lexer = lexer;
        this.sourceFile = sourceFile;
        this.productionName = productionName;
        this.line = line;
        this.column = column;
        this.followSet = followSet;
    }

    final TokenSource getTokenSource() {
        return lexer;
    }

    StackTraceElement createStackTraceElement() {
        return new StackTraceElement("SNBTParserImplement", productionName, sourceFile, line);
    }

    public void dump(PrintStream ps) {
        ps.println(productionName + ":" + line + ":" + column);
    }

}


