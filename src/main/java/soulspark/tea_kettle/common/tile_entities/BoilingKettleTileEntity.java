package soulspark.tea_kettle.common.tile_entities;

import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import soulspark.tea_kettle.common.blocks.KettleBlock;
import soulspark.tea_kettle.core.init.ModParticles;
import soulspark.tea_kettle.core.init.ModTileEntities;

public class BoilingKettleTileEntity extends TileEntity implements ITickableTileEntity {
	public BoilingKettleTileEntity() {
		super(ModTileEntities.BOILING_KETTLE.get());
	}
	
	@Override
	public void tick() {
		if (world != null && world.isRemote) {
			// emits steam particles
			BlockPos offset = pos.offset(getBlockState().get(KettleBlock.FACING).rotateYCCW()).subtract(pos);
			world.addParticle(ModParticles.STEAM.get(), pos.getX() + 0.5f + offset.getX() * 0.45f, pos.getY() + 0.35f, pos.getZ() + 0.5f + offset.getZ() * 0.45f, 0.0D, 0.05D, 0.0D);
		}
	}
}
