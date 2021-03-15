package soulspark.tea_kettle.core.init;

import soulspark.tea_kettle.TeaKettle;
import soulspark.tea_kettle.client.tile_entity.render.CupTileEntityRenderer;
import soulspark.tea_kettle.common.items.KettleItem;
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
		RenderTypeLookup.setRenderLayer(ModBlocks.EMPTY_KETTLE.get(), RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(ModBlocks.WATER_KETTLE.get(), RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(ModBlocks.BOILING_KETTLE.get(), RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(ModBlocks.CUP.get(), RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(ModBlocks.TEA_BUSH.get(), RenderType.getCutout());
		
		RenderTypeLookup.setRenderLayer(ModBlocks.GREEN_TEA.get(), RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModBlocks.WHITE_TEA.get(), RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModBlocks.OOLONG_TEA.get(), RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModBlocks.BLACK_TEA.get(), RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModBlocks.BAMBOO_TEA.get(), RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModBlocks.ROSE_TEA.get(), RenderType.getTranslucent());
		
		ClientRegistry.bindTileEntityRenderer(ModTileEntities.CUP.get(), CupTileEntityRenderer::new);
	}
}
