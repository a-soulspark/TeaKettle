package soulspark.tea_kettle.common.blocks;

import net.minecraft.block.*;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("ConstantConditions")
public class CampfireKettleBlock extends FilledKettleBlock {
	public enum Content implements IStringSerializable {
		WATER("water"), MILK("milk");
		
		private final String name;
		Content(String nameIn) { name = nameIn; }
		
		@Override
		public String getString() {
			return name;
		}
	}
	
	public static final VoxelShape KETTLE_SHAPE = FilledKettleBlock.SHAPE.withOffset(0, 0.5f, 0);
	public static final VoxelShape CAMPFIRE_SHAPE = makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 7.0D, 16.0D);
	public static final EnumProperty<Content> CONTENT = EnumProperty.create("content", Content.class);
	
	private Block baseBlock;
	private final Supplier<Block> baseBlockSupplier;
	
	public CampfireKettleBlock(Function<BlockState, Item> itemSupplier, Supplier<Block> baseBlockSupplier, Properties builder) {
		super(itemSupplier, builder);
		this.baseBlockSupplier = baseBlockSupplier;
		setDefaultState(getDefaultState().with(CONTENT, Content.WATER));
	}
	
	public Block getBaseBlock() {
		if (baseBlock == null) baseBlock = baseBlockSupplier.get();
		return baseBlock;
	}
	
	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return VoxelShapes.or(CAMPFIRE_SHAPE, KETTLE_SHAPE);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		Entity entity = context.getEntity();
		if (entity instanceof PlayerEntity) return isKettleSelected(pos, (LivingEntity) entity) ? KETTLE_SHAPE : CAMPFIRE_SHAPE;
		return super.getRayTraceShape(state, worldIn, pos, context);
	}
	
	public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
		if (!entityIn.isImmuneToFire() && entityIn instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity)entityIn))
			entityIn.attackEntityFrom(DamageSource.IN_FIRE, 1);
		
		super.onEntityCollision(state, worldIn, pos, entityIn);
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		if (!isKettleSelected(pos, player)) return ActionResultType.PASS;
		
		ActionResultType result = super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
		if (result == ActionResultType.CONSUME) worldIn.setBlockState(pos, getBaseBlock().getDefaultState().with(CampfireBlock.FACING, state.get(FACING)));
		
		return result;
	}
	
	@Override
	public float getPlayerRelativeBlockHardness(BlockState state, PlayerEntity player, IBlockReader worldIn, BlockPos pos) {
		if (!isKettleSelected(pos, player)) return super.getPlayerRelativeBlockHardness(state, player, worldIn, pos);
		int i = net.minecraftforge.common.ForgeHooks.canHarvestBlock(state, player, worldIn, pos) ? 30 : 100;
		return player.getDigSpeed(state, pos) / 0.5f / (float)i;
	}
	
	@Override
	public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, FluidState fluid) {
		if (!isKettleSelected(pos, player)) return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
		else {
			world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundType.METAL.getBreakSound(), SoundCategory.BLOCKS, 1, 1);
			if (!world.isRemote) {
				if (!player.abilities.isCreativeMode) spawnAsEntity(world, pos, getGrabStack(state, world, pos));
				world.setBlockState(pos, getBaseBlock().getDefaultState().with(FACING, state.get(FACING)));
			}
			else Minecraft.getInstance().particles.addBlockDestroyEffects(pos, state);
			
			return false;
		}
	}
	
	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
		List<ItemStack> drops = super.getDrops(state, builder);
		drops.add(getGrabStack(state, builder.getWorld(), builder.get(LootParameters.BLOCK_ENTITY)));
		return drops;
	}
	
	@Override
	public void grab(World world, BlockPos pos) {
		if (!world.isRemote) world.setBlockState(pos, getBaseBlock().getDefaultState().with(FACING, world.getBlockState(pos).get(FACING)));
	}
	
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if (rand.nextInt(10) == 0) worldIn.playSound(((float) pos.getX() + 0.5F), (float) pos.getY() + 0.5F, (float) pos.getZ() + 0.5F, SoundEvents.BLOCK_CAMPFIRE_CRACKLE, SoundCategory.BLOCKS, 0.5F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.6F, false);
	}
	
	@Override
	public SoundType getSoundType(BlockState state, IWorldReader world, BlockPos pos, @Nullable Entity entity) {
		if (entity instanceof LivingEntity && isKettleSelected(pos, (LivingEntity) entity)) return SoundType.METAL;
		return super.getSoundType(state, world, pos, entity);
	}
	
	public boolean isKettleSelected(BlockPos pos, LivingEntity entity) {
		Vector3d startVec = entity.getEyePosition(1);
		Vector3d endVec = startVec.add(entity.getLookVec().scale(entity.getAttribute(ForgeMod.REACH_DISTANCE.get()).getValue()));
		RayTraceResult subHit = KETTLE_SHAPE.rayTrace(startVec, endVec, pos);
		
		return subHit != null && subHit.getType() == Type.BLOCK;
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING, HOT, CONTENT, FULLNESS);
	}
}
