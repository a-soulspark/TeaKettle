package com.soulspark.kettle.core.init;

import com.soulspark.kettle.Kettle;
import com.soulspark.kettle.client.tile_entity.render.CupTileEntityRenderer;
import com.soulspark.kettle.common.items.KettleItem;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientInitEvents {
	@SubscribeEvent
	public static void onClientSetupEvent(FMLClientSetupEvent event) {
		RenderTypeLookup.setRenderLayer(ModBlocks.KETTLE.get(), RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(ModBlocks.TEA_BUSH.get(), RenderType.getCutout());
		
		RenderTypeLookup.setRenderLayer(ModBlocks.GREEN_TEA.get(), RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModBlocks.WHITE_TEA.get(), RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModBlocks.OOLONG_TEA.get(), RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModBlocks.BLACK_TEA.get(), RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModBlocks.BAMBOO_TEA.get(), RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModBlocks.ROSE_TEA.get(), RenderType.getTranslucent());
		
		ItemModelsProperties.registerProperty(ModItems.KETTLE.get(), new ResourceLocation(Kettle.MODID, "content"), (stack, world, entity) -> {
			String content = KettleItem.getContentFromStack(stack);
			switch (content) {
				case "hot_water_full": return 1;
				case "hot_water": return 0.75f;
				case "water": return 0.5f;
				default: return 0;
			}
		});
		
		ClientRegistry.bindTileEntityRenderer(ModTileEntities.CUP.get(), CupTileEntityRenderer::new);
		
		Kettle.LOGGER.info("Hello, setting client up for ya.");
	}
}
