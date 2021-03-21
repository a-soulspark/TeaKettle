package soulspark.tea_kettle.common.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.registries.ForgeRegistryEntry;
import soulspark.tea_kettle.core.init.ModRecipeTypes;

public class ShearingRecipe extends ShapelessRecipe {
	public ShearingRecipe(ResourceLocation idIn, String groupIn, ItemStack recipeOutputIn, NonNullList<Ingredient> recipeItemsIn) {
		super(idIn, groupIn, recipeOutputIn, recipeItemsIn);
	}
	
	@Override
	public IRecipeSerializer<?> getSerializer() {
		return ModRecipeTypes.SHEARING_SERIALIZER;
	}
	
	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv) {
		// get the default remaining items for a shapeless recipe
		NonNullList<ItemStack> remainingItems = super.getRemainingItems(inv);
		
		// loop through each slot in the crafting inventory
		for (int i = 0; i < remainingItems.size(); ++i) {
			ItemStack stack = inv.getStackInSlot(i);
			// if the item in the slot are shears...
			if (stack.getItem() instanceof ShearsItem) {
				// try to damage the item, but this is *ugly*!
				PlayerEntity player = ForgeHooks.getCraftingPlayer();
				ServerPlayerEntity serverPlayer = null;
				if (player instanceof ServerPlayerEntity) serverPlayer = (ServerPlayerEntity) player;
				if (player != null && player.abilities.isCreativeMode || !stack.attemptDamageItem(1, player != null ? player.world.rand : new SharedSeedRandom(), serverPlayer))
					remainingItems.set(i, stack.copy());
			}
		}
		
		return remainingItems;
	}
	
	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<ShearingRecipe> {
		public ShearingRecipe read(ResourceLocation recipeId, JsonObject json) {
			String s = JSONUtils.getString(json, "group", "");
			NonNullList<Ingredient> nonnulllist = readIngredients(JSONUtils.getJsonArray(json, "ingredients"));
			if (nonnulllist.isEmpty()) {
				throw new JsonParseException("No ingredients for shapeless recipe");
			} else {
				ItemStack itemstack = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result"));
				return new ShearingRecipe(recipeId, s, itemstack, nonnulllist);
			}
		}
		
		private static NonNullList<Ingredient> readIngredients(JsonArray ingredientArray) {
			NonNullList<Ingredient> nonnulllist = NonNullList.create();
			
			for(int i = 0; i < ingredientArray.size(); ++i) {
				Ingredient ingredient = Ingredient.deserialize(ingredientArray.get(i));
				if (!ingredient.hasNoMatchingItems()) {
					nonnulllist.add(ingredient);
				}
			}
			
			return nonnulllist;
		}
		
		public ShearingRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
			String s = buffer.readString(32767);
			int i = buffer.readVarInt();
			NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i, Ingredient.EMPTY);
			
			for(int j = 0; j < nonnulllist.size(); ++j) {
				nonnulllist.set(j, Ingredient.read(buffer));
			}
			
			ItemStack itemstack = buffer.readItemStack();
			return new ShearingRecipe(recipeId, s, itemstack, nonnulllist);
		}
		
		public void write(PacketBuffer buffer, ShearingRecipe recipe) {
			buffer.writeString(recipe.getGroup());
			buffer.writeVarInt(recipe.getIngredients().size());
			
			for(Ingredient ingredient : recipe.getIngredients()) {
				ingredient.write(buffer);
			}
			
			buffer.writeItemStack(recipe.getRecipeOutput());
		}
	}
}
