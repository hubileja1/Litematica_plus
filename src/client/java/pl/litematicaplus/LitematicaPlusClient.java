package pl.litematicaplus;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.world.InteractionResult;

public class LitematicaPlusClient implements ClientModInitializer {
    public static final String MOD_ID = "litematica_plus";

    @Override
    public void onInitializeClient() {
        // Zapamiętujemy ostatnio kliknięty blok – to on zostanie otwarty jako kontener.
        UseBlockCallback.EVENT.register((player, level, hand, hit) -> {
            if (level.isClientSide()) {
                ContainerTracker.setLastClicked(hit.getBlockPos());
            }
            return InteractionResult.PASS;
        });

        // Wczytanie zapisanych zamian bloków
        BlockReplacements.load();

        // Komendy /lplus replace <from> <to> oraz /lplus clear
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            var toArg = ClientCommands.argument("to", StringArgumentType.word())
                    .executes(ctx -> LplusCommand.replace(
                            StringArgumentType.getString(ctx, "from"),
                            StringArgumentType.getString(ctx, "to")));

            var fromArg = ClientCommands.argument("from", StringArgumentType.word())
                    .then(toArg);

            var replace = ClientCommands.literal("replace")
                    .then(fromArg);

            var clear = ClientCommands.literal("clear")
                    .executes(ctx -> LplusCommand.clear());

            dispatcher.register(ClientCommands.literal("lplus")
                    .then(replace)
                    .then(clear));
        });
    }
}