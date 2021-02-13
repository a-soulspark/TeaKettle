package soulspark.tea_kettle.common.items;

import soulspark.tea_kettle.common.blocks.KettleBlock;
import soulspark.tea_kettle.TeaKettle;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class KettleItem extends BlockItem {
	public KettleItem(KettleBlock block, Properties builder) {
		super(block, builder);
	}
	
	@SuppressWarnings("ConstantConditions")
	@Override
	public String getTranslationKey(ItemStack stack) {
		if (stack.hasTag()) {
			CompoundNBT tag = stack.getTag();
			String content = tag.getCompound("BlockStateTag").getString("content");
			String defaultKey = super.getTranslationKey(stack);
			
			switch (content) {
				case "hot_water": return defaultKey + "_hot_water";
				case "water": return defaultKey + "_water";
				default: return defaultKey;
			}
		}
		
		return super.getTranslationKey(stack);
	}
	
	@Override
	public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
		if (context.getFace() == Direction.UP && context.getWorld().getBlockState(context.getPos()).getBlock() instanceof AbstractFurnaceBlock)
			return onItemUse(context);
		
		return super.onItemUseFirst(stack, context);
	}
	
	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		if (context.getPlayer() != null) {
			ActionResult<ItemStack> result = tryCollectWater(context.getWorld(), context.getPlayer(), context.getHand());
			if (result.getType().isSuccessOrConsume()) {
				context.getPlayer().setHeldItem(context.getHand(), result.getResult());
				return result.getType();
			}
//			Kettle.LOGGER.info("Hoopity doo! I got this: {}", getContentFromStack(context.getItem()));
//			Kettle.LOGGER.info("Hoopity dew! Take that: {} {}", rayTrace.getType(), rayTrace.getPos());
		}
		
		return super.onItemUse(context);
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		// xTODO> fix the desync (again)
		//  so, turns out the desync is a problem even in vanilla MC
		//  if not even Mojang can fix this thing, why would I?
		
		// xTODO> add different names to the different kettle states
		// xTODO> duplicate cup of tea in creative inventory (one of them is supposed to be a cup)
		//  prob bc they're BlockItems for the same block
		// xTODO: fix kettle not filling up in creative
		//  P.S.: it seems to have to do with DrinkHelper.fill. it's what's used for buckets and bottles
		//  prob doesn't fit this very well tho
		
		/* Final Tasks */
		// xTODO> add recipes to all items
		// xTODO> add more types of tea
		//  idk which ones, gotta do my research amirite
		// xTODO> improve kettle textures (block don't match item. tweak either of them lol)
		
		// xTODO> empty kettle by right clicking on the air (DEBUG)
		
		return tryCollectWater(worldIn, playerIn, handIn);
	}
	
	protected ActionResult<ItemStack> tryCollectWater(World worldIn, PlayerEntity playerIn, Hand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);
		// if the kettle is empty, raytrace for fluids
		if (getContentFromStack(stack).equals("empty")) {
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
					ItemStack filledStack = stack.copy();
					
					CompoundNBT tag = filledStack.getOrCreateTag();
					CompoundNBT blockStateTag = tag.getCompound("BlockStateTag");
					blockStateTag.putString("content", "water");
					blockStateTag.putInt("fullness", 2);
					tag.put("BlockStateTag", blockStateTag);
					filledStack.setTag(tag);
					
					// Kettle.LOGGER.info("Looby hoopy! Look at this! {} {}", rayTrace.getType(), rayTrace.getPos());
					
					worldIn.playSound(playerIn, playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(), SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);
					return ActionResult.func_233538_a_(turnBottleIntoItem(stack, playerIn, filledStack), worldIn.isRemote);
				}
			}
		}
		
		return ActionResult.resultPass(stack);
	}
	
	public static String getContentFromStack(ItemStack stack) {
		CompoundNBT tag = stack.getTag();
		if (tag == null) return "empty"; // has no tag; return empty as it's the default state
		String content = tag.getCompound("BlockStateTag").getString("content");
		if (content.equals("hot_water") && tag.getCompound("BlockStateTag").getInt("fullness") > 1) content += "_full";
		
		return content.isEmpty() ? "empty" : content; // if there's no BlockStateTag nbt in the stack, return the default state ("empty")
	}
	
	protected ItemStack turnBottleIntoItem(ItemStack bottleStack, PlayerEntity player, ItemStack stack) {
		player.addStat(Stats.ITEM_USED.get(this));
		return stack;//DrinkHelper.fill(bottleStack, player, stack);
	}
}
