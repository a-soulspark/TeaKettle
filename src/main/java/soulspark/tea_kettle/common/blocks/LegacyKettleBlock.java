package soulspark.tea_kettle.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockReader;
import soulspark.tea_kettle.common.tile_entities.LegacyKettleTileEntity;

import javax.annotation.Nullable;

public class LegacyKettleBlock extends Block {
	public enum Content implements IStringSerializable {
		EMPTY("empty"), WATER("water"), HOT_WATER("hot_water");
		
		private final String name;
		Content(String nameIn) { name = nameIn; }
		
		@Override
		public String getString() {
			return name;
		}
	}
	
	public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
	public static final EnumProperty<Content> CONTENT = EnumProperty.create("content", Content.class, Content.EMPTY, Content.WATER, Content.HOT_WATER);
	public static final IntegerProperty FULLNESS = IntegerProperty.create("fullness", 0, 2);
	
	public LegacyKettleBlock(Properties properties) {
		super(properties);
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new LegacyKettleTileEntity();
	}
	
	/*
	@Override
	public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
		BlockState upgradedState;
		
		switch (state.get(LegacyKettleBlock.CONTENT)) {
			case EMPTY:
				upgradedState = ModBlocks.EMPTY_KETTLE.get().getDefaultState();
				break;
			case WATER:
				upgradedState = ModBlocks.WATER_KETTLE.get().getDefaultState();
				break;
			default:
				upgradedState = ModBlocks.WATER_KETTLE.get().getDefaultState().with(FilledKettleBlock.HOT, true);
				break;
		}
		
		worldIn.setBlockState(pos, upgradedState.with(KettleBlock.FACING, state.get(LegacyKettleBlock.FACING)));
	}
	*/
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING, CONTENT, FULLNESS, FilledKettleBlock.LIT);
	}
}
