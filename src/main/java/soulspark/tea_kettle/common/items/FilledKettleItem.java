package soulspark.tea_kettle.common.items;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.CampfireTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import soulspark.tea_kettle.common.blocks.CampfireKettleBlock;
import soulspark.tea_kettle.common.blocks.CampfireKettleBlock.Content;
import soulspark.tea_kettle.common.blocks.FilledKettleBlock;
import soulspark.tea_kettle.common.blocks.KettleBlock;
import soulspark.tea_kettle.common.tile_entities.KettleTileEntity;
import soulspark.tea_kettle.core.init.ModBlocks;
import soulspark.tea_kettle.core.util.TeaKettleUtils;

import javax.annotation.Nullable;

public class FilledKettleItem extends KettleItem {
	public final boolean isHot;
	public final Content content;
	
	public FilledKettleItem(KettleBlock block, boolean isHot, Content content, Properties builder) {
		super(block, builder);
		this.isHot = isHot;
		this.content = content;
	}
	
	@Override
	public String getTranslationKey(ItemStack stack) {
		return super.getTranslationKey() + ((isHot) ? "_hot" : "");
	}
	
	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		if (isHot) super.fillItemGroup(group, items);
	}
	
	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		World world = context.getWorld();
		BlockState state = world.getBlockState(context.getPos());
		
		// placing kettle on a campfire
		if (context.getFace() == Direction.UP && ModBlocks.CAMPFIRE_KETTLES.containsKey(state.getBlock()) && tryReplace(context)) return ActionResultType.func_233537_a_(world.isRemote);
		return super.onItemUse(context);
	}
	
	public boolean tryReplace(ItemUseContext context) {
		BlockPos pos = context.getPos();
		World world = context.getWorld();
		PlayerEntity player = context.getPlayer();
		BlockState state = world.getBlockState(pos);
		
		if (!canPlace(new BlockItemUseContext(context), state)) return false;

		TileEntity tileEntity = world.getTileEntity(pos);
		if (tileEntity instanceof CampfireTileEntity) {
			NonNullList<ItemStack> items = ((CampfireTileEntity) tileEntity).getInventory();
			for (ItemStack stack : items) if (!stack.isEmpty()) return false;
		}
		
		// place a campfire w/ kettle block at the location, copying the FACING state from the previous state and HOT from the item stack used
		if (!world.isRemote) {
			world.setBlockState(pos, ModBlocks.CAMPFIRE_KETTLES.get(state.getBlock()).getDefaultState().with(KettleBlock.FACING, world.getBlockState(pos).get(CampfireBlock.FACING)).with(CampfireKettleBlock.HOT, isHot).with(CampfireKettleBlock.CONTENT, content).with(CampfireKettleBlock.FULLNESS, (int)(TeaKettleUtils.getFullness(context.getItem(), world, player) * 4)));
			KettleTileEntity kettleTileEntity = (KettleTileEntity) world.getTileEntity(pos);
			if (kettleTileEntity != null) kettleTileEntity.boilingTicks = context.getItem().getOrCreateChildTag("BlockEntityTag").getInt("BoilingTicks");
		}

		if (player != null && !player.abilities.isCreativeMode) player.setHeldItem(context.getHand(), ItemStack.EMPTY);
		
		world.playSound(player, pos.getX(), pos.getY(), pos.getZ(), SoundType.METAL.getPlaceSound(), SoundCategory.NEUTRAL, 1.0F, 1.0F);
		
		return true;
	}
	
	@Override
	public ItemStack getContainerItem(ItemStack stack) {
		CompoundNBT kettleTag = stack.getOrCreateTag();
		CompoundNBT blockStateTag = kettleTag.getCompound("BlockStateTag");
		
		// decrease the fullness of the kettle by one
		int fullness = blockStateTag.getInt("fullness");
		// if it's the creative inventory item (no block state tag)
		if (fullness == 0) fullness = 4;
		
		// if the kettle has no fullness, make it empty
		if (--fullness <= 0) return super.getContainerItem(stack);

		blockStateTag.putInt("fullness", fullness);
		kettleTag.put("BlockStateTag", blockStateTag);
		// finally, update the item's tag
		stack.setTag(kettleTag);
		return stack;
	}
	
	@Nullable
	@Override
	protected BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockState state = super.getStateForPlacement(context);
		if (state != null && state.getBlock() == getBlock()) return state.with(FilledKettleBlock.HOT, isHot);
		return state;
	}
}
