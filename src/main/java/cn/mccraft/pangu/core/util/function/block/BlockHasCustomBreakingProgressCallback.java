package cn.mccraft.pangu.core.util.function.block;

import net.minecraft.block.state.IBlockState;

@FunctionalInterface
public interface BlockHasCustomBreakingProgressCallback {
  boolean apply(IBlockState arg0);
}
