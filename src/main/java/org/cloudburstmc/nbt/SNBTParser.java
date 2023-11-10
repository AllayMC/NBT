package org.cloudburstmc.nbt;

import org.allaymc.snbt.Node;
import org.allaymc.snbt.ParseException;
import org.allaymc.snbt.SNBTParserImplement;
import org.allaymc.snbt.Token;
import org.allaymc.snbt.ast.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Author: Cool_Loong <br>
 * Date: 6/7/2023 <br>
 * Allay Project
 */
public class SNBTParser {
    private final Node value;

    private SNBTParser(String SNBT) {
        SNBTParserImplement parser = new SNBTParserImplement(SNBT);
        parser.Root();
        value = parser.rootNode().getFirstChild();//VALUE EOF
    }

    public static Object parse(String content) {
        SNBTParser parser = new SNBTParser(content);
        return parser.parseNode(parser.value);
    }

    public static <T> T parse(NbtType<T> type, String content) {
        SNBTParser parser = new SNBTParser(content);
        Object o = parser.parseNode(parser.value);
        return type.getTagClass().cast(o);
    }

    private Object parseNode(Node node) throws ParseException {
        if (node instanceof ByteArrayNBT) {
            Object[] boxedArray = getTokenStream(node, t -> t.getType() == Token.TokenType.BYTE).map(t -> {
                String v = t.getNormalizedText();
                return Byte.parseByte(v.substring(0, v.length() - 1));
            }).toArray();
            int len = boxedArray.length;
            byte[] array = new byte[len];
            for (int i = 0; i < len; i++) {
                array[i] = ((Number) boxedArray[i]).byteValue();
            }
            return array;
        } else if (node instanceof IntArrayNBT) {
            return getTokenStream(node, t -> t.getType() == Token.TokenType.BYTE).map(t -> Integer.parseInt(t.getNormalizedText())).mapToInt(Integer::intValue).toArray();
        } else if (node instanceof LongArrayNBT) {
            return getTokenStream(node, t -> t.getType() == Token.TokenType.LONG).map(t -> {
                String v = t.getNormalizedText();
                return Long.parseLong(v.substring(0, v.length() - 1));
            }).mapToLong(Long::longValue).toArray();
        } else if (node instanceof ListNBT) {//only Value
            return parseListTag(node);
        } else if (node instanceof CompoundNBT) {//only KeyValuePair
            return parseNBT(node);
        }
        return null;
    }

    private Object parseToken(Token token) {
        try {
            var s = token.getNormalizedText();
            switch (token.getType()) {
                case FLOAT -> {
                    return Float.parseFloat(s.substring(0, s.length() - 1));
                }
                case DOUBLE -> {
                    return Double.parseDouble(s.substring(0, s.length() - 1));
                }
                case BOOLEAN -> {
                    return Boolean.parseBoolean(token.getNormalizedText()) ? 1 : 0;
                }
                case BYTE -> {
                    return Byte.parseByte(s.substring(0, s.length() - 1));
                }
                case SHORT -> {
                    return Short.parseShort(s.substring(0, s.length() - 1));
                }
                case INTEGER -> {
                    return Integer.parseInt(s);
                }
                case LONG -> {
                    return Long.parseLong(s.substring(0, s.length() - 1));
                }
                case STRING -> {
                    return s.substring(1, s.length() - 1);
                }
                default -> {
                    return null;
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }

    private NbtMap parseNBT(Node node) throws ParseException {
        NbtMapBuilder builder = NbtMap.builder();
        for (Iterator<Node> it = node.iterator(); it.hasNext(); ) {
            Node child = it.next();
            if (child instanceof KeyValuePair) {
                String first = child.getFirstChild().getImage();
                var key = first.substring(1, first.length() - 1);
                if (child.getChildCount() == 3) {
                    var value = child.getChild(2);
                    if (value.hasChildNodes()) {
                        builder.put(key, parseNode(value));
                    } else {
                        var token = (Token) value;
                        if (isLiteralValue(token)) {
                            builder.put(key, parseToken(token));
                        }
                    }
                } else {
                    builder.putEnd(key);
                }
            }
        }
        return builder.build();
    }

    @SuppressWarnings("unchecked")
    private NbtList<Object> parseListTag(Node node) {
        var result = new ArrayList<>();
        for (Iterator<Node> it = node.iterator(); it.hasNext(); ) {
            Node child = it.next();
            if (child instanceof Token token) {
                if (isLiteralValue(token)) {
                    result.add(parseToken(token));
                }
            } else if (child.hasChildNodes()) {
                result.add(parseNode(child));
            }
        }
        if (result.size() == 0) {
            return new NbtList<>();
        }
        return new NbtList<>((NbtType<Object>) NbtType.byClass(result.get(0).getClass()), result);
    }

    private Stream<Token> getTokenStream(Node node, Predicate<Token> predicate) {
        return node.getAllTokens(false).stream().filter(t -> t instanceof Token)
                .map(t -> (Token) t)
                .filter(predicate);
    }

    private boolean isLiteralValue(Token token) {
        return switch (token.getType()) {
            case FLOAT, DOUBLE, STRING, SHORT, INTEGER, LONG, BYTE, BOOLEAN -> true;
            default -> false;
        };
    }
}
