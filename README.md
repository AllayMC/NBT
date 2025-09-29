# NBT
![nbt](https://img.shields.io/maven-central/v/org.allaymc/nbt?label=nbt&link=https%3A%2F%2Fcentral.sonatype.com%2Fartifact%2Forg.allaymc%2Fnbt)
NBT library used by Allay which is originally forked from https://github.com/CloudburstMC/NBT

This fork has the following extra features:
- MutableNbtMap
- SNBT support
- Convenient conversion between `record` and NBT by using `@nbt` annotation

### Dependency 

#### Maven
```xml
<dependencies>
    <dependency>
        <groupId>org.allaymc</groupId>
        <artifactId>nbt</artifactId>
        <version>3.0.10</version>
    </dependency>
</dependencies>
```

#### Gradle (Kotlin DSL)

```kt
repositories {
    mavenCentral()
}

implementation("org.allaymc:nbt:3.0.10")
```
