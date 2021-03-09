package soulspark.tea_kettle.common.blocks;

import soulspark.tea_kettle.common.tile_entities.KettleTileEntity;
import soulspark.tea_kettle.core.init.ModItems;
import soulspark.tea_kettle.core.util.KettleTags;
import net.minecraft.block.*;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.state.*;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class KettleBlock extends Block {
	public enum Content implements IStringSerializable {
		EMPTY("empty"), WATER("water"), HOT_WATER("hot_water");
		
		private final String name;
		Content(String nameIn) { name = nameIn; }
		
		@Override
		public String getString() {
			return name;
		}
	}
	
	protected static final VoxelShape SHAPE = Block.makeCuboidShape(4, 0, 4, 12, 6, 12);
	public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
	public static final EnumProperty<Content> CONTENT = EnumProperty.create("content", Content.class, Content.EMPTY, Content.WATER, Content.HOT_WATER);
	public static final IntegerProperty FULLNESS = IntegerProperty.create("fullness", 0, 2);
	public static final BooleanProperty LIT = BlockStateProperties.LIT;
	
	public KettleBlock(Properties builder) {
		super(builder);
		// gives the block a default state facing north, with empty content and unlit
		setDefaultState(getStateContainer().getBaseState().with(FACING, Direction.NORTH).with(CONTENT, Content.EMPTY).with(FULLNESS, 0).with(LIT, false));
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
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new KettleTileEntity();
	}
	
	// returns true if the block underneath is in the "hot" tag or is an AbstractFurnaceBlock
	protected boolean isHot(IWorld world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);
		return state.isIn(KettleTags.HOT) || ((state.isIn(KettleTags.FURNACES) || state.getBlock() instanceof AbstractFurnaceBlock) && state.get(AbstractFurnaceBlock.LIT));
	}
	
	@Nullable
	@Override
	// makes the block face to the right of the placing direction, and updates the lit state to match the block underneath
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite()).with(LIT, isHot(context.getWorld(), context.getPos().down()));
	}
	
	// returns true if the block below has a solid face on top (eg. is a Cube block or a fence)
	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
		BlockPos below = pos.down();
		return worldIn.getBlockState(below).isSolidSide(worldIn, below, Direction.UP);
	}
	
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		// if the block below changed, drop the kettle if it's air, or just update the "lit" aspect of it with the new block
		if (facing == Direction.DOWN) return (!stateIn.isValidPosition(worldIn, currentPos)) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos).with(LIT, isHot(worldIn, facingPos));
		// otherwise, change nothing
		return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		ItemStack itemStack = player.getHeldItem(handIn);
		TileEntity tileEntity = worldIn.getTileEntity(pos);
		
		if (tileEntity instanceof KettleTileEntity) {
			// if you right-clicked the kettle with an empty hand... [must be mainhand, the offhand is inconsistent)
			if (itemStack.isEmpty() && handIn == Hand.MAIN_HAND) {
				// spawns in a new KettleItem stack and gives it the BlockEntityTag corresponding to this block's TileEntity
				ItemStack kettleStack = new ItemStack(ModItems.KETTLE.get());
				kettleStack.setTagInfo("BlockEntityTag", tileEntity.serializeNBT());
				
				// stores the "content" blockstate into the BlockStateTag nbt of the item
				CompoundNBT blockStates = new CompoundNBT();
				blockStates.putString("content", state.get(CONTENT).name);
				blockStates.putInt("fullness", state.get(FULLNESS));
				kettleStack.setTagInfo("BlockStateTag", blockStates);
				
				// breaks the kettle block and sends the Kettle item straight to the player's inventory
				worldIn.removeBlock(pos, false);
				player.setHeldItem(handIn, kettleStack);
				
				return ActionResultType.func_233537_a_(worldIn.isRemote);
			} else {
				Item item = itemStack.getItem();
				if (item == Items.POTION && PotionUtils.getPotionFromItem(itemStack) == Potions.WATER) { // if this is a water bottle...
					// if the kettle is empty, fill it up
					if (state.get(CONTENT) == Content.EMPTY && !worldIn.isRemote) {
						if (!player.abilities.isCreativeMode) {
							// replace the hand with a glass bottle, water bottles aren't stackable after all
							player.setHeldItem(handIn, new ItemStack(Items.GLASS_BOTTLE));
							if (player instanceof ServerPlayerEntity)
								((ServerPlayerEntity) player).sendContainerToPlayer(player.container);
						}
						
						// update the kettle to be filled with water
						worldIn.setBlockState(pos, state.with(CONTENT, Content.WATER).with(FULLNESS, 2));
						worldIn.playSound(null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
					}
					
					// ugly ass function that makes the event succeed if this is a *logical client*
					return ActionResultType.func_233537_a_(worldIn.isRemote);
				} else if (item == Items.GLASS_BOTTLE) {
					// if there's water in the kettle, try filling the bottle with it
					if (state.get(CONTENT) != Content.EMPTY && !worldIn.isRemote) {
						if (!player.abilities.isCreativeMode) {
							ItemStack newStack = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), Potions.WATER);
							itemStack.shrink(1);
							
							// if that was the last bottle in the stack, replace your hand with the bottle (think Dolphin Milk)
							// if there are more bottles, try adding to your inventory. if there's no room, I threw it on the ground!!
							if (itemStack.isEmpty()) player.setHeldItem(handIn, newStack);
							else if (!player.addItemStackToInventory(newStack)) player.dropItem(itemStack, false);
							
							// some server updating crap. idk what this does, but it's in Mojang's code, should be important.
							if (player instanceof ServerPlayerEntity)
								((ServerPlayerEntity) player).sendContainerToPlayer(player.container);
						}
						
						// update the kettle to be empty
						worldIn.setBlockState(pos, state.with(CONTENT, Content.EMPTY).with(FULLNESS, 0));
						((KettleTileEntity) tileEntity).resetBoilingTicks();
						worldIn.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
					}
					
					// see above ^^^
					return ActionResultType.func_233537_a_(worldIn.isRemote);
				}
			}
		}
		
		return ActionResultType.PASS;
	}
	
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}
	
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.toRotation(state.get(FACING)));
	}
	
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING, CONTENT, FULLNESS, LIT);
	}
}
