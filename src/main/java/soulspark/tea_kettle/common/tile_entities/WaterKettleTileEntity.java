package soulspark.tea_kettle.common.tile_entities;

import soulspark.tea_kettle.common.blocks.WaterKettleBlock;
import soulspark.tea_kettle.common.blocks.KettleBlock;
import soulspark.tea_kettle.core.init.ModBlocks;
import soulspark.tea_kettle.core.init.ModParticles;
import soulspark.tea_kettle.core.init.ModTileEntities;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public class WaterKettleTileEntity extends TileEntity implements ITickableTileEntity {
	public static final int TICKS_TO_BOIL = 150;
	
	public int boilingTicks = 0;
	
	public WaterKettleTileEntity() {
		super(ModTileEntities.WATER_KETTLE.get());
	}
	
	@Override
	public void tick() {
		if (world == null) return;
		
		BlockState state = getBlockState();
		boolean lit = state.get(WaterKettleBlock.LIT);
		
		if (world.isRemote) {
			// chance increases with boiling ticks
			if (Math.random() < boilingTicks / (TICKS_TO_BOIL * 2f) - 0.2f) {
				BlockPos offset = pos.offset(state.get(KettleBlock.FACING).rotateYCCW()).subtract(pos);
				world.addParticle(ModParticles.STEAM.get(), pos.getX() + 0.5f + offset.getX() * 0.45f, pos.getY() + 0.35f, pos.getZ() + 0.5f + offset.getZ() * 0.45f, 0.0D, 0.05D * boilingTicks / TICKS_TO_BOIL, 0.0D);
			}
		} else {
			if (lit) {
				// increases boiling ticks until it reaches boiling point and then turns water into hot water
				boilingTicks += 1;
				if (boilingTicks >= TICKS_TO_BOIL) {
					BlockState oldState = getBlockState();
					
					world.playSound(null, pos, SoundEvents.BLOCK_BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundCategory.BLOCKS, 1f, 1.2f);
					world.setBlockState(pos, ModBlocks.BOILING_KETTLE.get().getDefaultState().with(KettleBlock.FACING, state.get(KettleBlock.FACING)));
					notifyChanged(oldState); // makes sure to point out the state changed too
				}
				else notifyChanged();
			}
			else {
				if (boilingTicks > 0 && world.rand.nextFloat() < 0.25f) {
					// if not on a hot surface anymore, slowly lose heat
					boilingTicks -= 1;
					// notify the client about the temperature of the block
					notifyChanged();
				}
			}
		}
	}
	
	// called by KettleBlock when emptying it with a glass bottle; prevents ticks to persist when the kettle is empty
	public void resetBoilingTicks() {
		boilingTicks = 0;
		notifyChanged();
	}
	
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
	}
}
