package soulspark.tea_kettle.client;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraftforge.client.model.*;
import net.minecraftforge.client.model.geometry.IModelGeometryPart;
import net.minecraftforge.client.model.geometry.IMultipartModelGeometry;

import java.util.*;
import java.util.function.Function;

public class CampfireKettleModel extends CompositeModel {
	
	public CampfireKettleModel(boolean isGui3d, boolean isSideLit, boolean isAmbientOcclusion, TextureAtlasSprite particle, ImmutableMap<String, IBakedModel> bakedParts, IModelTransform combinedTransform, ItemOverrideList overrides) {
		super(isGui3d, isSideLit, isAmbientOcclusion, particle, bakedParts, combinedTransform, overrides);
	}
	
	public static class Loader implements IModelLoader<Geometry>
	{
		public static final Loader INSTANCE = new Loader();
		
		private Loader() {}
		
		@Override
		public void onResourceManagerReload(IResourceManager resourceManager) { }
		
		@Override
		public Geometry read(JsonDeserializationContext deserializationContext, JsonObject modelContents)
		{
			if (!modelContents.has("parts"))
				throw new RuntimeException("Composite model requires a \"parts\" element.");
			ImmutableMap.Builder<String, Submodel> parts = ImmutableMap.builder();
			JsonElement transformsElement = modelContents.get("transforms");
			JsonObject transforms = null;
			
			if (transformsElement != null && transformsElement.isJsonObject()) transforms = (JsonObject)transformsElement;
			
			for(Map.Entry<String, JsonElement> part : modelContents.get("parts").getAsJsonObject().entrySet())
			{
				IModelTransform modelTransform = new SimpleModelTransform(new TransformationMatrix( Matrix4f.makeTranslate(0, transforms == null ? 0 : !transforms.has(part.getKey()) ? 0 : transforms.get(part.getKey()).getAsJsonObject().get("y").getAsFloat(), 0)));
				parts.put(part.getKey(), new Submodel(
						part.getKey(),
						deserializationContext.deserialize(part.getValue(), BlockModel.class),
						modelTransform));
			}

			return new Geometry(parts.build());
		}
	}
	
	private static class Submodel implements IModelGeometryPart
	{
		private final String name;
		private final BlockModel model;
		private final IModelTransform modelTransform;
		
		private Submodel(String name, BlockModel model, IModelTransform modelTransform)
		{
			this.name = name;
			this.model = model;
			this.modelTransform = modelTransform;
		}
		
		@Override
		public String name()
		{
			return name;
		}
		
		@Override
		public void addQuads(IModelConfiguration owner, IModelBuilder<?> modelBuilder, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ResourceLocation modelLocation)
		{
			throw new UnsupportedOperationException("Attempted to call adQuads on a Submodel instance. Please don't.");
		}
		
		public IBakedModel bakeModel(ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ResourceLocation modelLocation)
		{
			return model.bakeModel(bakery, spriteGetter, new ModelTransformComposition(this.modelTransform, modelTransform,
					this.modelTransform.isUvLock() || modelTransform.isUvLock()), modelLocation);
		}
		
		@Override
		public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors)
		{
			return model.getTextures(modelGetter, missingTextureErrors);
		}
	}
	
	public static class Geometry implements IMultipartModelGeometry<Geometry>
	{
		private final ImmutableMap<String, Submodel> parts;
		
		Geometry(ImmutableMap<String, Submodel> parts)
		{
			this.parts = parts;
		}
		
		@Override
		public Collection<? extends IModelGeometryPart> getParts()
		{
			return parts.values();
		}
		
		@Override
		public Optional<? extends IModelGeometryPart> getPart(String name)
		{
			return Optional.ofNullable(parts.get(name));
		}
		
		@Override
		public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation)
		{
			RenderMaterial particleLocation = owner.resolveTexture("particle");
			TextureAtlasSprite particle = spriteGetter.apply(particleLocation);
			
			ImmutableMap.Builder<String, IBakedModel> bakedParts = ImmutableMap.builder();
			for(Map.Entry<String, Submodel> part : parts.entrySet())
			{
				Submodel submodel = part.getValue();
				if (!owner.getPartVisibility(submodel))
					continue;
				bakedParts.put(part.getKey(), submodel.bakeModel(bakery, spriteGetter, modelTransform, modelLocation));
			}
			return new CompositeModel(owner.isShadedInGui(), owner.isSideLit(), owner.useSmoothLighting(), particle, bakedParts.build(), owner.getCombinedTransform(), overrides);
		}
		
		@Override
		public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors)
		{
			Set<RenderMaterial> textures = new HashSet<>();
			for(Submodel part : parts.values())
			{
				textures.addAll(part.getTextures(owner, modelGetter, missingTextureErrors));
			}
			return textures;
		}
	}
}