package org.cloudburstmc.nbt;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Author: Cool_Loong <br>
 * Date: 6/7/2023 <br>
 * Allay Project
 */
public class SNBTTest {
    @Test
    void TestDeserializationSNBT() {
        var str = """
                {"Air":300s,"Armor":[{"Count":0b,"Damage":0s,"Name":"","WasPickedUp":0b},{"Count":0b,"Damage":0s,"Name":"","WasPickedUp":0b},{"Count":0b,"Damage":0s,"Name":"","WasPickedUp":0b},{"Count":0b,"Damage":0s,"Name":"","WasPickedUp":0b}],"AttackTime":0s,"Attributes":[{"Base":0.0f,"Current":0.0f,"DefaultMax":1024.0f,"DefaultMin":-1024.0f,"Max":1024.0f,"Min":-1024.0f,"Name":"minecraft:luck"},{"Base":10.0f,"Current":-8.0f,"DefaultMax":10.0f,"DefaultMin":0.0f,"Max":10.0f,"Min":0.0f,"Name":"minecraft:health"},{"Base":0.0f,"Current":0.0f,"DefaultMax":16.0f,"DefaultMin":0.0f,"Max":16.0f,"Min":0.0f,"Name":"minecraft:absorption"},{"Base":0.0f,"Current":0.0f,"DefaultMax":1.0f,"DefaultMin":0.0f,"Max":1.0f,"Min":0.0f,"Name":"minecraft:knockback_resistance"},{"Base":0.25f,"Current":0.25f,"DefaultMax":3.40282E38f,"DefaultMin":0.0f,"Max":3.40282E38f,"Min":0.0f,"Name":"minecraft:movement"},{"Base":0.02f,"Current":0.02f,"DefaultMax":3.40282E38f,"DefaultMin":0.0f,"Max":3.40282E38f,"Min":0.0f,"Name":"minecraft:underwater_movement"},{"Base":0.02f,"Current":0.02f,"DefaultMax":3.40282E38f,"DefaultMin":0.0f,"Max":3.40282E38f,"Min":0.0f,"Name":"minecraft:lava_movement"},{"Base":16.0f,"Current":16.0f,"DefaultMax":2048.0f,"DefaultMin":0.0f,"Max":2048.0f,"Min":0.0f,"Name":"minecraft:follow_range"}],"BodyRot":-2.70421f,"BreedCooldown":0,"Chested":0b,"Color":0b,"Color2":0b,"Dead":0b,"DeathTime":0s,"FallDistance":0.0f,"HurtTime":0s,"InLove":0,"Invulnerable":0b,"IsAngry":0b,"IsAutonomous":0b,"IsBaby":0b,"IsEating":0b,"IsGliding":0b,"IsGlobal":0b,"IsIllagerCaptain":0b,"IsOrphaned":0b,"IsOutOfControl":0b,"IsPregnant":0b,"IsRoaring":0b,"IsScared":0b,"IsStunned":0b,"IsSwimming":0b,"IsTamed":0b,"IsTrusting":0b,"LastDimensionId":0,"LeasherID":-1l,"LootDropped":0b,"LoveCause":0l,"Mainhand":[{"Count":0b,"Damage":0s,"Name":"","WasPickedUp":0b}],"MarkVariant":0,"NaturalSpawn":1b,"Offhand":[{"Count":0b,"Damage":0s,"Name":"","WasPickedUp":0b}],"OnGround":1b,"OwnerNew":-1l,"PortalCooldown":0,"Pos":[0.442949f,65.0f,-16.7079f],"Rotation":[-2.70421f,0.0f],"Saddled":0b,"Sheared":0b,"ShowBottom":0b,"Sitting":0b,"SkinID":0,"Strength":0,"StrengthMax":0,"Surface":1b,"Tags":[],"TargetID":-1l,"TradeExperience":0,"TradeTier":0,"UniqueID":-4294967212l,"Variant":0,"boundX":0,"boundY":0,"boundZ":0,"canPickupItems":0b,"definitions":["+minecraft:pig","+","+minecraft:pig_adult","+minecraft:pig_unsaddled"],"expDropEnabled":1b,"hasBoundOrigin":0b,"hasSetCanPickupItems":1b,"identifier":"minecraft:pig","internalComponents":{"EntityStorageKeyComponent":{"StorageKey":"T"}}}""";
        var NBT = SNBTParser.parse(NbtType.COMPOUND, str);
        var SNBT = NBT.toSNBT();
        assertEquals(str, SNBT);
    }

    @Test
    void TestNBT2SNBT() {
        NbtMap oldNBT = NbtMap.builder().putInt("test1", 1)
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
        var oldSNBT = oldNBT.toSNBT();
        var newNbt = SNBTParser.parse(NbtType.COMPOUND, oldSNBT);
        assertEquals(oldSNBT, newNbt.toSNBT());
    }
}
