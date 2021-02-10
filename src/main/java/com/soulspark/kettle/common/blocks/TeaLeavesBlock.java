package com.soulspark.kettle.common.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class TeaLeavesBlock extends LeavesBlock {
	public TeaLeavesBlock(Properties properties) {
		super(properties);
	}
	
	@Override
	public boolean ticksRandomly(BlockState state) {
		return true;
	}
	
	@Override
	public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
		super.randomTick(state, worldIn, pos, random);
		
		if (!worldIn.isRemote) {
			int neighborLeaves = 0;
			for (Direction direction : Direction.values()) {
				if (worldIn.getBlockState(pos.offset(direction)).getBlock() == this && ++neighborLeaves > 2) break;
			}
			
			if (neighborLeaves <= 2) spread(worldIn, pos);
		}
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		ItemStack stack = player.getHeldItem(handIn);
		if (stack.getItem() instanceof BoneMealItem) {
			// spreads the leaves in 2-4 random directions (duplicates are possible)
			// will only replace air
			if (!worldIn.isRemote) spread(worldIn, pos);
			// spawns tons of happy particles to make this moment happy ^^
			for (int i = 0; i < 16; i++) worldIn.addParticle(ParticleTypes.HAPPY_VILLAGER, pos.getX() + worldIn.rand.nextFloat() - 0.5, pos.getY() + worldIn.rand.nextFloat() - 0.5, pos.getZ() + worldIn.rand.nextFloat() - 0.5, 0, 0, 0);
			
			if (!player.abilities.isCreativeMode) stack.shrink(1);
			worldIn.playSound(null, pos, SoundEvents.BLOCK_GRASS_BREAK, SoundCategory.BLOCKS, 1, 1);
			return ActionResultType.func_233537_a_(worldIn.isRemote);
		}
		
		return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
	}
	
	void spread(World worldIn, BlockPos pos) {
		for (int i = 0; i < 3 + worldIn.rand.nextInt() % 3; i++) {
			BlockPos placePos = pos.offset(Direction.getRandomDirection(worldIn.rand));
			if (worldIn.getBlockState(placePos).isReplaceable(Fluids.WATER)) {
				worldIn.destroyBlock(placePos, true);
				worldIn.setBlockState(placePos, getDefaultState().with(PERSISTENT, true));
			}
		}
	}
}
