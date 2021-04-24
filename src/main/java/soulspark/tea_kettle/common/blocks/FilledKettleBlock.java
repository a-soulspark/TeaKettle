package soulspark.tea_kettle.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import soulspark.tea_kettle.common.items.FilledKettleItem;
import soulspark.tea_kettle.common.tile_entities.KettleTileEntity;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;

@SuppressWarnings("ConstantConditions")
public class FilledKettleBlock extends KettleBlock {
	public static final BooleanProperty LIT = BlockStateProperties.LIT;
	public static final BooleanProperty HOT = BooleanProperty.create("hot");
	public static final IntegerProperty FULLNESS = IntegerProperty.create("fullness", 1, 4);
	
	private final Function<Boolean, Item> itemSupplier;
	
	public FilledKettleBlock(Function<Boolean, Item> itemSupplier, Properties builder) {
		super(builder);
		this.itemSupplier = itemSupplier;
		
		setDefaultState(getDefaultState().with(HOT, false).with(LIT, false).with(FULLNESS, 4));
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return VoxelShapes.or(SHAPE, Block.makeCuboidShape(6.0D, 6.0D, 6.0D, 10.0D, 7.0D, 10.0D));
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new KettleTileEntity();
	}
	
	@Override
	// makes the block face to the right of the placing direction, and updates the lit state to match the block underneath
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return super.getStateForPlacement(context).with(LIT, isHot(context.getWorld(), context.getPos().down()));
	}
	
	@Override
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		BlockState state = super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
		if (state.getBlock() == this) return state.with(LIT, isHot(worldIn, facingPos));
		return state;
	}
	
	@Override
	public ItemStack getGrabStack(BlockState state, World worldIn, BlockPos pos) {
		ItemStack kettleStack = new ItemStack(itemSupplier.apply(state.get(HOT)));
		kettleStack.setTagInfo("BlockEntityTag", worldIn.getTileEntity(pos).serializeNBT());
		// stores the "fullness" blockstate into the BlockStateTag nbt of the item
		CompoundNBT blockStates = new CompoundNBT();
		blockStates.putInt("fullness", state.get(FULLNESS));
		kettleStack.setTagInfo("BlockStateTag", blockStates);
		
		return kettleStack;
	}
	
	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
		List<ItemStack> drops = super.getDrops(state, builder);
		for (ItemStack drop : drops) {
			if (drop.getItem() instanceof FilledKettleItem) {
				CompoundNBT tag = drop.getOrCreateTag();
				CompoundNBT blockStateTag = tag.getCompound("BlockStateTag");
				blockStateTag.putInt("fullness", state.get(FULLNESS));
				tag.put("BlockStateTag", blockStateTag);
			}
		}
		return drops;
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(LIT, HOT, FULLNESS);
	}
}
