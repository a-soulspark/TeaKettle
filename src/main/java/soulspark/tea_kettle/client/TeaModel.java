package soulspark.tea_kettle.client;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraftforge.client.model.*;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.VanillaResourceType;
import soulspark.tea_kettle.TeaKettle;
import soulspark.tea_kettle.core.init.ClientInitEvents;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class TeaModel implements IModelGeometry<TeaModel> {
	public static final ResourceLocation BASE_STEAM = new ResourceLocation(TeaKettle.MODID, "item/tea_steam");
	public static final ResourceLocation WEAK_STEAM = new ResourceLocation(TeaKettle.MODID, "item/tea_steam_weak");
	public static final ResourceLocation STRONG_STEAM = new ResourceLocation(TeaKettle.MODID, "item/tea_steam_strong");
	
	private final ResourceLocation baseLocation;
	private final ResourceLocation steamLocation;
	
	public TeaModel(ResourceLocation baseLocation, ResourceLocation steamLocation) {
		this.steamLocation = steamLocation;
		this.baseLocation = baseLocation;
	}
	
	@Override
	public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation) {
		RenderMaterial steamTexture = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS_TEXTURE, steamLocation);
		RenderMaterial baseTexture = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS_TEXTURE, baseLocation);
		
		TeaKettle.LOGGER.info("Base tex: {}, mode loc: {}, base loc: {}, steam loc: {}", baseTexture.getTextureLocation(), modelLocation, baseLocation, baseTexture);
		ImmutableMap<ItemCameraTransforms.TransformType, TransformationMatrix> transformMap = PerspectiveMapWrapper.getTransforms(new ModelTransformComposition(owner.getCombinedTransform(), modelTransform));
		
		TransformationMatrix transform = modelTransform.getRotation();
		ItemMultiLayerBakedModel.Builder builder = ItemMultiLayerBakedModel.builder(owner, spriteGetter.apply(baseTexture), new TeaOverrideList(owner, bakery, this), transformMap);
		
		builder.addQuads(ItemLayerModel.getLayerRenderType(false), ItemLayerModel.getQuadsForSprites(ImmutableList.of(baseTexture), transform, spriteGetter));
		builder.addQuads(ItemLayerModel.getLayerRenderType(false), ItemLayerModel.getQuadsForSprites(ImmutableList.of(steamTexture), transform, spriteGetter));
		
		return builder.build();
	}
	
	@Override
	public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
		return Sets.newHashSet();
	}
	
	public TeaModel withSteam(ResourceLocation steamTexture) {
		return new TeaModel(baseLocation, steamTexture);
	}
	
	public enum Loader implements IModelLoader<TeaModel> {
		INSTANCE;
		
		@Override
		public IResourceType getResourceType()
		{
			return VanillaResourceType.MODELS;
		}
		
		@Override
		public void onResourceManagerReload(IResourceManager resourceManager) { }
		@Override
		public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate) { }
		
		@Override
		public TeaModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
			return new TeaModel(new ResourceLocation(modelContents.getAsJsonObject("textures").get("base").getAsString()), BASE_STEAM);
		}
	}
	
	private static final class TeaOverrideList extends ItemOverrideList {
		private IBakedModel WEAK_MODEL;
		private IBakedModel STRONG_MODEL;
		
		private final IModelConfiguration owner;
		private final ModelBakery bakery;
		private final TeaModel parent;
		
		public TeaOverrideList(IModelConfiguration owner, ModelBakery bakery, TeaModel parent) {
			this.owner = owner;
			this.bakery = bakery;
			this.parent = parent;
		}
		
		@Nullable
		@Override
		public IBakedModel getOverrideModel(IBakedModel model, ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity livingEntity) {
			float sweetness = ClientInitEvents.getSweetness(stack);
			
			if (sweetness == 0) return model;
			else if (sweetness <= 0.5f) {
				if (WEAK_MODEL == null)
					WEAK_MODEL = parent.withSteam(WEAK_STEAM).bake(owner, bakery, ModelLoader.defaultTextureGetter(), ModelRotation.X0_Y0, this, new ResourceLocation("tea_kettle:tea_override"));
				return WEAK_MODEL;
			}
			else {
				if (STRONG_MODEL == null)
					STRONG_MODEL = parent.withSteam(STRONG_STEAM).bake(owner, bakery, ModelLoader.defaultTextureGetter(), ModelRotation.X0_Y0, this, new ResourceLocation("tea_kettle:tea_override"));
				return STRONG_MODEL;
			}
		}
	}
}