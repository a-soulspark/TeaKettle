package soulspark.tea_kettle.core.init;

import soulspark.tea_kettle.TeaKettle;
import soulspark.tea_kettle.core.worldgen.TeaBushFeature;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.placement.ChanceConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModFeatures {
	public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, TeaKettle.MODID);
	
	public static final RegistryObject<Feature<NoFeatureConfig>> TEA_BUSH_FEATURE = FEATURES.register("tea_bush", TeaBushFeature::new);
	
	public static ConfiguredFeature<?, ?> TEA_BUSH;
	
	public static void registerConfiguredFeatures() {
		TEA_BUSH = TEA_BUSH_FEATURE.get().withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG)
				.withPlacement(Placement.CHANCE.configure(new ChanceConfig(4)))
				.withPlacement(Features.Placements.HEIGHTMAP_PLACEMENT);
		Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, new ResourceLocation("tea_kettle:tea_bush"), ModFeatures.TEA_BUSH);
	}
}
