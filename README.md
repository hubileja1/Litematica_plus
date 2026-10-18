# Litematica+ (litematica_plus)
Start: 26.2. Zależności: Litematica 26.2-0.28.4, MaLiLib 26.2-0.29.4 (Modrinth Maven).
1. Uzupełnij `gradle.properties` (loader/loom/fabric api) wg https://fabricmc.net/develop
2. `./gradlew genSources` i popraw sygnaturę w `ContainerScreenMixin`
3. `./gradlew runClient`
Weryfikacja do zrobienia w grze: czy block entity w schemacie ma zawartość, kolejność połówek dużej skrzyni.
