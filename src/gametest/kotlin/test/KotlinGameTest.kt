package test

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest.EMPTY_STRUCTURE
import net.minecraft.block.Blocks
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket
import net.minecraft.test.GameTest
import net.minecraft.test.TestContext
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.turtton.weaver.createMockServerPlayer

@Suppress("UNUSED")
class KotlinGameTest : FabricGameTest {
    @GameTest(templateName = EMPTY_STRUCTURE)
    fun playerTest(context: TestContext) {
        val serverPlayer = context.createMockServerPlayer()
        val targetPos = BlockPos(0.0, 0.0, 1.0)
        context.setBlockState(targetPos, Blocks.STONE)
        serverPlayer.interactionManager.processBlockBreakingAction(context.getAbsolutePos(targetPos), PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, Direction.UP, context.world.topY, 1)

        context.expectBlock(Blocks.AIR, targetPos)
        context.complete()
    }
}
