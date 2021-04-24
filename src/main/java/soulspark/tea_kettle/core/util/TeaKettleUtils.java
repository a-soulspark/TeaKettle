package soulspark.tea_kettle.core.util;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import soulspark.tea_kettle.TeaKettle;

public class TeaKettleUtils {
	public static float getFullness(ItemStack stack, World world, Entity entity) {
		if (!stack.hasTag()) return 1;
		
		int fullness = stack.getOrCreateChildTag("BlockStateTag").getInt("fullness");
		if (fullness == 0) fullness = 4;
		return fullness / 4.0f;
	}
	
	public static float getSweetness(ItemStack stack) {
		if (!stack.hasTag()) return 0;
		
		String sweetnessString = stack.getOrCreateChildTag("BlockStateTag").getString("sweetness");
		int sweetness = sweetnessString.isEmpty() ? 0 : Integer.parseInt(sweetnessString);
		return sweetness / 2.0f;
	}
	
	public static ResourceLocation getExternalBlockName(ResourceLocation itemName) {
		return itemName.getNamespace().equals(TeaKettle.MODID) ? itemName : new ResourceLocation(TeaKettle.MODID, itemName.getNamespace() + "_" + itemName.getPath());
	}
}
