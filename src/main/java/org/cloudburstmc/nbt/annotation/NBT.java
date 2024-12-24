package org.cloudburstmc.nbt.annotation;

import org.cloudburstmc.nbt.NbtMap;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link Record} marked with this annotation can be converted {@link org.cloudburstmc.nbt.NbtMap NBT} and create from it<p>
 * By using {@link org.cloudburstmc.nbt.NbtMap#fromRecord(Record) fromRecord} and {@link org.cloudburstmc.nbt.NbtUtils#createRecordFromNBT(Class, NbtMap) createRecordFromNBT}
 */
@Target(value = {ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface NBT {
}
