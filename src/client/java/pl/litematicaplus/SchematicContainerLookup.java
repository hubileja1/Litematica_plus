package pl.litematicaplus;

import fi.dy.masa.litematica.world.SchematicWorldHandler;
import fi.dy.masa.litematica.world.WorldSchematic;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;

/** Pobiera oczekiwaną zawartość kontenera ze schematu załadowanego w Litematice. */
public final class SchematicContainerLookup {
    private SchematicContainerLookup() {}

    /**
     * @return oczekiwany ItemStack dla slotu kontenera (EMPTY = slot ma być pusty)
     *         albo null, gdy brak schematu lub w tym miejscu nie ma kontenera.
     */
    public static ItemStack expected(BlockPos pos, int containerSlot) {
        WorldSchematic world = SchematicWorldHandler.getSchematicWorld();
        if (world == null || pos == null) return null;               // zmiana 1

        BlockState state = world.getBlockState(pos);
        BlockPos primary = pos;
        int slot = containerSlot;

        // Duża skrzynia: sloty 0-26 = połowa RIGHT, 27-53 = połowa LEFT (do weryfikacji w grze).
        if (state.getBlock() instanceof ChestBlock && state.hasProperty(ChestBlock.TYPE)
                && state.getValue(ChestBlock.TYPE) != ChestType.SINGLE) {
            BlockPos other = pos.relative(ChestBlock.getConnectedDirection(state));
            boolean clickedIsRight = state.getValue(ChestBlock.TYPE) == ChestType.RIGHT;
            BlockPos right = clickedIsRight ? pos : other;
            BlockPos left = clickedIsRight ? other : pos;
            primary = slot < 27 ? right : left;
            slot = slot % 27;
        }

        BlockEntity be = world.getBlockEntity(primary);
        if (be instanceof Container container && slot >= 0 && slot < container.getContainerSize()) {
            return container.getItem(slot);
        }
        return null;                                                 // zmiana 2
    }
}