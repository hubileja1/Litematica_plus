# Litematica+ (litematica_plus)
Start: 26.2. Dependencies: Litematica 26.2-0.28.4, MaLiLib 26.2-0.29.4 (Modrinth Maven).
1. Configure `gradle.properties` (loader/loom/fabric api) according to https://fabricmc.net/develop
2. Run `./gradlew genSources` and fix the signature in `ContainerScreenMixin`
3. Run `./gradlew runClient`
Verification to be performed in-game: check if block entities within the schematic retain their contents, and verify the orientation/ordering of the two halves of large chests.
