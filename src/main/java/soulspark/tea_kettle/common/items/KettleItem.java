package soulspark.tea_kettle.common.items;

import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import soulspark.tea_kettle.common.blocks.KettleBlock;
import soulspark.tea_kettle.core.util.TeaKettleTags;

public class KettleItem extends BlockItem {
	public KettleItem(KettleBlock block, Properties builder) {
		super(block, builder);
	}
	
	@Override
	public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
		BlockState state = context.getWorld().getBlockState(context.getPos());
		
		if (context.getFace() == Direction.UP && (state.isIn(TeaKettleTags.FURNACES) || state.getBlock() instanceof AbstractFurnaceBlock))
			return onItemUse(context);
		
		return super.onItemUseFirst(stack, context);
	}
}
