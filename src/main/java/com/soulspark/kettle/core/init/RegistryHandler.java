package com.soulspark.kettle.core.init;

import net.minecraftforge.eventbus.api.IEventBus;

public class RegistryHandler {
	public static void register(IEventBus bus) {
		ModEffects.EFFECTS.register(bus);
		ModItems.ITEMS.register(bus);
		ModBlocks.BLOCKS.register(bus);
		ModTileEntities.TILE_ENTITIES.register(bus);
		ModParticles.PARTICLES.register(bus);
		ModFeatures.FEATURES.register(bus);
	}
}
