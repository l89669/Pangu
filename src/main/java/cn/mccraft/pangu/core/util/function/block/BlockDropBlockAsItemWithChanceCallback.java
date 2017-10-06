package cn.mccraft.pangu.core.util.function.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@FunctionalInterface
public interface BlockDropBlockAsItemWithChanceCallback {
  void apply(World arg0, BlockPos arg1, IBlockState arg2, float arg3, int arg4);
}
