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
import soulspark.tea_kettle.core.init.ModItems;
import soulspark.tea_kettle.core.init.ModRecipeTypes;

@JeiPlugin
public class TeaKettleJEIPlugin implements IModPlugin {

	@Override
	public ResourceLocation getPluginUid() {
		return new ResourceLocation("tea_kettle", "main");
	}
	
	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {
		registry.addRecipeCategories(
				new CupDrinkRecipeCategory(registry.getJeiHelpers().getGuiHelper()),
				new TeaMixingRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
	}
	
	@Override
	public void registerRecipes(IRecipeRegistration registry) {
		World world = Minecraft.getInstance().world;
		if (world != null) {
			registry.addRecipes(world.getRecipeManager().getRecipesForType(ModRecipeTypes.CUP_DRINK), CupDrinkRecipeCategory.UID);
			registry.addRecipes(world.getRecipeManager().getRecipesForType(ModRecipeTypes.TEA_MIXING), TeaMixingRecipeCategory.UID);
			registry.addRecipes(world.getRecipeManager().getRecipesForType(ModRecipeTypes.SHEARING), VanillaRecipeCategoryUid.CRAFTING);
		}
	}
	
	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
		ItemStack stack = new ItemStack(ModItems.CUP.get());
		registry.addRecipeCatalyst(stack, CupDrinkRecipeCategory.UID);
		
		//registry.addRecipeCatalyst(new ItemStack(ModItems.BOILING_KETTLE.get()), cupDrinkCategory.UID);
		
		stack = new ItemStack(ModItems.FROTHING_KETTLE.get());
		registry.addRecipeCatalyst(stack, TeaMixingRecipeCategory.UID);
		
		if (ModList.get().isLoaded("simplytea")) {
			//registry.addRecipeCatalyst(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("simplytea:teapot_hot"))), cupDrinkCategory.UID);
			stack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("simplytea:teapot_frothed")));
			registry.addRecipeCatalyst(stack, TeaMixingRecipeCategory.UID);

			stack = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("simplytea:cup")));
			registry.addRecipeCatalyst(stack, CupDrinkRecipeCategory.UID);
		}
	}
}