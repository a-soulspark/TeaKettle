package soulspark.tea_kettle.core.compat;

import net.blay09.mods.cookingforblockheads.block.ModBlocks;
import net.blay09.mods.cookingforblockheads.tile.MilkJarTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DrinkHelper;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fluids.capability.IFluidHandler;
import soulspark.tea_kettle.core.init.ModItems;

public class CFBInteropProxy implements InteropProxy {
	@Override
	public ActionResultType onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
		World world = event.getWorld();
		ItemStack heldStack = event.getItemStack();
		PlayerEntity player = event.getPlayer();
		BlockState state = world.getBlockState(event.getPos());

		if (heldStack.getItem() == ModItems.EMPTY_KETTLE.get() && state.isIn(ModBlocks.milkJar)) {
			MilkJarTileEntity tileEntity = (MilkJarTileEntity) world.getTileEntity(event.getPos());
			if (tileEntity != null) {
				if (tileEntity.getMilkAmount() >= 1000.0F) {
					tileEntity.drain(1000, IFluidHandler.FluidAction.EXECUTE);
					player.setHeldItem(event.getHand(), DrinkHelper.fill(heldStack.copy(), player, new ItemStack(ModItems.MILK_KETTLE.get())));
					world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ENTITY_COW_MILK, SoundCategory.BLOCKS, 1, 1);
				}
				
				return ActionResultType.func_233537_a_(world.isRemote);
			}
		}
		
		return ActionResultType.PASS;
	}
}
