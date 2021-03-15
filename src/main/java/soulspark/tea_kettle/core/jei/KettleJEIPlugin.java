package soulspark.tea_kettle.core.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import soulspark.tea_kettle.core.init.ModItems;
import soulspark.tea_kettle.core.init.ModRecipeTypes;

@JeiPlugin
public class KettleJEIPlugin implements IModPlugin {
	@Override
	public ResourceLocation getPluginUid() {
		return new ResourceLocation("tea_kettle", "main");
	}
	
	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {
		registry.addRecipeCategories(new TeaSteepingRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
	}
	
	@Override
	public void registerRecipes(IRecipeRegistration registry) {
		World world = Minecraft.getInstance().world;
		if (world != null) {
			registry.addRecipes(world.getRecipeManager().getRecipesForType(ModRecipeTypes.TEA_STEEPING), TeaSteepingRecipeCategory.UID);
			registry.addRecipes(world.getRecipeManager().getRecipesForType(ModRecipeTypes.SHEARING), VanillaRecipeCategoryUid.CRAFTING);
		}
	}
	
	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
		registry.addRecipeCatalyst(new ItemStack(ModItems.CUP.get()), TeaSteepingRecipeCategory.UID);
		
		ItemStack kettleStack = new ItemStack(ModItems.BOILING_KETTLE.get());
		CompoundNBT tag = new CompoundNBT();
		CompoundNBT blockStateTag = tag.getCompound("BlockStateTag");
		blockStateTag.putString("content", "hot_water");
		blockStateTag.putInt("fullness", 2);
		tag.put("BlockStateTag", blockStateTag);
		kettleStack.setTag(tag);
		
		registry.addRecipeCatalyst(kettleStack, TeaSteepingRecipeCategory.UID);
	}
}