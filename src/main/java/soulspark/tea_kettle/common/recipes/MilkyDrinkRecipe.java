package soulspark.tea_kettle.common.recipes;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import soulspark.tea_kettle.core.init.ModItems;
import soulspark.tea_kettle.core.init.ModRecipeTypes;

public class MilkyDrinkRecipe extends TeaSteepingRecipe {
	public MilkyDrinkRecipe(ResourceLocation id, NonNullList<Ingredient> ingredients, ItemStack result) {
		super(id, ingredients, result);
	}
	@Override
	public IRecipeType<?> getType() {
		return ModRecipeTypes.MILKY_DRINK;
	}
	@Override
	public ItemStack getIcon() {
		return new ItemStack(ModItems.FROTHING_KETTLE.get());
	}
}
