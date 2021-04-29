package soulspark.tea_kettle.core.compat;

import com.minecraftabnormals.neapolitan.core.registry.NeapolitanItems;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import soulspark.tea_kettle.common.blocks.KettleBlock;
import soulspark.tea_kettle.core.init.ModBlocks;

public class NeapolitanInteropProxy implements InteropProxy {
	@Override
	public ActionResultType onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
		BlockPos pos = event.getPos();
		World world = event.getWorld();
		PlayerEntity player = event.getPlayer();
		ItemStack heldStack = event.getItemStack();
		BlockState state = world.getBlockState(event.getPos());
		
		if (state.getBlock() == ModBlocks.EMPTY_KETTLE.get() && heldStack.getItem() == NeapolitanItems.MILK_BOTTLE.get()) {
			if (!world.isRemote) {
				if (!player.abilities.isCreativeMode) {
					ItemStack resultStack = heldStack.getContainerItem();
					heldStack.shrink(1);
					if (heldStack.isEmpty()) player.setHeldItem(event.getHand(), resultStack);
					else if (!player.addItemStackToInventory(resultStack)) player.dropItem(resultStack, false);
				}
				world.setBlockState(event.getPos(), ModBlocks.MILK_KETTLE.get().getDefaultState().with(KettleBlock.FACING, state.get(KettleBlock.FACING)));
			}
			world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1, 1);
			return ActionResultType.func_233537_a_(world.isRemote);
		}
		
		return ActionResultType.PASS;
	}
}
