package com.nukkitx.nbt.stream;

import com.nukkitx.nbt.NBTEncodingType;
import com.nukkitx.nbt.TagType;
import com.nukkitx.nbt.tag.*;
import com.nukkitx.nbt.util.VarInt;

import java.io.Closeable;
import java.io.DataInput;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.nukkitx.nbt.NBTEncodingType.BEDROCK;
import static com.nukkitx.nbt.NBTIO.MAX_DEPTH;

public class NBTInputStream implements Closeable {
    private final DataInput input;
    private final NBTEncodingType encoding;
    private boolean closed = false;

    public NBTInputStream(DataInput input) {
        this(input, NBTEncodingType.NOTCHIAN);
    }

    public NBTInputStream(DataInput input, NBTEncodingType encoding) {
        this.input = Objects.requireNonNull(input, "input");
        this.encoding = Objects.requireNonNull(encoding, "encoding");
    }

    public Tag<?> readTag() throws IOException {
        return readTag(0);
    }

    private Tag<?> readTag(int depth) throws IOException {
        if (closed) {
            throw new IllegalStateException("Trying to read from a closed reader!");
        }
        int typeId = input.readByte() & 0xFF;
        TagType type = TagType.byId(typeId);
        if (type == null) {
            throw new IOException("Invalid encoding ID " + typeId);
        }

        return deserialize(type, false, depth);
    }

    private Tag<?> deserialize(TagType type, boolean skipName, int depth) throws IOException {
        if (depth > MAX_DEPTH) {
            throw new IllegalArgumentException("NBT compound is too deeply nested");
        }

        String tagName = null;
        if (type != TagType.END && !skipName) {
            int length = encoding == BEDROCK ? input.readByte() & 0xFF : input.readShort();
            byte[] tagNameBytes = new byte[length];
            input.readFully(tagNameBytes);
            tagName = new String(tagNameBytes, StandardCharsets.UTF_8);
        }

        switch (type) {
            case END:
                if (depth == 0) {
                    throw new IllegalArgumentException("Found a TAG_End in root tag!");
                }
                return EndTag.INSTANCE;
            case BYTE:
                byte b = input.readByte();
                return new ByteTag(tagName, b);
            case SHORT:
                short sh = input.readShort();
                return new ShortTag(tagName, sh);
            case INT:
                int in = encoding == BEDROCK ? VarInt.readInt(input) : input.readInt();
                return new IntTag(tagName, in);
            case LONG:
                return new LongTag(tagName, input.readLong());
            case FLOAT:
                return new FloatTag(tagName, input.readFloat());
            case DOUBLE:
                return new DoubleTag(tagName, input.readDouble());
            case BYTE_ARRAY:
                int arraySz1 = encoding == BEDROCK ? VarInt.readInt(input) : input.readInt();
                byte[] valueBytesBa = new byte[arraySz1];
                input.readFully(valueBytesBa);
                return new ByteArrayTag(tagName, valueBytesBa);
            case STRING:
                int length = encoding == BEDROCK ? input.readByte() : input.readUnsignedShort();
                byte[] valueBytes = new byte[length];
                input.readFully(valueBytes);
                return new StringTag(tagName, new String(valueBytes, StandardCharsets.UTF_8));
            case COMPOUND:
                Map<String, Tag<?>> map = new HashMap<>();
                Tag<?> inTag1;
                while ((inTag1 = readTag(depth + 1)) != EndTag.INSTANCE) {
                    map.put(inTag1.getName(), inTag1);
                }
                return new CompoundTag(tagName, map);
            case LIST:
                int inId = input.readByte() & 0xFF;
                TagType listType = TagType.byId(inId);
                if (listType == null) {
                    String append = tagName == null ? "" : "('" + tagName + "')";
                    throw new IllegalArgumentException("Found invalid type in TAG_List" + append + ": " + inId);
                }
                List<Tag<?>> list = new ArrayList<>();
                int listLength = encoding == BEDROCK ? VarInt.readInt(input) : input.readInt();
                for (int i = 0; i < listLength; i++) {
                    list.add(deserialize(listType, true, depth + 1));
                }
                // Unchecked cast is expected
                return new ListTag(tagName, listType.getTagClass(), list);
            case INT_ARRAY:
                int arraySz2 = encoding == BEDROCK ? VarInt.readInt(input) : input.readInt();
                int[] intValues = new int[arraySz2];
                for (int i = 0; i < arraySz2; i++) {
                    intValues[i] = encoding == BEDROCK ? VarInt.readInt(input) : input.readInt();
                }
                return new IntArrayTag(tagName, intValues);
            case LONG_ARRAY:
                int arraySz3 = encoding == BEDROCK ? VarInt.readInt(input) : input.readInt();
                long[] longValues = new long[arraySz3];
                for (int i = 0; i < arraySz3; i++) {
                    longValues[i] = encoding == BEDROCK ? VarInt.readLong(input) : input.readLong();
                }
                return new LongArrayTag(tagName, longValues);
        }

        throw new IllegalArgumentException("Unknown type " + type);
    }

    @Override
    public void close() throws IOException {
        if (closed) return;
        closed = true;
        if (input instanceof Closeable) {
            ((Closeable) input).close();
        }
    }
}
