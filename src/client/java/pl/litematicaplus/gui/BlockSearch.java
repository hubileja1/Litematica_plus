package pl.litematicaplus.gui;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public final class BlockSearch {
    private BlockSearch() {}

    public static String shortId(Block b) {
        String id = BuiltInRegistries.BLOCK.getKey(b).toString();
        return id.startsWith("minecraft:") ? id.substring(10) : id;
    }

    /** Bloki pasujące do fragmentu nazwy: najpierw zaczynające się od niego, potem zawierające. */
    public static List<Block> search(String query, int limit) {
        String q = query.trim().toLowerCase(Locale.ROOT);
        if (q.startsWith("minecraft:")) q = q.substring(10);
        if (q.isEmpty()) return List.of();

        List<Block> found = new ArrayList<>();
        for (Identifier id : BuiltInRegistries.BLOCK.keySet()) {
            String path = id.getPath();
            if (!path.contains(q)) continue;
            Block block = BuiltInRegistries.BLOCK.getOptional(id).orElse(null);
            if (block == null || block.asItem() == Items.AIR) continue; // bez woda/ogień/powietrze
            found.add(block);
        }

        final String fq = q;
        found.sort(Comparator
                .comparing((Block b) -> !shortId(b).startsWith(fq))
                .thenComparing(BlockSearch::shortId));

        // jeśli wpisano dokładną nazwę i nic innego – nie podpowiadaj
        if (found.size() == 1 && shortId(found.get(0)).equals(q)) return List.of();
        return found.size() > limit ? new ArrayList<>(found.subList(0, limit)) : found;
    }
}