package soulspark.tea_kettle.common.recipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;
import soulspark.tea_kettle.core.init.ModRecipeTypes;

public class TeaSteepingRecipe implements IRecipe<IInventory> {
	private final ResourceLocation id;
	private final Ingredient ingredient;
	private final ItemStack result;
	
	public TeaSteepingRecipe(ResourceLocation id, Ingredient ingredient, ItemStack result) {
		this.id = id;
		this.ingredient = ingredient;
		this.result = result;
	}
	
	@Override
	public NonNullList<Ingredient> getIngredients() {
		return NonNullList.from(ingredient, ingredient);
	}
	@Override
	public boolean matches(IInventory inv, World worldIn) {
		return ingredient.test(inv.getStackInSlot(0));
	}
	@Override
	public ItemStack getCraftingResult(IInventory inv) {
		return result.copy();
	}
	@Override
	public boolean canFit(int width, int height) {
		return true;
	}
	@Override
	public ItemStack getRecipeOutput() {
		return result;
	}
	@Override
	public ResourceLocation getId() {
		return id;
	}
	@Override
	public IRecipeSerializer<?> getSerializer() {
		return ModRecipeTypes.TEA_STEEPING_SERIALIZER;
	}
	@Override
	public IRecipeType<?> getType() {
		return ModRecipeTypes.TEA_STEEPING;
	}
	
	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<TeaSteepingRecipe> {
		@Override
		public TeaSteepingRecipe read(ResourceLocation recipeId, JsonObject json) {
			JsonElement ingredientJson = (JSONUtils.isJsonArray(json, "ingredient") ? JSONUtils.getJsonArray(json, "ingredient") : JSONUtils.getJsonObject(json, "ingredient"));
			Ingredient ingredient = Ingredient.deserialize(ingredientJson);
			
			// throw error if there's no result
			if (!json.has("result")) throw new com.google.gson.JsonSyntaxException("Missing result, expected to find a string or object");
			
			ItemStack itemStack;
			if (json.get("result").isJsonObject()) itemStack = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result"));
			else {
				String s1 = JSONUtils.getString(json, "result");
				ResourceLocation resourcelocation = new ResourceLocation(s1);
				itemStack = new ItemStack(Registry.ITEM.getOptional(resourcelocation).orElseThrow(() -> new IllegalStateException("Item: " + s1 + " does not exist")));
			}
			
			return new TeaSteepingRecipe(recipeId, ingredient, itemStack);
		}
		
		public TeaSteepingRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
			Ingredient ingredient = Ingredient.read(buffer);
			ItemStack itemstack = buffer.readItemStack();
			return new TeaSteepingRecipe(recipeId, ingredient, itemstack);
		}
		
		public void write(PacketBuffer buffer, TeaSteepingRecipe recipe) {
			recipe.ingredient.write(buffer);
			buffer.writeItemStack(recipe.result);
		}
	}
}
