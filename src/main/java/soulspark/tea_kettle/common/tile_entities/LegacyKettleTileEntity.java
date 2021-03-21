package soulspark.tea_kettle.common.tile_entities;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import soulspark.tea_kettle.TeaKettle;
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
		TeaKettle.LOGGER.info("I tried so hard!");
		
		if (world == null) return;
		
		BlockState state = getBlockState();
		
		TeaKettle.LOGGER.info("and got so far!!");
		
		if (!world.isRemote) {
			BlockState upgradedState;
			TeaKettle.LOGGER.info("but in the end");
			
			LegacyKettleBlock.Content content = state.get(LegacyKettleBlock.CONTENT);
			
			if (content == LegacyKettleBlock.Content.EMPTY)
				upgradedState = ModBlocks.EMPTY_KETTLE.get().getDefaultState();
			else
				upgradedState = ModBlocks.WATER_KETTLE.get().getDefaultState().with(FilledKettleBlock.LIT, state.get(FilledKettleBlock.LIT)).with(FilledKettleBlock.HOT, content == LegacyKettleBlock.Content.HOT_WATER).with(FilledKettleBlock.FULLNESS, Math.max(1, state.get(LegacyKettleBlock.FULLNESS)));
			
			world.setBlockState(pos, upgradedState.with(KettleBlock.FACING, state.get(LegacyKettleBlock.FACING)), 2);
			
		}
	}
	
	/*
	@Override
	// reads the data in a packet when it's received [by default it doesn't do anything]
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) { read(getBlockState(), pkt.getNbtCompound()); }
	
	void notifyChanged() { notifyChanged(getBlockState()); }
	
	void notifyChanged(BlockState oldState) {
		this.markDirty();
		if (world != null) world.notifyBlockUpdate(this.getPos(), oldState, this.getBlockState(), 3);
	}
	
	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		boilingTicks = nbt.contains("BoilingTicks") ? nbt.getInt("BoilingTicks") : 0;
	}
	
	@Override
	public CompoundNBT write(CompoundNBT compound) {
		super.write(compound);
		compound.putInt("BoilingTicks", boilingTicks);
		return compound;
	}
	
	@Nullable
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(pos, -1, getUpdateTag());
	}
	
	@Override
	public CompoundNBT getUpdateTag() {
		return write(new CompoundNBT());
	}*/
}
