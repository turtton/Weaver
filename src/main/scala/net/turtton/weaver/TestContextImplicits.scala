package net.turtton.weaver

import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.test.{GameTestException, TestContext}
import net.minecraft.util.math.BlockPos

object TestContextImplicits {
  implicit class Impl(context: TestContext) {
    def createMockServerPlayer(relativePos: BlockPos = BlockPos(0, 1, 0)): ServerPlayerEntity = TestContextExtensions.createMockServerPlayer(context, relativePos)
  }
  
  def fail(message: String): Unit = {
    throw GameTestException(message)
  }
}
