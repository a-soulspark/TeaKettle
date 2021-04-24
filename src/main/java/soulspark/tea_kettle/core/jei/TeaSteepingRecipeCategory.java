package soulspark.tea_kettle.core.jei;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
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
	protected final IDrawable[] backgrounds;
	protected final IDrawable icon;
	protected final String title;

	public TeaSteepingRecipeCategory(Class<? extends T> recipeClass, String name, ItemStack iconStack, IGuiHelper helper) {
		this.recipeClass = recipeClass;
		UID = new ResourceLocation(TeaKettle.MODID, name);
		backgrounds = new IDrawable[] {
				helper.createDrawable(new ResourceLocation(String.format("tea_kettle:textures/gui/jei_%s.png", UID.getPath())), 0, 0, 96 ,44),
				helper.createDrawable(new ResourceLocation(String.format("tea_kettle:textures/gui/jei_%s_pair.png", UID.getPath())), 0, 0, 96 ,44)};
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
		return backgrounds[0];
	}
	@Override
	public IDrawable getIcon() {
		return icon;
	}
	
	@Override
	public void draw(T recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
		int ingredientCount = recipe.getIngredients().size();
		
		if (ingredientCount == 2) {
			backgrounds[1].draw(matrixStack);
		}
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
		int i = 0;
		iRecipeLayout.getItemStacks().init(i, true, 6, 20);
		iRecipeLayout.getItemStacks().set(i++, iIngredients.getInputs(VanillaTypes.ITEM).get(0));
		
		if (recipe.getIngredients().size() > 1) {
			iRecipeLayout.getItemStacks().init(i, true, 24, 20);
			iRecipeLayout.getItemStacks().set(i++, iIngredients.getInputs(VanillaTypes.ITEM).get(1));
		}
		
		iRecipeLayout.getItemStacks().init(i, true, 73, 20);
		iRecipeLayout.getItemStacks().set(i, iIngredients.getOutputs(VanillaTypes.ITEM).get(0));
	}
}