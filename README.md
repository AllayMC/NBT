# NBT
[![](https://jitpack.io/v/AllayMC/NBT.svg)](https://jitpack.io/#AllayMC/NBT)  
NBT library used by Allay which is originally forked from https://github.com/CloudburstMC/NBT

This fork has the following extra features:
- MutableNbtMap
- SNBT support
- Convenient conversion between `record` and NBT by using `@nbt` annotation

### Dependency 

#### Maven
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.AllayMC</groupId>
        <artifactId>NBT</artifactId>
        <version>3.0.10</version>
    </dependency>
</dependencies>
```

#### Gradle (Kotlin DSL)

```kt
repositories {
    maven { url = uri("https://jitpack.io") }
}

implementation("com.github.AllayMC:NBT:3.0.10")
```
