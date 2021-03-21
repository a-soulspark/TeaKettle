package soulspark.tea_kettle.common.tile_entities;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import soulspark.tea_kettle.core.init.ModTileEntities;
import soulspark.tea_kettle.core.util.CupItemHandler;

public class CupTileEntity extends TileEntity {
	public CupItemHandler handler;
	
	public CupTileEntity() {
		super(ModTileEntities.CUP.get());
		handler = new CupItemHandler(1, this::notifyChanged);
	}
	
	@Override
	public void read(BlockState state, CompoundNBT compound) {
		super.read(state, compound);
		handler.deserializeNBT(compound);
	}
	
	@Override
	public CompoundNBT write(CompoundNBT compound) {
		return super.write(compound).merge(handler.serializeNBT());
	}
	
	void notifyChanged() { notifyChanged(getBlockState()); }
	
	void notifyChanged(BlockState oldState) {
		this.markDirty();
		if (world != null) world.notifyBlockUpdate(this.getPos(), oldState, this.getBlockState(), 3);
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		read(getBlockState(), pkt.getNbtCompound());
	}
	
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(pos, -1, getUpdateTag());
	}
	
	@Override
	public CompoundNBT getUpdateTag() {
		return write(new CompoundNBT());
	}
}
