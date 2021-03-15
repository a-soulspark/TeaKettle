package soulspark.tea_kettle.core.init;

import soulspark.tea_kettle.TeaKettle;
import soulspark.tea_kettle.common.tile_entities.BoilingKettleTileEntity;
import soulspark.tea_kettle.common.tile_entities.CupTileEntity;
import soulspark.tea_kettle.common.tile_entities.WaterKettleTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@SuppressWarnings("ConstantConditions")
public class ModTileEntities {
	public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, TeaKettle.MODID);
	
	public static final RegistryObject<TileEntityType<WaterKettleTileEntity>> WATER_KETTLE = TILE_ENTITIES.register("water_kettle", () -> TileEntityType.Builder.create(WaterKettleTileEntity::new, ModBlocks.WATER_KETTLE.get()).build(null));
	public static final RegistryObject<TileEntityType<BoilingKettleTileEntity>> BOILING_KETTLE = TILE_ENTITIES.register("boiling_kettle", () -> TileEntityType.Builder.create(BoilingKettleTileEntity::new, ModBlocks.BOILING_KETTLE.get()).build(null));
	public static final RegistryObject<TileEntityType<CupTileEntity>> CUP = TILE_ENTITIES.register("cup", () -> TileEntityType.Builder.create(CupTileEntity::new, ModBlocks.CUP.get()).build(null));
	
}
