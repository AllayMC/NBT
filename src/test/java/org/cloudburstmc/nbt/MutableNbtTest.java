package org.cloudburstmc.nbt;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Author: Cool_Loong <br>
 * Date: 6/10/2023 <br>
 * Allay Project
 */
public class MutableNbtTest {
    static MutableNbtMap nbt;

    static {
        nbt = MutableNbtMap.from(NbtMap.builder().putInt("test1", 1)
                .putString("test2", "hahaha")
                .putBoolean("test3", false)
                .putByte("test4", (byte) 12)
                .putFloat("test5", (float) 1.22)
                .putDouble("test6", 2.333)
                .putShort("test7", (short) 2131)
                .putByteArray("test8", new byte[]{1, 2, 3, 4, 5})
                .putList("list1", NbtType.LIST,
                        new NbtList<>(NbtType.INT, 2, 3),
                        new NbtList<>(NbtType.DOUBLE, 3.33, 4.44),
                        new NbtList<>(NbtType.BYTE_ARRAY, new byte[]{9, 8, 7, 6, 5}, new byte[]{11, 22, 33, 44, 55})
                )
                .putList("list2", NbtType.COMPOUND,
                        NbtMap.builder().putString("string1", "123").putString("string2", "456").build(),
                        NbtMap.builder().putString("string3", "789").putString("string4", "101112").build()
                )
                .putCompound("nestCompoundTag", NbtMap.builder().putString("string5", "123123123").putString("string6", "zxdzsfdsdf").build())
                .build());
    }

    @Test
    void mutable() {
        assertEquals(nbt.getByte("test4"), (byte) 12);
        nbt.putByte("test4", (byte) 13);
        assertEquals(nbt.getByte("test4"), (byte) 13);
    }

    @Test
    void equals() {
        MutableNbtMap copy1 = MutableNbtMap.from(NbtMap.builder().putInt("test1", 1)
                .putString("test2", "hahaha")
                .putBoolean("test3", false)
                .putByte("test4", (byte) 12)
                .putFloat("test5", (float) 1.22)
                .putDouble("test6", 2.333)
                .putShort("test7", (short) 2131)
                .putByteArray("test8", new byte[]{1, 2, 3, 4, 5})
                .putList("list1", NbtType.LIST,
                        new NbtList<>(NbtType.INT, 2, 3),
                        new NbtList<>(NbtType.DOUBLE, 3.33, 4.44),
                        new NbtList<>(NbtType.BYTE_ARRAY, new byte[]{9, 8, 7, 6, 5}, new byte[]{11, 22, 33, 44, 55})
                )
                .putList("list2", NbtType.COMPOUND,
                        NbtMap.builder().putString("string1", "123").putString("string2", "456").build(),
                        NbtMap.builder().putString("string3", "789").putString("string4", "101112").build()
                )
                .putCompound("nestCompoundTag", NbtMap.builder().putString("string5", "123123123").putString("string6", "zxdzsfdsdf").build())
                .build());
        NbtMap copy2 = NbtMap.builder().putInt("test1", 1)
                .putString("test2", "hahaha")
                .putBoolean("test3", false)
                .putByte("test4", (byte) 12)
                .putFloat("test5", (float) 1.22)
                .putDouble("test6", 2.333)
                .putShort("test7", (short) 2131)
                .putByteArray("test8", new byte[]{1, 2, 3, 4, 5})
                .putList("list1", NbtType.LIST,
                        new NbtList<>(NbtType.INT, 2, 3),
                        new NbtList<>(NbtType.DOUBLE, 3.33, 4.44),
                        new NbtList<>(NbtType.BYTE_ARRAY, new byte[]{9, 8, 7, 6, 5}, new byte[]{11, 22, 33, 44, 55})
                )
                .putList("list2", NbtType.COMPOUND,
                        NbtMap.builder().putString("string1", "123").putString("string2", "456").build(),
                        NbtMap.builder().putString("string3", "789").putString("string4", "101112").build()
                )
                .putCompound("nestCompoundTag", NbtMap.builder().putString("string5", "123123123").putString("string6", "zxdzsfdsdf").build())
                .build();
        assertEquals(nbt, copy1);
        assertEquals(nbt, copy2);
    }

    @Test
    void toImmutable() {
        NbtMap immutable = NbtMap.builder().putInt("test1", 1)
                .putString("test2", "hahaha")
                .putBoolean("test3", false)
                .putByte("test4", (byte) 12)
                .putFloat("test5", (float) 1.22)
                .putDouble("test6", 2.333)
                .putShort("test7", (short) 2131)
                .putByteArray("test8", new byte[]{1, 2, 3, 4, 5})
                .putList("list1", NbtType.LIST,
                        new NbtList<>(NbtType.INT, 2, 3),
                        new NbtList<>(NbtType.DOUBLE, 3.33, 4.44),
                        new NbtList<>(NbtType.BYTE_ARRAY, new byte[]{9, 8, 7, 6, 5}, new byte[]{11, 22, 33, 44, 55})
                )
                .putList("list2", NbtType.COMPOUND,
                        NbtMap.builder().putString("string1", "123").putString("string2", "456").build(),
                        NbtMap.builder().putString("string3", "789").putString("string4", "101112").build()
                )
                .putCompound("nestCompoundTag", NbtMap.builder().putString("string5", "123123123").putString("string6", "zxdzsfdsdf").build())
                .build();
        NbtMap build = nbt.build();
        assertEquals(build, immutable);
    }
}
