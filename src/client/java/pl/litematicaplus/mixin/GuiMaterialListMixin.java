package pl.litematicaplus.mixin;

import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.gui.GuiMaterialList;
import fi.dy.masa.litematica.materials.MaterialListBase;
import fi.dy.masa.litematica.schematic.placement.SchematicPlacement;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.litematicaplus.gui.GuiBlockReplacement;

@Mixin(GuiMaterialList.class)
public abstract class GuiMaterialListMixin {

    @Inject(method = "initGui", at = @At("TAIL"))
    private void litematicaPlus$button(CallbackInfo ci) {
        GuiMaterialList gui = (GuiMaterialList) (Object) this;
        SchematicPlacement placement = litematicaPlus$findPlacement(gui.getMaterialList());
        if (placement == null) return;
        pl.litematicaplus.BlockReplacements.setListPlacement(placement.getName());

        String label = "Replace";
        int width = gui.getStringWidth(label) + 10;
        int x = (int) (gui.getScreenWidth() * 0.63);   // wolne miejsce między "Info HUD" a "Multiplier"
        int y = 24;   // było: 22

        ButtonGeneric button = new ButtonGeneric(x, y, width, 20, label, new String[0]);
        gui.addButton(button, (b, m) -> GuiBase.openGui(new GuiBlockReplacement(placement, gui)));
    }

    /** Szuka placementu po tytule listy ("... placement 'Nazwa'"), awaryjnie bierze zaznaczony. */
    private static SchematicPlacement litematicaPlus$findPlacement(MaterialListBase list) {
        var manager = DataManager.getSchematicPlacementManager();
        String title = list.getTitle();
        if (title != null) {
            for (SchematicPlacement p : manager.getAllSchematicsPlacements()) {
                if (title.contains("'" + p.getName() + "'")) return p;
            }
        }
        return manager.getSelectedSchematicPlacement();
    }
}