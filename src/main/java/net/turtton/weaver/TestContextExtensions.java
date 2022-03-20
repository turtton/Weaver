package net.turtton.weaver;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;

import java.util.UUID;

public class TestContextExtensions {
    public static ServerPlayerEntity createMockServerPlayer(TestContext context, BlockPos relativePos) {
        return new ServerPlayerEntity(context.getWorld().getServer(), context.getWorld(), new GameProfile(UUID.randomUUID(), "test-mock-server-player")) {

            {
                networkHandler = new ServerPlayNetworkHandler(context.getWorld().getServer(), new ClientConnection(NetworkSide.SERVERBOUND), this);
                refreshPositionAndAngles(context.getAbsolutePos(relativePos), 0f, 0f);
                changeGameMode(GameMode.CREATIVE);
            }
        };
    }
}
