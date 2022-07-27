package test

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest
import net.minecraft.block.Blocks
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket
import net.minecraft.test.{GameTest, TestContext}
import net.minecraft.util.math.{BlockPos, Direction}
import net.turtton.weaver.TestContextImplicits.*

class ScalaGameTest extends FabricGameTest {
  @GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
  def playerTest(context: TestContext): Unit = {
    val serverPlayer = context.createMockServerPlayer()
    val targetPos = BlockPos(0, 0, 1)
    context.setBlockState(targetPos, Blocks.STONE)
    serverPlayer.interactionManager.processBlockBreakingAction(context.getAbsolutePos(targetPos), PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, Direction.UP, context.getWorld.getTopY, 1)
    
    context.expectBlock(Blocks.AIR, targetPos)
    context.complete()
  }
}
