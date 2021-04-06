package soulspark.tea_kettle.core.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import soulspark.tea_kettle.common.recipes.MilkyDrinkRecipe;
import soulspark.tea_kettle.common.recipes.TeaSteepingRecipe;
import soulspark.tea_kettle.core.init.ModItems;
import soulspark.tea_kettle.core.init.ModRecipeTypes;

@JeiPlugin
public class KettleJEIPlugin implements IModPlugin {
	public TeaSteepingRecipeCategory<TeaSteepingRecipe> teaSteepingCategory;
	public TeaSteepingRecipeCategory<MilkyDrinkRecipe> teaMilkingCategory;
	
	@Override
	public ResourceLocation getPluginUid() {
		return new ResourceLocation("tea_kettle", "main");
	}
	
	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {
		teaSteepingCategory = new TeaSteepingRecipeCategory<>(TeaSteepingRecipe.class, "tea_steeping", new ItemStack(ModItems.GREEN_TEA.get()), registry.getJeiHelpers().getGuiHelper());
		teaMilkingCategory = new TeaSteepingRecipeCategory<>(MilkyDrinkRecipe.class, "milky_drink", new ItemStack(ModItems.FROTHING_KETTLE.get()), registry.getJeiHelpers().getGuiHelper());
		registry.addRecipeCategories(teaSteepingCategory);
		registry.addRecipeCategories(teaMilkingCategory);
		
		registry.addRecipeCategories(new TeaMixingRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
	}
	
	@Override
	public void registerRecipes(IRecipeRegistration registry) {
		World world = Minecraft.getInstance().world;
		if (world != null) {
			registry.addRecipes(world.getRecipeManager().getRecipesForType(ModRecipeTypes.TEA_STEEPING), teaSteepingCategory.UID);
			registry.addRecipes(world.getRecipeManager().getRecipesForType(ModRecipeTypes.MILKY_DRINK), teaMilkingCategory.UID);
			registry.addRecipes(world.getRecipeManager().getRecipesForType(ModRecipeTypes.TEA_MIXING), TeaMixingRecipeCategory.UID);
			registry.addRecipes(world.getRecipeManager().getRecipesForType(ModRecipeTypes.SHEARING), VanillaRecipeCategoryUid.CRAFTING);
		}
	}
	
	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
		ItemStack stack = new ItemStack(ModItems.CUP.get());
		registry.addRecipeCatalyst(stack, teaSteepingCategory.UID);
		registry.addRecipeCatalyst(stack, teaMilkingCategory.UID);
		
		registry.addRecipeCatalyst(new ItemStack(ModItems.BOILING_KETTLE.get()), teaSteepingCategory.UID);
		
		stack = new ItemStack(ModItems.FROTHING_KETTLE.get());
		registry.addRecipeCatalyst(stack, teaMilkingCategory.UID);
		registry.addRecipeCatalyst(stack, TeaMixingRecipeCategory.UID);
		
		if (ModList.get().isLoaded("simplytea")) {
			registry.addRecipeCatalyst(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("simplytea:teapot_hot"))), teaSteepingCategory.UID);
			
			stack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("simplytea:teapot_frothed")));
			registry.addRecipeCatalyst(stack, teaMilkingCategory.UID);
			registry.addRecipeCatalyst(stack, TeaMixingRecipeCategory.UID);
		}
	}
}