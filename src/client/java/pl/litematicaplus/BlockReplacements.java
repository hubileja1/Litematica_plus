package pl.litematicaplus;

import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import fi.dy.masa.litematica.data.DataManager;

import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class BlockReplacements {
    // nazwa placementu -> (stary blok -> nowy blok)
    private static final Map<String, Map<Block, Block>> MAPS = new ConcurrentHashMap<>();
    private static volatile Set<Block> SOURCES = Set.of();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("litematica_plus_replacements.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private BlockReplacements() {}

    public static boolean isEmpty() { return MAPS.isEmpty(); }
    public static Map<Block, Block> forPlacement(String name) { return MAPS.get(name); }

    /** Szybki test: czy ten blok ma zamianę w którymkolwiek placemencie. */
    public static boolean isSource(Block b) { return SOURCES.contains(b); }

    private static void rebuildSources() {
        Set<Block> s = new HashSet<>();
        MAPS.values().forEach(m -> s.addAll(m.keySet()));
        SOURCES = s;
    }

    public static void set(String placement, Block from, Block to) {
        if (from == to) return;
        Map<Block, Block> map = MAPS.computeIfAbsent(placement, k -> new ConcurrentHashMap<>());

        // wpisy, które teraz dają na ekranie blok 'from', przekieruj na nowy cel
        for (Map.Entry<Block, Block> e : map.entrySet()) {
            if (e.getValue() == from) {
                if (e.getKey() == to) map.remove(e.getKey()); // wróciło do oryginału
                else e.setValue(to);
            }
        }

        map.put(from, to);
        rebuildSources();
        save();
    }
    public static String listPlacementName() { return listPlacement; }

    public static void remove(String placement, Block from) {
        var m = MAPS.get(placement);
        if (m != null) { m.remove(from); if (m.isEmpty()) MAPS.remove(placement); }
        rebuildSources();
        save();
    }

    /** Podmienia blok, zachowując wspólne właściwości (facing, half, waterlogged...). */
    public static BlockState apply(BlockState state, Block to) {
        BlockState result = to.defaultBlockState();
        for (Property<?> p : state.getProperties()) {
            if (result.hasProperty(p)) result = copy(state, result, p);
        }
        return result;
    }

    private static <T extends Comparable<T>> BlockState copy(BlockState from, BlockState to, Property<T> p) {
        T value = from.getValue(p);
        return p.getPossibleValues().contains(value) ? to.setValue(p, value) : to;
    }

    // ---- zapis / odczyt ----
    public static void save() {
        JsonObject root = new JsonObject();
        MAPS.forEach((placement, map) -> {
            JsonObject o = new JsonObject();
            map.forEach((a, b) -> o.addProperty(BuiltInRegistries.BLOCK.getKey(a).toString(),
                    BuiltInRegistries.BLOCK.getKey(b).toString()));
            root.add(placement, o);
        });
        try { Files.writeString(FILE, GSON.toJson(root)); } catch (Exception e) { e.printStackTrace(); }
    }

    public static void load() {
        MAPS.clear();
        if (Files.exists(FILE)) {
            try {
                JsonObject root = JsonParser.parseString(Files.readString(FILE)).getAsJsonObject();
                root.entrySet().forEach(pe -> {
                    Map<Block, Block> map = new ConcurrentHashMap<>();
                    pe.getValue().getAsJsonObject().entrySet().forEach(be -> {
                        var a = BuiltInRegistries.BLOCK.getOptional(Identifier.parse(be.getKey()));
                        var b = BuiltInRegistries.BLOCK.getOptional(Identifier.parse(be.getValue().getAsString()));
                        if (a.isPresent() && b.isPresent()) map.put(a.get(), b.get());
                    });
                    if (!map.isEmpty()) MAPS.put(pe.getKey(), map);
                });
            } catch (Exception e) { e.printStackTrace(); }
        }
        rebuildSources();
    }
    // ---- placement, dla którego liczona jest lista materiałów ----
    private static volatile String listPlacement;

    public static void setListPlacement(String name) { listPlacement = name; }

    /** Mapa zamian dla listy materiałów: placement z otwartego okna listy, awaryjnie zaznaczony. */
    public static Map<Block, Block> activeListMap() {
        String name = listPlacement;
        if (name != null) {
            var m = MAPS.get(name);
            if (m != null) return m;
        }
        var selected = DataManager.getSchematicPlacementManager().getSelectedSchematicPlacement();
        return selected == null ? null : MAPS.get(selected.getName());
    }
}