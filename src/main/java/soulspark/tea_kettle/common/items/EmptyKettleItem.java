package soulspark.tea_kettle.common.items;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import soulspark.tea_kettle.common.blocks.KettleBlock;
import soulspark.tea_kettle.core.init.ModItems;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EmptyKettleItem extends KettleItem {
	public EmptyKettleItem(KettleBlock block, Properties builder) {
		super(block, builder);
	}
	
	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		if (context.getPlayer() != null) {
			ActionResult<ItemStack> result = tryCollectWater(context.getWorld(), context.getPlayer(), context.getHand());
			if (result.getType().isSuccessOrConsume()) {
				ItemStack stack = context.getItem();
				ItemStack returnStack = result.getResult();
				PlayerEntity player = context.getPlayer();
				
				returnStack = fillKettle(stack.copy(), player, returnStack);
				player.setHeldItem(context.getHand(), returnStack);
				
				return result.getType();
			}
		}
		
		return super.onItemUse(context);
	}
	
	@Override
	public ActionResultType itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {
		if (target instanceof CowEntity) {
			playerIn.playSound(SoundEvents.ENTITY_COW_MILK, 1.0F, 1.0F);
			
			ItemStack returnStack = fillKettle(stack.copy(), playerIn, new ItemStack(ModItems.MILK_KETTLE.get()));
			playerIn.setHeldItem(hand, returnStack);
			
			return ActionResultType.func_233537_a_(playerIn.world.isRemote);
		}
		
		return ActionResultType.PASS;
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		ActionResult<ItemStack> result = tryCollectWater(worldIn, playerIn, handIn);
		if (result.getType().isSuccessOrConsume()) {
			ItemStack stack = playerIn.getHeldItem(handIn);
			ItemStack filledStack = result.getResult();
			
			return new ActionResult<>(result.getType(), fillKettle(stack.copy(), playerIn, filledStack));
		}
		return result;
	}
	
	protected ActionResult<ItemStack> tryCollectWater(World worldIn, PlayerEntity playerIn, Hand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);
		// if the kettle is empty, raytrace for fluids
		BlockRayTraceResult rayTrace = rayTrace(worldIn, playerIn, RayTraceContext.FluidMode.SOURCE_ONLY);
		// check if the raycast didn't miss nor hit an entity
		if (rayTrace.getType() == RayTraceResult.Type.BLOCK) {
			BlockPos pos = rayTrace.getPos();
			// i think this prevents protected blocks from being modified. not sure tho
			if (!worldIn.isBlockModifiable(playerIn, pos)) return ActionResult.resultPass(stack);
			
			// only fill this up with water fluids
			if (worldIn.getFluidState(pos).isTagged(FluidTags.WATER)) {
				// creates a copy of the kettle stack, now with water
				ItemStack filledStack = new ItemStack(ModItems.WATER_KETTLE.get());
				worldIn.playSound(playerIn, playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(), SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);
				return ActionResult.func_233538_a_(filledStack, worldIn.isRemote);
			}
		}
		
		return ActionResult.resultPass(stack);
	}
	
	protected ItemStack fillKettle(ItemStack emptyStack, PlayerEntity player, ItemStack stack) {
		player.addStat(Stats.ITEM_USED.get(this));
		return DrinkHelper.fill(emptyStack, player, stack);
	}
	
/*
	TODO:
	 > the brewing of kool tea
	  >> how steam doe??
*/
	
	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
		return new FluidHandlerItemStack(stack, 1000) {
			@Override
			public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
				return stack.getFluid() == Fluids.WATER;
			}
			
			@Override
			public int fill(FluidStack resource, FluidAction doFill) {
				if (resource.getAmount() >= 1000) {
					super.fill(resource, doFill);
					container = new ItemStack(ModItems.WATER_KETTLE.get());
					return 1000;
				}
				return 0;
			}
		};
	}
}
