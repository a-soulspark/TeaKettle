package soulspark.tea_kettle.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.*;
import net.minecraft.loot.LootContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IntegerProperty;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import soulspark.tea_kettle.common.recipes.TeaMixingRecipe;
import soulspark.tea_kettle.core.init.ModBlocks;
import soulspark.tea_kettle.core.init.ModItems;
import soulspark.tea_kettle.core.init.ModParticles;
import soulspark.tea_kettle.core.init.ModRecipeTypes;
import soulspark.tea_kettle.core.util.TeaKettleTags;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class TeaBlock extends Block implements IGrabbable {
	public static final DirectionProperty FACING = CupBlock.FACING;
	public static final IntegerProperty SWEETNESS = IntegerProperty.create("sweetness", 0, 2);
	
	public TeaBlock(Properties properties) {
		super(properties);
		setDefaultState(getStateContainer().getBaseState().with(FACING, Direction.NORTH).with(SWEETNESS, 0));
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return CupBlock.SHAPE;
	}
	
	@Override
	public PushReaction getPushReaction(BlockState state) {
		return PushReaction.DESTROY;
	}
	
	@Nullable
	@Override
	// makes the block face to the right of the placing direction
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockState state = super.getStateForPlacement(context);
		if (state == null) return null;
		return state.with(FACING, context.getPlacementHorizontalFacing().getOpposite());
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
		ItemStack stack = player.getHeldItem(handIn);
		
		if (stack.isEmpty() && handIn == Hand.MAIN_HAND) {
			if (!worldIn.isRemote) {
				// put the first drop into the player's hand (that supposedly is the cup)
				ItemStack teaStack = getGrabStack(state, worldIn, pos);
				
				if (stack.isEmpty()) player.setHeldItem(handIn, teaStack);
				// send the update to clients and remove the block
				((ServerPlayerEntity) player).sendContainerToPlayer(player.container);
				grab(worldIn, pos);
			}
			// in the client, just play a little equip sound because you can't do anything drop-related there
			else player.playSound(SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 1, 1);
			return ActionResultType.func_233537_a_(worldIn.isRemote);
		}
		else if (stack.getItem().isIn(TeaKettleTags.FROTHING_KETTLES)) {
			// tries getting the recipe for this cup
			if (!tryMix(state, worldIn, pos)) return ActionResultType.CONSUME;
			
			// empty the kettle if player isn't in creative mode
			if (!player.abilities.isCreativeMode) {
				player.setHeldItem(handIn, stack.getContainerItem());
				if (player instanceof ServerPlayerEntity) ((ServerPlayerEntity) player).sendContainerToPlayer(player.container);
			}
			
			// play a sound
			worldIn.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 0.8F, 0.9F);
			return ActionResultType.func_233537_a_(worldIn.isRemote);
		}
		else {
			int sweetenerFactor = stack.getItem().isIn(TeaKettleTags.STRONG_SWEENETERS) ? 2 : stack.getItem().isIn(TeaKettleTags.WEAK_SWEENETERS) ? 1 : 0;
			if (sweetenerFactor > state.get(SWEETNESS)) {
				if (!worldIn.isRemote) {
					if (!player.abilities.isCreativeMode) {
						// replace the hand with a glass bottle, water bottles aren't stackable after all
						ItemStack returnStack = stack.getContainerItem();
						if (stack.getItem() == Items.HONEY_BOTTLE) returnStack = new ItemStack(ModItems.HALF_HONEY_BOTTLE.get());
						/*
						else if (stack.getItem() == ModItems.HALF_HONEY_BOTTLE.get()) returnStack = new ItemStack(Items.GLASS_BOTTLE);
						*/
						
						stack.shrink(1);
						if (stack.isEmpty()) player.setHeldItem(handIn, returnStack);
						else player.addItemStackToInventory(returnStack);
						
						if (player instanceof ServerPlayerEntity) ((ServerPlayerEntity) player).sendContainerToPlayer(player.container);
					}
					
					// update the kettle to be filled with water
					worldIn.setBlockState(pos, state.with(SWEETNESS, sweetenerFactor));
					worldIn.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 0.8F, 0.9f);
				}
				
				// ugly ass function that makes the event succeed if this is a *logical client*
				return ActionResultType.func_233537_a_(worldIn.isRemote);
			}
		}
		
		return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
	}
	
	@Override
	public ItemStack getGrabStack(BlockState state, World world, BlockPos pos) {
		ItemStack teaStack = new ItemStack(asItem());
		CompoundNBT tag = teaStack.getOrCreateTag();
		CompoundNBT blockStateTag = tag.getCompound("BlockStateTag");
		blockStateTag.putString("sweetness", state.get(SWEETNESS).toString());
		tag.put("BlockStateTag", blockStateTag);
		teaStack.setTag(tag);
		
		return teaStack;
	}
	
	private boolean tryMix(BlockState state, World worldIn, BlockPos pos) {
		Optional<TeaMixingRecipe> optional = worldIn.getRecipeManager().getRecipe(ModRecipeTypes.TEA_MIXING, new Inventory(new ItemStack(asItem())), worldIn);
		if (optional.isPresent()) {
			TeaMixingRecipe recipe = optional.get();
			// gets the output item type of the recipe
			Item item = recipe.getRecipeOutput().getItem();
			if (ModBlocks.TEA_ITEM_TO_BLOCK.containsKey(item.getRegistryName())) item = ModBlocks.TEA_ITEM_TO_BLOCK.get(item.getRegistryName());
			// if the item is a BlockItem, get the block of that item and sets this cup to that
			if (item instanceof BlockItem) {
				worldIn.setBlockState(pos, ((BlockItem) item).getBlock().getDefaultState().with(FACING, state.get(FACING)).with(SWEETNESS, state.get(SWEETNESS)));
				return true;
			}
		}
		
		// if any of the previous checks failed, the tea wasn't made
		return false;
	}
	
	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
		List<ItemStack> drops = super.getDrops(state, builder);
		for (ItemStack drop : drops) {
			if (drop.getItem() == asItem()) {
				CompoundNBT tag = drop.getOrCreateTag();
				CompoundNBT blockStateTag = tag.getCompound("BlockStateTag");
				blockStateTag.putString("sweetness", state.get(SWEETNESS).toString());
				tag.put("BlockStateTag", blockStateTag);
			}
		}
		return drops;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		int sweetness = stateIn.get(SWEETNESS);
		BasicParticleType particleData = ModParticles.TEA_STEAM.get();
		
		switch (sweetness) {
			case 1:
				particleData = ModParticles.WEAK_TEA_STEAM.get();
				break;
			case 2:
				particleData = ModParticles.STRONG_TEA_STEAM.get();
				break;
		}
		
		if (rand.nextFloat() < 0.5f) worldIn.addParticle(particleData, true, pos.getX() + randomSteamPos(rand), pos.getY() + 0.3125, pos.getZ() + randomSteamPos(rand), 0, 0, 0);
		worldIn.addParticle(particleData, true, pos.getX() + randomSteamPos(rand), pos.getY() + 0.3125, pos.getZ() + randomSteamPos(rand), 0, 0, 0);
	}
	
	private float randomSteamPos(Random rand) {
		return 0.425f + rand.nextFloat() * 0.15f;
	}
	
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}
	
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.toRotation(state.get(FACING)));
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING, SWEETNESS);
	}
}
