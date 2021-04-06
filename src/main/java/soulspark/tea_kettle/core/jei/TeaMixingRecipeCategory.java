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
import soulspark.tea_kettle.common.recipes.TeaMixingRecipe;
import soulspark.tea_kettle.core.init.ModItems;

public class TeaMixingRecipeCategory implements IRecipeCategory<TeaMixingRecipe> {
	public static final ResourceLocation UID = new ResourceLocation(TeaKettle.MODID, "tea_mixing");
	
	private final IDrawable background;
	private final IDrawable icon;
	private final String title;

	public TeaMixingRecipeCategory(IGuiHelper helper) {
		background = helper.createDrawable(new ResourceLocation("tea_kettle:textures/gui/jei_tea_mixing.png"), 0, 0, 96 ,44);
		icon = helper.createDrawableIngredient(new ItemStack(ModItems.BLACK_LATTE.get()));
		title = I18n.format("tea_kettle.jei.tea_mixing");
	}
	
	@Override
	public ResourceLocation getUid() {
		return UID;
	}
	@Override
	public Class<? extends TeaMixingRecipe> getRecipeClass() {
		return TeaMixingRecipe.class;
	}
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
	public void setIngredients(TeaMixingRecipe teaMixingRecipe, IIngredients iIngredients) {
		ImmutableList.Builder<ItemStack> inputs = ImmutableList.builder();
		
		for (Ingredient ingredient : teaMixingRecipe.getIngredients()) {
			inputs.add(ingredient.getMatchingStacks());
		}
		
		iIngredients.setInputs(VanillaTypes.ITEM, inputs.build());
		iIngredients.setOutput(VanillaTypes.ITEM, teaMixingRecipe.getRecipeOutput());
	}
	
	@Override
	public void setRecipe(IRecipeLayout iRecipeLayout, TeaMixingRecipe teaMixingRecipe, IIngredients iIngredients) {
		iRecipeLayout.getItemStacks().init(0, true, 6, 20);
		iRecipeLayout.getItemStacks().set(0, iIngredients.getInputs(VanillaTypes.ITEM).get(0));

		iRecipeLayout.getItemStacks().init(3, true, 72, 20);
		iRecipeLayout.getItemStacks().set(3, iIngredients.getOutputs(VanillaTypes.ITEM).get(0));
	}
}