# crackerslib

A small library mod adding a highly customizable config menu system and some smaller utilities. To be expanded.

Used in mods such as [Mob Battle Music](https://www.curseforge.com/minecraft/mc-mods/mob-battle-music) and [Story Mod](https://www.curseforge.com/minecraft/mc-mods/story-mod).

To use in your own mod, include the following in your in your ``build.gradle`` file (replace ``${minecraft_version}`` and ``${crackerslib_version}`` appropriately, or define them in your ``gradle.properties`` file):

```gradle
repositories {
  maven {
    name "nonamecrackers2Maven"
    url "https://maven.nonamecrackers2.dev/releases"
  }
}

dependencies {
  implementation fg.deobf("nonamecrackers2:crackerslib-forge:${minecraft_version}-${crackerslib_version}")
}
```

Feel free to use Jar-in-Jar to package CrackersLib with your mod (when publishing your mod, make sure to use the jar tagged with ``-all``).

Jar-in-Jar example:

```gradle
dependencies {
  jarJar("nonamecrackers2:crackerslib-forge:${minecraft_version}-${crackerslib_version}") {
    jarJar.ranged(it, "[${minecraft_version}-${crackerslib_version},)")
  }
  implementation fg.deobf("nonamecrackers2:crackerslib-forge:${minecraft_version}-${crackerslib_version}")
}
```
