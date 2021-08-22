package soulspark.tea_kettle.core.init;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import soulspark.tea_kettle.TeaKettle;
import soulspark.tea_kettle.common.recipes.ConfigCondition;
import soulspark.tea_kettle.common.recipes.CupDrinkRecipe;
import soulspark.tea_kettle.common.recipes.ShearingRecipe;
import soulspark.tea_kettle.common.recipes.TeaMixingRecipe;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = TeaKettle.MODID)
public class ModRecipeTypes {
	public static final IRecipeType<CupDrinkRecipe> CUP_DRINK = createRecipeType();
	public static final IRecipeType<TeaMixingRecipe> TEA_MIXING = createRecipeType();
	public static final IRecipeType<ShearingRecipe> SHEARING = createRecipeType();
	
	static <T extends IRecipe<?>> IRecipeType<T> createRecipeType() {
		return new IRecipeType<T>() {
			public String toString() {
				ResourceLocation key = Registry.RECIPE_TYPE.getKey(this);
				return (key != null) ? key.toString() : "";
			}
		};
	}
	
	public static final IRecipeSerializer<CupDrinkRecipe> CUP_DRINK_SERIALIZER = new CupDrinkRecipe.Serializer();
	public static final IRecipeSerializer<TeaMixingRecipe> TEA_MIXING_SERIALIZER = new TeaMixingRecipe.Serializer();
	public static final IRecipeSerializer<ShearingRecipe> SHEARING_SERIALIZER = new ShearingRecipe.Serializer();
	
	@SubscribeEvent
	public static void register(RegistryEvent.Register<IRecipeSerializer<?>> event) {
		CraftingHelper.register(ConfigCondition.Serializer.INSTANCE);

		ResourceLocation id = new ResourceLocation(TeaKettle.MODID, "cup_drink");
		Registry.register(Registry.RECIPE_TYPE, id, CUP_DRINK);
		event.getRegistry().register(CUP_DRINK_SERIALIZER.setRegistryName(id));
		
		id = new ResourceLocation(TeaKettle.MODID, "shearing");
		Registry.register(Registry.RECIPE_TYPE, id, SHEARING);
		event.getRegistry().register(SHEARING_SERIALIZER.setRegistryName(id));
		
		id = new ResourceLocation(TeaKettle.MODID, "tea_mixing");
		Registry.register(Registry.RECIPE_TYPE, id, TEA_MIXING);
		event.getRegistry().register(TEA_MIXING_SERIALIZER.setRegistryName(id));
	}
}
