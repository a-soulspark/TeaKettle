package com.soulspark.kettle.common.blocks;

import com.soulspark.kettle.common.items.KettleItem;
import com.soulspark.kettle.common.tile_entities.CupTileEntity;
import com.soulspark.kettle.core.init.ModItems;
import com.soulspark.kettle.core.util.KettleTags;
import com.soulspark.kettle.core.util.TeaSteepRecipes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
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
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.List;

public class CupBlock extends Block {
	protected static final VoxelShape SHAPE = Block.makeCuboidShape(5.0D, 0.0D, 5.0D, 11.0D, 5.0D, 11.0D);
	public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
	
	public CupBlock(Properties properties) {
		super(properties);
		setDefaultState(getStateContainer().getBaseState().with(FACING, Direction.NORTH));
	}
	
	@Override
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
		return new CupTileEntity();
	}
	
	@Nullable
	@Override
	// makes the block face to the right of the placing direction
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
	}
	
	// limits the placement to solid top faces only
	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
		BlockPos below = pos.down();
		return worldIn.getBlockState(below).isSolidSide(worldIn, below, Direction.UP);
	}
	
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		// drop the cup if the block underneath it was broken
		return facing == Direction.DOWN && !stateIn.isValidPosition(worldIn, currentPos) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		TileEntity tileEntity = worldIn.getTileEntity(pos);
		
		if (tileEntity instanceof CupTileEntity) {
			// gets the cup tile entity version of the tile entity
			CupTileEntity cupTileEntity = (CupTileEntity) tileEntity;
			ItemStack stack = player.getHeldItem(handIn);
			boolean handEmpty = stack.isEmpty();
			
			// if main hand is empty, put the cup in it
			if (handEmpty || stack.getItem() == ModItems.CUP.get()) {
				// don't want anything to do with an empty offhand
				if (handIn == Hand.MAIN_HAND) {
					// if the cup is empty, put it in your hand
					if (cupTileEntity.handler.isEmpty()) {
						// if the world is remote, skip all of it and just say "success"
						if (!worldIn.isRemote) {
							List<ItemStack> drops = getDrops(state, (ServerWorld) worldIn, pos, tileEntity);
							if (!drops.isEmpty()) {
								ItemStack drop = drops.get(0);
								// if the cup is the same as the cup you're holding, increase the stack by 1
								// if it isn't, try adding the cup to the inventory if there's room for it
								if (stack.isEmpty() || stack.getItem() == drop.getItem()) {
									drop.grow(stack.getCount());
									player.setHeldItem(handIn, drop);
								}
								else if (!player.addItemStackToInventory(drop)) return ActionResultType.FAIL;
								// send the update to clients and remove the block
								((ServerPlayerEntity) player).sendContainerToPlayer(player.container);
								worldIn.removeBlock(pos, false);
							}
						}
						else player.playSound(SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 1, 1);
					} else {
						// gets the item in the cup and gives it to the player
						ItemStack inStack = cupTileEntity.handler.getStack();
						if (handEmpty) player.setHeldItem(handIn, inStack);
						else player.addItemStackToInventory(inStack);
						
						if (player instanceof ServerPlayerEntity) ((ServerPlayerEntity) player).sendContainerToPlayer(player.container);
						// removes the item from the cup
						cupTileEntity.handler.setStack(ItemStack.EMPTY);
					}
					
					// only gets this far if !worldIn.isRemote()
					return ActionResultType.func_233537_a_(worldIn.isRemote);
				}
			}
			else {
				if (stack.getItem().isIn(KettleTags.TEA_INGREDIENTS)) {
					// if the cup doesn't have any items [and is in the server logical side]
					if (cupTileEntity.handler.isEmpty()) {
						// creates a copy of the used stack with a count of 1, and puts it in the cup
						ItemStack cupStack = stack.copy();
						cupStack.setCount(1);
						cupTileEntity.handler.setStack(cupStack);
						
						// don't remove items if is in creative
						if (!player.abilities.isCreativeMode) {
							// takes one item from the player's hand
							stack.shrink(1);
						}
						
						player.playSound(SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 1, 1);
						return ActionResultType.func_233537_a_(worldIn.isRemote);
					}
					
					return ActionResultType.CONSUME;
				} else if (stack.getItem() instanceof KettleItem) {
					ItemStack teaIngredient = cupTileEntity.handler.getStack();
					CompoundNBT tag = stack.getTag();
					// if the cup has an ingredient and the item used is a kettle with hot water
					if (!teaIngredient.isEmpty() && tag != null && tag.getCompound("BlockStateTag").getString("content").equals("hot_water")) {
						CompoundNBT blockStateTag = tag.getCompound("BlockStateTag");
						// decrease the fullness of the kettle by one
						// if the kettle has no fullness left, change its content to empty
						// yes, this is messy. no, I don't care.
						if (blockStateTag.getInt("fullness") > 1) blockStateTag.putInt("fullness", blockStateTag.getInt("fullness") - 1);
						else {
							blockStateTag.putString("content", "empty");
							blockStateTag.putInt("fullness", 0);
						}
						
						tag.getCompound("BlockEntityTag").putInt("BoilingTicks", 0);
						stack.setTag(tag);
						
						// set the player's hand to the now empty kettle
						player.setHeldItem(handIn, stack);
						if (player instanceof ServerPlayerEntity) ((ServerPlayerEntity) player).sendContainerToPlayer(player.container);
						
						if (teaIngredient.getItem().isIn(KettleTags.TEA_INGREDIENTS)) {
							makeTea(teaIngredient.getItem(), state, worldIn, pos);
						}
						
						// play a sound
						worldIn.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
						return ActionResultType.func_233537_a_(worldIn.isRemote);
					}
				}
			}
		}
		
		// use the default behavior if all else failed
		return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
	}
	
	void makeTea(Item ingredient, BlockState state, World worldIn, BlockPos pos) {
		TeaBlock result = TeaSteepRecipes.getRecipe(ingredient).getValue();
		if (result != null) worldIn.setBlockState(pos, result.getDefaultState().with(FACING, state.get(FACING)));
		else {
			TileEntity tileEntity = worldIn.getTileEntity(pos);
			if (tileEntity instanceof CupTileEntity) ((CupTileEntity) tileEntity).handler.setStack(ItemStack.EMPTY);
		}
	}
	
	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
		List<ItemStack> drops = super.getDrops(state, builder);
		TileEntity tileEntity = builder.get(LootParameters.BLOCK_ENTITY);
		// make it also drop the item inside (if there's none, the stack will be empty)
		if (tileEntity instanceof CupTileEntity) drops.add(((CupTileEntity) tileEntity).handler.getStack());
		
		return drops;
	}
	
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}
	
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.toRotation(state.get(FACING)));
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}
}