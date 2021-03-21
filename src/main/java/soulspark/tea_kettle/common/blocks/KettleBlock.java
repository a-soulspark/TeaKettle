package soulspark.tea_kettle.common.blocks;

import net.minecraft.block.*;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import soulspark.tea_kettle.core.init.ModItems;
import soulspark.tea_kettle.core.util.TeaKettleTags;

public class KettleBlock extends Block implements IGrabbable {
	protected static final VoxelShape SHAPE = Block.makeCuboidShape(4, 0, 4, 12, 6, 12);
	public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
	
	public KettleBlock(Properties builder) {
		super(builder);
		// gives the block a default state facing north, with empty content and unlit
		setDefaultState(getStateContainer().getBaseState().with(FACING, Direction.NORTH));
	}
	
	@Override
	// gives the block its custom shape
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return SHAPE;
	}
	
	@Override
	public PushReaction getPushReaction(BlockState state) {
		return PushReaction.DESTROY;
	}
	
	// returns true if the block underneath is in the "hot" tag or is an AbstractFurnaceBlock
	protected boolean isHot(IWorld world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);
		return state.isIn(TeaKettleTags.HOT) || ((state.isIn(TeaKettleTags.FURNACES) || state.getBlock() instanceof AbstractFurnaceBlock) && state.get(AbstractFurnaceBlock.LIT));
	}
	
	@Override
	// makes the block face to the right of the placing direction, and updates the lit state to match the block underneath
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
	}
	
	// returns true if the block below has a solid face on top (eg. is a Cube block or a fence)
	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
		BlockPos below = pos.down();
		return worldIn.getBlockState(below).isSolidSide(worldIn, below, Direction.UP);
	}
	
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		// if the block below changed, drop the kettle if it's air, or just update the "lit" aspect of it with the new block
		if (facing == Direction.DOWN) return (!stateIn.isValidPosition(worldIn, currentPos)) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
		// otherwise, change nothing
		return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		ItemStack itemStack = player.getHeldItem(handIn);
		
		// if you right-clicked the kettle with an empty hand... [must be mainhand, the offhand is inconsistent)
		if (itemStack.isEmpty() && handIn == Hand.MAIN_HAND) {
			if (!worldIn.isRemote) {// spawns in a new KettleItem stack and gives it the BlockEntityTag corresponding to this block's TileEntity
				ItemStack stack = getGrabStack(state, worldIn, pos);
				player.setHeldItem(handIn, stack);
				if (player instanceof ServerPlayerEntity)
					((ServerPlayerEntity) player).sendContainerToPlayer(player.container);
				
				// breaks the kettle block and sends the Kettle item straight to the player's inventory
				worldIn.removeBlock(pos, false);
			} else player.playSound(SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 1, 1);
			
			return ActionResultType.func_233537_a_(worldIn.isRemote);
		}
		
		return ActionResultType.PASS;
	}
	
	@Override
	public ItemStack getGrabStack(BlockState state, World worldIn, BlockPos pos) {
		return new ItemStack(ModItems.EMPTY_KETTLE.get());
	}
	
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}
	
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.toRotation(state.get(FACING)));
	}
	
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}
}
