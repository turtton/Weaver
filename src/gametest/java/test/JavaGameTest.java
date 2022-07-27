package test;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameMode;
import net.turtton.weaver.TestContextExtensions;

public class JavaGameTest implements FabricGameTest {
    @GameTest(templateName = EMPTY_STRUCTURE)
    public void playerTest(TestContext context) {
        var serverPlayer = TestContextExtensions.createMockServerPlayer(context, BlockPos.ORIGIN);
        var targetPos = new BlockPos(0, 0, 1);
        context.setBlockState(targetPos, Blocks.STONE);
        serverPlayer.interactionManager.processBlockBreakingAction(context.getAbsolutePos(targetPos), PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, Direction.UP, context.getWorld().getTopY(), 1);

        context.expectBlock(Blocks.AIR, targetPos);
        context.complete();
    }
}
