package org.cloudburstmc.nbt;

import org.cloudburstmc.nbt.util.function.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

/**
 * Author: Cool_Loong <br>
 * Date: 6/10/2023 <br>
 * Allay Project
 */
public interface NBTReader {
    Set<Map.Entry<String, Object>> entrySet();

    boolean containsKey(String key, NbtType<?> type);

    boolean getBoolean(String key);

    boolean getBoolean(String key, boolean defaultValue);

    void listenForBoolean(String key, BooleanConsumer consumer);

    byte getByte(String key);

    byte getByte(String key, byte defaultValue);

    void listenForByte(String key, ByteConsumer consumer);

    short getShort(String key);

    short getShort(String key, short defaultValue);

    void listenForShort(String key, ShortConsumer consumer);

    int getInt(String key);

    int getInt(String key, int defaultValue);

    void listenForInt(String key, IntConsumer consumer);

    long getLong(String key);

    long getLong(String key, long defaultValue);

    void listenForLong(String key, LongConsumer consumer);

    float getFloat(String key);

    float getFloat(String key, float defaultValue);

    void listenForFloat(String key, FloatConsumer consumer);

    double getDouble(String key);

    double getDouble(String key, double defaultValue);

    void listenForDouble(String key, DoubleConsumer consumer);

    String getString(String key);

    String getString(String key, String defaultValue);

    void listenForString(String key, Consumer<String> consumer);

    byte[] getByteArray(String key);

    byte[] getByteArray(String key, byte[] defaultValue);

    void listenForByteArray(String key, Consumer<byte[]> consumer);

    int[] getIntArray(String key);

    int[] getIntArray(String key, int[] defaultValue);

    void listenForIntArray(String key, Consumer<int[]> consumer);

    long[] getLongArray(String key);

    long[] getLongArray(String key, long[] defaultValue);

    void listenForLongArray(String key, Consumer<long[]> consumer);

    NbtMap getCompound(String key);

    NbtMap getCompound(String key, NbtMap defaultValue);

    void listenForCompound(String key, Consumer<NbtMap> consumer);

    <T> List<T> getList(String key, NbtType<T> type);

    <T> void listenForList(String key, NbtType<T> type, Consumer<List<T>> consumer);

    Number getNumber(String key);

    Number getNumber(String key, Number defaultValue);

    void listenForNumber(String key, NumberConsumer consumer);
}
