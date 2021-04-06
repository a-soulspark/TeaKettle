package soulspark.tea_kettle.common.items;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import soulspark.tea_kettle.core.init.ModItems;
import soulspark.tea_kettle.core.util.TeaKettleUtils;

import javax.annotation.Nullable;
import java.util.List;

public class TeaItem extends BlockItem {
	public static double sweetnessFactor = 1;
	
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
	// only place tea if you're sneaking
	public ActionResultType tryPlace(BlockItemUseContext context) {
		if (context.getPlayer() != null && !context.getPlayer().isSneaking()) return ActionResultType.FAIL;
		return super.tryPlace(context);
	}
	
	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
		if (!worldIn.isRemote) sweetnessFactor = 1 + Math.max(0, TeaKettleUtils.getSweetness(stack) * 2 - 0.5);
		super.onItemUseFinish(stack, worldIn, entityLiving);
		sweetnessFactor = 1;
		
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
	
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		switch ((int)(TeaKettleUtils.getSweetness(stack) * 2)) {
			case 1:
				tooltip.add(new TranslationTextComponent("tea_kettle.tea_sweetened_weak").modifyStyle(style -> style.forceFormatting(TextFormatting.GRAY)));
				break;
			case 2:
				tooltip.add(new TranslationTextComponent("tea_kettle.tea_sweetened_strong").modifyStyle(style -> style.forceFormatting(TextFormatting.GRAY)));
		}
	}
}
