package soulspark.tea_kettle.common.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import soulspark.tea_kettle.core.init.ModBlocks;

public class EmptyKettleBlock extends KettleBlock {
	public EmptyKettleBlock(Properties builder) {
		super(builder);
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		ActionResultType result = super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
		
		if (result == ActionResultType.PASS) {
			ItemStack itemStack = player.getHeldItem(handIn);
			Item item = itemStack.getItem();
			if (item == Items.POTION && PotionUtils.getPotionFromItem(itemStack) == Potions.WATER) { // if this is a water bottle...
				if (!worldIn.isRemote) {
					if (!player.abilities.isCreativeMode) {
						// replace the hand with a glass bottle, water bottles aren't stackable after all
						player.setHeldItem(handIn, new ItemStack(Items.GLASS_BOTTLE));
						if (player instanceof ServerPlayerEntity) ((ServerPlayerEntity) player).sendContainerToPlayer(player.container);
					}
					
					// update the kettle to be filled with water
					worldIn.setBlockState(pos, ModBlocks.WATER_KETTLE.get().getDefaultState().with(FACING, state.get(FACING)));
					worldIn.playSound(null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
				}
				
				// ugly ass function that makes the event succeed if this is a *logical client*
				return ActionResultType.func_233537_a_(worldIn.isRemote);
			} else if (item == Items.MILK_BUCKET) { // if this is a water bottle...
				if (!worldIn.isRemote) {
					if (!player.abilities.isCreativeMode) {
						// replace the hand with a glass bottle, water bottles aren't stackable after all
						player.setHeldItem(handIn, new ItemStack(Items.BUCKET));
						if (player instanceof ServerPlayerEntity) ((ServerPlayerEntity) player).sendContainerToPlayer(player.container);
					}
					
					// update the kettle to be filled with water
					worldIn.setBlockState(pos, ModBlocks.MILK_KETTLE.get().getDefaultState().with(FACING, state.get(FACING)));
					worldIn.playSound(null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
				}
				
				// ugly ass function that makes the event succeed if this is a *logical client*
				return ActionResultType.func_233537_a_(worldIn.isRemote);
			}
		}
		
		return result;
	}
}
