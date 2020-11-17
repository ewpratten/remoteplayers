# RemotePlayers

RemotePlayers is a Minecraft mod that pulls data from a [dynmap](https://github.com/webbukkit/dynmap) server into [Xaero's minimap](https://www.curseforge.com/minecraft/mc-mods/xaeros-minimap). By asynchronously fetching data over a REST API, and displaying it through hooks into Xaero96's class files. This is done with a mix of reflection and [Mixins](https://github.com/SpongePowered/Mixin).

## Installation

This mod can be built by cloning this repository, then running:

```sh
./gradlew build
```

The resulting mod files are stored in `build/libs/`
