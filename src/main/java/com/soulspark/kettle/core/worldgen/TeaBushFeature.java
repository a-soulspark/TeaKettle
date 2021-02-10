package com.soulspark.kettle.core.worldgen;

import com.soulspark.kettle.core.init.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import java.util.Random;

public class TeaBushFeature extends Feature<NoFeatureConfig> {
	public TeaBushFeature() {
		super(NoFeatureConfig.field_236558_a_);
	}
	
	@Override
	public boolean generate(ISeedReader worldIn, ChunkGenerator generator, Random rand, BlockPos pos, NoFeatureConfig config) {
		BlockPos down = pos.down();
		BlockState soil = worldIn.getBlockState(down);
		if(soil.canSustainPlant(worldIn, down, Direction.UP, ModBlocks.TEA_BUSH.get()) && worldIn.getBlockState(pos).isAir(worldIn, pos)) {
			worldIn.setBlockState(pos, ModBlocks.TEA_BUSH.get().getDefaultState(), 3);
			return true;
		}
		return false;
	}
}
