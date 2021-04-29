package soulspark.tea_kettle.common.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public interface IGrabbable {
	ItemStack getGrabStack(BlockState state, World world, @Nullable TileEntity tileEntity);
	default ItemStack getGrabStack(BlockState state, World world, BlockPos pos) { return getGrabStack(state, world, world.getTileEntity(pos)); }
	
	default void grab(World world, BlockPos pos) {
		if (!world.isRemote) world.removeBlock(pos, false);
	}
}
