package soulspark.tea_kettle.common.items;

import net.minecraft.block.BlockState;
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
import soulspark.tea_kettle.core.util.KettleTags;

public class KettleItem extends BlockItem {
	public KettleItem(KettleBlock block, Properties builder) {
		super(block, builder);
	}
	
	@Override
	public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
		BlockState state = context.getWorld().getBlockState(context.getPos());
		
		if (context.getFace() == Direction.UP && (state.isIn(KettleTags.FURNACES) || state.getBlock() instanceof AbstractFurnaceBlock))
			return onItemUse(context);
		
		return super.onItemUseFirst(stack, context);
	}
}
