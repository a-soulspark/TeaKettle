package soulspark.tea_kettle.common.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
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

public class CupDrinkRecipe implements IRecipe<IInventory> {
	protected final ResourceLocation id;
	protected final NonNullList<Ingredient> ingredients;
	protected final ItemStack result;
	
	public CupDrinkRecipe(ResourceLocation id, NonNullList<Ingredient> ingredients, ItemStack result) {
		this.id = id;
		this.ingredients = ingredients;
		this.result = result;
	}
	
	@Override
	public NonNullList<Ingredient> getIngredients() {
		return ingredients;
	}

	@SuppressWarnings("ConstantConditions")
	@Override
	public boolean matches(IInventory inv, World worldIn) {
		RecipeItemHelper recipeitemhelper = new RecipeItemHelper();
		int i = 0;
		
		for(int j = 0; j < inv.getSizeInventory(); ++j) {
			ItemStack itemstack = inv.getStackInSlot(j);
			if (itemstack != null // might fix #11...?
					&& !itemstack.isEmpty()) {
				++i;
				recipeitemhelper.func_221264_a(itemstack, 1);
			}
		}
				
		return i == ingredients.size() && ingredients.get(0).test(inv.getStackInSlot(0)) && recipeitemhelper.canCraft(this, null);
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
		return ModRecipeTypes.CUP_DRINK_SERIALIZER;
	}
	@Override
	public IRecipeType<?> getType() {
		return ModRecipeTypes.CUP_DRINK;
	}
	@Override
	public ItemStack getIcon() {
		return ingredients.get(0).getMatchingStacks()[0];
	}
	
	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<CupDrinkRecipe> {
		@Override
		public CupDrinkRecipe read(ResourceLocation recipeId, JsonObject json) {
			NonNullList<Ingredient> ingredients = readIngredients(JSONUtils.getJsonArray(json, "ingredients"));

			if (ingredients.isEmpty()) throw new JsonParseException("No ingredients for drink recipe");
			else if (ingredients.size() > 7) throw new JsonParseException("Too many ingredients for drink recipe, max is 7");
			else {
				String resultName = JSONUtils.getString(json, "result");
				ItemStack result = new ItemStack(Registry.ITEM.getOptional(new ResourceLocation(resultName)).orElseThrow(() -> new IllegalStateException("Item: " + resultName + " does not exist")));

				ingredients.add(0, Ingredient.deserialize(json.get("catalyst")));
				return new CupDrinkRecipe(recipeId, ingredients, result);
			}
		}
		
		private static NonNullList<Ingredient> readIngredients(JsonArray ingredientArray) {
			NonNullList<Ingredient> ingredients = NonNullList.create();
			
			for(int i = 0; i < ingredientArray.size(); ++i) {
				Ingredient ingredient = Ingredient.deserialize(ingredientArray.get(i));
				if (!ingredient.hasNoMatchingItems()) ingredients.add(ingredient);
			}
			
			return ingredients;
		}
		
		public CupDrinkRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
			int i = buffer.readVarInt();
			NonNullList<Ingredient> ingredients = NonNullList.withSize(i, Ingredient.EMPTY);
			
			for(int j = 0; j < i; ++j) ingredients.set(j, Ingredient.read(buffer));
			
			ItemStack result = buffer.readItemStack();
			return new CupDrinkRecipe(recipeId, ingredients, result);
		}
		
		public void write(PacketBuffer buffer, CupDrinkRecipe recipe) {
			buffer.writeVarInt(recipe.ingredients.size());
			for(Ingredient ingredient : recipe.ingredients) ingredient.write(buffer);
			
			buffer.writeItemStack(recipe.result);
		}
	}
}

