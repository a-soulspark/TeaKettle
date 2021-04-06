package soulspark.tea_kettle.common.blocks;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class SimplyCupBlock extends CupBlock {
	public ResourceLocation itemName;
	public Item item;
	
	public SimplyCupBlock(ResourceLocation name, Properties properties) {
		super(properties);
		itemName = name;
	}
	
	@Override
	public Item asItem() {
		if (item == null) item = ForgeRegistries.ITEMS.getValue(itemName);
		return item;
	}
}
