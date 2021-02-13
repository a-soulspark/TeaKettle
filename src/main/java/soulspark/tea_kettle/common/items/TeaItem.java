package soulspark.tea_kettle.common.items;

import soulspark.tea_kettle.core.init.ModItems;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.*;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class TeaItem extends BlockItem {
	
	public TeaItem(Block blockIn, Properties builder) {
		super(blockIn, builder);
	}
	
	/**
	 * returns the action that specifies what animation to play when the items is being used
	 */
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.DRINK;
	}
	
	@Override
	/* changes the burp sound when finishing drinking */
	public SoundEvent getEatSound() {
		return SoundEvents.ENTITY_GENERIC_DRINK;
	}
	
	/**
	 * Called to trigger the item's "innate" right click behavior. To handle when this item is used on a Block, see
	 * {@link #onItemUse}.
	 */
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		return DrinkHelper.startDrinking(worldIn, playerIn, handIn);
	}
	
	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
		super.onItemUseFinish(stack, worldIn, entityLiving);
		
		PlayerEntity player = entityLiving instanceof PlayerEntity ? (PlayerEntity) entityLiving : null;
		// optional, good practice stuff vvv
		if (player instanceof ServerPlayerEntity) {
			CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayerEntity)player, stack);
			player.addStat(Stats.ITEM_USED.get(this));
		}
		
		if (player != null) {
			ItemStack cupStack = new ItemStack(ModItems.CUP.get());
			if (stack.isEmpty()) return cupStack;
			else if (!player.abilities.isCreativeMode && !player.inventory.addItemStackToInventory(cupStack))
				player.dropItem(cupStack, false);
		}
		
		return stack;
		
	}
}
