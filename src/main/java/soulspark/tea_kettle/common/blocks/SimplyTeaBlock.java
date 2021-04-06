package soulspark.tea_kettle.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class SimplyTeaBlock extends ExternalTeaBlock {
	public static final IntegerProperty FULLNESS = IntegerProperty.create("fullness", 1, 2);
	
	public SimplyTeaBlock(ResourceLocation itemName, Properties properties) {
		super(itemName, properties);
		setDefaultState(getDefaultState().with(FULLNESS, 2));
	}
	
	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockState defaultState = super.getStateForPlacement(context);
		if (defaultState == null) return null;
		
		ItemStack stack = context.getItem();
		int fullness = 2 - stack.getOrCreateTag().getInt("Damage") % 2;
		return defaultState.with(FULLNESS, fullness);
	}
	
	@Override
	public ItemStack getGrabStack(BlockState state, World world, BlockPos pos) {
		ItemStack teaStack = super.getGrabStack(state, world, pos);
		teaStack.getOrCreateTag().putInt("Damage", state.get(FULLNESS) % 2);
		return teaStack;
	}
	
	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
		List<ItemStack> drops = super.getDrops(state, builder);
		for (ItemStack drop : drops) {
			CompoundNBT tag = drop.getOrCreateTag();
			tag.putInt("Damage", state.get(FULLNESS) % 2);
			drop.setTag(tag);
		}
		return drops;
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(FULLNESS);
	}
}
