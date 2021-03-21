package soulspark.tea_kettle.core.init;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import soulspark.tea_kettle.TeaKettle;
import soulspark.tea_kettle.client.CupTileEntityRenderer;
import soulspark.tea_kettle.client.TeaModel;

public class ClientInitEvents {
	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event) {
		RenderTypeLookup.setRenderLayer(ModBlocks.CUP.get(), RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(ModBlocks.EMPTY_KETTLE.get(), RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(ModBlocks.WATER_KETTLE.get(), RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(ModBlocks.MILK_KETTLE.get(), RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(ModBlocks.TEA_BUSH.get(), RenderType.getCutout());
		
		RenderTypeLookup.setRenderLayer(ModBlocks.GREEN_TEA.get(), RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModBlocks.WHITE_TEA.get(), RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModBlocks.OOLONG_TEA.get(), RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModBlocks.BLACK_TEA.get(), RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModBlocks.BAMBOO_TEA.get(), RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModBlocks.ROSE_TEA.get(), RenderType.getTranslucent());
		
		RenderTypeLookup.setRenderLayer(ModBlocks.GREEN_LATTE.get(), RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModBlocks.WHITE_LATTE.get(), RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModBlocks.OOLONG_LATTE.get(), RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ModBlocks.BLACK_LATTE.get(), RenderType.getTranslucent());
		
		ItemModelsProperties.registerProperty(ModItems.BOILING_KETTLE.get(), new ResourceLocation(TeaKettle.MODID, "fullness"), ClientInitEvents::getFullness);
		ItemModelsProperties.registerProperty(ModItems.BOILING_KETTLE.get(), new ResourceLocation(TeaKettle.MODID, "fullness"), ClientInitEvents::getFullness);
		ItemModelsProperties.registerProperty(ModItems.FROTHING_KETTLE.get(), new ResourceLocation(TeaKettle.MODID, "fullness"), ClientInitEvents::getFullness);
		
		ClientRegistry.bindTileEntityRenderer(ModTileEntities.CUP.get(), CupTileEntityRenderer::new);
	}
	
	@SubscribeEvent
	public static void onModelRegistry(ModelRegistryEvent event) {
		ModelLoaderRegistry.registerLoader(new ResourceLocation(TeaKettle.MODID, "tea"), TeaModel.Loader.INSTANCE);
	}
	
	@SubscribeEvent
	public static void onTextureStitch(TextureStitchEvent.Pre event) {
		event.addSprite(TeaModel.BASE_STEAM);
		event.addSprite(TeaModel.WEAK_STEAM);
		event.addSprite(TeaModel.STRONG_STEAM);
		ModItems.TEAS.forEach(reg -> event.addSprite(new ResourceLocation(TeaKettle.MODID, "item/" + reg.getId().getPath())));
	}
	
	public static float getFullness(ItemStack stack, World world, Entity entity) {
		if (!stack.hasTag()) return 1;
		
		int fullness = stack.getOrCreateChildTag("BlockStateTag").getInt("fullness");
		if (fullness == 0) fullness = 4;
		return fullness / 4.0f;
	}
	
	public static float getSweetness(ItemStack stack) {
		if (!stack.hasTag()) return 0;
		
		String sweetnessString = stack.getOrCreateChildTag("BlockStateTag").getString("sweetness");
		int sweetness = sweetnessString.isEmpty() ? 0 : Integer.parseInt(sweetnessString);
		return sweetness / 2.0f;
	}
}
