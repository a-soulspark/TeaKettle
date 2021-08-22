package soulspark.tea_kettle.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import soulspark.tea_kettle.TeaKettle;
import soulspark.tea_kettle.core.init.ModBlocks;

import javax.annotation.Nullable;
import java.util.Optional;

public class SimplyTeaBlock extends ExternalTeaBlock {
	public static final IntegerProperty FULLNESS = IntegerProperty.create("fullness", 1, 2);
	public static final String EXTRA_TAG = "with_honey";
	
	public SimplyTeaBlock(ResourceLocation itemName, Properties properties) {
		super(itemName, properties);
		setDefaultState(getDefaultState().with(FULLNESS, 2));
	}
	
	protected String getExtraTag() {
		return EXTRA_TAG;
	}
	
	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockState defaultState = super.getStateForPlacement(context);
		if (defaultState == null) return null;
		
		ItemStack stack = context.getItem();
		CompoundNBT tag = stack.getTag();
		TeaKettle.LOGGER.info(tag);
		int fullness = tag != null ? 2 - tag.getInt("Damage") % 2 : 2;
		boolean withHoney = tag != null && tag.getBoolean(getExtraTag());
		
		return defaultState.with(FULLNESS, fullness).with(SWEETNESS, withHoney ? 2 : 0);
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		ItemStack stack = player.getHeldItem(handIn);
		if (!player.canPlayerEdit(pos, hit.getFace(), stack)) return ActionResultType.PASS;
		
		if (stack.isEmpty() && handIn == Hand.MAIN_HAND) {
			if (!worldIn.isRemote) {
				// put the first drop into the player's hand (that supposedly is the cup)
				ItemStack teaStack = getGrabStack(state, worldIn, pos);
				
				if (stack.isEmpty()) player.setHeldItem(handIn, teaStack);
				// grab the block
				grab(worldIn, pos);
			}
			// in the client, just play a little equip sound because you can't do anything drop-related there
			else player.playSound(SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 1, 1);
			return ActionResultType.func_233537_a_(worldIn.isRemote);
		}
		else {
			CraftingInventory inventory = new CraftingInventory(new Container(ContainerType.CRAFTING, -1) {
				@Override
				public boolean canInteractWith(PlayerEntity playerIn) {
					return true;
				}
			}, 2, 1);
			
			inventory.setInventorySlotContents(0, getGrabStack(state, worldIn, pos));
			inventory.setInventorySlotContents(1, stack);
			
			Optional<ICraftingRecipe> optional = worldIn.getRecipeManager().getRecipe(IRecipeType.CRAFTING, inventory, worldIn);
			if (optional.isPresent()) {
				ICraftingRecipe recipe = optional.get();
				// gets the output item type of the recipe
				if (ModBlocks.TEA_ITEM_TO_BLOCK.containsKey(recipe.getRecipeOutput().getItem().getRegistryName())) {
					BlockState newState = getStateForPlacement(new BlockItemUseContext(player, handIn, recipe.getRecipeOutput(), hit));
					
					if (newState != null) {
						worldIn.setBlockState(pos, newState.with(FACING, state.get(FACING)));

						// empty the kettle if player isn't in creative mode
						if (!player.abilities.isCreativeMode) {
							stack.shrink(1);
							if (stack.isEmpty()) player.setHeldItem(handIn, stack.getContainerItem());
							else player.addItemStackToInventory(stack.getContainerItem());
						}

						// play a sound
						worldIn.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 0.8F, 0.9F);
						return ActionResultType.func_233537_a_(worldIn.isRemote);
					}
				}
			}
		}
		
		return ActionResultType.PASS;
	}
	
	@Override
	protected CompoundNBT fillTeaData(ItemStack stack, BlockState state) {
		CompoundNBT tag = super.fillTeaData(stack, state);
		tag.getCompound("BlockStateTag").remove("sweetness");
		if (state.get(SWEETNESS) == 2) tag.putBoolean(getExtraTag(), true);
		tag.putInt("Damage", state.get(FULLNESS) % 2);
		return tag;
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(FULLNESS);
	}
}
