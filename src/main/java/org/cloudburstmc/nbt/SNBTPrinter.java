package org.cloudburstmc.nbt;

import java.util.Iterator;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Author: Cool_Loong <br>
 * Date: 6/7/2023 <br>
 * Allay Project
 */
public final class SNBTPrinter {
    private static final CommonPrinter commonPrinter = new CommonPrinter();

    private SNBTPrinter() {
    }

    /**
     * Output specified nbt with SNBT format
     *
     * @param nbt the nbt {@link NbtType}
     * @return SNBT
     */
    public static String toSNBT(Object nbt) {
        return commonPrinter.toSNBT(nbt);
    }

    /**
     * Output specified nbt with SNBT format
     *
     * @param nbt   the nbt {@link NbtType}
     * @param space The indentation number of formatted output, must be >0
     * @return SNBT string
     */
    public static String toSNBT(Object nbt, int space) {
        if (space <= 0) throw new IllegalArgumentException("The space must be >0");
        PrettyPrinter prettyPrinter = new PrettyPrinter(space);
        return prettyPrinter.toSNBT(nbt);
    }

    /**
     * Output specified NBTMap with SNBT format
     *
     * @param nbt the nbt {@link NbtType}
     * @return SNBT
     */
    public static String toSNBT(NbtMap nbt) {
        return commonPrinter.toSNBT(nbt);
    }

    /**
     * Output specified NBTMap with SNBT format
     *
     * @param nbt   the nbt {@link NbtType}
     * @param space The indentation number of formatted output, must be >0
     * @return SNBT string
     */
    public static String toSNBT(NbtMap nbt, int space) {
        if (space <= 0) throw new IllegalArgumentException("The space must be >0");
        PrettyPrinter prettyPrinter = new PrettyPrinter(space);
        return prettyPrinter.toSNBT(nbt);
    }

    /**
     * Output specified NbtList with SNBT format
     *
     * @param nbt the nbt {@link NbtType}
     * @return SNBT
     */
    public static String toSNBT(NbtList<?> nbt) {
        return commonPrinter.toSNBT(nbt);
    }

    /**
     * Output specified NbtList with SNBT format
     *
     * @param nbt   the nbt {@link NbtType}
     * @param space The indentation number of formatted output, must be >0
     * @return SNBT string
     */
    public static String toSNBT(NbtList<?> nbt, int space) {
        if (space <= 0) throw new IllegalArgumentException("The space must be >0");
        PrettyPrinter prettyPrinter = new PrettyPrinter(space);
        return prettyPrinter.toSNBT(nbt);
    }

    private static class PrettyPrinter {
        private final int space;
        private int maxDepth = 0;

        public PrettyPrinter(int space) {
            this.space = space;
        }

        public String toSNBT(Object o) {
            if (o instanceof Byte) {
                return ((byte) o) + "b";
            } else if (o instanceof Short) {
                return ((short) o) + "s";
            } else if (o instanceof Integer) {
                return String.valueOf((int) o);
            } else if (o instanceof Long) {
                return ((long) o) + "l";
            } else if (o instanceof Float) {
                return ((float) o) + "f";
            } else if (o instanceof Double) {
                return ((double) o) + "d";
            } else if (o instanceof byte[]) {
                StringJoiner joiner = new StringJoiner(", ");
                for (int i : (byte[]) o) {
                    joiner.add(i + "b");
                }
                return "[B; " + joiner + "]";
            } else if (o instanceof String) {
                return "\"" + o + "\"";
            } else if (o instanceof int[]) {
                StringJoiner joiner = new StringJoiner(", ");
                for (int i : (int[]) o) {
                    joiner.add(String.valueOf(i));
                }
                return "[I; " + joiner + "]";
            } else if (o instanceof long[]) {
                StringJoiner joiner = new StringJoiner(", ");
                for (long i : (long[]) o) {
                    joiner.add(i + "l");
                }
                return "[L; " + joiner + "]";
            } else if (o == NbtType.END) {
                return "";
            } else if (o instanceof NbtList<?> list) {
                maxDepth++;
                return toSNBT(list);
            } else if (o instanceof NbtMap map) {
                maxDepth++;
                return toSNBT(map);
            }
            throw new IllegalArgumentException();
        }

        public String toSNBT(NbtMap map) {
            Iterator<Map.Entry<String, Object>> i = map.entrySet().iterator();
            if (!i.hasNext()) {
                maxDepth--;
                return "{}";
            }
            StringBuilder sb = new StringBuilder();
            String spaceStr1 = " ".repeat(space * maxDepth);
            String spaceStr2 = " ".repeat(space * (maxDepth + 1));
            sb.append('{').append('\n');
            while (true) {
                Map.Entry<String, Object> e = i.next();
                String key = e.getKey();
                String value = toSNBT(e.getValue());
                String string = "\"" + key + "\": " + value;
                sb.append(spaceStr2).append(string);
                if (!i.hasNext()) {
                    maxDepth--;
                    return sb.append('\n').append(spaceStr1).append('}').toString();
                }
                sb.append(',').append('\n');
            }
        }

        public String toSNBT(NbtList<?> list) {
            Iterator<?> it = list.iterator();
            if (!it.hasNext()) {
                maxDepth--;
                return "[]";
            }
            StringBuilder sb = new StringBuilder();
            String spaceStr1 = " ".repeat(space * maxDepth);
            String spaceStr2 = " ".repeat(space * (maxDepth + 1));
            sb.append('[').append('\n');
            while (true) {
                String string = toSNBT(it.next());
                sb.append(spaceStr2).append(string);
                if (!it.hasNext()) {
                    maxDepth--;
                    return sb.append('\n').append(spaceStr1).append(']').toString();
                }
                sb.append(',').append('\n');
            }
        }
    }

    private static class CommonPrinter {
        public String toSNBT(Object o) {
            if (o instanceof Byte) {
                return ((byte) o) + "b";
            } else if (o instanceof Short) {
                return ((short) o) + "s";
            } else if (o instanceof Integer) {
                return String.valueOf((int) o);
            } else if (o instanceof Long) {
                return ((long) o) + "l";
            } else if (o instanceof Float) {
                return ((float) o) + "f";
            } else if (o instanceof Double) {
                return ((double) o) + "d";
            } else if (o instanceof byte[]) {
                StringJoiner joiner = new StringJoiner(",");
                for (int i : (byte[]) o) {
                    joiner.add(i + "b");
                }
                return "[B;" + joiner + "]";
            } else if (o instanceof String) {
                return "\"" + o + "\"";
            } else if (o instanceof int[]) {
                StringJoiner joiner = new StringJoiner(",");
                for (int i : (int[]) o) {
                    joiner.add(String.valueOf(i));
                }
                return "[I;" + joiner + "]";
            } else if (o instanceof long[]) {
                StringJoiner joiner = new StringJoiner(",");
                for (long i : (long[]) o) {
                    joiner.add(i + "l");
                }
                return "[L;" + joiner + "]";
            } else if (o == NbtType.END) {
                return "";
            } else if (o instanceof NbtList<?> list) {
                return toSNBT(list);
            } else if (o instanceof NbtMap map) {
                return toSNBT(map);
            }
            throw new IllegalArgumentException();
        }

        public String toSNBT(NbtMap map) {
            Iterator<Map.Entry<String, Object>> i = map.entrySet().iterator();
            if (!i.hasNext()) {
                return "{}";
            }
            StringBuilder sb = new StringBuilder();
            sb.append('{');
            while (true) {
                Map.Entry<String, Object> e = i.next();
                String key = e.getKey();
                String value = toSNBT(e.getValue());
                String string = "\"" + key + "\":" + value;
                sb.append(string);
                if (!i.hasNext()) {
                    return sb.append('}').toString();
                }
                sb.append(',');
            }
        }

        public String toSNBT(NbtList<?> list) {
            Iterator<?> it = list.iterator();
            if (!it.hasNext()) {
                return "[]";
            }
            StringBuilder sb = new StringBuilder();
            sb.append('[');
            while (true) {
                String string = toSNBT(it.next());
                sb.append(string);
                if (!it.hasNext()) {
                    return sb.append(']').toString();
                }
                sb.append(',');
            }
        }
    }
}
