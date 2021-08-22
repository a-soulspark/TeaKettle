package soulspark.tea_kettle.core.jei;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import soulspark.tea_kettle.TeaKettle;
import soulspark.tea_kettle.common.recipes.CupDrinkRecipe;
import soulspark.tea_kettle.core.init.ModItems;

public class CupDrinkRecipeCategory implements IRecipeCategory<CupDrinkRecipe> {
	public static final ResourceLocation UID = new ResourceLocation(TeaKettle.MODID, "cup_drink");
	protected final IDrawableStatic background;
	protected final IDrawable slot;
	protected final IDrawable slotCup;
	protected final IDrawable icon;
	protected final String title;

	public CupDrinkRecipeCategory(IGuiHelper helper) {
		background = helper.createDrawable(new ResourceLocation("tea_kettle:textures/gui/jei_cup_drink.png"), 0, 0, 150,44);
				//helper.createDrawable(new ResourceLocation(String.format("tea_kettle:textures/gui/jei_%s_pair.png", UID.getPath())), 0, 0, 96 ,44)};
		icon = helper.createDrawableIngredient(new ItemStack(ModItems.GREEN_TEA.get()));
		slot = helper.createDrawable(new ResourceLocation("tea_kettle:textures/gui/jei_cup_drink.png"), 0, 44, 18,18);
		slotCup = helper.createDrawable(new ResourceLocation("tea_kettle:textures/gui/jei_cup_drink.png"), 18, 44, 27,20);
		title = I18n.format("tea_kettle.jei." + UID.getPath());
	}
	
	@Override
	public ResourceLocation getUid() {
		return UID;
	}
	@Override
	public Class<? extends CupDrinkRecipe> getRecipeClass() { return CupDrinkRecipe.class; }
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
	public void draw(CupDrinkRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
		int ingredientCount = recipe.getIngredients().size();
		
		for (int i = 1; i < ingredientCount; i++) {
			if (i + 1 < ingredientCount) slotCup.draw(matrixStack, 11 + (i - 1) * 18, 19);
			slotCup.draw(matrixStack, 2 + (i - 1) * 18, 19);
		}
		
		for (int i = 1; i < ingredientCount; i++) slot.draw(matrixStack, 3 + (i - 1) * 18, 20);
	}
	
	@Override
	public void setIngredients(CupDrinkRecipe recipe, IIngredients iIngredients) {
		ImmutableList.Builder<ItemStack> inputs = ImmutableList.builder();
		
		for (Ingredient ingredient : recipe.getIngredients()) {
			inputs.add(ingredient.getMatchingStacks());
		}
		
		iIngredients.setInputIngredients(recipe.getIngredients());
		iIngredients.setOutput(VanillaTypes.ITEM, recipe.getRecipeOutput());
	}
	
	@Override
	public void setRecipe(IRecipeLayout iRecipeLayout, CupDrinkRecipe recipe, IIngredients iIngredients) {
		int i = 0;
		iRecipeLayout.getItemStacks().init(i, true, 21, 0);
		iRecipeLayout.getItemStacks().set(i++, iIngredients.getInputs(VanillaTypes.ITEM).get(0));
		
		for (; i < recipe.getIngredients().size(); i++) {
			iRecipeLayout.getItemStacks().init(i, true, 3 + (i - 1) * 18, 20);
			iRecipeLayout.getItemStacks().set(i, iIngredients.getInputs(VanillaTypes.ITEM).get(i));
		}
		
		iRecipeLayout.getItemStacks().init(i, true, 129, 20);
		iRecipeLayout.getItemStacks().set(i, iIngredients.getOutputs(VanillaTypes.ITEM).get(0));
	}
}