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
import soulspark.tea_kettle.common.recipes.CupDrinkRecipe;
import soulspark.tea_kettle.common.tile_entities.CupTileEntity;
import soulspark.tea_kettle.core.init.ModBlocks;
import soulspark.tea_kettle.core.init.ModRecipeTypes;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Iterator;
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
		
		if (handEmpty) {
			if (handIn == Hand.MAIN_HAND) {
				if (!worldIn.isRemote) {
					ItemStack cupStack = cupTileEntity.handler.pop();
					if (cupStack.isEmpty()) {
						player.setHeldItem(handIn, getGrabStack(state, worldIn, pos));
						worldIn.removeBlock(pos, false);
					} else
						player.setHeldItem(handIn, cupStack.split(64));  // otherwise, get the item in the cup and gives it to the player
					
					((ServerPlayerEntity) player).sendContainerToPlayer(player.container);
					return ActionResultType.CONSUME;
				} else player.playSound(SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 1, 1);
			}
			
			return ActionResultType.func_233537_a_(worldIn.isRemote);
		}
		else {
			ItemStack[] ingredients = new ItemStack[cupTileEntity.handler.getStacks().size() + 1];
			Iterator<ItemStack> iterator = cupTileEntity.handler.getStacks().iterator();
			int i = 0;
			
			ingredients[i++] = handStack;
			while (iterator.hasNext()) ingredients[i++] = iterator.next();
			
			if (tryMakeTea(state, worldIn, pos, ingredients)) {
				// empty the kettle if player isn't in creative mode
				if (!player.abilities.isCreativeMode) {
					if (handStack.getCount() == 1) player.setHeldItem(handIn, handStack.getContainerItem());
					else {
						handStack.shrink(1);
						player.addItemStackToInventory(handStack.getContainerItem());
					}
				}
				
				// play a sound
				worldIn.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
				return ActionResultType.func_233537_a_(worldIn.isRemote);
			}
			// if a recipe for this exists
			else {
				//ItemStack[] ingredients = cupStack.isEmpty() ? new ItemStack[] { handStack } : new ItemStack[] { handStack, cupStack };
				ItemStack putStack = handStack.copy();
				putStack.setCount(1);
				
				NonNullList<ItemStack> newStacks = NonNullList.create();
				newStacks.addAll(cupTileEntity.handler.getStacks());
				newStacks.add(putStack);
				
				if (isIngredient(condenseStacks(newStacks), worldIn)) {
					// creates a copy of the used stack with a count of 1, and tries putting in the cup
					ItemStack copyStack = handStack.copy();
					copyStack.setCount(1);
					/*take a look at CampfireBlock.onBlockActivated() to see how THEY do it,
						desync-less!*/
					// if it could be added...
					if (!worldIn.isRemote && cupTileEntity.handler.addStack(copyStack)) {
						// takes one item from the player's hand, unless if is in creative
						if (!player.abilities.isCreativeMode) handStack.shrink(1);
						player.playSound(SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 1, 1);
						
						return ActionResultType.CONSUME;
					}
					else if (worldIn.isRemote) player.playSound(SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 1, 1);
					
					return ActionResultType.func_233537_a_(worldIn.isRemote);
				}
			}
		}
		
		// use the default behavior if all else failed
		return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
	}
	
	public HashMap<Item, Integer> condenseStacks(NonNullList<ItemStack> uncondensedStacks) {
		HashMap<Item, Integer> condensedSet = new HashMap<>();
		for (ItemStack stack : uncondensedStacks) condensedSet.compute(stack.getItem(), (a, b) -> (b == null ? 0 : b) + stack.getCount());
		
		return condensedSet;
	}
	
	protected boolean isIngredient(HashMap<Item, Integer> ingredients, World world) {
		List<CupDrinkRecipe> recipes = world.getRecipeManager().getRecipesForType(ModRecipeTypes.CUP_DRINK);
		
		return recipes.stream().anyMatch(recipe -> {
			for (Item item : ingredients.keySet()) {
				ItemStack stack = new ItemStack(item);
				if (recipe.getIngredients().stream().skip(1).filter(ingredient -> ingredient.test(stack)).count() < ingredients.get(item))
					return false;
			}
			return true;
		});
	}
	
	@Override
	public ItemStack getGrabStack(BlockState state, World world, TileEntity tileEntity) {
		if (tileEntity instanceof CupTileEntity) {
			CupTileEntity cupTileEntity = (CupTileEntity) tileEntity;
			ItemStack cupStack = cupTileEntity.handler.getLastStack();
			if (!cupStack.isEmpty()) return cupStack;
		}
		
		return new ItemStack(asItem());
	}
	
	@Override
	public void grab(World world, BlockPos pos) {
		TileEntity tileEntity = world.getTileEntity(pos);
		if (!(tileEntity instanceof CupTileEntity) || world.isRemote) return;
		CupTileEntity cupTileEntity = (CupTileEntity)tileEntity;
		
		ItemStack cupStack = cupTileEntity.handler.pop();
		if (cupStack.isEmpty()) world.removeBlock(pos, false);
	}
	
	private boolean tryMakeTea(BlockState state, World worldIn, BlockPos pos, ItemStack[] ingredients) {
		Optional<?> optional = worldIn.getRecipeManager().getRecipe(ModRecipeTypes.CUP_DRINK, new Inventory(ingredients), worldIn);
		
		if (optional.isPresent()) {
			CupDrinkRecipe recipe = (CupDrinkRecipe) optional.get();
			// gets the output item type of the recipe
			Item item = recipe.getRecipeOutput().getItem();
			if (ModBlocks.TEA_ITEM_TO_BLOCK.containsKey(item.getRegistryName())) item = ModBlocks.TEA_ITEM_TO_BLOCK.get(item.getRegistryName());
			// if the item is a BlockItem, get the block of that item and sets this cup to that
			if (item instanceof BlockItem) {
				Block teaBlock = ((BlockItem) item).getBlock();
				worldIn.setBlockState(pos, teaBlock.getDefaultState().with(FACING, state.get(FACING)));
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