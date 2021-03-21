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
import soulspark.tea_kettle.core.init.ModItems;

public class TeaSteepingRecipeCategory implements IRecipeCategory<TeaSteepingRecipe> {
	public static final ResourceLocation UID = new ResourceLocation(TeaKettle.MODID, "tea_steeping");
	
	private final IDrawable background;
	private final IDrawable icon;
	private final String title;

	public TeaSteepingRecipeCategory(IGuiHelper helper) {
		background = helper.createDrawable(new ResourceLocation("tea_kettle:textures/gui/jei_tea_steeping.png"), 0, 0, 96 ,44);
		icon = helper.createDrawableIngredient(new ItemStack(ModItems.CUP.get()));
		title = I18n.format("tea_kettle.jei.tea_steeping");
	}
	
	@Override
	public ResourceLocation getUid() {
		return UID;
	}
	@Override
	public Class<? extends TeaSteepingRecipe> getRecipeClass() {
		return TeaSteepingRecipe.class;
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
	public void setIngredients(TeaSteepingRecipe teaSteepingRecipe, IIngredients iIngredients) {
		ImmutableList.Builder<ItemStack> inputs = ImmutableList.builder();
		//TeaKettle.LOGGER.info("RGAGH! setting the ingredients for {}", teaSteepingRecipe.getId());
		
		for (Ingredient ingredient : teaSteepingRecipe.getIngredients()) {
			inputs.add(ingredient.getMatchingStacks());
			//TeaKettle.LOGGER.info("yup, this is an ingredient! (this: {})", (Object) ingredient.getMatchingStacks());
		}
		
		iIngredients.setInputIngredients(teaSteepingRecipe.getIngredients());
		//iIngredients.setInputs(VanillaTypes.ITEM, inputs.build());
		iIngredients.setOutput(VanillaTypes.ITEM, teaSteepingRecipe.getRecipeOutput());
	}
	
	@Override
	public void setRecipe(IRecipeLayout iRecipeLayout, TeaSteepingRecipe teaSteepingRecipe, IIngredients iIngredients) {
		//List<List<ItemStack>> inputs = iIngredients.getInputs(VanillaTypes.ITEM);
		/*
		iRecipeLayout.getItemStacks().init(0, true, 6, 20);
		iRecipeLayout.getItemStacks().set(0, new ItemStack(ModItems.CUP.get()));
		*/
		
		iRecipeLayout.getItemStacks().init(0, true, 6, 20);
		iRecipeLayout.getItemStacks().set(0, iIngredients.getInputs(VanillaTypes.ITEM).get(0));
		/*
		ItemStack kettleStack = new ItemStack(ModItems.KETTLE.get());
		CompoundNBT tag = new CompoundNBT();
		CompoundNBT blockStateTag = tag.getCompound("BlockStateTag");
		blockStateTag.putString("content", "hot_water");
		tag.put("BlockStateTag", blockStateTag);
		kettleStack.setTag(tag);
		
		iRecipeLayout.getItemStacks().init(2, true, 38, 0);
		iRecipeLayout.getItemStacks().set(2, kettleStack);
		*/
		iRecipeLayout.getItemStacks().init(3, true, 72, 20);
		iRecipeLayout.getItemStacks().set(3, iIngredients.getOutputs(VanillaTypes.ITEM).get(0));
		
//		iRecipeLayout.getItemStacks().init(1, true, 10, 35);
//		iRecipeLayout.getItemStacks().set(1, new ItemStack(ModItems.CUP.get()));
/*ItemStack kettleStack = new ItemStack(ModItems.KETTLE.get());
		CompoundNBT tag = new CompoundNBT();
		CompoundNBT blockStateTag = tag.getCompound("BlockStateTag");
		blockStateTag.putString("content", "hot_water");
		tag.put("BlockStateTag", blockStateTag);
		kettleStack.setTag(tag);*//*

		
		iRecipeLayout.getItemStacks().init(2, true, 40, 35);
		iRecipeLayout.getItemStacks().set(2, new ItemStack(ModItems.KETTLE.get()));
		
		iRecipeLayout.getItemStacks().init(0, true, 10, 35);
		iRecipeLayout.getItemStacks().set(0, iIngredients.getOutputs(VanillaTypes.ITEM).get(0));
		*/
	}
}