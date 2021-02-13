package soulspark.tea_kettle.common.blocks;

import soulspark.tea_kettle.core.init.ModItems;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class TeaBushBlock extends BushBlock implements IGrowable {
	public static final int GROWN_AGE = 2;
	
	public static final IntegerProperty AGE = BlockStateProperties.AGE_0_2;
	private static final VoxelShape BUSHLING_SHAPE = Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 8.0D, 13.0D);
	private static final VoxelShape GROWING_SHAPE = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 14.0D, 14.0D);
	
	public TeaBushBlock(Properties properties) {
		super(properties);
	}
	
	@Override
	public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {
		return new ItemStack(ModItems.TEA_LEAF.get());
	}
	
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		switch (state.get(AGE)) {
			case 0: return BUSHLING_SHAPE;
			case 1: return GROWING_SHAPE;
			default: return super.getShape(state, worldIn, pos, context);
		}
	}
	
	/**
	 * Returns whether or not this block is of a type that needs random ticking. Called for ref-counting purposes by
	 * ExtendedBlockStorage in order to broadly cull a chunk from the random chunk update list for efficiency's sake.
	 */
	public boolean ticksRandomly(BlockState state) {
		return state.get(AGE) < GROWN_AGE;
	}
	
	/**
	 * Performs a random tick on a block.
	 */
	public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
		int i = state.get(AGE);
		if (i < GROWN_AGE && worldIn.getLightSubtracted(pos.up(), 0) >= 9 && net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, pos, state,random.nextInt(5) == 0)) {
			worldIn.setBlockState(pos, state.with(AGE, i + 1), 2);
			net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state);
		}
		
	}
	
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		int i = state.get(AGE);
		
		if (i < GROWN_AGE && player.getHeldItem(handIn).getItem() == Items.BONE_MEAL) return ActionResultType.PASS;
		else if (i > 1) {
			spawnAsEntity(worldIn, pos, new ItemStack(ModItems.TEA_LEAF.get(), 1 + worldIn.rand.nextInt(2)));
			worldIn.playSound(null, pos, SoundEvents.BLOCK_VINE_STEP, SoundCategory.BLOCKS, 1.0F, 0.8F + worldIn.rand.nextFloat() * 0.4F);
			worldIn.setBlockState(pos, state.with(AGE, 1), 2);
			return ActionResultType.func_233537_a_(worldIn.isRemote);
		} else return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
	}
	
	/**
	 * Whether this IGrowable can grow
	 */
	public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) {
		return state.get(AGE) < GROWN_AGE;
	}
	
	@Override
	public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
		return true;
	}
	
	public void grow(ServerWorld worldIn, Random rand, BlockPos pos, BlockState state) {
		int i = Math.min(GROWN_AGE, state.get(AGE) + 1);
		worldIn.setBlockState(pos, state.with(AGE, i), 2);
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(AGE);
	}
}
