package cn.mccraft.pangu.core.util.function.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

@FunctionalInterface
public interface BlockCanCreatureSpawnCallback {
  boolean apply(IBlockState arg0, IBlockAccess arg1, BlockPos arg2,
      EntityLiving.SpawnPlacementType arg3);
}
