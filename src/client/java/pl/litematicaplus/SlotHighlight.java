package pl.litematicaplus;

import net.minecraft.world.item.ItemStack;

/** Logika kolorów niezależna od wersji Minecrafta (bez rysowania). */
public final class SlotHighlight {
    public static final int MISSING = 0x280000FF; // niebieski – brakuje przedmiotu (25%)
    public static final int WRONG   = 0x50FF0000; // czerwony – zły przedmiot (~31%)
    public static final int EXTRA   = 0x50AA00FF; // fioletowy – przedmiot tam, gdzie ma być pusto
    public static final int NONE    = 0;

    private SlotHighlight() {}

    public static int colorFor(ItemStack expected, ItemStack actual) {
        if (!expected.isEmpty() && actual.isEmpty()) return MISSING;
        if (!expected.isEmpty() && !ItemStack.isSameItem(expected, actual)) return WRONG;
        if (expected.isEmpty() && !actual.isEmpty()) return EXTRA;
        return NONE;
    }
}
