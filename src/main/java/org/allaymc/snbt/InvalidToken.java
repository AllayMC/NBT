/* Generated by: CongoCC Parser Generator. InvalidToken.java  */
package org.allaymc.snbt;


/**
* Token subclass to represent lexically invalid input
*/
public class InvalidToken extends Token {

    public InvalidToken(SNBTLexer tokenSource, int beginOffset, int endOffset) {
        super(TokenType.INVALID, tokenSource, beginOffset, endOffset);
    }

    public String getNormalizedText() {
        return "Lexically Invalid Input:" + getImage();
    }

}


