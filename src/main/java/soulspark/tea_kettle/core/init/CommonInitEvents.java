package soulspark.tea_kettle.core.init;

import soulspark.tea_kettle.core.util.TeaSteepRecipes;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class CommonInitEvents {
	@SubscribeEvent
	public static void onCommonSetup(FMLCommonSetupEvent event) {
		TeaSteepRecipes.registerRecipes();
		ModFeatures.registerConfiguredFeatures();
	}
}
