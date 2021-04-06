package soulspark.tea_kettle.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
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
import soulspark.tea_kettle.TeaKettle;
import soulspark.tea_kettle.common.recipes.TeaSteepingRecipe;
import soulspark.tea_kettle.common.tile_entities.CupTileEntity;
import soulspark.tea_kettle.core.init.ModBlocks;
import soulspark.tea_kettle.core.init.ModRecipeTypes;
import soulspark.tea_kettle.core.util.TeaKettleTags;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class CupBlock extends Block implements IGrabbable {
	protected static final VoxelShape SHAPE = Block.makeCuboidShape(5.0D, 0.0D, 5.0D, 11.0D, 5.0D, 11.0D);
	public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
	
	public CupBlock(Properties properties) {
		super(properties);
		setDefaultState(getStateContainer().getBaseState().with(FACING, Direction.NORTH));
		ModBlocks.CUPS.add(this);
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
		if (!(tileEntity instanceof CupTileEntity)) return ActionResultType.PASS;
		
		// gets the cup tile entity version of the tile entity
		CupTileEntity cupTileEntity = (CupTileEntity) tileEntity;
		ItemStack handStack = player.getHeldItem(handIn);
		boolean handEmpty = handStack.isEmpty();
		
		// if main hand is empty, put the cup in it
		if (handEmpty) {
			// don't want anything to do with an empty offhand
			if (handIn == Hand.MAIN_HAND) {
				ItemStack cupStack = cupTileEntity.handler.getLastStack();
				// if the cup is empty, put it in your hand
				if (cupStack.isEmpty()) {
					// if the world is remote, skip all of it and just say "success"
					if (!worldIn.isRemote) {
						ItemStack blockStack = getGrabStack(state, worldIn, pos);
						// if the cup is the same as the cup you're holding, increase the stack by 1
						if (!player.abilities.isCreativeMode) blockStack.grow(handStack.getCount());
						player.setHeldItem(handIn, blockStack);
						// send the update to clients and remove the block
						((ServerPlayerEntity) player).sendContainerToPlayer(player.container);
						worldIn.removeBlock(pos, false);
					}
					else player.playSound(SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 1, 1);
				} else {
					// gets the item in the cup and gives it to the player
					player.setHeldItem(handIn, cupStack.split(64));
					//((ServerPlayerEntity)player).sendContainerToPlayer(player.container);
					// removes the item from the cup
					//cupTileEntity.handler.setStack(ItemStack.EMPTY);
				}
				
				// only gets this far if !worldIn.isRemote()
				return ActionResultType.func_233537_a_(worldIn.isRemote);
			}
		}
		else {
			if (handStack.getItem().isIn(TeaKettleTags.KETTLES)) {
				// if the cup has an ingredient and the item used is a kettle with hot water
				if (!cupTileEntity.handler.isEmpty()) {
					// tries getting the recipe for the given ingredient
					if (!tryMakeTea(state, worldIn, pos, cupTileEntity.handler.getStacks().toArray(new ItemStack[2]), handStack)) return ActionResultType.CONSUME;
					
					// empty the kettle if player isn't in creative mode
					if (!player.abilities.isCreativeMode) {
						player.setHeldItem(handIn, handStack.getContainerItem());
						if (player instanceof ServerPlayerEntity) ((ServerPlayerEntity) player).sendContainerToPlayer(player.container);
					}
					
					// play a sound
					worldIn.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
					return ActionResultType.func_233537_a_(worldIn.isRemote);
				}
			}
			// if a recipe for this exists
			else {
				ItemStack cupStack = cupTileEntity.handler.getLastStack();
				KettleBlock.LOGGER.info("isnt full! {}", cupStack);
				ItemStack[] ingredients = cupStack.isEmpty() ? new ItemStack[] { handStack } : new ItemStack[] { handStack, cupStack };
				
				if (isIngredient(ingredients, ModRecipeTypes.TEA_STEEPING, worldIn) || isIngredient(ingredients, ModRecipeTypes.MILKY_DRINK, worldIn)) {
					// creates a copy of the used stack with a count of 1, and tries putting in the cup
					ItemStack copyStack = handStack.copy();
					copyStack.setCount(1);
					
					// if it could be added...
					if (cupTileEntity.handler.addStack(copyStack)) {
						// don't remove items if is in creative
						if (!player.abilities.isCreativeMode) {
							// takes one item from the player's hand
							handStack.shrink(1);
							if (player instanceof ServerPlayerEntity)
								((ServerPlayerEntity) player).sendContainerToPlayer(player.container);
						}
						
						player.playSound(SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 1, 1);
						return ActionResultType.func_233537_a_(worldIn.isRemote);
					}
					return ActionResultType.CONSUME;
				}
			}
		}
		
		// use the default behavior if all else failed
		return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
	}
	
	protected <T extends TeaSteepingRecipe> boolean isIngredient(ItemStack[] stacks, IRecipeType<T> recipeType, World world) {
		if (stacks.length == 0) return false;
		if (stacks.length == 2) return world.getRecipeManager().getRecipe(ModRecipeTypes.TEA_STEEPING, new Inventory(stacks), world).isPresent() || world.getRecipeManager().getRecipe(ModRecipeTypes.MILKY_DRINK, new Inventory(stacks), world).isPresent();
		List<T> recipes = world.getRecipeManager().getRecipesForType(recipeType);
		
		return recipes.stream().anyMatch(recipe -> {
			for (ItemStack stack : stacks)
				if (recipe.getIngredients().stream().noneMatch(ingredient -> ingredient.test(stack))) return false;
			return true;
		});
		//return world.getRecipeManager().getRecipe(, new Inventory(stack), world).isPresent() || world.getRecipeManager().getRecipe(ModRecipeTypes.MILKY_DRINK, new Inventory(stack), world).isPresent();
	}
	
	@Override
	public ItemStack getGrabStack(BlockState state, World world, BlockPos pos) {
		TileEntity tileEntity = world.getTileEntity(pos);
		if (tileEntity instanceof CupTileEntity) {
			CupTileEntity cupTileEntity = (CupTileEntity) tileEntity;
			ItemStack cupStack = cupTileEntity.handler.getLastStack();
			if (!cupStack.isEmpty()) return cupStack;
		}
		
		return new ItemStack(asItem());
	}
	
	@Override
	public void grab(BlockState state, World world, BlockPos pos) {
		TileEntity tileEntity = world.getTileEntity(pos);
		if (!(tileEntity instanceof CupTileEntity) || world.isRemote) return;
		CupTileEntity cupTileEntity = (CupTileEntity)tileEntity;
		
		ItemStack cupStack = cupTileEntity.handler.getLastStack();
		if (cupStack.isEmpty()) world.removeBlock(pos, false);
		else cupTileEntity.handler.setLastStack(ItemStack.EMPTY);
	}
	
	private boolean tryMakeTea(BlockState state, World worldIn, BlockPos pos, ItemStack[] ingredients, ItemStack handStack) {
		Optional<?> optional = handStack.getItem().isIn(TeaKettleTags.BOILING_KETTLES) ? worldIn.getRecipeManager().getRecipe(ModRecipeTypes.TEA_STEEPING, new Inventory(ingredients), worldIn) : worldIn.getRecipeManager().getRecipe(ModRecipeTypes.MILKY_DRINK, new Inventory(ingredients), worldIn);
		
		if (optional.isPresent()) {
			TeaSteepingRecipe recipe = (TeaSteepingRecipe) optional.get();
			// gets the output item type of the recipe
			Item item = recipe.getRecipeOutput().getItem();
			if (ModBlocks.TEA_ITEM_TO_BLOCK.containsKey(item.getRegistryName())) item = ModBlocks.TEA_ITEM_TO_BLOCK.get(item.getRegistryName());
			// if the item is a BlockItem, get the block of that item and sets this cup to that
			if (item instanceof BlockItem) {
				worldIn.setBlockState(pos, ((BlockItem) item).getBlock().getDefaultState().with(FACING, state.get(FACING)));
				return true;
			}
		}
		
		// if any of the previous checks failed, the tea wasn't made
		return false;
	}
	
	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
		List<ItemStack> drops = super.getDrops(state, builder);
		TileEntity tileEntity = builder.get(LootParameters.BLOCK_ENTITY);
		// make it also drop the item inside (if there's none, the stack will be empty)
		if (tileEntity instanceof CupTileEntity) drops.addAll(((CupTileEntity) tileEntity).handler.getStacks());
		
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