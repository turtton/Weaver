package net.turtton.weaver.mixin;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.TestCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(TestCommand.class)
public class MixinTestCommand {

    /**
     * Disable TestCommand
     * @author turtton
     * @reason This command causes serializing error and sends "minecraft:"(unknown type data) to client. Some custom clients cannot ignore this error...
     */
    @Overwrite
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {}
}
