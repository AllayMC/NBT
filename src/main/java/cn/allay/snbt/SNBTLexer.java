/* Generated by: CongoCC Parser Generator. SNBTLexer.java  */
package cn.allay.snbt;

import cn.allay.snbt.Token.TokenType;

import java.util.BitSet;
import java.util.EnumSet;

import static cn.allay.snbt.Token.TokenType.*;


public class SNBTLexer extends TokenSource {

    public enum LexicalState {
        SNBT
    }

    LexicalState lexicalState = LexicalState.values()[0];
    EnumSet<TokenType> activeTokenTypes = EnumSet.allOf(TokenType.class);
    {
    }
    // Token types that are "regular" tokens that participate in parsing,
    // i.e. declared as TOKEN
    static final EnumSet<TokenType> regularTokens = EnumSet.of(EOF, COLON, COMMA, OPEN_BRACKET, CLOSE_BRACKET, OPEN_BRACE, CLOSE_BRACE, SEMICOLON, B, I, L, FLOAT_TYPE, DOUBLE_TYPE, LONG_TYPE, BYTE_TYPE, SHORT_TYPE, BOOLEAN, FLOAT, DOUBLE, INTEGER, BYTE, LONG, SHORT, STRING);
    // Token types that do not participate in parsing
    // i.e. declared as UNPARSED (or SPECIAL_TOKEN)
    private static final EnumSet<TokenType> unparsedTokens = EnumSet.noneOf(TokenType.class);
    // Tokens that are skipped, i.e. SKIP
    static final EnumSet<TokenType> skippedTokens = EnumSet.of(WHITESPACE);
    // Tokens that correspond to a MORE, i.e. that are pending
    // additional input
    private static final EnumSet<TokenType> moreTokens = EnumSet.noneOf(TokenType.class);

    public SNBTLexer(CharSequence input) {
        this("input", input);
    }

    /**
    * @param inputSource just the name of the input source (typically the filename)
    * that will be used in error messages and so on.
    * @param input the input
    */
    public SNBTLexer(String inputSource, CharSequence input) {
        this(inputSource, input, LexicalState.SNBT, 1, 1);
    }

    /**
    * @param inputSource just the name of the input source (typically the filename) that
    * will be used in error messages and so on.
    * @param input the input
    * @param lexicalState The starting lexical state, may be null to indicate the default
    * starting state
    * @param line The line number at which we are starting for the purposes of location/error messages. In most
    * normal usage, this is 1.
    * @param column number at which we are starting for the purposes of location/error messages. In most normal
    * usages this is 1.
    */
    public SNBTLexer(String inputSource, CharSequence input, LexicalState lexState, int startingLine, int startingColumn) {
        super(inputSource, input, startingLine, startingColumn, 1, true, false, false, "");
        if (lexicalState != null) switchTo(lexState);
    }

    /**
    * The public method for getting the next token.
    * It checks whether we have already cached
    * the token after this one. If not, it finally goes
    * to the NFA machinery
    */
    public Token getNextToken(Token tok) {
        if (tok == null) {
            tok = tokenizeAt(0);
            cacheToken(tok);
            return tok;
        }
        Token cachedToken = tok.nextCachedToken();
        // If the cached next token is not currently active, we
        // throw it away and go back to the XXXLexer
        if (cachedToken != null && activeTokenTypes != null && !activeTokenTypes.contains(cachedToken.getType())) {
            reset(tok);
            cachedToken = null;
        }
        if (cachedToken == null) {
            Token token = tokenizeAt(tok.getEndOffset());
            cacheToken(token);
            return token;
        }
        return cachedToken;
    }


    static class MatchInfo {
        TokenType matchedType;
        int matchLength;

        MatchInfo(TokenType matchedType, int matchLength) {
            this.matchedType = matchedType;
            this.matchLength = matchLength;
        }

    }


    /**
    * Core tokenization method. Note that this can be called from a static context.
    * Hence the extra parameters that need to be passed in.
    */
    static MatchInfo getMatchInfo(CharSequence input, int position, EnumSet<TokenType> activeTokenTypes, NfaFunction[] nfaFunctions) {
        if (position >= input.length()) {
            return new MatchInfo(EOF, 0);
        }
        int start = position;
        int matchLength = 0;
        TokenType matchedType = TokenType.INVALID;
        BitSet currentStates = new BitSet(87);
        BitSet nextStates = new BitSet(87);
        // the core NFA loop
        do {
            // Holder for the new type (if any) matched on this iteration
            if (position > start) {
                // What was nextStates on the last iteration
                // is now the currentStates!
                BitSet temp = currentStates;
                currentStates = nextStates;
                nextStates = temp;
                nextStates.clear();
            } else {
                currentStates.set(0);
            }
            if (position >= input.length()) {
                break;
            }
            int curChar = Character.codePointAt(input, position++);
            if (curChar > 0xFFFF) position++;
            int nextActive = currentStates.nextSetBit(0);
            while (nextActive != -1) {
                TokenType returnedType = nfaFunctions[nextActive].apply(curChar, nextStates, activeTokenTypes);
                if (returnedType != null && (position - start > matchLength || returnedType.ordinal() < matchedType.ordinal())) {
                    matchedType = returnedType;
                    matchLength = position - start;
                }
                nextActive = currentStates.nextSetBit(nextActive + 1);
            }
            if (position >= input.length()) break;
        }
        while (!nextStates.isEmpty());
        return new MatchInfo(matchedType, matchLength);
    }

    /**
    * @param position The position at which to tokenize.
    * @return the Token at position
    */
    final Token tokenizeAt(int position) {
        int tokenBeginOffset = position;
        boolean inMore = false;
        StringBuilder invalidChars = null;
        Token matchedToken = null;
        TokenType matchedType = null;
        // The core tokenization loop
        while (matchedToken == null) {
            if (!inMore) tokenBeginOffset = position;
            MatchInfo matchInfo = getMatchInfo(this, position, activeTokenTypes, nfaFunctions);
            matchedType = matchInfo.matchedType;
            inMore = moreTokens.contains(matchedType);
            position += matchInfo.matchLength;
            if (matchedType == TokenType.INVALID) {
                if (invalidChars == null) {
                    invalidChars = new StringBuilder();
                }
                int cp = Character.codePointAt(this, tokenBeginOffset);
                invalidChars.appendCodePoint(cp);
                ++position;
                if (cp > 0xFFFF) ++position;
                continue;
            }
            if (invalidChars != null) {
                return new InvalidToken(this, tokenBeginOffset - invalidChars.length(), tokenBeginOffset);
            }
            if (skippedTokens.contains(matchedType)) {
                skipTokens(tokenBeginOffset, position);
            } else if (regularTokens.contains(matchedType) || unparsedTokens.contains(matchedType)) {
                matchedToken = Token.newToken(matchedType, this, tokenBeginOffset, position);
                matchedToken.setUnparsed(!regularTokens.contains(matchedType));
            }
        }
        return matchedToken;
    }

    /**
    * Switch to specified lexical state.
    * @param lexState the lexical state to switch to
    * @return whether we switched (i.e. we weren't already in the desired lexical state)
    */
    public boolean switchTo(LexicalState lexState) {
        if (this.lexicalState != lexState) {
            this.lexicalState = lexState;
            return true;
        }
        return false;
    }

    // Reset the token source input
    // to just after the Token passed in.
    void reset(Token t, LexicalState state) {
        uncacheTokens(t);
        if (state != null) {
            switchTo(state);
        }
    }

    void reset(Token t) {
        reset(t, null);
    }

    void cacheToken(Token tok) {
        cacheTokenAt(tok, tok.getBeginOffset());
    }


    // NFA related code follows.
    // The functional interface that represents
    // the acceptance method of an NFA state
    static interface NfaFunction {

        TokenType apply(int ch, BitSet bs, EnumSet<TokenType> validTypes);

    }

    private static NfaFunction[] nfaFunctions;
    // Initialize the various NFA method tables
    static {
        SNBT.NFA_FUNCTIONS_init();
    }

    //The Nitty-gritty of the NFA code follows.
    /**
    * Holder class for NFA code related to SNBT lexical state
    */
    private static class SNBT {

        private static TokenType getNfaNameSNBTIndex0(int ch, BitSet nextStates, EnumSet<TokenType> validTypes) {
            TokenType type = null;
            if (ch == '"') {
                nextStates.set(37);
            } else if (ch == '\'') {
                nextStates.set(18);
            } else if (ch == '-') {
                nextStates.set(7);
                nextStates.set(15);
                nextStates.set(20);
                nextStates.set(27);
                nextStates.set(31);
                nextStates.set(39);
            } else if (ch == '0') {
                nextStates.set(23);
                nextStates.set(21);
                nextStates.set(17);
                nextStates.set(29);
                nextStates.set(8);
            } else if (ch >= '1' && ch <= '9') {
                nextStates.set(1);
                nextStates.set(16);
                nextStates.set(28);
                nextStates.set(22);
                nextStates.set(14);
            } else if (ch == 'f') {
                nextStates.set(33);
            } else if (ch == 't') {
                nextStates.set(24);
            }
            if (ch == '0') {
                if (validTypes == null || validTypes.contains(INTEGER)) type = INTEGER;
            } else if (ch >= '1' && ch <= '9') {
                nextStates.set(30);
                if (validTypes == null || validTypes.contains(INTEGER)) type = INTEGER;
            } else if (ch == 'S') {
                if (validTypes == null || validTypes.contains(SHORT_TYPE)) type = SHORT_TYPE;
            } else if (ch == 's') {
                if (validTypes == null || validTypes.contains(SHORT_TYPE)) type = SHORT_TYPE;
            } else if (ch == 'B') {
                if (validTypes == null || validTypes.contains(BYTE_TYPE)) type = BYTE_TYPE;
            } else if (ch == 'b') {
                if (validTypes == null || validTypes.contains(BYTE_TYPE)) type = BYTE_TYPE;
            } else if (ch == 'L') {
                if (validTypes == null || validTypes.contains(LONG_TYPE)) type = LONG_TYPE;
            } else if (ch == 'l') {
                if (validTypes == null || validTypes.contains(LONG_TYPE)) type = LONG_TYPE;
            } else if (ch == 'D') {
                if (validTypes == null || validTypes.contains(DOUBLE_TYPE)) type = DOUBLE_TYPE;
            } else if (ch == 'd') {
                if (validTypes == null || validTypes.contains(DOUBLE_TYPE)) type = DOUBLE_TYPE;
            } else if (ch == 'F') {
                if (validTypes == null || validTypes.contains(FLOAT_TYPE)) type = FLOAT_TYPE;
            } else if (ch == 'f') {
                if (validTypes == null || validTypes.contains(FLOAT_TYPE)) type = FLOAT_TYPE;
            } else if (ch == '\t') {
                nextStates.set(32);
                if (validTypes == null || validTypes.contains(WHITESPACE)) type = WHITESPACE;
            } else if (ch == '\n') {
                nextStates.set(32);
                if (validTypes == null || validTypes.contains(WHITESPACE)) type = WHITESPACE;
            } else if (ch == '\r') {
                nextStates.set(32);
                if (validTypes == null || validTypes.contains(WHITESPACE)) type = WHITESPACE;
            } else if (ch == ' ') {
                nextStates.set(32);
                if (validTypes == null || validTypes.contains(WHITESPACE)) type = WHITESPACE;
            }
            if (ch == 'L') {
                if (validTypes == null || validTypes.contains(L)) type = L;
            } else if (ch == 'I') {
                if (validTypes == null || validTypes.contains(I)) type = I;
            } else if (ch == 'B') {
                if (validTypes == null || validTypes.contains(B)) type = B;
            } else if (ch == ';') {
                if (validTypes == null || validTypes.contains(SEMICOLON)) type = SEMICOLON;
            } else if (ch == '}') {
                if (validTypes == null || validTypes.contains(CLOSE_BRACE)) type = CLOSE_BRACE;
            } else if (ch == '{') {
                if (validTypes == null || validTypes.contains(OPEN_BRACE)) type = OPEN_BRACE;
            } else if (ch == ']') {
                if (validTypes == null || validTypes.contains(CLOSE_BRACKET)) type = CLOSE_BRACKET;
            } else if (ch == '[') {
                if (validTypes == null || validTypes.contains(OPEN_BRACKET)) type = OPEN_BRACKET;
            } else if (ch == ',') {
                if (validTypes == null || validTypes.contains(COMMA)) type = COMMA;
            } else if (ch == ':') {
                if (validTypes == null || validTypes.contains(COLON)) type = COLON;
            }
            return type;
        }

        private static TokenType getNfaNameSNBTIndex1(int ch, BitSet nextStates, EnumSet<TokenType> validTypes) {
            TokenType type = null;
            if (ch == '.') {
                nextStates.set(2);
            } else if (ch >= '0' && ch <= '9') {
                nextStates.set(1);
            } else if ((ch == 'E') || (ch == 'e')) {
                nextStates.set(4);
            } else if (ch == 'D') {
                if (validTypes == null || validTypes.contains(DOUBLE)) type = DOUBLE;
            } else if (ch == 'd') {
                if (validTypes == null || validTypes.contains(DOUBLE)) type = DOUBLE;
            }
            return type;
        }

        private static TokenType getNfaNameSNBTIndex2(int ch, BitSet nextStates, EnumSet<TokenType> validTypes) {
            TokenType type = null;
            if (ch >= '0' && ch <= '9') {
                nextStates.set(3);
            }
            return type;
        }

        private static TokenType getNfaNameSNBTIndex3(int ch, BitSet nextStates, EnumSet<TokenType> validTypes) {
            TokenType type = null;
            if (ch >= '0' && ch <= '9') {
                nextStates.set(3);
            } else if ((ch == 'E') || (ch == 'e')) {
                nextStates.set(4);
            } else if (ch == 'D') {
                if (validTypes == null || validTypes.contains(DOUBLE)) type = DOUBLE;
            } else if (ch == 'd') {
                if (validTypes == null || validTypes.contains(DOUBLE)) type = DOUBLE;
            }
            return type;
        }

        private static TokenType getNfaNameSNBTIndex4(int ch, BitSet nextStates, EnumSet<TokenType> validTypes) {
            TokenType type = null;
            if ((ch == '+') || (ch == '-')) {
                nextStates.set(5);
            } else if (ch >= '1' && ch <= '9') {
                nextStates.set(6);
            }
            return type;
        }

        private static TokenType getNfaNameSNBTIndex5(int ch, BitSet nextStates, EnumSet<TokenType> validTypes) {
            TokenType type = null;
            if (ch >= '1' && ch <= '9') {
                nextStates.set(6);
            }
            return type;
        }

        private static TokenType getNfaNameSNBTIndex6(int ch, BitSet nextStates, EnumSet<TokenType> validTypes) {
            TokenType type = null;
            if (ch >= '1' && ch <= '9') {
                nextStates.set(6);
            } else if (ch == 'D') {
                if (validTypes == null || validTypes.contains(DOUBLE)) type = DOUBLE;
            } else if (ch == 'd') {
                if (validTypes == null || validTypes.contains(DOUBLE)) type = DOUBLE;
            }
            return type;
        }

        private static TokenType getNfaNameSNBTIndex7(int ch, BitSet nextStates, EnumSet<TokenType> validTypes) {
            TokenType type = null;
            if (ch == '0') {
                nextStates.set(8);
            } else if (ch >= '1' && ch <= '9') {
                nextStates.set(14);
            }
            return type;
        }

        private static TokenType getNfaNameSNBTIndex8(int ch, BitSet nextStates, EnumSet<TokenType> validTypes) {
            TokenType type = null;
            if (ch == '.') {
                nextStates.set(12);
            } else if ((ch == 'E') || (ch == 'e')) {
                nextStates.set(9);
            } else if (ch == 'F') {
                if (validTypes == null || validTypes.contains(FLOAT)) type = FLOAT;
            } else if (ch == 'f') {
                if (validTypes == null || validTypes.contains(FLOAT)) type = FLOAT;
            }
            return type;
        }

        private static TokenType getNfaNameSNBTIndex9(int ch, BitSet nextStates, EnumSet<TokenType> validTypes) {
            TokenType type = null;
            if ((ch == '+') || (ch == '-')) {
                nextStates.set(11);
            } else if (ch >= '1' && ch <= '9') {
                nextStates.set(10);
            }
            return type;
        }

        private static TokenType getNfaNameSNBTIndex10(int ch, BitSet nextStates, EnumSet<TokenType> validTypes) {
            TokenType type = null;
            if (ch >= '1' && ch <= '9') {
                nextStates.set(10);
            } else if (ch == 'F') {
                if (validTypes == null || validTypes.contains(FLOAT)) type = FLOAT;
            } else if (ch == 'f') {
                if (validTypes == null || validTypes.contains(FLOAT)) type = FLOAT;
            }
            return type;
        }

        private static TokenType getNfaNameSNBTIndex11(int ch, BitSet nextStates, EnumSet<TokenType> validTypes) {
            TokenType type = null;
            if (ch >= '1' && ch <= '9') {
                nextStates.set(10);
            }
            return type;
        }

        private static TokenType getNfaNameSNBTIndex12(int ch, BitSet nextStates, EnumSet<TokenType> validTypes) {
            TokenType type = null;
            if (ch >= '0' && ch <= '9') {
                nextStates.set(13);
            }
            return type;
        }

        private static TokenType getNfaNameSNBTIndex13(int ch, BitSet nextStates, EnumSet<TokenType> validTypes) {
            TokenType type = null;
            if (ch >= '0' && ch <= '9') {
                nextStates.set(13);
            } else if ((ch == 'E') || (ch == 'e')) {
                nextStates.set(9);
            } else if (ch == 'F') {
                if (validTypes == null || validTypes.contains(FLOAT)) type = FLOAT;
            } else if (ch == 'f') {
                if (validTypes == null || validTypes.contains(FLOAT)) type = FLOAT;
            }
            return type;
        }

        private static TokenType getNfaNameSNBTIndex14(int ch, BitSet nextStates, EnumSet<TokenType> validTypes) {
            TokenType type = null;
            if (ch == '.') {
                nextStates.set(12);
            } else if (ch >= '0' && ch <= '9') {
                nextStates.set(14);
            } else if ((ch == 'E') || (ch == 'e')) {
                nextStates.set(9);
            } else if (ch == 'F') {
                if (validTypes == null || validTypes.contains(FLOAT)) type = FLOAT;
            } else if (ch == 'f') {
                if (validTypes == null || validTypes.contains(FLOAT)) type = FLOAT;
            }
            return type;
        }

        private static TokenType getNfaNameSNBTIndex15(int ch, BitSet nextStates, EnumSet<TokenType> validTypes) {
            TokenType type = null;
            if (ch == '0') {
                nextStates.set(17);
            } else if (ch >= '1' && ch <= '9') {
                nextStates.set(16);
            }
            return type;
        }

        private static TokenType getNfaNameSNBTIndex16(int ch, BitSet nextStates, EnumSet<TokenType> validTypes) {
            TokenType type = null;
            if (ch >= '0' && ch <= '9') {
                nextStates.set(16);
            } else if (ch == 'L') {
                if (validTypes == null || validTypes.contains(LONG)) type = LONG;
            } else if (ch == 'l') {
                if (validTypes == null || validTypes.contains(LONG)) type = LONG;
            }
            return type;
        }

        private static TokenType getNfaNameSNBTIndex17(int ch, BitSet nextStates, EnumSet<TokenType> validTypes) {
            TokenType type = null;
            if (ch == 'L') {
                if (validTypes == null || validTypes.contains(LONG)) type = LONG;
            } else if (ch == 'l') {
                if (validTypes == null || validTypes.contains(LONG)) type = LONG;
            }
            return type;
        }

        private static TokenType getNfaNameSNBTIndex18(int ch, BitSet nextStates, EnumSet<TokenType> validTypes) {
            TokenType type = null;
            if ((ch >= 0x0 && ch <= '&') || (ch >= '(')) {
                nextStates.set(18);
            }
            if (ch == '\\') {
                nextStates.set(19);
            } else if (ch == '\'') {
                if (validTypes == null || validTypes.contains(STRING)) type = STRING;
            }
            return type;
        }

        private static TokenType getNfaNameSNBTIndex19(int ch, BitSet nextStates, EnumSet<TokenType> validTypes) {
            TokenType type = null;
            if (ch == '\'') {
                nextStates.set(18);
            }
            return type;
        }

        private static TokenType getNfaNameSNBTIndex20(int ch, BitSet nextStates, EnumSet<TokenType> validTypes) {
            TokenType type = null;
            if (ch == '0') {
                nextStates.set(21);
            } else if (ch >= '1' && ch <= '9') {
                nextStates.set(22);
            }
            return type;
        }

        private static TokenType getNfaNameSNBTIndex21(int ch, BitSet nextStates, EnumSet<TokenType> validTypes) {
            TokenType type = null;
            if (ch == 'S') {
                if (validTypes == null || validTypes.contains(SHORT)) type = SHORT;
            } else if (ch == 's') {
                if (validTypes == null || validTypes.contains(SHORT)) type = SHORT;
            }
            return type;
        }

        private static TokenType getNfaNameSNBTIndex22(int ch, BitSet nextStates, EnumSet<TokenType> validTypes) {
            TokenType type = null;
            if (ch >= '0' && ch <= '9') {
                nextStates.set(22);
            } else if (ch == 'S') {
                if (validTypes == null || validTypes.contains(SHORT)) type = SHORT;
            } else if (ch == 's') {
                if (validTypes == null || validTypes.contains(SHORT)) type = SHORT;
            }
            return type;
        }

        private static TokenType getNfaNameSNBTIndex23(int ch, BitSet nextStates, EnumSet<TokenType> validTypes) {
            TokenType type = null;
            if (ch == '.') {
                nextStates.set(2);
            } else if ((ch == 'E') || (ch == 'e')) {
                nextStates.set(4);
            } else if (ch == 'D') {
                if (validTypes == null || validTypes.contains(DOUBLE)) type = DOUBLE;
            } else if (ch == 'd') {
                if (validTypes == null || validTypes.contains(DOUBLE)) type = DOUBLE;
            }
            return type;
        }

        private static TokenType getNfaNameSNBTIndex24(int ch, BitSet nextStates, EnumSet<TokenType> validTypes) {
            TokenType type = null;
            if (ch == 'r') {
                nextStates.set(25);
            }
            return type;
        }

        private static TokenType getNfaNameSNBTIndex25(int ch, BitSet nextStates, EnumSet<TokenType> validTypes) {
            TokenType type = null;
            if (ch == 'u') {
                nextStates.set(26);
            }
            return type;
        }

        private static TokenType getNfaNameSNBTIndex26(int ch, BitSet nextStates, EnumSet<TokenType> validTypes) {
            TokenType type = null;
            if (ch == 'e') {
                if (validTypes == null || validTypes.contains(BOOLEAN)) type = BOOLEAN;
            }
            return type;
        }

        private static TokenType getNfaNameSNBTIndex27(int ch, BitSet nextStates, EnumSet<TokenType> validTypes) {
            TokenType type = null;
            if (ch == '0') {
                nextStates.set(29);
            } else if (ch >= '1' && ch <= '9') {
                nextStates.set(28);
            }
            return type;
        }

        private static TokenType getNfaNameSNBTIndex28(int ch, BitSet nextStates, EnumSet<TokenType> validTypes) {
            TokenType type = null;
            if (ch >= '0' && ch <= '9') {
                nextStates.set(28);
            } else if (ch == 'B') {
                if (validTypes == null || validTypes.contains(BYTE)) type = BYTE;
            } else if (ch == 'b') {
                if (validTypes == null || validTypes.contains(BYTE)) type = BYTE;
            }
            return type;
        }

        private static TokenType getNfaNameSNBTIndex29(int ch, BitSet nextStates, EnumSet<TokenType> validTypes) {
            TokenType type = null;
            if (ch == 'B') {
                if (validTypes == null || validTypes.contains(BYTE)) type = BYTE;
            } else if (ch == 'b') {
                if (validTypes == null || validTypes.contains(BYTE)) type = BYTE;
            }
            return type;
        }

        private static TokenType getNfaNameSNBTIndex30(int ch, BitSet nextStates, EnumSet<TokenType> validTypes) {
            TokenType type = null;
            if (ch >= '0' && ch <= '9') {
                nextStates.set(30);
                if (validTypes == null || validTypes.contains(INTEGER)) type = INTEGER;
            }
            return type;
        }

        private static TokenType getNfaNameSNBTIndex31(int ch, BitSet nextStates, EnumSet<TokenType> validTypes) {
            TokenType type = null;
            if (ch == '0') {
                nextStates.set(23);
            } else if (ch >= '1' && ch <= '9') {
                nextStates.set(1);
            }
            return type;
        }

        private static TokenType getNfaNameSNBTIndex32(int ch, BitSet nextStates, EnumSet<TokenType> validTypes) {
            TokenType type = null;
            if (ch == '\t') {
                nextStates.set(32);
                if (validTypes == null || validTypes.contains(WHITESPACE)) type = WHITESPACE;
            } else if (ch == '\n') {
                nextStates.set(32);
                if (validTypes == null || validTypes.contains(WHITESPACE)) type = WHITESPACE;
            } else if (ch == '\r') {
                nextStates.set(32);
                if (validTypes == null || validTypes.contains(WHITESPACE)) type = WHITESPACE;
            } else if (ch == ' ') {
                nextStates.set(32);
                if (validTypes == null || validTypes.contains(WHITESPACE)) type = WHITESPACE;
            }
            return type;
        }

        private static TokenType getNfaNameSNBTIndex33(int ch, BitSet nextStates, EnumSet<TokenType> validTypes) {
            TokenType type = null;
            if (ch == 'a') {
                nextStates.set(34);
            }
            return type;
        }

        private static TokenType getNfaNameSNBTIndex34(int ch, BitSet nextStates, EnumSet<TokenType> validTypes) {
            TokenType type = null;
            if (ch == 'l') {
                nextStates.set(35);
            }
            return type;
        }

        private static TokenType getNfaNameSNBTIndex35(int ch, BitSet nextStates, EnumSet<TokenType> validTypes) {
            TokenType type = null;
            if (ch == 's') {
                nextStates.set(36);
            }
            return type;
        }

        private static TokenType getNfaNameSNBTIndex36(int ch, BitSet nextStates, EnumSet<TokenType> validTypes) {
            TokenType type = null;
            if (ch == 'e') {
                if (validTypes == null || validTypes.contains(BOOLEAN)) type = BOOLEAN;
            }
            return type;
        }

        private static TokenType getNfaNameSNBTIndex37(int ch, BitSet nextStates, EnumSet<TokenType> validTypes) {
            TokenType type = null;
            if ((ch >= 0x0 && ch <= '!') || (ch >= '#')) {
                nextStates.set(37);
            }
            if (ch == '\\') {
                nextStates.set(38);
            } else if (ch == '"') {
                if (validTypes == null || validTypes.contains(STRING)) type = STRING;
            }
            return type;
        }

        private static TokenType getNfaNameSNBTIndex38(int ch, BitSet nextStates, EnumSet<TokenType> validTypes) {
            TokenType type = null;
            if (ch == '"') {
                nextStates.set(37);
            }
            return type;
        }

        private static TokenType getNfaNameSNBTIndex39(int ch, BitSet nextStates, EnumSet<TokenType> validTypes) {
            TokenType type = null;
            if (ch == '0') {
                if (validTypes == null || validTypes.contains(INTEGER)) type = INTEGER;
            } else if (ch >= '1' && ch <= '9') {
                nextStates.set(30);
                if (validTypes == null || validTypes.contains(INTEGER)) type = INTEGER;
            }
            return type;
        }

        private static void NFA_FUNCTIONS_init() {
            nfaFunctions = new NfaFunction[] {SNBT::getNfaNameSNBTIndex0, SNBT::getNfaNameSNBTIndex1,
            SNBT::getNfaNameSNBTIndex2, SNBT::getNfaNameSNBTIndex3, SNBT::getNfaNameSNBTIndex4,
            SNBT::getNfaNameSNBTIndex5, SNBT::getNfaNameSNBTIndex6, SNBT::getNfaNameSNBTIndex7,
            SNBT::getNfaNameSNBTIndex8, SNBT::getNfaNameSNBTIndex9, SNBT::getNfaNameSNBTIndex10,
            SNBT::getNfaNameSNBTIndex11, SNBT::getNfaNameSNBTIndex12, SNBT::getNfaNameSNBTIndex13,
            SNBT::getNfaNameSNBTIndex14, SNBT::getNfaNameSNBTIndex15, SNBT::getNfaNameSNBTIndex16,
            SNBT::getNfaNameSNBTIndex17, SNBT::getNfaNameSNBTIndex18, SNBT::getNfaNameSNBTIndex19,
            SNBT::getNfaNameSNBTIndex20, SNBT::getNfaNameSNBTIndex21, SNBT::getNfaNameSNBTIndex22,
            SNBT::getNfaNameSNBTIndex23, SNBT::getNfaNameSNBTIndex24, SNBT::getNfaNameSNBTIndex25,
            SNBT::getNfaNameSNBTIndex26, SNBT::getNfaNameSNBTIndex27, SNBT::getNfaNameSNBTIndex28,
            SNBT::getNfaNameSNBTIndex29, SNBT::getNfaNameSNBTIndex30, SNBT::getNfaNameSNBTIndex31,
            SNBT::getNfaNameSNBTIndex32, SNBT::getNfaNameSNBTIndex33, SNBT::getNfaNameSNBTIndex34,
            SNBT::getNfaNameSNBTIndex35, SNBT::getNfaNameSNBTIndex36, SNBT::getNfaNameSNBTIndex37,
            SNBT::getNfaNameSNBTIndex38, SNBT::getNfaNameSNBTIndex39};
        }

    }

}

