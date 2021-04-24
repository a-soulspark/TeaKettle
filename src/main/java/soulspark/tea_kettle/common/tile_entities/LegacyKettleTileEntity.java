package soulspark.tea_kettle.common.tile_entities;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import soulspark.tea_kettle.common.blocks.FilledKettleBlock;
import soulspark.tea_kettle.common.blocks.KettleBlock;
import soulspark.tea_kettle.common.blocks.LegacyKettleBlock;
import soulspark.tea_kettle.core.init.ModBlocks;
import soulspark.tea_kettle.core.init.ModTileEntities;

public class LegacyKettleTileEntity extends TileEntity implements ITickableTileEntity {
	public LegacyKettleTileEntity() {
		super(ModTileEntities.LEGACY_KETTLE.get());
	}
	
	@Override
	public void tick() {
		if (world == null) return;
		
		BlockState state = getBlockState();
		
		if (!world.isRemote) {
			BlockState upgradedState;
			
			LegacyKettleBlock.Content content = state.get(LegacyKettleBlock.CONTENT);
			
			if (content == LegacyKettleBlock.Content.EMPTY)
				upgradedState = ModBlocks.EMPTY_KETTLE.get().getDefaultState();
			else
				upgradedState = ModBlocks.WATER_KETTLE.get().getDefaultState().with(FilledKettleBlock.LIT, state.get(FilledKettleBlock.LIT)).with(FilledKettleBlock.HOT, content == LegacyKettleBlock.Content.HOT_WATER).with(FilledKettleBlock.FULLNESS, Math.max(1, state.get(LegacyKettleBlock.FULLNESS)));
			
			world.setBlockState(pos, upgradedState.with(KettleBlock.FACING, state.get(LegacyKettleBlock.FACING)));
			
		}
	}
}
