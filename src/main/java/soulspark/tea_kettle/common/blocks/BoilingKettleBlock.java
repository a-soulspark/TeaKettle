package soulspark.tea_kettle.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import soulspark.tea_kettle.common.tile_entities.BoilingKettleTileEntity;
import soulspark.tea_kettle.core.init.ModBlocks;
import soulspark.tea_kettle.core.init.ModItems;

import javax.annotation.Nullable;

public class BoilingKettleBlock extends KettleBlock {
	public static final IntegerProperty FULLNESS = IntegerProperty.create("fullness", 0, 2);
	
	public BoilingKettleBlock(Properties builder) {
		super(builder);
		setDefaultState(getDefaultState().with(FULLNESS, 2));
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new BoilingKettleTileEntity();
	}
	
	@Override
	protected ItemStack getPickUpItem(BlockState state, World worldIn, BlockPos pos) {
		ItemStack kettleStack = new ItemStack(ModItems.BOILING_KETTLE.get());
		// stores the "fullness" blockstate into the BlockStateTag nbt of the item
		CompoundNBT blockStates = new CompoundNBT();
		blockStates.putInt("fullness", state.get(FULLNESS));
		kettleStack.setTagInfo("BlockStateTag", blockStates);
		
		return kettleStack;
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(FULLNESS);
	}
}
