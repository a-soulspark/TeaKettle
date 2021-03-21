package soulspark.tea_kettle.common.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IGrabbable {
	ItemStack getGrabStack(BlockState state, World world, BlockPos pos);
	
	default void grab(BlockState state, World world, BlockPos pos) {
		world.removeBlock(pos, false);
	}
}
