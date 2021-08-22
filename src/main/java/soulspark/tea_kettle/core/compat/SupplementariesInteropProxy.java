/*
package soulspark.tea_kettle.core.compat;

import net.mehvahdjukaar.supplementaries.block.blocks.JarBlock;
import net.mehvahdjukaar.supplementaries.block.tiles.JarBlockTile;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DrinkHelper;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import soulspark.tea_kettle.core.init.ModItems;

public class SupplementariesInteropProxy implements InteropProxy {
	@Override
	public ActionResultType onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
		World world = event.getWorld();
		ItemStack heldStack = event.getItemStack();
		PlayerEntity player = event.getPlayer();
		BlockState state = world.getBlockState(event.getPos());
		
		if (heldStack.getItem() == ModItems.EMPTY_KETTLE.get() && state.getBlock() instanceof JarBlock) {
			JarBlockTile tileEntity = (JarBlockTile) world.getTileEntity(event.getPos());
			if (tileEntity != null && tileEntity.fluidHolder.canRemove(4)) {
				// give a water kettle and shrink the fluid holder's contents
				if (tileEntity.fluidHolder.getFluid().getFluid() == Fluids.WATER) {
					player.setHeldItem(event.getHand(), DrinkHelper.fill(heldStack.copy(), player, new ItemStack(ModItems.WATER_KETTLE.get())));
					world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1, 1);
				}
				// give a milk kettle and shrink the fluid holder's contents
				else if (tileEntity.fluidHolder.getFluid().getID().equals("supplementaries:milk")) {
					player.setHeldItem(event.getHand(), DrinkHelper.fill(heldStack.copy(), player, new ItemStack(ModItems.MILK_KETTLE.get())));
					world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ENTITY_COW_MILK, SoundCategory.BLOCKS, 1, 1);
				}
				else return ActionResultType.PASS;
				
				tileEntity.fluidHolder.shrink(4);
				return ActionResultType.func_233537_a_(world.isRemote);
			}
		}
		
		return ActionResultType.PASS;
	}
}
*/
