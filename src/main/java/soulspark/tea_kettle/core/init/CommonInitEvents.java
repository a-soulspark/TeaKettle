package soulspark.tea_kettle.core.init;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import net.minecraft.block.ComposterBlock;
import net.minecraft.util.datafix.DataFixesManager;
import soulspark.tea_kettle.core.util.TeaSteepRecipes;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class CommonInitEvents {
	@SubscribeEvent
	public static void onCommonSetup(FMLCommonSetupEvent event) {
		TeaSteepRecipes.registerRecipes();
		ModFeatures.registerConfiguredFeatures();
		
		ComposterBlock.CHANCES.put(ModItems.TEA_LEAF.get(), 0.3f);
		ComposterBlock.CHANCES.put(ModItems.WHITE_TEA_LEAF.get(), 0.3f);
		ComposterBlock.CHANCES.put(ModItems.OOLONG_TEA_LEAF.get(), 0.3f);
		ComposterBlock.CHANCES.put(ModItems.BLACK_TEA_LEAF.get(), 0.3f);
		ComposterBlock.CHANCES.put(ModItems.ROSE_PETAL.get(), 0.3f);
		ComposterBlock.CHANCES.put(ModItems.BAMBOO_LEAF.get(), 0.3f);
	}
}
