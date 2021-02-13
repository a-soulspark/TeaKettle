package soulspark.tea_kettle.common.tile_entities;

import soulspark.tea_kettle.common.blocks.KettleBlock;
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

public class KettleTileEntity extends TileEntity implements ITickableTileEntity {
	private static final int TICKS_TO_BOIL = 150;
	
	private int boilingTicks = 0;
	
	public KettleTileEntity() {
		super(ModTileEntities.KETTLE.get());
	}
	
	@Override
	public void tick() {
		if (world == null) return;
		
		BlockState state = getBlockState();
		boolean lit = state.get(KettleBlock.LIT);
		KettleBlock.Content content = state.get(KettleBlock.CONTENT);
		
		if (world.isRemote) {
			// creates smoke particles if the kettle isn't empty (can be unlit so smoke keeps coming out of a hot, fading away kettle)
			if (content != KettleBlock.Content.EMPTY) {
				// chance increases with boiling ticks
				// if the water is boiling, the formula makes the chance substantially higher
				float chance = content == KettleBlock.Content.HOT_WATER ? boilingTicks / (TICKS_TO_BOIL * 0.5f) + 0.2f : boilingTicks / (TICKS_TO_BOIL * 2f) - 0.2f;
				
				if (Math.random() < chance) {
					BlockPos offset = pos.offset(state.get(KettleBlock.FACING).rotateYCCW()).subtract(pos);
					world.addParticle(ModParticles.STEAM.get(), pos.getX() + 0.5f + offset.getX() * 0.45f, pos.getY() + 0.35f, pos.getZ() + 0.5f + offset.getZ() * 0.45f, 0.0D, 0.05D * boilingTicks / TICKS_TO_BOIL, 0.0D);
				}
			}
		} else {
			// xTODO> make the kettle always cool down when not lit
			//  > prob will want to use some fancy formulas similar to above to determine the rates nicely
			
			// xTODO fix the water UV (too much detail per pixel)
			// xTODO make the tea a drink rather than food (idk how. just do it somehow.)
			// xTODO> add directionality to the cup (just rotate it bruv)
			// xTODO> see if you can fix the absence of sounds when picking the cup up
			// xTODO> maybe also add a sound when placing items in the cup
			//  > yes, I know the flower pot doesn't have that, but I think it'd be a nice QoL change.
			//  > it is, after all, an event that *should* be notified to the player when it succeeds. hmm.
			// xTODO> finally, tix the textures for tea leaf and cup and tea items, all the items pretty much.
			//  > theyre all too below the slot boundaries. make them more like bamboo ;)
			
			if (lit) {
				if (content == KettleBlock.Content.WATER) {
					// increases boiling ticks until it reaches boiling point and then turns water into hot water
					boilingTicks += 1;
					if (boilingTicks >= TICKS_TO_BOIL) {
						BlockState oldState = getBlockState();
						
						world.playSound(null, pos, SoundEvents.BLOCK_BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundCategory.BLOCKS, 1f, 1.2f);
						world.setBlockState(pos, state.with(KettleBlock.CONTENT, KettleBlock.Content.HOT_WATER));
						notifyChanged(oldState); // makes sure to point out the state changed too
					}
					else notifyChanged();
				}
				// if it's boiling and is on a hot surface, set boiling ticks to max (fixes taking it off and back onto a hot surface while water is still hot)
				else if (content == KettleBlock.Content.HOT_WATER) boilingTicks = TICKS_TO_BOIL;
			}
			else {
				if (content != KettleBlock.Content.EMPTY && boilingTicks > 0 && world.rand.nextFloat() < 0.25f) {
					// if not on a hot surface anymore, slowly lose heat
					boilingTicks -= 1;
					// if the kettle had hot water but all boiling ticks are gone, fade into normal water
					if (boilingTicks <= 0 && content == KettleBlock.Content.HOT_WATER) {
						BlockState oldState = getBlockState();
						
						world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1f, 1f);
						world.setBlockState(pos, state.with(KettleBlock.CONTENT, KettleBlock.Content.WATER));
						notifyChanged(oldState);
					}
					// otherwise, just notify the client about the temperature of the block
					else notifyChanged();
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
