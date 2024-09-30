# crackerslib

A small library mod adding a highly customizable config menu system and some smaller utilities. To be expanded.

Used in mods such as [Mob Battle Music](https://www.curseforge.com/minecraft/mc-mods/mob-battle-music), [Story Mod](https://www.curseforge.com/minecraft/mc-mods/story-mod) and [Cracker's Wither Storm Mod](https://www.curseforge.com/minecraft/mc-mods/crackers-wither-storm-mod).

To use in your own mod, include the following in your in your ``build.gradle`` file (replace ``${crackerslib_version}`` appropriately, or define them in your ``gradle.properties`` file):

```gradle
repositories {
    maven {
        name "nonamecrackers2Maven"
        url "https://maven.nonamecrackers2.dev/releases" //Alternatively "snapshots" if you need snapshot builds
    }
}

dependencies {
    implementation "nonamecrackers2:crackerslib-neoforge:${crackerslib_version}"
}
```

For a full list of versions, please refer to the [maven](https://maven.nonamecrackers2.dev/)

Feel free to use Jar-in-Jar to package CrackersLib with your mod (when publishing your mod, make sure to use the jar tagged with ``-all``).

Jar-in-Jar example:

```gradle
dependencies {
    jarJar("nonamecrackers2:crackerslib-neoforge:${crackerslib_version}") {
    	jarJar.ranged(it, "${crackerslib_version_range}")
    }
    
    implementation "nonamecrackers2:crackerslib-neoforge:${crackerslib_version}"
}
```
