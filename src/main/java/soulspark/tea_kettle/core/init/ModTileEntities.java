package soulspark.tea_kettle.core.init;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import soulspark.tea_kettle.TeaKettle;
import soulspark.tea_kettle.common.tile_entities.CupTileEntity;
import soulspark.tea_kettle.common.tile_entities.KettleTileEntity;
import soulspark.tea_kettle.common.tile_entities.LegacyKettleTileEntity;

@SuppressWarnings("ConstantConditions")
public class ModTileEntities {
	public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, TeaKettle.MODID);
	
	public static final RegistryObject<TileEntityType<LegacyKettleTileEntity>> LEGACY_KETTLE = TILE_ENTITIES.register("kettle", () -> TileEntityType.Builder.create(LegacyKettleTileEntity::new, ModBlocks.LEGACY_KETTLE.get()).build(null));
	public static final RegistryObject<TileEntityType<KettleTileEntity>> FILLED_KETTLE = TILE_ENTITIES.register("filled_kettle", () -> TileEntityType.Builder.create(KettleTileEntity::new, ModBlocks.WATER_KETTLE.get(), ModBlocks.MILK_KETTLE.get()).build(null));
	public static final RegistryObject<TileEntityType<CupTileEntity>> CUP = TILE_ENTITIES.register("cup", () -> TileEntityType.Builder.create(CupTileEntity::new, ModBlocks.CUP.get()).build(null));
	
}
