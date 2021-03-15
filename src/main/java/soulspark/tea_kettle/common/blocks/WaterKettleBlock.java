package soulspark.tea_kettle.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import soulspark.tea_kettle.common.tile_entities.WaterKettleTileEntity;
import soulspark.tea_kettle.core.init.ModBlocks;
import soulspark.tea_kettle.core.init.ModItems;

import javax.annotation.Nullable;

@SuppressWarnings("ConstantConditions")
public class WaterKettleBlock extends KettleBlock {
	public static final BooleanProperty LIT = BlockStateProperties.LIT;
	
	public WaterKettleBlock(Properties builder) {
		super(builder);
		setDefaultState(getDefaultState().with(LIT, false));
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new WaterKettleTileEntity();
	}
	
	@Override
	// makes the block face to the right of the placing direction, and updates the lit state to match the block underneath
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return super.getStateForPlacement(context).with(LIT, isHot(context.getWorld(), context.getPos().down()));
	}
	
	@Override
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		BlockState state = super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
		if (state.isIn(this)) return state.with(LIT, isHot(worldIn, facingPos));
		return state;
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		ActionResultType result = super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
		
		if (result == ActionResultType.PASS) {
			ItemStack itemStack = player.getHeldItem(handIn);
			Item item = itemStack.getItem();
			if (item == Items.GLASS_BOTTLE) {
				// if there's water in the kettle, try filling the bottle with it
				if (!worldIn.isRemote) {
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
					worldIn.setBlockState(pos, ModBlocks.EMPTY_KETTLE.get().getDefaultState());
					worldIn.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
				}
				
				// see above ^^^
				return ActionResultType.func_233537_a_(worldIn.isRemote);
			}
		}
		
		return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
	}
	
	@Override
	protected ItemStack getPickUpItem(BlockState state, World worldIn, BlockPos pos) {
		ItemStack kettleStack = new ItemStack(ModItems.WATER_KETTLE.get());
		kettleStack.setTagInfo("BlockEntityTag", worldIn.getTileEntity(pos).serializeNBT());
		return kettleStack;
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(LIT);
	}
}
