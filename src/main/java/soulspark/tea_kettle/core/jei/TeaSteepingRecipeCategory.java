package soulspark.tea_kettle.core.jei;

import com.google.common.collect.ImmutableList;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import soulspark.tea_kettle.TeaKettle;
import soulspark.tea_kettle.common.recipes.TeaSteepingRecipe;

public class TeaSteepingRecipeCategory<T extends TeaSteepingRecipe> implements IRecipeCategory<T> {
	public final ResourceLocation UID;
	protected final Class<? extends T> recipeClass;
	protected final IDrawable background;
	protected final IDrawable icon;
	protected final String title;

	public TeaSteepingRecipeCategory(Class<? extends T> recipeClass, String name, ItemStack iconStack, IGuiHelper helper) {
		this.recipeClass = recipeClass;
		UID = new ResourceLocation(TeaKettle.MODID, name);
		background = helper.createDrawable(new ResourceLocation(String.format("tea_kettle:textures/gui/jei_%s.png", UID.getPath())), 0, 0, 96 ,44);
		icon = helper.createDrawableIngredient(iconStack);
		title = I18n.format("tea_kettle.jei." + UID.getPath());
	}
	
	@Override
	public ResourceLocation getUid() {
		return UID;
	}
	@Override
	public Class<? extends T> getRecipeClass() { return recipeClass; }
	@Override
	public String getTitle() {
		return title;
	}
	@Override
	public IDrawable getBackground() {
		return background;
	}
	@Override
	public IDrawable getIcon() {
		return icon;
	}
	
	@Override
	public void setIngredients(T recipe, IIngredients iIngredients) {
		ImmutableList.Builder<ItemStack> inputs = ImmutableList.builder();
		
		for (Ingredient ingredient : recipe.getIngredients()) {
			inputs.add(ingredient.getMatchingStacks());
		}
		
		iIngredients.setInputIngredients(recipe.getIngredients());
		iIngredients.setOutput(VanillaTypes.ITEM, recipe.getRecipeOutput());
	}
	
	@Override
	public void setRecipe(IRecipeLayout iRecipeLayout, T recipe, IIngredients iIngredients) {
		iRecipeLayout.getItemStacks().init(0, true, 6, 20);
		iRecipeLayout.getItemStacks().set(0, iIngredients.getInputs(VanillaTypes.ITEM).get(0));
		
		iRecipeLayout.getItemStacks().init(3, true, 72, 20);
		iRecipeLayout.getItemStacks().set(3, iIngredients.getOutputs(VanillaTypes.ITEM).get(0));
	}
}