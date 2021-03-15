package soulspark.tea_kettle.common.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import soulspark.tea_kettle.common.blocks.KettleBlock;
import soulspark.tea_kettle.core.init.ModItems;

public class EmptyKettleItem extends KettleItem {
	public EmptyKettleItem(KettleBlock block, Properties builder) {
		super(block, builder);
	}
	
	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		if (context.getPlayer() != null) {
			ActionResult<ItemStack> result = tryCollectWater(context.getWorld(), context.getPlayer(), context.getHand());
			if (result.getType().isSuccessOrConsume()) {
				context.getPlayer().setHeldItem(context.getHand(), result.getResult());
				return result.getType();
			}
		}
		
		return super.onItemUse(context);
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		return tryCollectWater(worldIn, playerIn, handIn);
	}
	
	protected ActionResult<ItemStack> tryCollectWater(World worldIn, PlayerEntity playerIn, Hand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);
		// if the kettle is empty, raytrace for fluids
		BlockRayTraceResult rayTrace = rayTrace(worldIn, playerIn, RayTraceContext.FluidMode.SOURCE_ONLY);
		// check if the raycast didn't miss nor hit an entity
		// Kettle.LOGGER.info("Hoopity dew! Take that: {} {}", rayTrace.getType(), rayTrace.getPos());
		if (rayTrace.getType() == RayTraceResult.Type.BLOCK) {
			BlockPos pos = rayTrace.getPos();
			// i think this prevents protected blocks from being modified. not sure tho
			if (!worldIn.isBlockModifiable(playerIn, pos)) return ActionResult.resultPass(stack);
			
			// only fill this up with water fluids
			if (worldIn.getFluidState(pos).isTagged(FluidTags.WATER)) {
				// creates a copy of the kettle stack, now with water
				ItemStack filledStack = new ItemStack(ModItems.WATER_KETTLE.get());
				
				worldIn.playSound(playerIn, playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(), SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);
				return ActionResult.func_233538_a_(turnBottleIntoItem(playerIn, filledStack), worldIn.isRemote);
			}
		}
		
		return ActionResult.resultPass(stack);
	}
	
	protected ItemStack turnBottleIntoItem(PlayerEntity player, ItemStack stack) {
		player.addStat(Stats.ITEM_USED.get(this));
		return stack;
	}
}
