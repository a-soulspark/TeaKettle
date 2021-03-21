package soulspark.tea_kettle.common.items;

import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import soulspark.tea_kettle.common.blocks.FilledKettleBlock;
import soulspark.tea_kettle.common.blocks.KettleBlock;

import javax.annotation.Nullable;

public class FilledKettleItem extends KettleItem {
	protected final boolean isHot;
	
	public FilledKettleItem(KettleBlock block, boolean isHot, Properties builder) {
		super(block, builder);
		this.isHot = isHot;
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
